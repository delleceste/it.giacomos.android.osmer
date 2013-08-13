package it.giacomos.android.osmer;

import android.util.Log;
import android.widget.Toast;
import android.widget.ViewFlipper;
import it.giacomos.android.osmer.observations.MapMode;
import it.giacomos.android.osmer.observations.ObservationType;
import it.giacomos.android.osmer.pager.FlipperChildren;
import it.giacomos.android.osmer.widgets.map.MapViewMode;
import it.giacomos.android.osmer.widgets.map.OMapFragment;

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
				Toast.makeText(a.getApplicationContext(), R.string.radarUpdateToast, Toast.LENGTH_SHORT).show();
			}
			if(mapMode.currentMode == MapMode.WEBCAM)
			{
				a.updateWbcamList();
				//Toast.makeText(a.getApplicationContext(), a.getString(R.string.webcamUpdateToast), Toast.LENGTH_SHORT).show();
			}
		}
	}

}
