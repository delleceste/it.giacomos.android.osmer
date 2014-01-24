package it.giacomos.android.osmer.PROva.forecastRepr;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

public class Locality implements ForecastDataInterface {
	
	private LatLng mLatLng;
	private String mId, mName;
	public int particularSnow, particularStorm;
	public String tMin, tMax;
	public Bitmap mSnowBitmap, mLightningBitmap;
	
	public Locality(String id)
	{
		particularSnow = particularStorm = 100;
		mId  = id;
		mLatLng = null;
		mSnowBitmap = mLightningBitmap = null;

		if(id.compareTo("L1") == 0)
			mName = "Gemona";
		else if(id.compareTo("L2") == 0)
			mName = "Carso";
		else if(id.compareTo("L3") == 0)
			mName = "Claut";
		else if(id.compareTo("L4") == 0)
			mName = "Cividale";
		else if(id.compareTo("L5") == 0)
			mName = "Sappada";
		else if(id.compareTo("L6") == 0)
			mName = "Piancavallo";
		else if(id.compareTo("L7") == 0)
			mName = "Tarvisio";
		else if(id.compareTo("L8") == 0)
			mName = "Lussari";
		else if(id.compareTo("L9") == 0)
			mName = "Zoncolan";
		else if(id.compareTo("L10") == 0)
			mName = "Sella Nevea";
		else if(id.compareTo("L11") == 0)
			mName = "Canin";
		else if(id.compareTo("L12") == 0)
			mName = "Forni Avoltri";
	}
	
	public String getData(ForecastDataStringMap dataMap)
	{
		String t = mName;
		if(particularSnow != 100)
			t += "\n" + dataMap.get(ForecastDataStringMap.SNOW) + ": " + dataMap.get(particularSnow);
		if(particularStorm != 100)
			t += "\n" + dataMap.get(ForecastDataStringMap.STORMS);
		return t;	
	}
	
	public boolean hasSomeBitmap()
	{
		return mSnowBitmap != null || mLightningBitmap != null;
	}
	
	public void setSnowBitmap(Bitmap b)
	{
		mSnowBitmap = b;
	}
	
	public void setLightningBitmap(Bitmap b)
	{
		mLightningBitmap = b;
	}
	
	public Bitmap lightningBitmap()
	{
		return mLightningBitmap;
	}
	
	public Bitmap snowBitmap()
	{
		return mSnowBitmap;
	}
	
	@Override
	public ForecastDataType getType() {
		return ForecastDataType.LOCALITY;
	}

	@Override
	public String getId() {
		return mId;
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
	public void setLatLng(LatLng ll) {
		mLatLng = ll;
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return mName;
	}
}
