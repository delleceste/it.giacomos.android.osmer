package it.giacomos.android.osmer.downloadManager;

import android.graphics.Bitmap;
import it.giacomos.android.osmer.BitmapType;
import it.giacomos.android.osmer.StringType;

public interface DownloadManagerUpdateListener {
	
	public void onTextUpdate(String txt, StringType t, String errorMessage);
	public void onBitmapUpdate(Bitmap bmp, BitmapType t, String errorMessage);
	public void onProgressUpdate(int step, int total);
	public void onDownloadStart(DownloadReason reason);
	public void onStateChanged(long oldState, long state);
}
