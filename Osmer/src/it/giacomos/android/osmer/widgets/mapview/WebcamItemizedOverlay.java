package it.giacomos.android.osmer.widgets.mapview;

import it.giacomos.android.osmer.BitmapType;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.downloadManager.state.BitmapListener;
import it.giacomos.android.osmer.downloadManager.state.BitmapTask;
import it.giacomos.android.osmer.webcams.WebcamData;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.GeoPoint;

public class WebcamItemizedOverlay<Item extends OverlayItem> extends ItemizedOverlay<OverlayItem> 
implements ZoomChangeListener, BitmapListener
{

	public WebcamItemizedOverlay(Drawable defaultMarker, OMapView map) {
		super(boundCenterBottom(defaultMarker));
		populate();
		mMap = map;
		mWebcams = new ArrayList<WebcamData>();
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
					Log.i("WebcamItemizedOverlay: update()", "adding overlkay item " + wd.location + " webcams are " + mWebcams.size());
					mOverlayItems.add(overlayitem);
					needsRedraw = true;
					populate();
					mWebcams.add(wd);
				}
			}
			else
				Log.i("WebcamItemizedOverlay: update()", "Webcam " + wd.location + ": " + wd.url + " already in list");
		}
		return needsRedraw;
	}
	
	@Override
	protected boolean onTap(int index) 
	{
	  Resources res = mMap.getResources();
	  OverlayItem item = mOverlayItems.get(index);
	  String location = item.getTitle();
	  WebcamData wd = getDataByGeoPoint(item.getPoint());
	  String message = res.getString(R.string.webcam_getting_image);
	  if(wd != null)
	  {
		  BitmapTask webcamImageTask = new BitmapTask(this, BitmapType.WEBCAM);
		  try 
		  {
			URL webcamUrl = new URL(wd.url);
			webcamImageTask.execute(webcamUrl);
		  }
		  catch (MalformedURLException e) 
		  {
			// TODO Auto-generated catch block
			  message = res.getString(R.string.error_message) + ": " + e.getLocalizedMessage();
		  }
		  Toast t = Toast.makeText(mMap.getContext(), message, Toast.LENGTH_SHORT);
		  t.setGravity(Gravity.CENTER|Gravity.BOTTOM, 0, 0);
		  t.show();
		  new BaloonOnMap(mMap, location, wd.text, R.drawable.camera_web_map, item.getPoint());
		  mMap.getController().animateTo(item.getPoint());
	  }
	  return true;
	}

	@Override
	public void onBitmapUpdate(Bitmap bmp, BitmapType bt, String errorMessage) 
	{
		if(bt == BitmapType.WEBCAM)
		{
			Log.i("WebcamItemizedOverlay: onBitmapUpdate", "received BITMAP");
			if(!errorMessage.isEmpty())
				Toast.makeText(mMap.getContext(), mMap.getResources().getString(R.string.error_message) + "\n" + errorMessage, Toast.LENGTH_LONG).show();
			else
			{
				Log.i("WebcamItemizedOverlay: onBitmapUpdate", " There are " + mMap.getChildCount() + " children");
				for(int i=0; i < mMap.getChildCount(); ++i) 
				{
				    View nextChild = mMap.getChildAt(i);
				    Log.i("WebcamItemizedOverlay: onBitmapUpdate", "class of child is " + nextChild.getClass());
				    if(nextChild instanceof MapBaloon)
				    {
				    	MapBaloon baloon = (MapBaloon) nextChild;
				    	if(baloon != null)
				    	{
				    		Log.i("WebcamItemizedOverlay: onBitmapUpdate", "setting iconn!!");
				    		baloon.setIcon(new BitmapDrawable(bmp));
				    	}
				    	else
				    		Log.e("WebcamItemizedOverlay: onBitmapUpdate", "cannot cast to baloon");
				    	break;
				    }
				}
			}
		}
		
	}

	@Override
	public void onZoomLevelChanged(int level) 
	{
		
		
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
	
	private OMapView mMap;
	private ArrayList<OverlayItem> mOverlayItems = new ArrayList<OverlayItem>();
	private ArrayList<WebcamData> mWebcams;
}
