package it.giacomos.android.osmer.rainAlert.interfaces;

import it.giacomos.android.osmer.rainAlert.RainDetectResult;

public interface ImgCompareI 
{
	public RainDetectResult compare(ImgOverlayInterface imgOI1, 
			ImgOverlayInterface imgOI2, 
			ImgParamsInterface img_params_interface);
}
