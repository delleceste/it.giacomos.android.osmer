package it.giacomos.android.osmer.network.state;

import it.giacomos.android.osmer.network.DownloadManagerUpdateListener;
import it.giacomos.android.osmer.network.DownloadStatus;
import it.giacomos.android.osmer.observations.MapMode;

public abstract class State
{
	protected final DownloadStatus dDownloadStatus;
	
	public State(DownloadManagerUpdateListener l, DownloadStatus downloadStatus)
	{
		m_stateUpdateListener = l;
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

	public void getObservationsTable(MapMode mapMode) 
	{
		
	}
	
	public abstract StateName name();

	protected DownloadManagerUpdateListener m_stateUpdateListener;

	public void getRadarImage() {
		
		
	}
	
	public void getWebcamList() 
	{
		
	}

}