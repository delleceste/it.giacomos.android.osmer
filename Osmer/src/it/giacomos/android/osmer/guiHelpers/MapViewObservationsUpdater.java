package it.giacomos.android.osmer.guiHelpers;

import android.util.Log;
import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.StringType;
import it.giacomos.android.osmer.observations.TableToMap;
import it.giacomos.android.osmer.widgets.mapview.OMapView;

public class MapViewObservationsUpdater 
{
	public MapViewObservationsUpdater(OsmerActivity a, String text, StringType t)
	{
		OMapView mapView = (OMapView) a.findViewById(R.id.mapview);
		mapView.updateObservations(TableToMap.convert(text, t));
	}

}
