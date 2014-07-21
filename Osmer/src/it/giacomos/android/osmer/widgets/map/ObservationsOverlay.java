/**
 * 
 */
package it.giacomos.android.osmer.widgets.map;

import it.giacomos.android.osmer.pro.R;
import it.giacomos.android.osmer.locationUtils.LocationNamesMap;
import it.giacomos.android.osmer.observations.MapMode;
import it.giacomos.android.osmer.observations.ObservationData;
import it.giacomos.android.osmer.observations.ObservationType;
import it.giacomos.android.osmer.observations.SkyDrawableIdPicker;
import it.giacomos.android.osmer.preferences.Settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
	private MapMode mMapMode;	
	private ArrayList<Marker> mMarkers;
	private HashMap<String, ObservationData> mDataMap;
	private GoogleMap mMap;	
	private Resources mResources;
	private int mDefaultMarkerIconResourceId;
	private MapBaloonInfoWindowAdapter mMapBaloonInfoWindowAdapter;
	private Settings mSettings;

	public ObservationsOverlay(int defaultMarkerIconResId, 
			ObservationType oType, 
			MapMode mapMode,
			OMapFragment mapFragment) 
	{
		setObservationType(oType);
		setObservationTime(mapMode);

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

	/** Update the internal HashMap<String, ObservationData> that associates a location name
	 * to its observation data
	 * 
	 * This method does not create/update the map markers.
	 * 
	 * @see update
	 * 
	 * @param map HashMap containing couples location name/observation data.
	 */
	public void setData(HashMap<String, ObservationData> map)
	{
		mDataMap = map;
	}

	/** Creates the markers associated to each location in the internal HashMap storing
	 * the couples location name/observation data. Every hash map key contains a location.
	 * On each location a marker is placed. Its icon represents the observation data associated
	 * to the location.
	 * 
	 * <h3>Note</h3><p>The hash map has to be initialized with a previous call to setData.
	 * </p>
	 * @see setData
	 * 
	 * 
	 * @param level the current zoom level that applies to the map. The zoom level value indicates
	 * how many markers can be placed on the map: high zoom level: many markers, small zoom level,
	 * a few markers. 
	 */
	public void update(int level)
	{
		if(mDataMap.size() == 0)
			return;

		LocationNamesMap locMap = new LocationNamesMap();
		Vector<String> locationsForLevel = locMap.locationsForLevel(level);
		CustomMarkerBitmapFactory obsBmpFactory = new CustomMarkerBitmapFactory(mResources);
		ObservationDataToText observationDataToText = new ObservationDataToText(mMapMode, mObservationType, mResources);
		float calculatedFontSize = -1;
		if(mSettings.hasObservationsMarkerFontSize())
			obsBmpFactory.setInitialFontSize(mSettings.observationsMarkerFontSize());
		Bitmap icon = null;
		/* get the icon if observation type != SKY. If observation type == SKY, then the
		 * icon is picked through skyDrawableIdPicker (see if branch below).
		 */
		if(mObservationType != ObservationType.SKY)
			icon = BitmapFactory.decodeResource(mResources, mDefaultMarkerIconResourceId);

		/* 1. remove unnecessary markers (if zoom has decreased the number of necessary markers may 
		 *    decrease too.)
		 */

		/* remove */
		mRemoveUnnecessaryMarkers(locationsForLevel);

		for(String location : locationsForLevel)
		{
			LatLng latLng = locMap.get(location);
			/* 2. build and add the marker only if it is not already shown */
			if(latLng != null && !mMarkerPresent(location))
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
					markerOptions.snippet(observationDataToText.toText(odata));

					SkyDrawableIdPicker skyDrawableIdPicker = new SkyDrawableIdPicker();
					int resourceId = skyDrawableIdPicker.get(data);
					if(resourceId > -1)
					{
						/* for sky no label, so do not use obsBmpFactory */
						BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(resourceId);
						if(bitmapDescriptor != null)
							markerOptions.icon(bitmapDescriptor);
					}
				}
				else if(!data.contains("---") && odata != null)
				{
					markerOptions = new MarkerOptions();
					markerOptions.position(latLng);
					markerOptions.title(location);
					markerOptions.snippet(observationDataToText.toText(odata));
					markerOptions.icon(obsBmpFactory.getIcon(icon, data));
				}
				if(markerOptions != null)
				{
					mMarkers.add(mMap.addMarker(markerOptions));
				}
			}
		} /* end for */
		calculatedFontSize = obsBmpFactory.getCachedFontSize();
		if(calculatedFontSize > 0) /* may not have passed through obsBmpFactory if mObservationType == ObservationType.SKY */
			mSettings.setObservationsMarkerFontSize(obsBmpFactory.getCachedFontSize());
	}

	private boolean mMarkerPresent(String locationName)
	{
		for(Marker m : mMarkers)
			if(m.getTitle().compareTo(locationName) == 0)
				return true;
		return false;
	}

	private void mRemoveUnnecessaryMarkers(Vector<String> locationsForLevel) 
	{
		if(mMarkers.size() == 0)
			return;
		Marker m = null;
		String markerLocation;
		Iterator<Marker> markerIterator = mMarkers.iterator();
		while(markerIterator.hasNext())
		{
			m = markerIterator.next();
			markerLocation = m.getTitle();
			if(!locationsForLevel.contains(markerLocation))
			{
				m.remove();
				markerIterator.remove();
			}		
		}
	}

	@Override
	public boolean onMarkerClick(Marker m) 
	{
		ObservationData od = mDataMap.get(m.getTitle());
		if(od != null)
		{
			if(mObservationType == ObservationType.SKY)
			{
				int resourceId = new SkyDrawableIdPicker().get(od.get(mObservationType));
				if(resourceId > -1)
					mMapBaloonInfoWindowAdapter.setIcon(mResources.getDrawable(resourceId));
			}
			else
				mMapBaloonInfoWindowAdapter.setIcon(mResources.getDrawable(mDefaultMarkerIconResourceId));
			/* disable baloon hint from now on */
			mSettings.setMapMarkerHintEnabled(false);
		}
		return false;
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

	public MapMode getMapMode() {
		return mMapMode;
	}

	public void setObservationTime(MapMode mMapMode) {
		this.mMapMode = mMapMode;
	}

	@Override
	public void clear() 
	{
//		Log.e("ObservationsOverlay: cleara called", "maerkers size " + mMarkers.size());
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
