package it.giacomos.android.osmer.network.state;

import it.giacomos.android.osmer.network.DownloadManagerUpdateListener;
import it.giacomos.android.osmer.observations.ObservationTime;
import it.giacomos.android.osmer.observations.ObservationType;

public abstract class State{
	public State(DownloadManagerUpdateListener l)
	{
		m_stateUpdateListener = l;
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

	public void getObservationsTable(ObservationTime oTime) 
	{
		// TODO Auto-generated method stub
		
	}
	
	public abstract StateName name();

	protected DownloadManagerUpdateListener m_stateUpdateListener;

	public void getRadarImage() {
		
		
	}
	
	public void getWebcamList() 
	{
		
	}

}
