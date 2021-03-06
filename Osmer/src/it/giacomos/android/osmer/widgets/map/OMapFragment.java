package it.giacomos.android.osmer.widgets.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.fragments.MapFragmentListener;
import it.giacomos.android.osmer.locationUtils.GeoCoordinates;
import it.giacomos.android.osmer.network.Data.DataPoolBitmapListener;
import it.giacomos.android.osmer.network.Data.DataPoolCacheUtils;
import it.giacomos.android.osmer.network.state.BitmapType;
import it.giacomos.android.osmer.network.state.ViewType;
import it.giacomos.android.osmer.observations.MapMode;
import it.giacomos.android.osmer.observations.ObservationData;
import it.giacomos.android.osmer.observations.ObservationDrawableIdPicker;
import it.giacomos.android.osmer.observations.ObservationType;
import it.giacomos.android.osmer.observations.ObservationsCacheUpdateListener;
import it.giacomos.android.osmer.preferences.Settings;
import it.giacomos.android.osmer.webcams.ExternalImageViewerLauncher;
import it.giacomos.android.osmer.webcams.LastImageCache;
import it.giacomos.android.osmer.webcams.WebcamData;
import it.giacomos.android.osmer.webcams.WebcamXMLAssetLoader;
import it.giacomos.android.osmer.webcams.WebcamXMLDecoder;
import it.giacomos.android.osmer.widgets.OAnimatedTextView;
import it.giacomos.android.osmer.widgets.map.animation.RadarAnimation;
import it.giacomos.android.osmer.widgets.map.animation.RadarAnimationListener;
import it.giacomos.android.osmer.widgets.map.report.OnTiltChangeListener;
import it.giacomos.android.osmer.widgets.map.report.ReportOverlay;
import it.giacomos.android.osmer.widgets.map.report.network.PostReportAsyncTaskPool;
import it.giacomos.android.osmer.widgets.map.report.network.PostType;

