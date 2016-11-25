package it.giacomos.android.osmer.network.state;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import it.giacomos.android.osmer.network.DownloadManagerUpdateListener;
import it.giacomos.android.osmer.network.DownloadReason;
import it.giacomos.android.osmer.network.DownloadStatus;
import it.giacomos.android.osmer.observations.MapMode;

public class Online extends State implements BitmapTaskListener, TextTaskListener {

	/** 
	 * When the Online state class is instantiated, it checks if the download
	 * is complete and not too old.
	 * If it is not complete or it is too old, the download status is put in
	 * INIT state, as to indicate that the download is not complete and a 
	 * full download starts.
	 * Setting status in INIT state (its state is put to 0) means that all
	 * XXX_DOWNLOADED flags are discarded.
	 * 
	 * @param stateUpdateListener
	 * @param downloadStatus
	 */
	public Online(DownloadManagerUpdateListener stateUpdateListener,
			DownloadStatus downloadStatus) 
	{
		super(stateUpdateListener, downloadStatus);

		m_urls = new Urls();
		mTotSteps = 0;
		mCurrentStep = 0;
		mMyTasks = new ArrayList<AsyncTask>();
		downloadStatus.isOnline = true;

		if(downloadStatus.downloadIncomplete() || downloadStatus.lastCompleteDownloadIsOld())
		{
			if(downloadStatus.state == DownloadStatus.INIT)
				m_downloadManagerUpdateListener.onDownloadStart(DownloadReason.Init);
			else if(downloadStatus.downloadIncomplete())
				m_downloadManagerUpdateListener.onDownloadStart(DownloadReason.Incomplete);
			else if(downloadStatus.lastCompleteDownloadIsOld())
				m_downloadManagerUpdateListener.onDownloadStart(DownloadReason.DataExpired);

			downloadStatus.state = DownloadStatus.INIT;
			
			/* starts text task for today symbols and text task for situation */
			startTextTask(m_urls.todaySymtableUrl(), ViewType.TODAY_SYMTABLE);
			startTextTask(m_urls.situationUrl(), ViewType.HOME);
			mGetObservationsTable(MapMode.LATEST_OBSERVATIONS);
		}
	}

	public StateName name() { return StateName.Online; }

	public String toString() { return name().toString(); }

	public void getSituation()
	{
		if(!dDownloadStatus.latestTableDownloaded() && !mIsTextTaskExecuting(ViewType.LATEST_TABLE))
		{
			mGetObservationsTable(MapMode.LATEST_OBSERVATIONS);
		}
		if(!dDownloadStatus.homeDownloaded() && !mIsTextTaskExecuting(ViewType.HOME))
		{
			startTextTask(m_urls.situationUrl(), ViewType.HOME);
		}
	}

	public void getTodayForecast()
	{
		if(!dDownloadStatus.todaySymtableDownloaded() && !mIsTextTaskExecuting(ViewType.TODAY_SYMTABLE))
		{
			startTextTask(m_urls.todaySymtableUrl(), ViewType.TODAY_SYMTABLE);
		}
		if(!dDownloadStatus.todayDownloaded() && !mIsTextTaskExecuting(ViewType.TODAY))
		{
			startTextTask(m_urls.todayUrl(), ViewType.TODAY);
		}
	}

	public void getTomorrowForecast()
	{
		if(!dDownloadStatus.tomorrowSymtableDownloaded() && !mIsTextTaskExecuting(ViewType.TOMORROW_SYMTABLE))
		{
			startTextTask(m_urls.tomorrowSymtableUrl(), ViewType.TOMORROW_SYMTABLE);
		}
		if(!dDownloadStatus.tomorrowDownloaded() && !mIsTextTaskExecuting(ViewType.TOMORROW))
		{
			startTextTask(m_urls.tomorrowUrl(), ViewType.TOMORROW);
		}
	}

	public void getTwoDaysForecast()
	{
		if(!dDownloadStatus.twoDaysSymtableDownloaded() && !mIsTextTaskExecuting(ViewType.TWODAYS_SYMTABLE))
		{
			startTextTask(m_urls.twoDaysSymtableUrl(), ViewType.TWODAYS_SYMTABLE);
		}
		if(!dDownloadStatus.twoDaysDownloaded() && !mIsTextTaskExecuting(ViewType.TWODAYS))
		{
			startTextTask(m_urls.twoDaysUrl(), ViewType.TWODAYS);
		}
	}

