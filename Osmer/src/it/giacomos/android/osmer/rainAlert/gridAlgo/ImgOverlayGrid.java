package it.giacomos.android.osmer.rainAlert.gridAlgo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import it.giacomos.android.osmer.rainAlert.genericAlgo.ImgOverlayBase;
import it.giacomos.android.osmer.rainAlert.interfaces.ImgParamsInterface;



public class ImgOverlayGrid extends ImgOverlayBase 
{
	private Grid mGrid;
	
	public ImgOverlayGrid(String imgFilename,
			int imgW,
			int imgH, 
			double topLeftLat, 
			double topLeftLon, 
			double botRightLat,
			double botRightLon, 
			double widthKm, 
			double heightKm,
			double radiusKm, 
			double lat, 
			double lon) 
	{
		super(imgFilename, imgW, imgH, topLeftLat, topLeftLon, botRightLat,
				botRightLon, widthKm, heightKm, radiusKm, lat, lon);
	}

	public Grid getGrid()
	{
		return mGrid;
	}

	public void init(String configurationAsString)
	{
		mGrid = new Grid();
		mGrid.setImgSize(getImgH(), getImgH());
		mGrid.setSize(getWidth(), getHeight());
		mGrid.init(configurationAsString, this.getCenterX(), this.getCenterY());
	}
	
	/** Calculates the value of the intensity in the grid. After this call ends, all the elements
	 * in the grid will have their intensity value calculated.
	 */
	@Override
	public void processImage(ImgParamsInterface imgParams) 
	{
		Log.e("ImgOverlayGrid.processImage", "decoding bitmap " + image_filename);
		Bitmap radar_image = BitmapFactory.decodeFile(this.image_filename);		
		if(radar_image != null)
		{			
			this.mGrid.calculateDbz(radar_image, imgParams);
		}
		else
		{
			Log.e("ImgOverlayGrid.processImage", "Failed to decode image from file " + image_filename);
		}
		
	}

}
