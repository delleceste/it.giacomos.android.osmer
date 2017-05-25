package it.giacomos.android.osmer.widgets.map.report.network;

import android.os.AsyncTask;
import android.util.Log;

import it.giacomos.android.osmer.network.HttpPostParametrizer;
import it.giacomos.android.osmer.network.HttpWriteRead;

/** 
 * This class uses PostReportAsyncTaskPool in order to be cancelled when the Activity
 * is destroyed.
 * It is important to register on creation and unregister in onCancelled and in
 * onPostExecute
 * 
 * @author giacomo
 *
 */
public class RemovePostTask extends AsyncTask<String, Integer, String> {

	private static String CLI = "afe0983der38819073rxc1900lksjd";
	private PostType mType;
	
	/* RemovePostConfirmDialog implements RemovePostTaskListener */
	private RemovePostTaskListener mRemovePostTaskListener;
	
	private String mErrorMsg, mDeviceId;
	private double mLatitude, mLongitude;
	
	public RemovePostTask(PostType type, String devid, double latitude, double longitude, RemovePostTaskListener li)
	{
		mType = type;
		mRemovePostTaskListener = li;
		mDeviceId = devid;
		mLatitude = latitude;
		mLongitude = longitude;
		
		PostReportAsyncTaskPool.Instance().registerTask(this);
	}
	
	@Override
	protected String doInBackground(String... urls) 
	{
		String returnVal = "";
		mErrorMsg = "";
		
		HttpPostParametrizer parametrizer = new HttpPostParametrizer();
        parametrizer.add("cli", CLI);
		parametrizer.add("d", mDeviceId);
		parametrizer.add("la", mLatitude);
		parametrizer.add("lo", mLongitude);
		
		if(mType == PostType.REQUEST_REMOVE)
			parametrizer.add("t", "q");
		else if(mType == PostType.REPORT_REMOVE)
			parametrizer.add("t", "r");
		else
        	return "-1";
				
		String params = parametrizer.toString();
		HttpWriteRead httpWriteRead = new HttpWriteRead("RemovePostTask");
		httpWriteRead.setValidityMode(HttpWriteRead.ValidityMode.MODE_RESPONSE_VALID_IF_ZERO);
		if(!httpWriteRead.read(urls[0], params))
		{
			mErrorMsg = httpWriteRead.getError();
			Log.e("UpdMyLocaTask.doInBg", "Error updating my location: " + httpWriteRead.getError());
		}
		returnVal = httpWriteRead.getResponse();
		return returnVal;
	}

	@Override
	protected void onCancelled (String result)
	{
		/* unregister the task from the PostReportAsyncTaskPool when finished */
		PostReportAsyncTaskPool.Instance().unregisterTask(this);
	}
	
	@Override
	public void onPostExecute(String doc)
	{
		if(mErrorMsg.isEmpty())
			mRemovePostTaskListener.onRemovePostTaskCompleted(false, "", mType);
		else
			mRemovePostTaskListener.onRemovePostTaskCompleted(true, mErrorMsg, mType);
		
		PostReportAsyncTaskPool.Instance().unregisterTask(this);
	}
}
