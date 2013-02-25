package it.giacomos.android.osmer.downloadManager;

import java.util.Calendar;
import java.util.Date;

import android.util.Log;
import it.giacomos.android.osmer.BitmapType;
import it.giacomos.android.osmer.StringType;
import it.giacomos.android.osmer.widgets.CurrentScreen;

public class DownloadStatus {

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
	
	/* suppose that the webcam list is to be downloaded every time, since, for now
	 * the information about date and timed is stored in a web page and an html
	 * page.
	 */
	public boolean webcamListDownloaded() { return false; }
	
	public boolean lastCompleteDownloadIsOld()
	{
		return System.currentTimeMillis() - m_lastUpdateCompletedOn > 30000;
	}
	
	public boolean observationsNeedUpdate()
	{
		Date now = Calendar.getInstance().getTime();
		if(now.getSeconds() - m_observationsSavedOn.getSeconds() > 60)
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

	public void setDownloadErrorCondition(boolean err)
	{
		if(err)
			state = (state | DOWNLOAD_ERROR_CONDITION);
		else
			state = (state & ~DOWNLOAD_ERROR_CONDITION);
	}
	
	public void updateState(StringType st, boolean downloaded)
	{
		if(downloaded)
		{
			if(st == StringType.TODAY)
				state = (state | TODAY_DOWNLOADED);
			else if(st == StringType.HOME)
				state = (state | HOME_DOWNLOADED);
			else if(st == StringType.TOMORROW)
				state = (state | TOMORROW_DOWNLOADED);
			else if(st == StringType.TWODAYS)
				state = (state | TWODAYS_DOWNLOADED);
			m_lastUpdateCompletedOn = System.currentTimeMillis();
		}
		else
		{
			if(st == StringType.TODAY)
				state = (state & ~TODAY_DOWNLOADED);
			else if(st == StringType.HOME)
				state = (state & ~HOME_DOWNLOADED);
			else if(st == StringType.TOMORROW)
				state = (state & ~TOMORROW_DOWNLOADED);
			else if(st == StringType.TWODAYS)
				state = (state & ~TWODAYS_DOWNLOADED);
		}
		
		if(!downloaded)
			setDownloadErrorCondition(true);
		else if(downloadComplete())
			setDownloadErrorCondition(false);
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
			m_lastUpdateCompletedOn = System.currentTimeMillis();
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
			setDownloadErrorCondition(false);
	}

	public long state;
	public int currentScreen = CurrentScreen.HOME_SCREEN;
	public boolean isOnline;
	
	private long m_lastUpdateCompletedOn;
	
	public  static final long INIT = 0x0;

	/* text related */
	public static final long  HOME_DOWNLOADED = 0x01;
	public static final long TODAY_DOWNLOADED = 0x02;
	public static final long TOMORROW_DOWNLOADED = 0x04;
	public static final long TWODAYS_DOWNLOADED = 0x08;
	/* bitmap related */
	public static final long TODAY_IMAGE_DOWNLOADED = 0x10;
	public static final long TOMORROW_IMAGE_DOWNLOADED = 0x20;
	public static final long TWODAYS_IMAGE_DOWNLOADED = 0x40;
	public static final long RADAR_IMAGE_DOWNLOADED = 0x80;
	
	
	public static final long FORECAST_DOWNLOAD_REQUESTED = 0x100;
	
	
	public static final long DOWNLOAD_ERROR_CONDITION = 0x200;
	
	
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
