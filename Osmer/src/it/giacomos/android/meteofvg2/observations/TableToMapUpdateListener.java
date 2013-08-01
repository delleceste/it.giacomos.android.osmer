package it.giacomos.android.meteofvg2.observations;

import it.giacomos.android.meteofvg2.ViewType;

import java.util.HashMap;

public interface TableToMapUpdateListener {

	void onTableUpdate(HashMap<String, ObservationData> map,
			ViewType mStringType);

}
