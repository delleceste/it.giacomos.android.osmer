package it.giacomos.android.osmer.pro.network.Data;

import it.giacomos.android.osmer.pro.network.state.ViewType;

public interface DataPoolTextListener 
{
	/* fromCache is false if the update comes from a network task, true if it
	 * comes from the cached data.
	 */
	public abstract void onTextChanged(String txt, ViewType t, boolean fromCache);
	
	public abstract void onTextError(String error, ViewType t);
}
