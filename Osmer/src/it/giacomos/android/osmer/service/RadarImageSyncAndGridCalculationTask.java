package it.giacomos.android.osmer.service;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLngBounds;

import it.giacomos.android.osmer.locationUtils.GeoCoordinates;
import it.giacomos.android.osmer.rainAlert.RainDetectResult;
import it.giacomos.android.osmer.rainAlert.SyncImages;
import it.giacomos.android.osmer.rainAlert.genericAlgo.MeteoFvgImgParams;
import it.giacomos.android.osmer.rainAlert.genericAlgo.SloImgParams;
import it.giacomos.android.osmer.rainAlert.gridAlgo.ImgCompareGrids;
import it.giacomos.android.osmer.rainAlert.gridAlgo.ImgOverlayGrid;
import it.giacomos.android.osmer.rainAlert.interfaces.ImgParamsInterface;

public class RadarImageSyncAndGridCalculationTask extends
        AsyncTask<String, Integer, RainDetectResult>
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

//		mMyLatitude = 46.06009;
//		mMyLongitude = 12.079811;
    }

    @Override
    protected RainDetectResult doInBackground(String... configurations)
    {
        RainDetectResult rainDetectRes = null;
        /* some configuration file names and the grid configuration are passed inside configurations arg */
        String gridConf = configurations[0];
        String radarImgLocalPath = configurations[1];
        String radarImgRemotePath = configurations[2];
        String lastImgFileName = "";
        String prevImgFileName = "";
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

        int imgW = 0, imgH = 0;
        String radarSource = "";

        double defaultRadius = 20; /* 20km */
		
		/* sync radar images for rain detection */
        SyncImages syncer = new SyncImages();
        String[] config = syncer.sync(radarImgRemotePath, radarImgLocalPath);

        if (config != null)
        {
            String[] imgInfo = config[0].split(","); /* slo,700,500 */  /* source,imgWidth,imgHeight */
            if (imgInfo.length == 3)
            {
                try
                {
                    radarSource = imgInfo[0];
                    imgW = Integer.parseInt(imgInfo[1]);
                    imgH = Integer.parseInt(imgInfo[2]);
                    lastImgFileName = radarImgLocalPath + "/" + config[1];
                    prevImgFileName = radarImgLocalPath + "/" + config[2];

                    LatLngBounds bounds = null;
                /* get the bounds according to the radar image dimensions */
                    if (radarSource.compareToIgnoreCase("slo") == 0)
                        bounds = GeoCoordinates.getRadarImageBounds("slo");
                    else
                        bounds = GeoCoordinates.getRadarImageBounds("");

                    topLeftLat = bounds.northeast.latitude;
                    topLeftLon = bounds.southwest.longitude;
                    botRightLat = bounds.southwest.latitude;
                    botRightLon = bounds.northeast.longitude;

                    float[] result = new float[1];
				/* calculate width between topLeft lon and botRightLon (=topRightLon in a rectangle) along topLeftLat */
                    Location.distanceBetween(topLeftLat, topLeftLon, topLeftLat, botRightLon, result);
                    widthKm = result[0]; /* result is in meters */
				/* calculate width between topLeftLon and botRightLon (=topRightLon in a rectangle) along southern border */
                    Location.distanceBetween(botRightLat, topLeftLon, botRightLat, botRightLon, result);
				/* average the two results for best precision */
                    widthKm = (widthKm + result[0]) / 2.0000; /* result is in meters */

                    Location.distanceBetween(topLeftLat, topLeftLon, botRightLat, topLeftLon, result);  /* along western side */
                    heightKm = result[0];
                    Location.distanceBetween(topLeftLat, botRightLon, botRightLat, botRightLon, result); /* along eastern side */
				/* average the results */
                    heightKm = (heightKm + result[0]) / 2.000; /* result is in meters */

                    Log.e("RadarImageSync..doInBg", "slo radar: width averaged " + widthKm + " height averaged " + heightKm + " img w " + imgW + ", img h " + imgH + " top left lat " + topLeftLat + " top left lon " + topLeftLon
                        + " bot right lat " + botRightLat + " bot right lon " + botRightLon);
                }
                catch (NumberFormatException e)
                {
                    Log.e("RadarImgSync..doInBg", "error parsing line  " + config[0]);
                }

                ImgOverlayGrid imgoverlaygrid_0 = new ImgOverlayGrid(lastImgFileName,
                        imgW, imgH, topLeftLat, topLeftLon, botRightLat, botRightLon,
                        widthKm, heightKm, defaultRadius, mMyLatitude, mMyLongitude);

                ImgOverlayGrid imgoverlaygrid_1 = new ImgOverlayGrid(prevImgFileName,
                        501, 501, topLeftLat, topLeftLon, botRightLat,
                        botRightLon, widthKm, heightKm, defaultRadius, mMyLatitude, mMyLongitude);

                imgoverlaygrid_1.init(gridConf);
                imgoverlaygrid_0.init(gridConf);

                if (imgoverlaygrid_1.isValid() && imgoverlaygrid_0.isValid())
                {
                    /* use correct parameters for the image */
                    ImgParamsInterface imgParams = null;
                    if(radarSource.compareToIgnoreCase("slo") == 0)
                        imgParams = new SloImgParams();
                    else
                        imgParams = new MeteoFvgImgParams();

                    imgoverlaygrid_1.processImage(imgParams);
                    imgoverlaygrid_0.processImage(imgParams);

                    ImgCompareGrids imgCmpGrids = new ImgCompareGrids();
                    rainDetectRes = imgCmpGrids.compare(imgoverlaygrid_0, imgoverlaygrid_1, imgParams);
                }
                else /* latitude and longitude of the user outside the valid radar area */
                    rainDetectRes = new RainDetectResult();
            }
        }
        else
            Log.e("RadarImageSync... ", "config is null!");


        if (rainDetectRes != null)
            Log.e("RadarImageSync... ", "last " + lastImgFileName + ", prev " + prevImgFileName +
                    ", rain: " + rainDetectRes.willRain + " intensity: " + rainDetectRes.dbz + " tlLa " + topLeftLat + " tlLon " + topLeftLon + ", brla " +
                    botRightLat + ", brlon " + botRightLon + " myLa " + mMyLatitude + ", myLon " + mMyLongitude);

        return rainDetectRes;
    }

    @Override
    public void onPostExecute(RainDetectResult result)
    {
        if (result != null)
            mRadarImageSyncTaskListener.onRainDetectionDone(result);
    }

    @Override
    public void onCancelled(RainDetectResult result)
    {
		/* no need to call  onRainDetectionDone on mRadarImageSyncTaskListener */
        Log.e("RadarImageSyncAndGridCalculationTask.onCancelled", "task cancelled");
    }

}
