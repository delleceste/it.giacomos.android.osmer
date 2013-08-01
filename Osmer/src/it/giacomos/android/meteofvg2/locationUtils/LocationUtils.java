package it.giacomos.android.meteofvg2.locationUtils;

import android.location.Location;

public class LocationUtils {
	
	public boolean locationInsideRegion(Location location)
	{
		double lat = location.getLatitude() * 1000000;
		double lon = location.getLongitude() * 1000000;
	
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
