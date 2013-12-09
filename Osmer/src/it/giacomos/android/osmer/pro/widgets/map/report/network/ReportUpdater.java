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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import android.content.Context;
import android.content.IntentFilter;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.Toast;
import it.giacomos.android.osmer.pro.network.NetworkStatusMonitor;
import it.giacomos.android.osmer.pro.network.NetworkStatusMonitorListener;
import it.giacomos.android.osmer.pro.network.Data.DataPoolCacheUtils;
import it.giacomos.android.osmer.pro.network.state.Urls;
import it.giacomos.android.osmer.pro.network.state.ViewType;

public class ReportUpdater extends AsyncTask<String, Integer, String>   
implements NetworkStatusMonitorListener,
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener
{
	private static final long DOWNLOAD_REPORT_OLD_TIMEOUT = 10000;
	private static final String CLI = "afe0983der38819073rxc1900lksjd";
	
	private Context mContext;
	private ReportUpdaterListener mReportUpdaterListener;
	private LocationClient mLocationClient;
	private NetworkStatusMonitor mNetworkStatusMonitor;
	private String mErrorMsg;
	private long mLastReportUpdatedAt;

	public ReportUpdater(Context ctx, ReportUpdaterListener rul)
	{
		mContext = ctx;
		mLocationClient = new LocationClient(ctx, this, this);
		mNetworkStatusMonitor = new NetworkStatusMonitor(this);
		mReportUpdaterListener = rul;
		mLastReportUpdatedAt = 0;
	}
	
	public void clear()
	{
		if(mLocationClient != null)
			mLocationClient.disconnect();
		mContext.unregisterReceiver(mNetworkStatusMonitor);
	}
	
	public void update(boolean force)
	{
		Toast.makeText(mContext, "ReportUpdater.update: registering status monitor", Toast.LENGTH_LONG).show();
		mContext.registerReceiver(mNetworkStatusMonitor, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
	}
	
	@Override
	public void onNetworkBecomesAvailable() 
	{
		Toast.makeText(mContext, "ReportUpdater.onNetworkBecomesAvailable: net avail. connecting location cli", Toast.LENGTH_LONG).show();
		mLocationClient.connect();
	}

	@Override
	public void onNetworkBecomesUnavailable() 
	{
		Toast.makeText(mContext, "ReportUpdater.onNetworkBecomesUnavailable: disconnecting location cli", Toast.LENGTH_LONG).show();
		mLocationClient.disconnect();
	}

	@Override
	public void onPostExecute(String doc)
	{
		if(mErrorMsg.isEmpty())
		{
			mReportUpdaterListener.onReportUpdateDone(doc);
			Log.e("ReportUpdater.onPostExecute", "saving to cache");
			DataPoolCacheUtils dataPoolCUtils = new DataPoolCacheUtils();
			dataPoolCUtils.saveToStorage(doc.getBytes(), ViewType.REPORT, mContext);
			mLastReportUpdatedAt = System.currentTimeMillis();
		}
		else
			mReportUpdaterListener.onReportUpdateError(doc);
	}
	
	@Override
	protected String doInBackground(String... urls) 
	{
		String document = "";
		Location l = mLocationClient.getLastLocation();
		if(l == null){
			mErrorMsg = "Location unavailable";
			return "";
		}
		mErrorMsg = "";
		String deviceId = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);
		HttpClient httpClient = new DefaultHttpClient();
        HttpPost request = new HttpPost(urls[0]);
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("cli", CLI));
        postParameters.add(new BasicNameValuePair("d", deviceId));
        postParameters.add(new BasicNameValuePair("la", String.valueOf(l.getLatitude())));
        postParameters.add(new BasicNameValuePair("lo", String.valueOf(l.getLongitude())));
        // postParameters.add(new BasicNameValuePair("loc", mLocality));
        UrlEncodedFormEntity form;
		try {
			form = new UrlEncodedFormEntity(postParameters);
	        request.setEntity(form);
	        Log.e("ReportUpdater.doInBackground", postParameters.toString());
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

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle arg0) 
	{
		Toast.makeText(mContext, "onConnected: location avail. would start update", Toast.LENGTH_LONG).show();
		this.execute(new Urls().getReportUrl());
	}

	@Override
	public void onDisconnected() 
	{
		Toast.makeText(mContext, "ReportUpdater.onDisconnected", Toast.LENGTH_LONG).show();
	}
	
	/** Evaluate if the report is old 
	 * 
	 * @return
	 */
	public boolean reportUpToDate() 
	{	
		return (System.currentTimeMillis() - mLastReportUpdatedAt) < DOWNLOAD_REPORT_OLD_TIMEOUT;
	}

}