	public void getThreeDaysForecast()
	{
		if(!dDownloadStatus.threeDaysSymtableDownloaded() && !mIsTextTaskExecuting(ViewType.THREEDAYS_SYMTABLE))
		{
			startTextTask(m_urls.threeDaysSymtableUrl(), ViewType.THREEDAYS_SYMTABLE);
		}
		if(!dDownloadStatus.threeDaysDownloaded() && !mIsTextTaskExecuting(ViewType.THREEDAYS))
		{
			startTextTask(m_urls.threeDaysUrl(), ViewType.THREEDAYS);
		}
	}

	public void getFourDaysForecast()
	{
		if(!dDownloadStatus.fourDaysSymtableDownloaded() && !mIsTextTaskExecuting(ViewType.FOURDAYS_SYMTABLE))
		{
			startTextTask(m_urls.fourDaysSymtableUrl(), ViewType.FOURDAYS_SYMTABLE);
		}
		if(!dDownloadStatus.fourDaysDownloaded() && !mIsTextTaskExecuting(ViewType.FOURDAYS))
		{
			startTextTask(m_urls.fourDaysUrl(), ViewType.FOURDAYS);
		}
	}

	public void getTodayTextOnly()
	{
		if(!dDownloadStatus.todayDownloaded()  && !mIsTextTaskExecuting(ViewType.TODAY))
		{
			startTextTask(m_urls.todayUrl(), ViewType.TODAY);
		}
	}

	public void getRadarImage(String source)
	{
		/* always refresh radar image on request because it changes frequently.
		 * This call actually always returns true and the call is placed for analogy
		 * with the other similar methods
		 */
		if(!dDownloadStatus.radarImageDownloaded())
		{
			startBitmapTask(m_urls.radarImageUrl(source), BitmapType.RADAR);
		}
	}
	
	public void getReport(String url)
	{
		if(!mIsTextTaskExecuting(ViewType.REPORT))
			startTextTask(url, ViewType.REPORT);
	}

	public void getObservationsTable(MapMode mapMode) 
	{
		mTotSteps++;
	//	Log.e("getObservationsTable", "mapMode " + mapMode + " dTotSteps " + mTotSteps);
		m_downloadManagerUpdateListener.onDownloadStart(DownloadReason.PartialDownload);
		mGetObservationsTable(mapMode);
	}

	@Override
	public void onTextUpdate(String s, ViewType vt, String errorMessage, AsyncTask<URL, Integer, String> task) 
	{
		long oldState = dDownloadStatus.state;
		/* in version < 2.3, we used to complete download in onBitmapUpdate after BitmapType.TODAY was
		 * downloaded. Following the same logic, after HOME has been downloaded we complete
		 * the data download.
		 */
		if(vt == ViewType.HOME /* && !dDownloadStatus.fullForecastDownloadRequested() */)
		{
//			dDownloadStatus.setFullForecastDownloadRequested(true);
			
			if(!mIsTextTaskExecuting(ViewType.TODAY))
				startTextTask(m_urls.todayUrl(), ViewType.TODAY);
			
			if(!mIsTextTaskExecuting(ViewType.TOMORROW_SYMTABLE))
				startTextTask(m_urls.tomorrowSymtableUrl(), ViewType.TOMORROW_SYMTABLE);
			
			if(!mIsTextTaskExecuting(ViewType.TOMORROW))
				startTextTask(m_urls.tomorrowUrl(), ViewType.TOMORROW);
			
			if(!mIsTextTaskExecuting(ViewType.TWODAYS_SYMTABLE))
				startTextTask(m_urls.twoDaysSymtableUrl(), ViewType.TWODAYS_SYMTABLE);
			
			if(!mIsTextTaskExecuting(ViewType.THREEDAYS_SYMTABLE))
				startTextTask(m_urls.threeDaysSymtableUrl(), ViewType.THREEDAYS_SYMTABLE);
			
			if(!mIsTextTaskExecuting(ViewType.FOURDAYS_SYMTABLE))
				startTextTask(m_urls.fourDaysSymtableUrl(), ViewType.FOURDAYS_SYMTABLE);
			
			if(!mIsTextTaskExecuting(ViewType.TWODAYS))
				startTextTask(m_urls.twoDaysUrl(), ViewType.TWODAYS);
			
			if(!mIsTextTaskExecuting(ViewType.THREEDAYS))
				startTextTask(m_urls.threeDaysUrl(), ViewType.THREEDAYS);
			
			if(!mIsTextTaskExecuting(ViewType.FOURDAYS))
				startTextTask(m_urls.fourDaysUrl(), ViewType.FOURDAYS);
			
			if(!mIsTextTaskExecuting(ViewType.DAILY_TABLE))
				mGetObservationsTable(MapMode.DAILY_OBSERVATIONS);
		}
		
		dDownloadStatus.updateState(vt, errorMessage.isEmpty());
		m_downloadManagerUpdateListener.onTextUpdate(s, vt, errorMessage);
		/* publish progress , after DownloadStatus state has been updated */
		mCurrentStep++;
		m_downloadManagerUpdateListener.onProgressUpdate(mCurrentStep, mTotSteps);
		m_downloadManagerUpdateListener.onStateChanged(oldState, dDownloadStatus.state);
		mProgressNeedsReset();
		mMyTasks.remove(task);
	}

