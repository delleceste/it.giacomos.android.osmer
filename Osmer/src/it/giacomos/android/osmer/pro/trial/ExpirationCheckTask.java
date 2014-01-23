package it.giacomos.android.osmer.pro.trial;

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

public class ExpirationCheckTask extends AsyncTask<String, Integer, String> 
{
	private static final String CLI = "afe098388uygbbhntnu3rxc1900lksjd";
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
		Log.e("XXXXX ExpirationCheckTask.onCancelled", "TASK CANCELLED!!");
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
		HttpClient httpClient = new DefaultHttpClient();
        HttpPost request = new HttpPost(urls[0]);
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("cli", CLI));
        postParameters.add(new BasicNameValuePair("d", mAndroidId));
        UrlEncodedFormEntity form;
		try {
			form = new UrlEncodedFormEntity(postParameters);
	        request.setEntity(form);
	        Log.e("----> ExpirationCheckTask.doInBackground", "thread " + Thread.currentThread());
	        HttpResponse response = httpClient.execute(request);
	        StatusLine statusLine = response.getStatusLine();
	        if(statusLine.getStatusCode() < 200 || statusLine.getStatusCode() >= 300)
	        	mErrorMsg = statusLine.getReasonPhrase();
	        else if(statusLine.getStatusCode() < 0)
	        	mErrorMsg = "Server error";
	        /* check the echo result */
	        HttpEntity entity = response.getEntity();
	        document = EntityUtils.toString(entity);
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
		return document;
	}

	
}
