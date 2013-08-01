package it.giacomos.android.meteofvg2.downloadManager;

import android.graphics.Bitmap;
import it.giacomos.android.meteofvg2.BitmapType;
import it.giacomos.android.meteofvg2.ViewType;

public interface DownloadManagerUpdateListener {
	
	public void onTextUpdate(String txt, ViewType t, String errorMessage);
	public void onBitmapUpdate(Bitmap bmp, BitmapType t, String errorMessage);
	public void onProgressUpdate(int step, int total);
	public void onDownloadStart(DownloadReason reason);
	public void onStateChanged(long oldState, long state);
}
