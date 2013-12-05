package it.giacomos.android.osmer.pro.widgets.map.report;

import java.util.ArrayList;

import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
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
import it.giacomos.android.osmer.pro.service.sharedData.NotificationData;
import it.giacomos.android.osmer.pro.service.sharedData.ReportRequestNotification;
import it.giacomos.android.osmer.pro.service.sharedData.ServiceSharedData;
import it.giacomos.android.osmer.pro.widgets.map.MapBaloonInfoWindowAdapter;
import it.giacomos.android.osmer.pro.widgets.map.OMapFragment;
import it.giacomos.android.osmer.pro.widgets.map.OOverlayInterface;
import it.giacomos.android.osmer.pro.widgets.map.OverlayType;
import it.giacomos.android.osmer.pro.widgets.map.ReportRequestListener;

public class ReportOverlay implements OOverlayInterface, DataPoolTextListener, ReportOverlayTaskListener, 
OnMarkerClickListener, OnMapLongClickListener, OnInfoWindowClickListener, OnMapClickListener
{

	private OMapFragment mMapFrag;
	private ReportOverlayTask mReportOverlayTask;
	private ArrayList<Marker> mMarkers;
	private MapBaloonInfoWindowAdapter mMapBaloonInfoWindowAdapter;
	private ReportRequestListener mReportRequestListener;
	/* the server returns only one request, the most recent in our area */
	private Marker mRequestMarker;

	public ReportOverlay(OMapFragment oMapFragment) 
	{
		mMapFrag = oMapFragment;
		mReportRequestListener = null;
		mReportOverlayTask = null;
		mMapBaloonInfoWindowAdapter = new MapBaloonInfoWindowAdapter(mMapFrag.getActivity());
		mRequestMarker = null;
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
		/* get from the data shared with the service the request for a report, if present */
		ServiceSharedData serviceSharedData = ServiceSharedData.Instance();
		NotificationData lastNotificationData = serviceSharedData.getNotificationData(NotificationData.TYPE_REQUEST);
		if(lastNotificationData != null && !lastNotificationData.isConsumed())
		{
			MarkerOptions reqMarkerOptions = mBuildReportRequestMarker(lastNotificationData);
			mMarkers.add(mMapFrag.getMap().addMarker(reqMarkerOptions));
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
	
	private MarkerOptions mBuildReportRequestMarker(NotificationData notificationData)
	{
		String  title = mMapFrag.getString(R.string.reportRequest);
		ReportRequestNotification rrn = (ReportRequestNotification) notificationData;
		String snippet = mMapFrag.getString(R.string.from) + " " + rrn.username;
		if(rrn.locality.length() > 0)
			snippet += "\n" + mMapFrag.getString(R.string.reportLocality) + ": " + rrn.locality;
		snippet += "\nlat. " + rrn.latitude + ", long. " + rrn.longitude;
		MarkerOptions reqMarkerO = new MarkerOptions();
		reqMarkerO.position(new LatLng(notificationData.latitude, notificationData.longitude));
		reqMarkerO.title(title);
		reqMarkerO.snippet(snippet);
		reqMarkerO.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
		return reqMarkerO;
	}

	@Override
	public void onMapLongClick(LatLng point) 
	{
		/* remove currently displayed marker */
		mRemoveReportRequestMarker();
		Marker reqMarker = mCreateRequestMarker(point);
		reqMarker.showInfoWindow();
	}
	
	private Marker mCreateRequestMarker(LatLng point)
	{
		String snippet = mMapFrag.getString(R.string.reportTouchBaloonToRequest);
		MarkerOptions reqMarkerO = new MarkerOptions();
		reqMarkerO.position(point);
		reqMarkerO.title(mMapFrag.getString(R.string.reportRequest));
		reqMarkerO.snippet(snippet);
		reqMarkerO.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
		reqMarkerO.draggable(true);
		Marker reqMarker = mMapFrag.getMap().addMarker(reqMarkerO);
		mMarkers.add(reqMarker);
		return reqMarker;
	}
	
	private void mRemoveReportRequestMarker()
	{
		int requestMarkerIndex = -1;
		for(int i = 0; i < mMarkers.size(); i++)
		{
			Marker marker = mMarkers.get(i);
			if(marker.getTitle() == mMapFrag.getString(R.string.reportRequest))
			{
				if(marker.isInfoWindowShown())
				{
					marker.hideInfoWindow();
					marker.remove();
					requestMarkerIndex = i;
				}
				break;
			}
		}
		if(requestMarkerIndex >= 0)
			mMarkers.remove(requestMarkerIndex);
	}
	
	private void mSaveReportRequestMarkerPosition()
	{
		
	}
	
	private void mRestoreReportRequestMarker()
	{
		
	}
	
	public void setOnReportRequestListener(ReportRequestListener rrl)
	{
		mReportRequestListener = rrl;
	}

	@Override
	public void onInfoWindowClick(Marker marker) 
	{
		if(marker.getTitle().compareTo(mMapFrag.getString(R.string.reportRequest)) == 0)
			mReportRequestListener.onReportRequest(marker.getPosition());
	}

	@Override
	/** hide request marker, if present */
	public void onMapClick(LatLng position) 
	{
		mRemoveReportRequestMarker();
	}

}