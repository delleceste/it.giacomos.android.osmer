package it.giacomos.android.osmer.pro.widgets.map.report;

import android.content.Context;

import com.google.android.gms.maps.model.MarkerOptions;

public interface DataInterface 
{
	public static int TYPE_REPORT = 0;
	public static int TYPE_REQUEST = 1;
	public double getLatitude();
	public double getLongitude();
	public int getType();
	public MarkerOptions buildMarkerOptions(Context ctx);
}
