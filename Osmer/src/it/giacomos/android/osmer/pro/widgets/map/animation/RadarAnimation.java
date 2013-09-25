package it.giacomos.android.osmer.pro.widgets.map.animation;

import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.pro.widgets.map.OMapFragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ToggleButton;
import android.view.View.OnClickListener;

public class RadarAnimation implements OnClickListener, AnimationTaskListener
{
	private OMapFragment mMapFrag;
	private RadarAnimationStatus mAnimationStatus;
	private ArrayList<RadarAnimationListener> mAnimationListeners;
	private int mFrameNo, mDownloadProgress;
	
	private SparseArray<String> mAnimationData;
	
	public RadarAnimation(OMapFragment mapf)
	{
		mMapFrag = mapf;
		mAnimationStatus = RadarAnimationStatus.NOT_RUNNING;
		mFrameNo = mDownloadProgress = 0;
		/* button listeners */
		((ToggleButton) (mMapFrag.getActivity().findViewById(R.id.playPauseButton))).setOnClickListener(this);
		((ToggleButton) (mMapFrag.getActivity().findViewById(R.id.stopButton))).setOnClickListener(this);
	}

	public void pause()
	{
		Log.e("RadarAnimation", "start");
		mAnimationStatus = RadarAnimationStatus.PAUSED;
		
		for(RadarAnimationListener ral : mAnimationListeners)
			ral.onRadarAnimationPause();
	}
	
	public void resume() 
	{
		showControls();
	}
	
	public void start()
	{
		Log.e("RadarAnimation", "start");
		mAnimationStatus = RadarAnimationStatus.RUNNING;
		
		for(RadarAnimationListener ral : mAnimationListeners)
			ral.onRadarAnimationStart();
	}

	public void stop() 
	{
		Log.e("RadarAnimation", "stop");
		mAnimationStatus = RadarAnimationStatus.NOT_RUNNING;
		
		hideControls();
		
		for(RadarAnimationListener ral : mAnimationListeners)
			ral.onRadarAnimationStop();
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
	
	public void saveState(Bundle outState) {
		if(mAnimationStatus == RadarAnimationStatus.RUNNING)
			outState.putInt("animationStatus", 1);
		else if(mAnimationStatus == RadarAnimationStatus.NOT_RUNNING)
			outState.putInt("animationStatus", 0);
		else if(mAnimationStatus == RadarAnimationStatus.PAUSED)
			outState.putInt("animationStatus", 2);
		
		outState.putInt("animationDownloadProgress", mDownloadProgress);
		outState.putInt("animationFrameNo", mFrameNo);
	}

	public void restoreState(Bundle savedInstanceState) 
	{
		mFrameNo = savedInstanceState.getInt("animationFrameNo", 0);
		mDownloadProgress = savedInstanceState.getInt("animationDownloadProgress", 0);
		int state = savedInstanceState.getInt("animationStatus", 0);
		/* status initialized to NOT_RUNNING in the constructor. No need to check for
		 * state == 0
		 */
		if(state == 1)
			mAnimationStatus = RadarAnimationStatus.RUNNING;
		else if(state == 2)
			mAnimationStatus = RadarAnimationStatus.PAUSED;
	}

	@Override
	public void onClick(View v) 
	{
		if(v.getId() == R.id.playPauseButton)
		{
			ToggleButton pp = (ToggleButton) v;
			if(pp.isChecked())
				resume();
			else
				stop();
			
			pp.setChecked(!pp.isChecked());
		}
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
	}

	@Override
	public void animationCanStart() 
	{
		
	}

	@Override
	public void onDownloadComplete() 
	{
		
	}

}
