package it.giacomos.android.osmer.widgets.map.animation;

public abstract class ProgressState extends State 
{
	protected int dTotSteps, dDownloadStep, dFrameNo, dPauseOnFrameNo;
	
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
			dPauseOnFrameNo = prevProgressState.getPauseOnFrameNo();
		}
		else
		{
			dTotSteps = 0;
			dDownloadStep = 0;
			dFrameNo = 0;
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

	public void setFrameNo(int frameNo)
	{
		dFrameNo = frameNo;
	}
	
	public int getFrameNo()
	{
		return dFrameNo;
	}
	
	public int getTotalFrames()
	{
		if(dTotSteps > 0)
			return dTotSteps - 1;
		else
			return 0;
	}
}
