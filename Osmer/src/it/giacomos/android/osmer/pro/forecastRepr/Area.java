package it.giacomos.android.osmer.pro.forecastRepr;

import it.giacomos.android.osmer.R;

import java.util.HashMap;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.text.Spanned;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.maps.model.LatLng;

public class Area implements ForecastDataInterface {

	public Spanned rainProb, stormProb;
	public String snowAlt, t1000, t2000, zero;
	
	private String mId, mName;
	
	private LatLng mLatLng;
	
	public int sky, rain, snow, storm, mist, wind;
	
	private Bitmap mBitmap;
		
	public Area(String id)
	{
		mId = id;
		sky = -1; 
		rain = 100; 
		snow = 100; 
		storm = 100; 
		mist = 100; 
		wind = 100;
		mLatLng = null;
		mBitmap = null;
		/* name is statically set here to keep downloaded data as small as possible */
		if(id.compareTo("A1") == 0)
			mName = "Alpi Carniche";
		else if(id.compareTo("A2") == 0)
			mName = "Alpi Giulie";
		else if(id.compareTo("A3") == 0)
			mName = "Prealpi Carniche";
		else if(id.compareTo("A4") == 0)
			mName = "Prealpi Giulie";
		else if(id.compareTo("A5") == 0)
			mName = "Pordenone";
		else if(id.compareTo("A6") == 0)
			mName = "Udine";
		else if(id.compareTo("A7") == 0)
			mName = "Gorizia";
		else if(id.compareTo("A8") == 0)
			mName = "Lignano";
		else if(id.compareTo("A9") == 0)
			mName = "Trieste";	
	}
	
	public String getData(ForecastDataStringMap dataMap)
	{
		String t = mName;
		/* sky always present */
		t += "\n" + dataMap.get(sky);
		
		if(rain != 100)
			t += "\n" + dataMap.get(1006) + ": " + dataMap.get(rain);
		if(snow != 100)
			t += "\n" + dataMap.get(1010) + ": " + dataMap.get(snow);
		if(storm != 100)
			t += "\n" + dataMap.get(storm);
		if(mist != 100)
			t += "\n" + dataMap.get(1014) + ": " + dataMap.get(mist);
		if(wind != 100)
			t += "\n" + dataMap.get(1016) + ": " + dataMap.get(wind);
		
		return t;
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
	public String getId() 
	{
		return mId;
	}

	@Override
	public ForecastDataType getType() {
		
		return ForecastDataType.AREA;
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
