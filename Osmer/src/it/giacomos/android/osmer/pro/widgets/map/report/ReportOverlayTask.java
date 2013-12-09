package it.giacomos.android.osmer.pro.widgets.map.report;

import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.pro.widgets.map.MapBaloonInfoWindowAdapter;

import java.util.ArrayList;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

public class ReportOverlayTask extends AsyncTask<DataInterface, Integer, DataInterface[] > 
{
	private Context mContext;
	private ReportOverlayTaskListener mReportOverlayTaskListener;
	
	public ReportOverlayTask(Context ctx, ReportOverlayTaskListener rotl)
	{
		super();
		mContext = ctx;
		mReportOverlayTaskListener = rotl;
	}
	
	@Override
	protected DataInterface[] doInBackground(DataInterface... params) 
	{
		if(params == null)
			return null;
		
		int dataSiz = params.length;
		MarkerOptions markerOptions;
		for(int i = 0; i < dataSiz; i++)
		{
			if(this.isCancelled())
				break;
			
			DataInterface dataInterface = params[i];
			dataInterface.buildMarkerOptions(mContext);
		}
		return params;
	}
	
	@Override
	public void onCancelled(DataInterface [] dataI)
	{
		
	}
	
	@Override
	public void onPostExecute(DataInterface [] dataI)
	{
		mReportOverlayTaskListener.onReportOverlayTaskFinished(dataI);
	}
}
