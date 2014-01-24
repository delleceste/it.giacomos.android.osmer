package it.giacomos.android.osmer.PROva.observations;
import java.util.HashMap;

import android.util.Log;
import it.giacomos.android.osmer.PROva.network.Data.DataPoolCacheUtils;
import it.giacomos.android.osmer.PROva.network.Data.DataPoolTextListener;
import it.giacomos.android.osmer.PROva.network.state.ViewType;
import it.giacomos.android.osmer.PROva.widgets.LatestObservationCacheChangeListener;

public class ObservationsCache implements TableToMapUpdateListener, DataPoolTextListener 
{
	public ObservationsCache()
	{
		mDailyMap = new HashMap <String, ObservationData> ();
		mLatestMap = new HashMap <String, ObservationData> ();
		mLatestObservation = "";
		mDailyObservation = "";
		mLatestObservationCacheChangeListener = null;
		mObservationsCacheUpdateListener = null;
	}

	public void installObservationsCacheUpdateListener(ObservationsCacheUpdateListener l)
	{
		mObservationsCacheUpdateListener = l;
		if(mLatestMap != null)
			l.onObservationsCacheUpdate(mLatestMap, ViewType.LATEST_TABLE);
		if(mDailyMap != null)
			l.onObservationsCacheUpdate(mDailyMap, ViewType.DAILY_TABLE);
	}
	
	/* register a cache update listener. 
	 * SituationImage in package widgets is a listener.
	 * If registered, whenever the cache changes, the listener is modified.
	 * The listener is notified inside store() which is called by this class
	 * in restoreLatestFromStorage or by onTextUpdate in OsmerActivity.
	 */
	public void setLatestObservationCacheChangeListener(LatestObservationCacheChangeListener l)
	{
		mLatestObservationCacheChangeListener = l;
		if(mLatestMap != null)
			l.onCacheUpdate(this);
	}

	public void clear()
	{
		mDailyMap.clear();
		mLatestMap.clear();
	}

	boolean hasDaily()
	{
		return !mDailyMap.isEmpty();
	}

	boolean hasLatest()
	{
		return !mLatestMap.isEmpty();
	}

	public void onTableUpdate(HashMap <String, ObservationData> map, ViewType t)
	{
//		Log.e("ObservationsCache.onTableUpdate", "got update type " + t);
		switch(t)
		{
		case DAILY_TABLE:
			mDailyMap = map;
			break;
		case LATEST_TABLE:
			mLatestMap = map;
			if(mLatestObservationCacheChangeListener != null)
				mLatestObservationCacheChangeListener.onCacheUpdate(this);
			break;
		}
		if(mObservationsCacheUpdateListener != null)
			mObservationsCacheUpdateListener.onObservationsCacheUpdate(map, t);
	}
	

	@Override
	public void onTextChanged(String s, ViewType t, boolean fromCache) 
	{
		if(t == ViewType.DAILY_TABLE && s != mDailyObservation)
		{
			/* call expensive TableToMap only if the string has changed */
			TableToMapAsyncTask at = new TableToMapAsyncTask(t, this);
			at.execute(s);
			mDailyObservation = s;
		}
		else if(s != mLatestObservation)
		{
			TableToMapAsyncTask at = new TableToMapAsyncTask(t, this);
			at.execute(s);
			mLatestObservation = s;
		}
	}

	@Override
	public void onTextError(String error, ViewType t) 
	{
		
	}

	public HashMap<String, ObservationData> getObservationData(MapMode mapMode) {
		switch(mapMode)
		{
		case DAILY_OBSERVATIONS:
			return getDailyObservationData();
		default:
			return getLatestObservationData();
		}
	}

	public HashMap <String, ObservationData> getDailyObservationData()
	{
		return mDailyMap;
	}

	public HashMap<String, ObservationData> getLatestObservationData()
	{
		return mLatestMap;
	}

	public ObservationData getObservationData(String location, ViewType t)
	{
		if(t == ViewType.DAILY_TABLE && mDailyMap.containsKey(location))
			return mDailyMap.get(location);
		else if(t == ViewType.LATEST_TABLE && mLatestMap.containsKey(location))
			return mLatestMap.get(location);
		return null;
	}

	private LatestObservationCacheChangeListener mLatestObservationCacheChangeListener;
	private HashMap <String, ObservationData> mDailyMap;
	private HashMap <String, ObservationData> mLatestMap;
	private String mLatestObservation, mDailyObservation;
	private  ObservationsCacheUpdateListener mObservationsCacheUpdateListener;
}
