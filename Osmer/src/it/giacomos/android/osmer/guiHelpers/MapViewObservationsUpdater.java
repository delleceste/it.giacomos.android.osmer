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
		TableToMap tableToMap = new TableToMap();
		mapView.updateObservations(tableToMap.convert(text, t));
		tableToMap = null;
	}

}
