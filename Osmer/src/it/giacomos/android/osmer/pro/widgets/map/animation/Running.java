package it.giacomos.android.osmer.pro.widgets.map.animation;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import it.giacomos.android.osmer.PROva.R;
import it.giacomos.android.osmer.pro.network.DownloadStatus;
import it.giacomos.android.osmer.pro.widgets.map.OMapFragment;

/** This state listens for AnimationTask progress updates and completes
 * the job started by Buffering.
 * 
 * The task is cancelled only if cancel() is invoked on the Running state.
 * From the running state we can go to
 * 
 * - cancel (the async task is cancelled)
 * 
 * - pause the async task is not cancelled (thus going on saving images on the
 *         external storage until the last image is saved).
 *         By passing to the Paused state the callback on the handler is 
 *         of course removed, so no more image updates take place on the 
 *         google map.
 *         I strike the fact that the AnimationTask is not cancelled when
 *         switching out from the Running state, unless cancel() is invoked.
 *         
 *  To the Running state we can get from Buffering and Paused.
 *  
 *  If the animation task is still running, 'this' is set as animation task
 *  listener, thus receiving progress updates from the moment of the state
 *  switch onwards.
 * 
 * @author giacomo
 *
 */
public class Running extends ProgressState implements AnimationTaskListener, Runnable
{	
	/* holds whether the Running state automatically switches to pause after notifying the
	 * RadarAnimation that the desired frame is ready to be used (mPauseOnFrameNo >= 0).
	 * This only happens after a state restore (screen rotation or app gone in background).
	 * Normally, this value is set to -1 (Running state persists until finishes or explicitly
	 * put in pause or cancelled).
	 */
	private int mPauseOnFrameNo;
	private int mAnimationStepDuration;
	private Handler mTimeoutHandler;
	
	Running(RadarAnimation radarAnimation, 
			AnimationTask animationTask,
			State previousState) 
	{
		/* if previousState is ProgressState, fetches the frameNo, the tot frames, the download step */
		super(radarAnimation, animationTask, previousState);
		
		Log.e("Running.Running", "previous state was " + previousState.getStatus());
		if(previousState.getStatus() == RadarAnimationStatus.BUFFERING)
		{
			Buffering bu = (Buffering) previousState;
			mPauseOnFrameNo = bu.getPauseOnFrameNo();
		}
		else if(previousState.getStatus() == RadarAnimationStatus.PAUSED)
		{
			Paused pa = (Paused) previousState;
			Log.e("Running.Running", "previous state was PAUSED, tot steps " + dTotSteps + " dFrameNo " + dFrameNo + " download step " + dDownloadStep);
			mPauseOnFrameNo = -1;
		}
//		else if(previousState.getStatus() == RadarAnimationStatus.FINISHED)
//		{
//			dFrameNo = 0;
//			dDownloadStep = 0;
//			dTotSteps = ((Finished) previousState).getTotSteps();
//			mPauseOnFrameNo = -1;
//		}
		else
			Log.e("Running.Running", "error: can only get to RUNNING from BUFFERING, PAUSED or FINISHED states");
		
		mAnimationStepDuration = 1000;
	}

	public int getDownloadStep()
	{
		return dDownloadStep;
	}
	
	public int getTotSteps()
	{
		return dTotSteps;
	}
	
	public int getFrameNo()
	{
		return dFrameNo;
	}
	
	/** Returns the value of the only frame that is posted to the animation listener.
	 * This value is initialized in the constructor and its value is taken from the 
	 * Buffering previous state, if Buffering was the previous state. Otherwise, it is
	 * initialized to -1, meaning that the animation must not be put in pause after posting the
	 * mPauseOnFrameNo frame.
	 * 
	 * @return the frame at which the state will migrate to pause.
	 */
	public int getPauseOnFrameNo()
	{
		return mPauseOnFrameNo;
	}
	
	@Override
	public RadarAnimationStatus getStatus() 
	{
		return RadarAnimationStatus.RUNNING;
	}
	
