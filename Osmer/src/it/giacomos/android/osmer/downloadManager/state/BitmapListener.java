package it.giacomos.android.osmer.downloadManager.state;

import it.giacomos.android.osmer.BitmapType;
import android.graphics.Bitmap;

public interface BitmapListener {
	public void onBitmapUpdate(Bitmap bmp, BitmapType bt, String errorMessage);
}
