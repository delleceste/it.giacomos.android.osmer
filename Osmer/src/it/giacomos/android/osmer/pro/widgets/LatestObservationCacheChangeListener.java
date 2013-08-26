package it.giacomos.android.osmer.pro.widgets;

import it.giacomos.android.osmer.pro.observations.ObservationsCache;

public interface LatestObservationCacheChangeListener {
	public void onCacheUpdate(ObservationsCache cache);
}
