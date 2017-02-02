package it.giacomos.android.osmer.rainAlert.genericAlgo;

import android.util.Log;

import it.giacomos.android.osmer.rainAlert.interfaces.ImgOverlayInterface;

public abstract class ImgOverlayBase implements ImgOverlayInterface
{
	public ImgOverlayBase(String imgFilename, int imgWi, int imgHe, 
			double topLeftLa,  double topLeftLo, 
			double botRightLa, double $botRightLo,
			double widthK, double heightK, double radiusK,
			double la,  double lo)
	{
		imgW = imgWi;
		imgH = imgHe;
		image_filename = imgFilename;
		
		topLeftLat = topLeftLa;
		topLeftLon = topLeftLo;
		
		botRightLat = botRightLa;
		botRightLon = $botRightLo;
		
		widthKm = widthK;
		heightKm = heightK;
		radiusKm = radiusK;
		
		/* latitude and longitude of the user must be a point inside the region */
		//if(la >= botRightLat && la <= topLeftLat && lo >= topLeftLon && lo <= botRightLon)
			mMapCenterToPix(la, lo);
	}
	
	private int imgW = 0, imgH = 0;
	private double topLeftLat = 0,  topLeftLon = 0,  botRightLat =  0, botRightLon = -1;
	
	private double mCenterX = -1.0,  mCenterY = -1.0;
	
	protected double widthKm = 0.0,  heightKm = 0.0,  radiusKm = 0.0;
	
	protected String image_filename = "";
	
	public boolean isValid()
	{
		return mCenterX >= 0.0 && mCenterY >= 0.0;
	}
	
	public int getImgW()
	{
		return imgW;
	}
	
	public int getImgH()
	{
		return imgH;
	}

	public double getCenterX()
	{
		return mCenterX;
	}
	
	public double getCenterY()
	{
		return mCenterY;
	}
	
	private void mMapCenterToPix(double lat, double lon)
	{
			/* map latitude/longitude lat, lon coordinates to x and y pixel coordinates between 0 and $limg (501 pixels wide) */
			mCenterX = (double) imgW * (lon - topLeftLon) /  (botRightLon - topLeftLon);
			mCenterY = (double) imgH - imgH * (lat - botRightLat) / (topLeftLat - botRightLat);
//			Log.e("ImgOverlayBase.mMapCenterToPix", "lat " + lat + ", lon " + lon + " mapped to " + mCenterX + ", "  + mCenterY);
	}
	
	public double getWidth()
	{
		return radiusKm * imgW / widthKm;
	}
	
	public double getHeight()
	{
		return radiusKm * imgH / heightKm;
	}
}

