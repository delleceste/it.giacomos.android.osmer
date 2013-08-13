package it.giacomos.android.osmer.widgets.map;

public class WebcamOverlayUpdateRunnable implements Runnable {

	private WebcamOverlay mWebcamOverlay;
	
	public WebcamOverlayUpdateRunnable(WebcamOverlay webcamOverlay)
	{
		mWebcamOverlay = webcamOverlay;
	}
	
	@Override
	public void run() 
	{
		mWebcamOverlay.update();
	}

}