	@Override
	public void enter() 
	{
		Log.e("Running.enter",  this + "entering RUNNING state");
		hideProgressBar();
		/* show controls */
		OMapFragment mapFrag = dRadarAnimation.getMapFragment();
		mapFrag.getActivity().findViewById(R.id.animationButtonsLinearLayout).setVisibility(View.VISIBLE);
//		mapFrag.getActivity().findViewById(R.id.animationTimestampLinearLayout).setVisibility(View.VISIBLE);
		mapFrag.getActivity().findViewById(R.id.radarAnimTime).setVisibility(View.VISIBLE);
//		mapFrag.getActivity().findViewById(R.id.radarAnimTimestampImageView).setVisibility(View.VISIBLE);
		mapFrag.getActivity().findViewById(R.id.stopButton).setVisibility(View.VISIBLE);
		mapFrag.getActivity().findViewById(R.id.playPauseButton).setVisibility(View.VISIBLE);
		mapFrag.getActivity().findViewById(R.id.radarAnimProgressBar).setVisibility(View.GONE);
		mapFrag.getActivity().findViewById(R.id.nextButton).setVisibility(View.GONE);
		mapFrag.getActivity().findViewById(R.id.previousButton).setVisibility(View.GONE);
		dAnimationTask.setAnimationTaskListener(this);
		mTimeoutHandler = new Handler();
		mTimeoutHandler.postDelayed(this, 250);
	}

	public void cancel()
	{
		Log.e("Running.cancel", "migrating to not running. Cancelling tasks, remove callbacks on Handler");
		mTimeoutHandler.removeCallbacks(this);
		dAnimationTask.cancel(false);
		dRadarAnimation.onTransition(RadarAnimationStatus.NOT_RUNNING);
	}

	@Override
	public void onProgressUpdate(int step, int total) 
	{
		dDownloadStep = step;
		dTotSteps = total;
		dRadarAnimation.onDownloadProgressChanged(step, total);
	}

	@Override
	public void onDownloadComplete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDownloadError(String message) 
	{
		dRadarAnimation.onError(message);
		Log.e("Running.onDownloadError", "cancelling task, going to NOT_RUNNING");
		cancel();
	}

	@Override
	public void onUrlsReady(String urlList) 
	{
		
	}

	@Override
	public void onTaskCancelled() 
	{	
		
	}

	public void leave() 
	{
		mTimeoutHandler.removeCallbacks(this);
		if(dFrameNo >= dTotSteps)
		{
			Log.e("Running.leave", "frame no == total frames: migrating to PAUSED");
			dRadarAnimation.onTransition(RadarAnimationStatus.PAUSED);
		}
		else if(dDownloadStep <= dFrameNo)
		{
			Log.e("Running.leave", this + "dDownloadStep <= dFrameNo: " + dDownloadStep + " <= " + dFrameNo + " going to buffering");
			dRadarAnimation.onTransition(RadarAnimationStatus.BUFFERING);
		}
		else if(mPauseOnFrameNo >= 0)
		{
			Log.e("Running.leave", "paused on frame no " + mPauseOnFrameNo + ": migrating to PAUSED");
			dRadarAnimation.onTransition(RadarAnimationStatus.PAUSED);
		}
		else 
		{
			Log.e("Running.leave", "leave method incorrectly called... dunnow what to do: cancelling");
			cancel();
		}
	}
	
