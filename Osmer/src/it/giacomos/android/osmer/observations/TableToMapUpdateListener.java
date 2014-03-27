package it.giacomos.android.osmer.observations;

import it.giacomos.android.osmer.network.state.ViewType;

import java.util.HashMap;

public interface TableToMapUpdateListener {

	void onTableUpdate(HashMap<String, ObservationData> map,
			ViewType mStringType);

}
