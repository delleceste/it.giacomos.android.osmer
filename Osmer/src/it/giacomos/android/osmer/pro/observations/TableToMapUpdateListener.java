package it.giacomos.android.osmer.pro.observations;

import it.giacomos.android.osmer.pro.ViewType;

import java.util.HashMap;

public interface TableToMapUpdateListener {

	void onTableUpdate(HashMap<String, ObservationData> map,
			ViewType mStringType);

}
