package it.giacomos.android.osmer.PROva.widgets.map.animation;

import it.giacomos.android.osmer.PROva.R;
import it.giacomos.android.osmer.PROva.widgets.map.OMapFragment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
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
public class Paused extends ProgressState implements AnimationTaskListener 
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
			dAnimationTask.setAnimationTaskListener(this);
			/* - pause does not cancel the download task.
			 * - pause can be entered only from the RUNNING state, either when paused by the
			 *   user or when the animation finishes
			 * 
			 * It just pauses the animation.
			 * This is done in the constructor by the super() call, which removes 
			 * callbacks from the handler, thus pausing the animation.
			 * What we have to do is to change the pause control to the play one.
			 */
			OMapFragment mapFrag = dRadarAnimation.getMapFragment();
			mapFrag.getActivity().findViewById(R.id.animationButtonsLinearLayout).setVisibility(View.VISIBLE);
//			mapFrag.getActivity().findViewById(R.id.animationTimestampLinearLayout).setVisibility(View.VISIBLE);
			mapFrag.getActivity().findViewById(R.id.radarAnimTime).setVisibility(View.VISIBLE);
//			mapFrag.getActivity().findViewById(R.id.radarAnimTimestampImageView).setVisibility(View.VISIBLE);
			mapFrag.getActivity().findViewById(R.id.nextButton).setVisibility(View.VISIBLE);
			mapFrag.getActivity().findViewById(R.id.previousButton).setVisibility(View.VISIBLE);
			ToggleButton tb = (ToggleButton )mapFrag.getActivity().findViewById(R.id.playPauseButton);
			tb.setChecked(true);
		}
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
		return true;
	}

	@Override
	public void onProgressUpdate(int step, int total) 
	{
		dTotSteps = total;
		dDownloadStep = step;
	}

	@Override
	public void onDownloadComplete() 
	{
		
	}

	@Override
	public void onDownloadError(String message) 
	{
		dRadarAnimation.onError(message);
		Log.e("Paused.onDownloadError", "cancelling task, going to NOT_RUNNING");		
	}

	@Override
	public void onUrlsReady(String urlList) 
	{
		
	}

	@Override
	public void onTaskCancelled() 
	{
		
	}
}
