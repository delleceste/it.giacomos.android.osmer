package it.giacomos.android.osmer.service;

import android.os.AsyncTask;
import android.util.Log;

import it.giacomos.android.osmer.network.HttpPostParametrizer;
import it.giacomos.android.osmer.network.HttpWriteRead;

public class UpdateMyLocationTask extends AsyncTask<String, Integer, String> {

	private String mErrorMsg;
	private boolean mRainNotificationEnabled, mPushRainNotificationEnabled;
	String mDeviceId, mRegistrationId;
	private FetchRequestsTaskListener mServiceDataTaskListener;
	double mLatitude, mLongitude;

	private static String CLI = "afe0983der38819073rxc1900lksjd";

	public UpdateMyLocationTask(FetchRequestsTaskListener sdtl, String deviceId, 
			String registrationId, double lat, double longit, 
			boolean rainNotificationEnabled,
			boolean pushRainNotificationEnabled)
	{
		mErrorMsg = "";
		mServiceDataTaskListener = sdtl;
		mDeviceId = deviceId;
		mRegistrationId = registrationId;
		mLatitude = lat;
		mLongitude = longit;
		mRainNotificationEnabled = rainNotificationEnabled;
		mPushRainNotificationEnabled = pushRainNotificationEnabled;
	}

	public void removeFetchRequestTaskListener()
	{
		mServiceDataTaskListener = null;
	}

	@Override
	protected String doInBackground(String... urls) 
	{
		String data = "";
		mErrorMsg = "";
        HttpPostParametrizer parametrizer = new HttpPostParametrizer();
        parametrizer.add("cli", CLI);
		parametrizer.add("d", mDeviceId);
		parametrizer.add("la", mLatitude);
		parametrizer.add("lo", mLongitude);
		parametrizer.add("rid", mRegistrationId);
		parametrizer.add("rain_detect", mRainNotificationEnabled);
		parametrizer.add("push_rain_notification", mPushRainNotificationEnabled);
		String params = parametrizer.toString();
		HttpWriteRead httpWriteRead = new HttpWriteRead("UpdateMyLocationTask");
		httpWriteRead.setValidityMode(HttpWriteRead.ValidityMode.MODE_ANY_RESPONSE_VALID);
		if(!httpWriteRead.read(urls[0], params))
		{
			mErrorMsg = httpWriteRead.getError();
			Log.e("UpdMyLocaTask.doInBg", "Error updating my location: " + httpWriteRead.getError());
		}
		data = httpWriteRead.getResponse();
		return data;
	}

	@Override
	public void onPostExecute(String data)
	{
		if(mServiceDataTaskListener != null)
		{
			if(mErrorMsg.isEmpty())
				mServiceDataTaskListener.onServiceDataTaskComplete(false, data);
			else
				mServiceDataTaskListener.onServiceDataTaskComplete(true, mErrorMsg);
		}
	}
	
	@Override
	public void onCancelled(String data)
	{
//		Log.e("UpdateMyLocationTask.onCancelled", "task cancelled");
	}
}
