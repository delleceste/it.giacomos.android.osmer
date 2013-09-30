package it.giacomos.android.osmer.pro.widgets.map.animation;

import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.pro.locationUtils.GeoCoordinates;
import it.giacomos.android.osmer.pro.network.state.Urls;
import it.giacomos.android.osmer.pro.widgets.map.OMapFragment;
import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.view.View.OnClickListener;
import android.os.AsyncTask;

public class RadarAnimation implements OnClickListener,  RadarAnimationStateChangeListener
{
	private OMapFragment mMapFrag;
	private ArrayList<RadarAnimationListener> mAnimationListeners;
	private int mFrameNo, mDownloadProgress, mTotalSteps, mSavedFrameNo;
	private String mUrlList;
	private Handler mTimeoutHandler;
	private boolean mControlsVisible;
	private GroundOverlayOptions mGroundOverlayOptions;
	private GroundOverlay mGroundOverlay;
	private AnimationTask mAnimationTask;

	private SparseArray<AnimationData> mAnimationData;

	/* holds the state of the animation */
	private State mState;

	public RadarAnimation(OMapFragment mapf)
	{
		mMapFrag = mapf;
		/* stores the number of frame that was set on the map in onSaveInstanceState */
		mSavedFrameNo = -1; /* -1 if not set */
		mResetProgressVariables();
		/* button listeners */
		((ToggleButton) (mMapFrag.getActivity().findViewById(R.id.playPauseButton))).setOnClickListener(this);
		((ToggleButton) (mMapFrag.getActivity().findViewById(R.id.stopButton))).setOnClickListener(this);
		mControlsVisible = false;
		mAnimationListeners = new ArrayList<RadarAnimationListener>();
		mAnimationData = new SparseArray<AnimationData>();
		mTimeoutHandler = new Handler();
		mAnimationTask = null;

		mGroundOverlayOptions = null;
		mGroundOverlay = null;
		mState = new NotRunning(this, mAnimationTask, mTimeoutHandler, null);
		mState.enter(); /* not running: hide controls */
	}

	public OMapFragment getMapFragment() 
	{
		return mMapFrag;
	}

	public void pause()
	{
		Log.e("RadarAnimation", "pause");
		mState = new Paused(this, mAnimationTask, mTimeoutHandler, mState);
		for(RadarAnimationListener ral : mAnimationListeners)
			ral.onRadarAnimationPause();
	}

	/** called when the play/pause toggle button is clicked
	 * 
	 */
	public void resume() 
	{
		if(getStatus() == RadarAnimationStatus.PAUSED)
		{
			mState = new Running(this, mAnimationTask, mTimeoutHandler, mState,
					mUrlList, mDownloadProgress, mTotalSteps);
			mState.enter();

			for(RadarAnimationListener ral : mAnimationListeners)
				ral.onRadarAnimationResumed();
		}
		else if(getStatus()  == RadarAnimationStatus.NOT_RUNNING)
		{
			start();
		}
		else
			Log.e("RadarAnimation.resume", "cannot resume animation from status " + getStatus());
	}

	/** Called to restore the animation when interrupted by a screen orientation change
	 *  (or when the activity is put in the background).
	 *  Any state variable has to be correctly initialized in order to start() perform the
	 *  correct resume. (mDownloadProgress, mUrlList, mSavedFrameNo).
	 *  If restore is called right after restoreState, then it should correctly resume 
	 *  a previously interrupted animation.
	 * 
	 */
	public void restore()
	{
		if(getStatus() == RadarAnimationStatus.PAUSED)
		{
			Log.e("RadarAnimation.restore", "the animation status is PAUSED, starting animation task");
			
			mAnimationTask = new AnimationTask(mMapFrag.getActivity().getApplicationContext().getExternalFilesDir(null).getPath());
			Buffering buffering = new Buffering(this, mAnimationTask,
					mTimeoutHandler, mState, mUrlList);
			
			buffering.enter();
			mState = buffering;
			
			Toast.makeText(mMapFrag.getActivity().getApplicationContext(), 
					mMapFrag.getResources().getString(R.string.radarAnimationPausedAfterRotation),
					Toast.LENGTH_SHORT).show();
		}
		else
			Log.e("RadarAnimation.restore", "animation status " + getStatus());

		for(RadarAnimationListener ral : mAnimationListeners)
			ral.onRadarAnimationRestored();
	}

	public void start()
	{
		Log.e("RadarAnimation", "start");
		mState = new Buffering(this, mAnimationTask, mTimeoutHandler, mState, mUrlList);

		for(RadarAnimationListener ral : mAnimationListeners)
			ral.onRadarAnimationStart();
	}

	public void stop() 
	{
		Log.e("RadarAnimation", "stop");
		mState = new NotRunning(this, mAnimationTask, mTimeoutHandler, mState);

		/* reset counters and the list of image urls */
		mResetProgressVariables();

		/* remove image */
		if(mGroundOverlay != null)
			mGroundOverlay.remove();

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
		return (mState.getStatus() == RadarAnimationStatus.RUNNING ||
				mState.getStatus() == RadarAnimationStatus.PAUSED ||
				mState.getStatus() == RadarAnimationStatus.BUFFERING);
	}

