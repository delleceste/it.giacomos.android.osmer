package it.giacomos.android.osmer.locationUtils;

import android.graphics.Bitmap;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class GeoCoordinates {
	
	/** 
	 * top left anchor coord
	 */
	public static final LatLng topLeft = new LatLng(46840517/1e6, 11938366/1e6);
	
	/**
	 * bottom right anchor coord
	 */
	public static final LatLng bottomRight = new LatLng(44597090/1e6, 15029332/1e6);
	
	/**
	 * Fossalon radar position
	 */
	public static final LatLng center = new LatLng(45726700/1e6, 13477500/1e6);
	
	/**
	 * coordinates of the region only
	 * 
	 * - left taken from point  46.264554, 12.321253 
	 * - top taken from point 46.647917, 12.762669
	 *   http://www.gorissen.info/Pierre/maps/googleMapLocation.php?lat=46.647917&lon=12.762669&setLatLon=Set
	 *   
	 */
	public static final LatLng fvgTopLeft = new LatLng(46647917/1e6, 12321253/1e6);
	
	/**
	 * - right taken from  45.633342, 13.918819
	 *   http://www.gorissen.info/Pierre/maps/googleMapLocation.php?lat=45.633342&lon=13.918819&setLatLon=Set
	 * - bottom taken from  45.580838, 13.810244
	 *   http://www.gorissen.info/Pierre/maps/googleMapLocation.php?lat=45.580838&lon=13.810244&setLatLon=Set
	 */
	public static final LatLng fvgBottomRight = new LatLng(45580838/1e6, 13918819/1e6 );
	
	/* LatLngBounds southwest, northeast */
	public static final LatLngBounds radarImageBounds = new LatLngBounds(new LatLng(44.569090, 11.828366),
			new LatLng(46.830517, 15.1057));
	
	//public static final LatLngBounds radarImageBounds = new LatLngBounds(new LatLng(44.6052, 11.9294), 
	//		new LatLng(46.8080, 15.0857));
	
	public static final LatLng radarImageCenter = new LatLng((radarImageBounds.northeast.latitude + 
			radarImageBounds.southwest.latitude) / 2.0f, 
			(radarImageBounds.northeast.longitude + radarImageBounds.southwest.longitude)/2.0f);
	
	public static final float radarImageRadius()
	{
		float [] results = new float[1];
		Location.distanceBetween(radarImageCenter.latitude, radarImageCenter.longitude, radarImageCenter.latitude, radarImageBounds.northeast.longitude, results);
		return results[0];
	}
	
	public static final LatLng fvgNorthEast = new LatLng(46647917/1e6, 13918819/1e6);
	
	public static final LatLng fvgSouthWest = new LatLng(45580838/1e6, 12321253/1e6);
	
	public static final LatLngBounds regionBounds = new LatLngBounds(fvgSouthWest, fvgNorthEast);
	
	public static final LatLng radarScaleTopLeft = new LatLng(45.801082, 15.351009);

//	public static LatLngBounds getRadarImageBounds(String mRadarSource)
//	{
//		/* slo
//		 * south: premantura
//		 */
//		if(mRadarSource.compareToIgnoreCase("SLO") == 0)
//			/*                                  south       west                   north     east      */
//			// from  calculation return new LatLngBounds(new LatLng(44.792496, 12.185306), new LatLng(47.403526, 16.696444));
//			return new LatLngBounds(new LatLng(44.906496, 12.182306), new LatLng(47.26726, 16.693444));
//		else
//			return radarImageBounds;
//	}

	public static LatLngBounds getRadarImageBounds(Bitmap bmp)
	{
		if(bmp.getWidth() == 512 && bmp.getHeight() == 600) /* ARPAV */
		{
			return new LatLngBounds(new LatLng(44.230090, 11.115066),
					new LatLng(47.000517, 14.4337));
		}
		return radarImageBounds;

	}
}
