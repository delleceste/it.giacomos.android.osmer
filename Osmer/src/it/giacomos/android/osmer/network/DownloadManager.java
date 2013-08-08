package it.giacomos.android.osmer.network;

import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.network.Data.DownloadListener;
import it.giacomos.android.osmer.network.state.BitmapType;
import it.giacomos.android.osmer.network.state.Offline;
import it.giacomos.android.osmer.network.state.Online;
import it.giacomos.android.osmer.network.state.State;
import it.giacomos.android.osmer.network.state.StateName;
import it.giacomos.android.osmer.network.state.ViewType;
import it.giacomos.android.osmer.observations.ObservationTime;
import it.giacomos.android.osmer.observations.ObservationType;
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
	public DownloadManager(DownloadStateListener l)
	{
		m_downloadUpdateListener = l;
//		ConnectivityManager cm = (ConnectivityManager) activity.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//		if(cm.getActiveNetworkInfo().isAvailable())
//			setState(new Online());
//		else
		setState(new Offline(this));
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
	public void onNetworkBecomesAvailable() {
		setState(new Online(this));
		m_downloadUpdateListener.networkStatusChanged(true);
	}

	@Override
	public void onNetworkBecomesUnavailable() {
		setState(new Offline(this));
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
	
	public void getObservationsTable(ObservationTime oTime) {
		// TODO Auto-generated method stub
		m_state.getObservationsTable(oTime);
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
	public void onBitmapUpdate(Bitmap bmp, BitmapType t, String errorMessage) {
		if(bmp != null)
			mDownloadListener.onBitmapUpdate(bmp, t);
		else
			mDownloadListener.onBitmapUpdateError(t, errorMessage);
	}

	@Override
	public void onTextUpdate(String txt, ViewType t, String errorMessage) {
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
