package it.giacomos.android.osmer.pro.widgets.map;

import com.google.android.gms.maps.model.LatLng;

public interface MyReportRequestListener 
{
	public void onMyReportRequestTriggered(LatLng pointOnMap, String mMyRequestMarkerLocality);
	public void onMyReportLocalityChanged(String locality);
}
