package it.giacomos.android.osmer.downloadManager.state;

import java.net.URL;

import it.giacomos.android.osmer.BitmapType;
import android.graphics.Bitmap;
import android.os.AsyncTask;

public interface BitmapListener {
	public void onBitmapUpdate(Bitmap bmp, BitmapType bt, String errorMessage, AsyncTask<URL, Integer, Bitmap> task);
}
