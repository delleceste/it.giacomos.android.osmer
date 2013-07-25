/**
 * 
 */
package it.giacomos.android.osmer.widgets.map;

import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.locationUtils.LocationNamesMap;
import it.giacomos.android.osmer.observations.ObservationData;
import it.giacomos.android.osmer.observations.ObservationTime;
import it.giacomos.android.osmer.observations.ObservationType;
import it.giacomos.android.osmer.observations.SkyDrawableIdPicker;
import it.giacomos.android.osmer.preferences.Settings;
import it.giacomos.android.osmer.webcams.ExternalImageViewerLauncher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

/**
 * @author giacomo
 * @param <Item>
 *
 */
public class ObservationsOverlay
	implements ZoomChangeListener,
	OOverlayInterface,
	OnMarkerClickListener
{
	private ObservationType mObservationType;
	private ObservationTime mObservationTime;	
	private ArrayList<Marker> mMarkers;
	private HashMap<String, ObservationData> mDataMap;
	private GoogleMap mMap;	
	private Resources mResources;
	private int mDefaultMarkerIconResourceId;
	private MapBaloonInfoWindowAdapter mMapBaloonInfoWindowAdapter;
	private Settings mSettings;
	
	public ObservationsOverlay(int defaultMarkerIconResId, 
			ObservationType oType, 
			ObservationTime oTime,
			OMapFragment mapFragment) 
	{
		setObservationType(oType);
		setObservationTime(oTime);
		
		mMarkers = new ArrayList<Marker>();
		mDataMap = new HashMap<String, ObservationData>();
		mMap = mapFragment.getMap();
		mResources = mapFragment.getResources();
		mDefaultMarkerIconResourceId = defaultMarkerIconResId;
		mMapBaloonInfoWindowAdapter = new MapBaloonInfoWindowAdapter(mapFragment.getActivity());
		mMap.setInfoWindowAdapter(mMapBaloonInfoWindowAdapter);
		mMap.setOnMarkerClickListener(this);
		mSettings = new Settings(mapFragment.getActivity().getApplicationContext());
	}
	
	public void initListenersAndAdapters()
	{
		
	}
	
	public void setData(HashMap<String, ObservationData> map)
	{
		mDataMap = map;
	}

	/**
	 * updates the overlay with the new values in the map
	 * @param map a map containing the City and the 
	 */
	public void update(int level)
	{
		if(mDataMap.size() == 0)
			return;
		
		LocationNamesMap locMap = new LocationNamesMap();
		Vector<String> locationsForLevel = locMap.locationsForLevel(level);
		CustomMarkerBitmapFactory obsBmpFactory = new CustomMarkerBitmapFactory(mResources);
		if(mSettings.hasObservationsMarkerFontSize())
			obsBmpFactory.setInitialFontSize(mSettings.observationsMarkerFontSize());
		
		/* 1. remove unnecessary markers (if zoom has decreased the number of necessary markers may 
		 *    decrease too.)
		 */
		
		/* remove */
		mRemoveUnnecessaryMarkers(locationsForLevel);
		
		for(String location : locationsForLevel)
		{
			LatLng latLng = locMap.get(location);
			/* 2. build and add the marker only if it is not already shown */
			if(latLng != null && !mMarkerPresent(latLng))
			{ 
				String data = mResources.getString(R.string.not_available);
				ObservationData odata = null;
				if(mDataMap.containsKey(location))
					odata = mDataMap.get(location);
				if(odata != null)
					data = odata.get(mObservationType);
				
				MarkerOptions markerOptions = null;
				/* in the title marker option we save the location */
				if(mObservationType == ObservationType.SKY && odata != null)
				{
					markerOptions = new MarkerOptions();
					markerOptions.position(latLng);
					markerOptions.title(location);
					markerOptions.snippet(makeText(odata));
					
					SkyDrawableIdPicker skyDrawableIdPicker = new SkyDrawableIdPicker();
					int resourceId = skyDrawableIdPicker.get(data);
					if(resourceId > -1)
					{
						BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(resourceId);
						if(bitmapDescriptor != null)
							markerOptions.icon(obsBmpFactory.getIcon(resourceId, mResources, data));
					}
				}
				else if(!data.contains("---") && odata != null)
				{
					markerOptions = new MarkerOptions();
					markerOptions.position(latLng);
					markerOptions.title(location);
					markerOptions.snippet(makeText(odata));
					markerOptions.icon(obsBmpFactory.getIcon(mDefaultMarkerIconResourceId, mResources, data));
				}
				if(markerOptions != null)
				{
					mMarkers.add(mMap.addMarker(markerOptions));
				}
			}
		} /* end for */
		mSettings.setObservationsMarkerFontSize(obsBmpFactory.getCachedFontSize());
	}

	private boolean mMarkerPresent(LatLng ll)
	{
		for(Marker m : mMarkers)
			if(m.getPosition() == ll)
				return true;
		return false;
	}
	
	private void mRemoveUnnecessaryMarkers(Vector<String> locationsForLevel) 
	{
		if(mMarkers.size() == 0)
			return;
		Log.e("mRemoveUnnecessaryMarkers", "processing locations n. " + locationsForLevel.size() + ", markers " + mMarkers.size());
		boolean found = false; 
		Marker m = null;
		ArrayList<Marker> newMarkers = new ArrayList<Marker>();
		for(String location : locationsForLevel)
		{
			found = false;
			for(int i = 0; i < mMarkers.size(); i++)
			{
				m = mMarkers.get(i);
				if(m.getTitle().compareTo(location) == 0)
				{
					found = true;
					Log.e("mRemoveUnnecessaryMarkers", "keeping " + m.getTitle());
					newMarkers.add(m);
					break;
				}
			}
			if(!found) /* add to new list */
			{
				m.remove();
				mMarkers.remove(m);
				Log.e("mRemoveUnnecessaryMarkers", "removing " + m.getTitle());
			}
		}
	}

	@Override
	public boolean onMarkerClick(Marker m) 
	{
		return false;
	}
	
	private String makeText(ObservationData od)
	{
		String txt;
		txt = od.time + " - " + mResources.getString(R.string.sky) + ": " + od.sky + "\n";
		
		if(mObservationTime == ObservationTime.DAILY)
		{
			if(mObservationType == ObservationType.SKY || 
					mObservationType == ObservationType.MIN_TEMP ||  
					mObservationType == ObservationType.MAX_TEMP ||  
					mObservationType == ObservationType.MEAN_TEMP)
			{
				boolean hasTMin, hasTMax;
				hasTMin = od.has(ObservationType.MIN_TEMP);
				hasTMax = od.has(ObservationType.MAX_TEMP);
				/* tmin and tmax abbreviated on the same line */
				if(hasTMin)
					txt += mResources.getString(R.string.min_temp_abbr) + ": " + od.tMin;
				if(hasTMax)
					txt += mResources.getString(R.string.max_temp_abbr) + ": " + od.tMax;
				if(hasTMin || hasTMax) /* newline if necessary */
					txt += "\n";
				if(od.has(ObservationType.MEAN_TEMP))
					txt += mResources.getString(R.string.mean_temp) + ": " + od.tMed + "\n";
			}
			else if(mObservationType == ObservationType.RAIN)
			{
				if(od.has(ObservationType.RAIN))
					txt += mResources.getString(R.string.rain) + ": " + od.rain + "\n";	
			}
			else if(mObservationType == ObservationType.MAX_WIND || mObservationType == ObservationType.MEAN_WIND)
			{
				if(od.has(ObservationType.MEAN_WIND))
					txt += mResources.getString(R.string.mean_wind) + ": " + od.vMed + "\n";
				if(od.has(ObservationType.MAX_WIND))
					txt += mResources.getString(R.string.max_wind) + ": " + od.vMax + "\n";
			}
			else if(mObservationType == ObservationType.MEAN_HUMIDITY)
			{
				if(od.has(ObservationType.MEAN_HUMIDITY))
					txt += mResources.getString(R.string.mean_humidity) + ": " + od.uMed + "\n";
			}
		}
		else 
		{
			if(mObservationType == ObservationType.SKY || 
					mObservationType == ObservationType.TEMP ||  
					mObservationType == ObservationType.SEA ||  
					mObservationType == ObservationType.SNOW || 
					mObservationType == ObservationType.RAIN)
			{
				if(od.has(ObservationType.TEMP))
					txt += mResources.getString(R.string.temp) + ": " + od.temp + "\n";
				if(od.has(ObservationType.SEA))
					txt += mResources.getString(R.string.sea) + ": " + od.sea + "\n";
				if(od.has(ObservationType.SNOW))
					txt += mResources.getString(R.string.snow) + ": " + od.snow + "\n";
				if(od.has(ObservationType.RAIN))
				{
					String rain = od.rain;
					rain = rain.replaceAll("[^\\d+\\.)]", "");
					/* when observation type is rain, show rain even if rain is 0.0 */
					if(Float.parseFloat(rain) > 0.0f || mObservationType == ObservationType.RAIN)
						txt += mResources.getString(R.string.rain) + ": " + od.rain + "\n";
				}
			}
			else if(mObservationType == ObservationType.HUMIDITY && od.has(ObservationType.HUMIDITY))
				txt += mResources.getString(R.string.humidity) + ": " + od.humidity + "\n";
			else if(mObservationType == ObservationType.PRESSURE && od.has(ObservationType.PRESSURE))
				txt += mResources.getString(R.string.pressure) + ": " + od.pressure + "\n";
			
			else if(mObservationType == ObservationType.WIND && od.has(ObservationType.WIND))
				txt += mResources.getString(R.string.wind) + ": " + od.wind + "\n";
		}

		return txt;
	}
	
	public boolean isInfoWindowVisible()
	{
		for(int i = 0; i < mMarkers.size(); i++)
			if(mMarkers.get(i).isInfoWindowShown())
				return true;
		return false;
	}
	
	public void hideInfoWindow()
	{
		for(int i = 0; i < mMarkers.size(); i++)
			mMarkers.get(i).hideInfoWindow();
	}
	
	@Override
	public void onZoomLevelChanged(float level) {
		this.update(Math.round(level));
	}
	

	public int size() 
	{
		return mMarkers.size();
	}
	
	public ObservationType getObservationType() {
		return mObservationType;
	}

	public void setObservationType(ObservationType mObservationType) {
		this.mObservationType = mObservationType;
	}

	public ObservationTime getObservationTime() {
		return mObservationTime;
	}

	public void setObservationTime(ObservationTime mObservationTime) {
		this.mObservationTime = mObservationTime;
	}

	
	@Override
	public void clear() 
	{
		for(int i = 0; i < mMarkers.size(); i++)
			mMarkers.get(i).remove();
		mMarkers.clear();
	}

	@Override
	public int type() {
		// TODO Auto-generated method stub
		return 0;
	}

}
