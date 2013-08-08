package it.giacomos.android.osmer.locationUtils;

import java.util.ArrayList;

import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.interfaceHelpers.ErrorDialogFragment;
import it.giacomos.android.osmer.network.DownloadStatus;
import it.giacomos.android.osmer.network.state.StateName;
import it.giacomos.android.osmer.widgets.ODoubleLayerImageView;
import android.app.Dialog;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class LocationService implements   GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener, LocationListener,
GeocodeAddressUpdateListener
{

	
	private FragmentActivity mActivity;
	private static LocationService mLocationService = null;
	private LocationClient mLocationClient;
	private ArrayList<LocationServiceUpdateListener> mLocationServiceUpdateListeners;
	private ArrayList<LocationServiceAddressUpdateListener> mLocationServiceAddressUpdateListeners;
	private LocationRequest mLocationRequest;
	private Location mCurrentLocation;
	private LocationInfo mCurrentLocationInfo;
	/* store location services available flag if servicesAvailable returns true */
	private boolean mServicesAvailable;
	
	/* Define a request code to send to Google Play services
	 * This code is returned in Activity.onActivityResult
	 */
	public final static int
	CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	

	
	public static LocationService Instance()
	{
		if(mLocationService == null)
			mLocationService = new LocationService();
		return mLocationService;
	}
	
	/* call right after calling Instance(), first time */
	public void init(FragmentActivity activity)
	{
		if(mActivity == null)
			mActivity = activity;
	}
	
	private LocationService()
	{
		mServicesAvailable = false;
		mActivity = null;
		mLocationClient = null;
		mCurrentLocation = null;
		mCurrentLocationInfo = null;
		mLocationServiceUpdateListeners = new ArrayList<LocationServiceUpdateListener>();
		mLocationServiceAddressUpdateListeners = new ArrayList<LocationServiceAddressUpdateListener>();
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
		boolean result;
		Log.e("LocationService", "checking if services are available...");
		result = servicesAvailable();
		
		/* Constructor:
		 * LocationClient(Context context, GooglePlayServicesClient.ConnectionCallbacks, 
		 * GooglePlayServicesClient.OnConnectionFailedListener) 
		 * 
		 */
		if(result)
		{
			Log.e("LocationService.connect()", "connecting location client");
			mLocationRequest = LocationRequest.create();
			mLocationRequest.setInterval(Constants.LOCATION_UPDATE_INTERVAL);
			mLocationRequest.setFastestInterval(Constants.LOCATION_FASTEST_UPDATE_INTERVAL);
			mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			mLocationClient = new LocationClient(mActivity, this,  this);
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
				Log.e("LocationService.disconnect()", "removing location updates");
				mLocationClient.removeLocationUpdates(this);
			}
			Log.e("LocationService.disconnect()", "disconnecting location client");
			mLocationClient.disconnect();
		}
	}

	public boolean servicesAvailable() 
	{
		if(mServicesAvailable)
			return true;
		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity);
		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) 
		{
			// In debug mode, log the status
			Log.e("Location Updates", "Google Play services is available.");
			mServicesAvailable = true;	
		} 
		else // Google Play services was not available for some reason
		{
			Log.e("Location Updates", "Google Play services is NOT available.");
			showErrorDialog(resultCode);
			mServicesAvailable = false;
		}
		return mServicesAvailable;
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
        	Log.e("LocationService.onConnectionFailed", "has resoulution");
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        mActivity, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } 
            catch (IntentSender.SendIntentException e) 
            {
            	Log.e("LocationService.onConnectionFailed", "IntentSender.SendIntentException");
                // Log the error
                e.printStackTrace();
            }
        } 
        else 
        {
        	Log.e("LocationService.onConnectionFailed", "no resolution available");
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
        	showErrorDialog(connectionResult.getErrorCode());
        }
    }

	private void showErrorDialog(int errorCode)
	{
		Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
    			errorCode,
				mActivity, CONNECTION_FAILURE_RESOLUTION_REQUEST);
		if (errorDialog != null) 
		{
			// Create a new DialogFragment for the error dialog
			ErrorDialogFragment errorFragment =
					new ErrorDialogFragment();
			// Set the dialog in the DialogFragment
			errorFragment.setDialog(errorDialog);
			// Show the error dialog in the DialogFragment
			errorFragment.show(mActivity.getSupportFragmentManager(),
					"Location Services");
		}
	}
	
	public void registerLocationServiceUpdateListener(LocationServiceUpdateListener l)
	{
		mLocationServiceUpdateListeners.add(l);
		if(mCurrentLocation != null) /* immediately notify listener if possible */
			l.onLocationChanged(mCurrentLocation);
	}
	
	public void removeLocationServiceUpdateListener(LocationServiceUpdateListener l)
	{
		mLocationServiceUpdateListeners.remove(l);
	}
	
	public void registerLocationServiceAddressUpdateListener(LocationServiceAddressUpdateListener al)
	{
		mLocationServiceAddressUpdateListeners.add(al);
		if(mCurrentLocationInfo != null)
		{
			/* immediately notify the just registered listener */
			al.onLocalityChanged(mCurrentLocationInfo.locality, 
					mCurrentLocationInfo.subLocality, 
					mCurrentLocationInfo.address);
		}
	}
	
	public void removeLocationServiceAddressUpdateListener(LocationServiceAddressUpdateListener al)
	{
		mLocationServiceAddressUpdateListeners.remove(al);
	}
	
	@Override
	public void onConnected(Bundle bundle) 
	{
		if(mLocationClient != null)
		{
			Log.e("LocationService.onConnected", "requesting location updates");
			mLocationClient.requestLocationUpdates(mLocationRequest, this);
		}
	}

	@Override
	public void onDisconnected() 
	{
		for(LocationServiceUpdateListener l : mLocationServiceUpdateListeners)
			l.onLocationServiceError("Error: disconnected from location updates");
	}

	public void updateGeocodeAddress()
	{
		GeocodeAddressTask geocodeAddressTask = new GeocodeAddressTask(mActivity, this);
		geocodeAddressTask.parallelExecute(mCurrentLocation);
	}
	
	@Override
	public void onLocationChanged(Location location) 
	{
		Log.e("LocationService.onLocationChanged", "notifying location changes to listeners no. " 
				+ mLocationServiceUpdateListeners.size());
		for(LocationServiceUpdateListener l : mLocationServiceUpdateListeners)
			l.onLocationChanged(location);
		
		/* do we still need LocationComparer ? */
		LocationComparer locationComparer = new LocationComparer();
		
		if(locationComparer.isBetterLocation(location, mCurrentLocation))
		{	
			mCurrentLocation = location; /* save current location */
			if(DownloadStatus.Instance().isOnline)
			{
				Log.e("LocationService.onLocationChanged", "we are online, starting geocode task");
				updateGeocodeAddress();
			}
		}
		else
			Log.e("LocationService.onLocationChanged", " !!!! new location is not better than old");
		locationComparer = null;
	}

	@Override
	/* executed when a new locality / address becomes available.
	 */
	public void onGeocodeAddressUpdate(LocationInfo locInfo)
	{
		if(locInfo.error.isEmpty())
		{
			for(LocationServiceAddressUpdateListener lsal : mLocationServiceAddressUpdateListeners)
			{
				lsal.onLocalityChanged(locInfo.locality, locInfo.subLocality, locInfo.address);
			}
			mCurrentLocationInfo = locInfo;
		}
		
	}
}

