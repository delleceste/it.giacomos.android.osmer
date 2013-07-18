package it.giacomos.android.osmer.downloadManager.state;

import java.net.URL;

import android.os.AsyncTask;
import it.giacomos.android.osmer.ViewType;

public interface TextListener 
{
	/* task: if the listener keeps a list of (active) tasks, by passing a reference to the 
	 * current task, it is possible to allow the listener to remove the completed task
	 * from its list.
	 */
	public void onTextUpdate(String bmp, ViewType st, String errorMessage, AsyncTask<URL, Integer, String> task);
}
