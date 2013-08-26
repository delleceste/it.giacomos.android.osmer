package it.giacomos.android.osmer.pro.downloadManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkStatusMonitor extends BroadcastReceiver {
	public NetworkStatusMonitor(NetworkStatusMonitorListener networkStatusMonitorListener)
	{
		m_networkStatusMonitorListener = networkStatusMonitorListener;
	}
	
	public  void onReceive(Context context, Intent intent)
	{
		boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
		String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
		boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);

		NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

		if(currentNetworkInfo.isConnected())
			m_networkStatusMonitorListener.onNetworkBecomesAvailable();
		else 
			m_networkStatusMonitorListener.onNetworkBecomesUnavailable();
		
		// do application-specific task(s) based on the current network state, such
		// as enabling queuing of HTTP requests when currentNetworkInfo is connected etc.
	}

	public boolean isConnected() { return m_isConnected; }
	
	private NetworkStatusMonitorListener m_networkStatusMonitorListener;
	
	private boolean m_isConnected;

}
