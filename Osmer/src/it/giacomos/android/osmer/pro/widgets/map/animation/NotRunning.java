package it.giacomos.android.osmer.pro.widgets.map.animation;

import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.pro.widgets.map.OMapFragment;
import android.os.Handler;
import android.view.View;

public class NotRunning extends State 
{
	public NotRunning(RadarAnimation radarAnimation, AnimationTask at, Handler handler, State previousState) 
	{
		/* removes previousState as callback, so first of all it stops the animation */
		super(radarAnimation, at, handler, previousState);
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
			dAnimationTask.cancel(false);
		
		/* hide all animation controls */
		OMapFragment mapFrag = dRadarAnimation.getMapFragment();
		mapFrag.getActivity().findViewById(R.id.radarAnimTime).setVisibility(View.GONE);
		mapFrag.getActivity().findViewById(R.id.stopButton).setVisibility(View.GONE);
		mapFrag.getActivity().findViewById(R.id.playPauseButton).setVisibility(View.GONE);
		mapFrag.getActivity().findViewById(R.id.radarAnimProgressBar).setVisibility(View.GONE);
	}

	public void leave()
	{
//		dRadarAnimation.onTransition(RadarAnimationStatus.NOT_RUNNING);
	}

	@Override
	public void run() 
	{
		
	}
}
