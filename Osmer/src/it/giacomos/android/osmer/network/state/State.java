package it.giacomos.android.osmer.network.state;

import it.giacomos.android.osmer.network.DownloadManagerUpdateListener;
import it.giacomos.android.osmer.network.DownloadStatus;
import it.giacomos.android.osmer.observations.MapMode;

public abstract class State
{
	protected final DownloadStatus dDownloadStatus;
	
	public State(DownloadManagerUpdateListener l, DownloadStatus downloadStatus)
	{
		m_downloadManagerUpdateListener = l;
		dDownloadStatus = downloadStatus;
	}	

	public DownloadStatus getDownloadStatus()
	{
		return dDownloadStatus;
	}
	
	public void getSituation()
	{
		
	}

	public void getTodayForecast()
	{
		
	}

	public void getTomorrowForecast()
	{
		
	}

	public void getTwoDaysForecast()
	{
		
	}

	public void getThreeDaysForecast()
	{
		
	}

	public void getFourDaysForecast()
	{
		
	}

	public void getObservationsTable(MapMode mapMode) 
	{
		
	}
	
	public abstract StateName name();

	protected DownloadManagerUpdateListener m_downloadManagerUpdateListener;

	public void getRadarImage(String source) {
		
		
	}
	
	public void getWebcamList() 
	{
		
	}

	public void getReport(String url) 
	{
		
	}

}
