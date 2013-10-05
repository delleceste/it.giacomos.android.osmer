package it.giacomos.android.osmer.pro.widgets.map.animation;

import android.os.Handler;

public abstract class ProgressState extends State 
{
	protected int dTotSteps, dDownloadStep, dFrameNo, dTotalFrames, dPauseOnFrameNo;
	
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
			dPauseOnFrameNo = prevProgressState.getPauseOnFrameNo();
		}
		else
		{
			dTotSteps = 0;
			dDownloadStep = 0;
			dFrameNo = 0;
			dTotalFrames = 0;
			dPauseOnFrameNo = -1;
		}
	}
	
	public int getPauseOnFrameNo() 
	{
		return dPauseOnFrameNo;
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
