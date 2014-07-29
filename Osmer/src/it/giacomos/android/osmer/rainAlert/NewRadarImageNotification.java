package it.giacomos.android.osmer.rainAlert;

import android.util.Log;

public class NewRadarImageNotification implements SyncImagesListener 
{

	private String mFilename;
	
	public String getFilename()
	{
		return mFilename;
	}
	
	public NewRadarImageNotification(String desc)
	{
		mFilename = desc.replace("I:", "");
	}

	@Override
	public void onImagesSynced(String[] filepaths) 
	{
		if(filepaths != null)
		{
			Log.e("ReportDataService.onImagesSynced", " saved images " + filepaths[0] + " and " + filepaths[1]);
		}
		else
			Log.e("ReportDataService.onImagesSynced", " failed to saved images!");	
		
	}
	
}
