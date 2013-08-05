package it.giacomos.android.osmer.downloadManager.Data;

public interface DataPoolTextListener 
{
	public abstract void onTextChanged(String txt);
	
	public abstract void onTextError(String error);
}
