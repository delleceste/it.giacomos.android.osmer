package it.giacomos.android.osmer.pro.service;

import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.pro.OsmerActivity;
import it.giacomos.android.osmer.pro.network.state.Urls;
import it.giacomos.android.osmer.pro.preferences.Settings;
import it.giacomos.android.osmer.pro.service.sharedData.NotificationData;
import it.giacomos.android.osmer.pro.service.sharedData.ReportRequestNotification;
import it.giacomos.android.osmer.pro.service.sharedData.ServiceSharedData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
	public static String REPORT_REQUEST_NOTIFICATION_TAG = "ReportRequestNotification";
	public static String REPORT_RECEIVED_NOTIFICATION_TAG = "ReportReceivedNotification";

	private Location mLocation;
	private Handler mHandler;
	private LocationClient mLocationClient;
	private FetchRequestsDataTask mServiceDataTask;
	private long mSleepInterval;
	private boolean mIsStarted;

	public ReportDataService() 
	{
		super();
		mHandler = null;
		mLocationClient = null;
		mLocation = null;
		mServiceDataTask = null;
		mIsStarted = false;
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
		Log.e(">>>> ReportDataService <<<<<< ", "onStartCommand");
		log("\nReportDataService.onStartCommand()");
		Settings s = new Settings(this);
		mSleepInterval = s.getServiceSleepIntervalMillis();
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


		return Service.START_STICKY;
	}

	@Override
	/** This method is executed when mSleepInterval time interval has elapsed 
	 *  It connects the location client in order to wait for an available Location.
	 */
	public void run() 
	{
		/* when the location is available, we will update data */
		if(!mLocationClient.isConnected())
			mLocationClient.connect();
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
		boolean taskStarted = false;
		mLocation = mLocationClient.getLastLocation();
		/* will connect on next run to update location before downloading up to date data */
		mLocationClient.disconnect(); 

		if(mLocation != null)
		{
			Toast.makeText(this.getApplicationContext(), "Meteo.FVG: updating reports...", Toast.LENGTH_LONG).show();
			/* check that the network is still available */
			final ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			final NetworkInfo netinfo = connMgr.getActiveNetworkInfo();
			if(netinfo != null && netinfo.isConnected())
			{
				/* get the device id */
				String deviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
				/* start the service and execute it. When the thread finishes, onServiceDataTaskComplete()
				 * will schedule the next task.
				 */
				mServiceDataTask = new FetchRequestsDataTask(this, deviceId, mLocation.getLatitude(), mLocation.getLongitude());

				Log.e("ReportDataService.run()", "dataTask.execute() entering");
				mServiceDataTask.execute(new Urls().getRequestsUrl());
				taskStarted = true;
			}
			else
				Log.e("Meteo.FVG.ReportDataService.run", "connection is unavailable");
		}

		/* next update is scheduled inside onServiceDataTaskComplete (i.e. when the download task
		 * is complete) to ensure that download tasks never ovelap.
		 * But if there is no location available here and/or no connectivity,
		 * we must schedule an update here.
		 */
		if(!taskStarted)
			mHandler.postDelayed(this, mSleepInterval);
	}

	@Override
	public void onDestroy()
	{
		/* clean tasks, stop scheduled repetition of data task, disconnect from 
		 * location service.
		 */
		if(mServiceDataTask != null)
			mServiceDataTask.cancel(false);
		if(mHandler != null)
			mHandler.removeCallbacks(this);
		if(mLocationClient != null && mLocationClient.isConnected())
			mLocationClient.disconnect();

		Log.e(">>>> ReportDataService.onDestroy <<<<<<", "--> disconnecting from location service");
		super.onDestroy();
		log("ReportDataService.onDestroy " );
	}

	@Override
	public void onServiceDataTaskComplete(boolean error, String dataAsString) 
	{
		log("ReportDataService.onServiceDataTaskComplete: error " + error + ", data: " + dataAsString);
		Log.e(">>>> ReportDataService.onServiceDataTaskComplete", "data: " + dataAsString + " error " + error);

		ServiceSharedData sharedData = ServiceSharedData.Instance();
		NotificationManager mNotificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		/* a request has been withdrawn, remove notification, if present */
		if(dataAsString.isEmpty())
		{
			/* remove notification, if present */
			NotificationData currentNotification = sharedData.getNotificationData(NotificationData.TYPE_REQUEST);
			if(currentNotification != null) /* a notification is present */
			{
				Log.e("ReportDataService.onServiceDataTaskComplete", " removing notification with id " + currentNotification.makeId());
				mNotificationManager.cancel(REPORT_REQUEST_NOTIFICATION_TAG, currentNotification.makeId());

				/* mark as consumed. The currentNotification is not removed from sharedData because sharedData
				 * keeps it there in order not to bother us with possibly new notifications incoming in a near
				 * future. currentNotification thus needs to be stored in order to be used by 
				 * canBeConsideredNew() sharedData method.
				 * On the other hand, the map view tests this variable in order to show or not a marker.
				 */
				currentNotification.setConsumed(true);
			}
		}

		ReportRequestNotification notificationData = new ReportRequestNotification(dataAsString);
		if(notificationData.isValid())
		{
			if(sharedData.canBeConsideredNew(notificationData, this))
			{
				/* replace the previous notification data (if any) with the new one.
				 * This updates the sharedData timestamp of the last notification
				 */
				sharedData.setWasNotified(notificationData);
				/* and notify */
				String message = getResources().getString(R.string.notificatonNewReportRequest) + " " + notificationData.username;
				if(notificationData.locality.length() > 0)
					message += " - " + notificationData.locality;

				NotificationCompat.Builder notificationBuilder =
						new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(getResources().getString(R.string.app_name))
				.setContentText(message);

				// Creates an explicit intent for an Activity in your app
				Intent resultIntent = new Intent(this, OsmerActivity.class);
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
						stackBuilder.getPendingIntent(
								0,
								PendingIntent.FLAG_UPDATE_CURRENT
								);
				notificationBuilder.setContentIntent(resultPendingIntent);
				notificationBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
				// mId allows you to update the notification later on.

				mNotificationManager.notify(ReportDataService.REPORT_REQUEST_NOTIFICATION_TAG, notificationData.makeId(),  notificationBuilder.build());
			}
			/* just update the shared data notification data with the most up to date 
			 * values of latitude, longitude, username...
			 */
			sharedData.updateCurrentRequest(notificationData);
		}

		/* schedule next update only when all the work is finished */
		mHandler.postDelayed(this, mSleepInterval);
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) 
	{
		/* LocationClient.connect() failed. No onConnected callback will be executed,
		 * so no task will be started. Just schedule another try:
		 */
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
			out.append(message + ": " + Calendar.getInstance().getTime().toLocaleString() + "\n");
			out.close();
		} catch (FileNotFoundException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



}
