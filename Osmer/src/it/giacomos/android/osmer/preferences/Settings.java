package it.giacomos.android.osmer.preferences;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings 
{
	public Settings(Context ctx)
	{
		mSharedPreferences = ctx.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
	}
	
	public boolean isHomeIconsHintEnabled()
	{
		boolean res = mSharedPreferences.getBoolean("HINT_HOME_ICONS", true);
		return res;
	}
	
	public void setHomeIconsHintEnabled(boolean en)
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putBoolean("HINT_HOME_ICONS", en);
		e.commit();
	}

	public boolean isSwipeHintEnabled()
	{
		boolean res = mSharedPreferences.getBoolean("HINT_SWIPE", true);
		return res;
	}
	
	public void setSwipeHintEnabled(boolean en)
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putBoolean("HINT_SWIPE", en);
		e.commit();
	}
	
	public boolean isMapMeasureHintEnabled()
	{
		boolean res = mSharedPreferences.getBoolean("HINT_MAP_MEASURE", true);
		return res;
	}
	
	public void setMapMeasureHintEnabled(boolean en)
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putBoolean("HINT_MAP_MEASURE", en);
		e.commit();
	}

	public boolean isMapMoveToMeasureHintEnabled()
	{
		boolean res = mSharedPreferences.getBoolean("HINT_MAP_MOVE_TO_MEASURE", true);
		return res;
	}
	
	public void setMapMoveToMeasureHintEnabled(boolean en)
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putBoolean("HINT_MAP_MOVE_TO_MEASURE", en);
		e.commit();
	}

	public boolean isMapMoveLocationToMeasureHintEnabled()
	{
		boolean res = mSharedPreferences.getBoolean("HINT_MAP_MOVE_LOCATION_TO_MEASURE", true);
		return res;
	}
	
	public void setMapMoveLocationToMeasureHintEnabled(boolean en)
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putBoolean("HINT_MAP_MOVE_LOCATION_TO_MEASURE", en);
		e.commit();
	}
	
	public boolean isMapMarkerHintEnabled()
	{
		boolean res = mSharedPreferences.getBoolean("HINT_MAP_MARKER", true);
		return res;
	}
	
	public void setMapMarkerHintEnabled(boolean en)
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putBoolean("HINT_MAP_MARKER", en);
		e.commit();
	}	
	
	public boolean isObsScrollIconsHintEnabled()
	{
		boolean res = mSharedPreferences.getBoolean("HINT_OBS_SCROLL_ICONS", true);
		return res;
	}
	
	public void setObsScrollIconsHintEnabled(boolean en)
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putBoolean("HINT_OBS_SCROLL_ICONS", en);
		e.commit();
	}

	
	private final String PREFERENCES_NAME = "Osmer.conf";
	private SharedPreferences mSharedPreferences;
}
