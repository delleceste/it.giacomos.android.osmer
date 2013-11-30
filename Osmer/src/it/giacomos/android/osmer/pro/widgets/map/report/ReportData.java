package it.giacomos.android.osmer.pro.widgets.map.report;

public class ReportData 
{
	public String username, datetime, locality, comment, temperature;
	public int sky, wind;
	double latitude, longitude;
	
	public ReportData(String u, String d, String l, String c, String t, int s, int w, double lat, double longi)
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
	}

}
