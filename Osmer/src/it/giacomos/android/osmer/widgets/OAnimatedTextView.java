package it.giacomos.android.osmer.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.TextView;



public class OAnimatedTextView extends TextView implements AnimationListener
{
	public final long TIMEOUT = 1500L;
	
	public OAnimatedTextView(Context context, AttributeSet set) 
	{
		super(context, set);
	}
	
	public void scheduleShow()
	{
		postDelayed(new ShowDelayedAnimatedTextViewRunnable(this), TIMEOUT);
	}
	
	public void hide()
	{
		setVisibility(View.GONE);
	}

	public void animateHide() 
	{
		TranslateAnimation translation = new TranslateAnimation(0, 0, 0, 100);
		translation.setDuration(1000);
		translation.setAnimationListener(this);
		this.startAnimation(translation);
	}

	@Override
	public void onAnimationEnd(Animation animation) 
	{
		this.setVisibility(View.GONE);
	}

	@Override
	public void onAnimationRepeat(Animation animation) 
	{
		
	}

	@Override
	public void onAnimationStart(Animation animation) 
	{
		
	}

	public boolean animationHasStarted() 
	{
		boolean hasStarted = getAnimation() != null && getAnimation().hasStarted();
		return hasStarted;
	}

}
