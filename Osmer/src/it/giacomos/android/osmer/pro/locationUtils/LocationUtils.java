package it.giacomos.android.osmer.pro.locationUtils;

import android.location.Location;

public class LocationUtils {
	
	public boolean locationInsideRegion(Location location)
	{
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		return locationInsideRegion(latitude, longitude);
	}
	
	public boolean locationInsideRegion(double latitude, double longitude)
	{
		double lat = latitude * 1000000;
		double lon = longitude * 1000000;
	
		if(lat > GeoCoordinates.bottomRight.getLatitudeE6() && 
				lat < GeoCoordinates.topLeft.getLatitudeE6() && 
				lon > GeoCoordinates.topLeft.getLongitudeE6() && 
				lon < GeoCoordinates.bottomRight.getLongitudeE6())
		{
			return true;
		}
		return false;
	}
}
