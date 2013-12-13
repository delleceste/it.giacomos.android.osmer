package it.giacomos.android.osmer.pro.service.sharedData;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;
import it.giacomos.android.osmer.pro.network.DownloadStatus;
import it.giacomos.android.osmer.pro.preferences.Settings;

public class ServiceSharedData {

	private static ServiceSharedData _instance = null;

	/* maps the notification data type into the corresponding notification data.
	 * For each type, only one notificatin data can be present.
	 * (No more than one notification at time)
	 */
	private HashMap<Short, NotificationData> mNotificationDataHash;

	private Date mLastNotifiedDate;
	
	public static ServiceSharedData Instance()
	{
		if(_instance == null)
			_instance = new ServiceSharedData();
		return _instance;
	}

	private ServiceSharedData()
	{
		mNotificationDataHash = new HashMap<Short, NotificationData>();
		mLastNotifiedDate = null;
	}

	public NotificationData getNotificationData(short type)
	{
		return mNotificationDataHash.get(type);
	}
	
	public void setWasNotified(NotificationData notificationData)
	{
		short type = notificationData.getType();
		/* replace old notificationData for the specified type.
		 * Remember that only one notificationData per type can be considered.
		 */
		mNotificationDataHash.put(type, notificationData);
		mLastNotifiedDate = notificationData.getDate();
	}

	public boolean canBeConsideredNew(NotificationData notificationData, Context ctx) 
	{
		NotificationData inHashND;
		short type = notificationData.getType();
		if(!mNotificationDataHash.containsKey(type))
			return true;
		else
			inHashND = mNotificationDataHash.get(type);

		/* if the notification is exactly the same, never trigger it again. */
		if(inHashND.equals(notificationData))
			return false; /* exactly the same */


		/* we can filter out subsequent requests basing on the minimum time between notifications
		 * desired by the user.
		 */
		long minMillis = new Settings(ctx).minTimeBetweenReportRequestNotificationsMinutes() * 60 * 1000;
		long diffTimeMs =  notificationData.getDate().getTime() - mLastNotifiedDate.getTime() ;
		Log.e("dates are inHase " , mLastNotifiedDate.toLocaleString() +", new " + notificationData.getDate().toLocaleString());
		Log.e("dates are inHas ", "difftime ms = " + diffTimeMs + " min millis " + minMillis);
		if(diffTimeMs < minMillis)
		{
			Log.e("ServiceSharedData", "diffTimeMillis < minimum CANNOT BE CONSIDERETH NEW");
			return false; /* not new */
		}
		
		Log.e("ServiceSharedData", "diffTimeMillis > minimum --> NEW");
		return true; /* elapsed time is greater than the minimum interval required between notifications */

	}

	public void updateCurrentRequest(ReportRequestNotification notificationData) 
	{
		short type = notificationData.getType();
		/* replace old notificationData for the specified type.
		 * Remember that only one notificationData per type can be considered.
		 */
		mNotificationDataHash.put(type, notificationData);
	}
}
