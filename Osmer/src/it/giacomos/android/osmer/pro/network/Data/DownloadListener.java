package it.giacomos.android.osmer.pro.network.Data;

import it.giacomos.android.osmer.pro.network.state.BitmapType;
import it.giacomos.android.osmer.pro.network.state.ViewType;
import android.graphics.Bitmap;

public interface DownloadListener 
{
	public void onBitmapUpdate(Bitmap bmp, BitmapType t);
	public void onBitmapUpdateError(BitmapType t, String error);

	public void onTextUpdate(String text, ViewType t);
	public void onTextUpdateError(ViewType t, String error);
	public void onBitmapBytesUpdate(byte[] bytes, BitmapType bt);
	public void onTextBytesUpdate(byte[] bytes, ViewType vt);
}