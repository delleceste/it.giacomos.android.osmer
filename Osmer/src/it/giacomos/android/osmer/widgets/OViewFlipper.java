package it.giacomos.android.osmer.widgets;

import it.giacomos.android.osmer.FlipperChildChangeListener;
import it.giacomos.android.osmer.FlipperChildren;
import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.guiHelpers.TitlebarUpdater;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;
import it.giacomos.android.osmer.preferences.*;
public class OViewFlipper extends ViewFlipper implements StateSaver , OnTouchListener
{
	public OViewFlipper(Context context, AttributeSet attrs) {
		super(context, attrs);	
		mRestoreSuccessful = false;
		mDisableMove = false;
		mSettings = new Settings(context);
		mSwipeHintEnabled = mSettings.isSwipeHintEnabled();
	}

	public void setDisplayedChild(int child)
	{
		super.setDisplayedChild(child);
		mFlipperChildChangeListener.onFlipperChildChangeEvent(child);
	}

	public void setOnChildPageChangedListener(FlipperChildChangeListener l)
	{
		mFlipperChildChangeListener = l;
	}

	@Override
	public boolean saveOnInternalStorage() 
	{
		return true;
	}

	public boolean restoreFromInternalStorage()
	{
		return true;
	}

	public Parcelable onSaveInstanceState()
	{
		Parcelable p = super.onSaveInstanceState();
		Bundle bundle = new Bundle();
		bundle.putParcelable("OViewFlipper", p);
		bundle.putInt("displayedChild", getDisplayedChild());
		return bundle;
	}

	public void onRestoreInstanceState (Parcelable state)
	{
		Bundle b = (Bundle) state;
		mRestoreSuccessful = b.containsKey("displayedChild");
		if(mRestoreSuccessful)
			setDisplayedChild(b.getInt("displayedChild"));
		super.onRestoreInstanceState(b.getParcelable("OViewFlipper"));
	}

	public boolean onTouch(View v, MotionEvent touchevent) {
		// Get the action that was donAndroide on this touch event

	//	Log.e("onTouch ", "displayedCHild " + getDisplayedChild());
		if(getDisplayedChild() == FlipperChildren.MAP)
			return false;
		
		switch (touchevent.getAction())
		{
		case MotionEvent.ACTION_DOWN:
		{
			// store the X value when the user's finger was pressed down
			mDownXValue = touchevent.getX();
			mDisableMove = false;
			/* propagate touch event on child, otherwise it wouldn't be 
			 * called
			 */
			v.onTouchEvent(touchevent);
			break; 
		}
		case MotionEvent.ACTION_OUTSIDE:
		case MotionEvent.EDGE_RIGHT:
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_MOVE:
		{
			if(!mDisableMove)
			{
				// Get the X value when the user released his/her finger
				float currentX = touchevent.getX();            
				OViewFlipper vf = (OViewFlipper) findViewById(R.id.viewFlipper1);
				// going backwards: pushing stuff to the right
				if (mDownXValue - currentX < -30)
				{                 
					// Set the animation
					vf.setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_right));
					vf.setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_right));

					// Flip!
					vf.showPrevious();
					mFlipperChildChangeListener.onFlipperChildChangeEvent(this.getDisplayedChild());
					/* upon flipping, disable further unwanted flippings (until next ACTION_DOWN) */
					mDisableMove = true;
				}

				// going forwards: pushing stuff to the left
				else if (mDownXValue - currentX > 30)
				{
					// Set the animation
					vf.setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_left));
					vf.setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_left));
					// Flip!
					vf.showNext();
					mFlipperChildChangeListener.onFlipperChildChangeEvent(this.getDisplayedChild());
					/* upon flipping, disable further unwanted flippings  (until next ACTION_DOWN) */
					mDisableMove = true;
				}
				
				new TitlebarUpdater((OsmerActivity) getContext());
				
				/* disable swipe hint (Toast displayed on Activity) */
				if(mSwipeHintEnabled)
				{
					mSwipeHintEnabled = false;
					mSettings.setSwipeHintEnabled(false);
				}
			}
			break;
		}
		}

		// if you return false, these actions will not be recorded
		return true;
	}


	public boolean isRestoreSuccessful() {
		return mRestoreSuccessful;
	}

	public String makeFileName()
	{
		return "";
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

	private boolean mRestoreSuccessful;

	private float mDownXValue;

	/** 
	 * when touching (ACTION_DOWN) move event is enabled.
	 * As soon as the user moves the finger enough in order to flip to next or previous
	 * flipper child, move is disabled to prevent subsequent unwanted flippings.
	 * Move event is enabled again at next touch.
	 */
	private boolean mDisableMove;
	
	private Settings mSettings;

	private boolean mSwipeHintEnabled;
	
	private FlipperChildChangeListener mFlipperChildChangeListener;
}
