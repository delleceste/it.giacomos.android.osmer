package it.giacomos.android.meteofvg2.observations;

import it.giacomos.android.meteofvg2.ViewType;

import java.util.HashMap;

public interface ObservationsCacheUpdateListener {
	
	public void onObservationsCacheUpdate(HashMap<String, ObservationData> map, ViewType t);

}
