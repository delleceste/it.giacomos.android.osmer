package it.giacomos.android.osmer.pro.widgets.map.report.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

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
	private String mDeviceId;
	private PostReportTaskListener mPostReportTaskListener;
	private double mLat, mLong;
	
	private static String CLI = "afe0983der38819073rxc1900lksjd";
	
	public PostReportTask(String user, String deviceId, String locality, double lat, double lng, 
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

		PostReportAsyncTaskPool.Instance().registerTask(this);
	}
	
	@Override
	protected String doInBackground(String...urls) 
	{
		mErrorMsg = "";
		HttpClient httpClient = new DefaultHttpClient();
        HttpPost request = new HttpPost(urls[0]);
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("cli", CLI));
        postParameters.add(new BasicNameValuePair("n", mUser));
        postParameters.add(new BasicNameValuePair("d", mDeviceId));
        postParameters.add(new BasicNameValuePair("s", String.valueOf(mSky)));
        postParameters.add(new BasicNameValuePair("w", String.valueOf(mWind)));
        postParameters.add(new BasicNameValuePair("t", mTemp));
        postParameters.add(new BasicNameValuePair("c", String.valueOf(mComment)));
        postParameters.add(new BasicNameValuePair("la", String.valueOf(mLat)));
        postParameters.add(new BasicNameValuePair("lo", String.valueOf(mLong)));
        postParameters.add(new BasicNameValuePair("l", String.valueOf(mLocality)));

        UrlEncodedFormEntity form;
		try {
			form = new UrlEncodedFormEntity(postParameters);
	        request.setEntity(form);
	        Log.e("PostReportTask.doInBackground", postParameters.toString());
	        HttpResponse response = httpClient.execute(request);
	        StatusLine statusLine = response.getStatusLine();
	        if(statusLine.getStatusCode() < 200 || statusLine.getStatusCode() >= 300)
	        	mErrorMsg = statusLine.getReasonPhrase();
	        else if(statusLine.getStatusCode() < 0)
	        	mErrorMsg = "Server error";
	        /* check the echo result */
	        HttpEntity entity = response.getEntity();
	        String returnVal = EntityUtils.toString(entity);
	        if(returnVal.compareTo("0") != 0)
	        	mErrorMsg = "Server error: the server returned " + returnVal;
		} 
		catch (UnsupportedEncodingException e) 
		{
			mErrorMsg = e.getLocalizedMessage();
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			mErrorMsg = e.getLocalizedMessage();
			e.printStackTrace();
		} catch (IOException e) {
			mErrorMsg = e.getLocalizedMessage();
			e.printStackTrace();
		}
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
