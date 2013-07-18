package it.giacomos.android.osmer.observations;

import it.giacomos.android.osmer.ViewType;

import java.util.HashMap;

public interface ObservationsCacheUpdateListener {
	
	public void onObservationsCacheUpdate(HashMap<String, ObservationData> map, ViewType t);

}
