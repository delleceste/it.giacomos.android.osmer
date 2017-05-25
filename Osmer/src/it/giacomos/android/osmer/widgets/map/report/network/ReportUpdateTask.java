package it.giacomos.android.osmer.widgets.map.report.network;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import it.giacomos.android.osmer.network.HttpPostParametrizer;
import it.giacomos.android.osmer.network.HttpWriteRead;

public class ReportUpdateTask extends AsyncTask<String, Integer, String> 
{
	private static final String CLI = "afe0983der38819073rxc1900lksjd";
	private String mErrorMsg;
	private ReportUpdateTaskListener mReportUpdateTaskListener;
	Location mLastLocation;
	String mAndroidId;
	
	public ReportUpdateTask(ReportUpdateTaskListener reportUpdateTaskListener,
			Location l, String androidId)
	{
		super();
		mReportUpdateTaskListener = reportUpdateTaskListener;
		mLastLocation = l;
		mAndroidId = androidId;
	}
	
	@Override
	public void onPostExecute(String doc)
	{
		mReportUpdateTaskListener.onReportUpdateTaskComplete(!mErrorMsg.isEmpty(), doc);
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
		Location l = mLastLocation;
		if(l == null){
			mErrorMsg = "Location unavailable";
			return "";
		}
		mErrorMsg = "";
		
		HttpPostParametrizer parametrizer = new HttpPostParametrizer();
        parametrizer.add("cli", CLI);
		parametrizer.add("d", mAndroidId);
		parametrizer.add("la", l.getLatitude());
		parametrizer.add("lo", l.getLongitude());
		
		String params = parametrizer.toString();
		HttpWriteRead httpWriteRead = new HttpWriteRead("ReportUpdateTask");
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
