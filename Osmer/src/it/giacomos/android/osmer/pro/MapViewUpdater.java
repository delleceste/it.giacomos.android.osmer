package it.giacomos.android.osmer.pro;

import android.util.Log;
import android.widget.Toast;
import android.widget.ViewFlipper;
import it.giacomos.android.osmer.pro.R;
import it.giacomos.android.osmer.pro.observations.MapMode;
import it.giacomos.android.osmer.pro.observations.ObservationType;
import it.giacomos.android.osmer.pro.pager.FlipperChildren;
import it.giacomos.android.osmer.pro.widgets.map.MapViewMode;
import it.giacomos.android.osmer.pro.widgets.map.OMapFragment;

public class MapViewUpdater {
	public void update(OsmerActivity a)
	{
		int displayedChild = ((ViewFlipper)a.findViewById(R.id.viewFlipper)).getDisplayedChild();
		if(displayedChild == FlipperChildren.PAGE_MAP)
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
