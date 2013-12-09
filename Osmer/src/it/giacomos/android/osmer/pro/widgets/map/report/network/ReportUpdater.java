package it.giacomos.android.osmer.pro.widgets.map.report.network;

import java.net.URL;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import it.giacomos.android.osmer.pro.network.DownloadStatus;
import it.giacomos.android.osmer.pro.network.NetworkStatusMonitor;
import it.giacomos.android.osmer.pro.network.NetworkStatusMonitorListener;
import it.giacomos.android.osmer.pro.network.Data.DataPoolCacheUtils;
import it.giacomos.android.osmer.pro.network.state.TextTaskListener;
import it.giacomos.android.osmer.pro.network.state.ViewType;

public class ReportUpdater extends AsyncTask<String, Integer, String>   
implements NetworkStatusMonitorListener, TextTaskListener,
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener
{
	private Context mContext;
	
	private LocationClient mLocationClient;
	private NetworkStatusMonitor mNetworkStatusMonitor;

	public ReportUpdater(Context ctx)
	{
		mContext = ctx;
		mLocationClient = new LocationClient(ctx, this, this);
		mNetworkStatusMonitor = new NetworkStatusMonitor(this);
	}
	
	public void dismiss()
	{
		if(mLocationClient != null)
			mLocationClient.disconnect();
		mContext.unregisterReceiver(mNetworkStatusMonitor);
	}
	
	public void update(boolean force)
	{
		Toast.makeText(mContext, "ReportUpdater.onNetworkBecomesUnavailable: disconnecting location cli", Toast.LENGTH_LONG).show();
		mContext.registerReceiver(mNetworkStatusMonitor, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
	}
	
	@Override
	public void onNetworkBecomesAvailable() 
	{
		Toast.makeText(mContext, "ReportUpdater.onNetworkBecomesAvailable: net avail. connecting location cli", Toast.LENGTH_LONG).show();
		mLocationClient.connect();
	}

	@Override
	public void onNetworkBecomesUnavailable() 
	{
		Toast.makeText(mContext, "ReportUpdater.onNetworkBecomesUnavailable: disconnecting location cli", Toast.LENGTH_LONG).show();
		mLocationClient.disconnect();
	}

	@Override
	protected String doInBackground(String... urls) 
	{
		
		return null;
	}

	@Override
	public void onTextUpdate(String text, ViewType vt, String errorMessage,
			AsyncTask<URL, Integer, String> task) 
	{
		if(vt == ViewType.REPORT)
		{
			Log.e("Online.onTextUpdate", "marking report is updataed now");
			DownloadStatus.Instance().setReportUpdatedNow();
		}
	}

	@Override
	public void onTextBytesUpdate(byte[] bytes, ViewType vt) 
	{
		Log.e("ReportUpdater.onTextBytesUpdate", "saving " + vt);
		DataPoolCacheUtils dataPoolCUtils = new DataPoolCacheUtils();
		dataPoolCUtils.saveToStorage(bytes, vt, mContext);
		
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle arg0) 
	{
		Toast.makeText(mContext, "onConnected: location avail. would start update", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onDisconnected() 
	{
		Toast.makeText(mContext, "ReportUpdater.onDisconnected", Toast.LENGTH_LONG).show();
	}

}
