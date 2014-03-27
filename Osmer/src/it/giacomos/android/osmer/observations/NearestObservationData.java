package it.giacomos.android.osmer.observations;
import it.giacomos.android.osmer.locationUtils.LocationNamesMap;
import it.giacomos.android.osmer.locationUtils.NearLocationFinder;

import java.util.ArrayList;
import java.util.HashMap;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class NearestObservationData {

	public NearestObservationData()
	{

	}

	public ObservationData get(Location location, ObservationsCache observationsCache)
	{
		long startT = System.nanoTime();
		LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
		LocationNamesMap locationNamesMap = new LocationNamesMap();
		ArrayList<LatLng> points = new ArrayList<LatLng>(locationNamesMap.getMap().values());
		NearLocationFinder nearLocationFinder = new NearLocationFinder();
		LatLng nearestLocation = nearLocationFinder.nearestLocation(myLatLng, points);
		String nearestLocationName = locationNamesMap.getLocationName(nearestLocation);


		HashMap<String, ObservationData> latestObsData = observationsCache.getLatestObservationData();
		ObservationData obsData = latestObsData.get(nearestLocationName);
		long endT = System.nanoTime();
		return obsData;

	}
}
