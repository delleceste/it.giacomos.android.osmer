package it.giacomos.android.osmer.pro.widgets.map.animation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class FileHelper 
{
	private String mErrorMessage;
	
	public FileHelper()
	{

	}

	public String getErrorMessage()
	{
		return mErrorMessage;
	}
	
	public Bitmap decodeImage(String fileName, String externalStorageDirPath)
	{
		Bitmap bmp;
		bmp = BitmapFactory.decodeFile(externalStorageDirPath + "/" + fileName);
		return bmp;
	}
	
	public boolean isExternalFileSystemDirReadableWritable()
	{
		boolean externalStorageAvailable = false;
		boolean externalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			externalStorageAvailable = externalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			externalStorageAvailable = true;
			externalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other states, but all we need
			//  to know is we can neither read nor write
			externalStorageAvailable = externalStorageWriteable = false;
		}

		return externalStorageAvailable && externalStorageWriteable;
	}

	public boolean exists(String fileName, String externalStorageDirPath)
	{
		File file = new File(externalStorageDirPath, fileName);
		return file.exists();
	}
	
	public boolean storeRadarImage(String fileName, byte[] image, String externalStorageDirPath)
	{
		File file = new File(externalStorageDirPath, fileName);
		try
		{
			FileOutputStream fos;
			fos = new FileOutputStream(file);
			fos.write(image);
			fos.close();
			return true;
		} 
		catch (FileNotFoundException e) {
			mErrorMessage = e.getLocalizedMessage();
		}
		catch (IOException e) {
			mErrorMessage = e.getLocalizedMessage();
		}
		return false;
		
	}

	/** Removes from the directory on the external filesystem where the application can 
	 *  place persistent files it owns the files that start with "radar-" and whose names are not
	 *  among the needed files.
	 *  
	 * @param  needed the list of _needed_ files (not to remove)
	 * @param  ctx
	 * @return the number of successfully deleted files
	 */
	public int removeUnneededFiles(ArrayList<String> needed, String externalStorageDirPath)
	{
		boolean deleted;
		int removed = 0;
		File dir = new File(externalStorageDirPath);
		File [] files = dir.listFiles();
		for(File file : files)
		{
			String fName = file.getName();
			if(fName.startsWith("radar-"))
			{
				if(!needed.contains(fName))
				{
					deleted = file.delete();
					if(deleted)
					{
						Log.e("FileHelper.removeUnneededFiles", "successfully removed unneeded file " + fName);
						removed++;
					}
					else
						Log.e("FileHelper.removeUnneededFiles", "failed to delete unneeded file " + fName);
				}
				else
					Log.e("FileHelper.removeUnneededFiles", "the file " + fName + " is still needed");
			}
		}
		return removed;
	}

}
