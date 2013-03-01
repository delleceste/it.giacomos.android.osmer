package it.giacomos.android.osmer.widgets.mapview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.giacomos.android.osmer.OMapViewEventListener;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.StringType;
import it.giacomos.android.osmer.locationUtils.GeoCoordinates;
import it.giacomos.android.osmer.observations.ObservationData;
import it.giacomos.android.osmer.observations.ObservationDrawableIdPicker;
import it.giacomos.android.osmer.observations.ObservationTime;
import it.giacomos.android.osmer.observations.ObservationType;
import it.giacomos.android.osmer.observations.ObservationsCacheUpdateListener;
import it.giacomos.android.osmer.webcams.WebcamData;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import com.google.android.maps.MapController;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.os.Bundle;
import android.os.Parcelable;

public class OMapView extends MapView implements ObservationsCacheUpdateListener
{
	public final int minLatitude = GeoCoordinates.bottomRight.getLatitudeE6();
	public final int maxLatitude = GeoCoordinates.topLeft.getLatitudeE6();
	public final int minLongitude = GeoCoordinates.topLeft.getLongitudeE6();
	public final int maxLongitude = GeoCoordinates.bottomRight.getLongitudeE6();

	public OMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub

		setBuiltInZoomControls(true);
		/* add my location overlay */
		mMyLocationOverlay = new MyLocationOverlay(context, this);
		getOverlays().add(mMyLocationOverlay);
		