	public void onTextBytesUpdate(byte [] bytes, ViewType vt)
	{
		if(bytes != null)
			m_downloadManagerUpdateListener.onTextBytesUpdate(bytes, vt);
	}

	@Override
	public void onBitmapBytesUpdate(byte [] bytes, BitmapType bt)
	{
		m_downloadManagerUpdateListener.onBitmapBytesUpdate(bytes, bt);
	}

	@Override
	public void onBitmapUpdate(Bitmap bmp, BitmapType bt, String errorMessage, AsyncTask<URL, Integer, Bitmap> task) 
	{			
		dDownloadStatus.updateState(bt, bmp != null);
		m_downloadManagerUpdateListener.onBitmapUpdate(bmp, bt, errorMessage);

		/* publish progress, after DownloadStatus state has been updated */
		mCurrentStep++;
		m_downloadManagerUpdateListener.onProgressUpdate(mCurrentStep, mTotSteps);
		mProgressNeedsReset();
		mMyTasks.remove(task);
	}

	public void cancelRunningTasks()
	{
		for(int i = 0; i < mMyTasks.size(); i++)
		{
			/* try to cancel the task. At least, onPostExecute is not called 
			 * Calling this method guarantees that onPostExecute(Object) is never invoked.
			 */
//			Log.e("cancelRunningTasks", "state " + mMyTasks.get(i).getStatus());
			mMyTasks.get(i).cancel(false);
		}
		mMyTasks.clear();
	}

	private void startBitmapTask(String urlStr, BitmapType t)
	{
		BitmapTask bitmapTask = new BitmapTask(this, t);
		try{
			URL url = new URL(urlStr);
			bitmapTask.parallelExecute(url);
			mTotSteps++;
			mMyTasks.add(bitmapTask);
		}
		catch(MalformedURLException e)
		{
			onBitmapUpdate(null, t, e.getMessage(), null);
		}

//		Log.e("startBitmapTask", "type " + t + " dTotSteps " + mTotSteps);
	}

	private void startTextTask(String urlStr, ViewType t)
	{
		TextTask textTask = new TextTask(this, t);
		try{
			URL url = new URL(urlStr);
			textTask.parallelExecute(url);
			mTotSteps++;
			mMyTasks.add(textTask);
		}
		catch(MalformedURLException e)
		{
			onTextUpdate("Malformed url \"" + urlStr + "\"\n" , t, e.getMessage(), null);
		}
	}

	protected void mGetObservationsTable(MapMode mapMode)
	{
		/* start text task ... */
		String surl = null;
		ViewType viewType;

		if(mapMode == MapMode.DAILY_OBSERVATIONS)
		{
			surl = m_urls.dailyTableUrl();
			viewType = ViewType.DAILY_TABLE;
		}
		else
		{
			surl = m_urls.latestTableUrl();
			viewType = ViewType.LATEST_TABLE;
		}

		TextTask textTask = new TextTask(this, viewType);
		try{
			URL url = new URL(surl);
			textTask.parallelExecute(url);
			mTotSteps++;
			mMyTasks.add(textTask);
		}
		catch(MalformedURLException e)
		{
			onTextUpdate("Malformed url \"" + surl + "\"\n" , viewType, e.getMessage(), null);
		}
	}

	private boolean mIsTextTaskExecuting(ViewType vt)
	{
		for(AsyncTask<?, ?, ?> at : mMyTasks)
		{
			if(at instanceof TextTask )
			{
				TextTask tt = (TextTask) at;
				if(tt.getType() == vt && tt.getStatus() == AsyncTask.Status.RUNNING)
				{
//					Log.e("mIsTextTaskExecuting", "TextTask " + vt + " is already RUNNING");
					return true;
				}
			}
		}
//		Log.e("mIsTextTaskExecuting", "TextTask " + vt + " is NOT RUNNING");
		return false;	
	}

	private void mProgressNeedsReset()
	{
		if(mCurrentStep == mTotSteps)
		{
			mCurrentStep = mTotSteps = 0;
//			"mProgressNeedsReset", " dTotSteps " + mTotSteps);
		}
	}


	ArrayList<AsyncTask> mMyTasks;
	Urls m_urls;
	int mTotSteps;
	int mCurrentStep;
}
