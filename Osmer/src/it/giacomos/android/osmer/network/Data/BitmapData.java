package it.giacomos.android.osmer.network.Data;

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
	
	public boolean equals(BitmapData other)
	{
		if(other == null)
			return false;
		
		if(this.bitmap == null)
			return other.bitmap == null && error == other.error && fromCache == other.fromCache;
		
		if(other.bitmap == null)
			return this.bitmap == null && error == other.error && fromCache == other.fromCache;
		
		return other.fromCache == this.fromCache && 
				other.bitmap.sameAs(this.bitmap) && 
				this.error == other.error;
	}
	
	public Bitmap bitmap;
	public String error;
	public boolean fromCache;
}
