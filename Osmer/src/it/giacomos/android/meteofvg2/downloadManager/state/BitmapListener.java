package it.giacomos.android.meteofvg2.downloadManager.state;

import java.net.URL;

import it.giacomos.android.meteofvg2.BitmapType;
import android.graphics.Bitmap;
import android.os.AsyncTask;

public interface BitmapListener {
	public void onBitmapUpdate(Bitmap bmp, BitmapType bt, String errorMessage, AsyncTask<URL, Integer, Bitmap> task);
}
