package it.giacomos.android.osmer.widgets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import it.giacomos.android.osmer.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;


public class ODoubleLayerImageView extends ImageView 
	implements StateSaver
{

	public ODoubleLayerImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mRestoreSuccessful = false;
		mRestoredFromInternalStorage = false;
		mLocation = null;
		mLocationPoint = null;
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mLayerDrawableAvailable = false;
	}

	@Override
	public boolean saveOnInternalStorage() 
	{	
		if(mLayerDrawableAvailable)
		{
			LayerDrawable lDrawable = (LayerDrawable) getDrawable();
			if(lDrawable != null)
			{
				FileOutputStream fos;
				try {
					fos = getContext().openFileOutput(makeFileName(), Context.MODE_PRIVATE);
					/* bitmap is stored in layer 1 (see setBitmap) */
					BitmapDrawable bd = (BitmapDrawable) lDrawable.getDrawable(1);
					bd.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, fos);	
					fos.close();
					return true; /* success */
				} 
				catch (FileNotFoundException e) {
					/* nada que hacer */
				}
				catch (IOException e) {

				}
			}
		}

		return false; /* bitmap null or impossible to save on file */
	}

	public boolean restoreFromInternalStorage()
	{
		/* Decode a file path into a bitmap. If the specified file name is null, 
		 * or cannot be decoded into a bitmap, the function returns null. 
		 */
		Log.i("ODoubleLayer...", "* restoreFromInternalStorage: setting bitmap" + this.toString() + " : " + Integer.toHexString(getId()));
		File filesDir = this.getContext().getApplicationContext().getFilesDir();
		Bitmap bmp = BitmapFactory.decodeFile(filesDir.getAbsolutePath() + "/" + makeFileName());
		if(bmp != null)
		{
			this.setBitmap(bmp);
			mRestoredFromInternalStorage = true;
			mRestoreSuccessful = true;
		}
		return bmp != null;
	}
	
	public Parcelable onSaveInstanceState()
	{
		Parcelable p = super.onSaveInstanceState();
		Bundle bundle = new Bundle();
		bundle.putParcelable("OImageViewState", p);
		if(mLayerDrawableAvailable)
		{
			LayerDrawable lDrawable = (LayerDrawable) getDrawable();
			if(lDrawable != null)
			{	
				BitmapDrawable bd = (BitmapDrawable) lDrawable.getDrawable(1);
				bundle.putParcelable("bitmap", bd.getBitmap());
			}
		}
		return bundle;
	}
	
	public void onRestoreInstanceState (Parcelable state)
	{
		Bundle b = (Bundle) state;
		/* do not restore twice if restoreFromInternalStorage completed successfully
		 * 
		 */
		if(!mRestoredFromInternalStorage)
		{
			Bitmap bmp = (Bitmap) b.getParcelable("bitmap");
			Log.i("ODoubleLayer...", "* onRestoreInstanceState: setting bitmap" + bmp + this.toString()+ " : " + Integer.toHexString(getId()));
			if(bmp != null)
			{
				setBitmap(bmp);
				mRestoreSuccessful = true;
			}
		}
		super.onRestoreInstanceState(b.getParcelable("OImageViewState"));
	}

	public void setBitmap(Bitmap bitmap) 
	{
		Drawable layers[] = new Drawable[2];
		Context appContext = this.getContext().getApplicationContext();
		Resources res = appContext.getResources();
		Bitmap fvgBackgroundBmp = BitmapFactory.decodeResource(res, R.drawable.fvg_background2);
		layers[0] = new BitmapDrawable(res, fvgBackgroundBmp);
		layers[1] = new BitmapDrawable(res, bitmap);
		LayerDrawable layerDrawable = new LayerDrawable(layers);
		setImageDrawable(layerDrawable);
		mLayerDrawableAvailable = true;
	}

	public boolean isRestoreSuccessful() {
		return mRestoreSuccessful;
	}

	public String makeFileName()
	{
		return "image_" + this.getId() + ".bmp";
	}

	public void unbindDrawables()
	{
		if(mLayerDrawableAvailable)
		{
			LayerDrawable lDrawable = (LayerDrawable) getDrawable();
			if(lDrawable != null)
			{	
				lDrawable.getDrawable(1).setCallback(null);
				lDrawable.getDrawable(0).setCallback(null);
				lDrawable.setCallback(null);
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
	
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		mLocation = location;
		mLocationPoint = new LocationToImgPixelMapper().mapToPoint(this, location);
		this.invalidate();
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
				txtLoc = "...";
					// mAccuracy + " " + String.format("%.2f", mLocation.getAccuracy()) + "m";
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
	}
	
	public Location getLocation()
	{
		return mLocation;
	}
	
	protected boolean mRestoredFromInternalStorage;
	
	private String mLocality = "...", mSubLocality = "", mAddress = "";
	
	private Location mLocation;
	
	private final int mLocationPointRadius1 = 8, mLocationPointRadius2 = 14, mLocationCircleRadius = 6;
	
	private PointF mLocationPoint;
			
	private boolean mRestoreSuccessful = false;
	
	private boolean mDrawLocationEnabled = true;
	
	protected Paint mPaint;
	
	private boolean mLayerDrawableAvailable;
		
}
