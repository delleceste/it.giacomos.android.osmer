package it.giacomos.android.osmer.pro.network;

public interface NetworkStatusMonitorListener {
	void onNetworkBecomesAvailable();
	void onNetworkBecomesUnavailable();

}
