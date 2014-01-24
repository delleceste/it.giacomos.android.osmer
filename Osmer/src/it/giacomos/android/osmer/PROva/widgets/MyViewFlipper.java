package it.giacomos.android.osmer.PROva.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ViewFlipper;

/** Class that reimplements onDetachedFromWindow in order to fix the 
 *  IllegalArgumentException which appears to be a bug in android 2.1
 *  
 *  @author giacomo
 *
 */
public class MyViewFlipper extends ViewFlipper 
{
	public MyViewFlipper(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyViewFlipper(Context context) {
        super(context);
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
}
