package it.giacomos.android.osmer.pro.forecastRepr;

import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.model.LatLng;

public class Strip implements ForecastDataInterface {

	private String mId, mName;
	private LatLng mLatLng;
	public String t1000, t2000, zero, tMin, tMax;
	private Drawable mDrawable;

	public Strip(String id)
	{
		mId = id;
		mLatLng = null;
		t1000 = t2000 = zero = tMin = tMax = "";
		if(id.compareTo("F1") == 0)
			mName = "Monti";
		else if(id.compareTo("F2") == 0)
			mName = "Alta Pianura";
		else if(id.compareTo("F3") == 0)
			mName = "Bassa Pianura";
		else if(id.compareTo("F4") == 0)
			mName = "Costa";
	}
	
	@Override
	public LatLng getLatLng() {
		return mLatLng;
	}

	@Override
	public boolean isEmpty() {
		return mLatLng == null;
	}

	@Override
	public String getId() {
		return mId;
	}

	@Override
	public ForecastDataType getType() {
		
		return ForecastDataType.STRIP;
	}
	
	@Override
	public void setLatLng(LatLng ll) {
		mLatLng = ll;
		
	}

	@Override
	public String getName() {
		
		return mName;
	}
}
