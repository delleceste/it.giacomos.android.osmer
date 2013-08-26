package it.giacomos.android.osmer.pro;

import it.giacomos.android.osmer.pro.observations.ObservationTime;
import it.giacomos.android.osmer.pro.observations.ObservationType;

public interface ObservationTypeSelectionDialogListener 
{
	public void onSelectionDone(ObservationType t, ObservationTime oTime);
}
