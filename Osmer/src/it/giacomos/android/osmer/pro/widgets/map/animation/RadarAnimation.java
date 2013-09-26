package it.giacomos.android.osmer.pro.widgets.map.animation;

import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.pro.network.state.Urls;
import it.giacomos.android.osmer.pro.widgets.map.OMapFragment;
import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.view.View.OnClickListener;
import android.os.AsyncTask;

public class RadarAnimation implements OnClickListener, AnimationTaskListener, Runnable
{
	private OMapFragment mMapFrag;
	private RadarAnimationStatus mAnimationStatus;
	private ArrayList<RadarAnimationListener> mAnimationListeners;
	private int mFrameNo, mDownloadProgress;
	private String mUrlList;
	private AnimationTask mAnimationTask;
	private int animationStepDuration;
	private Handler mTimeoutHandler;
	private boolean mControlsVisible;

	private SparseArray<AnimationData> mAnimationData;

	public RadarAnimation(OMapFragment mapf)
	{
		mMapFrag = mapf;
		mAnimationStatus = RadarAnimationStatus.NOT_RUNNING;
		mFrameNo = mDownloadProgress = 0;
		/* button listeners */
		((ToggleButton) (mMapFrag.getActivity().findViewById(R.id.playPauseButton))).setOnClickListener(this);
		((ToggleButton) (mMapFrag.getActivity().findViewById(R.id.stopButton))).setOnClickListener(this);
		animationStepDuration = 1000;
		mControlsVisible = false;
		mAnimationListeners = new ArrayList<RadarAnimationListener>();
		mAnimationData = new SparseArray<AnimationData>();
		mTimeoutHandler = new Handler();
	}

	public void pause()
	{
		Log.e("RadarAnimation", "pause");
		/* change status */
		mAnimationStatus = RadarAnimationStatus.PAUSED;
		/* cancel scheduled run */
		mTimeoutHandler.removeCallbacks(this);

		for(RadarAnimationListener ral : mAnimationListeners)
			ral.onRadarAnimationPause();
	}

	/** called when the play/pause toggle button is clicked
	 * 
	 */
	public void resume() 
	{
		if(mAnimationStatus == RadarAnimationStatus.PAUSED)
		{
			mAnimationStatus = RadarAnimationStatus.RUNNING;
			mTimeoutHandler.postDelayed(this, 250);
		}
		else if(mAnimationStatus == RadarAnimationStatus.NOT_RUNNING)
		{
			start();
		}
		else
			Log.e("RadarAnimation.resume", "cannot resume animation from status " + mAnimationStatus);
	}

	/** Called to restore the animation when interrupted by a screen orientation change.
	 *  Any state variable has to be correctly initialized in order to start() perform the
	 *  correct resume. (mDownloadProgress, mUrlList, mFrameNo).
	 *  If restore is called right after restoreState, then it should correctly resume 
	 *  a previously interrupted animation.
	 * 
	 */
	public void restore()
	{
		if(mAnimationStatus == RadarAnimationStatus.RUNNING)
		{
			Log.e("RadarAnimation.restore", "status is running, so invoking start");
			start();
		}
		else if(mAnimationStatus == RadarAnimationStatus.PAUSED)
		{
			start(); /* start the thread to retrieve / complete its task */
			mAnimationStatus = RadarAnimationStatus.PAUSED;
			
		}
		/* animation may have been NOT_RUNNING but the controls may have been visible */
		if(mControlsVisible)
			showControls();
			
	}

	public void start()
	{
		Log.e("RadarAnimation", "start");
		mAnimationStatus = RadarAnimationStatus.RUNNING;

		/* show progress bar and stop button */
		setProgressBarValue(0, 10);
		mMapFrag.getActivity().findViewById(R.id.stopButton).setVisibility(View.VISIBLE);

		mAnimationTask = new AnimationTask(this, mMapFrag.getActivity().getApplicationContext().getExternalFilesDir(null).getPath());
		mAnimationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Urls().radarHistoricalFileListUrl());

