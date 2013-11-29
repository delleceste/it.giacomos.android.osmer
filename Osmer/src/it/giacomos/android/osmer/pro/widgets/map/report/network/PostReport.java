package it.giacomos.android.osmer.pro.widgets.map.report.network;

import com.google.android.gms.maps.model.LatLng;

import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.pro.network.state.Urls;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class PostReport implements PostReportTaskListener
{
	private Context mContext;
	
	public PostReport(String user, double lat, double lng, int sky, int wind, String temp, String comment, Context ctx)
	{
		mContext = ctx;
		PostReportTask reportTask = new PostReportTask(user, lat, lng, sky, wind, temp, comment, this);
		String url = new Urls().postReportUrl();
		reportTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
	}

	@Override
	public void onTaskCompleted(boolean error, String message) 
	{
		if(!error)
			Toast.makeText(mContext, R.string.reportOk, Toast.LENGTH_SHORT).show();
		else
		{
			String m = mContext.getResources().getString(R.string.reportError) + "\n" + message;
			Toast.makeText(mContext, m, Toast.LENGTH_LONG).show();
		}
		
	}

}
