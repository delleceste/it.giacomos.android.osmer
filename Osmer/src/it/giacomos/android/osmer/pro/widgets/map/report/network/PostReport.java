package it.giacomos.android.osmer.pro.widgets.map.report.network;

import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.pro.network.state.Urls;
import it.giacomos.android.osmer.pro.widgets.map.OMapFragment;
import it.giacomos.android.osmer.pro.widgets.map.ReportPublishedListener;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class PostReport implements PostReportTaskListener
{
	private Context mContext;
	private ReportPublishedListener mReportPublishedListener;
	
	public PostReport(String user, String locality, 
			double lat, double lng, int sky, int wind, 
			String temp, String comment, OMapFragment frag)
	{
		mContext = frag.getActivity().getApplicationContext();
		mReportPublishedListener = frag;
		PostReportTask reportTask = new PostReportTask(user, locality, lat, lng, sky, wind, temp, comment, this);
		String url = new Urls().postReportUrl();
		reportTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
	}

	@Override
	public void onTaskCompleted(boolean error, String message) 
	{
		if(!error)
		{
			Toast.makeText(mContext, R.string.reportOk, Toast.LENGTH_SHORT).show();
			mReportPublishedListener.onReportPublished();
		}
		else
		{
			String m = mContext.getResources().getString(R.string.reportError) + "\n" + message;
			Toast.makeText(mContext, m, Toast.LENGTH_LONG).show();
		}
		
	}

}
