package it.giacomos.android.osmer.pro.forecastRepr;

import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.model.LatLng;

public class Strip implements ForecastDataInterface {

	private String mName;
	private LatLng mLatLng;
	public String t1000, t2000, zero, tMin, tMax;
	private Drawable mDrawable;

	public Strip(String name)
	{
		mName = name;
		mLatLng = null;
	}
	
	@Override
	public LatLng getLatLng() {
		return mLatLng;
	}

	@Override
	public boolean isEmpty() {
		return mDrawable == null || mLatLng == null;
	}

	@Override
	public Drawable getDrawable() {
		return null;
	}

	@Override
	public String getName() {
		return mName;
	}

	@Override
	public ForecastDataType getType() {
		
		return ForecastDataType.STRIP;
	}
	
	@Override
	public void setLatLng(LatLng ll) {
		mLatLng = ll;
		
	}
}
