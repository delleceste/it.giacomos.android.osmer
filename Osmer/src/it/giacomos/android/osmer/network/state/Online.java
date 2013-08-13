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
				m_stateUpdateListener.onDownloadStart(DownloadReason.Init);
			else if(downloadStatus.downloadIncomplete())
				m_stateUpdateListener.onDownloadStart(DownloadReason.Incomplete);
			else if(downloadStatus.lastCompleteDownloadIsOld())
				m_stateUpdateListener.onDownloadStart(DownloadReason.DataExpired);
			
			downloadStatus.state = DownloadStatus.INIT;		
			/* starts bitmap task for today bmp and text task for situation */
			//
			mTotSteps = 9; /* manually set 9 steps */
			mStartBitmapTask(m_urls.todayImageUrl(), BitmapType.TODAY);
			mStartTextTask(m_urls.situationUrl(), ViewType.HOME);
			mGetObservationsTable(MapMode.LATEST_OBSERVATIONS);
		}
	}

	public StateName name() { return StateName.Online; }
	
	public String toString() { return name().toString(); }
	
	public void getSituation()
	{
		if(!dDownloadStatus.todayBmpDownloaded())
		{
			startBitmapTask(m_urls.todayImageUrl(), BitmapType.TODAY);
		}
		if(!dDownloadStatus.homeDownloaded())
		{
			startTextTask(m_urls.situationUrl(), ViewType.HOME);
		}
	}
	
	public void getTodayForecast()
	{
		if(!dDownloadStatus.todayBmpDownloaded())
		{
			startBitmapTask(m_urls.todayImageUrl(), BitmapType.TODAY);
		}
		if(!dDownloadStatus.todayDownloaded())
		{
			startTextTask(m_urls.todayUrl(), ViewType.TODAY);
		}
	}

	public void getTomorrowForecast()
	{
		if(!dDownloadStatus.tomorrowBmpDownloaded())
		{
			startBitmapTask(m_urls.tomorrowImageUrl(), BitmapType.TOMORROW);
		}
		if(!dDownloadStatus.tomorrowDownloaded())
		{
			startTextTask(m_urls.tomorrowUrl(), ViewType.TOMORROW);
		}
	}
	
	public void getTwoDaysForecast()
	{
		if(!dDownloadStatus.twoDaysBmpDownloaded())
		{
			startBitmapTask(m_urls.twoDaysImageUrl(), BitmapType.TWODAYS);
		}
		if(!dDownloadStatus.twoDaysDownloaded())
		{
			startTextTask(m_urls.twoDaysUrl(), ViewType.TWODAYS);
		}
	}
	
	public void getTodayTextOnly()
	{
		if(!dDownloadStatus.todayDownloaded())
		{
			startTextTask(m_urls.todayUrl(), ViewType.TODAY);
		}
	}

	public void getRadarImage()
	{
		/* always refresh radar image on request because it changes frequently.
		 * This call actually always returns true and the call is placed for analogy
		 * with the other similar methods
		 */
		if(!dDownloadStatus.radarImageDownloaded())
		{
			startBitmapTask(m_urls.radarImageUrl(), BitmapType.RADAR);
		}
	}
	
	/* for now, webcam list is obtained by a couple of files
	 * http://www.osmer.fvg.it/GOOGLE/DatiWebcams1.php
	 * and WebcamsList.xml
	 */
	public void getWebcamList()
	{
		if(!dDownloadStatus.webcamListDownloaded())
		{
//			Log.e("Online", "getWebcamList: downloading webcam update now");
			dDownloadStatus.setWebcamListsDownloadRequested(true);
			startTextTask(m_urls.webcamMapData(), ViewType.WEBCAMLIST_OSMER);
			startTextTask(m_urls.webcamsListXML(), ViewType.WEBCAMLIST_OTHER);
		}
//		else
//			Log.e("Online", "getWebcamList: data not too old, not starting download");
	}
	
	public void getObservationsTable(MapMode mapMode) 
	{
		mTotSteps++;
		m_stateUpdateListener.onDownloadStart(DownloadReason.PartialDownload);
		mGetObservationsTable(mapMode);
	}
	
	@Override
	public void onTextUpdate(String s, ViewType st, String errorMessage, AsyncTask<URL, Integer, String> task) 
	{
		long oldState = dDownloadStatus.state;
		dDownloadStatus.updateState(st, errorMessage.isEmpty());
		m_stateUpdateListener.onTextUpdate(s, st, errorMessage);
		/* publish progress , after DownloadStatus state has been updated */
		mCurrentStep++;
		m_stateUpdateListener.onProgressUpdate(mCurrentStep, mTotSteps);
		m_stateUpdateListener.onStateChanged(oldState, dDownloadStatus.state);
		mProgressNeedsReset();
		mMyTasks.remove(task);
	}

	public void onTextBytesUpdate(byte [] bytes, ViewType vt)
	{
		if(bytes != null)
			m_stateUpdateListener.onTextBytesUpdate(bytes, vt);
	}
	
	@Override
	public void onBitmapBytesUpdate(byte [] bytes, BitmapType bt)
	{
		m_stateUpdateListener.onBitmapBytesUpdate(bytes, bt);
	}
	
	@Override
	public void onBitmapUpdate(Bitmap bmp, BitmapType bt, String errorMessage, AsyncTask<URL, Integer, Bitmap> task) 
	{	
		// TODO Auto-generated method stub
		if(bt == BitmapType.TODAY && !dDownloadStatus.fullForecastDownloadRequested())
		{
			dDownloadStatus.setFullForecastDownloadRequested(true);
			mStartTextTask(m_urls.todayUrl(), ViewType.TODAY);
			mStartBitmapTask(m_urls.tomorrowImageUrl(), BitmapType.TOMORROW);
			mStartTextTask(m_urls.tomorrowUrl(), ViewType.TOMORROW);
			mStartBitmapTask(m_urls.twoDaysImageUrl(), BitmapType.TWODAYS);
			mStartTextTask(m_urls.twoDaysUrl(), ViewType.TWODAYS);
			mGetObservationsTable(MapMode.DAILY_OBSERVATIONS);
		}
		dDownloadStatus.updateState(bt, bmp != null);
		m_stateUpdateListener.onBitmapUpdate(bmp, bt, errorMessage);

		/* publish progress, after DownloadStatus state has been updated */
		mCurrentStep++;
		m_stateUpdateListener.onProgressUpdate(mCurrentStep, mTotSteps);
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
			mMyTasks.get(i).cancel(false);
		}
		mMyTasks.clear();
	}
	
	protected void startBitmapTask(String urlStr, BitmapType t)
	{
		mTotSteps++;
		m_stateUpdateListener.onDownloadStart(DownloadReason.PartialDownload);
		mStartBitmapTask(urlStr, t);
	}
	
	protected void startTextTask(String urlStr, ViewType t)
	{
		mTotSteps++;
		m_stateUpdateListener.onDownloadStart(DownloadReason.PartialDownload);
		mStartTextTask(urlStr, t);
	}
	
	protected void mStartBitmapTask(String urlStr, BitmapType t)
	{
		BitmapTask bitmapTask = new BitmapTask(this, t);
		try{
			URL url = new URL(urlStr);
			bitmapTask.parallelExecute(url);
			mMyTasks.add(bitmapTask);
		}
		catch(MalformedURLException e)
		{
			onBitmapUpdate(null, t, e.getMessage(), null);
		}
	}

	protected void mStartTextTask(String urlStr, ViewType t)
	{
		TextTask textTask = new TextTask(this, t);
		try{
			URL url = new URL(urlStr);
			textTask.parallelExecute(url);
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
		String referer = null;
		ViewType stringType;
		
		if(mapMode == MapMode.DAILY_OBSERVATIONS)
		{
			surl = m_urls.dailyTableUrl();
			referer = m_urls.dailyTableReferer();
			stringType = ViewType.DAILY_TABLE;
		}
		else
		{
			surl = m_urls.latestTableUrl();
			referer = m_urls.latestTableReferer();
			stringType = ViewType.LATEST_TABLE;
		}
		
		TextTask textTask = new TextTask(this, stringType);
		try{
			URL url = new URL(surl);
			textTask.setReferer(referer);
			textTask.parallelExecute(url);
			mMyTasks.add(textTask);
		}
		catch(MalformedURLException e)
		{
			onTextUpdate("Malformed url \"" + surl + "\"\n" , stringType, e.getMessage(), null);
		}
	}
	
	private void mProgressNeedsReset()
	{
		if(mCurrentStep == mTotSteps)
			mCurrentStep = mTotSteps = 0;
	}
	

	ArrayList<AsyncTask> mMyTasks;
	Urls m_urls;
	int mTotSteps;
	int mCurrentStep;
}
