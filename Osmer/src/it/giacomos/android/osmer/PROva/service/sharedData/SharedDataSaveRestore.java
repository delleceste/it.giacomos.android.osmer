package it.giacomos.android.osmer.PROva.service.sharedData;

import it.giacomos.android.osmer.PROva.Logger;
import it.giacomos.android.osmer.PROva.preferences.Settings;

import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharedDataSaveRestore
{
	private SharedPreferences mSharedPreferences;
	private long mLastNotifiedTimeMillis;

	public SharedDataSaveRestore(SharedPreferences sp)
	{
		mSharedPreferences = sp;
	}
	
	public HashMap<Short, NotificationData> loadNotificationData()
	{
		HashMap<Short, NotificationData> data = new HashMap<Short, NotificationData>();
		
		/* request */
		String reportRequestNotificationAsStr = mSharedPreferences.getString("LAST_REPORT_REQUEST_NOTIF", "");
		if(!reportRequestNotificationAsStr.isEmpty())
			data.put(NotificationData.TYPE_REQUEST, 
					new ReportRequestNotification(reportRequestNotificationAsStr));
		/* report */
		String reportNotificationAsStr = mSharedPreferences.getString("LAST_REPORT_NOTIF", "");
		if(!reportNotificationAsStr.isEmpty())
			data.put(NotificationData.TYPE_REPORT, 
					new ReportNotification(reportNotificationAsStr));
		
		Log.e("SharedDataSaveRestore.load", "loaded data size " + data.size());
		/// Logger.log("SharedDataSR.load: REQ " + reportRequestNotificationAsStr);
		/// Logger.log("SharedDataSR.load: REP " + reportNotificationAsStr);
		return data;
	}
	
	public void saveNotificationData(HashMap<Short, NotificationData> data)
	{
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		String repAsString = "", reqAsString = "";
		if(data.containsKey(NotificationData.TYPE_REPORT))
		{
			repAsString = data.get(NotificationData.TYPE_REPORT).toString();
			editor.putString("LAST_REPORT_NOTIF", repAsString);
			editor.commit();
			
		}
		if(data.containsKey(NotificationData.TYPE_REQUEST))
		{
			reqAsString = data.get(NotificationData.TYPE_REQUEST).toString();
			editor.putString("LAST_REPORT_REQUEST_NOTIF", reqAsString);
			editor.commit();
		}
		Log.e("SharedDataSaveRestore.save", "saved data size " + data.size());
		/// Logger.log("SharedDataSR.save: saved REQ " + reqAsString);
		/// Logger.log("SharedDataSR.save: saved REP " + repAsString);
	}

	public void setLastNotifiedTimeMillis(long lastNotifiedMillis) 
	{
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putLong("SERVICE_LAST_NOTIFIED_MILLIS", lastNotifiedMillis);
		editor.commit();
	}

	public long getLastNotifiedTimeMillis() 
	{
		return mSharedPreferences.getLong("SERVICE_LAST_NOTIFIED_MILLIS", 0L);
	}
	
}
