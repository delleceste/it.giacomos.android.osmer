package it.giacomos.android.meteofvg2.guiHelpers;

import android.util.Log;
import it.giacomos.android.meteofvg2.OsmerActivity;
import it.giacomos.android.meteofvg2.R;
import it.giacomos.android.meteofvg2.ViewType;
import it.giacomos.android.meteofvg2.observations.TableToMap;
import it.giacomos.android.meteofvg2.widgets.map.OMapFragment;

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
