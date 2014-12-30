package it.giacomos.android.osmer.interfaceHelpers;
import android.content.res.Resources;
import it.giacomos.android.osmer.network.DownloadStatus;
import it.giacomos.android.osmer.observations.MapMode;
import it.giacomos.android.osmer.observations.ObservationType;
import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.widgets.ForecastTabs;
import it.giacomos.android.osmer.widgets.map.MapViewMode;
import it.giacomos.android.osmer.widgets.map.OMapFragment;

import java.lang.String;

public class TitleBuilder 
{
	public String makeTitle(OsmerActivity a)
	{
		OMapFragment map = (OMapFragment) a.getSupportFragmentManager().findFragmentById(R.id.mapview);
		MapViewMode mapMode = null;
		DownloadStatus ds = a.getDownloadStatus();
		boolean networkAvailable = ds.isOnline;
		int scr = a.getViewPager().getCurrentItem();
		Resources res = a.getResources();
		String t = "";
		
		if (a.getDrawerListView().getCheckedItemPosition() == 0
				&& (scr == ForecastTabs.HOME_TAB || 
				scr == ForecastTabs.TODAY_TAB|| 
				scr == ForecastTabs.TOMORROW_TAB || 
				scr == ForecastTabs.TWODAYS_TAB || 
				scr == ForecastTabs.THREEDAYS_TAB || 
				scr == ForecastTabs.FOURDAYS_TAB)
				) 
		{
			t += res.getString(R.string.forecast_title);
			
		} 
		else if(map != null)
		{
			mapMode = map.getMode();
			if(mapMode.currentMode == MapMode.RADAR)
				t += res.getString(R.string.radar_title);
			else if(mapMode.currentMode == MapMode.WEBCAM)
				t += res.getString(R.string.title_webcam);
			else if(mapMode.currentMode == MapMode.DAILY_OBSERVATIONS)
				t += res.getString(R.string.observations_title_daily);
			else if(mapMode.currentMode == MapMode.LATEST_OBSERVATIONS)
				t += res.getString(R.string.observations_title_latest);
			else if(mapMode.currentMode == MapMode.REPORT)
				t += res.getString(R.string.reportDialogTitle);
		}
		
	//	t += " - " + res.getString(R.string.app_name);
		
		/* network status */
		if(!networkAvailable)
			t += " - " + res.getString(R.string.offline);
		
		return t;
	}
}
