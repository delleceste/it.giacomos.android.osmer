/**
 * 
 */
package it.giacomos.android.osmer.widgets.mapview;

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
public class ObservationsItemizedOverlay<Item extends OverlayItem> extends ItemizedOverlay<OverlayItem> 
	implements ZoomChangeListener,
	OnClickListener
{

	public ObservationsItemizedOverlay(Drawable defaultMarker, 
			ObservationType oType, 
			ObservationTime oTime,
			MapView mapView) 
	{
		super(boundCenterBottom(defaultMarker));
		populate();
		setObservationType(oType);
		setObservationTime(oTime);
		
		mMap = new HashMap<String, ObservationData>();
		mMapView = mapView;
		mPaint = new Paint();
		mRes = mMapView.getResources();
		mDensityDpi = mapView.getResources().getDisplayMetrics().densityDpi;
	}

	/**
	 * updates the overlay with the new values in the map
	 * @param map a map containing the City and the 
	 */
	public void update(HashMap<String, ObservationData> map , int level)
	{
		mMap = map;
		/* fixes a bug in maps */
		this.setLastFocusedIndex(-1);
		mOverlayItems.clear();
		LocationNamesMap locMap = new LocationNamesMap();
		Vector<String> locationsForLevel = locMap.locationsForLevel(level);
		for(String location : locationsForLevel)
		{
			GeoPoint gp = locMap.get(location);
			if(gp != null)
			{ 
				String data = mMapView.getResources().getString(R.string.not_available);
				if(map.containsKey(location))
					 data = map.get(location).get(mObservationType);
				if(data == null)
					data = "err";
				OverlayItem overlayitem = null;
				if(mObservationType == ObservationType.SKY)
				{
				   overlayitem = SkyOverlayItemPicker.get(gp, 
						   location, data, mMapView.getResources());
				}
				else if(!data.contains("---"))
				{
					overlayitem = new OverlayItem(gp, location, data);
				}
				if(overlayitem != null)
					addOverlayItem(overlayitem);
			}
		}
	}
	
	public void draw(Canvas canvas, MapView mapView, boolean shadow)
	{
		super.draw(canvas, mapView, false); /* no shadow on icons */
		int yoffset;
		Projection proj = mapView.getProjection();

		/* get the necessary font height to draw the text below the marker */
		Rect r = new Rect();
		/* adjust font according to display resolution */
		if(mDensityDpi == DisplayMetrics.DENSITY_XHIGH)
			mPaint.setTextSize(21);
		else
			mPaint.setTextSize(19);
		mPaint.setAntiAlias(true);
		int textColor= Color.BLACK;
		
		RectF rectf = new RectF();
		for(OverlayItem item : mOverlayItems)
		{
			String text = item.getSnippet();
			Point pt = proj.toPixels(item.getPoint(), null);
			mPaint.setARGB(255, 137, 218, 248);
			canvas.drawCircle(pt.x, pt.y, 3, mPaint);
			if(text != null && text != "")
			{
				mPaint.getTextBounds(text, 0, text.length(), r);
				yoffset = r.height();
				mPaint.setColor(Color.WHITE);	
				mPaint.setAlpha(190);
				rectf.left = pt.x - 4;
				rectf.top = pt.y - 4;
				rectf.right = pt.x + r.width() + 4;
				rectf.bottom =rectf.top + r.height() + 8;
				canvas.drawRoundRect(rectf, 6, 6, mPaint);
				mPaint.setColor(textColor);
				canvas.drawText(text, pt.x, pt.y + yoffset, mPaint);
			}
		}
	}

	@Override
	protected boolean onTap(int index) 
	{
	  OverlayItem item = mOverlayItems.get(index);
	  String location = item.getTitle();
	  if(mMap.containsKey(location))
	  {
		  /* BaloonOnMap creates a baloon and shows it on the map
		   * false: not a webcam baloon, inflates a different xml 
		   */
		  new BaloonOnMap(mMapView, mMap.get(location), item.getPoint(), false);
		  
		  /* install button close click listener */
		  MapBaloon baloon = (MapBaloon) mMapView.findViewById(R.id.mapbaloon);
		  baloon.findViewById(R.id.baloon_close_button).setOnClickListener(this);
		  /* animate the map to center on the item */
		  mMapView.getController().animateTo(item.getPoint());
		  /* disable marker hints from now on */
		  Settings s = new Settings(mMapView.getContext());
		  s.setMapMarkerHintEnabled(false);
	  }
	  return true;
	}
	
	@Override
	public void onClick(View view) 
	{
		if(view.getId() == R.id.baloon_close_button)
		{
			MapBaloon baloon = (MapBaloon) mMapView.findViewById(R.id.mapbaloon);
			if(baloon != null)
			{
				/* remove baloon */
				mMapView.removeView(baloon);
				/* restore previous position of the map */
				mMapView.getController().animateTo(baloon.getGeoPoint());
				baloon = null;
			}
		}
	}
	
	@Override
	public void onZoomLevelChanged(int level) {
		this.update(mMap, level);
	}
	
	public void addOverlayItem(OverlayItem overlay) {
	    mOverlayItems.add(overlay);
	    populate();
	}
	
	@Override
	protected OverlayItem createItem(int i) 
	{
		return mOverlayItems.get(i);
	}

	@Override
	public int size() 
	{
		return mOverlayItems.size();
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

	private ObservationType mObservationType;
	private ObservationTime mObservationTime;	
	private ArrayList<OverlayItem> mOverlayItems = new ArrayList<OverlayItem>();
	private HashMap<String, ObservationData> mMap;
	private Paint mPaint;
	MapView mMapView;
	Resources mRes;
	private int mDensityDpi;
}
