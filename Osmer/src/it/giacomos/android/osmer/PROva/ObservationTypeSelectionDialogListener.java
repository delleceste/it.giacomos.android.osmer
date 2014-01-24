package it.giacomos.android.osmer.PROva;

import it.giacomos.android.osmer.PROva.observations.MapMode;
import it.giacomos.android.osmer.PROva.observations.ObservationType;

public interface ObservationTypeSelectionDialogListener 
{
	public void onSelectionDone(ObservationType t, MapMode mapMode);
}
