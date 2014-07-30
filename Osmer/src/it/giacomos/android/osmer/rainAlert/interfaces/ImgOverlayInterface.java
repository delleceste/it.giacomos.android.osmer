package it.giacomos.android.osmer.rainAlert.interfaces;

public interface ImgOverlayInterface {
	
	public double getDbz(double latitude, double longitude, double radius);
		
	public void processImage(ImgParamsInterface imgParams);
	
}
