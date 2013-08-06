package it.giacomos.android.osmer.downloadManager.Data;

public class StringData 
{
	public boolean isValid()
	{
		return error == null || error.isEmpty();
	}
	
	public StringData(String str)
	{
		text = str;
		error = "";
	}
	
	public StringData(String str, String err)
	{
		text = str;
		error = err;
	}
	
	public boolean equals(StringData other)
	{
		return other != null && other.text.equals(this.text) && other.error.equals(this.error);
	}
	
	public String text;
	public String error;
}
