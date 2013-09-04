package it.giacomos.android.osmer.pro.forecastRepr;

import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.model.LatLng;

public interface ForecastDataInterface 
{
	public ForecastDataType getType();
	
	public String getName();
	
	public Drawable getDrawable();
	
	public LatLng getLatLng();
	
	public void setLatLng(LatLng ll);
	
	public boolean isEmpty();

}
