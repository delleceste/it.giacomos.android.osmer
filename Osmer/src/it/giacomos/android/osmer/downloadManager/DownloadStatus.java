package it.giacomos.android.osmer.downloadManager;

import java.util.Calendar;
import java.util.Date;
import it.giacomos.android.osmer.BitmapType;
import it.giacomos.android.osmer.ViewType;
import it.giacomos.android.osmer.webcams.WebcamDataCache;
import it.giacomos.android.osmer.widgets.CurrentScreen;

public class DownloadStatus {

	public static final int DOWNLOAD_OLD_TIMEOUT = 60000;
	public static final int DOWNLOAD_OBSERVATIONS_OLD_TIMEOUT = 60;
	
	
	public static DownloadStatus Instance()
	{
		if(m_instance == null)
			m_instance = new DownloadStatus();
		return m_instance;
	}
	
	public boolean homeDownloaded() { return (state & HOME_DOWNLOADED) != 0; }
	public boolean todayDownloaded() { return (state & TODAY_DOWNLOADED) != 0; }
	public boolean tomorrowDownloaded() { return (state & TOMORROW_DOWNLOADED) != 0; }
	public boolean twoDaysDownloaded() { return (state & TWODAYS_DOWNLOADED) != 0; }
	public boolean todayBmpDownloaded() { return (state & TODAY_IMAGE_DOWNLOADED) != 0; }
	public boolean tomorrowBmpDownloaded() { return (state & TOMORROW_IMAGE_DOWNLOADED) != 0; }
	public boolean twoDaysBmpDownloaded() { return (state & TWODAYS_IMAGE_DOWNLOADED) != 0; }
	public boolean downloadErrorCondition() { return (state & DOWNLOAD_ERROR_CONDITION) != 0; }

	/* suppose it is always necessary to refresh radar image.
	 * 
	 */
	public boolean radarImageDownloaded() { return false;	}
	
	
	public boolean webcamListDownloaded() 
	{ 
		WebcamDataCache webcamDataCache = WebcamDataCache.getInstance();
		return !webcamDataCache.dataIsOld();
	}
	
	public boolean lastCompleteDownloadIsOld()
	{
		return System.currentTimeMillis() - m_lastUpdateCompletedOn > DOWNLOAD_OLD_TIMEOUT;
	}
	
	public boolean observationsNeedUpdate()
	{
		Date now = Calendar.getInstance().getTime();
		if((now.getTime() - m_observationsSavedOn.getTime())/1000 > 60)
			return true;
		return false;
	}
	
	public void setObservationsSaved()
	{
		m_observationsSavedOn = Calendar.getInstance().getTime();
	}
	
	public long lastUpdateCompletedOn() { return m_lastUpdateCompletedOn; }
	
	public void setLastUpdateCompletedOn(long l) { m_lastUpdateCompletedOn = l; }
	
	public boolean downloadIncomplete()
	{
		return !homeDownloaded() || !todayDownloaded() || !tomorrowDownloaded() ||  !twoDaysDownloaded() 
				|| !todayBmpDownloaded() || !tomorrowBmpDownloaded() || !twoDaysBmpDownloaded();
	}
	
	public boolean downloadComplete() { return !downloadIncomplete(); }
	
	
	public boolean fullForecastDownloadRequested()
	{
		return (state &  FORECAST_DOWNLOAD_REQUESTED) != 0;
	}
	
	public void setFullForecastDownloadRequested(boolean requested)
	{
		if(requested)
			state = (state | FORECAST_DOWNLOAD_REQUESTED);
		else
			state = (state & ~FORECAST_DOWNLOAD_REQUESTED);
	}

	public void setWebcamListsDownloadRequested(boolean requested)
	{
		if(requested)
		{
			state = (state | WEBCAM_OSMER_DOWNLOAD_REQUESTED);
			state = (state | WEBCAM_OTHER_DOWNLOAD_REQUESTED);
			state = (state & ~WEBCAM_OSMER_DOWNLOADED);
			state = (state & ~WEBCAM_OTHER_DOWNLOADED);
		}
		else
		{
			state = (state & ~WEBCAM_OSMER_DOWNLOAD_REQUESTED);
			state = (state & ~WEBCAM_OTHER_DOWNLOAD_REQUESTED);
		}
	}
	
	public void setDownloadErrorCondition(boolean err)
	{
		if(err)
			state = (state | DOWNLOAD_ERROR_CONDITION);
		else
			state = (state & ~DOWNLOAD_ERROR_CONDITION);
	}
	
