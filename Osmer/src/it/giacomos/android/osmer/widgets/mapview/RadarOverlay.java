package it.giacomos.android.osmer.widgets.mapview;

import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.locationUtils.GeoCoordinates;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;


public class RadarOverlay extends Overlay implements OOverlayInterface
{
	RadarOverlay() 
	{
		super();
		mPaint = new Paint();
		mPaint.setFilterBitmap(true);
        mPaint.setAntiAlias(true);
	}

	@Override
	public int type() {
		return OverlayType.RADAR;
	}
	
	
	@Override
    public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when)
	{
		super.draw(canvas, mapView, shadow);
		
		if(mBitmap != null)
		{
			/*  second param null: A pre-existing object to use for the output; if null, a new Point will
			 *  be allocated and returned.
			 */
			Point topLeft = mapView.getProjection().toPixels(GeoCoordinates.topLeft, null);
			Point bottomRight = mapView.getProjection().toPixels(GeoCoordinates.bottomRight, null);
			
			Rect src = new Rect( 0, 0, mBitmap.getWidth(), mBitmap.getHeight() );
	        Rect dst = new Rect( topLeft.x, topLeft.y ,bottomRight.x, bottomRight.y );
	
//	        Log.i("draw RadarOverlay", "drawing bitmap (is null?) " + mBitmap + " - src " + src.toShortString() +
//	        		" dst " + dst.toShortString() + "Density " + mBitmap.getDensity() +
//	        		"draw rect " + canvas.getClipBounds().toShortString());
	        mPaint.setStyle(Paint.Style.STROKE);
	        mPaint.setAlpha(45);
	        canvas.drawCircle(dst.centerX(), dst.centerY(), dst.width()/2, mPaint);
	        canvas.drawBitmap(mBitmap, src, dst, mPaint);
	        mPaint.setAlpha(160);
	        Bitmap dbzScale = BitmapFactory.decodeResource(mapView.getResources(), R.drawable.scala_vmi_4);
	        canvas.drawBitmap(dbzScale, 
	        		dst.centerX() + dst.width() / 2 + 15,
	        		dst.centerY() - dbzScale.getHeight() / 2 , mPaint);
		}
		return false;
	}

	public Bitmap getBitmap()
	{
		return mBitmap;
	}
	
	public void updateBitmap(Bitmap bmp)
	{
		mBitmap = bmp;
	}
	
	Bitmap mBitmap;
	
	private Paint mPaint;
}
