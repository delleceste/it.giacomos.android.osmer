package it.giacomos.android.osmer.service;

import it.giacomos.android.osmer.rainAlert.RainDetectResult;

public interface RadarImageSyncAndCalculationTaskListener 
{
	public void onRainDetectionDone(RainDetectResult rainDetectResult);
}
