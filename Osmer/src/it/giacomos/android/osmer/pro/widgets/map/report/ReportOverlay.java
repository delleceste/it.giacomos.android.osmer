package it.giacomos.android.osmer.pro.widgets.map.report;

import java.util.ArrayList;

import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.pro.OsmerActivity;
import it.giacomos.android.osmer.pro.network.Data.DataPool;
import it.giacomos.android.osmer.pro.network.Data.DataPoolCacheUtils;
import it.giacomos.android.osmer.pro.network.Data.DataPoolTextListener;
import it.giacomos.android.osmer.pro.network.state.ViewType;
import it.giacomos.android.osmer.pro.widgets.map.MapBaloonInfoWindowAdapter;
import it.giacomos.android.osmer.pro.widgets.map.OMapFragment;
import it.giacomos.android.osmer.pro.widgets.map.OOverlayInterface;
import it.giacomos.android.osmer.pro.widgets.map.OverlayType;

public class ReportOverlay implements OOverlayInterface, DataPoolTextListener, ReportOverlayTaskListener, 
OnMarkerClickListener
{

	private OMapFragment mMapFrag;
	private ReportOverlayTask mReportOverlayTask;
	private ArrayList<Marker> mMarkers;
	private  MapBaloonInfoWindowAdapter mMapBaloonInfoWindowAdapter;

	public ReportOverlay(OMapFragment oMapFragment) 
	{
		mMapFrag = oMapFragment;
		mReportOverlayTask = null;
		mMapBaloonInfoWindowAdapter = new MapBaloonInfoWindowAdapter(mMapFrag.getActivity());
		mMapFrag.getMap().setInfoWindowAdapter(mMapBaloonInfoWindowAdapter);
		mMarkers = new ArrayList<Marker>();
		/* register as DataPool text listener */
		/* get data pool reference */
		DataPool dataPool = ((OsmerActivity) mMapFrag.getActivity()).getDataPool();
		DataPoolCacheUtils dataCacheUtils = new DataPoolCacheUtils(); /* cache utils reference */
		if(!dataPool.isTextValid(ViewType.REPORT)) /* if data pool hasn't got up to date text yet */
		{
			String text = dataCacheUtils.loadFromStorage(ViewType.REPORT, mMapFrag.getActivity().getApplicationContext());
			this.onTextChanged(text, ViewType.REPORT, true);
		}
		/* if there already is data for the given ViewType, the listener is immediately called */
		dataPool.registerTextListener(ViewType.REPORT, this);
	}

	@Override
	public void clear() 
	{
		Log.e("ReportOverlay: clear called", "maerkers size " + mMarkers.size());
		((OsmerActivity) mMapFrag.getActivity()).getDataPool().unregisterTextListener(ViewType.REPORT);
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
	public void onReportOverlayTaskFinished(
			ArrayList<MarkerOptions> markerOptionsArray) 
	{
		Log.e("onReportOverlayTaskFinished", "got " + markerOptionsArray.size());
		mRemoveMarkers();
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

	@Override
	public void onTextChanged(String txt, ViewType t, boolean fromCache) 
	{		
		Log.e("ReportOverlay.onTextChanged", "vtype" + t + " fromCache "  + fromCache + " : "  + txt);
		/* ok start processing data */
		ReportDataFactory reportDataFactory = new ReportDataFactory();
		ReportData[] reportDataList = reportDataFactory.parse(txt);
		if(reportDataList != null)
		{
			mReportOverlayTask = new ReportOverlayTask(mMapFrag.getActivity().getApplicationContext(), this);
			mReportOverlayTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, reportDataList);
		}
		else
			Toast.makeText(mMapFrag.getActivity().getApplicationContext(), R.string.reportNoneAvailable, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onTextError(String error, ViewType t) {
		Toast.makeText(mMapFrag.getActivity(), error, Toast.LENGTH_LONG).show();

	}

}