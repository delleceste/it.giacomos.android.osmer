package it.giacomos.android.osmer;

import it.giacomos.android.osmer.observations.MapMode;
import it.giacomos.android.osmer.observations.ObservationType;

public interface ObservationTypeSelectionDialogListener 
{
	public void onSelectionDone(ObservationType t, MapMode mapMode);
}
