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
import it.giacomos.android.osmer.R;
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
		mContext.registerReceiver(mNetworkStatusMonitor, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		mReportUpdaterListener = rul;
		mLastReportUpdatedAt = 0;
		mReportUpdateTask = null;
	}
	
	public void clear()
	{
		mContext.unregisterReceiver(mNetworkStatusMonitor);
		mLocationClient.disconnect();
		/* cancel thread if running */
		if(mReportUpdateTask != null)
			mReportUpdateTask.cancel(false);
	}
	
	public void update(boolean force)
	{
		Toast.makeText(mContext, "ReportUpdater.update: activating location client", Toast.LENGTH_SHORT).show();
		if(mNetworkStatusMonitor.isConnected())
			mLocationClient.connect();
		else
			Toast.makeText(mContext, R.string.reportNeedToBeOnline, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onNetworkBecomesAvailable() 
	{
		Toast.makeText(mContext, "ReportUpdater.onNetworkBecomesAvailable: net avail. connecting location cli", Toast.LENGTH_SHORT).show();
		if(!mLocationClient.isConnected())
			mLocationClient.connect();
		else
			onConnected(null);
	}

	@Override
	public void onNetworkBecomesUnavailable() 
	{
		Toast.makeText(mContext, "ReportUpdater.onNetworkBecomesUnavailable: disconnecting location cli", Toast.LENGTH_SHORT).show();
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
		Toast.makeText(mContext, "onConnected: location avail. would start update", Toast.LENGTH_SHORT).show();
		Log.e("ReportUpdater.onConnected", "thread "+ Thread.currentThread());
		String deviceId = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);
		if(mReportUpdateTask != null && mReportUpdateTask.getStatus() != AsyncTask.Status.FINISHED)
		{
			Log.e("%%%%%%%%%%%%%%%%%%%%%% CANCEL IN onConnected", "cancelling task was " + mReportUpdateTask.getStatus());
			mReportUpdateTask.cancel(false);
		}
		mReportUpdateTask = new ReportUpdateTask(this, mLocationClient.getLastLocation(), deviceId);
		mReportUpdateTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Urls().getReportUrl());
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
			mReportUpdaterListener.onReportUpdateError(data);
		
		mLocationClient.disconnect();
	}
	
	

}
