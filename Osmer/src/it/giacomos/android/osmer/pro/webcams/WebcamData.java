package it.giacomos.android.osmer.pro.webcams;

import com.google.android.gms.maps.model.LatLng;

import android.util.Log;

public class WebcamData 
{
	public String url = "";
	public String location = "";
	public String datetime = "";
	public String text = "";
	public LatLng latLng = null;
	
	public boolean isOther = false;
	
	public boolean equals(WebcamData other)
	{
		return this.location.equals(other.location) &&
				this.url.equals(other.url);
	}
	
	public String toString() 
	{
		return "WebcamData: " + location + "/" + text + "/" + url + "(" + latLng.latitude + ", " + 
				latLng.longitude + ")";
	}
}
