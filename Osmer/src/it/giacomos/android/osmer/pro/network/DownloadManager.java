package it.giacomos.android.osmer.pro.network;

import java.io.InputStream;

import it.giacomos.android.osmer.pro.OsmerActivity;
import it.giacomos.android.osmer.pro.network.Data.DownloadListener;
import it.giacomos.android.osmer.pro.network.state.BitmapType;
import it.giacomos.android.osmer.pro.network.state.Offline;
import it.giacomos.android.osmer.pro.network.state.Online;
import it.giacomos.android.osmer.pro.network.state.State;
import it.giacomos.android.osmer.pro.network.state.StateName;
import it.giacomos.android.osmer.pro.network.state.ViewType;
import it.giacomos.android.osmer.pro.observations.MapMode;
import it.giacomos.android.osmer.pro.observations.ObservationType;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.util.Log;

/** the State context 
 * 
 * @author giacomo
 *
 */
public class DownloadManager  implements NetworkStatusMonitorListener, 
	DownloadManagerUpdateListener
{
	public DownloadManager(DownloadStateListener l, DownloadStatus downloadStatus)
	{
		m_downloadUpdateListener = l;
		setState(new Offline(this, downloadStatus));
	}
	
	public void setDownloadListener(DownloadListener dl)
	{
		mDownloadListener = dl;
	}
	
	public void onPause(OsmerActivity activity)
	{
		activity.unregisterReceiver(m_networkStatusMonitor);
	}
	
	public void onResume(OsmerActivity activity)
	{
		m_networkStatusMonitor = new NetworkStatusMonitor(this);
		activity.registerReceiver(m_networkStatusMonitor, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
	}
	
	public void stopPendingTasks()
	{
		if(m_state.name() == StateName.Online)
		{
			Online onlineState = (Online) m_state;
			onlineState.cancelRunningTasks();
		}
	}

	@Override
	public void onNetworkBecomesAvailable() 
	{
		/* get the current download status and pass it to the new state
		 * The download status is a final field created in the activity constructor.
		 */
		setState(new Online(this, m_state.getDownloadStatus()));
		m_downloadUpdateListener.networkStatusChanged(true);
	}

	@Override
	public void onNetworkBecomesUnavailable() {
		setState(new Offline(this, m_state.getDownloadStatus()));
		m_downloadUpdateListener.networkStatusChanged(false);
	}
	
	public void getSituation()
	{
		m_state.getSituation();
	}

	public void getTodayForecast()
	{
		m_state.getTodayForecast();
	}

	public void getTomorrowForecast()
	{
		m_state.getTomorrowForecast();
	}

	public void getTwoDaysForecast()
	{
		m_state.getTwoDaysForecast();
	}

	public void getRadarImage()
	{
		m_state.getRadarImage();
	}
	
	public void getWebcamList() 
	{
		m_state.getWebcamList();
	}
	
	public void getObservationsTable(MapMode mapMode) 
	{
		m_state.getObservationsTable(mapMode);
	}
	
	public void setState(State s)
	{
		m_state = s;
	}
	
	public State state() 
	{
		return m_state;
	}

	@Override
	public void onBitmapBytesUpdate(byte [] bytes, BitmapType bt) 
	{
		mDownloadListener.onBitmapBytesUpdate(bytes, bt);
	}

	@Override
	public void onTextBytesUpdate(byte[] bytes, ViewType vt) 
	{
		mDownloadListener.onTextBytesUpdate(bytes, vt);
	}
	
	@Override
	public void onBitmapUpdate(Bitmap bmp, BitmapType t, String errorMessage) {

		if(bmp != null)
			mDownloadListener.onBitmapUpdate(bmp, t);
		else
			mDownloadListener.onBitmapUpdateError(t, errorMessage);
	}

	@Override
	public void onTextUpdate(String txt, ViewType t, String errorMessage) 
	{
		if(txt != null)
			mDownloadListener.onTextUpdate(txt, t);
		else
			mDownloadListener.onTextUpdateError(t, errorMessage);
	}

	@Override
	public void onProgressUpdate(int step, int total) {
		m_downloadUpdateListener.onDownloadProgressUpdate(step, total);
	}

	@Override
	public void onDownloadStart(DownloadReason reason) {
		m_downloadUpdateListener.onDownloadStart(reason);
	}
		
	public void onStateChanged(long oldState, long state)
	{
		m_downloadUpdateListener.onStateChanged(oldState, state);
	}
	
	private State m_state;
	private DownloadStateListener m_downloadUpdateListener;
	private DownloadListener mDownloadListener;
	private NetworkStatusMonitor m_networkStatusMonitor;
	
	
}