public class OMapFragment extends SupportMapFragment 
implements ObservationsCacheUpdateListener,
GoogleMap.OnCameraIdleListener,
WebcamOverlayChangeListener,
MeasureOverlayChangeListener,
DataPoolBitmapListener,
RadarAnimationListener,
		OnMapReadyCallback,
		GoogleMap.OnMapLoadedCallback,
		GoogleMap.OnCameraMoveListener
{
	private float mOldZoomLevel;
	private boolean mCenterOnFirstRun;
	private boolean mMapReady; /* a map is considered ready after first camera update */
	private RadarOverlay mRadarOverlay;
	private ObservationsOverlay mObservationsOverlay = null;
	private MapViewMode mMode = null;
	private GoogleMap mMap;
	private CameraPosition mSavedCameraPosition;
	private ZoomChangeListener mZoomChangeListener;
	private OnTiltChangeListener mOnTiltChangeListener;
	private ArrayList <OOverlayInterface> mOverlays;
	private boolean mMapClickOnBaloonImageHintEnabled;
	private Settings mSettings;
	private RadarOverlayUpdateListener mRadarOverlayUpdateListener;
	private RadarAnimation mRadarAnimation;
	private ReportOverlay mReportOverlay;
	private boolean mIsInitialising;

	/* MapFragmentListener: the activity must implement this in order to be notified when 
	 * the GoogleMap is ready.
	 */
	private MapFragmentListener mMapFragmentListener;

	public OMapFragment() 
	{
		super();
		mMapReady = false;
		mOldZoomLevel = -1.0f;
		mZoomChangeListener = null;
		mSavedCameraPosition = null;
		mMapFragmentListener = null;
		mOverlays = new ArrayList<OOverlayInterface>();
		mMode = new MapViewMode(ObservationType.NONE, MapMode.RADAR);
		mRadarAnimation = null;
		mReportOverlay = null;
	}

	@Override
	public void onMapReady(GoogleMap googleMap) 
	{
        Log.e("OmMapFrag", "onMapReady");
		mIsInitialising = true;
		mMapReady = true;
		mMap = googleMap;
        mMapFragmentListener.onCameraReady();

		OsmerActivity oActivity = (OsmerActivity) getActivity();
		mSettings = new Settings(oActivity.getApplicationContext());
		/* restore last used map type */
		mMap.setMapType(mSettings.getMapType());
		/* restore last camera position */

		mMap.setOnCameraIdleListener(this);
        mMap.setOnMapLoadedCallback(this);

        if ( (ContextCompat.checkSelfPermission(getActivity(),
				Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
				|| (ContextCompat.checkSelfPermission(getActivity(),
				Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED) )
			mMap.setMyLocationEnabled(true);

          /* create a radar overlay */
        mRadarOverlay = new RadarOverlay(mMap);
		/* add to overlays because when refreshed by mRefreshRadarImage
		 * a GroundOverlay is attached to the map and when switching the mode
		 * it must be in the list of overlays in order to be cleared.
		 */
        mOverlays.add(mRadarOverlay);

		UiSettings uiS = mMap.getUiSettings();
		//	uiS.setRotateGesturesEnabled(false);
		uiS.setZoomControlsEnabled(false);		/* restore last used map type */

		mMapClickOnBaloonImageHintEnabled = mSettings.isMapClickOnBaloonImageHintEnabled();

		/* register for radar image bitmap updates, and get updates even if the
		 * bitmap hasn't changed between subsequent updates. We want to receive
		 * all updates in order to save the time when the image was last
		 * downloaded.
		 */
		oActivity.getDataPool().setForceUpdateEvenOnSameBitmap(BitmapType.RADAR, true);
		oActivity.getDataPool().registerBitmapListener(BitmapType.RADAR, this);
		DataPoolCacheUtils dpcu = new DataPoolCacheUtils();
		/* initialize radar bitmap */
		onBitmapChanged(dpcu.loadFromStorage(BitmapType.RADAR,
				oActivity.getApplicationContext()), BitmapType.RADAR, true);

		/* map updates the observation data in ItemizedOverlay when new observations are available
		 *
		 */

		 mSavedCameraPosition = mSettings.getCameraPosition();
		if(mSavedCameraPosition != null && !(mSavedCameraPosition.target.latitude == 0.0 && mSavedCameraPosition.target.longitude == 0.0))
        {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mSavedCameraPosition));
            mSavedCameraPosition = null; /* restore camera just once! */
        }
        else
        {
			CameraPosition cameraPosition = mMap.getCameraPosition();
            if(mOldZoomLevel != cameraPosition.zoom && mZoomChangeListener != null)
                mZoomChangeListener.onZoomLevelChanged(cameraPosition.zoom);
            mOldZoomLevel = cameraPosition.zoom;
        }
		mMap.setOnCameraMoveListener(this);

        if(mRadarAnimation.getState().animationInProgress())
            mRadarAnimation.restore();

        if(mMode.currentMode == MapMode.REPORT && mReportOverlay != null)
        {
            mReportOverlay.onResume();
            OsmerActivity a = (OsmerActivity) getActivity();
            mReportOverlay.update(a.getApplicationContext(), false, a.getLocationService().getCurrentLocation());
        }

		/* when the activity creates us, it passes the initialization stuff through arguments */
		mSetMode(mMode);

	}

    @Override
    public void onMapLoaded()
    {
		 /* When the application is launched for the first time restore last camera position */
        mSavedCameraPosition = mSettings.getCameraPosition();
        if(mSavedCameraPosition  == null || (mSavedCameraPosition.target.latitude == 0.0 && mSavedCameraPosition.target.longitude == 0.0)) /* never saved */
		{
			/* center just once */
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(GeoCoordinates.regionBounds, 20);
            mMap.animateCamera(cu);
        }
    }


    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        OsmerActivity oActivity = (OsmerActivity) getActivity();
        mSettings = new Settings(oActivity.getApplicationContext());

        mMapClickOnBaloonImageHintEnabled = mSettings.isMapClickOnBaloonImageHintEnabled();

		/* register for radar image bitmap updates, and get updates even if the
		 * bitmap hasn't changed between subsequent updates. We want to receive
		 * all updates in order to save the time when the image was last
		 * downloaded.
		 */
//		oActivity.getDataPool().setForceUpdateEvenOnSameBitmap(BitmapType.RADAR, true);
//		oActivity.getDataPool().registerBitmapListener(BitmapType.RADAR, this);
//		DataPoolCacheUtils dpcu = new DataPoolCacheUtils();
//		/* initialize radar bitmap */
//		onBitmapChanged(dpcu.loadFromStorage(BitmapType.RADAR,
//				oActivity.getApplicationContext()), BitmapType.RADAR, true);

		/* map updates the observation data in ItemizedOverlay when new observations are available
		 *
		 */


//		FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.actionNewReport);
//		fab.setOnClickListener((OsmerActivity) this.getActivity());
//		fab.setColor(getResources().getColor(R.color.accent));
//		fab.setDrawable(getResources().getDrawable(R.drawable.ic_menu_edit_fab));

		/* set html text on Radar info text view */
        TextView radarInfoTextView = (TextView) getActivity().findViewById(R.id.radarInfoTextView);
        radarInfoTextView.setText(Html.fromHtml(getResources().getString(R.string.radar_info)));
        radarInfoTextView.setVisibility(View.GONE);

		/* radar animation setup */
        mRadarAnimation = new RadarAnimation(this);
        mRadarAnimation.setRadarSource(""); /* was "slo" for a period, but didn't work */
        mRadarAnimation.registerRadarAnimationListener(this);

		/* restoreState just initializes internal variables. We do not restore animation
		 * here. Animation is restored inside setMode, when the mode is MapMode.RADAR.
		 */
        if(savedInstanceState != null)
            mRadarAnimation.restoreState(savedInstanceState);

        ((ToggleButton) (oActivity.findViewById(R.id.playPauseButton))).setOnClickListener(mRadarAnimation);
        ((ToggleButton) (oActivity.findViewById(R.id.stopButton))).setOnClickListener(mRadarAnimation);
        ((Button)(oActivity.findViewById(R.id.previousButton))).setOnClickListener(mRadarAnimation);
        ((Button)(oActivity.findViewById(R.id.nextButton))).setOnClickListener(mRadarAnimation);

		/* hide all animation controls */
        oActivity.findViewById(R.id.animationButtonsLinearLayout).setVisibility(View.GONE);
        oActivity.findViewById(R.id.radarAnimTime).setVisibility(View.GONE);
        oActivity.findViewById(R.id.stopButton).setVisibility(View.GONE);
        oActivity.findViewById(R.id.playPauseButton).setVisibility(View.GONE);
        oActivity.findViewById(R.id.mapProgressBar).setVisibility(View.GONE);
        oActivity.findViewById(R.id.mapMessageTextView).setVisibility(View.GONE);

    }

	@Override
	public void onCameraIdle()
	{
        CameraPosition cameraPosition = mMap.getCameraPosition();
		if(getActivity() != null && getActivity().findViewById(R.id.mapMessageTextView) != null)
		{
			OAnimatedTextView radarUpdateTimestampText = (OAnimatedTextView) getActivity().findViewById(R.id.mapMessageTextView);
			if(mMapReady && radarUpdateTimestampText.getVisibility() == View.VISIBLE && !radarUpdateTimestampText.animationHasStarted())
				radarUpdateTimestampText.animateHide();
		}

		if(mOnTiltChangeListener != null)
			mOnTiltChangeListener.onTiltChanged(cameraPosition.tilt);
	} 

	public void onStart()
	{
		super.onStart();
	}

	public void onDestroy ()
	{
		mRemoveOverlays();
		if(mRadarOverlay != null)
			mRadarOverlay.finalize(); /* recycles bitmap for GC */
		/* clear webcam data, cancel current task, finalize info window adapter */
		if(mWebcamOverlay != null)
			mWebcamOverlay.clear();

		mRadarAnimation.onDestroy();
		
		/* report packages make use of PostReportAsyncTaskPool to register AsyncTasks that
		 * are launched to make a post (without waiting for a readback) and are not associated
		 * to objects that are destruction aware (they are launched by a dialog and not connected
		 * to a destruction aware object, such as this or OsmerActivity).
		 * So ask the PostReportAsyncTaskPool to cancel all pending tasks.
		 */
		PostReportAsyncTaskPool.Instance().cancelAll();
		
		super.onDestroy();
	}

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		/*
		 * get map asynch, called inside onCreate.
		 */
		getMapAsync(this);
	}
	
	public void onResume()
	{
		super.onResume();
        /* before the introduction of the onMapReadReady callback app version 3.7.0) we used to call restore()
         * on mRadarAnimation. Now mRadarAnimation.restore() is called from onMapReadReady
         * in order to guarantee map is ready before restoring animation state.
         */
	}
	
	public void onPause()
	{
		super.onPause();
		
		/* mMapReady is true if onCameraChanged has been called at least one time.
		 * This ensures that the map camera has been initialized and is not centered
		 * in lat/lang (0.0, 0.0). If mMapReady is true we correctly save an initialized
		 * camera position.
		 */
		if(mMapReady)
		{
			mSettings.saveMapCameraPosition(mMap.getCameraPosition());
			/* save the map type */
			mSettings.setMapType(mMap.getMapType());
		}

		if ( mMap != null && ((ContextCompat.checkSelfPermission(getActivity(),
				Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
				|| (ContextCompat.checkSelfPermission(getActivity(),
				Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED) ) )
			mMap.setMyLocationEnabled(false);
		mRadarAnimation.onPause();
		/* call onPause only if current mode is REPORT. Otherwise, the mode switch
		 * causes the overlay to be clear and the resources to be released.
		 */
		if(mReportOverlay != null && mMode.currentMode == MapMode.REPORT )
			mReportOverlay.onPause();
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		if(mRadarAnimation != null)
			mRadarAnimation.saveState(outState);

		super.onSaveInstanceState(outState); /* modificato x map v2 */
	}


	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onBitmapChanged(Bitmap bmp, BitmapType t, boolean fromCache) 
	{
		if(!fromCache) /* up to date */ 
		{
			mSettings.setRadarImageTimestamp(System.currentTimeMillis());
			if(getActivity() != null)
				getActivity().findViewById(R.id.mapProgressBar).setVisibility(View.GONE);
		}
		mRadarOverlay.updateBitmap(bmp);
		mRefreshRadarImage();
	}

	@Override
	public void onBitmapError(String error, BitmapType t) 
	{
		Toast.makeText(getActivity().getApplicationContext(), getActivity().getResources().getString(R.string.radarDownloadError) 
				+ ":\n" + error, Toast.LENGTH_LONG).show();
	}

	/** This method refreshes the image of the radar on the map.
	 *  NOTE: The map will have a GroundOverlay attached after this call is made.
	 */
	private void mRefreshRadarImage() 
	{
		if(mMode.currentMode == MapMode.RADAR   /* after first camera update */
				&& (mRadarAnimation == null  || !mRadarAnimation.getState().animationInProgress()) )
		{
			long radarTimestampMillis = mSettings.getRadarImageTimestamp();
			long currentTimestampMillis = System.currentTimeMillis();

			if(currentTimestampMillis - radarTimestampMillis < RadarOverlay.ACCEPTABLE_RADAR_DIFF_TIMESTAMP_MILLIS)
			{
				mRadarOverlay.updateColour();
			}
			else
			{
				mRadarOverlay.updateBlackAndWhite();
			}
			mRadarOverlayUpdateListener.onRadarImageUpdated();				
		}
	}


	public GoogleMap getMap()
	{
		return mMap;
	}


	public void centerMap() 
	{
		if(mMapReady)
		{
			CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(GeoCoordinates.regionBounds, 20);
			mMap.animateCamera(cu);
		}
	}

	public void moveTo(double latitude, double longitude)
	{
		if(mMapReady)
		{
			CameraUpdate cu = CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
			mMap.moveCamera(cu);
		}
	}
	
	@Override
	public void onObservationsCacheUpdate(HashMap<String, ObservationData> map, ViewType t) 
	{
		if(mMode == null)
			return;
		if((t == ViewType.DAILY_TABLE && mMode.currentMode == MapMode.DAILY_OBSERVATIONS ) ||
				(t == ViewType.LATEST_TABLE && mMode.currentMode == MapMode.LATEST_OBSERVATIONS))
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
			mObservationsOverlay.setData(map); /* update data but do not refresh */
			/* if the map mode is LATEST or DAILY, update the overlay */
			if(mMode != null && (mMode.currentMode == MapMode.LATEST_OBSERVATIONS || mMode.currentMode == MapMode.DAILY_OBSERVATIONS ) )
			{
				mObservationsOverlay.update(Math.round(mMap.getCameraPosition().zoom));
			}
		}
	}

	public void setMode(MapViewMode m)
	{
		if(mMap != null)
		{
			mSetMode(m);
		}
		else
		{
			mMode = m;
		}
	}
	
	private void mSetMode(MapViewMode m)
	{
		OAnimatedTextView radarTimestampText = (OAnimatedTextView) getActivity().findViewById(R.id.mapMessageTextView);	
	//	Log.e("--->OMapFragment: setMode invoked", "setMode invoked with mode: " +
	//			m.currentMode + ", time (type): " + m.currentType + " mMap " + mMap + " current mode " + mMode.currentMode + "  current type " + mMode.currentType );

		/* show the radar timestamp text anytime the mode is set to RADAR
		 * if (!m.equals(mMode)) then the radar timestamp text is scheduled to be shown
		 * inside if (m.mapMode == MapMode.RADAR) branch below.
		 * Two modes also differ when the isInit flag is different.
		 * All MapViewModes are constructed with isInit = true. The OMapFragment 
		 * constructor sets isInit to false in order to allocate a non null map mode
		 * and not to call setMode.
		 * Secondly, update the radar image in order to draw it black and white if old.
		 */
		if (m.equals(mMode) && m.currentMode == MapMode.RADAR && !mRadarAnimation.getState().animationInProgress())
		{
			radarTimestampText.scheduleShow();
			mRefreshRadarImage();
		}

		if(m.equals(mMode) && !mIsInitialising)
		{
			Log.e("OMapFragment", "mMode equals new mode");
			return;
		}
		if(mIsInitialising)
			mIsInitialising = false;

		/* mMode is still null the first time this method is invoked */
		if(mMode != null && mMode.currentMode == MapMode.RADAR)
			setMeasureEnabled(false);

		mMode = m;

		mUninstallAdaptersAndListeners();

		/* stop animation, be it in progress or not. If not in progress, stop removes
		 * the last animation frame image overlay from the map.
		 */
		if(m.currentMode == MapMode.HIDDEN && mRadarAnimation != null)
		{
			mRemoveOverlays();
			mRadarAnimation.stop();
		}

		if(m.currentMode == MapMode.RADAR) 
		{
			/* update the overlay with a previously set bitmap */
			mRemoveOverlays(); /* this removes any previous overlays... */
			mRefreshRadarImage();
			//mRadarOverlay.showSloRadarMarkers(getActivity().getApplicationContext());
			mOverlays.add(mRadarOverlay);
			radarTimestampText.scheduleShow();
		} 
		else if(m.currentMode == MapMode.WEBCAM) 
		{
			mRemoveOverlays();
			/* create the overlay passing the additional (fixed) webcams. 
			 * The overlay immediately registers for text updates on DataPool.
			 */
			mWebcamOverlay = new WebcamOverlay(R.drawable.camera_web_map, this);
			mWebcamOverlay.initialize((OsmerActivity) getActivity());
			mOverlays.add(mWebcamOverlay);
			radarTimestampText.hide();
			mRadarAnimation.stop();
		} 
		else if(m.currentMode == MapMode.REPORT)
		{
			mRemoveOverlays();
			mRadarAnimation.stop();
			radarTimestampText.hide();
			mReportOverlay = new ReportOverlay(this);
			mOnTiltChangeListener = mReportOverlay;
			mReportOverlay.setOnReportRequestListener((OsmerActivity) getActivity());
			mReportOverlay.setMapTilt(mMap.getCameraPosition().tilt);
			mMap.setOnMapLongClickListener(mReportOverlay);
			mMap.setOnInfoWindowClickListener(mReportOverlay);
			mMap.setOnMarkerDragListener(mReportOverlay);
			mMap.setOnMapClickListener(mReportOverlay);
			mMap.setOnMarkerClickListener(mReportOverlay);
			
			// mMap.setOnCameraChangeListener(mReportOverlay);
			mOverlays.add(mReportOverlay);
		}
		else if(m.currentMode != MapMode.HIDDEN)
		{
			Log.e("mSetMode", " map mode is " + m.currentMode);
			ObservationDrawableIdPicker observationDrawableIdPicker = new ObservationDrawableIdPicker();
			int resId = observationDrawableIdPicker.pick(m.currentType);
			observationDrawableIdPicker = null;
			if(resId > -1)
			{
				mRemoveOverlays();
				mObservationsOverlay = new ObservationsOverlay(resId, m.currentType, 
						m.currentMode, this);
				setOnZoomChangeListener(mObservationsOverlay);
				mOverlays.add(mObservationsOverlay);
				Log.e("mSetMode", " calling  mObservationsOverlay" + m.currentMode);
				mObservationsOverlay.update(Math.round(mMap.getCameraPosition().zoom));
			}
			radarTimestampText.hide();
			mRadarAnimation.stop();
		}
	}

	public void setMapFragmentListener(MapFragmentListener mfl)
	{
		mMapFragmentListener = mfl;
	}
	
	public void setRadarOverlayUpdateListener(RadarOverlayUpdateListener rol)
	{
		mRadarOverlayUpdateListener = rol;
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
		if(en && mMode.currentMode == MapMode.RADAR)
		{
			mMeasureOverlay = new MeasureOverlay(this);
			/* register before show, so that the LocationService immediately invokes the callback
			 * onLocationChange if my location is available.
			 */
			((OsmerActivity)getActivity()).getLocationService().registerLocationServiceUpdateListener(mMeasureOverlay);
			mMeasureOverlay.show();
		}
		else if(mMeasureOverlay != null && mMode.currentMode == MapMode.RADAR)
		{
			/* removes markers, line (if drawn) and saves settings */
			((OsmerActivity)getActivity()).getLocationService().removeLocationServiceUpdateListener(mMeasureOverlay);
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
	public void onWebcamBitmapBytesChanged(byte [] bmpBytes)
	{
		Context ctx = getActivity().getApplicationContext();
		/* save image on cache in order to display it in external viewer */
		LastImageCache saver = new LastImageCache();
		boolean success = saver.save(bmpBytes, ctx);
		if(success)
		{
			if(mMapClickOnBaloonImageHintEnabled)
				Toast.makeText(ctx, R.string.hint_click_on_map_baloon_webcam_image, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onWebcamBitmapChanged(Bitmap bmp) 
	{

	}

	@Override
	public void onWebcamInfoWindowImageClicked() 
	{
		ExternalImageViewerLauncher eivl = new ExternalImageViewerLauncher();
		eivl.startExternalViewer(getActivity());
		new Settings(getActivity().getApplicationContext()).setMapClickOnBaloonImageHintEnabled(false);
		mMapClickOnBaloonImageHintEnabled = false;	
	}

	@Override
	public void onWebcamErrorMessageChanged(String message) 
	{
		message = getActivity().getResources().getString(R.string.error_message) + ": " + message;
		Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onWebcamMessageChanged(int stringId) 
	{
		Toast.makeText(getActivity().getApplicationContext(), getActivity().getResources().getString(stringId), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onWebcamBitmapTaskCanceled(String url) 
	{
		String message = getActivity().getResources().getString(R.string.webcam_download_task_canceled)  + url;
		Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	}

	public ArrayList<WebcamData > mGetAdditionalWebcamsData()
	{
		ArrayList<WebcamData> webcamData = null;
		String additionalWebcamsTxt = "";
		/* get fixed additional webcams list from assets */
		WebcamXMLAssetLoader webcamXMLAssetLoader = new WebcamXMLAssetLoader(this.getActivity().getApplicationContext());
		additionalWebcamsTxt = webcamXMLAssetLoader.getText();
		WebcamXMLDecoder additionalWebcamsDec = new WebcamXMLDecoder();
		webcamData = additionalWebcamsDec.decode(additionalWebcamsTxt);
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
		if(mMap != null)
		{
			mMap.setInfoWindowAdapter(null);
			mMap.setOnMapClickListener(null);
			mMap.setOnMarkerClickListener(null);
			mMap.setOnMarkerDragListener(null);
			mMap.setOnInfoWindowClickListener(null);
			mMap.setOnMapLongClickListener(null);
			mMap.setOnMarkerDragListener(null);
		}
		setOnZoomChangeListener(null);
		mOnTiltChangeListener = null;
	}

	private MeasureOverlay mMeasureOverlay = null;
	private WebcamOverlay mWebcamOverlay = null;

	@Override
	public void onMeasureOverlayErrorMessage(int stringId) 
	{
		Toast.makeText(this.getActivity().getApplicationContext(), stringId, Toast.LENGTH_LONG).show();
	}

	public void startRadarAnimation() 
	{
		mRadarAnimation.start();
	}

	public void stopRadarAnimation()
	{
		mRadarAnimation.stop();
	}
	
	@Override
	public void onRadarAnimationStart() 
	{
		mRadarOverlay.clear();
		OAnimatedTextView radarTimestampTV = (OAnimatedTextView) getActivity().findViewById(R.id.mapMessageTextView);
		radarTimestampTV.hide();
	}

	@Override
	public void onRadarAnimationPause() 
	{

	}

	@Override
	public void onRadarAnimationStop() 
	{
		mRefreshRadarImage();
	}

	@Override
	public void onRadarAnimationRestored() 
	{
		mRadarOverlay.clear();
		OAnimatedTextView radarTimestampTV = (OAnimatedTextView) getActivity().findViewById(R.id.mapMessageTextView);
		radarTimestampTV.hide();
	}

	@Override
	public void onRadarAnimationResumed() 
	{

	}

	@Override
	public void onRadarAnimationProgress(int step, int total) {
		// TODO Auto-generated method stub
		
	}

	/** please invoke when in REPORT mode */
	public void onPostActionResult(boolean error, String message, PostType postType) 
	{
		if(mReportOverlay != null)
			mReportOverlay.onPostActionResult(error, message, postType);
	}

	public void myReportRequestDialogClosed(boolean accepted, LatLng position) {
		if(mMode.currentMode == MapMode.REPORT && mReportOverlay != null)
			mReportOverlay.removeMyPendingReportRequestMarker(position);
			
		
	}

	public void updateReport(boolean force) 
	{
		if(mMode.currentMode == MapMode.REPORT && mReportOverlay != null)
		{
			Log.e("OMapFrag.updateReport", "forcing " + force);
			OsmerActivity a = (OsmerActivity) getActivity();
			mReportOverlay.update(a.getApplicationContext(), force, a.getLocationService().getCurrentLocation());
		}
		else
			Log.e("OMapFragment.updateReport", "mReportOverlay " + mReportOverlay  + ", mMode.currentMode " + mMode.currentMode);
		
	}
	
	public boolean pointTooCloseToMyLocation(Location myLocation, LatLng pointOnMap)
	{
		Location pt = new Location("");
		pt.setLatitude(pointOnMap.latitude);
		pt.setLongitude(pointOnMap.longitude);
		return myLocation.distanceTo(pt) < 500;
	}

	@Override
	public void onCameraMove()
	{
		CameraPosition cameraPosition = mMap.getCameraPosition();
		if(cameraPosition.zoom != mOldZoomLevel)
		{
			if(mZoomChangeListener != null)
				mZoomChangeListener.onZoomLevelChanged(cameraPosition.zoom);
			mOldZoomLevel = cameraPosition.zoom;
		}

	}
}
