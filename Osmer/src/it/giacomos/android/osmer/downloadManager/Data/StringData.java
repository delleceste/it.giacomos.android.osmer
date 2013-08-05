package it.giacomos.android.osmer.downloadManager.Data;

public class StringData 
{
	public boolean isValid()
	{
		return error.isEmpty();
	}
	
	public StringData(String str)
	{
		text = str;
	}
	
	public StringData(String str, String err)
	{
		text = str;
		error = err;
	}
	
	public String text;
	public String error;
}
