package it.giacomos.android.osmer.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import it.giacomos.android.osmer.R;

public class AnimatedImageView extends ImageView {

	public AnimatedImageView(Context context) 
	{
		super(context);
		mErrorFlag = false;
		this.setVisibility(View.GONE);
	}

	public AnimatedImageView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		mErrorFlag = false;
		this.setVisibility(View.GONE);
	}

	public void hide()
	{
		if(!mErrorFlag)
		{
			this.clearAnimation();
			this.setImageResource(0);
			this.setVisibility(View.GONE);
		}
	}

	public void displayError()
	{
		mErrorFlag = true;
		this.clearAnimation();
		this.setImageResource(R.drawable.ic_dialog_alert);
	}

	public void resetErrorFlag()
	{
		mErrorFlag = false;
	}

	public void start()
	{
		if(this.getAnimation() == null && !mErrorFlag)
		{
//			Log.e("start() in AnimatedImageView", "starting animation............... " + toString());
			this.setVisibility(View.VISIBLE);
			this.setImageResource(R.drawable.spinner_20_inner_holo);
		//	Animation anim = AnimationUtils.loadAnimation(getContext(), R.drawable.animated_refresh_actionbar_image);
		//	startAnimation(anim);
		}
	}

	private boolean mErrorFlag;
}
