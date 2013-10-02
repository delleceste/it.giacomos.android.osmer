package it.giacomos.android.osmer.pro.widgets.map.animation;

import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.pro.widgets.map.OMapFragment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ToggleButton;

public class Finished extends State {

	private int mTotSteps;

	Finished(RadarAnimation radarAnimation, AnimationTask at,
			State previousState) 
	{
		super(radarAnimation, at, previousState);
		if(previousState != null && previousState.getStatus() == RadarAnimationStatus.RUNNING)
			mTotSteps = ((Running) previousState).getTotSteps();
		else if(previousState.getStatus() == RadarAnimationStatus.NOT_RUNNING)
		{
			Log.e("Finished.Finished", "entering FINISHED from NOT_RUNNING: restoring? :-)");
		}
		else
		{
			Log.e("Finished.Finished", "error: can only get to FINISHED after RUNNING state");
		}
	}

	public int getTotSteps()
	{
		return mTotSteps;
	}

	@Override
	public RadarAnimationStatus getStatus() 
	{
		return RadarAnimationStatus.FINISHED;
	}

	@Override
	public void enter() 
	{
		/* show play and cancel buttons. Don't show the timestamp label */
		OMapFragment mapFrag = dRadarAnimation.getMapFragment();
		mapFrag.getActivity().findViewById(R.id.radarAnimTime).setVisibility(View.GONE);
		mapFrag.getActivity().findViewById(R.id.stopButton).setVisibility(View.VISIBLE);
		ToggleButton tb = (ToggleButton )mapFrag.getActivity().findViewById(R.id.playPauseButton);
		tb.setVisibility(View.VISIBLE);
		tb.setChecked(true);
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
