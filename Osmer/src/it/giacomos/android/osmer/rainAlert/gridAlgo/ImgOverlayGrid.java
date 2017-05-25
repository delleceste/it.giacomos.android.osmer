package it.giacomos.android.osmer.rainAlert.gridAlgo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import it.giacomos.android.osmer.rainAlert.genericAlgo.ImgOverlayBase;
import it.giacomos.android.osmer.rainAlert.interfaces.ImgParamsInterface;



public class ImgOverlayGrid extends ImgOverlayBase 
{
	private Grid mGrid;
	
	public ImgOverlayGrid(Bitmap bmp,
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
		super(bmp, topLeftLat, topLeftLon, botRightLat,
				botRightLon, widthKm, heightKm, radiusKm, lat, lon);
	}

	public Grid getGrid()
	{
		return mGrid;
	}

	public void init(String configurationAsString)
	{
		mGrid = new Grid();
		mGrid.setImgSize(getImgW(), getImgH());
		mGrid.setSize(getWidth(), getHeight());
		mGrid.init(configurationAsString, this.getCenterX(), this.getCenterY());
	}
	
	/** Calculates the value of the intensity in the grid. After this call ends, all the elements
	 * in the grid will have their intensity value calculated.
	 */
	@Override
	public void processImage(ImgParamsInterface imgParams) 
	{
		if(image != null)
		{			
			this.mGrid.calculateDbz(image, imgParams);
		}
		else
		{
			Log.e("ImgOvGrid.processImage", "Image is null!");
		}
		
	}

}
