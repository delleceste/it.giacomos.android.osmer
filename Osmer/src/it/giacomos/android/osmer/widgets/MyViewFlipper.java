package it.giacomos.android.osmer.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ViewFlipper;

/** Class that reimplements onDetachedFromWindow in order to fix the 
 *  IllegalArgumentException which appears to be a bug in android 2.1
 *  
 *  @author giacomo
 *
 */
public class MyViewFlipper extends ViewFlipper
{
	private float mLastY = -1;
	
	private MyViewFlipperMovedListener mMyViewFlipperMovedListener;
	
	public MyViewFlipper(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyViewFlipper(Context context) {
        super(context);
    }
	
	public void setMyViewFlipperMovedListener(MyViewFlipperMovedListener l)
	{
		mMyViewFlipperMovedListener = l;
	}
	
	@Override
	/** bug ??
	 * http://daniel-codes.blogspot.com/2010/05/viewflipper-receiver-not-registered.html
	 */
	protected void onDetachedFromWindow() {
		try {
			super.onDetachedFromWindow();
		}
		catch (IllegalArgumentException e) {
			stopFlipping();
		}
	}
//
//	@Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) 
//	{
//		float y = ev.getY();
//		if(mLastY >= 0)
//		{
//			this.mMyViewFlipperMovedListener.onFlipperMovedUp(mLastY > y);
//		}
//		mLastY = y;
//		return super.onInterceptTouchEvent(ev);
//	}
}
