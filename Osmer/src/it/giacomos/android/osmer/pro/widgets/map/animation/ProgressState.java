package it.giacomos.android.osmer.pro.widgets.map.animation;

import android.os.Handler;

public abstract class ProgressState extends State 
{
	protected int dTotSteps, dDownloadStep, dFrameNo, dTotalFrames;
	
	public ProgressState(RadarAnimation radarAnimation, AnimationTask at,
			State previousState) 
	{
		super(radarAnimation, at, previousState);
		
		/* if we are initialized from a progress state, take the step parameters */
		if(previousState != null && previousState.isProgressState())
		{
			ProgressState prevProgressState = (ProgressState) previousState;
			dTotSteps = prevProgressState.getTotSteps();
			dDownloadStep = prevProgressState.getDownloadStep();
			dFrameNo = prevProgressState.getFrameNo();
			dTotalFrames = prevProgressState.getTotalFrames();
		}
		else
		{
			dTotSteps = 0;
			dDownloadStep = 0;
			dFrameNo = 0;
			dTotalFrames = 0;
		}
	}
	
	public int getTotSteps()
	{
		return dTotSteps;
	}
	
	public int getDownloadStep()
	{
		return dDownloadStep;
	}

	public int getFrameNo()
	{
		return dFrameNo;
	}
	
	public int getTotalFrames()
	{
		return dTotalFrames;
	}
}
