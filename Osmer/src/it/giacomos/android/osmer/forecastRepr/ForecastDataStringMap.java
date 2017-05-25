package it.giacomos.android.osmer.forecastRepr;

import android.content.res.Resources;
import android.util.SparseArray;

import it.giacomos.android.osmer.R;

public class ForecastDataStringMap {

	private SparseArray<String> mStringMap;

	public static final int SNOW  = 1010;
	public static final int STORMS  = 1013;
	public static final int T1000 = 11000;
	public static final int T2000 = 12000;
	public static final int TMIN = 15000;
	public static final int TMAX = 16000;
	public static final int WIND = 20000;

	public static final int WIND3000 = 20003;
	public static final int WIND2000 = 20002;
	public static final int M_SEC = 21000;
	public static final int KM_HOUR = 22000;
	
	public static final int EVO = 23000;
	public static final int EVO04 = 24000;
	public static final int EVO12 = 21200;
	public static final int EVO20 = 22200;
	
	public String get(int key)
	{
		return mStringMap.get(key);
	}
	
	public ForecastDataStringMap(Resources res)
	{
		mStringMap = new SparseArray<String>();
		mStringMap.put(0, res.getString(R.string.sky0));
		mStringMap.put(1, res.getString(R.string.sky1));
		mStringMap.put(2, res.getString(R.string.sky2));
		mStringMap.put(3, res.getString(R.string.sky3));
		mStringMap.put(4, res.getString(R.string.sky4));
		mStringMap.put(5, res.getString(R.string.sky5));
		mStringMap.put(6, res.getString(R.string.rain6));
		mStringMap.put(7, res.getString(R.string.rain7_abbrev));
		mStringMap.put(8, res.getString(R.string.rain8_abbrev));
		mStringMap.put(9, res.getString(R.string.rain9));
		mStringMap.put(36, res.getString(R.string.rain36));
		mStringMap.put(10, res.getString(R.string.snow10));
		mStringMap.put(11, res.getString(R.string.snow11));
		mStringMap.put(12, res.getString(R.string.snow12));
		mStringMap.put(13, res.getString(R.string.storm));
		mStringMap.put(14, res.getString(R.string.mist14));
		mStringMap.put(15, res.getString(R.string.mist15));

		mStringMap.put(16, res.getString(R.string.wind16));
		mStringMap.put(17, res.getString(R.string.wind17));
		mStringMap.put(18, res.getString(R.string.wind18));
		mStringMap.put(19, res.getString(R.string.wind19));
		mStringMap.put(20, res.getString(R.string.wind20));
		mStringMap.put(21, res.getString(R.string.wind21));
		mStringMap.put(22, res.getString(R.string.wind22));
		mStringMap.put(23, res.getString(R.string.wind23));
		mStringMap.put(24, res.getString(R.string.wind24));
		mStringMap.put(25, res.getString(R.string.wind25));
		mStringMap.put(26, res.getString(R.string.wind26));
		mStringMap.put(27, res.getString(R.string.wind27));
		mStringMap.put(28, res.getString(R.string.wind28));
		mStringMap.put(29, res.getString(R.string.wind29));
		mStringMap.put(30, res.getString(R.string.wind30));
		mStringMap.put(31, res.getString(R.string.wind31));
		mStringMap.put(32, res.getString(R.string.wind32));
		mStringMap.put(33, res.getString(R.string.wind33));
		mStringMap.put(34, res.getString(R.string.wind34));
		mStringMap.put(35, res.getString(R.string.wind35));

		mStringMap.put(1000, res.getString(R.string.sky));
		mStringMap.put(1006, res.getString(R.string.rain));
		mStringMap.put(SNOW, res.getString(R.string.snow));
		mStringMap.put(STORMS, res.getString(R.string.storms));
		mStringMap.put(1014, res.getString(R.string.mist));
		mStringMap.put(1016, res.getString(R.string.wind));
		
		mStringMap.put(T1000, res.getString(R.string.t1000));
		mStringMap.put(T2000, res.getString(R.string.t2000));
		mStringMap.put(TMIN, res.getString(R.string.min_temp_abbr));
		mStringMap.put(TMAX, res.getString(R.string.max_temp_abbr));
		
		mStringMap.put(WIND, res.getString(R.string.wind_lowercase));
		mStringMap.put(WIND3000, res.getString(R.string.wind_3000));
		mStringMap.put(WIND2000, res.getString(R.string.wind_2000));
		mStringMap.put(M_SEC, res.getString(R.string.mets_per_sec));
		mStringMap.put(KM_HOUR, res.getString(R.string.km_per_h));
		
		mStringMap.put(EVO, res.getString(R.string.evolution));
		mStringMap.put(EVO04, res.getString(R.string.evolution04));
		mStringMap.put(EVO12, res.getString(R.string.evolution12));
		mStringMap.put(EVO20, res.getString(R.string.evolution20));
		
	}
}
