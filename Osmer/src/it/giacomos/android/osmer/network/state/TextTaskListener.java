package it.giacomos.android.osmer.network.state;

import android.os.AsyncTask;

import java.net.URL;

public interface TextTaskListener 
{
	/* task: if the listener keeps a list of (active) tasks, by passing a reference to the 
	 * current task, it is possible to allow the listener to remove the completed task
	 * from its list.
	 */
	public void onTextUpdate(String text, ViewType st, String errorMessage, AsyncTask<URL, Integer, String> task);
	public void onTextBytesUpdate(byte [] bytes, ViewType vt);
}
