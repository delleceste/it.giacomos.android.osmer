package it.giacomos.android.osmer.pro.service.sharedData;

import java.util.Calendar;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;
import it.giacomos.android.osmer.pro.preferences.Settings;

/** This class manages the notifications.
 * Since the service can be killed at any time by Android, this class has the 
 * responsibility to save data each time it is modified. The saved data is 
 * the NotificationData (2 objects at most, one request and one report), and the
 * timestamp in milliseconds of the last notification.
 * 
 * Each time a NotificationData changes or the notification timestamp is renewed,
 * the changes must be saved. SharedPreferences are used by the helper class 
 * SharedDataSaveRestre in order to save and restore the objects used by
 * ServiceSharedData.
 * 
 * When the Instance() method is invoked, data is loaded from SharedPreferences
 * (timestamp and notification data).
 * 
 * In this way, if Android kills the Service the notification ServiceSharedData 
 * should not be lost across service restarts.
 * 
 * @author giacomo
 *
 */
public class ServiceSharedData 
{
	private static ServiceSharedData _instance = null;
	private SharedDataSaveRestore mSharedDataSaveRestore;

	/* maps the notification data type into the corresponding notification data.
	 * For each type, only one notification data can be present.
	 * (No more than one notification at time)
	 */
	private HashMap<Short, NotificationData> mNotificationDataHash;

	private long mLastNotifiedTimeMillis;
	
	public static ServiceSharedData Instance(Context ctx)
	{
		if(_instance == null)
			_instance = new ServiceSharedData(ctx);
		return _instance;
	}

	/** We have to restore the previous state in case the service gets destroyed
	 * and recreated
	 * 
	 * @param ctx the application context
	 */
	private ServiceSharedData(Context ctx)
	{
		mSharedDataSaveRestore = 
				new SharedDataSaveRestore(ctx.getSharedPreferences(Settings.PREFERENCES_NAME,
						Context.MODE_PRIVATE));
		mNotificationDataHash = mSharedDataSaveRestore.loadNotificationData();
		mLastNotifiedTimeMillis = mSharedDataSaveRestore.getLastNotifiedTimeMillis();
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
		 * Immediately save the data if the service is killed.
		 */
		mNotificationDataHash.put(type, notificationData);
		mLastNotifiedTimeMillis = System.currentTimeMillis(); /* now */
		/* save */
		mSharedDataSaveRestore.saveNotificationData(mNotificationDataHash);
		mSharedDataSaveRestore.setLastNotifiedTimeMillis(mLastNotifiedTimeMillis);
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
		long diffTimeMs =  Calendar.getInstance().getTime().getTime() - mLastNotifiedTimeMillis;
		Log.e("ServiceSharedData.canBeConsideredNew", "difftime ms = " + diffTimeMs + " min millis " + minMillis);
		if(diffTimeMs < minMillis)
		{
			Log.e("ServiceSharedData", "diffTimeMillis < minMillis CANNOT BE CONSIDERETH NEW");
			return false; /* not new */
		}
		
		Log.e("ServiceSharedData", "diffTimeMillis > minimum --> NEW");
		return true; /* elapsed time is greater than the minimum interval required between notifications */
	}

	public void updateCurrentRequest(NotificationData notificationData) 
	{
		short type = notificationData.getType();
		/* replace old notificationData for the specified type.
		 * Remember that only one notificationData per type can be considered.
		 */
		mNotificationDataHash.put(type, notificationData);
		/* save data on shared preferences */
		mSharedDataSaveRestore.saveNotificationData(mNotificationDataHash);
	}
}
