package it.giacomos.android.osmer.pro.widgets.map.report;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.pro.OsmerActivity;
import it.giacomos.android.osmer.pro.locationUtils.GeocodeAddressTask;
import it.giacomos.android.osmer.pro.locationUtils.GeocodeAddressUpdateListener;
import it.giacomos.android.osmer.pro.locationUtils.LocationInfo;
import it.giacomos.android.osmer.pro.network.Data.DataPool;
import it.giacomos.android.osmer.pro.network.Data.DataPoolCacheUtils;
import it.giacomos.android.osmer.pro.network.Data.DataPoolTextListener;
import it.giacomos.android.osmer.pro.network.state.ViewType;
import it.giacomos.android.osmer.pro.preferences.Settings;
import it.giacomos.android.osmer.pro.service.sharedData.NotificationData;
import it.giacomos.android.osmer.pro.service.sharedData.ReportRequestNotification;
import it.giacomos.android.osmer.pro.service.sharedData.ServiceSharedData;
import it.giacomos.android.osmer.pro.widgets.map.MapBaloonInfoWindowAdapter;
import it.giacomos.android.osmer.pro.widgets.map.OMapFragment;
import it.giacomos.android.osmer.pro.widgets.map.OOverlayInterface;
import it.giacomos.android.osmer.pro.widgets.map.OverlayType;
import it.giacomos.android.osmer.pro.widgets.map.MyReportRequestListener;
import it.giacomos.android.osmer.pro.widgets.map.report.network.PostType;
import it.giacomos.android.osmer.pro.widgets.map.report.network.ReportUpdater;

