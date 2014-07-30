package it.giacomos.android.osmer.rainAlert.gridAlgo;

import it.giacomos.android.osmer.rainAlert.genericAlgo.ImgOverlayBase;
import it.giacomos.android.osmer.rainAlert.interfaces.ImgParamsInterface;



public class ImgOverlayGrid extends ImgOverlayBase 
{

	public ImgOverlayGrid(String imgFilename, 
			String fileSuffix, 
			int imgW,
			int imgH, 
			double topLeftLat, 
			double topLeftLon, 
			double botRightLat,
			double $botRightLon, 
			double widthKm, 
			double heightKm,
			double radiusKm, 
			double lat, 
			double lon) 
	{
		super(imgFilename, fileSuffix, imgW, imgH, topLeftLat, topLeftLon, botRightLat,
				$botRightLon, widthKm, heightKm, radiusKm, lat, lon);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double getDbz(double latitude, double longitude, double radius) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void processImage(ImgParamsInterface imgParams) {
		// TODO Auto-generated method stub
		
	}

}
