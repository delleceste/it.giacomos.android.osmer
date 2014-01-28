package it.giacomos.android.osmer.pro.widgets.map.report;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import it.giacomos.android.osmer.pro.R;
import it.giacomos.android.osmer.pro.MyAlertDialogFragment;
import it.giacomos.android.osmer.pro.OsmerActivity;
import it.giacomos.android.osmer.pro.locationUtils.GeocodeAddressTask;
import it.giacomos.android.osmer.pro.locationUtils.GeocodeAddressUpdateListener;
import it.giacomos.android.osmer.pro.locationUtils.LocationInfo;
import it.giacomos.android.osmer.pro.network.Data.DataPoolCacheUtils;
import it.giacomos.android.osmer.pro.network.state.ViewType;
import it.giacomos.android.osmer.pro.preferences.Settings;
import it.giacomos.android.osmer.pro.service.sharedData.ReportRequestNotification;
import it.giacomos.android.osmer.pro.service.sharedData.ServiceSharedData;
import it.giacomos.android.osmer.pro.widgets.map.MapBaloonInfoWindowAdapter;
import it.giacomos.android.osmer.pro.widgets.map.OMapFragment;
import it.giacomos.android.osmer.pro.widgets.map.OOverlayInterface;
import it.giacomos.android.osmer.pro.widgets.map.OverlayType;
import it.giacomos.android.osmer.pro.widgets.map.ReportRequestListener;
import it.giacomos.android.osmer.pro.widgets.map.report.network.PostType;
import it.giacomos.android.osmer.pro.widgets.map.report.network.ReportUpdater;
import it.giacomos.android.osmer.pro.widgets.map.report.network.ReportUpdaterListener;

