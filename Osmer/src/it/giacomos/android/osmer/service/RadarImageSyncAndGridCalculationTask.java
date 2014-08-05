package it.giacomos.android.osmer.service;
import android.os.AsyncTask;
import android.util.Log;

import it.giacomos.android.osmer.locationUtils.GeoCoordinates;
import it.giacomos.android.osmer.rainAlert.SyncImages;
import it.giacomos.android.osmer.rainAlert.genericAlgo.MeteoFvgImgParams;
import it.giacomos.android.osmer.rainAlert.gridAlgo.ImgCompareGrids;
import it.giacomos.android.osmer.rainAlert.gridAlgo.ImgOverlayGrid;

public class RadarImageSyncAndGridCalculationTask extends
		AsyncTask<String, Integer, Boolean> 
{
	private RadarImageSyncAndCalculationTaskListener mRadarImageSyncTaskListener;
	private double mMyLatitude, mMyLongitude;
	
	public RadarImageSyncAndGridCalculationTask(double mylatitude, 
			double mylongitude, 
			RadarImageSyncAndCalculationTaskListener radarImageSyncAndCalculationTaskListener)
	{
		mRadarImageSyncTaskListener = radarImageSyncAndCalculationTaskListener;
		mMyLatitude = mylatitude;
		mMyLongitude = mylongitude;
	}
	
	@Override
	protected Boolean doInBackground(String... configurations) 
	{
		boolean willRain = false; /* by default, if something fails, no notification */
		/* some configuration file names and the grid configuration are passed inside configurations arg */
		String gridConf = configurations[0];
		String radarImgLocalPath = configurations[1];
		String radarImgRemotePath = configurations[2];
		
		/* sync radar images for rain detection */
		SyncImages syncer = new SyncImages();
		String [] filenames = syncer.sync(radarImgRemotePath, radarImgLocalPath);
		
		if(filenames != null)
		{
			String lastImgFileName = radarImgLocalPath + "/" + filenames[0];
			String prevImgFileName = radarImgLocalPath + "/" + filenames[1];
			
 
			/* From GeoCoordinates.java:
			 * public static final LatLngBounds radarImageBounds = new LatLngBounds(new LatLng(44.6052, 11.9294), 
			 *		new LatLng(46.8080, 15.0857));
			 */
			double topLeftLat = GeoCoordinates.radarImageBounds.northeast.latitude;
			double topLeftLon = GeoCoordinates.radarImageBounds.southwest.longitude;
			double botRightLat = GeoCoordinates.radarImageBounds.southwest.latitude;
			double botRightLon = GeoCoordinates.radarImageBounds.northeast.longitude;

			double widthKm = 240.337;
			double heightKm = 244.153;

			double defaultRadius = 20; /* 20km */

			double last_dbz = 0.0; /* reference passed to compare */

			ImgOverlayGrid imgoverlaygrid_0 = new ImgOverlayGrid(lastImgFileName, 
					501, 501, topLeftLat, topLeftLon, botRightLat, botRightLon, 
					widthKm, heightKm, defaultRadius, mMyLatitude, mMyLongitude);
			
			ImgOverlayGrid 	imgoverlaygrid_1 = new ImgOverlayGrid(prevImgFileName, 
					501, 501, topLeftLat, topLeftLon, botRightLat, 
					botRightLon, widthKm, heightKm, defaultRadius, mMyLatitude, mMyLongitude);

			imgoverlaygrid_1.init(gridConf);
			imgoverlaygrid_0.init(gridConf);

			MeteoFvgImgParams	imgParams = new MeteoFvgImgParams();

			imgoverlaygrid_1.processImage(imgParams);
			imgoverlaygrid_0.processImage(imgParams);

			ImgCompareGrids imgCmpGrids = new ImgCompareGrids();
			willRain = imgCmpGrids.compare(imgoverlaygrid_0,  imgoverlaygrid_1, imgParams, last_dbz);

//			Log.e("RadarImageSync... ", "last " + lastImgFileName + ", prev " + prevImgFileName + " last dbz " + 
//					last_dbz + ", rain: " + willRain + " tlLa " + topLeftLat + " tlLon " + topLeftLon + ", brla " +
//					botRightLat + ", brlon " + botRightLon);
		}
		else
			Log.e("RadarImageSync... ", "filenames is null!");

		return willRain;
	}
	
	@Override
	public void onPostExecute(Boolean willRain)
	{
		mRadarImageSyncTaskListener.onRainDetectionDone(willRain);
	}
	
	@Override
	public void onCancelled(Boolean willRain)
	{
		Log.e("RadarImageSyncAndGridCalculationTask.onCancelled", "task cancelled");
		mRadarImageSyncTaskListener.onRainDetectionDone(false);
	}

}
