package it.giacomos.android.osmer.service;

import it.giacomos.android.osmer.network.state.Urls;
import it.giacomos.android.osmer.rainAlert.SyncImages;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class RadarSyncAndRainDetectIntentService extends IntentService 
{
	public RadarSyncAndRainDetectIntentService()
	{
		super("RadarSyncAndRainDetectIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) 
	{
		Log.e("RadarSyncAndRainDetectIntentService", "onHandleIntent");
		/* sync radar images for rain detection */
		SyncImages syncer = new SyncImages();
		syncer.sync(new Urls().radarHistoricalFileListUrl(), this.getApplicationContext().getCacheDir().getPath());
		
	}

}
