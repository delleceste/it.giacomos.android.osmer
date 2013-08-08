package it.giacomos.android.osmer.network;

public interface NetworkStatusMonitorListener {
	void onNetworkBecomesAvailable();
	void onNetworkBecomesUnavailable();

}
