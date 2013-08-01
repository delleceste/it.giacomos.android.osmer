package it.giacomos.android.meteofvg2.widgets;

import it.giacomos.android.meteofvg2.observations.ObservationsCache;

public interface LatestObservationCacheChangeListener {
	public void onCacheUpdate(ObservationsCache cache);
}
