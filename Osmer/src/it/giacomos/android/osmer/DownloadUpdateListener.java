package it.giacomos.android.osmer;

import it.giacomos.android.osmer.downloadManager.DownloadReason;
import android.graphics.Bitmap;

public interface DownloadUpdateListener {
	
	public void onDownloadProgressUpdate(int step, int total);
	
	public void onBitmapUpdate(Bitmap bmp, BitmapType t);
	public void onBitmapUpdateError(BitmapType t, String error);

	public void onTextUpdate(String text, ViewType t);
	public void onTextUpdateError(ViewType t, String error);
	
	public void onDownloadStart(DownloadReason reason);
	
	public void networkStatusChanged(boolean online);
	
	public void onStateChanged(long previousState, long state);
}
