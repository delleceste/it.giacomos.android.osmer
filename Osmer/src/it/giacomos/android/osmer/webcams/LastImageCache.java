package it.giacomos.android.osmer.webcams;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class LastImageCache 
{
	public static final String CACHE_IMAGE_FILENAME = "last_webcam_image.jpeg";
	
	public boolean save(byte[] bmpBytes, Context ctx)
	{
		FileOutputStream fos;
		try {
			fos = ctx.openFileOutput(CACHE_IMAGE_FILENAME, Context.MODE_PRIVATE);
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
