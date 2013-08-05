package it.giacomos.android.osmer.downloadManager.Data;

import android.graphics.Bitmap;

public interface DataPoolBitmapListener {
	
	public abstract void onBitmapChanged(Bitmap bmp);
	
	public abstract void onBitmapError(String error);

}