public class ReportOverlay implements OOverlayInterface, DataPoolTextListener, 
ReportOverlayTaskListener, 
OnMarkerClickListener, OnMapLongClickListener, OnInfoWindowClickListener, 
OnMarkerDragListener, GeocodeAddressUpdateListener
{

	private OMapFragment mMapFrag;
	private ReportOverlayTask mReportOverlayTask;
	private MapBaloonInfoWindowAdapter mMapBaloonInfoWindowAdapter;
	private MyReportRequestListener mReportRequestListener;
	private GeocodeAddressTask mGeocodeAddressTask;
	private ReportUpdater mReportUpdater;
	
	/* maps the marker id (obtained with marker.getId()) with the associated marker data */
	private HashMap<String , DataInterface> mDataInterfaceHash;

	public ReportOverlay(OMapFragment oMapFragment) 
	{
		mMapFrag = oMapFragment;
		mReportRequestListener = null;
		mReportOverlayTask = null;
		mMapBaloonInfoWindowAdapter = new MapBaloonInfoWindowAdapter(mMapFrag.getActivity());
		mGeocodeAddressTask = null;
		mReportUpdater = new ReportUpdater(oMapFragment.getActivity().getApplicationContext());
		mMapFrag.getMap().setInfoWindowAdapter(mMapBaloonInfoWindowAdapter);
		mDataInterfaceHash = new HashMap<String, DataInterface>();
		
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
		Log.e("ReportOverlay: clear called", "maerkers size " + mDataInterfaceHash.size());
		((OsmerActivity) mMapFrag.getActivity()).getDataPool().unregisterTextListener(ViewType.REPORT);
		mCancelTasks();
		mRemoveMarkers();
	}

	private void mRemoveMarkers()
	{
		for(String markerId : mDataInterfaceHash.keySet())
			mDataInterfaceHash.get(markerId).getMarker().remove();
		mDataInterfaceHash.clear();
	}

	private void mCancelTasks()
	{
		if(mReportOverlayTask != null)
			mReportOverlayTask.cancel(false);
		if(mGeocodeAddressTask != null)
			mGeocodeAddressTask.cancel(true);
	}

	@Override
	public int type() 
	{
		return OverlayType.REPORT;
	}

	@Override
	public void hideInfoWindow() 
	{
		for(int i = 0; i < mDataInterfaceHash.size(); i++)
			mDataInterfaceHash.get(i).getMarker().hideInfoWindow();
	}

	@Override
	public boolean isInfoWindowVisible()
	{
		for(String markerId : mDataInterfaceHash.keySet())
		{
			Marker m = mDataInterfaceHash.get(markerId).getMarker();
			if(m == null)
				Log.e("isInfoWindowVisible", "marker for " + mDataInterfaceHash.get(markerId).getLatitude() +  ","
						+ mDataInterfaceHash.get(markerId).getLongitude() + " is null");
			if(m.isInfoWindowShown())
				return true;
		}
		return false;
	}

	@Override
	/** redraws all markers, the user report markers, my request marker and buddy request notification
	 *  marker, if present.
	 */
	public void onReportOverlayTaskFinished(DataInterface [] dataInterfaceList) 
	{
		if(dataInterfaceList != null) /* the task may return null */
		{
			Log.e("onReportOverlayTaskFinished", "got " + dataInterfaceList.length);
			mRemoveMarkers();
			for(DataInterface dataI : dataInterfaceList)
			{
				/* generate marker */
				Marker marker = mMapFrag.getMap().addMarker(dataI.getMarkerOptions());
				/* store into our data reference */
				dataI.setMarker(marker);
				/* insert data into hash map for future use */
				mDataInterfaceHash.put(marker.getId(), dataI);
			}
			/* do not need data interface list anymore, since it's been saved into hash */
			dataInterfaceList = new DataInterface[0];
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
		int reportDataLen = 0, requestDataLen = 0;
		/* In this first implementation, let the markers be updated even if the text has not changed.
		 * When the task has been completed, the buddy request notification marker is drawn if pertinent,
		 * inside onReportOverlayTaskFinished().
		 */

		/* ok start processing data */
		DataParser reportDataFactory = new DataParser();

		ReportData reportDataList[] = reportDataFactory.parseReports(txt); /* reports */
		RequestData requestDataList[] = reportDataFactory.parseRequests(txt);
		if(reportDataList != null)
			reportDataLen = reportDataList.length;
		if(requestDataList != null)
			requestDataLen = requestDataList.length;
		
		DataInterface dataList[] = new DataInterface[reportDataLen + requestDataLen];
		if(reportDataLen > 0) /* reportDataList not null */
			System.arraycopy(reportDataList, 0, dataList, 0,  reportDataLen);
		if(requestDataLen > 0) /* requestDataList not null */
			System.arraycopy(requestDataList, 0, dataList, reportDataLen, requestDataLen);

		mReportOverlayTask = new ReportOverlayTask(mMapFrag.getActivity().getApplicationContext(), this);
		mReportOverlayTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, dataList);

		if(dataList == null || dataList.length == 0)
			Toast.makeText(mMapFrag.getActivity().getApplicationContext(), R.string.reportNoneAvailable, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onTextError(String error, ViewType t) {
		Toast.makeText(mMapFrag.getActivity(), error, Toast.LENGTH_LONG).show();

	}

	@Override
	public void onMapLongClick(LatLng point) 
	{
		Marker myRequestMarker = mCreateMyRequestMarker(point);
		mStartGeocodeAddressTask(myRequestMarker);
		myRequestMarker.showInfoWindow();
	}

	/* Create my request marker. Embed the marker in a RequestData which is finally
	 * put into the hash table containing all the markers on the map.
	 */
	private Marker mCreateMyRequestMarker(LatLng point)
	{
		Context ctx = mMapFrag.getActivity().getApplicationContext();
		Settings s = new Settings(ctx);
		String userName = s.getReporterUserName();
		Date date = Calendar.getInstance().getTime();
		DateFormat df = DateFormat.getDateInstance();
		/* RequestData(String d, String user, String local, double la, double lo, String wri, boolean isPublished) */
		RequestData myRequestData = new RequestData(df.format(date), userName, "-", point.latitude, point.longitude, "w", false);
		myRequestData.buildMarkerOptions(ctx);
		Marker myRequestMarker = mMapFrag.getMap().addMarker(myRequestData.getMarkerOptions());
		myRequestData.setMarker(myRequestMarker);
		/* prepend "MyRequestMarker" to the id of my request marker */
		mDataInterfaceHash.put("MyRequestMarker" + myRequestMarker.getId(), myRequestData);
		return myRequestMarker;
	}

	public void setOnReportRequestListener(MyReportRequestListener rrl)
	{
		mReportRequestListener = rrl;
	}

	private void mStartGeocodeAddressTask(Marker marker)
	{
		Log.e("ReportOverlay.mStartGeocodeAddressTask", "starting geocode address task");
		mGeocodeAddressTask = new GeocodeAddressTask(mMapFrag.getActivity().getApplicationContext(), this);
		LatLng ll = marker.getPosition();
		Location location = new Location("");
		location.setLatitude(ll.latitude);
		location.setLongitude(ll.longitude);
		mGeocodeAddressTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, location);
	}

	@Override
	public void onInfoWindowClick(Marker marker) 
	{
		DataInterface dataI = mDataInterfaceHash.get(marker.getId());
		/* retrieve my request marker, if present. If it's mine and not published, trigger the request dialog
		 * execution.
		 */
		if(dataI != null && dataI.getType() == DataInterface.TYPE_REQUEST &&
				dataI.isWritable() && !dataI.isPublished())
		{
			Log.e("onInfoWindowClick", "got My request marker; clicked position  " + marker.getPosition().toString());
			mReportRequestListener.onMyReportRequestTriggered(marker.getPosition(), dataI.getLocality());
			marker.hideInfoWindow();
		}
		/* if the marker is mine, be it my request or my report, it is possible to remove it */
		else if(dataI != null && dataI.isWritable() && dataI.isPublished())
		{
			mReportRequestListener.onMyReportRequestDialogCancelled(marker.getPosition());
		}
	}

	@Override
	public void onMarkerDrag(Marker arg0) {

		Log.e("ReportOverlay.onMarkerDrag", "marker " + arg0.getTitle());
	}

	@Override
	public void onMarkerDragEnd(Marker marker) 
	{
		mStartGeocodeAddressTask(marker);
	}

	@Override
	public void onMarkerDragStart(Marker arg0) 
	{
		Log.e("ReportOverlay.onMarkerDragStart", "geocode address task null? " + (mGeocodeAddressTask != null));
		if(mGeocodeAddressTask != null)
		{
			Log.e("ReportOverlay.onMarkerDragStart", "stopping geocode address task");
			mGeocodeAddressTask.cancel(false);
		}
	}

	@Override
	public void onGeocodeAddressUpdate(LocationInfo locationInfo) 
	{	
		Log.e("ReportOverlay.onGeocodeAddressUpdate", "got location info!");
		for(DataInterface dataI : mDataInterfaceHash.values())
		{
			/* get, among all DataInterface entries, the requests and our request */
			if(dataI.getType() == DataInterface.TYPE_REQUEST && dataI.isWritable() 
					&& locationInfo.latitude == dataI.getLatitude() 
					&& locationInfo.longitude == dataI.getLongitude())
			{
				RequestData rd = (RequestData) dataI;
				rd.updateWithLocality(locationInfo.locality, mMapFrag.getActivity().getApplicationContext());
				rd.getMarker().hideInfoWindow();
				rd.getMarker().showInfoWindow();
				mReportRequestListener.onMyReportLocalityChanged(locationInfo.locality);
			}
		}
	}

	public void onPostActionResult(boolean error, String message, PostType postType) 
	{
		Log.e("onPostActionResult", " err " + error + " post type " + postType);
		if(postType == PostType.REQUEST)
		{
			
		}
	}

	public void removeMyPendingReportRequestMarker(LatLng position) 
	{
		for(DataInterface dataI : mDataInterfaceHash.values())
		{
			/* get, among all DataInterface entries, the requests and our request */
			if(dataI.getType() == DataInterface.TYPE_REQUEST && dataI.isWritable() 
					&& position.latitude == dataI.getLatitude() 
					&& position.longitude == dataI.getLongitude())
			{
				Log.e("removeMyPendingReportRequestMarker", "Cancel hit on dialog? removing maker "
						+ dataI.getLocality() + dataI.getMarker().getTitle());
				Marker toRemoveMarker = dataI.getMarker();
				mDataInterfaceHash.remove(toRemoveMarker.getId());
				toRemoveMarker.remove();
			}
		}
		
	}

	public void update(Context ctx, boolean force) 
	{
		mReportUpdater.update(force);
	}


}