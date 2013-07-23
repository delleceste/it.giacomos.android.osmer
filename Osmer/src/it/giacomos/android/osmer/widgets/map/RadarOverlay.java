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
import android.graphics.Color;

public class RadarOverlay extends Overlay implements OOverlayInterface
{
	private GoogleMap mMap;
	private GroundOverlay mGroundOverlay;
	private Circle mGroundOverlayCircle;
	private CircleOptions mCircleOptions;
	private GroundOverlayOptions mGroundOverlayOptions;
	
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
	
	public void update()
	{
		if(mBitmap == null)
			return;
		
		if(mGroundOverlay != null)
		{
			mGroundOverlay.remove();
			mGroundOverlay = null;
		}
		if(mGroundOverlayCircle == null)
		{
			mGroundOverlayCircle = mMap.addCircle(mCircleOptions);
		}
		
		/* specify the image before the ovelay is added */
		BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(mBitmap);
		mGroundOverlayOptions.image(bitmapDescriptor);
		mGroundOverlay = mMap.addGroundOverlay(mGroundOverlayOptions);
	}
	
	public boolean bitmapValid()
	{
		return mBitmap != null;
	}
	
	public void updateBitmap(Bitmap bmp)
	{
		mBitmap = null;
		mBitmap = bmp;
	}
	
	private Bitmap mBitmap;
}
