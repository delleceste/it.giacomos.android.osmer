package it.giacomos.android.osmer.widgets.map;

import it.giacomos.android.osmer.observations.MapMode;
import it.giacomos.android.osmer.observations.ObservationType;

public class MapViewMode 
{
	public MapViewMode()
	{
		isExplicit = true;
	}
	
	public boolean equals(MapViewMode other)
	{
		return other != null && other.currentMode == this.currentMode &&
				other.currentType == this.currentType && this.isExplicit == other.isExplicit;
	}
	
	public MapViewMode(ObservationType type, MapMode oMode)
	{
		currentType = type;
		currentMode = oMode;
		isExplicit = true;
	}
	
	public ObservationType currentType = ObservationType.NONE;
	public MapMode currentMode = MapMode.RADAR;
	
	/* is explicit means that the map mode has been explicitly set. 
	 * In the map fragment constructor a MapViewMode is initialized in order not 
	 * to be null but in this case it is not explicit.
	 */
	public boolean isExplicit;
}