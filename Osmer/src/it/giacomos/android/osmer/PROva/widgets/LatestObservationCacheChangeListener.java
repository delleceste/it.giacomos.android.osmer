package it.giacomos.android.osmer.PROva.widgets;

import it.giacomos.android.osmer.PROva.observations.ObservationsCache;

public interface LatestObservationCacheChangeListener {
	public void onCacheUpdate(ObservationsCache cache);
}
