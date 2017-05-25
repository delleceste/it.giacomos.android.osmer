package it.giacomos.android.osmer.trial;

import android.os.AsyncTask;
import android.util.Log;

import it.giacomos.android.osmer.network.HttpPostParametrizer;
import it.giacomos.android.osmer.network.HttpWriteRead;

public class ExpirationCheckTask extends AsyncTask<String, Integer, String> 
{
	private static final String CLI = "afe098388uytreiguthpgit1900lksjd";
	private String mErrorMsg;
	private ExpirationCheckTaskListener mExpirationCheckTaskListener;
	String mAndroidId;
	
	public ExpirationCheckTask(ExpirationCheckTaskListener expirationCheckTaskListener,
			String androidId)
	{
		super();
		mExpirationCheckTaskListener = expirationCheckTaskListener;
		mAndroidId = androidId;
	}
	
	@Override
	public void onPostExecute(String doc)
	{
		/* boolean success, string doc */
		mExpirationCheckTaskListener.onExpirationCheckTaskComplete(mErrorMsg.isEmpty(), doc);
	}
	
	@Override
	public void onCancelled(String doc)
	{
		
	}
	
	public String getError()
	{
		return mErrorMsg;
	}
	
	@Override
	protected String doInBackground(String... urls) 
	{
		String document = "";
		mErrorMsg = "";
		
		
		HttpPostParametrizer parametrizer = new HttpPostParametrizer();
        parametrizer.add("cli", CLI);
		parametrizer.add("d", mAndroidId);
		
		String params = parametrizer.toString();
		HttpWriteRead httpWriteRead = new HttpWriteRead("ExpirationCheckTask");
		httpWriteRead.setValidityMode(HttpWriteRead.ValidityMode.MODE_ANY_RESPONSE_VALID);
		if(!httpWriteRead.read(urls[0], params))
		{
			mErrorMsg = httpWriteRead.getError();
			Log.e("UpdMyLocaTask.doInBg", "Error updating my location: " + httpWriteRead.getError());
		}
		document = httpWriteRead.getResponse();

		return document;
	}

	
}
