package it.giacomos.android.osmer.pro.widgets.map.report.network;

import it.giacomos.android.osmer.pro.network.state.Urls;
import android.os.AsyncTask;

public class PostReport implements PostReportTaskListener, PostInterface
{
	private ReportPublishedListener mReportPublishedListener;
	
	public PostReport(String user, String deviceId, String locality, 
			double lat, double lng, int sky, int wind, 
			String temp, String comment,  ReportPublishedListener lis)
	{
		mReportPublishedListener = lis;
		PostReportTask reportTask = new PostReportTask(user, deviceId, locality, lat, lng, sky, wind, temp, comment, this);
		String url = new Urls().postReportUrl();
		reportTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
	}

	@Override
	public void onTaskCompleted( boolean error, String message) 
	{
		mReportPublishedListener.onPostActionResult(error, message, PostType.REPORT);
	}

	@Override
	public PostType getType() {
		return PostType.REPORT;
	}
}
