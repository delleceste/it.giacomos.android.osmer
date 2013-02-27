package it.giacomos.android.osmer;

import android.widget.Toast;
import it.giacomos.android.osmer.observations.ObservationType;
import it.giacomos.android.osmer.widgets.OViewFlipper;
import it.giacomos.android.osmer.widgets.mapview.MapViewMode;
import it.giacomos.android.osmer.widgets.mapview.OMapView;

public class CurrentViewUpdater {
	public CurrentViewUpdater(OsmerActivity a)
	{
		int displayedChild = ((OViewFlipper) a.findViewById(R.id.viewFlipper1)).getDisplayedChild();
		if(displayedChild == FlipperChildren.MAP)
		{
			OMapView mapView = (OMapView) a.findViewById(R.id.mapview); 
			MapViewMode mapMode = mapView.getMode();
			/* update radar if mapview is showing the radar */
			if(mapMode.currentType == ObservationType.RADAR)
			{
				a.radar();
				Toast.makeText(a, a.getString(R.string.radarUpdateToast), Toast.LENGTH_SHORT).show();
			}
			if(mapMode.currentType == ObservationType.WEBCAM)
			{
				a.webcams();
				Toast.makeText(a, a.getString(R.string.webcamUpdateToast), Toast.LENGTH_SHORT).show();
			}
		}
	}

}
