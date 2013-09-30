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
public abstract class State implements Runnable
{
	State(RadarAnimation radarAnimation, AnimationTask at, Handler handler, State previousState) 
	{
		dRadarAnimation = radarAnimation;
		dAnimationTask = at;
		dTimeoutHandler = handler;
		
		if(previousState != null && handler != null)
		{
			Log.e("State.State", "removing callbacks on handler " + handler + " for runnable " + previousState.getPreviousState());
			handler.removeCallbacks(previousState);
		}
	}

	public abstract RadarAnimationStatus getStatus();
	
	public abstract void enter();
	
	public abstract void cancel();
	
	public State getPreviousState() 
	{
		return dPreviousState;
	}
		
	protected RadarAnimation dRadarAnimation;
	
	protected AnimationTask dAnimationTask;
	
	protected RadarAnimationStatus dAnimationStatus;
	
	protected Handler dTimeoutHandler;
	
	protected State dPreviousState;
}
