package it.giacomos.android.osmer.network.Data;

import android.graphics.Bitmap;

import it.giacomos.android.osmer.network.state.BitmapType;
import it.giacomos.android.osmer.network.state.ViewType;

public interface DownloadListener 
{
	public void onBitmapUpdate(Bitmap bmp, BitmapType t);
	public void onBitmapUpdateError(BitmapType t, String error);

	public void onTextUpdate(String text, ViewType t);
	public void onTextUpdateError(ViewType t, String error);
	public void onBitmapBytesUpdate(byte[] bytes, BitmapType bt);
	public void onTextBytesUpdate(byte[] bytes, ViewType vt);
}
