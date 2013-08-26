package it.giacomos.android.osmer.pro.downloadManager;

public interface NetworkStatusMonitorListener {
	void onNetworkBecomesAvailable();
	void onNetworkBecomesUnavailable();

}
