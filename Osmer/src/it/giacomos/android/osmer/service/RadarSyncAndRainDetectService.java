package it.giacomos.android.osmer.service;

import java.io.IOException;
import java.io.InputStream;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.network.state.Urls;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class RadarSyncAndRainDetectService extends Service 
implements GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener, 
RadarImageSyncAndCalculationTaskListener
{
	private boolean mIsStarted;
	private LocationClient mLocationClient;
	private Location mLocation;

	public RadarSyncAndRainDetectService()
	{
		super();
		mLocationClient = null;
		mLocation = null;
		mIsStarted = false;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		Log.e("RadarSyncAndRainDetectService", "onStartCommand");
		if(!mIsStarted)
		{
			if(mLocationClient == null)
				mLocationClient = new LocationClient(this, this, this);
			/* wait for location client to be connected before syncing images and 
			 * perform calculations on the images received.
			 */
			mLocationClient.connect();
			mIsStarted = true;
		}
		else
		{
			Log.e("RadarSyncAndRainDetectService.onStartCommand", "* service is already running");
		}
		return Service.START_STICKY;
	}

	protected void startSyncImagesAndRainDetect(double mylatitude, double mylongitude) 
	{
		AssetManager assetManager = getApplicationContext().getAssets();
		InputStream input;
		String gridConf = "";
		try {
			input = assetManager.open("grid-5x5-only-towards-center.dat");
			int size = input.available();
			byte[] buffer = new byte[size];
			input.read(buffer);
			input.close();
			gridConf = new String(buffer);
			String radarImgFilePath = this.getApplicationContext().getCacheDir().getPath();
			RadarImageSyncAndGridCalculationTask mCalcTask = new RadarImageSyncAndGridCalculationTask(mylatitude,
					mylongitude, this, gridConf);
			String [] configurations = {gridConf, radarImgFilePath, new Urls().radarHistoricalFileListUrl()};
			mCalcTask.execute(configurations);
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		Log.e("RadarSyncAndRainDetectService.onConnectionFailed", "connection to location failed. Stopping service. Will wait for the next time");
		this.stopSelf();
	}

	@Override
	public void onConnected(Bundle arg0) {
		Log.e("RadarSyncAndRainDetectService.onConnected", "getting last location");
		mLocation = mLocationClient.getLastLocation();
		mLocationClient.disconnect(); /* immediately */
		if(mLocation != null)
		{
			startSyncImagesAndRainDetect(mLocation.getLatitude(), mLocation.getLongitude());
			Log.e("RadarSyncAndRainDetectService.onConnected", " location is good: lat " + mLocation.getLatitude() + 
					" lon " + mLocation.getLongitude());
			
		}
		else/* wait an entire mSleepInterval before retrying */
			Log.e("RadarSyncAndRainDetectService", "location is not available. Cannot calculate rain probability");

		/* in any case, our work stops here */
		Log.e("RadarSyncAndRainDetectService", "stopping service...");
		this.stopSelf();
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRainDetectionDone(boolean willRain) 
	{
		if(willRain)
		{
			NotificationManager mNotificationManager =
					(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			Intent resultIntent = new Intent(this, OsmerActivity.class);
			resultIntent.putExtra("ptLatitude", mLocation.getLatitude());
			resultIntent.putExtra("ptLongitude", mLocation.getLongitude());
			int iconId = R.drawable.ic_blue_pin;
			/*  */
			Log.e("RadarSyncAndRainDetectService", " it will rain!");
			int notificationFlags = Notification.DEFAULT_SOUND|
					Notification.FLAG_SHOW_LIGHTS;
			NotificationCompat.Builder notificationBuilder =
					new NotificationCompat.Builder(this)
			.setSmallIcon(iconId)
			.setAutoCancel(true)
			.setLights(Color.RED, 1000, 1000)
			.setContentTitle(getResources().getString(R.string.app_name))
			.setContentText("RAIN DETECTED FROM APP!!").setDefaults(notificationFlags);

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

			Notification notification = notificationBuilder.build();
			mNotificationManager.notify("RainNotificationFromApp", 2343,  notification);
		}
		else
		{
			Log.e("RadarSyncAndRainDetectService", " it will NOT rain!");
		}
	}

}
