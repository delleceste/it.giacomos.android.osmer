package it.giacomos.android.osmer;

import android.util.Log;
import android.widget.Toast;
import android.widget.ViewFlipper;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.observations.MapMode;
import it.giacomos.android.osmer.observations.ObservationType;
import it.giacomos.android.osmer.pager.FragmentType;
import it.giacomos.android.osmer.widgets.map.MapViewMode;
import it.giacomos.android.osmer.widgets.map.OMapFragment;

public class MapViewUpdater {
	public void update(OsmerActivity a)
	{
		int displayedChild = a.getDisplayedFragment();
		if(displayedChild == FragmentType.MAP)
		{		
			OMapFragment mapView = (OMapFragment) a.getSupportFragmentManager().findFragmentById(R.id.mapview); 
			MapViewMode mapMode = mapView.getMode();
			/* update radar if mapview is showing the radar */
			if(mapMode.currentMode == MapMode.RADAR)
			{
				a.radar();
			}
			else if(mapMode.currentMode == MapMode.WEBCAM)
			{
				a.updateWbcamList();
			}
			else if(mapMode.currentMode == MapMode.REPORT)
			{
				a.updateReport(true);
			}
		}
	}

}
