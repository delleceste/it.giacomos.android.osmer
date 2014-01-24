package it.giacomos.android.osmer.PROva.observations;

import it.giacomos.android.osmer.PROva.network.state.ViewType;

import java.util.HashMap;

public interface TableToMapUpdateListener {

	void onTableUpdate(HashMap<String, ObservationData> map,
			ViewType mStringType);

}
