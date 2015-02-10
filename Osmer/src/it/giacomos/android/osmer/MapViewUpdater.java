package it.giacomos.android.osmer;

import it.giacomos.android.osmer.observations.MapMode;
import it.giacomos.android.osmer.pager.FragmentType;
import it.giacomos.android.osmer.widgets.map.MapViewMode;
import it.giacomos.android.osmer.widgets.map.OMapFragment;

/** 
 * Updates the map contents if the currently displayed fragment is OMapFragment.
 * If the currently displayed fragment is not the map, nothing is done. 
 * @author giacomo
 *
 */
public class MapViewUpdater 
{
	public void update(OsmerActivity a)
	{
		int displayedChild = a.getDisplayedFragment();
		if(displayedChild == FragmentType.MAP)
		{		
			OMapFragment mapView = (OMapFragment) a.getMapFragment();
			MapViewMode mapMode = mapView.getMode();
			/* update radar if mapview is showing the radar */
			if(mapMode.currentMode == MapMode.RADAR)
			{
				a.radar();
			}
			else if(mapMode.currentMode == MapMode.REPORT)
			{
				a.updateReport(true);
			}

		}
	}

}
