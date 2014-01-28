package it.giacomos.android.osmer.pro.widgets.map.report.network;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.Toast;
import it.giacomos.android.osmer.pro.R;
import it.giacomos.android.osmer.pro.network.NetworkStatusMonitor;
import it.giacomos.android.osmer.pro.network.NetworkStatusMonitorListener;
import it.giacomos.android.osmer.pro.network.Data.DataPoolCacheUtils;
import it.giacomos.android.osmer.pro.network.state.Urls;
import it.giacomos.android.osmer.pro.network.state.ViewType;

public class ReportUpdater   
implements NetworkStatusMonitorListener,
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener,
ReportUpdateTaskListener
{
	private static final long DOWNLOAD_REPORT_OLD_TIMEOUT = 10000;

	private Context mContext;
	private ReportUpdaterListener mReportUpdaterListener;
	private LocationClient mLocationClient;
	private NetworkStatusMonitor mNetworkStatusMonitor;
	private long mLastReportUpdatedAt;
	private ReportUpdateTask mReportUpdateTask;

	public ReportUpdater(Context ctx, ReportUpdaterListener rul)
	{
		mContext = ctx;
		mLocationClient = new LocationClient(ctx, this, this);
		mNetworkStatusMonitor = new NetworkStatusMonitor(this);
		/* when the map switches mode, a new ReportUpdater is created, and it must be registered.
		 * onResume is not called when map switches mode. Instead, when the activity is paused, 
		 * the onPause method of ReportUpdater unregisters from NetworkStatusMonitor.
		 */
		mContext.registerReceiver(mNetworkStatusMonitor, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		mReportUpdaterListener = rul;
		mLastReportUpdatedAt = 0;
		mReportUpdateTask = null;
		DataPoolCacheUtils dpcu = new DataPoolCacheUtils();
		onReportUpdateTaskComplete(false, dpcu.loadFromStorage(ViewType.REPORT, ctx));
	}


	public void onPause() 
	{
		/* when the activity is paused, disconnect from network status monitor and 
		 * from location client. We can also cancel the report update task, since 
		 * when the activity is resumed an update is performed.
		 */
		Log.e("ReportUpdater.onPause", "calling clear()");
		clear();
	}
	
	public void onResume()
	{
		Log.e("onResume", "registering network status monitor in onResume");
		/* must (re)connect with the network status monitor in order to be notified when the network 
		 * goes up or down
		 */
		mContext.registerReceiver(mNetworkStatusMonitor, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
	}
	
	public void clear()
	{
		Log.e("ReportUpdater.clear()", "unregistering network status monitor receiver and location client");
		try
		{
			mContext.unregisterReceiver(mNetworkStatusMonitor);
		}
		catch(IllegalArgumentException iae)
		{
			/* when the activity is destroyed, onPause calls clear and then onDestroy in map fragment
			 * calls clear() again. On the other side, we need to call clear even if only paused and 
			 * not destroyed.
			 */
		}
		mLocationClient.disconnect();
		/* cancel thread if running */
		if(mReportUpdateTask != null)
			mReportUpdateTask.cancel(false);
	}

	public void update(boolean force)
	{
		if((System.currentTimeMillis() - mLastReportUpdatedAt > DOWNLOAD_REPORT_OLD_TIMEOUT) || force)
		{
			/* if a task is already running or about to run, do not do anything, because an update is on
			 * the way.
			 */
			if(mReportUpdateTask != null && (mReportUpdateTask.getStatus() == AsyncTask.Status.PENDING 
					|| mReportUpdateTask.getStatus() == AsyncTask.Status.RUNNING))
			{
				Log.e("update", "reportUpdateTask is running or pending");
				return;
			}

			if(mNetworkStatusMonitor.isConnected())
			{
				mLocationClient.disconnect();
				mLocationClient.connect();
			}
			else /* offline */
				Toast.makeText(mContext, R.string.reportNeedToBeOnline, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onNetworkBecomesAvailable() 
	{
		if(mLocationClient.isConnected())
			onConnected(null);
		else if(mLocationClient.isConnecting())
			return; /* wait for onConnected() */
		else
			mLocationClient.connect();
	}

	@Override
	public void onNetworkBecomesUnavailable() 
	{
		mLocationClient.disconnect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) 
	{
		Toast.makeText(mContext, "ReportUpdater: failed to connect to "
				+ "location service: " + arg0.toString(), Toast.LENGTH_SHORT).show();
	}

	@Override
	/** Called when the location client is connected. It is possible to obtain the last
	 * known location. So we start here the report update task to download the reports.
	 */
	public void onConnected(Bundle arg0) 
	{
		/* if a task is already running or about to run, do not do anything, because an update is on
		 * the way.
		 */
		if(mReportUpdateTask != null && (mReportUpdateTask.getStatus() == AsyncTask.Status.PENDING 
				|| mReportUpdateTask.getStatus() == AsyncTask.Status.RUNNING))
			return;

		String deviceId = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);
		
		if(mReportUpdateTask != null && mReportUpdateTask.getStatus() != AsyncTask.Status.FINISHED)
			mReportUpdateTask.cancel(false);
		
		mReportUpdateTask = new ReportUpdateTask(this, mLocationClient.getLastLocation(), deviceId);
		/* "http://www.giacomos.it/meteo.fvg/get_report.php" */
		mReportUpdateTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Urls().getReportUrl());

		/* no more interested in location updates, the task has been starting with the last known
		 * location.
		 */
		mLocationClient.disconnect();
	}

	@Override
	/** Called when ReportUpdater is disconnected from the location client
	 * Nothing to do.
	 */
	public void onDisconnected() 
	{
		Toast.makeText(mContext, "ReportUpdater.onDisconnected", Toast.LENGTH_SHORT).show();
	}

	/** Evaluate if the report is old 
	 * 
	 * @return
	 */
	public boolean reportUpToDate() 
	{	
		return (System.currentTimeMillis() - mLastReportUpdatedAt) < DOWNLOAD_REPORT_OLD_TIMEOUT;
	}

	@Override
	public void onReportUpdateTaskComplete(boolean error, String data) 
	{
		if(!error)
		{
			/* call onReportUpdateDone on ReportOverlay */
			mReportUpdaterListener.onReportUpdateDone(data);
			Log.e("ReportUpdater.onPostExecute", "saving to cache");
			/* save data into cache */
			DataPoolCacheUtils dataPoolCUtils = new DataPoolCacheUtils();
			dataPoolCUtils.saveToStorage(data.getBytes(), ViewType.REPORT, mContext);
			mLastReportUpdatedAt = System.currentTimeMillis();
		}
		else
			mReportUpdaterListener.onReportUpdateError(mReportUpdateTask.getError());
	}




}
