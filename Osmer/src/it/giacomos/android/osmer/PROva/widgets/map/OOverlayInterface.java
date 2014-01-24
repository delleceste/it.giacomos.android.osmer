package it.giacomos.android.osmer.PROva.widgets.map;

public abstract interface OOverlayInterface 
{
	public abstract void clear();
	
	public abstract int type();
	
	public abstract void hideInfoWindow();
	
	public abstract boolean isInfoWindowVisible();
}
