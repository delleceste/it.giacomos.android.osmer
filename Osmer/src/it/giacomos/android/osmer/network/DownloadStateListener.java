package it.giacomos.android.osmer.network;

public interface DownloadStateListener {
	
	public void onDownloadProgressUpdate(int step, int total);
	
	public void onDownloadStart(DownloadReason reason);
	
	public void networkStatusChanged(boolean online);
	
	public void onStateChanged(long previousState, long state);
}
