package it.giacomos.android.osmer.service;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ServiceManager 
{
	public ServiceManager()
	{

	}

	public boolean isServiceRunning(Context ctx)
	{
		ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) 
		{
			if (ReportDataService.class.getName().equals(service.service.getClassName())) 
				return true;
		}
		return false;
	}
	
	public boolean setEnabled(Context context, boolean enabled) 
	{
		final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		Intent myIntent = new Intent(context, ReportDataService.class);
		NetworkInfo netinfo = connMgr.getActiveNetworkInfo();
		
		if(enabled && netinfo != null && netinfo.isConnected())
		{
//			Log.e(" ServiceManager", "enabled "  + enabled + " starting service ReportDataService");
			ComponentName cn = context.startService(myIntent);
			return cn.getClassName().endsWith("ReportDataService");
		}
		else
		{
//			if(netinfo != null)
//				Log.e("ServiceManager.setEnabled", "enabled "  + enabled + 
//						" net up " +  connMgr.getActiveNetworkInfo().isConnectedOrConnecting() + " stopping service ReportDataService");
//			else
//				Log.e("ServiceManager.setEnabled", "enabled "  + enabled + 
//						" net info null: stopping service ReportDataService");
			return context.stopService(myIntent);
		}
	}

}

