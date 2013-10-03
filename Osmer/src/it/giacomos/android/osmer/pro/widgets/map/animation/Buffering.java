package it.giacomos.android.osmer.pro.widgets.map.animation;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.pro.network.DownloadStatus;
import it.giacomos.android.osmer.pro.network.state.Urls;
import it.giacomos.android.osmer.pro.widgets.map.OMapFragment;
import it.giacomos.android.osmer.pro.widgets.map.animation.RadarAnimation;

/** This class represents the first step of the download, when we are buffering data.
 * 
 * In this state it is possible to
 * 
 * - cancel the download of the animation
 * 
 * - go to the running state when enough data has been downloaded
 * 
 * In this state it is NOT possible to go to PAUSED.
 * 
 * This state can be reached from NOT_RUNNING (menu->animation) or from 
 * FINISHED (menu->animation or play button).
 * 
 * @author giacomo
 *
 */
public class Buffering extends ProgressState  implements  AnimationTaskListener
{

	private String mUrlList;
	private int mPauseOnFrameNo;
	
	Buffering(RadarAnimation radarAnimation, AnimationTask animationTask,
			State previousState, String urlList) 
	{
		super(radarAnimation, animationTask, previousState);
		dAnimationStatus = RadarAnimationStatus.BUFFERING;
		mUrlList = urlList;
		mPauseOnFrameNo = -1;
		/* network latency */
		if(previousState.getStatus() == RadarAnimationStatus.RUNNING)
		{
			Running ru = (Running) previousState;
			dTotSteps = ru.getTotSteps();
			dDownloadStep = ru.getDownloadStep();
			dFrameNo = ru.getFrameNo();
		}
	}
	
	public void setPauseOnFrameNo(int frameNo)
	{
		mPauseOnFrameNo = frameNo;
	}
	
	public int getPauseOnFrameNo()
	{
		return mPauseOnFrameNo;
	}
	
	public int getTotSteps()
	{
		return dTotSteps;
	}
	
	public int getCurrentStep()
	{
		return dDownloadStep;
	}
	
	public String getUrlList()
	{
		return mUrlList;
	}
	
	@Override
	public RadarAnimationStatus getStatus() 
	{
		return RadarAnimationStatus.BUFFERING;
	}

	@Override
	public void enter() 
	{
		Log.e("Buffering.enter", "entering" + "[" + 
						DownloadStatus.Instance().executionNumber + "]");
		OMapFragment mapFrag = dRadarAnimation.getMapFragment();
		Resources res = mapFrag.getActivity().getResources();
		ToggleButton tb = (ToggleButton )mapFrag.getActivity().findViewById(R.id.playPauseButton);
		tb.setChecked(true);
		/* play/ pause hidden */
		mapFrag.getActivity().findViewById(R.id.playPauseButton).setVisibility(View.GONE);
		/* stop (cancel) visible */
		mapFrag.getActivity().findViewById(R.id.stopButton).setVisibility(View.VISIBLE);
		/* progress bar visible */
		mapFrag.getActivity().findViewById(R.id.radarAnimProgressBar).setVisibility(View.VISIBLE);
		
		/* timestamp label visible and showing "buffering" */
		TextView timeTv = (TextView) mapFrag.getActivity().findViewById(R.id.radarAnimTime);
		timeTv.setVisibility(View.VISIBLE);
		
		/* show progress bar and stop button */
		dRadarAnimation.onDownloadProgressChanged(0, 10);

		String text = res.getString(R.string.radarAnimationBuffering);
		timeTv.setText(text);
		
		/* after state restore (screen rotation), mUrlList may contain the urls downloaded right before the rotation if
		 * the animation was running.
		 * Normally, we enter this state from NOT_RUNNING, and so a new task has to be created and executed.
		 * In case of high network latency, we may enter the BUFFERING state from a RUNNING state.
		 * In that case, the animation task is still executing and downloading images, so it must not be recreated and
		 * started. In any case, set the animation task listener to this in order to switch again to the RUNNING state
		 * when some more data has been downloaded.
		 */
		if(dPreviousState == null || dPreviousState.getStatus() != RadarAnimationStatus.RUNNING)
			dAnimationTask = new AnimationTask(mapFrag.getActivity().getApplicationContext().getExternalFilesDir(null).getPath());
		
		dAnimationTask.setDownloadUrls(mUrlList);
		dAnimationTask.setAnimationTaskListener(this);
		if(dPreviousState == null || dPreviousState.getStatus() != RadarAnimationStatus.RUNNING)
			dAnimationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Urls().radarHistoricalFileListUrl());
	}

	public void cancel() 
	{
		Log.e("Buffering.cancel", "cancelling task, migrating to NOT_RUNNING");
		dAnimationTask.cancel(false);
		dRadarAnimation.onTransition(RadarAnimationStatus.NOT_RUNNING);
	}
	
	public void leave() 
	{
		Log.e("Buffering.leave", "leaving for RUNNING");
		dRadarAnimation.onTransition(RadarAnimationStatus.RUNNING);
	}

	@Override
	public void onProgressUpdate(int step, int total) 
	{
		dDownloadStep = step;
		dTotSteps = total;
		
		/* when step is == 1, the images url file is ready and the RadarAnimation,
		 * inside onAnimationProgressChanged, will populate AnimationData SparseArray.
		 */
		dRadarAnimation.onDownloadProgressChanged(step, total);
		
		/* wait until the 40% of the images has been downloaded */
		if(step > 1 && step >= Math.round(0.4f * (float) total))
		{
			Log.e("Buffering.onProgressUpdate", "leaving with step " + step + " and total is " + total);
			/* can start animating */
			leave();
		}
		else
		{
			Log.e("Buffering.onProgressUpdate", "progress bar with step " + step + " and total is " + total);
			OMapFragment mapFrag = dRadarAnimation.getMapFragment();
			TextView timeTv = (TextView) mapFrag.getActivity().findViewById(R.id.radarAnimTime);
			String text = mapFrag.getActivity().getResources().getString(R.string.radarAnimationBuffering) + " " + 
					step + "/" + total;
			timeTv.setText(text);
		}
	}

	@Override
	public void onDownloadComplete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDownloadError(String message) 
	{
		dRadarAnimation.onError(message);
		Log.e("Buffering.onDownloadError", "cancelling task, going to NOT_RUNNING");
		cancel();
	}

	@Override
	public void onUrlsReady(String urlList) 
	{
		mUrlList = urlList;
		
	}

	@Override
	public void onTaskCancelled() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isRunnable() {
		return false;
	}

	@Override
	public boolean isProgressState() {
		return true;
	}

}
