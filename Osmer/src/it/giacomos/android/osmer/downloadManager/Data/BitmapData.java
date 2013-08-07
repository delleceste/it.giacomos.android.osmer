package it.giacomos.android.osmer.downloadManager.Data;

import android.graphics.Bitmap;

public class BitmapData 
{
	public boolean isValid()
	{
		return error == null || error.isEmpty();
	}
	
	public BitmapData(Bitmap bmp)
	{
		bitmap = bmp;
		error = "";
		fromCache = false;
	}
	
	public BitmapData(Bitmap bmp, String err)
	{
		bitmap = bmp;
		error = err;
		fromCache = false;
	}
	
	public Bitmap bitmap;
	public String error;
	public boolean fromCache;
}
