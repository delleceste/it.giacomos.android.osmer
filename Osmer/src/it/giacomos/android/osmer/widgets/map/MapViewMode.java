package it.giacomos.android.osmer.widgets.map;

import it.giacomos.android.osmer.observations.MapMode;
import it.giacomos.android.osmer.observations.ObservationType;

public class MapViewMode 
{
	public MapViewMode()
	{
		isInit = true;
	}
	
	public boolean equals(MapViewMode other)
	{
		return other != null && other.currentMode == this.currentMode &&
				other.currentType == this.currentType && this.isInit == other.isInit;
	}
	
	public MapViewMode(ObservationType type, MapMode oMode)
	{
		currentType = type;
		currentMode = oMode;
		isInit = false;
	}
	
	public ObservationType currentType = ObservationType.NONE;
	public MapMode currentMode = MapMode.RADAR;
	
	/* isInit if true means that the map mode has been set while the google map was not ready yet.
	 */
	public boolean isInit;
}