package it.giacomos.android.osmer.pro;

import android.util.Log;
import android.widget.Toast;
import android.widget.ViewFlipper;
import it.giacomos.android.osmer.R;
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
			Log.e("MapViewUpdater.update", "updating radar or webcam: " + displayedChild);
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
				Log.e("MapViewUpdater", "updating report from MapViewUpdater");
				a.updateReport(false); /* false: do not force update if recently updated */
			}
		}
	}

}
