package it.giacomos.android.osmer.pro.widgets.map.animation;

import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.pro.widgets.map.OMapFragment;
import android.os.Handler;
import android.util.Log;
import android.widget.ToggleButton;

/** This class represents the PAUSED state.
 * 
 * The PAUSED state can be reached from a previousState equal to RUNNING.
 
 * The PAUSED state can transition to the RUNNING state (touching again play after pause) or 
 * to the NOT_RUNNING state (touching the cancel button).
 * 
 * @author giacomo
 *
 */
public class Paused extends ProgressState 
{	
	Paused(RadarAnimation radarAnimation, AnimationTask animationTask, State previousState) 
	{		
		/* removes callbacks on handler (previous state run will not be invoked any more */
		super(radarAnimation, animationTask, previousState);
		if(previousState.getStatus() == RadarAnimationStatus.RUNNING)
		{
			/* before leaving RUNNING for PAUSED, the number of frames is 
			 * incremented by one. The last shown frame number is frameNo - 1
			 */
			dFrameNo--;
		}
	}
	
	@Override
	public RadarAnimationStatus getStatus() 
	{
		return RadarAnimationStatus.PAUSED;
	}

	@Override
	public void enter() 
	{
		if(dPreviousState != null && dPreviousState.getStatus() != RadarAnimationStatus.RUNNING)
			Log.e("Paused.enter", "Error: PAUSED state can be entered only from RUNNING state");
		else
		{
			/* - pause does not cancel the download task.
			 * - pause can be entered only from the RUNNING state.
			 * 
			 * It just pauses the animation.
			 * This is done in the constructor by the super() call, which removes 
			 * callbacks from the handler, thus pausing the animation.
			 * What we have to do is to change the pause control to the play one.
			 */
			OMapFragment mapFrag = dRadarAnimation.getMapFragment();
			ToggleButton tb = (ToggleButton )mapFrag.getActivity().findViewById(R.id.playPauseButton);
			tb.setChecked(true);
		}
	}

	public void leave() 
	{

	}

	@Override
	public boolean isRunnable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isProgressState() 
	{
		return true;
	}
}