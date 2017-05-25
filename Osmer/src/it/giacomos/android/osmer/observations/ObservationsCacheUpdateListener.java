package it.giacomos.android.osmer.observations;

import java.util.HashMap;

import it.giacomos.android.osmer.network.state.ViewType;

public interface ObservationsCacheUpdateListener {
	
	public void onObservationsCacheUpdate(HashMap<String, ObservationData> map, ViewType t);

}
