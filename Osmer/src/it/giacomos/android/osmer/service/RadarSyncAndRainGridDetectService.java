package it.giacomos.android.osmer.service;

import java.io.IOException;
import java.io.InputStream;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.network.state.Urls;
import it.giacomos.android.osmer.preferences.Settings;
import it.giacomos.android.osmer.rainAlert.RainDetectResult;
import it.giacomos.android.osmer.rainAlert.RainNotificationBuilder;
import it.giacomos.android.osmer.service.sharedData.NotificationData;
import it.giacomos.android.osmer.service.sharedData.RainNotification;
import it.giacomos.android.osmer.service.sharedData.ServiceSharedData;
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

public class RadarSyncAndRainGridDetectService extends Service 
implements GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener, 
RadarImageSyncAndCalculationTaskListener
{
	private boolean mIsStarted;
	private LocationClient mLocationClient;
	private Location mLocation;
	private long mTimestampSecs;

	public RadarSyncAndRainGridDetectService()
	{
		super();
		mLocationClient = null;
		mLocation = null;
		mIsStarted = false;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		if(!mIsStarted)
		{
			mTimestampSecs = intent.getLongExtra("timestamp", 0L);
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
			Log.e("RadarSyncAndRainGridDetectService.onStartCommand", "* service is already running");
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
					mylongitude, this);
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
		Log.e("RadarSyncAndRainGridDetectService.onConnectionFailed", "connection to location failed. Stopping service. Will wait for the next time");
		this.stopSelf();
	}

	@Override
	public void onConnected(Bundle arg0) 
	{
		Log.e("RadarSyncAndRainGridDetectService.onConnected", "getting last location");
		mLocation = mLocationClient.getLastLocation();
		mLocationClient.disconnect(); /* immediately */
		if(mLocation != null)
		{
			startSyncImagesAndRainDetect(mLocation.getLatitude(), mLocation.getLongitude());

		}
		else  /* wait an entire mSleepInterval before retrying */
			Log.e("RadarSyncAndRainGridDetectService", "location is not available. Cannot calculate rain probability");

		/* in any case, our work stops here */
		this.stopSelf();
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRainDetectionDone(RainDetectResult result) 
	{
		boolean willRain = result.willRain;
		float dbZ = result.dbz;
		if(new Settings(this).useInternalRainDetection())
		{
			RainNotification rainNotif = new RainNotification(result.willRain, mTimestampSecs, dbZ, 
					mLocation.getLatitude(), 
					mLocation.getLongitude());

			ServiceSharedData sharedData = ServiceSharedData.Instance(this);
			boolean alreadyNotifiedEqual = sharedData.alreadyNotifiedEqual(rainNotif);
			boolean arrivesTooLate = sharedData.arrivesTooLate(rainNotif);

			if(!alreadyNotifiedEqual && !arrivesTooLate && !sharedData.arrivesTooEarly(rainNotif, this))
			{
				NotificationManager mNotificationManager =
						(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				if(willRain)
				{
					int iconId = R.drawable.ic_launcher_statusbar_message_filled;
				
					Intent resultIntent = new Intent(this, OsmerActivity.class);
					resultIntent.putExtra("ptLatitude", mLocation.getLatitude());
					resultIntent.putExtra("ptLongitude", mLocation.getLongitude());

					RainNotificationBuilder rainNotifBuilder = new RainNotificationBuilder();
					Notification notification = rainNotifBuilder.build(this,  dbZ, iconId, rainNotif.latitude, rainNotif.longitude);
					
					mNotificationManager.notify(rainNotif.getTag(), rainNotif.getId(),  notification);
					/* update notification data */
					Log.e("RadarSyncAndRainGridDetectService.onRainDetectionDone", "notification setting notified " + rainNotif.getTag() + ", " + true);
					sharedData.updateCurrentRequest(rainNotif, true);
				}
				else /* it will not rain, remove notification if present */
				{
					Log.e("RadarSyncAndRainGridDetectService.onRainDetectionDone", "rain alert notification to be cancelled");
					RainNotification previousRainNotification = (RainNotification) sharedData.get(NotificationData.TYPE_RAIN);
					if(previousRainNotification != null && previousRainNotification.IsGoingToRain())
						mNotificationManager.cancel(rainNotif.getTag(), rainNotif.getId());
					sharedData.updateCurrentRequest(rainNotif, false);
				}
			}
		}
	}

}
