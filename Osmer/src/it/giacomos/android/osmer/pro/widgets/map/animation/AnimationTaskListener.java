package it.giacomos.android.osmer.pro.widgets.map.animation;

public interface AnimationTaskListener 
{
	public void onProgressUpdate(int step, int total);
	
	public void onDownloadComplete();
	
	public void onDownloadError(String message);
	
	public void onUrlsReady(String urlList);
	
	public void onTaskCancelled();
}
