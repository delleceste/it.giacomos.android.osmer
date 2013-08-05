package it.giacomos.android.osmer.guiHelpers;
import android.content.res.Resources;

import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.downloadManager.DownloadStatus;
import it.giacomos.android.osmer.observations.ObservationTime;
import it.giacomos.android.osmer.observations.ObservationType;
import it.giacomos.android.osmer.widgets.CurrentScreen;
import it.giacomos.android.osmer.widgets.map.MapViewMode;
import it.giacomos.android.osmer.widgets.map.OMapFragment;

import java.lang.String;

public class TitleBuilder 
{
	public String makeTitle(OsmerActivity a)
	{
		OMapFragment map = (OMapFragment) a.getSupportFragmentManager().findFragmentById(R.id.mapview);
		MapViewMode mapMode = null;
		DownloadStatus ds = DownloadStatus.Instance();
		boolean networkAvailable = ds.isOnline;
		int scr = a.getViewPager().getCurrentItem();
		Resources res = a.getResources();
		String t = "";
		
		if (scr == CurrentScreen.HOME_SCREEN || scr == CurrentScreen.TODAY_SCREEN
				|| scr == CurrentScreen.TOMORROW_SCREEN || scr == CurrentScreen.TWODAYS_SCREEN) {
			t += res.getString(R.string.forecast_title);
		} else if (scr == CurrentScreen.MAPVIEW_SCREEN) {
			mapMode = map.getMode();
			if(mapMode.currentType == ObservationType.RADAR)
				t += res.getString(R.string.radar_title);
			else if(mapMode.currentType == ObservationType.WEBCAM)
				t += res.getString(R.string.title_webcam);
			else if(mapMode.currentMode == ObservationTime.DAILY)
				t += res.getString(R.string.observations_title_daily);
			else if(mapMode.currentMode == ObservationTime.LATEST)
				t += res.getString(R.string.observations_title_latest);
		} else {
			t += "untitled (yet)";
		}
		
		t += " - " + res.getString(R.string.app_name);
		
		/* network status */
		if(!networkAvailable)
			t += " - " + res.getString(R.string.offline);
		return t;
	}
}
