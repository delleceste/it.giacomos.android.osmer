package it.giacomos.android.osmer.widgets.map;

import android.graphics.Bitmap;

public interface WebcamOverlayChangeListener 
{
	public abstract void onWebcamBitmapChanged(Bitmap bmp);
	
	public abstract void onWebcamBitmapBytesChanged(byte [] bmpBytes);
	
	public abstract void onWebcamInfoWindowImageClicked();
	
	public abstract void onWebcamErrorMessageChanged(String message);
	
	public abstract void onWebcamBitmapTaskCanceled(String url);
	
	public abstract void onWebcamMessageChanged(int resId);
}
