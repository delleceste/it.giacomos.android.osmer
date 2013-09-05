package it.giacomos.android.osmer.pro.widgets;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.pro.forecastRepr.Area;
import it.giacomos.android.osmer.pro.forecastRepr.ForecastDataFactory;
import it.giacomos.android.osmer.pro.forecastRepr.ForecastDataInterface;
import it.giacomos.android.osmer.pro.forecastRepr.ForecastDataType;
import it.giacomos.android.osmer.pro.locationUtils.LocationServiceAddressUpdateListener;
import it.giacomos.android.osmer.pro.locationUtils.LocationServiceUpdateListener;
import it.giacomos.android.osmer.pro.network.state.ViewType;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


public class ORegionImage extends ImageView 
implements LocationServiceUpdateListener, LocationServiceAddressUpdateListener
{

	public ORegionImage(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		mLocation = null;
		mLocationPoint = null;
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
	}

	public void setViewType(ViewType vt)
	{
		mViewType = vt;
	}
	
	public ViewType getViewType()
	{
		return mViewType;
	}
	
	public void setSymTable(String symtab)
	{
		ForecastDataFactory forecastDataFactory = new ForecastDataFactory(getResources());
		mForecastData = forecastDataFactory.getForecastData(symtab);
		this.invalidate();
	}
	
	/** sets a null callback on the drawables.
	 * recycles bitmaps.
	 * Checks for null in case setBitmap was called with a null parameter (and
	 * so the bitmap drawable is null).
	 */
	public void unbindDrawables()
	{		
		for(ForecastDataInterface fdi : mForecastData)
		{
			if(fdi.getType() == ForecastDataType.AREA)
			{
				Area a = (Area) fdi;
				Bitmap bmp = a.getSymbol();
				if(bmp != null)
				{
					Log.e("ORegionImage.umbindDrawables", "recycling bitmap " + bmp + ": " + a.getName() + ", " + mViewType);
					bmp.recycle();
				}
			}
		}
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

			if(densityDpi == DisplayMetrics.DENSITY_XHIGH)
				mPaint.setTextSize(20f);
			else if(densityDpi == DisplayMetrics.DENSITY_HIGH)
				mPaint.setTextSize(12);
			else
				mPaint.setTextSize(8);

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

	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		if(mDrawLocationEnabled)
			drawLocation(canvas);
		if(mViewType == ViewType.TODAY_SYMTABLE || 
				mViewType == ViewType.TOMORROW_SYMTABLE || 
				mViewType == ViewType.TWODAYS_SYMTABLE)
			drawSymbols(canvas);
	}
	
	public void drawSymbols(Canvas canvas)
	{
		Paint paint = new Paint();
		int iconW, iconH;
		LocationToImgPixelMapper locationMapper = new LocationToImgPixelMapper();
		for(ForecastDataInterface fdi : mForecastData)
		{
			if(fdi.getType() == ForecastDataType.AREA)
			{
				Area a = (Area) fdi;
				if(!a.isEmpty())
				{
					LatLng llng = a.getLatLng();
					PointF p = locationMapper.mapToPoint(this, llng.latitude, llng.longitude);
					Bitmap symbol = a.getSymbol();
					if(symbol != null)
					{
						iconW = symbol.getWidth();
						iconH = symbol.getHeight();
						canvas.drawBitmap(symbol, p.x - iconW/2, p.y - iconH/2, paint);
					}
				}
			}
		}
	}

	public Location getLocation()
	{
		return mLocation;
	}
	
	private String mLocality = "...", mSubLocality = "", mAddress = "";
	private Location mLocation;
	private final int mLocationPointRadius1 = 8, mLocationPointRadius2 = 14, mLocationCircleRadius = 6;
	private PointF mLocationPoint;
	private boolean mDrawLocationEnabled = true;
	protected Paint mPaint;
	private ViewType mViewType;
	private ArrayList<ForecastDataInterface> mForecastData;
}
