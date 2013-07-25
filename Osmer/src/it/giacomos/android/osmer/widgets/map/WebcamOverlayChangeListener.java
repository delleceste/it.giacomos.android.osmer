package it.giacomos.android.osmer.widgets.map;

import android.graphics.Bitmap;

public interface WebcamOverlayChangeListener 
{
	public abstract void onBitmapChanged(Bitmap bmp);
	
	public abstract void onInfoWindowImageClicked();
	
	public abstract void onErrorMessageChanged(String message);
	
	public abstract void onBitmapTaskCanceled(String url);
	
	public abstract void onMessageChanged(int resId);
}
