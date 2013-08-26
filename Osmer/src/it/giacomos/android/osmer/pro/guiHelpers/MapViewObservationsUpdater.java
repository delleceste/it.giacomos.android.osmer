package it.giacomos.android.osmer.pro.guiHelpers;

import android.util.Log;
import it.giacomos.android.osmer.pro.OsmerActivity;
import it.giacomos.android.osmer.pro.R;
import it.giacomos.android.osmer.pro.ViewType;
import it.giacomos.android.osmer.pro.observations.TableToMap;
import it.giacomos.android.osmer.pro.widgets.map.OMapFragment;

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
