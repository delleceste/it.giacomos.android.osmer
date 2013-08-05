package it.giacomos.android.osmer.downloadManager.Data;

import it.giacomos.android.osmer.BitmapType;
import it.giacomos.android.osmer.ViewType;
import android.graphics.Bitmap;

public interface DownloadListener 
{
	public void onBitmapUpdate(Bitmap bmp, BitmapType t);
	public void onBitmapUpdateError(BitmapType t, String error);

	public void onTextUpdate(String text, ViewType t);
	public void onTextUpdateError(ViewType t, String error);
}
