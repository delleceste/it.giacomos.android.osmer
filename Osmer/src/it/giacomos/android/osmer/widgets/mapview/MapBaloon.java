package it.giacomos.android.osmer.widgets.mapview;

import com.google.android.maps.GeoPoint;

import it.giacomos.android.osmer.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

// import android.util.Log;

public class MapBaloon extends LinearLayout {

	public enum Type { OBSERVATIONS, WEBCAM }
	
	public MapBaloon(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		mGeoPoint = null;
		mType = Type.OBSERVATIONS;
	}
	
	public Type getType()
	{
		return mType;
	}
	
	public void setType(Type t)
	{
		mType = t;
	}
	
	protected void onFinishInflate ()
	{
		super.onFinishInflate();
	}
	
	public void setTitle(String t)
	{
		TextView tv = (TextView) findViewById(R.id.baloon_title);
		if(tv != null)
		{
			tv.setText(t);
		}
	}
	
	public void setText(String t)
	{
		TextView tv = (TextView) findViewById(R.id.baloon_text);
		if(tv != null)
		{
			tv.setMinLines(t.split("\n").length + 2);
			tv.setMaxLines(t.split("\n").length + 2);
			tv.setText(t);
		}
	}
	
	public void setIcon(Drawable dra)
	{
		ImageView iv = (ImageView) findViewById(R.id.baloon_icon);
		iv.setScaleType(ScaleType.CENTER_CROP);
		iv.setBackgroundDrawable(dra);
	}
	
	public void setIcon(int id)
	{
		ImageView iv = (ImageView) findViewById(R.id.baloon_icon);
		if(id > -1)
			iv.setBackgroundDrawable(this.getResources().getDrawable(id));
		else
		{
			/* put an icon to show on the baloon otherwise the layout is scrambled
			 * Also, this indicates the lack of data in that time.
		 	*/

			iv.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.weather_none_available_map));
		}
	}
	
	public GeoPoint getGeoPoint()
	{
		return mGeoPoint;
	}
	
	public void setGeoPoint(GeoPoint p)
	{
		mGeoPoint = p;
	}

	@Override
    protected void dispatchDraw(Canvas canvas) {       
		int wid = getWidth();
		int hei = getHeight();
		int balRectHeight = 5 * hei/6;
        Paint panelPaint  = new Paint();
        panelPaint.setARGB(0, 0, 0, 0);
               
        RectF panelRect = new RectF();
        panelRect.set(0,0, wid, hei);
        canvas.drawRoundRect(panelRect, 5, 5, panelPaint);
       
        RectF baloonRect = new RectF();
        baloonRect.set(0,0, wid, balRectHeight);
        panelPaint.setARGB(240, 255, 255, 255);       
        canvas.drawRoundRect(baloonRect, 10, 10, panelPaint);
       
        Path baloonTip = new Path();
        baloonTip.moveTo(5*(wid/8), balRectHeight);
        baloonTip.lineTo(wid/2, hei);
        baloonTip.lineTo(3*(wid/4), balRectHeight);
       
        canvas.drawPath(baloonTip, panelPaint);
               
        super.dispatchDraw(canvas);
    }
	
	private Type mType;
	
	private GeoPoint mGeoPoint;

	
}
