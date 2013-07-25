package it.giacomos.android.osmer.preferences;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

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

	public boolean mapNeverCentered()
	{
		boolean res = mSharedPreferences.getBoolean("MAP_NEVER_CENTERED", true);
		return res;
	}

	public void setMapWasCentered(boolean was)
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putBoolean("MAP_NEVER_CENTERED", !was);
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

	public boolean hasMapWebcamMarkerFontSize()
	{
		return mSharedPreferences.contains("MAP_WEBCAM_MARKER_FONT_SIZE");
	}
	
	public float mapWebcamMarkerFontSize()
	{
		float res = mSharedPreferences.getFloat("MAP_WEBCAM_MARKER_FONT_SIZE", 21);
		return res;
	}
	
	public void setMapWebcamMarkerFontSize(float size) 
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putFloat("MAP_WEBCAM_MARKER_FONT_SIZE", size);
		e.commit();
	}

	public boolean hasObservationsMarkerFontSize()
	{
		return mSharedPreferences.contains("MAP_OBSERVATIONS_MARKER_FONT_SIZE");
	}
	
	public float observationsMarkerFontSize()
	{
		float res = mSharedPreferences.getFloat("MAP_OBSERVATIONS_MARKER_FONT_SIZE", 25);
		return res;
	}
	
	public void setObservationsMarkerFontSize(float size) 
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putFloat("MAP_OBSERVATIONS_MARKER_FONT_SIZE", size);
		e.commit();
	}
	
	public void setMapClickOnBaloonImageHintEnabled(boolean b) 
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putBoolean("HINT_MAP_CLICK_ON_BALOON_IMAGE", b);
		e.commit();
	}

	public boolean isMapClickOnBaloonImageHintEnabled() 
	{
		boolean res = mSharedPreferences.getBoolean("HINT_MAP_CLICK_ON_BALOON_IMAGE", true);
		return res;
	}

	public void saveMapCameraPosition(CameraPosition pos)
	{
		SharedPreferences.Editor e = mSharedPreferences.edit();
		e.putFloat("CameraZoom", pos.zoom);
		e.putFloat("CameraTargetLatitude", (float) pos.target.latitude);
		e.putFloat("CameraTargetLongitude", (float) pos.target.longitude);
		e.putFloat("CameraBearing", pos.bearing);
		e.putFloat("CameraTilt", pos.tilt);
		e.commit();
	}

	public CameraPosition getCameraPosition()
	{
		CameraPosition pos;
		/* check at least 3 keys to be sure all the parameters have been saved */
		if(mSharedPreferences.contains("CameraTargetLatitude") && mSharedPreferences.contains("CameraTargetLongitude")
				&& mSharedPreferences.contains("CameraTilt"))
		{
			LatLng latLng = new LatLng((double) mSharedPreferences.getFloat("CameraTargetLatitude", -1.0f), 
					(double) mSharedPreferences.getFloat("CameraTargetLongitude", -1.0f));
			pos = new CameraPosition.Builder().
					target(latLng).
					zoom(mSharedPreferences.getFloat("CameraZoom", -1.0f)).
					bearing(mSharedPreferences.getFloat("CameraBearing",-1.0f)).
					tilt(mSharedPreferences.getFloat("CameraTilt", -1.0f)).
					build();
			return pos;
		}
		return null;
	}


	private final String PREFERENCES_NAME = "Osmer.conf";
	private SharedPreferences mSharedPreferences;

}
