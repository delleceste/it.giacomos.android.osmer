package it.giacomos.android.osmer.PROva.forecastRepr;

import it.giacomos.android.osmer.PROva.R;
import it.giacomos.android.osmer.PROva.widgets.LocationToImgPixelMapper;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;

public class Zone implements ForecastDataInterface {
	
	private LatLng mLatLng;
	private String mId, mName;
	public int evo00, evo12, evo24;
	public SparseArray<String> mDataMap;
	private Region mRegion;
	private boolean mIsSelected;
	
	public Zone(String id, Resources res)
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
		
		mDataMap.put(0, res.getString(R.string.evo0));
		mDataMap.put(1, res.getString(R.string.evo1));
		mDataMap.put(2, res.getString(R.string.evo2));
		mDataMap.put(3, res.getString(R.string.evo3));
		mDataMap.put(4, res.getString(R.string.evo4));
		mDataMap.put(5, res.getString(R.string.evo5));
		mDataMap.put(6, res.getString(R.string.evo6));
		mDataMap.put(7, res.getString(R.string.evo7));
		mDataMap.put(8, res.getString(R.string.evo8));
		mDataMap.put(9, res.getString(R.string.evo9));
		mDataMap.put(10, res.getString(R.string.evo10));
		mDataMap.put(11, res.getString(R.string.evo11));
		mDataMap.put(12, res.getString(R.string.evo12));
		mDataMap.put(13, res.getString(R.string.evo13));
		mDataMap.put(14, res.getString(R.string.evo14));
		mDataMap.put(15, res.getString(R.string.evo15));
		
		mIsSelected = false;
	}
	
	public String getData(ForecastDataStringMap dataMap)
	{
		String t = mName + ", " + dataMap.get(ForecastDataStringMap.EVO) + ":";
		if(evo00 != 100)
			t += "\n" + dataMap.get(ForecastDataStringMap.EVO04) + ": " + mDataMap.get(evo00);
		if(evo12 != 100)
			t += "\n" + dataMap.get(ForecastDataStringMap.EVO12) + ": " + mDataMap.get(evo12);
		if(evo24 != 100)
			t += "\n" + dataMap.get(ForecastDataStringMap.EVO20) + ": " + mDataMap.get(evo24);
		
		return t;	
	}
	
	public boolean isSelected()
	{
		return mIsSelected;
	}
	
	public void setSelected(boolean s)
	{
		mIsSelected = s;
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
