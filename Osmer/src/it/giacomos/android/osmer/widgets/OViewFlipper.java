package it.giacomos.android.osmer.widgets;

import it.giacomos.android.osmer.FlipperChildChangeListener;
import it.giacomos.android.osmer.FlipperChildren;
import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.guiHelpers.TitlebarUpdater;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;
import it.giacomos.android.osmer.preferences.*;

public class OViewFlipper extends ViewFlipper implements OnTouchListener
{
	public OViewFlipper(Context context, AttributeSet attrs) {
		super(context, attrs);	
		mRestoreSuccessful = false;
		mDisableMove = false;
		mSwipeHintEnabled = false;
		mSettings = new Settings(context);
		/* problems in editor with shared preferences */
		if(!this.isInEditMode())
			mSwipeHintEnabled = mSettings.isSwipeHintEnabled();
	}

	public void setDisplayedChild(int child, boolean notifyListener)
	{
		super.setDisplayedChild(child);
		if(notifyListener)
			mFlipperChildChangeListener.onFlipperChildChangeEvent(child);
	}

	public void setOnChildPageChangedListener(FlipperChildChangeListener l)
	{
		mFlipperChildChangeListener = l;
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

		if(getDisplayedChild() == FlipperChildren.MAP || mDrawerVisible)
			return false;

		switch (touchevent.getAction())
		{
		case MotionEvent.ACTION_DOWN:
		{
			float xPos = touchevent.getX();
			/* allow flipping without clashing with application drawer, which 
			 * is shown when swiping from the left edge.
			 */
			if(xPos > getWidth() / 5)
			{
				// store the X value when the user's finger was pressed down
				mDownXValue = touchevent.getX();
				mDisableMove = false;
			}
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
				// going backwards: pushing stuff to the right
				if (mDownXValue - currentX < -30)
				{    	
					// Set the animation
					setOutAnimation(AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.slide_right));
					setInAnimation(AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.slide_right));
					
					// Flip!
					if(getDisplayedChild() > 0)
						showPrevious();
					else /* from situation to 2 days */
						setDisplayedChild(getChildCount() - 2);
					
					mFlipperChildChangeListener.onFlipperChildChangeEvent(this.getDisplayedChild());
					/* upon flipping, disable further unwanted flippings (until next ACTION_DOWN) */
					mDisableMove = true;
				}

				// going forwards: pushing stuff to the left
				else if (mDownXValue - currentX > 30)
				{
					// Set the animation
					setOutAnimation(AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.slide_left));
					setInAnimation(AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.slide_left));
					// Flip!
					if(getDisplayedChild() < getChildCount() - 2)
						showNext();
					else /* from 2 days to situation */
						setDisplayedChild(0);
					mFlipperChildChangeListener.onFlipperChildChangeEvent(this.getDisplayedChild());
					/* upon flipping, disable further unwanted flippings  (until next ACTION_DOWN) */
					mDisableMove = true;
				}
				
				TitlebarUpdater tbu = new TitlebarUpdater();
				tbu.update((OsmerActivity) this.getContext());
				tbu = null;
				
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
	
	public void setDrawerVisible(boolean v)
	{
		mDrawerVisible = v;
	}

	public boolean drawerVisible()
	{
		return mDrawerVisible;
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
	
	private boolean mDrawerVisible;
}