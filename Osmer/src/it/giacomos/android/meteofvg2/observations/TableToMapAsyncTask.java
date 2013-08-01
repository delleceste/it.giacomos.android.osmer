package it.giacomos.android.meteofvg2.observations;

import it.giacomos.android.meteofvg2.ViewType;

import java.util.HashMap;

import android.os.AsyncTask;

public class TableToMapAsyncTask extends AsyncTask<String, Integer, 
HashMap<String, ObservationData> > {

	public TableToMapAsyncTask(ViewType st, TableToMapUpdateListener l)
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
	
	public ViewType getmtringType() {
		return mStringType;
	}

	public void setStringType(ViewType mStringType) {
		this.mStringType = mStringType;
	}

	private ViewType mStringType;
	private  TableToMapUpdateListener mTableUpdateListener;
}
