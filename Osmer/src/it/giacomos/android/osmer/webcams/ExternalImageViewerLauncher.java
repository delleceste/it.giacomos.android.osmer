package it.giacomos.android.osmer.webcams;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class ExternalImageViewerLauncher 
{
	
	public ExternalImageViewerLauncher(Context ctx)
	{
	 	/* Context.getFilesDir()
	 	 * Returns the absolute path to the directory on the filesystem where files created with openFileOutput(String, int) are stored.
	 	 */
		String filename = "file://" + ctx.getFilesDir().getAbsolutePath();
		filename += "/" + LastImageCache.CACHE_IMAGE_FILENAME;
		Log.i("ExternalImageViewerLauncher", "viewing " + filename);
		Intent intent = new Intent(); 
		
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse(filename), "image/*"); 
		ctx.startActivity(intent);

	}

}
