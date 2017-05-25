package it.giacomos.android.osmer.observations;

import java.util.HashMap;

import it.giacomos.android.osmer.network.state.ViewType;

public interface TableToMapUpdateListener {

	void onTableUpdate(HashMap<String, ObservationData> map,
			ViewType mStringType);

}
