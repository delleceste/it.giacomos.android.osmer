package it.giacomos.android.osmer.guiHelpers;
import android.content.res.Resources;

import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.downloadManager.DownloadStatus;
import it.giacomos.android.osmer.widgets.CurrentScreen;
import it.giacomos.android.osmer.widgets.OViewFlipper;

import java.lang.String;

public class TitleBuilder 
{
	public String makeTitle(OsmerActivity a)
	{
		OViewFlipper flipper = (OViewFlipper) a.findViewById(R.id.viewFlipper1);
		DownloadStatus ds = DownloadStatus.Instance();
		boolean networkAvailable = ds.isOnline;
		int scr = flipper.getDisplayedChild();
		Resources res = a.getResources();
		String t = "";
		
		switch(scr)
		{
		case CurrentScreen.HOME_SCREEN:
			t += res.getString(R.string.home_title);
			break;
		case CurrentScreen.TODAY_SCREEN:
			t += res.getString(R.string.today_title);
			break;
		case CurrentScreen.TOMORROW_SCREEN:
			t += res.getString(R.string.tomorrow_title);
			break;
		case CurrentScreen.TWODAYS_SCREEN:
			t += res.getString(R.string.two_days_title);
			break;
			
		case CurrentScreen.MAPVIEW_SCREEN:
			ToggleButtonGroupHelper th =  a.getToggleButtonGroupHelper();
			if(th.isOn(R.id.buttonDailyObs))
				t += res.getString(R.string.observations_title_daily);
			else if(th.isOn(R.id.buttonLastObs))
				t += res.getString(R.string.observations_title_latest);
			else if(th.isOn(R.id.buttonRadar))
				t += res.getString(R.string.radar_title);
			else if(th.isOn(R.id.buttonDailySky))
				t += res.getString(R.string.observations_title_daily_sky);
			else if(th.isOn(R.id.buttonTMin))
				t += res.getString(R.string.observations_title_daily_tmin);
			else if(th.isOn(R.id.buttonTMean))
				t += res.getString(R.string.observations_title_daily_tmean);
			else if(th.isOn(R.id.buttonTMax))
				t += res.getString(R.string.observations_title_daily_tmax);
			else if(th.isOn(R.id.buttonHumMean))
				t += res.getString(R.string.observations_title_daily_hmean);
			else if(th.isOn(R.id.buttonWMean))
				t += res.getString(R.string.observations_title_daily_wmean);
			else if(th.isOn(R.id.buttonWMax))
				t += res.getString(R.string.observations_title_daily_wmax);
			else if(th.isOn(R.id.buttonDailyRain))
				t += res.getString(R.string.observations_title_daily_rain);
			
			/* latest */
			else if(th.isOn(R.id.buttonLatestSky))
				t += res.getString(R.string.observations_title_latest_sky);
			else if(th.isOn(R.id.buttonTemp))
				t += res.getString(R.string.observations_title_latest_temp);
			else if(th.isOn(R.id.buttonHumidity))
				t += res.getString(R.string.observations_title_latest_humidity);
			else if(th.isOn(R.id.buttonPressure))
				t += res.getString(R.string.observations_title_latest_pressure);
			else if(th.isOn(R.id.buttonWind))
				t += res.getString(R.string.observations_title_latest_wind);
			else if(th.isOn(R.id.buttonLatestRain))
				t += res.getString(R.string.observations_title_latest_rain);
			else if(th.isOn(R.id.buttonSnow))
				t += res.getString(R.string.observations_title_latest_snow);
			else if(th.isOn(R.id.buttonSea))
				t += res.getString(R.string.observations_title_latest_sea);
			else if(th.isOn(R.id.buttonWebcam))
				t += res.getString(R.string.title_webcam);
			
			break;
			
		default:
			t += "untitled (yet)";
			break;
		}
		
		t += " - ";
		
		/* network status */
		if(networkAvailable)
			t += res.getString(R.string.online);
		else
			t += res.getString(R.string.offline);
		return t;
	}
}
