package it.giacomos.android.meteofvg2.widgets.map;

import it.giacomos.android.meteofvg2.observations.ObservationTime;
import it.giacomos.android.meteofvg2.observations.ObservationType;

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
	
	public MapViewMode(ObservationType type, ObservationTime oTime)
	{
		currentType = type;
		currentMode = oTime;
		isExplicit = true;
	}
	
	public ObservationType currentType = ObservationType.RADAR;
	public ObservationTime currentMode = ObservationTime.LATEST;
	
	/* is explicit means that the map mode has been explicitly set. 
	 * In the map fragment constructor a MapViewMode is initialized in order not 
	 * to be null but in this case it is not explicit.
	 */
	public boolean isExplicit;
}