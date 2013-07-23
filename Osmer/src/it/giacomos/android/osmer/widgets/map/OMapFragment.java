package it.giacomos.android.osmer.widgets.map;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;

import it.giacomos.android.osmer.ViewType;
import it.giacomos.android.osmer.locationUtils.GeoCoordinates;
import it.giacomos.android.osmer.observations.ObservationData;
import it.giacomos.android.osmer.observations.ObservationDrawableIdPicker;
import it.giacomos.android.osmer.observations.ObservationTime;
import it.giacomos.android.osmer.observations.ObservationType;
import it.giacomos.android.osmer.observations.ObservationsCacheUpdateListener;
import it.giacomos.android.osmer.preferences.Settings;
import it.giacomos.android.osmer.webcams.AdditionalWebcams;
import it.giacomos.android.osmer.webcams.OtherWebcamListDecoder;
import it.giacomos.android.osmer.webcams.WebcamData;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import android.os.Bundle;
import android.os.Parcelable;

public class OMapFragment extends MapFragment 
implements ObservationsCacheUpdateListener,
GoogleMap.OnCameraChangeListener
{
	public final int minLatitude = GeoCoordinates.bottomRight.getLatitudeE6();
	public final int maxLatitude = GeoCoordinates.topLeft.getLatitudeE6();
	public final int minLongitude = GeoCoordinates.topLeft.getLongitudeE6();
	public final int maxLongitude = GeoCoordinates.bottomRight.getLongitudeE6();

	private float mOldZoomLevel;
	private boolean mCenterOnUpdate;
	private boolean mMapReady; /* a map is considered ready after first camera update */
	private RadarOverlay mRadarOverlay;
	private ObservationsOverlay mObservationsOverlay = null;
	private MapViewMode mMode = null;
	private GoogleMap mMap;
	private ZoomChangeListener mZoomChangeListener;
	private ArrayList <OOverlayInterface> mOverlays;

	public OMapFragment() 
	{
		super();
		mCenterOnUpdate = false;
		mMapReady = false;
		mOldZoomLevel = -1.0f;
		mZoomChangeListener = null;
		mOverlays = new ArrayList<OOverlayInterface>();
	}


	@Override
	public void onCameraChange(CameraPosition cameraPosition) 
	{
		if(mCenterOnUpdate)
		{
			/* center just once */
			mCenterOnUpdate = false;
			CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(GeoCoordinates.regionBounds, 20);
			mMap.animateCamera(cu);
		}

		if(!mMapReady)
		{
			mMapReady = true;
			/* bitmap already downloaded ? */
			if(mRadarOverlay.bitmapValid())
				mRadarOverlay.update();
		}
		if(mOldZoomLevel != cameraPosition.zoom && mZoomChangeListener != null)
			mZoomChangeListener.onZoomLevelChanged(cameraPosition.zoom);

		mOldZoomLevel = cameraPosition.zoom;
		Log.e("onCameraChanged: ", "zoom level " + mOldZoomLevel);
	} 

	/** Bug on MyLocationOverlay on android 4??
	 * If MyLocationOverlay enableMyLocation is called when GPS is disabled,
	 * it does not request gps updates. So activating gps later on does not
	 * make MyLocationOverlay get GPS updates.
	 * This is a hack to make things work on my Galaxy S3 running 4.1.x
	 * On 2.3.x things seem to work.
	 * 
	 * @param provider
	 */
	public void onPositionProviderEnabled(String provider)
	{
		//		if(provider.equals("gps") && mMyLocationOverlay.isMyLocationEnabled())
		{
			//			mMyLocationOverlay.disableMyLocation();
			//			mMyLocationOverlay.enableMyLocation();
		}
	}

	public void onPositionProviderDisabled(String provider)
	{

	}

	public void onResume()
	{
		super.onResume();
		/* get the GoogleMap object. Must be called after onCreateView is called.
		 * If it returns null, then Google Play services is not available.
		 */
		mMap = getMap();
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		mMap.setMyLocationEnabled(true);

		Settings settings = new Settings(this.getActivity().getApplicationContext());
		if(settings.mapNeverCentered())
		{
			/* schedule center map on first camera update */
			mCenterOnUpdate = true;
			settings.setMapWasCentered(true);
		}
		mMap.setOnCameraChangeListener(this);
		mMode = null;
		setMode(new MapViewMode(ObservationType.RADAR, ObservationTime.DAILY));
	}

	public void onPause()
	{
		super.onPause();
		mMap.setMyLocationEnabled(false);
	}

	public void setRadarImage(Bitmap bmp) 
	{
		mRadarOverlay.updateBitmap(bmp);
		if(mMapReady) /* after first camera update */
			mRadarOverlay.update();
	}

	public void updateWebcamList(ArrayList<WebcamData> webcams)
	{
		//		if(mMode.currentMode == ObservationTime.WEBCAM && 
		//				mMode.currentType == ObservationType.WEBCAM &&
		//						mWebcamItemizedOverlay != null)
		//		{
		//			if(mWebcamItemizedOverlay.update(webcams))
		//				this.invalidate();
		//		}
	}

	@Override
	public void onObservationsCacheUpdate(HashMap<String, ObservationData> map, ViewType t) 
	{
		if((t == ViewType.DAILY_TABLE && mMode.currentMode == ObservationTime.DAILY ) ||
				(t == ViewType.LATEST_TABLE && mMode.currentMode == ObservationTime.LATEST))
		{
			this.updateObservations(map);
		}
	}

	public void updateObservations(HashMap<String, ObservationData> map)
	{
		if(mObservationsOverlay != null)
		{
			mObservationsOverlay.setData(map);
			mObservationsOverlay.update(Math.round(mMap.getCameraPosition().zoom));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState); /* modificato x map v2 */
		Bundle bundle = new Bundle();
		bundle.putParcelable("RadarBitmap", mRadarOverlay.getBitmap());
		bundle.putBoolean("measureEnabled", mCircleOverlay != null);
		if(mCircleOverlay != null)
			mCircleOverlay.saveState(bundle);
		//		return bundle;
	}

	public void onRestoreInstanceState (Parcelable state)
	{
		Bundle b = (Bundle) state;
		//		super.onRestoreInstanceState(b.getParcelable("OMapViewState"));
		if(b.containsKey("RadarBitmap"))
		{
			Bitmap bmp = b.getParcelable("RadarBitmap");
			mRadarOverlay.updateBitmap(bmp);
		}
		if(b.containsKey("measureEnabled"))
		{
			setMeasureEnabled(b.getBoolean("measureEnabled"));
			if(mCircleOverlay != null)
				mCircleOverlay.restoreState(state);
		}
	}

	public void setMode(MapViewMode m)
	{
		if(m.equals(mMode))
		{
			return;
		}

		mMode = m;
		//		new BaloonOffMap(this);
		for(int i = 0; i < mOverlays.size(); i++)
			mOverlays.get(i).clear();
		mOverlays.clear();

		switch(m.currentType)
		{
		case RADAR:
			if(mRadarOverlay == null)
				mRadarOverlay = new RadarOverlay(mMap);
			mOverlays.add(mRadarOverlay);
			break;
			//		case SAT:
			//			break;
			//		case WEBCAM:
			//			Drawable webcamIcon = getResources().getDrawable(R.drawable.camera_web_map);
			//			ArrayList<WebcamData> webcamData = null;
			//			if(mWebcamItemizedOverlay == null) 
			//			{
			//				/* first time we enter webcam mode */
			//				webcamData = mGetAdditionalWebcamsData();
			//				mWebcamItemizedOverlay = new WebcamItemizedOverlay<OverlayItem>(webcamIcon, this);
			//				setOnZoomChangeListener(mWebcamItemizedOverlay);
			//				this.updateWebcamList(webcamData);
			//			}
			//			overlays.add(mWebcamItemizedOverlay);
			//			break;
			//			
		default:
			ObservationDrawableIdPicker observationDrawableIdPicker = new ObservationDrawableIdPicker();
			int resId = observationDrawableIdPicker.pick(m.currentType);
			observationDrawableIdPicker = null; 
			if(resId > -1)
			{
				mObservationsOverlay = null;
				mObservationsOverlay = new ObservationsOverlay(resId, m.currentType, 
						m.currentMode, this);
				setOnZoomChangeListener(mObservationsOverlay);
				mOverlays.add(mObservationsOverlay);
			}
			break;
		}
	}

	public void setOnZoomChangeListener(ZoomChangeListener l)
	{
		mZoomChangeListener = l;
	}

	public MapViewMode getMode()
	{
		return mMode;
	}

	public void setSatEnabled(boolean satEnabled)
	{
		//		setSatellite(satEnabled);
	}

	public boolean isMeasureEnabled()
	{
		return mCircleOverlay != null;
	}

	public void setMeasureEnabled(boolean en)
	{
		//		if(en)
		//		{
		//			mCircleOverlay = new CircleOverlay();
		//			getOverlays().add(mCircleOverlay);
		//		}
		//		else
		//		{
		//			getOverlays().remove(mCircleOverlay);
		//			mCircleOverlay = null;
		//		}
		//		this.invalidate();
	}

	public boolean baloonVisible()
	{
		return false; // aggiunto temporaneamente
		//		MapBaloon baloon = (MapBaloon) findViewById(R.id.mapbaloon);
		//		return (baloon != null && baloon.getVisibility() == View.VISIBLE);
	}

	public void removeBaloon()
	{
		//		MapBaloon baloon = (MapBaloon) findViewById(R.id.mapbaloon);
		//		if(baloon != null)
		//		{
		//			if(mWebcamItemizedOverlay != null)
		//				mWebcamItemizedOverlay.cancelCurrentWebcamTask();
		//			/* remove baloon */
		//			removeView(baloon);
		//			/* restore previous position of the map */
		//			getController().animateTo(baloon.getGeoPoint());
		//			baloon = null;
		//		}
	}

	public void dispatchDraw(Canvas canvas)
	{
		//		super.dispatchDraw(canvas);
		//		int zoomLevel = getZoomLevel();
		//		if(mOldZoomLevel != zoomLevel)
		//		{
		//			if(mZoomChangeListener != null)
		//				mZoomChangeListener.onZoomLevelChanged(zoomLevel);
		//			mOldZoomLevel = zoomLevel;
		//		}
	}

	public int suggestedBaloonWidth(MapBaloonInfoWindowAdapter.Type t)
	{
		int  wpix = 170; /* observations */
		Resources r = getResources();
		int orientation = r.getConfiguration().orientation;
		if(orientation == Configuration.ORIENTATION_PORTRAIT && t == MapBaloonInfoWindowAdapter.Type.WEBCAM)
			wpix = 310;
		else if(orientation == Configuration.ORIENTATION_LANDSCAPE && t == MapBaloonInfoWindowAdapter.Type.WEBCAM)
			wpix = 270;

		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, wpix, r.getDisplayMetrics());
	}

	public int suggestedBaloonHeight(MapBaloonInfoWindowAdapter.Type t)
	{
		int  hpix = 130; /* observations */
		Resources r = getResources();
		int orientation = r.getConfiguration().orientation;
		if(orientation == Configuration.ORIENTATION_PORTRAIT && t == MapBaloonInfoWindowAdapter.Type.WEBCAM)
			hpix = 280;
		else if(orientation == Configuration.ORIENTATION_LANDSCAPE && t == MapBaloonInfoWindowAdapter.Type.WEBCAM)
			hpix = 250;

		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, hpix, r.getDisplayMetrics());
	}

	public ArrayList<WebcamData > mGetAdditionalWebcamsData()
	{
		ArrayList<WebcamData> webcamData = null;
		String additionalWebcamsTxt = "";
		/* get fixed additional webcams list from assets */
		AdditionalWebcams additionalWebcams = new AdditionalWebcams(this.getActivity().getApplicationContext());
		additionalWebcamsTxt = additionalWebcams.getText();
		OtherWebcamListDecoder additionalWebcamsDec = new OtherWebcamListDecoder();
		webcamData = additionalWebcamsDec.decode(additionalWebcamsTxt, false);
		return webcamData;
	}


	//	private MyLocationOverlay mMyLocationOverlay = null;
	private CircleOverlay mCircleOverlay = null;
	//	private WebcamItemizedOverlay<OverlayItem> mWebcamItemizedOverlay = null;


}
