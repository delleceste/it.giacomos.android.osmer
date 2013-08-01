package it.giacomos.android.meteofvg2.webcams;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;

public class LastImageCache 
{
	public static final String CACHE_IMAGE_FILENAME = "last_webcam_image.jpeg";
	
	public boolean save(Bitmap bmp, Context ctx)
	{
		FileOutputStream fos;
		try {
			fos = ctx.openFileOutput(CACHE_IMAGE_FILENAME, Context.MODE_WORLD_READABLE);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);	
			fos.close();
			return true;
		} 
		catch (FileNotFoundException e) {
			/* nada que hacer */
		}
		catch (IOException e) {
			
		}
		return false;
	}
	
	
}
