package it.giacomos.android.osmer.locationUtils;

import android.location.Location;

public interface LocationServiceUpdateListener 
{
	public abstract void onLocationChanged(Location location);
	
	public abstract void onLocationServiceError(String message);
}
