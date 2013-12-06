package it.giacomos.android.osmer.pro.widgets.map.report;

import java.util.ArrayList;

import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
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

public class ReportOverlay implements OOverlayInterface, DataPoolTextListener, 
ReportOverlayTaskListener, 
OnMarkerClickListener, OnMapLongClickListener, OnInfoWindowClickListener, 
OnMarkerDragListener, GeocodeAddressUpdateListener
{

	private OMapFragment mMapFrag;
	private ReportOverlayTask mReportOverlayTask;
	private ArrayList<Marker> mMarkers;
	private MapBaloonInfoWindowAdapter mMapBaloonInfoWindowAdapter;
	private MyReportRequestListener mReportRequestListener;
	private String mMyRequestMarkerLocality;
	private GeocodeAddressTask mGeocodeAddressTask;
	private Marker mMyRequestMarker, mBuddyRequestMarker;

	public ReportOverlay(OMapFragment oMapFragment) 
	{
		mMapFrag = oMapFragment;
		mReportRequestListener = null;
		mReportOverlayTask = null;
		mMapBaloonInfoWindowAdapter = new MapBaloonInfoWindowAdapter(mMapFrag.getActivity());
		mGeocodeAddressTask = null;
		mMyRequestMarkerLocality = "-";
		mMyRequestMarker = null;
		mBuddyRequestMarker = null;
		mMapFrag.getMap().setInfoWindowAdapter(mMapBaloonInfoWindowAdapter);
		mMarkers = new ArrayList<Marker>();
		/* register as DataPool text listener */
		/* get data pool reference */
		DataPool dataPool = ((OsmerActivity) mMapFrag.getActivity()).getDataPool();
		DataPoolCacheUtils dataCacheUtils = new DataPoolCacheUtils(); /* cache utils reference */
		if(!dataPool.isTextValid(ViewType.REPORT)) /* if data pool hasn't got up to date text yet */
		{
			String text = dataCacheUtils.loadFromStorage(ViewType.REPORT, mMapFrag.getActivity().getApplicationContext());
			this.onTextRefresh(text, ViewType.REPORT, true, true);
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
		mRemoveReportMarkers();
		if(mMyRequestMarker != null)
		{
			mSaveMyRequestMarkerPosition();
			mMyRequestMarker.remove();
		}
		if(mBuddyRequestMarker != null)
			mBuddyRequestMarker.remove();
	}

	private void mRemoveReportMarkers()
	{
		for(int i = 0; i < mMarkers.size(); i++)
			mMarkers.get(i).remove();
		mMarkers.clear();
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
	public void hideInfoWindow() {
		// TODO Auto-generated method stub
		for(int i = 0; i < mMarkers.size(); i++)
			mMarkers.get(i).hideInfoWindow();
		if(mBuddyRequestMarker != null)
			mBuddyRequestMarker.hideInfoWindow();
		if(mMyRequestMarker != null)
			mMyRequestMarker.hideInfoWindow();
	}

	@Override
	public boolean isInfoWindowVisible() {
		for(int i = 0; i < mMarkers.size(); i++)
			if(mMarkers.get(i).isInfoWindowShown())
				return true;
		if(mBuddyRequestMarker != null && mBuddyRequestMarker.isInfoWindowShown())
			return true;
		if(mMyRequestMarker != null && mMyRequestMarker.isInfoWindowShown())
			return true;
		return false;
	}

	@Override
	/** redraws all markers, the user report markers, my request marker and buddy request notification
	 *  marker, if present.
	 */
	public void onReportOverlayTaskFinished(ArrayList<MarkerOptions> markerOptionsArray) 
	{
		if(markerOptionsArray != null) /* the task may return null */
		{
			Log.e("onReportOverlayTaskFinished", "got " + markerOptionsArray.size());
			mRemoveReportMarkers();
			for(MarkerOptions markerOptions : markerOptionsArray)
			{
				mMarkers.add(mMapFrag.getMap().addMarker(markerOptions));
			}
		}
		mPlaceBuddyRequestNotificationMarkerIfPresent();
		mRestoreMyRequestMarker();
	}

	@Override
	public boolean onMarkerClick(Marker m) 
	{
		mMapBaloonInfoWindowAdapter.setTitle(m.getTitle());
		mMapBaloonInfoWindowAdapter.setText(m.getSnippet());
		return false;
	}

	@Override
	public void onTextRefresh(String txt, ViewType t, boolean fromCache, boolean textChanged) 
	{		
		Log.e("ReportOverlay.onTextChanged", "vtype" + t + " fromCache "  + fromCache + " : "  + txt + " textChanged " + textChanged);

		/* In this first implementation, let the markers be updated even if the text has not changed.
		 * When the task has been completed, the buddy request notification marker is drawn if pertinent,
		 * inside onReportOverlayTaskFinished().
		 */

		/* ok start processing data */
		ReportDataFactory reportDataFactory = new ReportDataFactory();
		ReportData[] reportDataList = reportDataFactory.parse(txt);
		mReportOverlayTask = new ReportOverlayTask(mMapFrag.getActivity().getApplicationContext(), this);
		mReportOverlayTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, reportDataList);
		
		if(reportDataList == null)
			Toast.makeText(mMapFrag.getActivity().getApplicationContext(), R.string.reportNoneAvailable, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onTextError(String error, ViewType t) {
		Toast.makeText(mMapFrag.getActivity(), error, Toast.LENGTH_LONG).show();

	}

	private void mPlaceBuddyRequestNotificationMarkerIfPresent()
	{
		/* in every case, update notification data marker, whose data is periodically updated by the background
		 * service and kept into a static singleton serviceData.
		 */
		ServiceSharedData serviceSharedData = ServiceSharedData.Instance();
		NotificationData lastNotificationData = serviceSharedData.getNotificationData(NotificationData.TYPE_REQUEST);
		if(lastNotificationData != null && !lastNotificationData.isConsumed())
		{
			MarkerOptions buddyRequestMarkerOptions = mBuildBuddyRequestMarker(lastNotificationData);
			mBuddyRequestMarker = mMapFrag.getMap().addMarker(buddyRequestMarkerOptions);
		}
	}

	private MarkerOptions mBuildBuddyRequestMarker(NotificationData notificationData)
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
		if(mMyRequestMarker == null)
		{
			mMyRequestMarker = mCreateMyRequestMarker(point);
			mStartGeocodeAddressTask(mMyRequestMarker);
			mMyRequestMarker.showInfoWindow();
		}
		else /* if click point is distant, remove previous marker */
		{
			Location newLoc = new Location("");
			Location prevPos = new Location("");
			newLoc.setLatitude(point.latitude);
			newLoc.setLongitude(point.longitude);
			prevPos.setLatitude(mMyRequestMarker.getPosition().latitude);
			prevPos.setLongitude(mMyRequestMarker.getPosition().longitude);
			if(newLoc.distanceTo(prevPos) > 5000)
				mRemoveMyRequestMarker();
		}

	}

	private Marker mCreateMyRequestMarker(LatLng point)
	{
		String snippet = mMapFrag.getString(R.string.reportTouchBaloonToRequest);
		MarkerOptions reqMarkerO = new MarkerOptions();
		reqMarkerO.position(point);
		reqMarkerO.title(mMapFrag.getString(R.string.reportRequest));
		reqMarkerO.snippet(snippet);
		reqMarkerO.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
		reqMarkerO.draggable(true);
		mMyRequestMarker = mMapFrag.getMap().addMarker(reqMarkerO);
		return mMyRequestMarker;
	}

	private void mRemoveMyRequestMarker()
	{
		if(mMyRequestMarker != null)
		{
			mMyRequestMarker.remove();
			mMyRequestMarker = null;
			Settings s = new Settings(mMapFrag.getActivity().getApplicationContext());
			s.saveMyReportRequestMarkerAttributes(-1.0, -1.0);
		}
	}

	private void mSaveMyRequestMarkerPosition()
	{
		Settings s = new Settings(mMapFrag.getActivity().getApplicationContext());
		if(mMyRequestMarker != null)
			s.saveMyReportRequestMarkerAttributes(mMyRequestMarker.getPosition().latitude, mMyRequestMarker.getPosition().longitude);
		Log.e("mSaveMyRequestMarkerPosition", "saving marker for request " + mMyRequestMarker);
	}

	private void mRestoreMyRequestMarker()
	{
		double lat, longi;
		Settings s = new Settings(mMapFrag.getActivity().getApplicationContext());
		lat = s.getMyReportRequestMarkerLatitude();
		longi = s.getMyReportRequestMarkerLongitude();
		if(lat > 0 && longi > 0)
		{
			Log.e("mRestoreReportRequestMarker", "restoring marker for request " + lat + " long " + longi);
			mCreateMyRequestMarker(new LatLng(lat, longi));
		}
	}

	public void setOnReportRequestListener(MyReportRequestListener rrl)
	{
		mReportRequestListener = rrl;
	}

	private void mStartGeocodeAddressTask(Marker marker)
	{
		mMyRequestMarkerLocality = "-"; /* reset to unknown */
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
		if(marker.getId().compareTo(mMyRequestMarker.getId()) == 0)
		{
			Log.e("onInfoWindowClick", "clicked position " + marker.getPosition().toString());
			mReportRequestListener.onMyReportRequestTriggered(marker.getPosition(), mMyRequestMarkerLocality);
			mMyRequestMarker.hideInfoWindow();
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
		if(mMyRequestMarker != null)
		{
			/* location got from GeocodeAddressTask is never null */
			String locality = locationInfo.locality;
			String address = locationInfo.address;
			String snippet = locality;
			if(!address.isEmpty())
				snippet += "\n" + address;
			snippet += "\n" + mMapFrag.getString(R.string.reportTouchBaloonToRequest);
			mMyRequestMarker.setSnippet(snippet);
			mMyRequestMarker.hideInfoWindow();
			mMyRequestMarker.showInfoWindow();
			mMyRequestMarkerLocality = locality;
			mReportRequestListener.onMyReportLocalityChanged(locality);
		}
	}

	public void onPostActionResult(boolean canceled, boolean error, String message, PostType postType) 
	{
		Log.e("onPostActionResult", " canceled " + canceled + " err " + error + " post type " + postType);
		if(postType == PostType.REQUEST)
		{
			if(canceled)
				mRemoveMyRequestMarker();
			else if(!error && mMyRequestMarker != null)
			{
				mMyRequestMarker.setTitle(mMapFrag.getActivity().getString(R.string.reportRequestCorrectlySent));
				mMyRequestMarker.showInfoWindow();
			}
		}
	}

}