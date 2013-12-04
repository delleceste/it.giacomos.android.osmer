package it.giacomos.android.osmer.pro.reportDataService;

import java.util.ArrayList;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import it.giacomos.android.osmer.pro.locationUtils.Constants;
import it.giacomos.android.osmer.pro.locationUtils.LocationServiceUpdateListener;
import it.giacomos.android.osmer.pro.network.DownloadStatus;

public class ReportDataServiceLocationService implements   GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener, LocationListener
{
	private final Context mContext;
	private LocationClient mLocationClient;
	private LocationRequest mLocationRequest;
	private Location mCurrentLocation;
	private ServiceLocationUpdateListener mServiceLocationUpdateListener;
	/* store location services available flag if servicesAvailable returns true */
	
	/* Define a request code to send to Google Play services
	 * This code is returned in Activity.onActivityResult
	 */
	public final static int
	CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	
	public ReportDataServiceLocationService(Context ctx, ServiceLocationUpdateListener slul)
	{
		mContext = ctx;
		mLocationClient = null;
		mCurrentLocation = null;
		mServiceLocationUpdateListener = slul;
	}
	
	public Location getCurrentLocation()
	{
		return mCurrentLocation;
	}
	
	/* to be called onStart()
	 *
	 */
	public boolean connect()
	{
		/* servicesAvailable has an empty implementation.
		 * GooglePlay services check is done in OsmerActivity.
		 */
		boolean result = true;
//		result = servicesAvailable();
		
		/* Constructor:
		 * LocationClient(Context context, GooglePlayServicesClient.ConnectionCallbacks, 
		 * GooglePlayServicesClient.OnConnectionFailedListener) 
		 * 
		 */
		if(result)
		{
			mLocationRequest = LocationRequest.create();
			mLocationRequest.setInterval(180000); /* 3 mins */
			mLocationRequest.setFastestInterval(120000);
			/* google maps requests location updates. Make this service a passive listener */
			mLocationRequest.setPriority(LocationRequest.PRIORITY_NO_POWER);
			mLocationClient = new LocationClient(mContext, this,  this);
			mLocationClient.connect();
		}
		return result;
	}
	
	/* to be called onStop()
	 * 
	 */
	public void disconnect()
	{
		if(mLocationClient != null)
		{
			if(mLocationClient.isConnected())
			{
				Log.e("ReportDataServiceLocationService.disconnect()", "removing location updates");
				mLocationClient.removeLocationUpdates(this);
			}
//			Log.e("ReportDataServiceLocationService.disconnect()", "disconnecting location client");
			mLocationClient.disconnect();
		}
	}

	public boolean servicesAvailable() 
	{
		/* checks have been made inside Activity */
		return true;
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) 
	{
		/*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) 
        {
        	
        } 
        else 
        {
        	Log.e("ReportDataServiceLocationService.onConnectionFailed", "no resolution available");
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
        }
    }
	
	public void removeLocationServiceUpdateListener()
	{
		mServiceLocationUpdateListener = null;
	}
	
	@Override
	public void onConnected(Bundle bundle) 
	{
		if(mLocationClient != null)
		{
			Location lastKnownLocation = mLocationClient.getLastLocation();
			mLocationClient.requestLocationUpdates(mLocationRequest, this);
			if(lastKnownLocation != null)
				onLocationChanged(lastKnownLocation);
		}
	}

	@Override
	public void onDisconnected() 
	{
		
	}
	
	@Override
	public void onLocationChanged(Location location) 
	{
		if(mServiceLocationUpdateListener != null)
			mServiceLocationUpdateListener.onLocationChanged(location);
	}
}

