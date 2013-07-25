package it.giacomos.android.osmer.widgets.map;

import it.giacomos.android.osmer.preferences.Settings;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class CustomMarkerBitmapFactory 
{
	private int mCachedFontSize = -1;
	private float mInitialFontSize;
	private int  mTextBgColor;
	private float mTextWidthScaleFactor;
	private int mAlphaBelowText;
	
	public void setTextWidthScaleFactor(float factor)
	{
		mTextWidthScaleFactor = factor;
	}
	
	public void setAlphaBelowText(int alpha)
	{
		mAlphaBelowText = alpha;
		mTextBgColor = Color.argb(mAlphaBelowText, 250, 252, 255);
	}
	
	public void setInitialFontSize(float fontSize)
	{
		mInitialFontSize = fontSize;
	}
	
	public float getCachedFontSize()
	{
		return mCachedFontSize;
	}
	
	public CustomMarkerBitmapFactory(Resources res)
	{
		DisplayMetrics dm = res.getDisplayMetrics();
		if(dm.densityDpi == DisplayMetrics.DENSITY_LOW)
			mInitialFontSize = 12;
		else if(dm.densityDpi == DisplayMetrics.DENSITY_MEDIUM)
			mInitialFontSize = 15;
		else
			mInitialFontSize = 25;
		mAlphaBelowText = 160;
		mTextBgColor = Color.argb(mAlphaBelowText, 250, 252, 255);	
		mTextWidthScaleFactor = 2.0f;
	}
	
	/*  <iconW>
	 * +------+-----+
	 * |      |     | <- iconH
	 * | icon |     |
	 * +------------+
	 * |        text| <- iconH * 0.5
	 * +------------+
	 * <-- textW --->
	 */
	public BitmapDescriptor getIcon(int resId, Resources res, String label)
	{
		int iconW, iconH;
		int textW, textH;
		Bitmap icon = BitmapFactory.decodeResource(res, resId);
		iconW = icon.getWidth();
		iconH = icon.getHeight();
		textW = (int) Math.round(iconW * mTextWidthScaleFactor);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.BLACK);
		if(mCachedFontSize < 0) /* start from a certain font size and then scale it afterwards */
			paint.setTextSize(mInitialFontSize);
		else
			paint.setTextSize(mCachedFontSize);
		
		Rect bounds = new Rect();
		paint.getTextBounds(label, 0, label.length(), bounds);
		/* scale text while its length or height fall outside bounds */
		while(bounds.width() > textW)
		{
			Log.e("getIcon", "scaled to " + paint.getTextSize());
			paint.setTextSize(paint.getTextSize() - 1);
			paint.getTextBounds(label, 0, label.length(), bounds);
		}
		textH = bounds.height();
		textW = bounds.width();
		Bitmap bitmap = Bitmap.createBitmap(Math.max(textW, iconW), iconH + textH, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		/* draw the text */
		paint.setColor(mTextBgColor);
		canvas.drawRoundRect(new RectF(bitmap.getWidth() - textW, iconH, textW + iconW, textH + iconH), 2, 2, paint);
		paint.setColor(Color.BLACK);
		/* draw the original bitmap */
		canvas.drawBitmap(icon, 0, 0, paint);
		canvas.drawText(label, bitmap.getWidth() - textW, iconH + textH - 2, paint);
		
		if(mCachedFontSize != Math.round(paint.getTextSize()))
			mCachedFontSize = Math.round(paint.getTextSize()); /* save it for future use (optimization) */
		
		return BitmapDescriptorFactory.fromBitmap(bitmap);	
	}
}
