package it.giacomos.android.osmer.pro.widgets.map;

import it.giacomos.android.osmer.pro.R;
import it.giacomos.android.osmer.pro.locationUtils.GeoCoordinates;
import it.giacomos.android.osmer.pro.preferences.Settings;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


/**
 * This class manages distance measuring between two points: mCenterPoint and mTappedPoint.
 * 
 * States:
 * - the measure becomes active when the user taps on a point of the map (a circle becomes visible);
 * - by default, the measured distance is taken from the MyLocation overlay and the tapped point;
 * - after the user taps, it is possible to move around the center of the circle
 * - tapping outside a sensible area deactivates the measure;
 * - tapping inside the sensible area makes the circle grow/shrink
 * - tapping on a small area around the arrow, the arrow point can be moved
 * 
 * @author giacomo
 *
 */
public class MeasureOverlay implements OOverlayInterface ,
OnMarkerDragListener,
OnMapClickListener
{
	private OMapFragment mMapFragment;
	private Marker mp0;
	private Marker mp1;
	private Polyline mLine;
	private MeasureOverlayChangeListener mMeasureOverlayChangeListener;
	private boolean markerDragHintEnabled;
	private Settings mSettings;

	MeasureOverlay(OMapFragment mapFragment)
	{
		mp0 = null;
		mp1 = null;
		mLine = null;
		mMapFragment = mapFragment;
		mMeasureOverlayChangeListener = mapFragment;
		mSettings = new Settings(mapFragment.getActivity().getApplicationContext());
		markerDragHintEnabled = mSettings.isMapMoveToMeasureHintEnabled();
	}

	public void show()
	{
		GoogleMap map = mMapFragment.getMap();
		Resources res = mMapFragment.getResources();
		LatLng ll0 = null, ll1;
		Location l0 = null;
		try
		{
			l0 = map.getMyLocation();
			if(l0 != null)
				ll0 = new LatLng(l0.getLatitude(), l0.getLongitude());
			else
				ll0 = GeoCoordinates.radarImageCenter;
		}
		catch(IllegalArgumentException e)
		{
			mMeasureOverlayChangeListener.onMeasureOverlayErrorMessage(R.string.my_location_layer_not_enabled);
			ll0 = GeoCoordinates.fvgNorthEast;
		}
		float [] results = new float[1];

		Settings settings = new Settings(mMapFragment.getActivity().getApplicationContext());
		boolean restore = settings.hasMeasureMarker1LatLng();
		if(restore)
		{
			float [] positions = settings.getMeasureMarker1LatLng();
			ll1 = new LatLng(positions[0], positions[1]);
		}
		else
		{
			Location.distanceBetween(ll0.latitude, ll0.longitude, 
					GeoCoordinates.radarImageCenter.latitude, 
					GeoCoordinates.radarImageCenter.longitude, results);
			
			if(results[0] < 5000) /* position of the user too close to the radar image center */
				ll1 = GeoCoordinates.fvgSouthWest;
			else /* decide arbitrarily to put p1 in the center of the radar image */
				ll1 = GeoCoordinates.radarImageCenter;
		}

		BitmapDescriptor marker0BitmapDescriptor = 
				BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
		BitmapDescriptor marker1BitmapDescriptor = 
				BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);

		mp0 = map.addMarker(new MarkerOptions().icon(marker0BitmapDescriptor).title("PO").position(ll0)
				.snippet(res.getString(R.string.drag_p0)));

		mp1 = map.addMarker(new MarkerOptions().icon(marker1BitmapDescriptor).title("P1").position(ll1)
				.snippet(res.getString(R.string.drag_p1)));
		
		mp0.setDraggable(true);
		mp1.setDraggable(true);
		
		mp1.showInfoWindow();
		
		map.setOnMarkerDragListener(this);
		map.setOnMapClickListener(this);
	}

	@Override
	public void clear() 
	{
		/* save the position of p1 for the next time */
		mSettings.setMeasureMarker1LatLng((float) mp1.getPosition().latitude, (float) mp1.getPosition().longitude);
		/* do not show baloon hint again */
		if(mSettings.isMapMoveToMeasureHintEnabled() && !markerDragHintEnabled)
			mSettings.setMapMoveToMeasureHintEnabled(false);
		
		if(mp0 != null)
			mp0.remove();
		if(mp1 != null)
			mp1.remove();
		if(mLine != null)
			mLine.remove();
	}

	@Override
	public int type() 
	{
		return 0;
	}

	@Override
	public void hideInfoWindow() 
	{
		mp0.hideInfoWindow();
		mp1.hideInfoWindow();
	}

	@Override
	public boolean isInfoWindowVisible() 
	{
		return mp0.isInfoWindowShown() || mp1.isInfoWindowShown();
	}

	@Override
	public void onMarkerDrag(Marker m) 
	{
		
		
	}

	@Override
	public void onMarkerDragEnd(Marker m) 
	{
		Resources res = mMapFragment.getResources();
		/* calculate distance */
		String measUnit = res.getString(R.string.km);
		float [] distance = new float[1];
		Location.distanceBetween(
				mp0.getPosition().latitude, 
				mp0.getPosition().longitude, 
				mp1.getPosition().latitude, 
				mp1.getPosition().longitude, 
				distance);
		
		if(distance[0] < 1000)
			measUnit = res.getString(R.string.meters);
		else
			distance[0] = distance[0] / 1000.0f; /* convert to km */
		
		mp0.setSnippet(res.getString(R.string.dist_from_p1) + String.format(" %.1f", distance[0]) + measUnit);
		mp1.setSnippet(res.getString(R.string.dist_from_p0) + String.format(" %.1f", distance[0]) + measUnit);
		
		if(m.getTitle().equalsIgnoreCase("P0"))
		{
			mp0.showInfoWindow();
		}
		else
		{
			mp1.showInfoWindow();
		}
		
		int color = Color.argb(130, 0, 25, 245);
		PolylineOptions poly = new PolylineOptions();
		poly.add(mp0.getPosition());
		poly.add(mp1.getPosition());
		poly.color(color);
		poly.width(2.5f);
		
		mLine = mMapFragment.getMap().addPolyline(poly);
		markerDragHintEnabled = false;
	}

	@Override
	public void onMarkerDragStart(Marker m) 
	{
		mp1.hideInfoWindow();
		if(mLine != null)
		{
			mLine.remove();
			mLine = null;
		}
	}

	@Override
	public void onMapClick(LatLng ll) 
	{
		Log.e("MeasureOverlay: onMapClick", "clicked on " + ll.latitude + ", " + ll.longitude);
		if(mLine != null)
		{
			mLine.remove();
			mLine = null;
		}
		
	}

}


