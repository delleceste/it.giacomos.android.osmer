package it.giacomos.android.osmer.pro.observations;

import it.giacomos.android.osmer.pro.ViewType;

import java.util.HashMap;

public interface ObservationsCacheUpdateListener {
	
	public void onObservationsCacheUpdate(HashMap<String, ObservationData> map, ViewType t);

}
