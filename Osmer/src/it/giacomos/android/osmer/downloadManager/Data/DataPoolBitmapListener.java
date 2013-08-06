package it.giacomos.android.osmer.downloadManager.Data;

import it.giacomos.android.osmer.BitmapType;
import android.graphics.Bitmap;

public interface DataPoolBitmapListener {
	
	public abstract void onBitmapChanged(Bitmap bmp, BitmapType t);
	
	public abstract void onBitmapError(String error, BitmapType t);

}
