package it.giacomos.android.meteofvg2.observations;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import it.giacomos.android.meteofvg2.ViewType;
import it.giacomos.android.meteofvg2.widgets.LatestObservationCacheChangeListener;
import android.content.Context;
import android.util.Log;

public class ObservationsCache implements TableToMapUpdateListener 
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
	
	public void store(String s, ViewType t)
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

	public HashMap<String, ObservationData> getObservationData(ObservationTime oTime) {
		switch(oTime)
		{
		case DAILY:
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

	public boolean restoreFromStorage(Context ctx)
	{
		String data = "";
		final String[] filenames = { "latest_observations.txt", "daily_observations.txt" };
		for(String filename : filenames)
		{
			data = ""; /* clear! */
			try {
				String line;
				BufferedReader in = new BufferedReader(
						new FileReader(ctx.getFilesDir().getAbsolutePath() + "/" + filename));
				try {
					line = in.readLine();
					while(line != null)
					{
						data += line + "\n";
						line = in.readLine();
					}
				} 
				catch (IOException e) {}		
			} 
			catch (FileNotFoundException e) {}

			/* store the read file or the empty string */
			ViewType stringType;
			if(filename == "latest_observations.txt")
				stringType = ViewType.LATEST_TABLE;
			else
				stringType = ViewType.DAILY_TABLE;

			this.store(data, stringType);
		}

		return true;
	}

	public boolean saveLatestToStorage(Context ctx)
	{
		if(mLatestObservation != "")
		{
			try {
				FileOutputStream fos = ctx.openFileOutput("latest_observations.txt", Context.MODE_PRIVATE);
				fos.write(mLatestObservation.getBytes());
				fos.close();
			} 
			catch (FileNotFoundException e) {
				/* nada que hacer */
			}
			catch (IOException e) {

			}
		}
		if(mDailyObservation != "")
		{
			try {
				FileOutputStream fos = ctx.openFileOutput("daily_observations.txt", Context.MODE_PRIVATE);
				fos.write(mDailyObservation.getBytes());
				fos.close();
			} 
			catch (FileNotFoundException e) {
				/* nada que hacer */
			}
			catch (IOException e) {

			}
		}
		return true; 
	}


	private LatestObservationCacheChangeListener mLatestObservationCacheChangeListener;
	private HashMap <String, ObservationData> mDailyMap;
	private HashMap <String, ObservationData> mLatestMap;
	private String mLatestObservation, mDailyObservation;
	private  ObservationsCacheUpdateListener mObservationsCacheUpdateListener;

}
