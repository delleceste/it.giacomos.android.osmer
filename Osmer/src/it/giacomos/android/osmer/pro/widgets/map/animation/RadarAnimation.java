package it.giacomos.android.osmer.pro.widgets.map.animation;

import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.pro.locationUtils.GeoCoordinates;
import it.giacomos.android.osmer.pro.network.DownloadStatus;
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
	private GroundOverlayOptions mGroundOverlayOptions;
	private GroundOverlay mGroundOverlay;
	private String mUrlList;
	
	/* The animation task, which downloads all necessary data from the internet (text file
	 * with the list of the URLs of the images and all the images.
	 * This AnimationTask is allocated by the Buffering State, inside the enter method.
	 * Notwithstanding, this class keeps a reference to the task which is always passed through
	 * different states. Actually, the Buffering and Running states share the same animation task,
	 * while the other states can possibly cancel the task (for instance the NotRunning state, 
	 * entered after we cancel the download or the animation in the Buffering or Running states,
	 * cancels the task).
	 * Since an AsyncTask can be used only once, and since the Buffering state is the one that 
	 * initiates a new data fetch, the Buffering enter method allocates a new task each time it
	 * is called, thus changing the reference of mAnimationTask.
	 */
	private AnimationTask mAnimationTask;

	private SparseArray<AnimationData> mAnimationData;

	/* holds the state of the animation */
	private State mState;

	public RadarAnimation(OMapFragment mapf)
	{
		mMapFrag = mapf;
		/* stores the number of frame that was set on the map in onSaveInstanceState */
		mResetProgressVariables();
		/* button listeners */
		((ToggleButton) (mMapFrag.getActivity().findViewById(R.id.playPauseButton))).setOnClickListener(this);
		((ToggleButton) (mMapFrag.getActivity().findViewById(R.id.stopButton))).setOnClickListener(this);
		mAnimationListeners = new ArrayList<RadarAnimationListener>();
		mAnimationData = new SparseArray<AnimationData>();
		mAnimationTask = null;

		mGroundOverlayOptions = null;
		mGroundOverlay = null;
		mState = new NotRunning(this, mAnimationTask, null);
		mState.enter(); /* not running: hide controls */
		
		// TEMPPP
		DownloadStatus.Instance().executionNumber++;
	}

	public OMapFragment getMapFragment() 
	{
		return mMapFrag;
	}

	public void pause()
	{
		Log.e("RadarAnimation", "pause");
		mState = new Paused(this, mAnimationTask, mState);
		mState.enter();
		for(RadarAnimationListener ral : mAnimationListeners)
			ral.onRadarAnimationPause();
	}

	/** called when the play/pause toggle button is clicked
	 * 
	 */
	public void resume() 
	{
		if(mState.getStatus() == RadarAnimationStatus.PAUSED)
		{
			mState = new Running(this, mAnimationTask, mState);
			mState.enter();

			for(RadarAnimationListener ral : mAnimationListeners)
				ral.onRadarAnimationResumed();
		}
		else if(mState.getStatus() == RadarAnimationStatus.NOT_RUNNING 
				|| mState.getStatus() == RadarAnimationStatus.FINISHED)
		{
			start();
		}
		else
			Log.e("RadarAnimation.resume", "cannot resume animation from status " + mState.getStatus());
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
		if(mState.getStatus() == RadarAnimationStatus.INTERRUPTED ) /* animation running before rotation/ app backgrounded */
		{
			Interrupted interrupted = (Interrupted) mState;
			String urlList = interrupted.getUrlList();
			Buffering buffering = new Buffering(this, mAnimationTask,
					mState, urlList);
			mState = buffering;
			
			/* to tell the following Running state that it must post the mSavedFrameNo frame and go to pause */
			buffering.setPauseOnFrameNo(interrupted.getFrameNo());

			Log.e("RadarAnimation.restore", "the animation status is INTERRUPTED, starting animation task, will pause on frame "
					+ interrupted.getFrameNo());
			/* allocates a new AnimationTask. mAnimationTask is passed through states */
			buffering.enter();
			mAnimationTask = buffering.getAnimationTask();
			
			Toast.makeText(mMapFrag.getActivity().getApplicationContext(), 
					mMapFrag.getResources().getString(R.string.radarAnimationPausedAfterRotation),
					Toast.LENGTH_SHORT).show();
		}
		else
			Log.e("RadarAnimation.restore", "animation status " + mState.getStatus());

		for(RadarAnimationListener ral : mAnimationListeners)
			ral.onRadarAnimationRestored();
	}

	public void start()
	{
		Log.e("RadarAnimation", "start");
		DownloadStatus downloadStatus = DownloadStatus.Instance();
		if(downloadStatus.isOnline)
		{
			mState = new Buffering(this, mAnimationTask, mState, mUrlList);
			mState.enter();
			mAnimationTask = mState.getAnimationTask();

			for(RadarAnimationListener ral : mAnimationListeners)
				ral.onRadarAnimationStart();
		}
		else
			Toast.makeText(mMapFrag.getActivity().getApplicationContext(), R.string.radarAnimationMustBeOnline, Toast.LENGTH_LONG).show();
	}

	public void stop() 
	{
		Log.e("RadarAnimation", "stop");
		mState = new NotRunning(this, mAnimationTask, mState);
		mState.enter();
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
	}

	public void registerRadarAnimationListener(RadarAnimationListener ral)
	{
		mAnimationListeners.add(ral);
	}

	public void removeRadarAnimationListener(RadarAnimationListener ral)
	{
		mAnimationListeners.remove(ral);
	}

	public void saveState(Bundle outState) 
	{
		int lastFrameNo = -1;
		int downloadStep = -1;
		RadarAnimationStatus currentAnimationStatus = mState.getStatus();
		if(mState.isProgressState())
		{
			ProgressState progressState = (ProgressState) mState;
			lastFrameNo = progressState.getFrameNo();
			downloadStep = progressState.getDownloadStep();
			/* animationStatus 1 means it was interrupted */
			outState.putInt("animationStatus", 1);
			
			Log.e("RadarAnimation.saveStaeate", "state is in progress... saving variables sownload step: " + downloadStep + " frame no "
					+ lastFrameNo + " mUrlList " + mUrlList);
			
			if(currentAnimationStatus == RadarAnimationStatus.RUNNING && lastFrameNo > 0)
			{
				/* dFrameNo - 1 when RUNNING because the last run() invocation 
				 * incremented dFrameNo by 1 before returning. But on restore, 
				 * we want to show the paused animation with the last frame shown
				 * before the screen rotation.
				 */
				lastFrameNo--;
			}
		}
		else if(currentAnimationStatus == RadarAnimationStatus.NOT_RUNNING)
			outState.putInt("animationStatus", 0);
		else if(currentAnimationStatus == RadarAnimationStatus.FINISHED)
			outState.putInt("animationStatus", 2);

		Log.e("RadarAnimation.saveState", "lastFrameNo " + lastFrameNo);
		outState.putInt("animationFrameNo", lastFrameNo);
		outState.putInt("animationDownloadProgress", downloadStep);
		outState.putString("urlList", mUrlList);
		outState.putBoolean("controlsVisible", (mMapFrag.getActivity().findViewById(R.id.playPauseButton).getVisibility() == View.VISIBLE));
	}

	public void restoreState(Bundle savedInstanceState) 
	{
		int savedFrameNo = savedInstanceState.getInt("animationFrameNo", 0);
		int savedDownloadProgress = savedInstanceState.getInt("animationDownloadProgress", 0);
		String urlList = savedInstanceState.getString("urlList", "");
		int savedAnimationStatusAsInt = savedInstanceState.getInt("animationStatus", 0);
		
		/* NotRunning state already set in the constructor: look for 1 and 3 */
		if(savedAnimationStatusAsInt == 1)
		{
			Log.e("RadarAnimation.restoreState", "creating INTERRUPTED, savedFrameNo "  + savedFrameNo + 
					" download progress " + savedDownloadProgress);
			mState = new Interrupted(this, savedFrameNo, savedDownloadProgress, urlList);
			mState.enter();
		}
		else if(savedAnimationStatusAsInt == 2)
		{
			Log.e("RadarAnimation.restoreState", " saved status was FINISHED");
			mState = new Finished(this, mAnimationTask, mState);
			mState.enter();
		}
		else
			Log.e("RadarAnimation.restoreState", "no particular saved state");
	}

	public void onDestroy()
	{

	}

	public void onPause() 
	{
		Log.e("RadarAnimation.onPause", "setting Interrupted state to CANCEL THE TASK!");
		if(mState.animationInProgress())
		{
			/* this will cancel the task */
			mState = new Interrupted(this, mAnimationTask, mState, mUrlList);
			mState.enter();
		}
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
		/* first step: the file with the lines coupling timestamp/radar filename is ready.
		 * Parse it and populate mAnimationData.
		 */
		if(step == 1) /* download urls ready */
		{
			mUrlList = mAnimationTask.getDownloadUrls();
			mBuildAnimationData(mUrlList);
		}
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
		mMakeStep(frameNo);
	}

	@Override
	public void onTransition(RadarAnimationStatus to) 
	{
		RadarAnimationStatus from = mState.getStatus();
		Log.e("RadarAnimation.onTransition", from + " --> " + to + "[" + 
				this + "]");
		if(from == RadarAnimationStatus.BUFFERING && to == RadarAnimationStatus.RUNNING)
		{
			Running running = new Running(this, mAnimationTask, mState);
			mState = running;
			running.enter();
		}
		else if(from == RadarAnimationStatus.RUNNING && to == RadarAnimationStatus.FINISHED)
		{
			mState = new Finished(this, mAnimationTask, mState);
			mState.enter();
		}
		else if(to == RadarAnimationStatus.BUFFERING)
		{
			mState = new Buffering(this, mAnimationTask, mState, mUrlList);
			mState.enter();
		}
		else if(from == RadarAnimationStatus.RUNNING && to == RadarAnimationStatus.PAUSED)
		{
			mState = new Paused(this, mAnimationTask, mState);
			mState.enter();
		}
		
		else if(to == RadarAnimationStatus.FINISHED)
		{
			mState = new Finished(this, mAnimationTask, mState);
			mState.enter();
		}
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

	private void mMakeStep(int frameNo)
	{
		if(mAnimationData != null && frameNo < mAnimationData.size())
		{
			String text = mAnimationData.valueAt(frameNo).time + " [" + (frameNo + 1) + "/" + mAnimationData.size() + "]";
			TextView timeTv = (TextView) mMapFrag.getActivity().findViewById(R.id.radarAnimTime);
			timeTv.setText(text);

			/* get bitmap */
			FileHelper fileHelper = new FileHelper();
			Bitmap bmp = fileHelper.decodeImage(mAnimationData.valueAt(frameNo).fileName, 
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
	
	public State getState()
	{
		return mState;
	}
}
