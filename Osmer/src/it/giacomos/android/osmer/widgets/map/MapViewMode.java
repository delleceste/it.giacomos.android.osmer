package it.giacomos.android.osmer.widgets.map;

import it.giacomos.android.osmer.observations.MapMode;
import it.giacomos.android.osmer.observations.ObservationType;

public class MapViewMode 
{
	public MapViewMode()
	{

	}
	
	public boolean equals(MapViewMode other)
	{
		return other != null && other.currentMode == this.currentMode &&
				other.currentType == this.currentType;
	}
	
	public MapViewMode(ObservationType type, MapMode oMode)
	{
		currentType = type;
		currentMode = oMode;
	}
	
	public ObservationType currentType = ObservationType.NONE;
	public MapMode currentMode = MapMode.RADAR;
}