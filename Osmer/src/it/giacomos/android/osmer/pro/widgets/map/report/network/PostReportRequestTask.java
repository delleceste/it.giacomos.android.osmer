package it.giacomos.android.osmer.pro.widgets.map.report.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.util.Log;
import it.giacomos.android.osmer.pro.widgets.map.ReportPublishedListener;

public class PostReportRequestTask extends AsyncTask<String, Integer, String>{

	private ReportPublishedListener mReportPublishedListener;
	private String mErrorMsg;
	private String mUser, mLocality;
	double mLatitude, mLongitude;
	private String mDeviceId;
	
	private static String CLI = "afe0983der38819073rxc1900lksjd";
	
	public PostReportRequestTask(String user, String locality, double latitude,
			double longitude, ReportPublishedListener oActivity) 
	{
		mUser = user;
		mLocality = locality;
		mLatitude = latitude;
		mLongitude = longitude;
		mReportPublishedListener = oActivity;
	}

	public void setDeviceId(String id)
	{
		mDeviceId = id;
	}
	
	@Override
	protected String doInBackground(String... urls) 
	{
		mErrorMsg = "";
		if(mLocality.length() < 2)
			mLocality="";
		HttpClient httpClient = new DefaultHttpClient();
        HttpPost request = new HttpPost(urls[0]);
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("cli", CLI));
        postParameters.add(new BasicNameValuePair("n", mUser));
        postParameters.add(new BasicNameValuePair("d", mDeviceId));
        postParameters.add(new BasicNameValuePair("l", mLocality));
        postParameters.add(new BasicNameValuePair("la", String.valueOf(mLatitude)));
        postParameters.add(new BasicNameValuePair("lo", String.valueOf(mLongitude)));
        UrlEncodedFormEntity form;
		try {
			form = new UrlEncodedFormEntity(postParameters);
	        request.setEntity(form);
	        Log.e("PostReportRequestTask.doInBackground", postParameters.toString());
	        HttpResponse response = httpClient.execute(request);
	        StatusLine statusLine = response.getStatusLine();
	        if(statusLine.getStatusCode() < 200 || statusLine.getStatusCode() >= 300)
	        	mErrorMsg = statusLine.getReasonPhrase();
	        else if(statusLine.getStatusCode() < 0)
	        	mErrorMsg = "Server error";
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
	public void onPostExecute(String doc)
	{
		if(mErrorMsg.isEmpty())
			mReportPublishedListener.onReportPublished(false, "");
		else
			mReportPublishedListener.onReportPublished(true, mErrorMsg);
	}
}