	public RadarAnimationStatus getStatus()
	{
		return mState.getStatus();
	}

	public boolean isRunning() 
	{
		return  getStatus() == RadarAnimationStatus.RUNNING;
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
		int lastFrameNo = mFrameNo;
		RadarAnimationStatus currentAnimationStatus = getStatus();
		if(currentAnimationStatus == RadarAnimationStatus.RUNNING)
		{
			outState.putInt("animationStatus", 1);
			/* mFrameNo - 1 when RUNNING because the last run() invocation 
			 * incremented mFrameNo by 1 before returning. But on restore, 
			 * we want to show the paused animation with the last frame shown
			 * before the screen rotation.
			 */
			if(lastFrameNo > 0)
			{
				lastFrameNo--;
			}
		}
		else if(currentAnimationStatus == RadarAnimationStatus.NOT_RUNNING)
			outState.putInt("animationStatus", 0);
		else if(currentAnimationStatus == RadarAnimationStatus.PAUSED)
		{
			outState.putInt("animationStatus", 2);
			/* when saving the state in PAUSED status, lastFrameNo correctly points to the 
			 * currently shown frame. No need to subtract 1 as in the RUNNING status 
			 * case above.
			 */
		}

		Log.e("RadarAnimation.saveState", "lastFrameNo " + lastFrameNo);
		outState.putInt("animationFrameNo", lastFrameNo);
		outState.putInt("animationDownloadProgress", mDownloadProgress);
		outState.putString("urlList", mUrlList);
		outState.putBoolean("controlsVisible", (mMapFrag.getActivity().findViewById(R.id.playPauseButton).getVisibility() == View.VISIBLE));
	}

	public void restoreState(Bundle savedInstanceState) 
	{
		mSavedFrameNo = savedInstanceState.getInt("animationFrameNo", 0);
		mDownloadProgress = savedInstanceState.getInt("animationDownloadProgress", 0);
		mControlsVisible = savedInstanceState.getBoolean("controlsVisible", false);
		mUrlList = savedInstanceState.getString("urlList", "");
		int state = savedInstanceState.getInt("animationStatus", 0);
		/* status initialized to NOT_RUNNING in the constructor. No need to check for
		 * state == 0.
		 * On the other hand, if before rotating the animation was running, set it to
		 * paused after rotation.
		 */
		if(state > 0) /* set paused both if before the rotation it was running or paused */
			mState = new Paused(this, mAnimationTask, mTimeoutHandler, mState);
	}

	public void onDestroy()
	{

	}

	public void onPause() 
	{
		Log.e("RadarAnimation.onPause", "setting Paused state (this does not cancel task!)");
		mState = new Paused(this, mAnimationTask, mTimeoutHandler, mState);
		
		/* in case activity is resumed, just proceed in the same way as if it were destroyed and
		 * recreated.
		 * mFrameNo-- when RUNNING because the last run() invocation 
		 * incremented mFrameNo by 1 before returning
		 */
//		if(mAnimationStatus == RadarAnimationStatus.RUNNING)
//		{
//			mAnimationStatus = RadarAnimationStatus.PAUSED;
//			if(mFrameNo > 0)
//				mFrameNo--;
//		}
	}

