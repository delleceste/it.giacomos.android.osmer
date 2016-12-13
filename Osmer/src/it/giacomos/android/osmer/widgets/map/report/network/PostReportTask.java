package it.giacomos.android.osmer.widgets.map.report.network;

import it.giacomos.android.osmer.network.HttpPostParametrizer;
import it.giacomos.android.osmer.network.HttpWriteRead;

import android.os.AsyncTask;
import android.util.Log;

/** 
 * This class uses PostReportAsyncTaskPool in order to be cancelled when the Activity
 * is destroyed.
 * It is important to register on creation and unregister in onCancelled and in
 * onPostExecute
 * 
 * @author giacomo
 *
 */
public class PostReportTask extends AsyncTask<String, Integer, String> 
{
	private int mSky, mWind;
	private String mUser, mComment, mTemp, mLocality;
	private String mErrorMsg;
	private String mDeviceId, mRegistrationId;
	private PostReportTaskListener mPostReportTaskListener;
	private double mLat, mLong;
	
	private static String CLI = "afe0983der38819073rxc1900lksjd";
	
	public PostReportTask(String user, String deviceId, String registrationId, String locality, double lat, double lng, 
			int sky, int wind, String temp, String comment, PostReportTaskListener tl)
	{
		mSky = sky;
		mWind = wind;
		mUser = user;
		mTemp = temp;
		mComment = comment;
		mPostReportTaskListener = tl;
		mLat = lat;
		mLong = lng;
		mLocality = locality;
		mDeviceId = deviceId;
		mRegistrationId = registrationId;

		PostReportAsyncTaskPool.Instance().registerTask(this);
	}
	
	@Override
	protected String doInBackground(String...urls) 
	{
		String returnVal;
		mErrorMsg = "";
		
		HttpPostParametrizer parametrizer = new HttpPostParametrizer();
        parametrizer.add("cli", CLI);
		parametrizer.add("n", mUser);
		parametrizer.add("d", mDeviceId);
		parametrizer.add("rid", mRegistrationId);
		
		parametrizer.add("la", mLat);
		parametrizer.add("lo", mLong);
		parametrizer.add("l", mLocality);
		
		parametrizer.add("s", mSky);
		parametrizer.add("w", mWind);
		parametrizer.add("t", mTemp);
		parametrizer.add("c", mComment);
		
		String params = parametrizer.toString();
		HttpWriteRead httpWriteRead = new HttpWriteRead("UpdateMyLocationTask");
		httpWriteRead.setValidityMode(HttpWriteRead.ValidityMode.MODE_ANY_RESPONSE_VALID);
		if(!httpWriteRead.read(urls[0], params))
		{
			mErrorMsg = httpWriteRead.getError();
			Log.e("UpdMyLocaTask.doInBg", "Error updating my location: " + httpWriteRead.getError());
		}
		returnVal = httpWriteRead.getResponse().trim();
		if(returnVal.compareTo("0") != 0)
        	mErrorMsg = "PostReport: server error: " + returnVal;
		return null;
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
			mPostReportTaskListener.onTaskCompleted(false, "");
		else
			mPostReportTaskListener.onTaskCompleted(true, mErrorMsg);

		/* unregister the task from the PostReportAsyncTaskPool when finished */
		PostReportAsyncTaskPool.Instance().unregisterTask(this);
	}
	
}
