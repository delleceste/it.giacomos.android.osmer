package it.giacomos.android.osmer.pro.forecastRepr;

import android.graphics.drawable.Drawable;
import android.text.Spanned;

import com.google.android.gms.maps.model.LatLng;

public class Area implements ForecastDataInterface {

	public Spanned rainProb, stormProb;
	public String snowAlt, t1000, t2000, zero;
	
	private String mName;
	
	private LatLng mLatLng;
	
	public int sky, rain, snow, storm, mist, wind;
	
	private Drawable mDrawable;
	
	public Area(String name)
	{
		mName = name;
		sky = -1; 
		rain = -1; 
		snow = -1; 
		storm = -1; 
		mist = -1; 
		wind = -1;
		mLatLng = null;
	}
	
	public void setDrawable(Drawable d)
	{
		mDrawable = d;
	}
	
	@Override
	public LatLng getLatLng() {
		return mLatLng;
	}

	@Override
	public boolean isEmpty()
	{
		return mDrawable == null || mLatLng == null;
	}

	@Override
	public Drawable getDrawable() 
	{
		return null;
	}

	@Override
	public String getName() 
	{
		return mName;
	}

	@Override
	public ForecastDataType getType() {
		
		return ForecastDataType.AREA;
	}

	@Override
	public void setLatLng(LatLng ll) {
		mLatLng = ll;
		
	}

}
