package it.giacomos.android.osmer.pro.service.sharedData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

public abstract class NotificationData 
{
	public static short TYPE_REQUEST = 0;
	public static short TYPE_REPORT = 1;
	
	public double latitude, longitude;
	
	protected Date date;
	
	/* if true, the request has been satisfied, if false, it is already 
	 * pending.
	 * When the get_requests.php returns an empty document, it means that
	 * any request concerning our location has been consumed.
	 */
	private boolean mIsConsumed;
	
	public abstract short getType();
	
	public Date getDate()
	{
		return date;
	}
	
	public NotificationData()
	{
		latitude = longitude = -1;
		mIsConsumed = false;
	}
	
	public void setConsumed(boolean consumed)
	{
		mIsConsumed = consumed;
	}
	
	/**
	 * @return true if this NotificationData has been consumed (i.e. a request
	 * satisfied or a report visited).
	 * 
	 * This is used by the map view in order to show or not a marker in correspondance
	 * of the location where this data is bound. 
	 * Actually, the notification data is not removed from the service shared data 
	 * until a new notification arrives. In other words, if a notification data for this 
	 * location has been withdrawn, isConsumed will be true but the notification data still
	 * remains.
	 */
	public boolean isConsumed()
	{
		return mIsConsumed;
	}
	
	public int makeId()
	{
		int id = -1;
		/* need to check: a malformed string may have lead to a null date in makeDate */
		if(date != null)
			id = (int) getDate().getTime();
		return id;
	}
	
	public void makeDate(String datestr)
	{
		try{
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
			date = formatter.parse(datestr);
		} 
		catch(Exception e)
		{
			Log.e("NotificationData.makeDate", e.getLocalizedMessage());
		}
	}
	
	public boolean equals(NotificationData other)
	{
		Log.e("NotificationData.equals ", "result: " + String.valueOf(other.getType() == getType()) + ", " + 
				String.valueOf( other.latitude == latitude) + ", " + 
				String.valueOf(other.longitude == longitude)  +", " +  String.valueOf(other.date.equals(date)));
		Log.e("NotificationData.equals ", "date 1 " + date.toLocaleString() + ", other date " + other.date.toLocaleString());
		return other.getType() == getType() && other.latitude == latitude &&
				other.longitude == longitude && other.date.equals(date);
	}
}
