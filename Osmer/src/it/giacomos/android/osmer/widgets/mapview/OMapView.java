package it.giacomos.android.osmer.widgets.mapview;

import java.util.HashMap;
import java.util.List;

import it.giacomos.android.osmer.OMapViewEventListener;
import it.giacomos.android.osmer.locationUtils.GeoCoordinates;
import it.giacomos.android.osmer.observations.ObservationData;
import it.giacomos.android.osmer.observations.ObservationDrawableIdPicker;
import it.giacomos.android.osmer.observations.ObservationTime;
import it.giacomos.android.osmer.observations.ObservationType;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;

import com.google.android.maps.MapController;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.os.Bundle;
import android.os.Parcelable;

public class OMapView extends MapView
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

	public void updateObservations(HashMap<String, ObservationData> map)
	{
		Log.e("OMapView", "updateObservations: zoom level is " + this.getZoomLevel() + " map size is " + map.size());
		mObservationsItemizedOverlay.update(map, getZoomLevel());
		this.invalidate(); /* redraw */
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
			Log.e("OmapView onRestoreInstanceState", " Enableing measure ? " + b.getBoolean("measureEnabled"));
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
			Log.e("OmapView", "EQUALS");
			return;
		}
		else
			Log.e("OmapView", "DONT EQUALS");
		
		mMode = m;
		new BaloonOffMap(this);
		List<Overlay> overlays = getOverlays();
		/* remove our overlays except MyLocationOverlay and MapButtonsOverlay */
		while(overlays.size() > 1)
		{
			Log.e("OMapView", "removing overlay " + overlays.get(overlays.size() - 1).getClass().getName());
			overlays.remove(overlays.size() - 1);
		}
		switch(m.currentType)
		{
		case RADAR:
			Log.e("OMapView", "RADAR OVERLAY CERATED");
			mRadarOverlay = new RadarOverlay();
			overlays.add(mRadarOverlay);
			setOnZoomChangeListener(null);
			break;
		case SAT:
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

	private MyLocationOverlay mMyLocationOverlay = null;
	private RadarOverlay mRadarOverlay = null;
	private CircleOverlay mCircleOverlay = null;
	private ObservationsItemizedOverlay<OverlayItem> mObservationsItemizedOverlay = null;

	private MapViewMode mMode = null;
	private int mOldZoomLevel;

	private ZoomChangeListener mZoomChangeListener;
	private OMapViewEventListener mMapViewEventListener;
}
