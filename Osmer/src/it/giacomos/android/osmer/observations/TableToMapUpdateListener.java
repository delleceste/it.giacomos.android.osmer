package it.giacomos.android.osmer.observations;

import it.giacomos.android.osmer.StringType;

import java.util.HashMap;

public interface TableToMapUpdateListener {

	void onTableUpdate(HashMap<String, ObservationData> map,
			StringType mStringType);

}
