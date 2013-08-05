package it.giacomos.android.osmer.downloadManager.Data;

import android.graphics.Bitmap;

public class BitmapData 
{
	public boolean isValid()
	{
		return error.isEmpty();
	}
	
	public BitmapData(Bitmap bmp)
	{
		bitmap = bmp;
	}
	
	public BitmapData(Bitmap bmp, String err)
	{
		bitmap = bmp;
		error = err;
	}
	
	public Bitmap bitmap;
	public String error;
}
