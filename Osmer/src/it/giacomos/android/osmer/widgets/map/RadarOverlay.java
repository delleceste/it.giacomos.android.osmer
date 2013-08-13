package it.giacomos.android.osmer.widgets.map;

import it.giacomos.android.osmer.locationUtils.GeoCoordinates;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.maps.Overlay;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Log;

public class RadarOverlay extends Overlay implements OOverlayInterface
{
	private GoogleMap mMap;
	private GroundOverlay mGroundOverlay;
	private Circle mGroundOverlayCircle;
	private CircleOptions mCircleOptions;
	private GroundOverlayOptions mGroundOverlayOptions;
	private Bitmap mBitmap;
	private Bitmap mBlackAndWhiteBitmap;
	

	public static final long ACCEPTABLE_RADAR_DIFF_TIMESTAMP_MILLIS = 1000 * 20 ;
	
// test	public static final long ACCEPTABLE_RADAR_DIFF_TIMESTAMP_MILLIS = 1000 * 5;
	
	RadarOverlay(GoogleMap googleMap) 
	{
		mMap = googleMap;
		/* ground overlay: the radar image */
		mGroundOverlay = null;
		mGroundOverlayCircle = null;
		mGroundOverlayOptions = new GroundOverlayOptions();
		mGroundOverlayOptions.positionFromBounds(GeoCoordinates.radarImageBounds);
		mGroundOverlayOptions.transparency(0.65f);
		/* circle: delimits the radar area */
		int color = Color.argb(120, 150, 160, 245);
		mCircleOptions = new CircleOptions();
		mCircleOptions.radius(GeoCoordinates.radarImageRadius());
		mCircleOptions.center(GeoCoordinates.radarImageCenter);
		mCircleOptions.strokeColor(color);
		mCircleOptions.strokeWidth(1);
		mBlackAndWhiteBitmap = null;
		mBitmap = null;
	}

	
	@Override /* no info windows on radar layer */
	public void hideInfoWindow()
	{
		
	}
	
	@Override /* no info windows on radar layer */
	public boolean isInfoWindowVisible()
	{
		return false;
	}
	
	@Override
	public int type() 
	{
		return OverlayType.RADAR;
	}
	
	public Bitmap getBitmap()
	{
		return mBitmap;
	}
	
	/** Remove the ground overlay from the map.
	 *  Bitmap remains valid.
	 */
	@Override
	public void clear()
	{
		if(mGroundOverlay != null)
		{
			mGroundOverlay.remove();
			mGroundOverlay = null;
		}
		if(mGroundOverlayCircle != null)
		{
			mGroundOverlayCircle.remove();
			mGroundOverlayCircle = null;
		}
	}
	
	/** updates the image in color.
	 * If a ground overlay was previously added (i.e. an image was attached to the map),
	 * it is first removed and then a new one is added.
	 */
	public void updateColour()
	{
		if(mBlackAndWhiteBitmap != null)
		{
			mBlackAndWhiteBitmap.recycle();
			mBlackAndWhiteBitmap = null;
		}
		mRefreshBitmap(mBitmap);
	}
	
	/** Starting from the bitmap saved from the network or from the cache (which is 
	 * always kept in memory), this method creates a black and white version of the same 
	 * image.
	 * Any ground overlay is removed from the GoogleMap object and a new one is readded.
	 */
	public void updateBlackAndWhite() 
	{
		/* need mBitmap not null because we draw starting from it */
		if(mBitmap == null)
			return;
		
//		Log.e("updateBlackAndWhite", "creo black and white");
		/* bitmap to black and white */
		int width, height;
	    height = mBitmap.getHeight();
	    width = mBitmap.getWidth();
	    /* being a class member, only one black and white bitmap will be
	     * used, and the old one is recycled.
	     */
	    if(mBlackAndWhiteBitmap != null)
	    	mBlackAndWhiteBitmap.recycle();
	    
	    /// DEBUG
//	    Log.e("RadarOverlay.updateBlackAndWhite", "Black and White recycled " + mBlackAndWhiteBitmap );
	    
	    mBlackAndWhiteBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	   
	    /// DEBUG
//	    Log.e("RadarOverlay.updateBlackAndWhite", "Black and White created " + mBlackAndWhiteBitmap);
	    
	    Canvas c = new Canvas(mBlackAndWhiteBitmap);
	    Paint paint = new Paint();
	    ColorMatrix cm = new ColorMatrix();
	    cm.setSaturation(0);
	    ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
	    paint.setColorFilter(f);
	    c.drawBitmap(mBitmap, 0, 0, paint);
	    
	    mRefreshBitmap(mBlackAndWhiteBitmap);
	}
	
	private void mRefreshBitmap(Bitmap bmp)
	{
		if(bmp == null)
		{
//			Log.e("mRadarOverlay.RefreshBitmap", "bitmap is null");
			return;
		}
		
		if(mGroundOverlay != null)
			mGroundOverlay.remove();
		
		if(mGroundOverlayCircle == null)
			mGroundOverlayCircle = mMap.addCircle(mCircleOptions);
		
		/* specify the image before the ovelay is added */
		BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bmp);
		mGroundOverlayOptions.image(bitmapDescriptor);
		mGroundOverlay = mMap.addGroundOverlay(mGroundOverlayOptions);
	}
	
	public boolean bitmapValid()
	{
		return mBitmap != null;
	}
	
	public void updateBitmap(Bitmap bmp)
	{
//		Log.e("RadarOverlay.updateBitmap", "old bmp " + mBitmap + ", new " + bmp);
		mBitmap = null;
		mBitmap = bmp;
	}
	
	public void finalize()
	{
		/* mBitmap reference is held by DataPool, which recycles all bitmaps
		 * inside its clear() method.
		 */
		if(mBlackAndWhiteBitmap != null) /* but we can recycle this */
			mBlackAndWhiteBitmap.recycle();
		
		mBitmap = null;
		mBlackAndWhiteBitmap = null;
	}
	

}
