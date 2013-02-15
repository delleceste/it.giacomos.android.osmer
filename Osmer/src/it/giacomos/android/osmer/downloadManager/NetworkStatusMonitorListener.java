package it.giacomos.android.osmer.downloadManager;

public interface NetworkStatusMonitorListener {
	void onNetworkBecomesAvailable();
	void onNetworkBecomesUnavailable();

}
