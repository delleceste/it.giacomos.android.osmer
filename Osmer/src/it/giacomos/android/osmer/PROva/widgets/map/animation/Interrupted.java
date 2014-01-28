package it.giacomos.android.osmer.PROva.widgets.map.animation;

import it.giacomos.android.osmer.PROva.network.DownloadStatus;
import android.util.Log;

public class Interrupted extends ProgressState 
{
	private String mUrlList;
	
	Interrupted(RadarAnimation radarAnimation, AnimationTask at,
			 State previousState, String urlList) 
	{
		
		super(radarAnimation, at, previousState);
				
		mUrlList = urlList;
		if(previousState.getStatus() == RadarAnimationStatus.RUNNING && dFrameNo > 0)
			dFrameNo--;
	}
	
	Interrupted(RadarAnimation radarAnimation, 
			int savedFrameNo, 
			int savedDownloadStep,
			String urlList)
	{
		super(radarAnimation, null, null);
		dDownloadStep = savedDownloadStep;
		dFrameNo = savedFrameNo;
		mUrlList = urlList;
	}

	@Override
	public RadarAnimationStatus getStatus() {
		return RadarAnimationStatus.INTERRUPTED;
	}

	@Override
	public void enter() 
	{
		if(dAnimationTask != null && !dAnimationTask.isCancelled())
		{
			dAnimationTask.cancel(false);
		}
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

	public String getUrlList() 
	{
		return mUrlList;
	}

}
