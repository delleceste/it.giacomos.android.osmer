package it.giacomos.android.osmer.downloadManager.state;

import java.net.MalformedURLException;
import java.net.URL;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;
import it.giacomos.android.osmer.BitmapType;
import it.giacomos.android.osmer.ProgressBarParams;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.StringType;
import it.giacomos.android.osmer.downloadManager.DownloadManager;
import it.giacomos.android.osmer.downloadManager.DownloadReason;
import it.giacomos.android.osmer.downloadManager.DownloadStatus;
import it.giacomos.android.osmer.downloadManager.DownloadManagerUpdateListener;
import it.giacomos.android.osmer.guiHelpers.TitlebarUpdater;
import it.giacomos.android.osmer.observations.ObservationTime;
import it.giacomos.android.osmer.observations.ObservationType;

public class Online extends State implements BitmapListener, TextListener {

	public Online(DownloadManagerUpdateListener stateUpdateListener) {
		super(stateUpdateListener);
		
		m_urls = new Urls();
		mTotSteps = 0;
		mCurrentStep = 0;
		DownloadStatus downloadStatus = DownloadStatus.Instance();
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
			mStartTextTask(m_urls.situationUrl(), StringType.HOME);
			mGetObservationsTable(ObservationTime.LATEST);
		}
	}

	public StateName name() { return StateName.Online; }
	
	public String toString() { return name().toString(); }
	
	
	public void getSituation()
	{
		if(!DownloadStatus.Instance().todayBmpDownloaded())
		{
			startBitmapTask(m_urls.todayImageUrl(), BitmapType.TODAY);
		}
		if(!DownloadStatus.Instance().homeDownloaded())
		{
			startTextTask(m_urls.situationUrl(), StringType.HOME);
		}
	}
	
	public void getTodayForecast()
	{
		if(!DownloadStatus.Instance().todayBmpDownloaded())
		{
			startBitmapTask(m_urls.todayImageUrl(), BitmapType.TODAY);
		}
		if(!DownloadStatus.Instance().todayDownloaded())
		{
			startTextTask(m_urls.todayUrl(), StringType.TODAY);
		}
	}

	public void getTomorrowForecast()
	{
		if(!DownloadStatus.Instance().tomorrowBmpDownloaded())
		{
			startBitmapTask(m_urls.tomorrowImageUrl(), BitmapType.TOMORROW);
		}
		if(!DownloadStatus.Instance().tomorrowDownloaded())
		{
			startTextTask(m_urls.tomorrowUrl(), StringType.TOMORROW);
		}
	}
	
	public void getTwoDaysForecast()
	{
		if(!DownloadStatus.Instance().twoDaysBmpDownloaded())
		{
			startBitmapTask(m_urls.twoDaysImageUrl(), BitmapType.TWODAYS);
		}
		if(!DownloadStatus.Instance().twoDaysDownloaded())
		{
			startTextTask(m_urls.twoDaysUrl(), StringType.TWODAYS);
		}
	}
	
	public void getTodayTextOnly()
	{
		if(!DownloadStatus.Instance().todayDownloaded())
		{
			startTextTask(m_urls.todayUrl(), StringType.TODAY);
		}
	}

	public void getRadarImage()
	{
		/* always refresh radar image on request because it changes frequently.
		 * This call actually always returns true and the call is placed for analogy
		 * with the other similar methods
		 */
		if(!DownloadStatus.Instance().radarImageDownloaded())
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
		if(!DownloadStatus.Instance().webcamListDownloaded())
		{
			DownloadStatus.Instance().setWebcamListsDownloadRequested(true);
			startTextTask(m_urls.webcamMapData(), StringType.WEBCAMLIST_OSMER);
			startTextTask(m_urls.webcamsListXML(), StringType.WEBCAMLIST_OTHER);
		}
	}
	
	public void getObservationsTable(ObservationTime oTime) 
	{
		mTotSteps++;
		m_stateUpdateListener.onDownloadStart(DownloadReason.PartialDownload);
		mGetObservationsTable(oTime);
	}
	
	@Override
	public void onTextUpdate(String s, StringType st, String errorMessage) 
	{
		DownloadStatus downloadStatus = DownloadStatus.Instance();
		long oldState = downloadStatus.state;
		if(!errorMessage.isEmpty())
			Log.e("onTextUpdate(Online)", "error" + errorMessage);
		DownloadStatus.Instance().updateState(st, errorMessage.isEmpty());
		m_stateUpdateListener.onTextUpdate(s, st, errorMessage);
		/* publish progress , after DownloadStatus state has been updated */
		mCurrentStep++;
		Log.e("ontextUpdate" , "tot steps " + mTotSteps + " current step " + mCurrentStep);
		m_stateUpdateListener.onProgressUpdate(mCurrentStep, mTotSteps);
		m_stateUpdateListener.onStateChanged(oldState, downloadStatus.state);
		mProgressNeedsReset();
	}

	@Override
	public void onBitmapUpdate(Bitmap bmp, BitmapType bt, String errorMessage) 
	{	
		// TODO Auto-generated method stub
		if(bt == BitmapType.TODAY && !DownloadStatus.Instance().fullForecastDownloadRequested())
		{
			DownloadStatus.Instance().setFullForecastDownloadRequested(true);
			mStartTextTask(m_urls.todayUrl(), StringType.TODAY);
			mStartBitmapTask(m_urls.tomorrowImageUrl(), BitmapType.TOMORROW);
			mStartTextTask(m_urls.tomorrowUrl(), StringType.TOMORROW);
			mStartBitmapTask(m_urls.twoDaysImageUrl(), BitmapType.TWODAYS);
			mStartTextTask(m_urls.twoDaysUrl(), StringType.TWODAYS);
			mGetObservationsTable(ObservationTime.DAILY);
		}
		DownloadStatus.Instance().updateState(bt, bmp != null);
		m_stateUpdateListener.onBitmapUpdate(bmp, bt, errorMessage);

		/* publish progress, after DownloadStatus state has been updated */
		mCurrentStep++;
	//	Log.i("onBitmapUpdate" , "tot steps " + mTotSteps + " current step " + mCurrentStep);
		m_stateUpdateListener.onProgressUpdate(mCurrentStep, mTotSteps);
		mProgressNeedsReset();
	}
	
	protected void startBitmapTask(String urlStr, BitmapType t)
	{
		mTotSteps++;
		m_stateUpdateListener.onDownloadStart(DownloadReason.PartialDownload);
		mStartBitmapTask(urlStr, t);
	}
	
	protected void startTextTask(String urlStr, StringType t)
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
			bitmapTask.execute(url);
		}
		catch(MalformedURLException e)
		{
			onBitmapUpdate(null, t, e.getMessage());
		}
	}

	protected void mStartTextTask(String urlStr, StringType t)
	{
		TextTask textTask = new TextTask(this, t);
		try{
			URL url = new URL(urlStr);
			textTask.execute(url);
		}
		catch(MalformedURLException e)
		{
			onTextUpdate("Malformed url \"" + urlStr + "\"\n" , t, e.getMessage());
		}
	}
	
	protected void mGetObservationsTable(ObservationTime oTime)
	{
		/* start text task ... */
		String surl = null;
		String referer = null;
		StringType stringType;
		
		if(oTime == ObservationTime.DAILY)
		{
			surl = m_urls.dailyTableUrl();
			referer = m_urls.dailyTableReferer();
			stringType = StringType.DAILY_TABLE;
		}
		else
		{
			surl = m_urls.latestTableUrl();
			referer = m_urls.latestTableReferer();
			stringType = StringType.LATEST_TABLE;
		}
		
		TextTask textTask = new TextTask(this, stringType);
		try{
			URL url = new URL(surl);
			textTask.setReferer(referer);
			textTask.execute(url);
		}
		catch(MalformedURLException e)
		{
			onTextUpdate("Malformed url \"" + surl + "\"\n" , stringType, e.getMessage());
		}
	}
	
	private void mProgressNeedsReset()
	{
		if(mCurrentStep == mTotSteps)
			mCurrentStep = mTotSteps = 0;
	}
	

	
	Urls m_urls;
	int mTotSteps;
	int mCurrentStep;
}
