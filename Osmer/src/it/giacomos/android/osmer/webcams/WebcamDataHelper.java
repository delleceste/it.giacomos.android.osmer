package it.giacomos.android.osmer.webcams;
import it.giacomos.android.osmer.preferences.Settings;
import android.content.Context;
import android.util.Log;

public class WebcamDataHelper 
{
	private static final int UPTODATE_INTERVAL_MILLIS =  10 * 1000;
	
	public WebcamDataHelper()
	{
		
	}
	
	public void setDataUpdatedNow(Context ctx)
	{
		Settings s = new Settings(ctx);
		long now = System.currentTimeMillis();
		s.setWebcamLastUpdateTimestampMillis(now);
	}
	
	public  boolean dataIsOld(Context ctx)
	{
		Settings s = new Settings(ctx);
		long lastSavedTimestampMillis = s.getWebcamLastUpdateTimestampMillis();
		long currentTimestampMillis = System.currentTimeMillis();
		if(currentTimestampMillis - lastSavedTimestampMillis > UPTODATE_INTERVAL_MILLIS)
		{
			Log.e("WebcamDataHelper", "data is too old: current " +
					currentTimestampMillis + " last " + lastSavedTimestampMillis);
			return true;
		}
		else
		{
			Log.e("WebcamDataHelper", "data is NOT too old: current " +
					currentTimestampMillis + " last " + lastSavedTimestampMillis);
			return false;
		}
	}
}
