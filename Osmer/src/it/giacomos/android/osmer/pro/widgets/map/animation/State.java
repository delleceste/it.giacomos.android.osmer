package it.giacomos.android.osmer.pro.widgets.map.animation;

import android.os.Handler;
import android.util.Log;

/** Creates a new State with a radarAnimation, an animation task and a handler which
 * must be passed through subsequent states.
 * 
 * By default, the constructor of the State unschedules the previous state from the 
 * handler
 * 
 * @author giacomo
 *
 */
public abstract class State
{
	State(RadarAnimation radarAnimation, AnimationTask at, State previousState) 
	{
		dRadarAnimation = radarAnimation;
		dAnimationTask = at;
		dPreviousState = previousState;
		
		if(previousState != null && previousState.getStatus() == RadarAnimationStatus.RUNNING)
		{
			Running running = (Running) previousState;
			Log.e("State.State", "removing callbacks on handler " + running.getHandler() + " for runnable status " + previousState.getStatus());
			running.getHandler().removeCallbacks(running);
		}
		if(dAnimationTask != null)
			dAnimationTask.setAnimationTaskListener(null);
	}

	public abstract RadarAnimationStatus getStatus();
	
	public abstract void enter();
		
	public boolean animationInProgress()
	{
		return getStatus() == RadarAnimationStatus.BUFFERING || getStatus() == RadarAnimationStatus.RUNNING
				|| getStatus() == RadarAnimationStatus.PAUSED  || getStatus() == RadarAnimationStatus.INTERRUPTED;
	}
	
	public State getPreviousState() 
	{
		return dPreviousState;
	}
	
	public AnimationTask getAnimationTask()
	{
		return dAnimationTask;
	}
	
	public abstract boolean isRunnable();
	
	public abstract  boolean isProgressState();
		
	protected RadarAnimation dRadarAnimation;
	
	protected AnimationTask dAnimationTask;
	
	protected RadarAnimationStatus dAnimationStatus;
		
	protected State dPreviousState;
}
