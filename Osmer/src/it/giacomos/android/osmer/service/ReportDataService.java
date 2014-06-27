package it.giacomos.android.osmer.service;

import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.gcm.GcmRegistrationManager;
import it.giacomos.android.osmer.network.state.Urls;
import it.giacomos.android.osmer.preferences.Settings;
import it.giacomos.android.osmer.service.sharedData.NotificationData;
import it.giacomos.android.osmer.service.sharedData.NotificationDataFactory;
import it.giacomos.android.osmer.service.sharedData.RainNotification;
import it.giacomos.android.osmer.service.sharedData.ReportRequestNotification;
import it.giacomos.android.osmer.service.sharedData.ServiceSharedData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings.Secure;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

public class ReportDataService extends Service 
implements GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener, 
FetchRequestsTaskListener, Runnable
{
	private Location mLocation;
	private Handler mHandler;
	private LocationClient mLocationClient;
	private UpdateMyLocationTask mUpdateMyLocationTask;
	private long mSleepInterval;
	private boolean mIsStarted;
	private Settings mSettings;
	/* timestamp updated when the AsyncTask completes, successfully or not */
	private long mLastTaskStartedTimeMillis;
	private long mCheckIfNeedRunIntervalMillis;

	public ReportDataService() 
	{
		super();
		mHandler = null;
		mLocationClient = null;
		mLocation = null;
		mUpdateMyLocationTask = null;
		mIsStarted = false;
		mCheckIfNeedRunIntervalMillis = 20000;
	}

	@Override
	public IBinder onBind(Intent arg0) 
	{
		return null;
	}

	/** If wi fi network is enabled, I noticed that turning on 3G network as well 
	 * produces this method to be invoked another times. That is, the ConnectivityChangedReceiver
	 * triggers a Service start command. In this case, we must avoid that the handler schedules
	 * another execution of the timer.
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		//   Logger.log("RDS.onStartCmd: intent " + intent + "isStarted" + mIsStarted);
		if(!mIsStarted)
		{
			mSettings = new Settings(this);
			mSleepInterval = mSettings.getServiceSleepIntervalMillis();
			Log.e("ReportDataService.onStartCommand", "service started sleep interval " + mSleepInterval);
			/* the last time the network was used is saved so that if the service is killed and
			 * then restarted, we avoid too frequent and unnecessary downloads
			 */
			mLastTaskStartedTimeMillis = mSettings.getLastReportDataServiceStartedTimeMillis();
			mCheckIfNeedRunIntervalMillis = mSleepInterval / 6;

			if(mLocationClient == null)
				mLocationClient = new LocationClient(this, this, this);

			/* if onStartCommand is called multiple times,we must stop previously
			 * scheduled runs.
			 */
			if(mHandler != null)
				mHandler.removeCallbacks(this);
			mHandler = new Handler();
			mHandler.postDelayed(this, 3000);
			mIsStarted = true;

		}
		else
		{
			Log.e("ReportDataService.onStartCommand", "* service is already running");
		}
		return Service.START_STICKY;
	}

	@Override
	/** This method is executed when mCheckIfNeedRunIntervalMillis time interval has elapsed.
	 *  The mCheckIfNeedRunIntervalMillis is an interval shorter than mSleepInterval used to
	 *  check whether it is time to update or not. If the device goes to sleep, the timers 
	 *  are suspended and mSleepInterval may result too long in order to get an update in a 
	 *  reasonable time. Checking often with a simple comparison should be lightweight and 
	 *  a good compromise to provide a quick update when the phone wakes up.
	 *  It connects the location client in order to wait for an available Location.
	 */
	public void run() 
	{
		long currentTimeMillis = System.currentTimeMillis();
		/* do we need to actually proceed with update task? */
		if(currentTimeMillis - mLastTaskStartedTimeMillis >= mSleepInterval)
		{
			/* wait for connection and then get location and update data */
			//	log("I: run: connectin to loc cli");
			mLocationClient.connect();
		}
		else /* check in a while */
		{
			// log("I: run: not yet time");
			mHandler.postDelayed(this, mCheckIfNeedRunIntervalMillis);
		}
	}

	/**
	 * Called by Location Services when the request to connect the
	 * client finishes successfully. At this point, you can
	 * request the current location or start periodic updates
	 * The return value is the best, most recent location, based 
	 * on the permissions your app requested and the currently-enabled 
	 * location sensors.
	 * 
	 * After getting the last location, we can start the data fetch task.
	 * If for some reason mLocation is null (should not happen!) or the network
	 * is down (yes, we check again!), then no task is executed and a next
	 * schedule takes place by means of postDelayed on the handler (see the end
	 * of the method).
	 */
	@Override
	public void onConnected(Bundle arg0) 
	{
		// Log.e("ReportDataService.onConnected", "getting last location");
		mLocation = mLocationClient.getLastLocation();
		mLocationClient.disconnect(); /* immediately */
		if(mLocation != null)
		{
			startTask();
			/* mark the last execution complete timestamp */
			mLastTaskStartedTimeMillis = System.currentTimeMillis();
			/* save in case the service is killed and then restarted */
			mSettings.setLastReportDataServiceStartedTimeMillis(mLastTaskStartedTimeMillis);
			/* when the task is started, we start the short time check */
			mHandler.postDelayed(this, mCheckIfNeedRunIntervalMillis);
		}
		else/* wait an entire mSleepInterval before retrying */
			mHandler.postDelayed(this, mSleepInterval);
	}

	private void startTask()
	{
		/* check that the network is still available */
		final ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo netinfo = connMgr.getActiveNetworkInfo();
		if(netinfo != null && netinfo.isConnected())
		{
			/* if a task is still running, cancel it before starting a new one */
			if(mUpdateMyLocationTask != null && mUpdateMyLocationTask.getStatus() != AsyncTask.Status.FINISHED)
				mUpdateMyLocationTask.cancel(false);
			/* get the device id */
			String deviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
			/* get the registration id (for new versions, to work with google cloud messaging */
			GcmRegistrationManager gcmRm = new GcmRegistrationManager();
			String registrationId = gcmRm.getRegistrationId(this);
			
			if(!registrationId.isEmpty())
			{
				/* start the service and execute it. When the thread finishes, onServiceDataTaskComplete()
				 * will schedule the next task.
				 */
				mUpdateMyLocationTask = new UpdateMyLocationTask(this, deviceId,
						registrationId,
						mLocation.getLatitude(), mLocation.getLongitude(),
						mSettings.rainNotificationEnabled());
				/* old: "http://www.giacomos.it/meteo.fvg/get_reports_and_requests_for_my_location.php" 
				 * new: "http://www.giacomos.it/meteo.fvg/update_my_location.php"
				 */
				mUpdateMyLocationTask.execute(new Urls().getUpdateMyLocationUrl());
			}
			else
				gcmRm.registerInBackground(getApplicationContext());
		}
	}

	@Override
	public void onDestroy()
	{
		//   Logger.log("RDS.onDestroy");
		/* clean tasks, stop scheduled repetition of data task, disconnect from 
		 * location service.
		 */
		if(mUpdateMyLocationTask != null)
		{
			mUpdateMyLocationTask.removeFetchRequestTaskListener();
			mUpdateMyLocationTask.cancel(false);
		}
		if(mHandler != null)
			mHandler.removeCallbacks(this);

		if(mLocationClient != null && mLocationClient.isConnected())
			mLocationClient.disconnect();

		mIsStarted = false;

		super.onDestroy();
		// log("x: service destroyed" );
	}

	@Override
	public void onServiceDataTaskComplete(boolean error, String dataAsString) 
	{	
		boolean notified = false;
		short requestsCount = 0;
		//	if(error)
		Log.e("ReportDataService.onServiceDataTaskComplete", "data: " + dataAsString);

		ServiceSharedData sharedData = ServiceSharedData.Instance(this);
		NotificationManager mNotificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		ArrayList<NotificationData> notifications = new NotificationDataFactory().parse(dataAsString);
		for(NotificationData notificationData : notifications)
		{
			/* Rain alert notifications are marked valid only if they represent an alert 
			 * (there's a chance it's going to rain).
			 */
			if(notificationData.isValid() && notificationData.isRainAlert() && 
					!((RainNotification) notificationData).IsGoingToRain())
			{
				Log.e("onServiceDataTaskComplete", "rain alert notification to be cancelled");
				RainNotification rainNotif = (RainNotification) notificationData;
				mNotificationManager.cancel(rainNotif.getTag(), rainNotif.makeId());
				Log.e("onServiceDataTaskComplete", "RAIN notification setting notified " + notificationData.getTag() + ", " + notified);
				sharedData.updateCurrentRequest(notificationData, notified);
			}
			else if(notificationData.isValid() && notificationData.isRainAlert())
			{
				boolean alreadyNotifiedEqual = sharedData.alreadyNotifiedEqual(notificationData);
				if(!alreadyNotifiedEqual && !sharedData.arrivesTooEarly(notificationData, this))
				{
					Log.e("onServiceDataTaskComplete", "notification can be considereth new " + notificationData.username);
					/* and notify */
					String message = "";
					int iconId, ledColor;
					// Creates an explicit intent for an Activity in your app
					Intent resultIntent = new Intent(this, OsmerActivity.class);
					resultIntent.putExtra("ptLatitude", notificationData.latitude);
					resultIntent.putExtra("ptLongitude", notificationData.longitude);

//					if(notificationData.isRequest())
//					{
//						requestsCount++;
//						resultIntent.putExtra("NotificationReportRequest", true);
//						ReportRequestNotification rrnd = (ReportRequestNotification) notificationData;
//						message = getResources().getString(R.string.notificatonNewReportRequest) 
//								+ " " + notificationData.username;
//						if(rrnd.locality.length() > 0)
//							message += " - " + rrnd.locality;
//						iconId = R.drawable.ic_launcher_statusbar_request;
//						ledColor = Color.argb(0, 0, 255, 0); /* cyan notification */
//						//   Logger.log("RDS task ok.new req.notif " + notificationData.username);
//					}
//					else if(notificationData.isRainAlert())
					{
						RainNotification rainNotif = (RainNotification) notificationData;
						iconId = R.drawable.ic_launcher_statusbar_rain;
						ledColor = Color.argb(255, 0, 0, 0); /* red notification */
						if(rainNotif.IsGoingToRain())
						{
							float dbZ = rainNotif.getLastDbZ();
							Log.e("ReportDataService", "setting extra NotificationRainAlert");
							resultIntent.putExtra("NotificationRainAlert", true);

							if(dbZ < 27)
							{
								message = getResources().getString(R.string.notificationRainAlert);
							}
							else if(dbZ < 42)
							{
								message = getResources().getString(R.string.notificationRainModerate);
							}
							else
							{
								message = getResources().getString(R.string.notificationRainIntense);
							}
						}
					}
//					else
//					{
//						resultIntent.putExtra("NotificationReport", true); 
//						message = getResources().getString(R.string.notificationNewReportArrived) 
//								+ " "  + notificationData.username;
//						iconId = R.drawable.ic_launcher_statusbar_report;
//						ledColor = Color.argb(0, 255, 0, 0);
//						//   Logger.log("RDS task ok.new req.notif " + notificationData.username);
//					}

					//					int notificationFlags = Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS|
					//							Notification.FLAG_SHOW_LIGHTS;
					int notificationFlags = Notification.DEFAULT_SOUND|
							Notification.FLAG_SHOW_LIGHTS;
					NotificationCompat.Builder notificationBuilder =
							new NotificationCompat.Builder(this)
					.setSmallIcon(iconId)
					.setAutoCancel(true)
					.setContentTitle(getResources().getString(R.string.app_name))
					.setContentText(message).setDefaults(notificationFlags);

					// The stack builder object will contain an artificial back stack for the
					// started Activity.
					// This ensures that navigating backward from the Activity leads out of
					// your application to the Home screen.
					TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
					// Adds the back stack for the Intent (but not the Intent itself)
					stackBuilder.addParentStack(OsmerActivity.class);
					// Adds the Intent that starts the Activity to the top of the stack
					stackBuilder.addNextIntent(resultIntent);

					PendingIntent resultPendingIntent =
							stackBuilder.getPendingIntent( 0, PendingIntent.FLAG_UPDATE_CURRENT);

					notificationBuilder.setContentIntent(resultPendingIntent);
					notificationBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
					// mId allows you to update the notification later on.

					Notification notification = notificationBuilder.build();
					notification.ledARGB = ledColor;
					notification.ledOnMS = 800;
					notification.ledOffMS = 2200;
					mNotificationManager.notify(notificationData.getTag(), notificationData.makeId(),  notification);
					notified = true;
					/* update notification data */
					Log.e("onServiceDataTaskComplete", "notification setting notified " + notificationData.getTag() + ", " + notified);
					sharedData.updateCurrentRequest(notificationData, notified);
				}
				else
				{
					//   Logger.log("RDS task ok. notif not new " + notificationData.username);
					// log("task ok. notif not new " + notificationData.username);
					Log.e("onServiceDataTaskComplete", "notification IS NOT NEW " + notificationData.getType());
				}
			}
			else
			{
				// log("service task: notification not valid: " + dataAsString);
				Toast.makeText(this, "Notification not valid! " + 
						dataAsString, Toast.LENGTH_LONG).show();
			}
		} /* for(NotificationData notificationData : notifications) */
		
		/* a request has been withdrawn, remove notification, if present */
		if(requestsCount == 0)
		{
			/* remove notification, if present */
			NotificationData currentNotification = sharedData.getNotificationData(NotificationData.TYPE_REQUEST);
			if(currentNotification != null) /* a notification is present */
			{
				// Log.e("ReportDataService.onServiceDataTaskComplete", " removing notification with id " + currentNotification.makeId());
				mNotificationManager.cancel(currentNotification.getTag(), currentNotification.makeId());

				/* mark as consumed. The currentNotification is not removed from sharedData because sharedData
				 * keeps it there in order not to bother us with possibly new notifications incoming in a near
				 * future. currentNotification thus needs to be stored in order to be used by 
				 * canBeConsideredNew() sharedData method.
				 * On the other hand, the map view tests this variable in order to show or not a marker.
				 */
				currentNotification.setConsumed(true);
			}
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) 
	{
		/* LocationClient.connect() failed. No onConnected callback will be executed,
		 * so no task will be started. Just schedule another try, but directly sleep for
		 * mSleepInterval, do not try to reconnect too fast, so do not postDelayed of 
		 * mCheckIfNeedRunIntervalMillis.
		 */
		// Log.e("ReportDataService.onConnectionFailed", "connection to location failed sleeping for "  + mSleepInterval);
		mHandler.postDelayed(this, mSleepInterval);
	}

	@Override
	public void onDisconnected() 
	{

	}

	private void log(String message)
	{
		File f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

		PrintWriter out;
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter(f.getAbsolutePath() + "/Meteo.FVG.Service.log", true)));
			out.append(Calendar.getInstance().getTime().toLocaleString()+ ": " + message + "\n");
			out.close();
		} catch (FileNotFoundException e1) 
		{
			e1.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}



}
