package it.giacomos.android.osmer.pro.widgets.map.report;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.pro.network.state.TextTask;
import it.giacomos.android.osmer.pro.network.state.TextTaskListener;
import it.giacomos.android.osmer.pro.network.state.Urls;
import it.giacomos.android.osmer.pro.network.state.ViewType;
import it.giacomos.android.osmer.pro.observations.ObservationData;
import it.giacomos.android.osmer.pro.observations.ObservationType;
import it.giacomos.android.osmer.pro.observations.SkyDrawableIdPicker;
import it.giacomos.android.osmer.pro.widgets.map.MapBaloonInfoWindowAdapter;
import it.giacomos.android.osmer.pro.widgets.map.OMapFragment;
import it.giacomos.android.osmer.pro.widgets.map.OOverlayInterface;
import it.giacomos.android.osmer.pro.widgets.map.OverlayType;

public class ReportOverlay implements OOverlayInterface, TextTaskListener, ReportOverlayTaskListener, 
OnMarkerClickListener
{

	private OMapFragment mMapFrag;
	private TextTask mTextTask;
	private ReportOverlayTask mReportOverlayTask;
	private ArrayList<Marker> mMarkers;
	private  MapBaloonInfoWindowAdapter mMapBaloonInfoWindowAdapter;
	
	public ReportOverlay(OMapFragment oMapFragment) 
	{
		mMapFrag = oMapFragment;
		mReportOverlayTask = null;
		mTextTask = null;
		mMapBaloonInfoWindowAdapter = new MapBaloonInfoWindowAdapter(mMapFrag.getActivity());
		mMapFrag.getMap().setInfoWindowAdapter(mMapBaloonInfoWindowAdapter);
		mMarkers = new ArrayList<Marker>();
	}

	public void update()
	{
		mCancelTasks(); /* if yet running before starting new ones */
		mRemoveMarkers();
		mTextTask = new TextTask(this, ViewType.REPORT);
		String urlStr = new Urls().getReportUrl();
		try{
			URL url = new URL(urlStr);
			mTextTask.parallelExecute(url);
		}
		catch(MalformedURLException e)
		{
			onTextUpdate("Malformed url \"" + urlStr + "\"\n" , ViewType.REPORT, e.getMessage(), null);
		}
	}
	
	@Override
	public void clear() 
	{
		Log.e("ReportOverlay: clear called", "maerkers size " + mMarkers.size());
		mCancelTasks();
		mRemoveMarkers();
	}

	private void mRemoveMarkers()
	{
		for(int i = 0; i < mMarkers.size(); i++)
			mMarkers.get(i).remove();
		mMarkers.clear();
	}
	
	private void mCancelTasks()
	{
		if(mTextTask != null)
			mTextTask.cancel(false);
		if(mReportOverlayTask != null)
			mReportOverlayTask.cancel(false);
	}
	
	@Override
	public int type() 
	{
		return OverlayType.REPORT;
	}

	@Override
	public void hideInfoWindow() {
		// TODO Auto-generated method stub
		for(int i = 0; i < mMarkers.size(); i++)
			mMarkers.get(i).hideInfoWindow();
	}

	@Override
	public boolean isInfoWindowVisible() {
		for(int i = 0; i < mMarkers.size(); i++)
			if(mMarkers.get(i).isInfoWindowShown())
				return true;
		return false;
	}

	@Override
	public void onTextUpdate(String text, ViewType st, String errorMessage,
			AsyncTask<URL, Integer, String> task) 
	{
		if(!errorMessage.isEmpty())
			Toast.makeText(mMapFrag.getActivity(), errorMessage, Toast.LENGTH_LONG).show();
		else
		{
			/* ok start processing data */
			ReportDataFactory reportDataFactory = new ReportDataFactory();
			ReportData[] reportDataList = reportDataFactory.parse(text);
			if(reportDataList != null)
			{
				mReportOverlayTask = new ReportOverlayTask(mMapFrag.getActivity().getApplicationContext(), this);
				mReportOverlayTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, reportDataList);
			}
			else
				Toast.makeText(mMapFrag.getActivity().getApplicationContext(), R.string.reportNoneAvailable, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onTextBytesUpdate(byte[] bytes, ViewType vt) 
	{

	}

	@Override
	public void onReportOverlayTaskFinished(
			ArrayList<MarkerOptions> markerOptionsArray) 
	{
		Log.e("onReportOverlayTaskFinished", "got " + markerOptionsArray.size());
		for(MarkerOptions markerOptions : markerOptionsArray)
		{
			mMarkers.add(mMapFrag.getMap().addMarker(markerOptions));
		}

	}
	
	@Override
	public boolean onMarkerClick(Marker m) 
	{
		mMapBaloonInfoWindowAdapter.setTitle(m.getTitle());
		mMapBaloonInfoWindowAdapter.setText(m.getSnippet());
		return false;
	}
	
}