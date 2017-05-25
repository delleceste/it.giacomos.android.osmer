package it.giacomos.android.osmer.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import it.giacomos.android.osmer.preferences.Settings;

public class ConnectivityChangedReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent) 
	{  
		final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netinfo = connMgr.getActiveNetworkInfo();
		
		
//		if(netinfo!= null)
//		{
//			Log.e("ConnectivityChangedReceiver.onReceive", "connecting or connected " + netinfo.isConnectedOrConnecting());
//			Log.e("ConnectivityChangedReceiver.onReceive", "connected " + netinfo.isConnected());
//			Log.e("ConnectivityChangedReceiver.onReceive", "isAvailable " + netinfo.isAvailable());
//			Log.e("ConnectivityChangedReceiver.onReceive", "network info type " + netinfo.getState());
//		}
//		else
//			Log.e("ConnectivityChangedReceiver.onReceive", "net info null ");
		
		Settings s = new Settings(context);
		boolean notificationServiceEnabled = s.notificationServiceEnabled();
		if(notificationServiceEnabled)
			new ServiceManager().setEnabled(context, true);
		else
			Log.e("ConnectivityChangedReceiver.onReceive", "not starting service. (disabled)");
		
		/* rain alert. Start radar image sync and image comparison */
		if(s.rainNotificationEnabled() && netinfo != null && netinfo.isConnected())
		{
			Log.e("ConnectivityChangedReceiver", "starting radarSyncRainDetectIntentService");
			Intent radarSyncRainDetectIntent = new Intent(context, RadarSyncAndRainGridDetectService.class);
			context.startService(radarSyncRainDetectIntent);
		}
		else
			Log.e("ConnectivityChangedReceiver", "not starting radarSyncRainDetectIntentService " + netinfo);
	}
}
