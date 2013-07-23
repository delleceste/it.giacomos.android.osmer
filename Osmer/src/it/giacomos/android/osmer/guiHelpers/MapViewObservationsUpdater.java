package it.giacomos.android.osmer.guiHelpers;

import android.util.Log;
import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.ViewType;
import it.giacomos.android.osmer.observations.TableToMap;
import it.giacomos.android.osmer.widgets.map.OMapFragment;

public class MapViewObservationsUpdater 
{
	public MapViewObservationsUpdater(OsmerActivity a, String text, ViewType t)
	{
		OMapFragment mapView = (OMapFragment) a.getFragmentManager().findFragmentById(R.id.mapview);
		TableToMap tableToMap = new TableToMap();
		mapView.updateObservations(tableToMap.convert(text, t));
		tableToMap = null;
	}

}
