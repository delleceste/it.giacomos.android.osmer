package it.giacomos.android.osmer.pro.widgets.map.report;

import android.content.Context;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public interface DataInterface 
{
	public static int TYPE_REPORT = 0;
	public static int TYPE_REQUEST = 1;
	public double getLatitude();
	public double getLongitude();
	public void setLatitude(double d);
	public void setLongitude(double lon);
	public int getType();
	public String getLocality();
	
	public boolean isWritable();
	
	public MarkerOptions buildMarkerOptions(Context ctx);
	
	public MarkerOptions getMarkerOptions();
	
	public void setMarker(Marker m);
	
	public Marker getMarker();	
	
	public boolean isPublished();
}
