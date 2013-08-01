package it.giacomos.android.meteofvg2;

import it.giacomos.android.meteofvg2.observations.ObservationTime;
import it.giacomos.android.meteofvg2.observations.ObservationType;

public interface ObservationTypeSelectionDialogListener 
{
	public void onSelectionDone(ObservationType t, ObservationTime oTime);
}
