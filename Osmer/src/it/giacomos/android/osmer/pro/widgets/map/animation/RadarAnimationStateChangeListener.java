package it.giacomos.android.osmer.pro.widgets.map.animation;

public interface RadarAnimationStateChangeListener 
{
	public void onDownloadProgressChanged(int step, int total);
	
	public void onTransition(RadarAnimationStatus toStatus);
	
	public void onError(String message);
	
	public void onFrameUpdatePossible(int frameNo);
	
	public void onAnimationDataAvailable(int index, AnimationData animationData);

}