		centerMap();
		mMode = null;
		setMode(new MapViewMode(ObservationType.RADAR, ObservationTime.DAILY));
		mOldZoomLevel = this.getZoomLevel();
	}

	public void onResume()
	{
		mMyLocationOverlay.enableMyLocation();
	}

	public void onPause()
	{
		mMyLocationOverlay.disableMyLocation();
	}

	public void centerMap()
	{		
		MapController mapController = getController();
		mapController.animateTo(GeoCoordinates.center);
		mapController.setCenter(GeoCoordinates.center);
		setSpan();
	}

	public void setSpan()
	{
		getController().zoomToSpan((maxLatitude - minLatitude)/2, (maxLongitude - minLongitude)/2);
	}

	public void setRadarImage(Bitmap bmp) 
	{
		mRadarOverlay.updateBitmap(bmp);
		/* forces android.View to call onDraw(canvas) at some time in the future */
		this.invalidate();
	}

	public void updateWebcamList(ArrayList<WebcamData> webcams)
	{
		if(mMode.currentMode == ObservationTime.WEBCAM && 
				mMode.currentType == ObservationType.WEBCAM &&
						mWebcamItemizedOverlay != null)
		{
			Log.i("OMapView:: updateWebcamList", "updating overlay " + webcams.size() + " new items");
			if(mWebcamItemizedOverlay.update(webcams))
				this.invalidate();
		}
	}
	
	@Override
	public void onObservationsCacheUpdate(HashMap<String, ObservationData> map, StringType t) 
	{
		if((t == StringType.DAILY_TABLE && mMode.currentMode == ObservationTime.DAILY ) ||
				(t == StringType.LATEST_TABLE && mMode.currentMode == ObservationTime.LATEST))
		{
			this.updateObservations(map);
			/* forces android.View to call onDraw(canvas) at some time in the future */
			this.invalidate();
		}
	}
	
	public void updateObservations(HashMap<String, ObservationData> map)
	{
		if(mObservationsItemizedOverlay != null)
		{
			mObservationsItemizedOverlay.update(map, getZoomLevel());
			this.invalidate(); /* redraw */
		}
	}

	public MyLocationOverlay getMyLocationOverlay()
	{
		return mMyLocationOverlay;
	}

	public Parcelable onSaveInstanceState()
	{
		Parcelable p = super.onSaveInstanceState();
		Bundle bundle = new Bundle();
		bundle.putParcelable("OMapViewState", p);
		bundle.putParcelable("RadarBitmap", mRadarOverlay.getBitmap());
		bundle.putBoolean("measureEnabled", mCircleOverlay != null);
		if(mCircleOverlay != null)
			mCircleOverlay.saveState(bundle);
		return bundle;
	}

	public void onRestoreInstanceState (Parcelable state)
	{
		Bundle b = (Bundle) state;
		if(b.containsKey("RadarBitmap"))
		{
			Bitmap bmp = b.getParcelable("RadarBitmap");
			mRadarOverlay.updateBitmap(bmp);
		}
		if(b.containsKey("measureEnabled"))
		{
			setMeasureEnabled(b.getBoolean("measureEnabled"));
			mMapViewEventListener.onMeasureEnabled(b.getBoolean("measureEnabled"));
			if(mCircleOverlay != null)
				mCircleOverlay.restoreState(state);
		}
		
		super.onRestoreInstanceState(b.getParcelable("OMapViewState"));
	}

	public void setMode(MapViewMode m)
	{
		if(m.equals(mMode))
		{
			Log.i("setMode", "new mode equals");
			return;
		}
		
		mMode = m;
		new BaloonOffMap(this);
		List<Overlay> overlays = getOverlays();
		/* remove our overlays except MyLocationOverlay and MapButtonsOverlay */
		Log.i("OMApView ", "switching mode overlays " + overlays.size());
		while(overlays.size() > 1)
		{
			overlays.remove(overlays.size() - 1);
		}
		
		switch(m.currentType)
		{
		case RADAR:
			mRadarOverlay = new RadarOverlay();
			overlays.add(mRadarOverlay);
			setOnZoomChangeListener(null);
			break;
		case SAT:
			break;
		case WEBCAM:
			Drawable webcamIcon = getResources().getDrawable(R.drawable.camera_web_map);
			if(webcamIcon != null)
			{
				Log.i("OMapView", "Creating WebcamItemizedOverlay");
				mWebcamItemizedOverlay = new WebcamItemizedOverlay(webcamIcon, this);
				setOnZoomChangeListener(mWebcamItemizedOverlay);
				overlays.add(mWebcamItemizedOverlay);
			}
			break;
		default:
			int resId = ObservationDrawableIdPicker.pick(m.currentType);
			if(resId > -1)
			{
				Drawable drawable = null;
				drawable = getResources().getDrawable(resId);
				if(drawable != null)
				{
					mObservationsItemizedOverlay = new ObservationsItemizedOverlay<OverlayItem>(drawable, 
							m.currentType, 
							m.currentMode,
							this);
					setOnZoomChangeListener(mObservationsItemizedOverlay);
					overlays.add(mObservationsItemizedOverlay);
				}
			}
			break;
		}
		/* redraw the map view */
		this.invalidate();
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
		setSatellite(satEnabled);
	}
	
	public void setMeasureEnabled(boolean en)
	{
		if(en)
		{
			mCircleOverlay = new CircleOverlay();
			getOverlays().add(mCircleOverlay);
		}
		else
		{
			getOverlays().remove(mCircleOverlay);
			mCircleOverlay = null;
		}
		this.invalidate();
	}
	
	public boolean baloonVisible()
	{
		MapBaloon baloon = (MapBaloon) findViewById(R.id.mapbaloon);
		return (baloon != null && baloon.getVisibility() == View.VISIBLE);
	}
	
	public void removeBaloon()
	{
		MapBaloon baloon = (MapBaloon) findViewById(R.id.mapbaloon);
		if(baloon != null)
		{
			/* remove baloon */
			removeView(baloon);
			/* restore previous position of the map */
			getController().animateTo(baloon.getGeoPoint());
			baloon = null;
		}
	}
	
	public void setMapViewEventListener(OMapViewEventListener l)
	{
		mMapViewEventListener = l;
	}
	
	public void dispatchDraw(Canvas canvas)
	{
		super.dispatchDraw(canvas);
		int zoomLevel = getZoomLevel();
		if(mOldZoomLevel != zoomLevel)
		{
			if(mZoomChangeListener != null)
				mZoomChangeListener.onZoomLevelChanged(zoomLevel);
			mOldZoomLevel = zoomLevel;
		}
	}

	public int suggestedBaloonWidth(MapBaloon.Type t)
	{
		int  wpix = 170; /* observations */
		Resources r = getResources();
		int orientation = r.getConfiguration().orientation;
		if(orientation == Configuration.ORIENTATION_PORTRAIT && t == MapBaloon.Type.WEBCAM)
			wpix = 310;
		else if(orientation == Configuration.ORIENTATION_LANDSCAPE && t == MapBaloon.Type.WEBCAM)
			wpix = 270;
		
    	return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, wpix, r.getDisplayMetrics());
	}
	
	public int suggestedBaloonHeight(MapBaloon.Type t)
	{
		int  hpix = 100; /* observations */
		Resources r = getResources();
		int orientation = r.getConfiguration().orientation;
		if(orientation == Configuration.ORIENTATION_PORTRAIT && t == MapBaloon.Type.WEBCAM)
			hpix = 280;
		else if(orientation == Configuration.ORIENTATION_LANDSCAPE && t == MapBaloon.Type.WEBCAM)
			hpix = 250;
		
    	return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, hpix, r.getDisplayMetrics());
	}
	
	
	private MyLocationOverlay mMyLocationOverlay = null;
	private RadarOverlay mRadarOverlay = null;
	private CircleOverlay mCircleOverlay = null;
	private ObservationsItemizedOverlay<OverlayItem> mObservationsItemizedOverlay = null;
	private WebcamItemizedOverlay<OverlayItem> mWebcamItemizedOverlay = null;
	
	private MapViewMode mMode = null;
	private int mOldZoomLevel;

	private ZoomChangeListener mZoomChangeListener;
	private OMapViewEventListener mMapViewEventListener;
}
