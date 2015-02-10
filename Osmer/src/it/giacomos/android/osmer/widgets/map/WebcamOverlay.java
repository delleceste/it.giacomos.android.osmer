package it.giacomos.android.osmer.widgets.map;
import it.giacomos.android.osmer.network.state.WebcamBitmapTask;
import it.giacomos.android.osmer.network.state.WebcamBitmapTaskListener;
import it.giacomos.android.osmer.preferences.Settings;
import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.webcams.WebcamXMLAssetLoader;
import it.giacomos.android.osmer.webcams.WebcamXMLDecoder;
import it.giacomos.android.osmer.webcams.WebcamData;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.Display;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class WebcamOverlay implements 
WebcamBitmapTaskListener, 
OOverlayInterface,
OnMarkerClickListener,
OnInfoWindowClickListener,
OnMapClickListener,
Runnable
{	
	/* timeout before initialization is performed */
	private final int LOAD_XML_DELAY = 150;

	public WebcamOverlay(int markerResId, 
			OMapFragment mapFrag) 
	{
		mMarkerResId = markerResId;
		mMap = mapFrag.getMap();
		mContext = mapFrag.getActivity().getApplicationContext();
		mMarkerWebcamHash = new HashMap<Marker, WebcamData>();

		mSettings = new Settings(mapFrag.getActivity().getApplicationContext());
		mCurrentBitmapTask = null;
		mCustomMarkerBitmapFactory = new CustomMarkerBitmapFactory(mapFrag.getResources());
		mCustomMarkerBitmapFactory.setTextWidthScaleFactor(2.5f);
		mCustomMarkerBitmapFactory.setAlphaTextContainer(100);
		mCustomMarkerBitmapFactory.setInitialFontSize(mSettings.mapWebcamMarkerFontSize());
		//		Log.e("WebcamOverlay", "initial font size of marker " + mSettings.mapWebcamMarkerFontSize());
		mWebcamOverlayChangeListener = mapFrag;
		mInfoWindowAdapter = new WebcamBaloonInfoWindowAdapter(mapFrag.getActivity());
		/* adapter and listeners */
		mMap.setInfoWindowAdapter(mInfoWindowAdapter);
		mMap.setOnMarkerClickListener(this);
		mMap.setOnMapClickListener(this);
		mMap.setOnInfoWindowClickListener(this);
		mCurrentlySelectedMarker = null;
		mWaitString = mapFrag.getResources().getString(R.string.webcam_downloading);
		mWebcamIcon = BitmapFactory.decodeResource(mapFrag.getResources(), mMarkerResId);
		mIsActive = true;
		/* get screen width for baloon size optimization */
		Display display = mapFrag.getActivity().getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		mWebcamImageSize = Math.min(size.x, size.y) / 2;
	}

	/** Registers the webcam overlay as a text listener of DataPool.
	 * Then, if the data pool does not contain up to date values, the webcam list 
	 * is obtained by means of the DataPoolCacheUtils. In this case, a Runnable is
	 * scheduled in order to initialize the overlay with a certain delay, so that
	 * the interface preserves smoothness while switching to the webcam overlay.
	 * 
	 * @param oa reference to the OsmerActivity
	 */
	public void initialize(OsmerActivity oa)
	{
		/* initialization */
		new Handler().postDelayed(this, LOAD_XML_DELAY);
	}

	@Override
	public void run() 
	{
		Log.e("WebcamOverlay.run", " mIsActive " + mIsActive);
		if(mIsActive)
		{
			WebcamXMLDecoder otherDec = new WebcamXMLDecoder();
			ArrayList<WebcamData> wcData = otherDec.decode(new WebcamXMLAssetLoader(mContext).getText());
			scheduleUpdate(wcData);
		}
	}

	/** executes update after a bunch of milliseconds not to block the 
	 * user interface while parsing xml file and generating all the markers.
	 * In this way the user immediately switches to the webcam map mode and 
	 * has the impression that the markers appear immediately after the mode 
	 * switch
	 */
	public void scheduleUpdate(ArrayList<WebcamData> wcData)
	{
		if(mIsActive)
			new Handler().postDelayed(new WebcamOverlayUpdateRunnable(this, wcData), 450);
	}

	/**
	 * Uses the webcam data list previously saved by onTextChanged to create a marker for each
	 * WebcamData stored in the list. Then adds each marker to the map.
	 * @param res a reference to the Resources to use in order to create the webcam icons.
	 */
	public void update(ArrayList<WebcamData> wcData)
	{
		/* update is called by a runnable. The user may have switched to another map mode in the meantime.
		 * Look for mIsActive to prevent unwanted webcam marker updates inside a map mode .
		 */
		if(mIsActive)
		{
			for(WebcamData wd : wcData)
			{
				if(!webcamInList(wd))
				{
					LatLng ll = wd.latLng;
					if(ll != null)
					{ 
						MarkerOptions mo = new MarkerOptions();
						mo.title(wd.location).snippet(wd.text).position(wd.latLng);
						BitmapDescriptor bmpDescriptor = mCustomMarkerBitmapFactory.getIcon(mWebcamIcon, wd.location);
						mo.icon(bmpDescriptor);
						Marker m = mMap.addMarker(mo);
						mMarkerWebcamHash.put(m, wd);
					}
				}
			}
			/* save in settings the optimal font size in order for CustomMarkerBitmapFactory to quickly 
			 * obtain it without calculating it again.
			 */
			mSettings.setMapWebcamMarkerFontSize(mCustomMarkerBitmapFactory.getCachedFontSize());
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) 
	{
		mCurrentlySelectedMarker = marker;
		WebcamData wd = mMarkerWebcamHash.get(marker);
		cancelCurrentWebcamTask();
		mInfoWindowAdapter.finalize();
		mCurrentBitmapTask = new WebcamBitmapTask(this, mWebcamImageSize, mWebcamImageSize);
		try 
		{
			URL webcamUrl = new URL(wd.url);
			mCurrentBitmapTask.parallelExecute(webcamUrl);
			mInfoWindowAdapter.setData(wd.location + " - " + mWaitString, null, false);
		}
		catch (MalformedURLException e) 
		{
			mWebcamOverlayChangeListener.onWebcamErrorMessageChanged(e.getLocalizedMessage());
			mCurrentlySelectedMarker = null;
		}
		/* do not show info window until the image has been retrieved */
		return false;
	}

	@Override
	public void onWebcamBitmapBytesUpdate(byte[] bytes) 
	{
		/* notify map fragment that the image has changed */
		mWebcamOverlayChangeListener.onWebcamBitmapBytesChanged(bytes);	
	}

	@Override
	public void onWebcamBitmapUpdate(Bitmap bmp, String errorMessage) 
	{
		/* no need to check the mIsActive flag because the bitmap task is 
		 * cancelled by clear().
		 */
		if(bmp == null && !errorMessage.isEmpty())
		{
			mWebcamOverlayChangeListener.onWebcamErrorMessageChanged(errorMessage);
		}
		else if(bmp != null)
		{
			if(!errorMessage.isEmpty())
				mWebcamOverlayChangeListener.onWebcamErrorMessageChanged(errorMessage);
			if(mCurrentlySelectedMarker != null)
			{
				if(!errorMessage.isEmpty())
					mInfoWindowAdapter.setData(errorMessage, null, false);
				else
				{
					mInfoWindowAdapter.setData(mMarkerWebcamHash.get(mCurrentlySelectedMarker).text, bmp, true);

				}
				mMap.moveCamera(CameraUpdateFactory.scrollBy(0, -bmp.getHeight() / 2));
				mCurrentlySelectedMarker.showInfoWindow();
			}
		}
	}

	@Override
	public void onInfoWindowClick(Marker marker) 
	{
		if(mInfoWindowAdapter.isImageValid())
			mWebcamOverlayChangeListener.onWebcamInfoWindowImageClicked();
		else
			mWebcamOverlayChangeListener.onWebcamMessageChanged(R.string.webcam_wait_for_image);
	}

	@Override
	public void onMapClick(LatLng arg0) 
	{
		//		Log.e("onMapClick", " cancelling task ");
		cancelCurrentWebcamTask();
		mInfoWindowAdapter.finalize();
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
			//			Log.e("cancelCurrentWebcamTask", "cancelling task");
			mWebcamOverlayChangeListener.onWebcamBitmapTaskCanceled(mCurrentBitmapTask.getUrl());
			mCurrentBitmapTask.cancel(false);
		}
		//		else
		//			Log.e("cancelCurrentWebcamTask", "NOT cancelling task (not runnig)");
	}

	@Override
	public void clear() 
	{
		/* important! cancel bitmap task if running */
		cancelCurrentWebcamTask();
		for(Map.Entry<Marker, WebcamData> entrySet : mMarkerWebcamHash.entrySet())
			entrySet.getKey().remove();
		mMarkerWebcamHash.clear();
		/* recycle bitmap and unbind drawable */
		mInfoWindowAdapter.finalize();
		/* Marks the overlay as finalized, disabling all updates from async tasks */
		mIsActive = false;
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
	private WebcamBitmapTask mCurrentBitmapTask;
	private CustomMarkerBitmapFactory mCustomMarkerBitmapFactory;
	private WebcamOverlayChangeListener mWebcamOverlayChangeListener;
	private WebcamBaloonInfoWindowAdapter mInfoWindowAdapter;
	private Marker mCurrentlySelectedMarker;
	private String mWaitString;
	private Settings mSettings;
	private Context mContext;
	private Bitmap mWebcamIcon;
	private boolean mIsActive;
	private int mWebcamImageSize; /* pixels */
}
