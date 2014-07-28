package it.giacomos.android.osmer.rainAlert;

public class NewRadarImageNotification {

	private String mFilename;
	
	public String getFilename()
	{
		return mFilename;
	}
	
	public NewRadarImageNotification(String desc)
	{
		mFilename = desc.replace("I:", "");
	}
	
}
