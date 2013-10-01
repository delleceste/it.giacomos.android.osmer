package it.giacomos.android.osmer.pro.widgets.map.animation;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import it.giacomos.android.osmer.R;
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
public class Running extends State implements AnimationTaskListener
{

	private int mDownloadStep, mTotSteps, mFrameNo, mTotalFrames;
	
	/* holds whether the Running state automatically switches to pause after notifying the
	 * RadarAnimation that the desired frame is ready to be used (mPauseOnFrameNo >= 0).
	 * This only happens after a state restore (screen rotation or app gone in background).
	 * Normally, this value is set to -1 (Running state persists until finishes or explicitly
	 * put in pause or cancelled).
	 */
	private int mPauseOnFrameNo;
	private int mAnimationStepDuration;
	
	Running(RadarAnimation radarAnimation, 
			AnimationTask animationTask, 
			Handler handler,
			State previousState) 
	{
		/* removes callbacks on handler (previous state run will not be invoked any more */
		super(radarAnimation, animationTask, handler, previousState);
		
		Log.e("Running.Running", "previous state was " + previousState.getStatus());
		if(previousState.getStatus() == RadarAnimationStatus.BUFFERING)
		{
			Buffering bu = (Buffering) previousState;
			mDownloadStep = bu.getCurrentStep();
			mTotSteps = bu.getTotSteps();
			mPauseOnFrameNo = bu.getPauseOnFrameNo();
			mTotalFrames = mTotSteps - 1;
		}
		else if(previousState.getStatus() == RadarAnimationStatus.PAUSED)
		{
			Paused pa = (Paused) previousState;
			mDownloadStep = pa.getDownloadStep();
			mTotSteps = pa.getTotSteps();
			mFrameNo = pa.getFrameNo();
			Log.e("Running.Running", "previous state was PAUSED, tot steps " + mTotSteps + " mFrameNo " + mFrameNo + " download step " + mDownloadStep);
			mPauseOnFrameNo = -1;
			mTotalFrames = mTotSteps - 1;
		}
		else if(previousState.getStatus() == RadarAnimationStatus.FINISHED)
		{
			mFrameNo = 0;
			mDownloadStep = 0;
			mTotSteps = ((Finished) previousState).getTotSteps();
			mTotalFrames = mTotSteps - 1;
			mPauseOnFrameNo = -1;
		}
		else
			Log.e("Running.Running", "error: can only get to RUNNING from BUFFERING, PAUSED or FINISHED states");
		
		mAnimationStepDuration = 1000;
		mPauseOnFrameNo = -1;
	}

	public int getDownloadStep()
	{
		return mDownloadStep;
	}
	
	public int getTotSteps()
	{
		return mTotSteps;
	}
	
	public int getFrameNo()
	{
		return mFrameNo;
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
		hideProgressBar();
		/* show controls */
		OMapFragment mapFrag = dRadarAnimation.getMapFragment();
		mapFrag.getActivity().findViewById(R.id.radarAnimTime).setVisibility(View.VISIBLE);
		mapFrag.getActivity().findViewById(R.id.stopButton).setVisibility(View.VISIBLE);
		mapFrag.getActivity().findViewById(R.id.playPauseButton).setVisibility(View.VISIBLE);
		mapFrag.getActivity().findViewById(R.id.radarAnimProgressBar).setVisibility(View.GONE);
		dAnimationTask.setAnimationTaskListener(this);
		dTimeoutHandler.postDelayed(this, 250);
	}

	private void mCancel()
	{
		Log.e("Running.cancel", "migrating to not running. Cancelling tasks, remove callbacks on Handler");
		dTimeoutHandler.removeCallbacks(this);
		dAnimationTask.cancel(false);
		dRadarAnimation.onTransition(RadarAnimationStatus.NOT_RUNNING);
	}
	
	public void leave() 
	{
		dTimeoutHandler.removeCallbacks(this);
		
		if(mPauseOnFrameNo > 0)
		{
			Log.e("Running.leave", "paused on frame no " + mPauseOnFrameNo + ": migrating to PAUSED");
			dRadarAnimation.onTransition(RadarAnimationStatus.PAUSED);
		}
		else if(mFrameNo == mTotalFrames)
		{
			Log.e("Running.leave", "frame no == total frames: migrating to FINISHED");
			dRadarAnimation.onTransition(RadarAnimationStatus.FINISHED);
		}
		else if(mDownloadStep <= mFrameNo)
		{
			Log.e("Running.leave", "mDownloadStep <= mFrameNo: " + mDownloadStep + " <= " + mFrameNo + " going to buffering");
			dRadarAnimation.onTransition(RadarAnimationStatus.BUFFERING);
		}
		else
		{
			Log.e("Running.leave", "leave method incorrectly called... dunnow what to do: cancelling");
			mCancel();
		}
	}

	@Override
	public void onProgressUpdate(int step, int total) 
	{
		mDownloadStep = step;
		mTotSteps = total;
		mTotalFrames = mTotSteps - 1;
		
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
		mCancel();
	}

	@Override
	public void onUrlsReady(String urlList) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTaskCancelled() 
	{	
		
	}

	@Override
	public void run() 
	{
		if(mDownloadStep > mFrameNo)
		{
			OMapFragment mapFrag = dRadarAnimation.getMapFragment();
			 /* show "pause" button */
			ToggleButton tb = (ToggleButton )mapFrag.getActivity().findViewById(R.id.playPauseButton);
			tb.setChecked(false);
			
			/* hide progress bar as soon as we start animating */
			hideProgressBar();
			
			/* show the pause button and the time label */
			showControls();
			
			if(mPauseOnFrameNo < 0)
			{
				/* get image from the external storage and update */
				dRadarAnimation.onFrameUpdatePossible(mFrameNo);
			}
			else if(mFrameNo == mPauseOnFrameNo)
			{
				/* the current frame is the one desired before pausing (restore 
				 * mode): post the update of mFrameNo only.
				 * 
				 * This state continues "running" until the condition below
				 * `if(mFrameNo >= mTotalFrames)' is reached because in the Running 
				 * state the AnimationTask is cancelled only if cancel() is 
				 * invoked.
				 */
				dRadarAnimation.onFrameUpdatePossible(mPauseOnFrameNo);
			}
			/* increment the number of updated frames */
			mFrameNo++;
		}
		else /* not enough data! */
		{
			leave(); /* back to buffering */
		}

		if(mFrameNo >= mTotalFrames) /* end */
		{
			Log.e("Running.run()", "not rescheduling execution: frame no " + mFrameNo + " anim size " + mTotalFrames);
			leave();
		}
		else if(mPauseOnFrameNo < 0) /* reschedule */
		{
			dTimeoutHandler.postDelayed(this, this.mAnimationStepDuration);
		}
		else 
		{
			/* if mPauseOnFrameNo is set to >= 0, then rapidly check for (mDownloadStep > mFrameNo)
			 * in order to call dRadarAnimation.onFrameUpdatePossible(mPauseOnFrameNo) as soon as
			 * possible. Check in 150ms.
			 */
			dTimeoutHandler.postDelayed(this, 150);
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
	
}
