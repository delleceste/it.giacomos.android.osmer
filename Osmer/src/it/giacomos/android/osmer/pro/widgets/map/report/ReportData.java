package it.giacomos.android.osmer.pro.widgets.map.report;

import com.google.android.gms.maps.model.MarkerOptions;

public class ReportData implements DataInterface
{
	public String username, datetime, locality, comment, temperature, writable;
	public int sky, wind;
	double latitude, longitude;
	
	public ReportData(String u, String d, String l, String c, 
			String t, int s, int w, double lat, double longi, String writa)
	{
		username = u;
		datetime = d;
		locality = l;
		comment = c;
		temperature = t;
		sky = s;
		wind = w;
		latitude = lat;
		longitude = longi;
		writable = writa;
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
	public MarkerOptions buildMarkerOptions(Context ctx) {
		
		return null;
	}
}
