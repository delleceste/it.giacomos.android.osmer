package it.giacomos.android.osmer.downloadManager.Data;

import it.giacomos.android.osmer.ViewType;

public interface DataPoolTextListener 
{
	public abstract void onTextChanged(String txt, ViewType t);
	
	public abstract void onTextError(String error, ViewType t);
}
