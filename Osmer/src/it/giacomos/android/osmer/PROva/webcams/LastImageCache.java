package it.giacomos.android.osmer.PROva.webcams;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;

public class LastImageCache 
{
	public static final String CACHE_IMAGE_FILENAME = "last_webcam_image.jpeg";
	
	public boolean save(byte[] bmpBytes, Context ctx)
	{
		FileOutputStream fos;
		try {
			fos = ctx.openFileOutput(CACHE_IMAGE_FILENAME, Context.MODE_WORLD_READABLE);
			fos.write(bmpBytes);
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
