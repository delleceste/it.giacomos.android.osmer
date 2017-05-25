package it.giacomos.android.osmer.network.state;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.net.URL;

public interface BitmapTaskListener {
	public void onBitmapUpdate(Bitmap bmp, BitmapType bt, String errorMessage, AsyncTask<URL, Integer, Bitmap> task);	
	public void onBitmapBytesUpdate(byte[] mBitmapBytes, BitmapType bt);
}
