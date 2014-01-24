package it.giacomos.android.osmer.PROva.observations;

import it.giacomos.android.osmer.PROva.network.state.ViewType;

import java.util.HashMap;

public interface ObservationsCacheUpdateListener {
	
	public void onObservationsCacheUpdate(HashMap<String, ObservationData> map, ViewType t);

}
