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
public class PostReportRequestTask extends AsyncTask<String, Integer, String>{

	private PostActionResultListener mReportPublishedListener;
	private String mErrorMsg;
	private String mUser, mLocality;
	double mLatitude, mLongitude;
	private String mDeviceId, mRegistrationId;
	
	private static String CLI = "afe0983der38819073rxc1900lksjd";
	
	public PostReportRequestTask(String user, String locality, double latitude,
			double longitude, PostActionResultListener oActivity) 
	{
		mUser = user;
		mLocality = locality;
		mLatitude = latitude;
		mLongitude = longitude;
		mRegistrationId = "";
		mReportPublishedListener = oActivity;
		
		PostReportAsyncTaskPool.Instance().registerTask(this);
	}

	public void setDeviceId(String id)
	{
		mDeviceId = id;
	}
	
	public void setRegistrationId(String regId)
	{
		mRegistrationId = regId;
	}
	
	@Override
	protected String doInBackground(String... urls) 
	{
		mErrorMsg = "";
		if(mLocality.length() < 2)
			mLocality="";
		String returnVal = "0";
		mErrorMsg = "";
        HttpPostParametrizer parametrizer = new HttpPostParametrizer();
        parametrizer.add("cli", CLI);
		parametrizer.add("d", mDeviceId);
		parametrizer.add("rid", mRegistrationId);
		parametrizer.add("la", mLatitude);
		parametrizer.add("lo", mLongitude);
		parametrizer.add("n", mUser);
		parametrizer.add("l", mLocality);
		String params = parametrizer.toString();
		HttpWriteRead httpWriteRead = new HttpWriteRead("UpdateMyLocationTask");
		httpWriteRead.setValidityMode(HttpWriteRead.ValidityMode.MODE_ANY_RESPONSE_VALID);
		if(!httpWriteRead.read(urls[0], params))
		{
			mErrorMsg = httpWriteRead.getError();
			Log.e("UpdMyLocaTask.doInBg", "Error updating my location: " + httpWriteRead.getError());
		}
		returnVal = httpWriteRead.getResponse().trim();
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
			mReportPublishedListener.onPostActionResult(false, doc, PostType.REQUEST);
		else
			mReportPublishedListener.onPostActionResult(true, mErrorMsg, PostType.REQUEST);
		
		PostReportAsyncTaskPool.Instance().unregisterTask(this);
	}
}