	@Override
	public void run() 
	{
		Log.e("Running.run", "dDownloadStep " + dDownloadStep + " mPauseOnFrameNo " + mPauseOnFrameNo 
				+ " dFrameNo " + dFrameNo);
		if(dDownloadStep > dFrameNo)
		{
			OMapFragment mapFrag = dRadarAnimation.getMapFragment();
			 /* show "pause" button */
			ToggleButton tb = (ToggleButton )mapFrag.getActivity().findViewById(R.id.playPauseButton);
			tb.setChecked(false);
			
			/* hide progress bar as soon as we start animating */
			hideProgressBar();
			
			/* show the pause button and the time label */
			showControls();
			
			Log.e("Running.run", "pause on frame no " + mPauseOnFrameNo + " frame no " + dFrameNo);
			
			if(mPauseOnFrameNo < 0)
			{
				/* get image from the external storage and update */
				dRadarAnimation.onFrameUpdatePossible(dFrameNo);
			}
			else if(dFrameNo == mPauseOnFrameNo)
			{
				/* the current frame is the one desired before pausing (restore 
				 * mode): post the update of dFrameNo only.
				 * 
				 * This state continues "running" until the condition below
				 * `if(dFrameNo >= dTotalFrames)' is reached because in the Running 
				 * state the AnimationTask is cancelled only if cancel() is 
				 * invoked.
				 */
				dRadarAnimation.onFrameUpdatePossible(mPauseOnFrameNo);
			}
			/* increment the number of updated frames */
			dFrameNo++;
		}
		else /* not enough data! */
		{
			if(dDownloadStep > 1)
			{
				Log.e("Running.run", "posting an update possible at dwnlod step " + dDownloadStep +  " and dFrameNO " + dFrameNo);
				dRadarAnimation.onFrameUpdatePossible(dDownloadStep);
			}
			leave(); /* back to buffering */
			return;
		}
		
		if(dFrameNo - 1 == mPauseOnFrameNo)
		{
			/* dFrameNo has been incremented by one also in the case dFrameNo == mPauseOnFrameNo */
			Log.e("Running.run", "Leaving cuz dFrameNo - 1 == mPauseOnFrameNo");
			leave();
		}
		else if(dFrameNo >= dTotSteps) /* end */
		{
			Log.e("Running.run()", "not rescheduling execution: frame no " + dFrameNo + " anim size " + (dTotSteps - 1));
			leave();
		}
		else if(mPauseOnFrameNo < 0) /* reschedule */
		{
			mTimeoutHandler.postDelayed(this, this.mAnimationStepDuration);
		}
		else  if(dFrameNo < mPauseOnFrameNo)
		{
			Log.e("Running.run", "waiting for dFrameNo " + dFrameNo + " to become == mPauseOnFrameNo: " + mPauseOnFrameNo);
			/* if mPauseOnFrameNo is set to >= 0, then rapidly check for (dDownloadStep > dFrameNo)
			 * in order to call dRadarAnimation.onFrameUpdatePossible(mPauseOnFrameNo) as soon as
			 * possible. Check in 150ms.
			 */
			mTimeoutHandler.postDelayed(this, 150);
		}
	}

	public void hideProgressBar()
	{
		OMapFragment mapFrag = dRadarAnimation.getMapFragment();
		ProgressBar pb = (ProgressBar) mapFrag.getActivity().findViewById(R.id.radarAnimProgressBar);
		pb.setVisibility(View.GONE);
	}


	public void showProgressBar()
	{
		OMapFragment mapFrag = dRadarAnimation.getMapFragment();
		ProgressBar pb = (ProgressBar) mapFrag.getActivity().findViewById(R.id.radarAnimProgressBar);
		pb.setVisibility(View.VISIBLE);
	}
	

	public void showControls()
	{
		OMapFragment mapFrag = dRadarAnimation.getMapFragment();
		mapFrag.getActivity().findViewById(R.id.radarAnimTime).setVisibility(View.VISIBLE);
		mapFrag.getActivity().findViewById(R.id.stopButton).setVisibility(View.VISIBLE);
		mapFrag.getActivity().findViewById(R.id.playPauseButton).setVisibility(View.VISIBLE);
	}

	@Override
	public boolean isRunnable() 
	{
		return true;
	}

	@Override
	public boolean isProgressState() 
	{
		return true;
	}

	public Handler getHandler() 
	{
		return mTimeoutHandler;
	}
	
}
