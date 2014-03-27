package it.giacomos.android.osmer.widgets.map;

import it.giacomos.android.osmer.network.Data.DataPool;
import it.giacomos.android.osmer.network.Data.DataPoolCacheUtils;
import it.giacomos.android.osmer.network.state.ViewType;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

/** 
 * Delays by some milliseconds the initialization of the webcam overlay.
 * The initialization concerns the registration of the webcam overlay as text listener
 * on DataPool and the initialization of the webcam overlay with the configuration
 * of the webcams saved into the internal cache.
 * If the data pool already contains the webcam data (the webcam data has
 * already been downloaded from the internet), 
 * then internal storage cache lookup is not performed.
 * <p>
 * The DelayedWebcamOverlayInitializer start method allocates a Handler that
 * delays the webcam overlay configuration by DELAY milliseconds.
 * </p>
 * @author giacomo
 *
 */
public class DelayedWebcamOverlayInitializer implements Runnable {

	private final WebcamOverlay mWebcamOverlay;
	private final Context mContext;
	private final DataPool mDataPool;
	
	/* timeout before initialization is performed */
	private final int DELAY = 150;
	
	public DelayedWebcamOverlayInitializer(WebcamOverlay webcamOverlay, 
			DataPool dataPool, Context ctx)
	{
		mWebcamOverlay = webcamOverlay;
		mContext = ctx;
		mDataPool = dataPool;
	}
	
	/** schedule the initialization of the webcam overlay by DELAY milliseconds
	 * 
	 */
	void start()
	{
		new Handler().postDelayed(this, DELAY);
	}
	
	@Override
	public void run() 
	{
		mDataPool.registerTextListener(ViewType.WEBCAMLIST_OSMER, mWebcamOverlay);
		mDataPool.registerTextListener(ViewType.WEBCAMLIST_OTHER, mWebcamOverlay);
		
		DataPoolCacheUtils dpcu = new DataPoolCacheUtils();
		String webcamStr = "";
		if(!mDataPool.isTextValid(ViewType.WEBCAMLIST_OSMER))
		{
			/* load from cache */
			webcamStr = dpcu.loadFromStorage(ViewType.WEBCAMLIST_OSMER, mContext);
			/* the last true parameters is textChanged and triggers an update */
			mWebcamOverlay.onTextChanged(webcamStr, ViewType.WEBCAMLIST_OSMER, true);
		}
		
		if(!mDataPool.isTextValid(ViewType.WEBCAMLIST_OTHER))
		{
			webcamStr = dpcu.loadFromStorage(ViewType.WEBCAMLIST_OTHER, mContext);
			mWebcamOverlay.onTextChanged(webcamStr, ViewType.WEBCAMLIST_OTHER, true);
		}
	}
}
