package it.giacomos.android.osmer.pro.widgets.map.animation;

public interface RadarAnimationListener {
	
	public void onRadarAnimationStart();
	
	/** called at the end of pause
	 * 
	 */
	public void onRadarAnimationPause();
	
	/** called at the end of stop.
	 * At this point, controls are no more visible.
	 * Tasks are scheduled to be cancelled.
	 * Handler for animation updates has been unscheduled.
	 */
	public void onRadarAnimationStop();
	
	/** invoked at the end of restore().
	 * Must manually check for state.
	 */
	public void onRadarAnimationRestored();
	
	/** invoked from PAUSED to RUNNING 
	 */
	public void onRadarAnimationResumed();

}