	public void updateState(ViewType st, boolean downloaded)
	{
		if(downloaded)
		{
			if(st == ViewType.TODAY)
				state = (state | TODAY_DOWNLOADED);
			else if(st == ViewType.HOME)
				state = (state | HOME_DOWNLOADED);
			else if(st == ViewType.TOMORROW)
				state = (state | TOMORROW_DOWNLOADED);
			else if(st == ViewType.TWODAYS)
				state = (state | TWODAYS_DOWNLOADED);
			else if(st == ViewType.WEBCAMLIST_OSMER)
				state = (state | WEBCAM_OSMER_DOWNLOADED);
			else if(st == ViewType.WEBCAMLIST_OTHER)
				state = (state | WEBCAM_OTHER_DOWNLOADED);			
		}
		else
		{
			if(st == ViewType.TODAY)
				state = (state & ~TODAY_DOWNLOADED);
			else if(st == ViewType.HOME)
				state = (state & ~HOME_DOWNLOADED);
			else if(st == ViewType.TOMORROW)
				state = (state & ~TOMORROW_DOWNLOADED);
			else if(st == ViewType.TWODAYS)
				state = (state & ~TWODAYS_DOWNLOADED);
			else if(st == ViewType.WEBCAMLIST_OSMER)
				state = (state & ~WEBCAM_OSMER_DOWNLOADED);
			else if(st == ViewType.WEBCAMLIST_OTHER)
				state = (state & ~WEBCAM_OTHER_DOWNLOADED);
		}
		
		if(!downloaded)
			setDownloadErrorCondition(true);
		else if(downloadComplete())
		{
			setDownloadErrorCondition(false);
			m_lastUpdateCompletedOn = System.currentTimeMillis();
		}
	}
	
	public void updateState(BitmapType bt, boolean downloaded)
	{
		if(downloaded)
		{
			if(bt == BitmapType.TODAY)
				state = (state | TODAY_IMAGE_DOWNLOADED);
			else if(bt == BitmapType.TOMORROW)
				state = (state | TOMORROW_IMAGE_DOWNLOADED);
			else if(bt == BitmapType.TWODAYS)
				state = (state | TWODAYS_IMAGE_DOWNLOADED);
			else if(bt == BitmapType.RADAR)
				state = (state | RADAR_IMAGE_DOWNLOADED);
		}
		else
		{
			if(bt == BitmapType.TODAY)
				state = (state & ~TODAY_IMAGE_DOWNLOADED);
			else if(bt == BitmapType.TOMORROW)
				state = (state & ~TOMORROW_IMAGE_DOWNLOADED);
			else if(bt == BitmapType.TWODAYS)
				state = (state & ~TWODAYS_IMAGE_DOWNLOADED);
			else if(bt == BitmapType.RADAR)
				state = (state & ~RADAR_IMAGE_DOWNLOADED);
		}
		if(!downloaded)
			setDownloadErrorCondition(true);
		else if(downloadComplete())
		{
			setDownloadErrorCondition(false);
			m_lastUpdateCompletedOn = System.currentTimeMillis();
		}
	}

	public long state;
	public int currentScreen = CurrentScreen.HOME_SCREEN;
	public boolean isOnline;
	
	private long m_lastUpdateCompletedOn;
	
	public  static final long INIT = 0x0;

	/* text related */
	public static final long HOME_DOWNLOADED = 0x01;
	public static final long TODAY_DOWNLOADED = 0x02;
	public static final long TOMORROW_DOWNLOADED = 0x04;
	public static final long TWODAYS_DOWNLOADED = 0x08;
	/* bitmap related */
	public static final long TODAY_IMAGE_DOWNLOADED = 0x10;
	public static final long TOMORROW_IMAGE_DOWNLOADED = 0x20;
	public static final long TWODAYS_IMAGE_DOWNLOADED = 0x40;
	public static final long RADAR_IMAGE_DOWNLOADED = 0x80;
	
	public static final long FORECAST_DOWNLOAD_REQUESTED = 0x100;
	
	/* webcam related */
	public static final long WEBCAM_OSMER_DOWNLOAD_REQUESTED = 0x200;
	public static final long WEBCAM_OTHER_DOWNLOAD_REQUESTED = 0x400;
	public static final long WEBCAM_OSMER_DOWNLOADED = 0x800;
	public static final long WEBCAM_OTHER_DOWNLOADED = 0x1000;
	
	public static final long DOWNLOAD_ERROR_CONDITION = 0x10000000;
	
	
	private DownloadStatus()
	{
		init();
	}
	
	private static DownloadStatus m_instance = null;

	private Date m_observationsSavedOn;


	public void init() {
		state = INIT;
		m_lastUpdateCompletedOn = 0;
		// TODO Auto-generated method stub
		
	}
}
