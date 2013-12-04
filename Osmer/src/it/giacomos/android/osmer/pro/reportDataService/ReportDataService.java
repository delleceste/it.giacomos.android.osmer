package it.giacomos.android.osmer.pro.reportDataService;

import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.pro.OsmerActivity;
import it.giacomos.android.osmer.pro.locationUtils.LocationService;
import it.giacomos.android.osmer.pro.network.state.Urls;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings.Secure;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.format.DateFormat;
import android.util.Log;

public class ReportDataService extends Service implements ServiceLocationUpdateListener, ServiceDataTaskListener
{
	public static String REPORT_REQUEST_NOTIFICATION_ID = "ReportRequestNotification";
	public static String REPORT_RECEIVED_NOTIFICATION_ID = "ReportReceivedNotification";

	private ReportDataServiceLocationService mLocationService;
	public ReportDataService() 
	{
		super();
		mLocationService = null;
		Log.e("ReportDataService.ReportDataService()", "CONSTRUCTOR");
		log("\nReportDataService.ReportDataService(): CONSTRUCTOR, Thread" + Thread.currentThread().toString());
	}

	@Override
	public IBinder onBind(Intent arg0) 
	{

		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Calendar cal = Calendar.getInstance();
		Log.e(">>>> ReportDataService <<<<<< ", "onStartCommand");
		mLocationService = new ReportDataServiceLocationService(this.getApplicationContext(), this);
		boolean success = mLocationService.connect();
		log("ReportDataService.onHandleIntent(): connected to location service: success: " + success);		
		return Service.START_NOT_STICKY;
	}

	@Override
	public void onLocationChanged(Location l) 
	{
		/* for each service session, receive just one location and then disconnect from the service */
		mLocationService.disconnect();
		if(l == null)
		{
			Log.e(">>>> ReportDataService <<<<<<", "--> onLocationChanged: location is null!!!");
			log("ReportDataService.onLocationChanged(): location is null");
			this.stopSelf();
		}
		else
		{
			Log.e(">>>> ReportDataService <<<<<<", "--> onLocationChanged " + l.getLatitude() + ", " + l.getLongitude());
			log("ReportDataService.onLocationChanged(): starting ServiceDataTask, Thread" + Thread.currentThread().toString());
			String deviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
			ServiceDataTask dataTask = new ServiceDataTask(this, deviceId, l.getLatitude(), l.getLongitude());
			dataTask.execute(new Urls().getRequestsUrl());
		}


	}

	@Override
	public void onDestroy()
	{
		Log.e(">>>> ReportDataService.onDestroy <<<<<<", "--> disconnecting from location service");
		super.onDestroy();
		mLocationService.disconnect(); /* we disconnected in onLocationChanged, by the way */
		log("ReportDataService.onDestroy " );
	}

	/** stop the service */
	@Override
	public void onServiceDataTaskComplete(boolean error, String dataAsString) 
	{
		log("ReportDataService.onServiceDataTaskComplete: error " + error + ", data: " + dataAsString);
		Log.e(">>>> ReportDataService.onServiceDataTaskComplete", "data: " + dataAsString + " error " + error);

		ReportRequestNotification notificationData = new ReportRequestNotification(dataAsString);
		if(notificationData.isValid())
		{
			String message = getResources().getString(R.string.notificatonNewReportRequest) + notificationData.username;
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
			NotificationManager mNotificationManager =
					(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			// mId allows you to update the notification later on.

			int id = -1;

			try{
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
				Date date = formatter.parse(notificationData.datetime);
				id = (int) date.getTime();
			} 
			catch(Exception e)
			{
				System.out.println(e.getMessage());
			}

			mNotificationManager.notify(ReportDataService.REPORT_REQUEST_NOTIFICATION_ID, id,  notificationBuilder.build());
		}
		this.stopSelf();

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
