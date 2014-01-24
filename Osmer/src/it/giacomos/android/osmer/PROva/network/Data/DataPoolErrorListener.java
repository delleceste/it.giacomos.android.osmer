package it.giacomos.android.osmer.PROva.network.Data;

import it.giacomos.android.osmer.PROva.network.state.BitmapType;
import it.giacomos.android.osmer.PROva.network.state.ViewType;
import it.giacomos.android.osmer.PROva.widgets.map.report.network.PostType;

public interface DataPoolErrorListener {
	
	public abstract void onBitmapUpdateError(BitmapType t, String error);
	
	public abstract void onTextUpdateError(ViewType t, String error);
}
