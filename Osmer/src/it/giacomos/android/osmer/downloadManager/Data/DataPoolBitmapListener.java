package it.giacomos.android.osmer.downloadManager.Data;

import it.giacomos.android.osmer.BitmapType;
import android.graphics.Bitmap;

public interface DataPoolBitmapListener {
	
	/* fromCache is false if the update comes from a network task, true if it
	 * comes from the cached data.
	 */
	public abstract void onBitmapChanged(Bitmap bmp, BitmapType t, boolean fromCache);
	
	public abstract void onBitmapError(String error, BitmapType t);

}