public class ReportOverlay implements OOverlayInterface, 
ReportOverlayTaskListener, 
OnMapClickListener,
OnMarkerClickListener, OnMapLongClickListener, OnInfoWindowClickListener, 
OnMarkerDragListener, GeocodeAddressUpdateListener, ReportUpdaterListener
{

	private OMapFragment mMapFrag;
	private ReportOverlayTask mReportOverlayTask;
	private MapBaloonInfoWindowAdapter mMapBaloonInfoWindowAdapter;
	private ReportRequestListener mMyReportRequestListener; /* OsmerActivity */
	private GeocodeAddressTask mGeocodeAddressTask;
	private ReportUpdater mReportUpdater;

	/* maps the marker id (obtained with marker.getId()) with the associated marker data */
	private HashMap<String , DataInterface> mDataInterfaceHash;

	public ReportOverlay(OMapFragment oMapFragment) 
	{
		mMapFrag = oMapFragment;
		mMyReportRequestListener = null; /* OsmerActivity */
		mReportOverlayTask = null;
		mMapBaloonInfoWindowAdapter = new MapBaloonInfoWindowAdapter(mMapFrag.getActivity());
		mGeocodeAddressTask = null;
		mReportUpdater = new ReportUpdater(oMapFragment.getActivity().getApplicationContext(),  this);
		mMapFrag.getMap().setInfoWindowAdapter(mMapBaloonInfoWindowAdapter);
		mDataInterfaceHash = new HashMap<String, DataInterface>();

		/* register as DataPool text listener */
		/* get data pool reference */
		//		DataPoolCacheUtils dataCacheUtils = new DataPoolCacheUtils(); /* cache utils reference */
		//		{
		//			String text = dataCacheUtils.loadFromStorage(ViewType.REPORT, mMapFrag.getActivity().getApplicationContext());
		//			Log.e("----------- FROME CHAENC", "from chane!");
		//			onReportUpdateDone(text);
		//		}
	}

	@Override
	public void clear() 
	{
		mReportUpdater.clear();
		mCancelTasks();
		mRemoveMarkers();
	}

	/** ReportOverlay has a ReportUpdater which registers with the LocationClient and 
	 *  with the  NetworkStatusMonitor to obtain location updates and to be notified 
	 *  when the network goes up/down. When the activity is paused, it is necessary to
	 *  release such resources.
	 * 
	 */
	public void onPause()
	{
		mReportUpdater.onPause();
		/* we can cancel report overlay tasks and geocode address tasks if paused, because
		 * the activity, when resumed, updates data.
		 */
		mCancelTasks();
	}
	
	public void onResume()
	{
		mReportUpdater.onResume();
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
		for(String id : mDataInterfaceHash.keySet())
			mDataInterfaceHash.get(id).getMarker().hideInfoWindow();
	}

	@Override
	public boolean isInfoWindowVisible()
	{
		for(String markerId : mDataInterfaceHash.keySet())
		{
			Marker m = mDataInterfaceHash.get(markerId).getMarker();
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
		Context ctx = mMapFrag.getActivity().getApplicationContext();
		Resources res = ctx.getResources();
		int reportCount = 0;
		if(dataInterfaceList != null) /* the task may return null */
		{
			/* save my request markers that haven't been published yet in order not to lose them
			 * when the update takes place. Actually, mRemoveMarkers below clears all markers.
			 */
			ArrayList<DataInterface> myRequestsYetUnpublishedBackup = mSaveYetUnpublishedMyRequestData();
			mRemoveMarkers();
			for(DataInterface dataI : dataInterfaceList)
			{
				/* generate marker */
				Marker marker = mMapFrag.getMap().addMarker(dataI.getMarkerOptions());
				/* store into our data reference */
				dataI.setMarker(marker);
				/* insert data into hash map for future use */
				mDataInterfaceHash.put(marker.getId(), dataI);
				if(dataI.getType() == DataInterface.TYPE_REPORT)
					reportCount++;

			}
			/* do not need data interface list anymore, since it's been saved into hash */
			dataInterfaceList = new DataInterface[0];
			/* restore yet unpublished markers that the user was just placing into the map */
			mRestoreYetUnpublishedMyRequestData(myRequestsYetUnpublishedBackup);
		}
		if(reportCount > 0)
			Toast.makeText(ctx,  res.getString(R.string.thereAreNReports) +
					reportCount, Toast.LENGTH_SHORT).show();

		mCheckForFreshNotifications();
	}

	private void mCheckForFreshNotifications() 
	{
		for(DataInterface di : mDataInterfaceHash.values())
		{
			if(di.getType() == DataInterface.TYPE_REQUEST && !di.isWritable())
			{
				// ReportRequestNotification(String datet, String user, double lat, double lon, String loc)
				RequestData rd = (RequestData) di;
				ReportRequestNotification repReqN = new ReportRequestNotification(rd.datetime,
						rd.username, rd.getLatitude(), rd.getLongitude(), rd.locality);
				/* put the report request notification into the data shared with the service,
				 * so that the service does not trigger a notification.
				 */
				Context ctx = mMapFrag.getActivity().getApplicationContext();
				ServiceSharedData ssd = ServiceSharedData.Instance(ctx);
				if(ssd.canBeConsideredNew(repReqN, ctx))
				{
					/* true, sets the Notification request notified */
					ssd.updateCurrentRequest(repReqN, true);
					// NOTE
					// The following call has been removed and its functionality has been
					// put into the updateCurrentRequest through the boolean parameter.
					// ssd.setWasNotified(repReqN);
					/* animate camera to new request */
					mMapFrag.moveTo(rd.getLatitude(), rd.getLongitude());
					rd.getMarker().showInfoWindow();
				}	
			}
		}

	}

	@Override
	public boolean onMarkerClick(Marker m) 
	{
		mMapBaloonInfoWindowAdapter.setTitle(m.getTitle());
		mMapBaloonInfoWindowAdapter.setText(m.getSnippet());
		return false;
	}

	public void update(Context ctx, boolean force) 
	{
		mReportUpdater.update(force);
	}

	@Override
	public void onReportUpdateError(String error)
	{
		((OsmerActivity) mMapFrag.getActivity()).makePendingAlertErrorDialog(error);
	}

	/** This is invoked when the report data in textual form has completed downloading.
	 * @param txt the downloaded data in simple text format.
	 * 
	 * This method parses the textual data and creates two lists, using DataParser class:
	 * 1) the published reports (of type ReportData)
	 * 2) the pending request list (of type RequestData).
	 * The two lists are merged together and sent to an async task that creates the data
	 * structures used to afterwards build markers and place them on the map.
	 * When the async task finishes, onReportOverlayTaskFinished() method is invoked.
	 */
	@Override
	public void onReportUpdateDone(String txt) 
	{		
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
	public void onMapLongClick(LatLng point) 
	{
		mRemoveUnpublishedMyRequestMarkers();
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
		mDataInterfaceHash.put(myRequestMarker.getId(), myRequestData);

		return myRequestMarker;
	}

	public void setOnReportRequestListener(ReportRequestListener rrl)
	{
		mMyReportRequestListener = rrl;  /* OsmerActivity */
	}

	private void mStartGeocodeAddressTask(Marker marker)
	{
		Log.e("ReportOverlay.mStartGeocodeAddressTask", "starting geocode address task");
		mGeocodeAddressTask = 
				new GeocodeAddressTask(mMapFrag.getActivity().getApplicationContext(),
						this, marker.getId());
		LatLng ll = marker.getPosition();
		Location location = new Location("");
		location.setLatitude(ll.latitude);
		location.setLongitude(ll.longitude);
		mGeocodeAddressTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, location);
	}

	@Override
	public void onGeocodeAddressUpdate(LocationInfo locationInfo, String id) 
	{	
		Log.e("ReportOverlay.onGeocodeAddressUpdate", "-> " + locationInfo.locality);
		DataInterface dataI = mDataInterfaceHash.get(id);
		if(dataI != null) /* may have been removed */
		{
			RequestData rd = (RequestData) dataI;
			rd.setLocality(locationInfo.locality);
			rd.updateWithLocality(locationInfo.locality, mMapFrag.getActivity().getApplicationContext());
			rd.getMarker().hideInfoWindow();
			rd.getMarker().showInfoWindow();
			mMyReportRequestListener.onMyReportLocalityChanged(locationInfo.locality);
		}
	}

	/** OsmerActivity implements ReportRequestListener.
	 *  onInfoWindowClick invokes OsmerActivity callbacks in order to perform
	 *  specific actions such as show dialog fragments which are appropriate for
	 *  the action represented by the baloon.
	 */
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
			mMyReportRequestListener.onMyReportRequestTriggered(marker.getPosition(), dataI.getLocality());
			marker.hideInfoWindow();
		}
		/* if the marker is mine, be it my request or my report, it is possible to remove it */
		else if(dataI != null && dataI.isWritable() && dataI.isPublished())
		{
			PostType postType = null;
			if(dataI.getType() == DataInterface.TYPE_REQUEST)
				postType = PostType.REQUEST_REMOVE;
			else  if(dataI.getType() == DataInterface.TYPE_REPORT)
				postType = PostType.REPORT_REMOVE;
			if(postType != null)
				mMyReportRequestListener.onMyPostRemove(marker.getPosition(), postType);
		}
		/* a request for me? (read only) */
		else if(dataI != null && !dataI.isWritable() && dataI.isPublished() 
				&& dataI.getType() == DataInterface.TYPE_REQUEST)
		{
			mMyReportRequestListener.onMyReportPublish();
		}
		else if(dataI == null) /* must build a new request */
		{

		}
	}

	@Override
	public void onMarkerDrag(Marker arg0) {

	}

	@Override
	public void onMarkerDragEnd(Marker marker) 
	{
		/* must update data into data interface */
		DataInterface di = mDataInterfaceHash.get(marker.getId());
		if(di != null && di.getType() == DataInterface.TYPE_REQUEST) /* must be */
		{
			RequestData reqD = (RequestData) di;
			reqD.setLatitude(marker.getPosition().latitude);
			reqD.setLongitude(marker.getPosition().longitude);
			mStartGeocodeAddressTask(marker);
		}
	}

	@Override
	public void onMarkerDragStart(Marker arg0) 
	{
		if(mGeocodeAddressTask != null)
		{
			mGeocodeAddressTask.cancel(false);
		}
	}

	public void onPostActionResult(boolean error, String message, PostType postType) 
	{
		if(postType == PostType.REQUEST)
		{

		}
	}

	public void removeMyPendingReportRequestMarker(LatLng position) 
	{
		for(Iterator<Map.Entry<String, DataInterface>> it = mDataInterfaceHash.entrySet().iterator(); it.hasNext(); ) 
		{
			Map.Entry<String, DataInterface> entry = it.next();
			DataInterface dataI = entry.getValue();
			/* get, among all DataInterface entries, the requests and our request */
			if(dataI.getType() == DataInterface.TYPE_REQUEST && dataI.isWritable() 
					&& position.latitude == dataI.getLatitude() 
					&& position.longitude == dataI.getLongitude())
			{
//				Log.e("removeMyPendingReportRequestMarker", "Cancel hit on dialog? removing maker "
//						+ dataI.getLocality() + dataI.getMarker().getTitle());
				Marker toRemoveMarker = dataI.getMarker();
				toRemoveMarker.remove();
				it.remove(); /* remove from hash */
			}
		}

	}

	private void mRestoreYetUnpublishedMyRequestData(ArrayList<DataInterface> backupData)
	{
//		Log.e("mRestoreYetUnpublishedMyRequestData", "bk data sixe " + backupData.size() + " data if hash " 
//				+ mDataInterfaceHash.size());
		for(DataInterface di : backupData)
		{
			/* must rebuild marker options because locality may have not been set in the memorized marker options.
			 * onGeocodeAddressTask actually updates the locality by changing the snippet on the marker rather 
			 * than regenerating a MarkerOptions.
			 */
			MarkerOptions mo = ((RequestData) di).buildMarkerOptions(mMapFrag.getActivity().getApplicationContext());
			Marker myRestoredRequestMarker = mMapFrag.getMap().addMarker(mo);
			di.setMarker(myRestoredRequestMarker); /* replace old marker with new one */
			myRestoredRequestMarker.showInfoWindow();
			/* restore data in the hash now! */
			mDataInterfaceHash.put(myRestoredRequestMarker.getId(), di);


		}
	}

	private ArrayList<DataInterface> mSaveYetUnpublishedMyRequestData() 
	{
		ArrayList<DataInterface> myRequestsYetUnpublished = new ArrayList<DataInterface>();
		for(DataInterface di : mDataInterfaceHash.values())
		{
			if(di.getType() == DataInterface.TYPE_REQUEST && di.isWritable() && !di.isPublished())
			{
				myRequestsYetUnpublished.add(di);
			}
//			else
//				Log.e("mSaveYetUnpublishedMyRequestData", "not saving " + di.getLocality() +  " id " + di.getMarker().getId() +
//						"w " + di.isWritable()  + ", pub"  + di.isPublished());
		}
		return myRequestsYetUnpublished;
	}

	@Override
	public void onMapClick(LatLng arg0) 
	{
		if(this.isInfoWindowVisible())
			this.hideInfoWindow();
		else
			mRemoveUnpublishedMyRequestMarkers();

	}

	private void mRemoveUnpublishedMyRequestMarkers() 
	{
		for(Iterator<Map.Entry<String, DataInterface>> it = mDataInterfaceHash.entrySet().iterator(); it.hasNext(); ) 
		{
			Map.Entry<String, DataInterface> entry = it.next();

			DataInterface di = entry.getValue();
			if(di.getType() == DataInterface.TYPE_REQUEST && !di.isPublished() && di.isWritable())
			{
				di.getMarker().remove();
				it.remove();
			}
		}
	}

}