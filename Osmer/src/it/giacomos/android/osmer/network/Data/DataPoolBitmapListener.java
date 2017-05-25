package it.giacomos.android.osmer.network.Data;

import android.graphics.Bitmap;

import it.giacomos.android.osmer.network.state.BitmapType;

public interface DataPoolBitmapListener {
	
	/* fromCache is false if the update comes from a network task, true if it
	 * comes from the cached data.
	 */
	public abstract void onBitmapChanged(Bitmap bmp, BitmapType t, boolean fromCache);
	
	public abstract void onBitmapError(String error, BitmapType t);

}
