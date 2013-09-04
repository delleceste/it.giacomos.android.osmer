package it.giacomos.android.osmer.pro.forecastRepr;

import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.model.LatLng;

public class Locality implements ForecastDataInterface {
	
	private LatLng mLatLng;
	private String mName;
	public int particularSnow, particularStorm;
	public String tMin, tMax;
	private Drawable mDrawable;
	
	public Locality(String name)
	{
		particularSnow = particularStorm = -1;
		mName  = name;
		mLatLng = null;
	}
	
	@Override
	public ForecastDataType getType() {
		return ForecastDataType.LOCALITY;
	}

	@Override
	public String getName() {
		return mName;
	}

	@Override
	public Drawable getDrawable() {
		return null;
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
	public void setLatLng(LatLng ll) {
		mLatLng = ll;
		
	}
}
