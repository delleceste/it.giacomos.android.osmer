package it.giacomos.android.osmer.observations;

import it.giacomos.android.osmer.StringType;

import java.util.HashMap;

import android.os.AsyncTask;

public class TableToMapAsyncTask extends AsyncTask<String, Integer, 
HashMap<String, ObservationData> > {

	public TableToMapAsyncTask(StringType st, TableToMapUpdateListener l)
	{
		setStringType(st);
		mTableUpdateListener = l;
	}
	
	@Override
	protected HashMap<String, ObservationData> doInBackground(String... table) 
	{
		TableToMap tableToMap = new TableToMap();
		HashMap<String, ObservationData> hMap = tableToMap.convert(table[0], mStringType);
		tableToMap = null;
		return hMap;
	}

	public void onPostExecute(HashMap<String, ObservationData>  map)
	{
		mTableUpdateListener.onTableUpdate(map, mStringType);
	}
	
	public StringType getmtringType() {
		return mStringType;
	}

	public void setStringType(StringType mStringType) {
		this.mStringType = mStringType;
	}

	private StringType mStringType;
	private  TableToMapUpdateListener mTableUpdateListener;
}
