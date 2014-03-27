package it.giacomos.android.osmer.widgets;

import it.giacomos.android.osmer.locationUtils.GeoCoordinates;
import it.giacomos.android.osmer.locationUtils.LocationUtils;
import android.graphics.PointF;
import android.location.Location;
import android.util.Log;
import android.widget.ImageView;

public class LocationToImgPixelMapper 
{
	public LocationToImgPixelMapper()
	{

	}

	public PointF mapToPoint(ImageView v, double latitude, double longitude)
	{
		LocationUtils locationUtils = new LocationUtils();
		boolean insideRegion = locationUtils.locationInsideRegion(latitude, longitude);
		locationUtils = null;
		
		if(!insideRegion)
			return null;
		
		float vW = v.getWidth(); /* view width */
		float vH = v.getHeight(); /* view height */
		
		/* the following four will store the pixel coordinates of the 
		 * leftmost, topmost.. points of the region, with respect to
		 * the full ImageView rectangle, since this rectangle is of 
		 * interest for drawing the location point.
		 * 
		 */
		float left, top, bottom, right;
		/* the scale factor with which the downloaded gif image has been 
		 * scaled
		 */
		float scaleFact; 

		/* since the image is a rectangle with the width larger than the height
		 * and the scaling is performed by the ImageView with the FIT_CENTER scale type
		 * if the ImageView ratio is not equal to the GIF ratio there will be two bands
		 * one above and the other below the image (or a band at the left and one at
		 * the right if scaling is based on height (1) ). Each band's thickness will be equal to
		 * the offset value, calculated taking the ImageView height minus the GIF
		 * height and then divided by two (and the opposite if scaling is based on height (1) )
		 */
		float offset;
		
		/* image height */
		float imgHeight, imgWidth;
		
		/* get top left bottom and right latitude/longitude of the region */
		double topLatitude = GeoCoordinates.fvgTopLeft.getLatitudeE6();
		double leftLongitude = GeoCoordinates.fvgTopLeft.getLongitudeE6();
		double bottomLatitude  = GeoCoordinates.fvgBottomRight.getLatitudeE6();
		double rightLongitude = GeoCoordinates.fvgBottomRight.getLongitudeE6();
		
		latitude = latitude * 1E6;
		longitude = longitude * 1E6;
		
		float viewRatio = vW / vH;
		float imgRatio = GifParamsDesc.width / GifParamsDesc.height;
		
		/* 
		 * Do some calculations in order to find the top, the bottom, the left and
		 * the right of the region in pixel coordinates with respect to the ImageView.
		 * Once this piece of information is obtained, we must convert the latitude 
		 * and longitude coordinates into ImageView pixel coordinates.
		 *  
		 */
		if(viewRatio > imgRatio) /* scale needs be based on height (1) */
		{
			scaleFact = vH / GifParamsDesc.height;
			imgWidth = GifParamsDesc.width * scaleFact;
			imgHeight = vH;
			top = vH * GifParamsDesc.regionTopPercent;
			bottom = vH * GifParamsDesc.regionBottomPercent;
			offset = (vW - imgWidth) / 2.0f;
			left = offset + imgWidth * GifParamsDesc.regionLeftPercent;
			right = offset + imgWidth * GifParamsDesc.regionRightPercent;
		}
		else
		{
			scaleFact = vW / GifParamsDesc.width;
			imgHeight = scaleFact * GifParamsDesc.height;
			imgWidth = vW;
			left = vW * GifParamsDesc.regionLeftPercent;
			right = vW * GifParamsDesc.regionRightPercent;
			offset =  (vH - imgHeight) / 2.0f; 
			top = offset + imgHeight * GifParamsDesc.regionTopPercent;
			bottom = offset + imgHeight * GifParamsDesc.regionBottomPercent;
		}	

		/* now calculate */
		/* xGeo (longitude) : widthGeo = xPixel : width */
		double xPixel = left + (longitude - leftLongitude) * (right - left) / (rightLongitude - leftLongitude);
		double yPixel = top + (topLatitude - latitude) * (bottom - top) / (topLatitude - bottomLatitude);
		
		PointF mappedPoint = new PointF(Math.round(xPixel), Math.round(yPixel));
//		Log.e("LocationToImgPixelMapper: ",   "viewWidth " + vW + ", viewHeight " + vH);
//		Log.e("LocationToImgPixelMapper: ",   "bmpWidth " + GifParamsDesc.width + ", bmpHeight " + GifParamsDesc.height);
//		Log.e("LocationToImgPixelMapper: ",  "lat " + latitude + ", long " + longitude + 
//				", scaleFact: " + scaleFact);
//		Log.e("LocationToImgPixelMapper: ",  "top bot left right: " + top + ", " + bottom +
//				", " + left + ", " + right + " topOffset " + offset);
//		Log.e("LocationToImgPixelMapper: ", "rightLong: " + rightLongitude + ", leftLong " + 
//				leftLongitude + ", topLat " + topLatitude + ", botLat" + bottomLatitude);
//		Log.e("LocationToImgPixelMapper: ", "mappedPoint: " + mappedPoint.x + ", " + mappedPoint.y);
		return mappedPoint;
	}
	
	/**
	 * Given an image view and a Location with a latitude and longitude, this method
	 * translates the Location into pixel coordinates, taking into account the original
	 * width and height of the background image as defined in the GifParamsDesc class.
	 * 
	 * @param v the ImageView on which you want the position in pixel coordinates to be calculated
	 * @param l the Location
	 * 
	 * @return point in pixel coordinates that maps the Location into local coordinates.
	 *         The returned point is allocated by this method.
	 * 
	 */
	public PointF mapToPoint(ImageView v, Location l)
	{
		double latitude = l.getLatitude();
		double longitude = l.getLongitude();
		return mapToPoint(v, latitude, longitude);
	}
}
