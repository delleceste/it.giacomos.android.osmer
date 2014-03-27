package it.giacomos.android.osmer.forecastRepr;

import com.google.android.gms.maps.model.LatLng;

public interface ForecastDataInterface 
{
	public ForecastDataType getType();
	
	public String getId();
	
	public String getName();
			
	public LatLng getLatLng();
	
	public void setLatLng(LatLng ll);
	
	public boolean isEmpty();

}
