package it.giacomos.android.osmer.widgets.map;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import it.giacomos.android.osmer.MapFragmentListener;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.ViewType;
import it.giacomos.android.osmer.locationUtils.GeoCoordinates;
import it.giacomos.android.osmer.observations.ObservationData;
import it.giacomos.android.osmer.observations.ObservationDrawableIdPicker;
import it.giacomos.android.osmer.observations.ObservationTime;
import it.giacomos.android.osmer.observations.ObservationType;
import it.giacomos.android.osmer.observations.ObservationsCacheUpdateListener;
import it.giacomos.android.osmer.preferences.Settings;
import it.giacomos.android.osmer.webcams.AdditionalWebcams;
import it.giacomos.android.osmer.webcams.ExternalImageViewerLauncher;
import it.giacomos.android.osmer.webcams.LastImageCache;
import it.giacomos.android.osmer.webcams.OtherWebcamListDecoder;
import it.giacomos.android.osmer.webcams.WebcamData;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.os.Bundle;

public class OMapFragment extends MapFragment 
implements ObservationsCacheUpdateListener,
GoogleMap.OnCameraChangeListener,
WebcamOverlayChangeListener,
MeasureOverlayChangeListener
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
	private CameraPosition mSavedCameraPosition;
	private ZoomChangeListener mZoomChangeListener;
	private ArrayList <OOverlayInterface> mOverlays;
	private boolean mMapClickOnBaloonImageHintEnabled;

	/* MapFragmentListener: the activity must implement this in order to be notified when 
	 * the GoogleMap is ready.
	 */
	private MapFragmentListener mMapFragmentListener;

	public OMapFragment() 
	{
		super();
		mCenterOnUpdate = false;
		mMapReady = false;
		mOldZoomLevel = -1.0f;
		mMode = null;
		mZoomChangeListener = null;
		mSavedCameraPosition = null;
		mMapFragmentListener = null;
		mOverlays = new ArrayList<OOverlayInterface>();
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		try 
		{
			mMapFragmentListener = (MapFragmentListener) activity;
		} 
		catch (ClassCastException e) 
		{
			throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
		}
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
			mMapReady = true;

		if(mSavedCameraPosition != null)
		{
			mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mSavedCameraPosition));
			mSavedCameraPosition = null; /* restore camera just once! */
		}
		else
		{
			if(mOldZoomLevel != cameraPosition.zoom && mZoomChangeListener != null)
				mZoomChangeListener.onZoomLevelChanged(cameraPosition.zoom);
			mOldZoomLevel = cameraPosition.zoom;
		}
	} 

	public void onStart()
	{
		//		Log.e("OMapFragment", "onStart()");
		super.onStart();
	}

	public void onDestroy ()
	{
		//		Log.e("OMapFragment", "onDestroy()");
		Settings settings = new Settings(getActivity().getApplicationContext());
		CameraPosition cameraPos = mMap.getCameraPosition();
		settings.saveMapCameraPosition(cameraPos);
		/* The bitmap stored into the mRadarOverlay has to be saved */
		mRadarOverlay.saveOnInternalStorage(getActivity().getApplicationContext());
		mRadarOverlay.finalize(); /* recycles bitmap for GC */
		super.onDestroy();
	}

	public void onCreate(Bundle savedInstanceState)
	{
		//		Log.e("OMapFragment", "onCreate()");
		super.onCreate(savedInstanceState);
	}

	public void onResume()
	{
		//		Log.e("OMapFragment", "onResume()");
		super.onResume();
		mMap.setMyLocationEnabled(true);
	}

	public void onPause()
	{
		//		Log.e("OMapFragment", "onPause()");
		super.onPause();
		mMap.setMyLocationEnabled(false);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState); /* modificato x map v2 */
	}

	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
	}

	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = super.onCreateView(inflater, container, savedInstanceState);

		/* get the GoogleMap object. Must be called after onCreateView is called.
		 * If it returns null, then Google Play services is not available.
		 */
		mMap = getMap();
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		UiSettings uiS = mMap.getUiSettings();
		uiS.setRotateGesturesEnabled(false);

		Settings settings = new Settings(getActivity().getApplicationContext());
		mSavedCameraPosition = settings.getCameraPosition();
		if(mSavedCameraPosition == null) /* never saved */
			mCenterOnUpdate = true;
		mMap.setOnCameraChangeListener(this);
		mRadarOverlay = new RadarOverlay(mMap);
		/* initializes internal bitmap loading the last bitmap saved in the internal memory.
		 * This does not imply an update of the image over the map.
		 */
		mRadarOverlay.restoreFromInternalStorage(getActivity().getApplicationContext());

		setMode(new MapViewMode(ObservationType.RADAR, ObservationTime.DAILY));

		mMapFragmentListener.onGoogleMapReady();
		
		Settings s = new Settings(getActivity().getApplicationContext());
		mMapClickOnBaloonImageHintEnabled = s.isMapClickOnBaloonImageHintEnabled();

		return view;
	}


	public void setRadarImage(Bitmap bmp) 
	{
//		Log.e("setRadarImage", "entro, mode " + mMode.currentMode + ", type " + mMode.currentType);
		mRadarOverlay.updateBitmap(bmp);
		if(mMapReady && mMode.currentType == ObservationType.RADAR) /* after first camera update */
		{
//			Log.e("setRadarImage", "in effetti, aggiorno");
			mRadarOverlay.update();
		}
	}

	public void centerMap() 
	{
		if(mMapReady)
		{
			CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(GeoCoordinates.regionBounds, 20);
			mMap.animateCamera(cu);
		}
	}

	public void updateWebcamList(ArrayList<WebcamData> webcams)
	{
		if(mMode.currentMode == ObservationTime.WEBCAM && 
				mMode.currentType == ObservationType.WEBCAM &&
				mWebcamOverlay != null)
		{
			mWebcamOverlay.update(webcams, this.getResources());
		}
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

	/* the main activity after setMode invokes this method.
	 * ObservationData contains the last observation data, be it from cache or from 
	 * the net.
	 */
	public void updateObservations(HashMap<String, ObservationData> map)
	{
		if(mObservationsOverlay != null)
		{
			mObservationsOverlay.setData(map); /* update data */
			/* if the map mode is LATEST or DAILY, update the overlay */
			if(mMode != null && (mMode.currentMode == ObservationTime.LATEST || mMode.currentMode == ObservationTime.DAILY ) )
			{
//				Log.e("updateObservations:", "updating obs overlay current mode " + mMode.currentMode + ", type " + mMode.currentType);
				mObservationsOverlay.update(Math.round(mMap.getCameraPosition().zoom));
			}
//			else
//				Log.e("updateObservations:", "NOT !! updating obs overlay current mode " + mMode.currentMode + ", type " + mMode.currentType);
		}
	}

	public void setMode(MapViewMode m)
	{
//		Log.e("--->OMApFtagment: setMode invoked", "setMode invoked with mode: " + m.currentMode + ", time (type): " + m.currentType);
		if(m.equals(mMode))
			return;

		/* mMode is still null the first time this method is invoked */
		if(mMode != null && mMode.currentType == ObservationType.RADAR)
			setMeasureEnabled(false);
		
		mMode = m;

		mUninstallAdaptersAndListeners();
		
		switch(m.currentType)
		{
		case RADAR:
			/* update the overlay with a previously set bitmap */
//			Log.e("OMapFtagment: setMode:", "calling update on radar overlay");
			mRadarOverlay.update();
			mRemoveOverlays();
			mOverlays.add(mRadarOverlay);

			break;
		case WEBCAM:
			ArrayList<WebcamData> webcamData = mGetAdditionalWebcamsData();
			/// if(mWebcamOverlay == null)  /* first time we enter webcam mode */
				mWebcamOverlay = new WebcamOverlay(R.drawable.camera_web_map, this);
			
			this.updateWebcamList(webcamData);
			mRemoveOverlays();
			mOverlays.add(mWebcamOverlay);
			break;

		default:
			ObservationDrawableIdPicker observationDrawableIdPicker = new ObservationDrawableIdPicker();
			int resId = observationDrawableIdPicker.pick(m.currentType);
			observationDrawableIdPicker = null; 
			if(resId > -1)
			{
				/* no need for clear(). It is called in mObservationsOverlay.update() */
				mObservationsOverlay = null;
				mObservationsOverlay = new ObservationsOverlay(resId, m.currentType, 
						m.currentMode, this);
				setOnZoomChangeListener(mObservationsOverlay);
				mRemoveOverlays();
				mOverlays.add(mObservationsOverlay);
				mObservationsOverlay.update(Math.round(mMap.getCameraPosition().zoom));
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

	public void setTerrainEnabled(boolean satEnabled)
	{
		mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
	}

	public boolean isTerrainEnabled()
	{
		return mMap.getMapType() == GoogleMap.MAP_TYPE_TERRAIN;
	}

	public void setSatEnabled(boolean satEnabled)
	{
		mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
	}

	public boolean isSatEnabled()
	{
		return mMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE;
	}

	public void setNormalViewEnabled(boolean checked) 
	{
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	}

	public boolean isNormalViewEnabled()
	{
		return mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL;
	}

	public boolean isMeasureEnabled()
	{
		return mMeasureOverlay != null;
	}

	public void setMeasureEnabled(boolean en)
	{
		if(en && mMode.currentType == ObservationType.RADAR)
		{
			mMeasureOverlay = new MeasureOverlay(this);
			mMeasureOverlay.show();
		}
		else if(mMeasureOverlay != null && mMode.currentType == ObservationType.RADAR)
		{
			/* removes markers, line (if drawn) and saves settings */
			mMeasureOverlay.clear(); 
			mMeasureOverlay = null;
			/* no markers in radar mode if measure overlay is disabled */
			mMap.setOnMarkerDragListener(null);
			mMap.setOnMapClickListener(null);
		}
	}

	public boolean isInfoWindowVisible()
	{
		for(int i = 0; i < mOverlays.size(); i++)
			if(mOverlays.get(i).isInfoWindowVisible())
				return true;
		return false;
	}

	public void hideInfoWindow()
	{
		for(int i = 0; i < mOverlays.size(); i++)
			mOverlays.get(i).hideInfoWindow();
	}
	
	@Override
	public void onBitmapChanged(Bitmap bmp) 
	{
		Context ctx = getActivity().getApplicationContext();
		/* save image on cache in order to display it in external viewer */
		LastImageCache saver = new LastImageCache();
		boolean success = saver.save(bmp, ctx);
		if(success)
		{
			if(mMapClickOnBaloonImageHintEnabled)
				Toast.makeText(ctx, R.string.hint_click_on_map_baloon_webcam_image, Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	public void onInfoWindowImageClicked() 
	{
		ExternalImageViewerLauncher eivl = new ExternalImageViewerLauncher();
		eivl.startExternalViewer(getActivity());
		new Settings(getActivity().getApplicationContext()).setMapClickOnBaloonImageHintEnabled(false);
		mMapClickOnBaloonImageHintEnabled = false;	
	}
	
	@Override
	public void onErrorMessageChanged(String message) 
	{
		message = getResources().getString(R.string.error_message) + ": " + message;
		Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
	}@Override
	
	public void onMessageChanged(int stringId) 
	{
		Toast.makeText(getActivity().getApplicationContext(), getResources().getString(stringId), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onBitmapTaskCanceled(String url) 
	{
		String message = getResources().getString(R.string.webcam_download_task_canceled)  + url;
		Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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

	private void mRemoveOverlays()
	{
		for(int i = 0; i < mOverlays.size(); i++)
			mOverlays.get(i).clear();
		mOverlays.clear();
	}
	
	private void mUninstallAdaptersAndListeners()
	{
		mMap.setInfoWindowAdapter(null);
		mMap.setOnMapClickListener(null);
		mMap.setOnMarkerClickListener(null);
		mMap.setOnMarkerDragListener(null);
		mMap.setOnInfoWindowClickListener(null);
		setOnZoomChangeListener(null);
	}

	private MeasureOverlay mMeasureOverlay = null;
	private WebcamOverlay mWebcamOverlay = null;

	@Override
	public void onMeasureOverlayErrorMessage(int stringId) 
	{
		Toast.makeText(this.getActivity().getApplicationContext(), getResources().getString(stringId), Toast.LENGTH_LONG).show();
	}
{
		// TODO Auto-generated method stub
		
	}
}
