package it.giacomos.android.osmer.widgets;

import it.giacomos.android.osmer.observations.ObservationsCache;

public interface LatestObservationCacheChangeListener {
	public void onCacheUpdate(ObservationsCache cache);
}