		for(RadarAnimationListener ral : mAnimationListeners)
			ral.onRadarAnimationStart();
	}

	public void stop() 
	{
		Log.e("RadarAnimation", "stop");
		
		mAnimationStatus = RadarAnimationStatus.NOT_RUNNING;

		if(mAnimationTask != null && !mAnimationTask.isCancelled())
			mAnimationTask.cancel(false);

		/* cancel scheduled run */
		mTimeoutHandler.removeCallbacks(this);

		hideControls();
		hideProgressBar();
		/* reset counters and the list of image urls */
		mResetProgressVariables();

		for(RadarAnimationListener ral : mAnimationListeners)
			ral.onRadarAnimationStop();
	}

	private void mResetProgressVariables()
	{
		mUrlList = "";
		mDownloadProgress = 0;
		mFrameNo = 0;
	}
	
	public boolean animationInProgress()
	{
		return mAnimationStatus == RadarAnimationStatus.RUNNING ||
				mAnimationStatus == RadarAnimationStatus.PAUSED;
	}

	public RadarAnimationStatus getStatus()
	{
		return mAnimationStatus;
	}

	public boolean isRunning() 
	{
		return  mAnimationStatus == RadarAnimationStatus.RUNNING;
	}

	public void registerRadarAnimationListener(RadarAnimationListener ral)
	{
		mAnimationListeners.add(ral);
	}

	public void removeRadarAnimationListener(RadarAnimationListener ral)
	{
		mAnimationListeners.remove(ral);
	}

	public int getFrameNo()
	{
		return mFrameNo;
	}

	public int getDownloadProgress()
	{
		return mDownloadProgress;
	}

	public void saveState(Bundle outState) 
	{
		if(mAnimationStatus == RadarAnimationStatus.RUNNING)
			outState.putInt("animationStatus", 1);
		else if(mAnimationStatus == RadarAnimationStatus.NOT_RUNNING)
			outState.putInt("animationStatus", 0);
		else if(mAnimationStatus == RadarAnimationStatus.PAUSED)
			outState.putInt("animationStatus", 2);

		outState.putInt("animationDownloadProgress", mDownloadProgress);
		outState.putInt("animationFrameNo", mFrameNo);
		outState.putString("urlList", mUrlList);
		outState.putBoolean("controlsVisible", (mMapFrag.getActivity().findViewById(R.id.playPauseButton).getVisibility() == View.VISIBLE));
	}

	public void restoreState(Bundle savedInstanceState) 
	{
		mFrameNo = savedInstanceState.getInt("animationFrameNo", 0);
		mDownloadProgress = savedInstanceState.getInt("animationDownloadProgress", 0);
		mControlsVisible = savedInstanceState.getBoolean("controlsVisible");
		int state = savedInstanceState.getInt("animationStatus", 0);
		/* status initialized to NOT_RUNNING in the constructor. No need to check for
		 * state == 0
		 */
		if(state == 1)
			mAnimationStatus = RadarAnimationStatus.RUNNING;
		else if(state == 2)
			mAnimationStatus = RadarAnimationStatus.PAUSED;
	}
	
	public void onDestroy()
	{
		/* stop download task if running */
		if(mAnimationTask != null && mAnimationTask.getStatus() == AsyncTask.Status.RUNNING)
			mAnimationTask.cancel(false);
		
		mTimeoutHandler.removeCallbacks(this);
	}

	@Override
	public void onClick(View v) 
	{
		if(v.getId() == R.id.playPauseButton)
		{
			ToggleButton pp = (ToggleButton) v;
			if(!pp.isChecked())
				resume();
			else
				pause();

			//pp.setChecked(!pp.isChecked());
		}
		else if(v.getId() == R.id.stopButton)
			stop();
	}

	public void showControls()
	{
		mMapFrag.getActivity().findViewById(R.id.radarAnimTime).setVisibility(View.VISIBLE);
		mMapFrag.getActivity().findViewById(R.id.stopButton).setVisibility(View.VISIBLE);
		mMapFrag.getActivity().findViewById(R.id.playPauseButton).setVisibility(View.VISIBLE);
	}

	public void hideControls()
	{
		mMapFrag.getActivity().findViewById(R.id.radarAnimTime).setVisibility(View.GONE);
		mMapFrag.getActivity().findViewById(R.id.stopButton).setVisibility(View.GONE);
		mMapFrag.getActivity().findViewById(R.id.playPauseButton).setVisibility(View.GONE);
		mMapFrag.getActivity().findViewById(R.id.radarAnimProgressBar).setVisibility(View.GONE);
	}
	
	public void setButtonPlay()
	{
		ToggleButton tb = (ToggleButton )mMapFrag.getActivity().findViewById(R.id.playPauseButton);
		tb.setChecked(true);
	}

	public void setProgressBarValue(int step, int total)
	{
		ProgressBar pb = (ProgressBar) mMapFrag.getActivity().findViewById(R.id.radarAnimProgressBar);
		if(pb.getVisibility() == View.GONE && step < total)
			pb.setVisibility(View.VISIBLE);
		else if(step == total)
			pb.setVisibility(View.GONE);

		if(pb.getMax() != total)
			pb.setMax(total);
		pb.setProgress(step);
	}

	public void hideProgressBar()
	{
		ProgressBar pb = (ProgressBar) mMapFrag.getActivity().findViewById(R.id.radarAnimProgressBar);
		pb.setVisibility(View.GONE);
	}

	@Override
	public void onProgressUpdate(int step, int total) 
	{
		setProgressBarValue(step, total);
		mDownloadProgress = step;

		/* first step: the file with the lines coupling timestamp/radar filename is ready.
		 * Parse it and populate mAnimationData.
		 */
		if(step == 1) /* download urls ready */
			mBuildAnimationData(mAnimationTask.getDownloadUrls());

		/* if step > 1 mAnimationData is filled */

		/* wait until the 40% of the images has been downloaded */
		if(step > 1 && step == Math.round(0.4f * (float) total))
		{
			/* can start animating */
			mStartAnimation();
		}
		else
			setProgressBarValue(step, total);


	}

	@Override
	public void onDownloadComplete() 
	{

	}

	@Override
	public void onDownloadError(String message) 
	{
		String msg = mMapFrag.getActivity().getResources().getString(R.string.radarAnimDownloadError) + "\n" + message;
		Toast.makeText(mMapFrag.getActivity().getApplicationContext(), msg, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onUrlsReady(String urlList) 
	{
		mUrlList = urlList;
	}

	@Override
	public void onTaskCancelled()
	{
		String msg = mMapFrag.getActivity().getResources().getString(R.string.radarAnimationTaskCancelled);
		Toast.makeText(mMapFrag.getActivity().getApplicationContext(), msg, Toast.LENGTH_LONG).show();
		mTimeoutHandler.removeCallbacks(this);
	}

	private void mStartAnimation()
	{	
		Log.e("RadarAnimation.mStartAnimation", "schedulin' execution each ms " + animationStepDuration);
		mTimeoutHandler.postDelayed(this, 750); /* first frame after less than one second */
	}

	private void mBuildAnimationData(String txt)
	{
		mAnimationData.clear();
		String [] lines = txt.split("\n");
		for(int i = 0; i < lines.length; i++)
		{
			if(lines[i].contains("->"))
			{
				String [] parts = lines[i].split("->");
				AnimationData ad = new AnimationData(parts[0], parts[1]);
				mAnimationData.append(i, ad);
			}
		}
	}

	/** At each timeout, get the image and animate one step forward 
	 * 
	 */
	@Override
	public void run() 
	{
		if(mAnimationStatus == RadarAnimationStatus.NOT_RUNNING)
		{
			Log.e("RadarAnimation.run()", "animation status is paused or stopped, not rescheduling, not doing anything: " +
					mAnimationStatus);
		}
		else if(mAnimationStatus == RadarAnimationStatus.PAUSED)
		{
			/* normally reached after a restore phase (after screen orientation when the animation was paused)
			 * In that case, mFrameNo has been restored from the bundle
			 */
			if(mDownloadProgress == mFrameNo)
				mMakeStep();
			else
				Log.e("RadarAnimation.run()", "mDownloadProgress is " + mDownloadProgress + " discarding in pause mode");
		}
		else
		{
			if(mDownloadProgress > mFrameNo)
			{
				/* hide progress bar as soon as we start animating */
				hideProgressBar();
				/* show the pause button and the time label */
				showControls();

				/* get image from the external storage and update */

				mMakeStep();
				
				
				/* increment the number of updated frames */
				mFrameNo++;
						
			}
			else /* not enough data! Show progress bar */
			{
				setProgressBarValue(mDownloadProgress, mAnimationData.size());
			}

			if(mFrameNo >= mAnimationData.size()) /* end */
			{
				Log.e("RadarAnimation.run()", "not rescheduling execution: frame no " + mFrameNo + " anim size " + mAnimationData.size());
				/* animation ended */
				mAnimationStatus = RadarAnimationStatus.NOT_RUNNING;
				/* user can replay it */
				setButtonPlay();
				/* reset counters and invalidate url list */
				this.mResetProgressVariables();
			}
			else if(mAnimationStatus == RadarAnimationStatus.RUNNING) /* reschedule */
			{
				mTimeoutHandler.postDelayed(this, this.animationStepDuration);
			}

		} /* if(mAnimationStatus == RadarAnimationStatus.PAUSED || mAnimationStatus == RadarAnimationStatus.NOT_RUNNING) */
	}
	
	private void mMakeStep()
	{
		TextView timeTv = (TextView) mMapFrag.getActivity().findViewById(R.id.radarAnimTime);
		timeTv.setVisibility(View.VISIBLE);
		String text = mAnimationData.valueAt(mFrameNo).time + " [" + (mFrameNo + 1) + "/" + mAnimationData.size() + "] [" + 
				mDownloadProgress + "]";
		timeTv.setText(text);	
	}

}
