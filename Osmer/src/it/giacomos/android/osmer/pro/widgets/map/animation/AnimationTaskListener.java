package it.giacomos.android.osmer.pro.widgets.map.animation;

public interface AnimationTaskListener 
{
	public void onProgressUpdate(int step, int total);
	
	/** invoked when enough images have been buffered so that it is
	 *  possible to guarantee a sufficiently smooth animation.
	 */
	public void animationCanStart();
	
	public void onDownloadComplete();
	
	public void onDownloadError(String message);
	
	public void onUrlsReady(String urlList);
}
