package it.giacomos.android.osmer.webcams;

import com.google.android.maps.GeoPoint;

import android.util.Log;

public class WebcamData 
{
	public String url = "";
	public String location = "";
	public String datetime = "";
	public String text = "";
	public GeoPoint geoPoint = null;
	
	public boolean isOther = false;
	
	public boolean equals(WebcamData other)
	{
		return this.location.equals(other.location) &&
				this.url.equals(other.url);
	}
	
	public void print() 
	{
		Log.i("WebcamData: ", location + "/" + text + "/" + url);
		// TODO Auto-generated method stub
		
	}
}
