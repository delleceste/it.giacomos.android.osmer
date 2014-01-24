package it.giacomos.android.osmer.PROva.widgets;

import android.content.Context;
import android.location.LocationListener;

public interface ImageViewWithLocationInterface extends LocationListener {
	public void requestLocationUpdates(Context ctx);
	public void removeLocationUpdates(Context ctx);
}
