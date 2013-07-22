package it.giacomos.android.osmer.widgets.mapview;

import it.giacomos.android.osmer.BitmapType;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.downloadManager.state.BitmapListener;
import it.giacomos.android.osmer.downloadManager.state.BitmapTask;
import it.giacomos.android.osmer.locationUtils.GeoCoordinates;
import it.giacomos.android.osmer.preferences.Settings;
import it.giacomos.android.osmer.webcams.ExternalImageViewerLauncher;
import it.giacomos.android.osmer.webcams.LastImageCache;
import it.giacomos.android.osmer.webcams.WebcamData;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.Projection;

public class WebcamItemizedOverlay<Item extends OverlayItem> extends ItemizedOverlay<OverlayItem> 
implements ZoomChangeListener, BitmapListener, OnClickListener
{

	public WebcamItemizedOverlay(Drawable defaultMarker, OMapFragment map) {
		super(boundCenterBottom(defaultMarker));
		populate();
//		mMap = map;
//		mWebcams = new ArrayList<WebcamData>();
//		mPaint = new Paint();
//		mDensityDpi = map.getResources().getDisplayMetrics().densityDpi;
//		Settings s = new Settings(mMap.getContext().getApplicationContext());
//		mMapClickOnBaloonImageHintEnabled = s.isMapClickOnBaloonImageHintEnabled();
//		mZoomLevel = map.getZoomLevel();
//		mCurrentBitmapTask = null;
	}

	/**
	 * updates the overlay with the new values in the map
	 * @param map a map containing the City and the 
	 */
	public boolean update(ArrayList<WebcamData> webcams)
	{
		/* fixes a bug in maps */
		this.setLastFocusedIndex(-1);
		boolean needsRedraw = false;
		for(WebcamData wd : webcams)
		{
			if(!webcamInList(wd))
			{
				GeoPoint gp = wd.geoPoint;
				if(gp != null)
				{ 
					OverlayItem overlayitem = new OverlayItem(gp, wd.location, wd.text);
//					Log.i("WebcamItemizedOverlay: update()", "adding overlay item " + wd.location + " webcams are " + mWebcams.size());
					mOverlayItems.add(overlayitem);
					needsRedraw = true;
					populate();
					mWebcams.add(wd);
				}
			}
		}
		return needsRedraw;
	}

	@Override
	protected boolean onTap(int index) 
	{
//		Resources res = mMap.getResources();
//		OverlayItem item = mOverlayItems.get(index);
//		String location = item.getTitle();
//		WebcamData wd = getDataByGeoPoint(item.getPoint());
//		String message = "";
//		if(wd != null)
//		{
//			/* see method documentation */
//			cancelCurrentWebcamTask();
//			
//			mCurrentBitmapTask = new BitmapTask(this, BitmapType.WEBCAM);
//			try 
//			{
//				URL webcamUrl = new URL(wd.url);
//				mCurrentBitmapTask.parallelExecute(webcamUrl);
//			}
//			catch (MalformedURLException e) 
//			{
//				// TODO Auto-generated catch block
//				message = res.getString(R.string.error_message) + ": " + e.getLocalizedMessage();
//				Toast.makeText(mMap.getContext().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
//			}
//
//			/* creates a webcam baloon and adds it to the map */
//			new BaloonOnMap(mMap, location, wd.text, R.drawable.webcam_download, item.getPoint(), true);
//			/* install button close click listener */
//			MapBaloon baloon = (MapBaloon) mMap.findViewById(R.id.mapbaloon);
//			baloon.findViewById(R.id.baloon_close_button).setOnClickListener(this);
//			
//			Projection projection = mMap.getProjection();
//			/* get the width/height used to create the baloon.
//			 * The map view has a method that returns the values.
//			 */
//			int baloonWidth = mMap.suggestedBaloonWidth(MapBaloon.Type.WEBCAM);
//			int baloonHeight = mMap.suggestedBaloonHeight(MapBaloon.Type.WEBCAM);
//			Point out = null;
//			out = projection.toPixels(item.getPoint(), out);
//			int dx, dy;
//			int markerX = out.x;
//			int markerY = out.y;
//			final int margin = 10;
//				
//			dx = markerX - baloonWidth / 2 - (mMap.getWidth() - baloonWidth) / 2;
//			dy = markerY - baloonHeight - margin;
//
//			mMap.getController().scrollBy(dx, dy);
//		}
//		return true;
		return super.onTap(index); /* map v2 */
	}

	@Override
	public void onBitmapUpdate(Bitmap bmp, BitmapType bt, String errorMessage, AsyncTask<URL, Integer, Bitmap> unusedTaskParameter) 
	{
//		Context ctx = mMap.getContext().getApplicationContext();
//		MapBaloon baloon = (MapBaloon) mMap.findViewById(R.id.mapbaloon);
//		if(baloon != null && bmp == null && !errorMessage.isEmpty())
//		{
//			Toast.makeText(ctx, ctx.getResources().getString(R.string.error_message)
//					+ "\n" + errorMessage, Toast.LENGTH_LONG).show();
//
//		}
//		else if(baloon != null && bt == BitmapType.WEBCAM && bmp != null)
//		{
//			if(!errorMessage.isEmpty())
//				Toast.makeText(ctx.getApplicationContext(), ctx.getResources().getString(R.string.error_message) + "\n" + errorMessage, Toast.LENGTH_LONG).show();
//			else
//			{
//				baloon.setIcon(new BitmapDrawable(bmp));
//				/* save image on cache in order to display it in external viewer */
//				LastImageCache saver = new LastImageCache();
//				boolean success = saver.save(bmp, ctx);
//				if(success)
//				{
//					baloon.findViewById(R.id.baloon_icon).setOnClickListener(this);
//					if(mMapClickOnBaloonImageHintEnabled)
//						Toast.makeText(ctx, R.string.hint_click_on_map_baloon_webcam_image, Toast.LENGTH_LONG).show();
//				}
//				else
//					baloon.setOnClickListener(null); /* no clicks */
//			}
//		}
	}

	@Override
	public void onClick(View view) 
	{
//		if(view.getId() == R.id.baloon_close_button)
//		{
//			MapBaloon baloon = (MapBaloon) mMap.findViewById(R.id.mapbaloon);
//			if(baloon != null)
//			{
//				cancelCurrentWebcamTask();
//				/* remove baloon */
//				mMap.removeView(baloon);
//				/* restore previous position of the map */
//				mMap.getController().animateTo(baloon.getGeoPoint());
//				baloon = null;
//			}
//		}
//		else if(view.getId() == R.id.baloon_icon)
//		{
//			ExternalImageViewerLauncher eivl = new ExternalImageViewerLauncher();
//			eivl.startExternalViewer(mMap.getContext());
//			Settings s = new Settings(mMap.getContext().getApplicationContext());
//			s.setMapClickOnBaloonImageHintEnabled(false);
//			mMapClickOnBaloonImageHintEnabled = false;
//		}
	}
	
	public void draw(Canvas canvas, MapView mapView, boolean shadow)
	{
		super.draw(canvas, mapView, false); /* no shadow on icons */
		if(mZoomLevel > 9)
		{
			int yoffset;
			Projection proj = mapView.getProjection();

			/* get the necessary font height to draw the text below the marker */
			Rect r = new Rect();
			/* adjust font according to display resolution */
			if(mDensityDpi == DisplayMetrics.DENSITY_XHIGH)
				mPaint.setTextSize(16);
			else
				mPaint.setTextSize(14);
			mPaint.setAntiAlias(true);
			int textColor= Color.BLACK;

			RectF rectf = new RectF();
			for(OverlayItem item : mOverlayItems)
			{
				String text = item.getTitle();
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
	}
	
	/* Attempts to cancel execution of this task. This attempt will fail if the task 
	 * has already completed, already been cancelled, or could not be cancelled for 
	 * some other reason. If successful, and this task has not started when cancel 
	 * is called, this task should never run. If the task has already started, then 
	 * the mayInterruptIfRunning parameter determines 
	 * whether the thread executing this task should be interrupted in an attempt to
	 *  stop the task.
	 *  Returns
     * false if the task could not be cancelled, typically because it has already completed normally; 
	 * true otherwise
	 */
	public void cancelCurrentWebcamTask()
	{
		if(mCurrentBitmapTask != null  && mCurrentBitmapTask.getStatus() == AsyncTask.Status.RUNNING)
			mCurrentBitmapTask.cancel(false);
	}

	@Override
	public void onZoomLevelChanged(int level) 
	{
		mZoomLevel = level;
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

	protected WebcamData getDataByGeoPoint(GeoPoint gp)
	{
		for(WebcamData wd : mWebcams)
		{
			if(wd.geoPoint == gp)
				return wd;
		}
		return null;
	}

	boolean webcamInList(WebcamData otherWebcamData)
	{
		for(WebcamData wd : mWebcams)
			if(wd.equals(otherWebcamData))
				return true;
		return false;
	}

	private OMapFragment mMap;
	private ArrayList<OverlayItem> mOverlayItems = new ArrayList<OverlayItem>();
	private ArrayList<WebcamData> mWebcams;
	private int mDensityDpi;
	private Paint mPaint;
	private int mZoomLevel;
	private BitmapTask mCurrentBitmapTask;
	/* stores settings value locally in order not to query Settings every time */
	private boolean mMapClickOnBaloonImageHintEnabled;
}
