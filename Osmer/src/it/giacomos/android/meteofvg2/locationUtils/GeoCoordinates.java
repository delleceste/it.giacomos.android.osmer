package it.giacomos.android.meteofvg2.locationUtils;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.maps.GeoPoint;

public class GeoCoordinates {
	
	/** 
	 * top left anchor coord
	 */
	public static final GeoPoint topLeft = new GeoPoint(46840517,11938366);
	
	/**
	 * bottom right anchor coord
	 */
	public static final GeoPoint bottomRight = new GeoPoint(44597090,15029332);
	
	/**
	 * Fossalon radar position
	 */
	public static final GeoPoint center = new GeoPoint(45726700, 13477500);
	
	/**
	 * coordinates of the region only
	 * 
	 * - left taken from point  46.264554, 12.321253 
	 * - top taken from point 46.647917, 12.762669
	 *   http://www.gorissen.info/Pierre/maps/googleMapLocation.php?lat=46.647917&lon=12.762669&setLatLon=Set
	 *   
	 */
	public static final GeoPoint fvgTopLeft = new GeoPoint(46647917, 12321253);
	
	/**
	 * - right taken from  45.633342, 13.918819
	 *   http://www.gorissen.info/Pierre/maps/googleMapLocation.php?lat=45.633342&lon=13.918819&setLatLon=Set
	 * - bottom taken from  45.580838, 13.810244
	 *   http://www.gorissen.info/Pierre/maps/googleMapLocation.php?lat=45.580838&lon=13.810244&setLatLon=Set
	 */
	public static final GeoPoint fvgBottomRight = new GeoPoint(45580838, 13918819 );
	
	public static final LatLngBounds radarImageBounds = new LatLngBounds(new LatLng(44.6029, 11.8342), 
			new LatLng(46.8505, 15.0559));
	
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
}
