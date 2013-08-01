package it.giacomos.android.meteofvg2.widgets.map;

import it.giacomos.android.meteofvg2.BitmapType;
import it.giacomos.android.meteofvg2.R;
import it.giacomos.android.meteofvg2.downloadManager.state.BitmapListener;
import it.giacomos.android.meteofvg2.downloadManager.state.BitmapTask;
import it.giacomos.android.meteofvg2.preferences.Settings;
import it.giacomos.android.meteofvg2.webcams.ExternalImageViewerLauncher;
import it.giacomos.android.meteofvg2.webcams.WebcamData;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class WebcamOverlay implements 
	BitmapListener, 
	OOverlayInterface,
	OnMarkerClickListener,
	OnInfoWindowClickListener,
	OnMapClickListener
{

	public WebcamOverlay(int markerResId, OMapFragment mapFrag) 
	{
		mMarkerResId = markerResId;
		mMap = mapFrag.getMap();
		mMarkerWebcamHash = new HashMap<Marker, WebcamData>();
		
		mSettings = new Settings(mapFrag.getActivity().getApplicationContext());
		mCurrentBitmapTask = null;
		mCustomMarkerBitmapFactory = new CustomMarkerBitmapFactory(mapFrag.getResources());
		mCustomMarkerBitmapFactory.setTextWidthScaleFactor(2.5f);
		mCustomMarkerBitmapFactory.setAlphaTextContainer(100);
		mCustomMarkerBitmapFactory.setInitialFontSize(mSettings.mapWebcamMarkerFontSize());
		Log.e("WebcamOverlay", "initial font size of marker " + mSettings.mapWebcamMarkerFontSize());
		mWebcamOverlayChangeListener = mapFrag;
		mInfoWindowAdapter = new WebcamBaloonInfoWindowAdapter(mapFrag.getActivity());
		/* adapter and listeners */
		mMap.setInfoWindowAdapter(mInfoWindowAdapter);
		mMap.setOnMarkerClickListener(this);
		mMap.setOnMapClickListener(this);
		mMap.setOnInfoWindowClickListener(this);
		mCurrentlySelectedMarker = null;
		mWaitBitmap = BitmapFactory.decodeResource(mapFrag.getResources(), R.drawable.webcam_download);
		mWaitString = mapFrag.getResources().getString(R.string.webcam_downloading);
	}

	/**
	 * updates the overlay with the new values in the map
	 * @param map a map containing the City and the 
	 */
	public boolean update(ArrayList<WebcamData> webcams, Resources res)
	{
		/* fixes a bug in maps */
		boolean needsRedraw = false;
		Bitmap icon = BitmapFactory.decodeResource(res, mMarkerResId);
		for(WebcamData wd : webcams)
		{
			if(!webcamInList(wd))
			{
				LatLng ll = wd.latLng;
				if(ll != null)
				{ 
					MarkerOptions mo = new MarkerOptions();
					mo.title(wd.location).snippet(wd.text).position(wd.latLng);
					BitmapDescriptor bmpDescriptor = mCustomMarkerBitmapFactory.getIcon(icon, wd.location);
					mo.icon(bmpDescriptor);
					Marker m = mMap.addMarker(mo);
					needsRedraw = true;
					mMarkerWebcamHash.put(m, wd);
				}
			}
		}
		/* save in settings the optimal font size in order for CustomMarkerBitmapFactory to quickly 
		 * obtain it without calculating it again.
		 */
		mSettings.setMapWebcamMarkerFontSize(mCustomMarkerBitmapFactory.getCachedFontSize());
		return needsRedraw;
	}

	@Override
	public boolean onMarkerClick(Marker marker) 
	{
		mCurrentlySelectedMarker = marker;
		WebcamData wd = mMarkerWebcamHash.get(marker);
		cancelCurrentWebcamTask();
		mCurrentBitmapTask = new BitmapTask(this, BitmapType.WEBCAM);
		Log.e("onMarkerClick", "getting image for" + wd.location);
		try 
		{
			URL webcamUrl = new URL(wd.url);
			mCurrentBitmapTask.parallelExecute(webcamUrl);
			mInfoWindowAdapter.setData(wd.location + " - " + mWaitString, mWaitBitmap, false);
		}
		catch (MalformedURLException e) 
		{
			mWebcamOverlayChangeListener.onErrorMessageChanged(e.getLocalizedMessage());
			mCurrentlySelectedMarker = null;
		}
		/* do not show info window until the image has been retrieved */
		return false;
	}

	@Override
	public void onBitmapUpdate(Bitmap bmp, BitmapType bt, String errorMessage, AsyncTask<URL, Integer, Bitmap> unusedTaskParameter) 
	{
		if(bmp == null && !errorMessage.isEmpty())
		{
			mWebcamOverlayChangeListener.onErrorMessageChanged(errorMessage);
		}
		else if(bt == BitmapType.WEBCAM && bmp != null)
		{
			if(!errorMessage.isEmpty())
				mWebcamOverlayChangeListener.onErrorMessageChanged(errorMessage);
			if(mCurrentlySelectedMarker != null)
			{
				if(!errorMessage.isEmpty())
					mInfoWindowAdapter.setData(errorMessage, null, false);
				else
				{
					mInfoWindowAdapter.setData(mMarkerWebcamHash.get(mCurrentlySelectedMarker).text, bmp, true);
					/* notify map fragment that the image has changed */
					mWebcamOverlayChangeListener.onBitmapChanged(bmp);
						
				}
				mCurrentlySelectedMarker.showInfoWindow();
			}
		}
	}


	@Override
	public void onInfoWindowClick(Marker marker) 
	{
		if(mInfoWindowAdapter.isImageValid())
			mWebcamOverlayChangeListener.onInfoWindowImageClicked();
		else
			mWebcamOverlayChangeListener.onMessageChanged(R.string.webcam_wait_for_image);
	}


	@Override
	public void onMapClick(LatLng arg0) 
	{
		Log.e("onMapClick", " cancelling task ");
		cancelCurrentWebcamTask();
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
		{
			Log.e("cancelCurrentWebcamTask", "cancelling task");
			mWebcamOverlayChangeListener.onBitmapTaskCanceled(mCurrentBitmapTask.getUrl());
			mCurrentBitmapTask.cancel(false);
		}
		else
			Log.e("cancelCurrentWebcamTask", "NOT cancelling task (not runnig)");
	}

	@Override
	public void clear() 
	{
		Log.e("WebcamOverlay: clear", "clearing webcam data, cancelling current task, finalizing info window adapter");
		/* important! cancel bitmap task if running */
		cancelCurrentWebcamTask();
		for(Map.Entry<Marker, WebcamData> entrySet : mMarkerWebcamHash.entrySet())
			entrySet.getKey().remove();
		mMarkerWebcamHash.clear();
		/* recycle bitmap and unbind drawable */
		mInfoWindowAdapter.finalize();
	}

	protected WebcamData getDataByLatLng(LatLng ll)
	{
		for(WebcamData wd : mMarkerWebcamHash.values())
		{
			if(wd.latLng == ll)
				return wd;
		}
		return null;
	}

	boolean webcamInList(WebcamData otherWebcamData)
	{
		for(WebcamData wd : mMarkerWebcamHash.values())
			if(wd.equals(otherWebcamData))
				return true;
		return false;
	}

	@Override
	public int type() 
	{
		
		return 0;
	}

	@Override
	public void hideInfoWindow() 
	{
		for(Map.Entry<Marker, WebcamData> entrySet : mMarkerWebcamHash.entrySet())
			if(entrySet.getKey().isInfoWindowShown())
				entrySet.getKey().hideInfoWindow();
	}

	@Override
	public boolean isInfoWindowVisible() 
	{
		for(Map.Entry<Marker, WebcamData> entrySet : mMarkerWebcamHash.entrySet())
			if(entrySet.getKey().isInfoWindowShown())
				return true;
		return false;
	}


	private int mMarkerResId;
	private GoogleMap mMap;
	private HashMap<Marker, WebcamData> mMarkerWebcamHash;
	private BitmapTask mCurrentBitmapTask;
	private CustomMarkerBitmapFactory mCustomMarkerBitmapFactory;
	private WebcamOverlayChangeListener mWebcamOverlayChangeListener;
	private WebcamBaloonInfoWindowAdapter mInfoWindowAdapter;
	private Marker mCurrentlySelectedMarker;
	private Bitmap mWaitBitmap;
	private String mWaitString;
	private Settings mSettings;
}
