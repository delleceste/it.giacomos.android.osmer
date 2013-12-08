package it.giacomos.android.osmer.pro.widgets.map.report;

import com.google.android.gms.maps.model.MarkerOptions;

public class RequestData implements DataInterface
{
	private double latitude, longitude;
	private String writable;
	public String datetime, username;
	
	public RequestData(String d, String user, double la, double lo, String wri)
	{
		latitude = la;
		longitude = lo;
		writable = wri;
		username = user;
		datetime = d;
	}
	
	public boolean isWritable()
	{
		return (writable.compareTo("w") == 0);
	}

	@Override
	public double getLatitude() {
		return latitude;
	}

	@Override
	public double getLongitude() {
		return longitude;
	}

	@Override
	public MarkerOptions buildMarkerOptions() {
		// TODO Auto-generated method stub
		return null;
	}

}
