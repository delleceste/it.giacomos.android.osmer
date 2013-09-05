package it.giacomos.android.osmer.pro.forecastRepr;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.text.Spanned;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class Area implements ForecastDataInterface {

	public Spanned rainProb, stormProb;
	public String snowAlt, t1000, t2000, zero;
	
	private String mName;
	
	private LatLng mLatLng;
	
	public int sky, rain, snow, storm, mist, wind;
	
	private Bitmap mBitmap;
	
	public Area(String name)
	{
		mName = name;
		sky = -1; 
		rain = 100; 
		snow = 100; 
		storm = 100; 
		mist = 100; 
		wind = 100;
		mLatLng = null;
		mBitmap = null;
	}
	
	public void setSymbol(LayerDrawable d)
	{
		mBitmap = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(mBitmap); 
	    d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
	    d.draw(canvas);
	}
	
	@Override
	public LatLng getLatLng() {
		return mLatLng;
	}

	@Override
	public boolean isEmpty()
	{
		return mBitmap == null || mLatLng == null;
	}
	
	public Bitmap getSymbol()
	{
		return mBitmap;
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
