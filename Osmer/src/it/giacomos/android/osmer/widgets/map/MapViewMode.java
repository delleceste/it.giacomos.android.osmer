package it.giacomos.android.osmer.widgets.map;

import it.giacomos.android.osmer.observations.ObservationTime;
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
	
	public MapViewMode(ObservationType type, ObservationTime oTime)
	{
		currentType = type;
		currentMode = oTime;
	}
	
	public ObservationType currentType = ObservationType.RADAR;
	public ObservationTime currentMode = ObservationTime.LATEST;
}