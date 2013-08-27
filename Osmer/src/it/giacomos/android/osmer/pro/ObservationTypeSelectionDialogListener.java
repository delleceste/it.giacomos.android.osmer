package it.giacomos.android.osmer.pro;

import it.giacomos.android.osmer.pro.observations.MapMode;
import it.giacomos.android.osmer.pro.observations.ObservationType;

public interface ObservationTypeSelectionDialogListener 
{
	public void onSelectionDone(ObservationType t, MapMode mapMode);
}
