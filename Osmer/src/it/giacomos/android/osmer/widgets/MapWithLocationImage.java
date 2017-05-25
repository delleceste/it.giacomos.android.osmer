package it.giacomos.android.osmer.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.location.Location;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.locationUtils.LocationServiceAddressUpdateListener;
import it.giacomos.android.osmer.locationUtils.LocationServiceUpdateListener;

/** Class extending image view that is able to draw the location on demand.
 * 
 * @author giacomo
 * 
 * The MapWithLocationImage does not draw the location automatically in the onDraw() method.
 * Instead, you have to explicitly call drawLocation from a subclass.
 * This is done because you may want the location to be drawn as last element to be drawn.
 *
 */
public class MapWithLocationImage  extends ImageView 
implements LocationServiceUpdateListener, LocationServiceAddressUpdateListener
{
	private String mLocality = "...", mSubLocality = "", mAddress = "";
	private Location mLocation;
	private final int mLocationPointRadius1 = 8, mLocationPointRadius2 = 14, mLocationCircleRadius = 6;
	private PointF mLocationPoint;
	private boolean mDrawLocationEnabled = true;
	protected Paint mPaint;
	
	public MapWithLocationImage(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		mLocation = null;
		mLocationPoint = null;
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
	}
	

	public void onLocalityChanged(String locality, String subLocality, String address)
	{
		mLocality = locality;
		mSubLocality = subLocality;
		mAddress = address;
		this.invalidate();
	}

	@Override
	public void onLocationChanged(Location location) {
		mLocation = location;
		mLocationPoint = new LocationToImgPixelMapper().mapToPoint(this, location);
		this.invalidate();
	}

	@Override
	public void onLocationServiceError(String message) 
	{
		/* show a toast if visible */
		if(this.getVisibility() == View.VISIBLE)
			Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
	}

	protected boolean getDrawLocationEnabled()
	{
		return mDrawLocationEnabled;
	}
	
	protected void setDrawLocationEnabled(boolean ena)
	{
		mDrawLocationEnabled = ena;
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		/* recalculate location point when resized */
		if(mLocation != null)
			mLocationPoint = new LocationToImgPixelMapper().mapToPoint(this, mLocation);
	}

	protected void drawLocation(Canvas canvas)
	{
		if(mLocationPoint != null)
		{
			float strokeWidth = mPaint.getStrokeWidth();
			mPaint.setTextSize(20);
			float [] points = new float[8];

			mPaint.setARGB(180, 255, 255, 255);
			mPaint.setStrokeWidth(strokeWidth);
			canvas.drawCircle(mLocationPoint.x, mLocationPoint.y, mLocationCircleRadius, mPaint);

			points[0] = mLocationPoint.x - this.mLocationPointRadius1;
			points[1] = mLocationPoint.y - mLocationPointRadius1;
			points[2] = mLocationPoint.x + mLocationPointRadius1;
			points[3] = mLocationPoint.y + mLocationPointRadius1;
			/* second longer cross */
			points[4] = mLocationPoint.x + this.mLocationPointRadius2;
			points[5] = mLocationPoint.y - mLocationPointRadius2;
			points[6] = mLocationPoint.x - mLocationPointRadius2;
			points[7] = mLocationPoint.y + mLocationPointRadius2;

			/* draw the cross */
			mPaint.setARGB(225, 0, 0, 0);
			/* stronger stroke */
			mPaint.setStrokeWidth(1.3f);
			canvas.drawLines(points, mPaint);

			/*restore stroke width */
			mPaint.setStrokeWidth(strokeWidth);

			/* remove from image */
			float y = 0, x = 3;
			Rect txtR = new Rect();
			String txtLoc = "", txtAddr = "";
			if(!mSubLocality.isEmpty())
				txtLoc = mSubLocality;
			else if(!mLocality.isEmpty())
				txtLoc = mLocality;
			else
				txtLoc = "";

			int densityDpi = this.getResources().getDisplayMetrics().densityDpi;

			if(densityDpi == DisplayMetrics.DENSITY_XXXHIGH)
				mPaint.setTextSize(28f);
			else if(densityDpi == DisplayMetrics.DENSITY_XXHIGH)
				mPaint.setTextSize(25f);
			else if(densityDpi == DisplayMetrics.DENSITY_XHIGH)
				mPaint.setTextSize(20f);
			else if(densityDpi == DisplayMetrics.DENSITY_HIGH)
				mPaint.setTextSize(12);
			else
				mPaint.setTextSize(11);

			if(!mAddress.isEmpty())
			{
				txtAddr = mAddress + " ~"  + String.format("%.1f", mLocation.getAccuracy()) + this.getResources().getString(R.string.meters);
				mPaint.getTextBounds(txtAddr, 0, txtAddr.length(), txtR);
				y = 4 + txtR.height();
				canvas.drawText(txtAddr, x, y, mPaint);
			}

			mPaint.getTextBounds(txtLoc, 0, txtLoc.length(), txtR);
			y += 3 + txtR.height();
			canvas.drawText(txtLoc, x, y, mPaint);
		}
	}

	public Location getLocation()
	{
		return mLocation;
	}

	
	
}
