package it.giacomos.android.osmer.pro.forecastRepr;

import android.util.SparseArray;

import com.google.android.gms.maps.model.LatLng;

public class Zone implements ForecastDataInterface {
	
	private LatLng mLatLng;
	private String mId, mName;
	public int evo00, evo12, evo24;
	public SparseArray<String> mDataMap;
	
	public Zone(String id)
	{
		evo00 = evo12 = evo24 = 100;
		mId  = id;
		mLatLng = null;

		if(id.compareTo("Z1") == 0)
			mName = "Monti";
		else if(id.compareTo("Z2") == 0)
			mName = "Alta Pianura";
		else if(id.compareTo("Z3") == 0)
			mName = "Bassa Pianura";
		else if(id.compareTo("Z4") == 0)
			mName = "Costa";
		
		mDataMap = new SparseArray<String>();
		
		mDataMap.put(0, "sereno");
		mDataMap.put(1, "variabile");
		mDataMap.put(2, "coperto");
		mDataMap.put(3, "variab. piogge mod.");
		mDataMap.put(4, "variab. piogge abbond.");
		mDataMap.put(5, "variab. piogge abbond.\ne temporali");
		mDataMap.put(6, "variabile con temporali");
		mDataMap.put(6, "variabile e nevicate");
		mDataMap.put(7, "coperto piogge mod.\ne nevicate");
		mDataMap.put(8, "variab. e nevicate");
		mDataMap.put(9, "coperto piogge moderate");
		mDataMap.put(10, "coperto piogge abbondanti");
		mDataMap.put(11, "coperto piogge intense");
		mDataMap.put(12, "coperto piogge abbond.\ne temporali");
		mDataMap.put(13, "coperto con temporali");
		mDataMap.put(14, "coperto piogge abbond.\ne nevicate");
		mDataMap.put(15, "coperto con nevicate");
	}
	
	public String getData(ForecastDataStringMap dataMap)
	{
		String t = mName;
		if(evo00 != 100)
			t += "\n" + dataMap.get(ForecastDataStringMap.EVO04) + ": " + mDataMap.get(evo00);
		if(evo12 != 100)
			t += "\n" + dataMap.get(ForecastDataStringMap.EVO12) + ": " + mDataMap.get(evo12);
		if(evo24 != 100)
			t += "\n" + dataMap.get(ForecastDataStringMap.EVO20) + ": " + mDataMap.get(evo24);
		
		return t;	
	}
	
	@Override
	public ForecastDataType getType() {
		return ForecastDataType.ZONE;
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
