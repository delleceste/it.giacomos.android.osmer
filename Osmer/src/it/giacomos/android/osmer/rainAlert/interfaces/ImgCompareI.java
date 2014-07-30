package it.giacomos.android.osmer.rainAlert.interfaces;

public interface ImgCompareI 
{
	public boolean compare(ImgOverlayInterface imgOI1, ImgOverlayInterface imgOI2, double lastDbz, boolean debug);
}
