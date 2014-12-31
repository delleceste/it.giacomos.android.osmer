package it.giacomos.android.osmer.widgets.map.animation;

import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.widgets.map.OMapFragment;
import android.os.Handler;
import android.util.Log;
import android.view.View;

public class NotRunning extends State 
{
	public NotRunning(RadarAnimation radarAnimation, AnimationTask at, State previousState) 
	{
		/* removes previousState as callback, so first of all it stops the animation */
		super(radarAnimation, at, previousState);
	}

	@Override
	public RadarAnimationStatus getStatus() 
	{
		return RadarAnimationStatus.NOT_RUNNING;
	}

	@Override
	public void enter() 
	{
		/* cancels the current task */
		if(dAnimationTask!= null && !dAnimationTask.isCancelled())
		{
			Log.e("NotRunning.enter", "cancelling animation task");
		}
		else if(dAnimationTask != null)
			Log.e("NotRunning.enter", "not cancelling task: its state is " + dAnimationTask.getStatus());
	}

	public void leave()
	{
	}

	@Override
	public boolean isRunnable() 
	{
		return false;
	}

	@Override
	public boolean isProgressState() 
	{
		return false;
	}
}
