package it.giacomos.android.osmer.widgets.map;

import java.util.ArrayList;

import it.giacomos.android.osmer.webcams.WebcamData;

public class WebcamOverlayUpdateRunnable implements Runnable {

	private WebcamOverlay mWebcamOverlay;
	private ArrayList<WebcamData> mWebcamDataList;
	
	public WebcamOverlayUpdateRunnable(WebcamOverlay webcamOverlay, ArrayList<WebcamData> webcamDataList)
	{
		mWebcamOverlay = webcamOverlay;
		mWebcamDataList = webcamDataList;
	}
	
	@Override
	public void run() 
	{
		mWebcamOverlay.update(mWebcamDataList);
	}

}
