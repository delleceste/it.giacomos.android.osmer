package it.giacomos.android.osmer.rainAlert.gridAlgo;

import it.giacomos.android.osmer.rainAlert.interfaces.ImgCompareI;
import it.giacomos.android.osmer.rainAlert.interfaces.ImgOverlayInterface;


public class ImgCompareGrids implements ImgCompareI {

	@Override
	public boolean compare(ImgOverlayInterface imgOI1,
			ImgOverlayInterface imgOI2, double lastDbz, boolean debug) 
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	protected ImgOverlayGrid dLastGrid, dPrevGrid;

}