	public void onResume()
	{
		restore();
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
		}
		else if(v.getId() == R.id.stopButton)
			stop();
	}

	@Override
	public void onDownloadProgressChanged(int step, int total) 
	{
		mTotalSteps = total;
		mDownloadProgress = step;

		/* first step: the file with the lines coupling timestamp/radar filename is ready.
		 * Parse it and populate mAnimationData.
		 */
		if(step == 1) /* download urls ready */
			mBuildAnimationData(mAnimationTask.getDownloadUrls());
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

	@Override
	public void onFrameUpdatePossible(int frameNo) 
	{
		mFrameNo = frameNo;
		mMakeStep();
	}

	@Override
	public void onTransition(RadarAnimationStatus to) 
	{
		RadarAnimationStatus from = mState.getStatus();
		Log.e("RadarAnimation.onTransition", from + " --> " + to);
		if(from == RadarAnimationStatus.BUFFERING && to == RadarAnimationStatus.RUNNING)
		{
			Buffering buff = (Buffering) mState;
			Running running = new Running(this, mAnimationTask, mTimeoutHandler, mState,
					buff.getUrlList(), 
					buff.getCurrentStep(), buff.getTotSteps());
			
			if(mSavedFrameNo > -1)
				running.setPauseOnFrameNo(mSavedFrameNo);
			
			mState = running;
			running.enter();
		}
		else if(from == RadarAnimationStatus.RUNNING && to == RadarAnimationStatus.FINISHED)
		{

		}
		else if(to == RadarAnimationStatus.FINISHED)
		{
			Toast.makeText(mMapFrag.getActivity().getApplicationContext(), R.string.radarAnimationTaskCancelled, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onAnimationDataAvailable(int index, AnimationData animationData) 
	{
		mAnimationData.setValueAt(index, animationData);
	}

	/** implements onError from interface RadarAnimationStateChangeListener
	 * @param message the network error message
	 */
	@Override
	public void onError(String message) 
	{
		// TODO Auto-generated method stub
		String msg = mMapFrag.getActivity().getResources().getString(R.string.radarAnimDownloadError) + "\n" + message;
		Toast.makeText(mMapFrag.getActivity().getApplicationContext(), msg, Toast.LENGTH_LONG).show();
	}

	//	/** At each timeout, get the image and animate one step forward 
	//	 * 
	//	 */
	//	@Override
	//	public void run() 
	//	{
	//		if(mAnimationStatus == RadarAnimationStatus.NOT_RUNNING)
	//		{
	//			Log.e("RadarAnimation.run()", "animation status is paused or stopped, not rescheduling, not doing anything: " +
	//					mAnimationStatus);
	//		}
	//		else if(mAnimationStatus == RadarAnimationStatus.PAUSED)
	//		{
	//			Log.e("RadarAnimation.run()", "mDownloadProgress is " + mDownloadProgress + ", mFrameNo is " + mFrameNo);
	//			/* normally reached after a restore phase (after screen orientation when the animation was paused)
	//			 * In that case, mFrameNo has been restored from the bundle.
	//			 * Normally, run shouldn't be invoked after the user presses the "pause" button, because on pause button
	//			 * press, the mTimeoutHandler used to schedule a future update is stopped.
	//			 */
	//			if(mDownloadProgress < mFrameNo)
	//			{
	//				Log.e("RadarAnimation.run", "waiting for download progress to be equal to " + mFrameNo);
	//				mTimeoutHandler.postDelayed(this, 150);
	//				hidePlayPause();
	//			}
	//			else
	//			{
	//				Log.e("RadarAnimation.run", "mDownloadProgress >= mFrameNo -> " + mFrameNo + " - making step");
	//				showPlayPause();
	//				mMakeStep();
	//			}
	//		}
	//		else
	//		{
	//			if(mDownloadProgress > mFrameNo)
	//			{
	//				/* hide progress bar as soon as we start animating */
	//				hideProgressBar();
	//				/* show the pause button and the time label */
	//				showControls();
	//				/* get image from the external storage and update */
	//				mMakeStep();
	//				/* increment the number of updated frames */
	//				mFrameNo++;
	//			}
	//			else /* not enough data! Show progress bar */
	//			{
	//				setProgressBarValue(mDownloadProgress, mAnimationData.size());
	//				showTimestampText();
	//
	//				Log.e("RadarAnimation.run", " mDownloadProgress " + mDownloadProgress + "mAnimationData size " + mAnimationData.size());
	//				String text = mMapFrag.getActivity().getResources().getString(R.string.radarAnimationBuffering) + " " + 
	//						mDownloadProgress + "/" + mAnimationData.size();
	//				setTimestampText(text);
	//				hidePlayPause();
	//			}
	//
	//			if(mFrameNo >= mAnimationData.size()) /* end */
	//			{
	//				Log.e("RadarAnimation.run()", "not rescheduling execution: frame no " + mFrameNo + " anim size " + mAnimationData.size());
	//				/* animation ended */
	//				mAnimationStatus = RadarAnimationStatus.NOT_RUNNING;
	//				/* user can replay it */
	//				showPlayPause();
	//				setButtonPlay();
	//				/* reset counters and invalidate url list */
	//				this.mResetProgressVariables();
	//			}
	//			else if(mAnimationStatus == RadarAnimationStatus.RUNNING) /* reschedule */
	//			{
	//				mTimeoutHandler.postDelayed(this, this.animationStepDuration);
	//			}
	//
	//		} /* if(mAnimationStatus == RadarAnimationStatus.PAUSED || mAnimationStatus == RadarAnimationStatus.NOT_RUNNING) */
	//	}

	private void mMakeStep()
	{
		if(mAnimationData != null && mFrameNo < mAnimationData.size())
		{
			String text = mAnimationData.valueAt(mFrameNo).time + " [" + (mFrameNo + 1) + "/" + mAnimationData.size() + "]";
			TextView timeTv = (TextView) mMapFrag.getActivity().findViewById(R.id.radarAnimTime);
			timeTv.setText(text);

			/* get bitmap */
			FileHelper fileHelper = new FileHelper();
			Bitmap bmp = fileHelper.decodeImage(mAnimationData.valueAt(mFrameNo).fileName, 
					mMapFrag.getActivity().getApplicationContext().getExternalFilesDir(null).getPath());
			if(bmp != null)
			{
				GoogleMap googleMap = mMapFrag.getMap();
				/* ground overlay configuration */
				if(mGroundOverlayOptions == null)
				{
					mGroundOverlayOptions = new GroundOverlayOptions();
					mGroundOverlayOptions.positionFromBounds(GeoCoordinates.radarImageBounds);
					mGroundOverlayOptions.transparency(0.65f);
				}
				/* specify the image before the ovelay is added */
				BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bmp);
				mGroundOverlayOptions.image(bitmapDescriptor);
				if(mGroundOverlay != null)
					mGroundOverlay.remove();
				mGroundOverlay = googleMap.addGroundOverlay(mGroundOverlayOptions);
			}
		}
	}


}
