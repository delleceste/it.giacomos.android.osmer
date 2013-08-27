package it.giacomos.android.osmer.pro.network.Data;

import it.giacomos.android.osmer.pro.network.state.BitmapType;
import it.giacomos.android.osmer.pro.network.state.ViewType;

public interface DataPoolErrorListener {
	
	public abstract void onBitmapUpdateError(BitmapType t, String error);
	
	public abstract void onTextUpdateError(ViewType t, String error);

}
