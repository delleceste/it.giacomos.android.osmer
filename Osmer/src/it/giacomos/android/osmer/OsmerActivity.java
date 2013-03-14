package it.giacomos.android.osmer;
import com.google.android.maps.MapActivity;

import it.giacomos.android.osmer.downloadManager.DownloadManager;
import it.giacomos.android.osmer.downloadManager.DownloadReason;
import it.giacomos.android.osmer.downloadManager.DownloadStatus;
import it.giacomos.android.osmer.downloadManager.state.StateName;
import it.giacomos.android.osmer.downloadManager.state.Urls;
import it.giacomos.android.osmer.guiHelpers.ImageViewUpdater;
import it.giacomos.android.osmer.guiHelpers.MenuActionsManager;
import it.giacomos.android.osmer.guiHelpers.NetworkGuiErrorManager;
import it.giacomos.android.osmer.guiHelpers.ObservationTypeGetter;
import it.giacomos.android.osmer.guiHelpers.OnTouchListenerInstaller;
import it.giacomos.android.osmer.guiHelpers.TextViewUpdater;
import it.giacomos.android.osmer.guiHelpers.TitlebarUpdater;
import it.giacomos.android.osmer.guiHelpers.ToggleButtonGroupHelper;
import it.giacomos.android.osmer.guiHelpers.WebcamMapUpdater;
import it.giacomos.android.osmer.instanceSnapshotManager.SnapshotManager;
import it.giacomos.android.osmer.locationUtils.Constants;
import it.giacomos.android.osmer.locationUtils.GeocodeAddressTask;
import it.giacomos.android.osmer.locationUtils.GeocodeAddressUpdateListener;
import it.giacomos.android.osmer.locationUtils.LocationComparer;
import it.giacomos.android.osmer.locationUtils.LocationInfo;
import it.giacomos.android.osmer.observations.ObservationTime;
import it.giacomos.android.osmer.observations.ObservationType;
import it.giacomos.android.osmer.observations.ObservationsCache;
import it.giacomos.android.osmer.textToImage.TextDecoder;
import it.giacomos.android.osmer.textToImage.TextDecoderListener;
import it.giacomos.android.osmer.webcams.WebcamDataCache;
import it.giacomos.android.osmer.widgets.InfoHtmlBuilder;
import it.giacomos.android.osmer.widgets.ODoubleLayerImageView;
import it.giacomos.android.osmer.widgets.OTextView;
import it.giacomos.android.osmer.widgets.OViewFlipper;
import it.giacomos.android.osmer.widgets.mapview.OMapView;
import it.giacomos.android.osmer.widgets.mapview.MapViewMode;
import it.giacomos.android.osmer.widgets.SituationImage;
import it.giacomos.android.osmer.preferences.*;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;
/** 
 * 
 * @author giacomo
 *
 * - download first image (today forecast) and situation text immediately and independently (i.e. in
 *   two AsyncTasks each other), so that the user obtains the first information as soon as possible;
 * - in the background, continue downloading all other relevant information (tomorrow, two days image and
 *   forecast), so that it is ready when the user flips.
 */
public class OsmerActivity extends MapActivity 
implements OnClickListener, 
DownloadUpdateListener,
FlipperChildChangeListener,
SnapshotManagerListener,
OMapViewEventListener,
LocationListener,
GeocodeAddressUpdateListener,
TextDecoderListener
{
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//		Log.i("onCreate", "application created");
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		/* left icon for logo, right for connection status */
		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.main);
		init();
	}

	public void onResume()
	{	
		//		Log.i("onResume", "application resumed");
		super.onResume();

		/* registers network status monitor broadcast receiver (for this it needs `this')
		 * Must be called _after_ instance restorer's onResume!
		 */
		m_downloadManager.onResume(this);		

		/* Location Manager */
		OMapView omv = (OMapView)findViewById(R.id.mapview);
		omv.onResume();
		m_locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
		if(m_locationManager != null)
		{
			try
			{
			/*  (String provider, long minTime, float minDistance, LocationListener listener)
			 * 5000 the LocationManager could potentially rest for minTime milliseconds between location updates to conserve power.
			 * If minDistance is greater than 0, a location will only be broadcasted if the device moves by minDistance meters.
			 */
			m_locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 
					Constants.LOCATION_UPDATES_NETWORK_MIN_TIME, 
					Constants.LOCATION_UPDATES_NETWORK_MIN_DIST, this);

			m_locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
					Constants.LOCATION_UPDATES_GPS_MIN_TIME, 
					Constants.LOCATION_UPDATES_GPS_MIN_DIST, this);

			//		/* PASSIVE_PROVIDER: a special location provider for receiving locations without actually initiating a location fix.
			//		 * This provider can be used to passively receive location updates when other applications or 
			//		 * services request them without actually requesting the locations yourself. This provider will 
			//		 * return locations generated by other providers. You can query the getProvider() method to 
			//		 * determine the origin of the location update. Requires the permission ACCESS_FINE_LOCATION, 
			//		 * although if the GPS is not enabled this provider might only return coarse fixes. 
			//		 */
			//		m_locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 
			//				Constants.LOCATION_UPDATES_PASSIVE_MIN_TIME, 
			//				Constants.LOCATION_UPDATES_PASSIVE_MIN_DIST, this);
			}
			catch(IllegalArgumentException iae)
			{
				Log.e("OsmerActivity: onResume: requestLocationUpdates", iae.getLocalizedMessage());
			}
		}

		/* create WebcamDataCache with the Context */
		WebcamDataCache.getInstance(this.getCacheDir());

		/* hide Radar information text view on google map view */
		ToggleButton radarInfoButton = (ToggleButton) findViewById(R.id.radarInfoButton);
		View radarInfoTextView = findViewById(R.id.radarInfoTextView);
		if(radarInfoButton.isChecked())
			radarInfoTextView.setVisibility(View.VISIBLE);
		else
			radarInfoTextView.setVisibility(View.GONE);
	}

	public void onPause()
	{
		//		Log.i("onPause", "application paused");
		super.onPause();
		/* unregisters network status monitor broadcast receiver (for this it needs `this')
		 */
		m_downloadManager.onPause(this);
		((OMapView) findViewById(R.id.mapview)).onPause();
		if(m_locationManager != null)
			m_locationManager.removeUpdates(this);

		m_observationsCache.saveLatestToStorage(this);
		/* saves text and images on the internal storage 
		 * calling saveOnInternalStorage on OTextViews and ODoubleLayerImageViews,
		 * that implement the StateSaver interface.
		 */
		InternalStorageSaver iss = new InternalStorageSaver();
		iss.save(this);
		iss = null;
	}

	public void onStop()
	{
		//		Log.i("onStop", "application stopped");
		/* From Android documentation:
		 * Note that this method may never be called, in low memory situations where 
		 * the system does not have enough memory to keep your activity's process running 
		 * after its onPause() method is called. 
		 */
		super.onStop();
	}

	protected void onDestroy()
	{
		super.onDestroy();
	}

	public void onRestart()
	{
		super.onRestart();
	}

	public void onStart()
	{
		super.onStart();
	}

	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) 
	{
		this.closeOptionsMenu();
		if(mMenuActionsManager == null)
			mMenuActionsManager = new MenuActionsManager(this);

		boolean ret = mMenuActionsManager.itemSelected(item);
		return ret;
	}

	@Override
	public void onBackPressed()
	{
		/* first of all, if there's a baloon on the map, close it */
		ViewFlipper mainFlipper = (ViewFlipper) findViewById(R.id.viewFlipper1);
		OMapView map = (OMapView) findViewById(R.id.mapview);
		if(mainFlipper.getDisplayedChild() == 4 && map.baloonVisible()) /* map view visible */
		{
			map.removeBaloon();
		}
		else
		{
			ViewFlipper buttonFlipper = (ViewFlipper) findViewById(R.id.buttonsFlipper);
			int displayedChild = buttonFlipper.getDisplayedChild();
			switch(displayedChild)
			{
			case 1: /* sat, daily, last obs: just as clicking home */
				this.onClick(findViewById(R.id.buttonHome));
				break;
			case 2: /* daily observations, like pressing fvg button to go back to the satellite */
				this.onClick(findViewById(R.id.buttonMapInsideDaily));
				break;
			case 3:
				this.onClick(findViewById(R.id.buttonMapInsideLatest));
				break;
			default:
				super.onBackPressed();
			}
		}
	}

	@Override
	public Dialog onCreateDialog(int id, Bundle args)
	{
		if(id == MenuActionsManager.TEXT_DIALOG)
		{
			// Use the Builder class for convenient dialog construction
			Builder builder = new AlertDialog.Builder(this);
			builder.setPositiveButton(getResources().getString(R.string.ok_button), null);
			AlertDialog dialog = builder.create();
			dialog.setTitle(args.getString("title"));
			dialog.setCancelable(true);
			dialog.setMessage(Html.fromHtml(args.getString("text")));
			dialog.show();
			((TextView)dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());

		}
		return super.onCreateDialog(id, args);
	}


	public void init()
	{
		TextDecoder textDecoder = new TextDecoder(this);
		((OTextView) findViewById(R.id.todayTextView)).setTextChangeListener(textDecoder);
		((OTextView) findViewById(R.id.tomorrowTextView)).setTextChangeListener(textDecoder);
		((OTextView) findViewById(R.id.twoDaysTextView)).setTextChangeListener(textDecoder);
		((OTextView) findViewById(R.id.todayTextView)).setStringType(StringType.TODAY);
		((OTextView) findViewById(R.id.tomorrowTextView)).setStringType(StringType.TOMORROW);
		((OTextView) findViewById(R.id.twoDaysTextView)).setStringType(StringType.TWODAYS);

		OMapView map = (OMapView) findViewById(R.id.mapview);
		map.setMapViewEventListener(this);
		/* fixed in 1.1.5: satellite view button checked if map is  satellite.
		 * MapView saves somewhere its previous state and restores it.
		 * It was difficult to find where. Here it seems to be the right place to check or
		 * uncheck the satellite view button.
		 */
		((ToggleButton) findViewById(R.id.satelliteViewButton)).setChecked(map.isSatellite());

		/* install listeners on buttons */
		installButtonListener();
		installOnTouchListener();
		((OViewFlipper) findViewById(R.id.viewFlipper1)).setOnChildPageChangedListener(this);
		m_downloadManager = new DownloadManager(this);
		m_observationsCache = new ObservationsCache();
		/* map updates the observation data in ItemizedOverlay when new observations are available
		 *
		 */
		m_observationsCache.installObservationsCacheUpdateListener(map);

		SituationImage situationImage = (SituationImage) findViewById(R.id.homeImageView);
		/* Situation Image will listen for cache changes, which happen on store() call
		 * in this class or when cache is restored from the internal storage.
		 */
		m_observationsCache.setLatestObservationCacheChangeListener(situationImage);
		/* Before calling onResume on download manager */
		m_observationsCache.restoreFromStorage(this);
		
		mRestoreFromInternalStorage();
		mSettings = new Settings(this);
		mMenuActionsManager = null;
		mSwipeHintCount = mTapOnMarkerHintCount = 0;
		mCurrentLocation = null;

		/* set html text on Radar info text view */
		TextView radarInfoTextView = (TextView)findViewById(R.id.radarInfoTextView);
		radarInfoTextView.setText(Html.fromHtml(getResources().getString(R.string.radar_info)));
	}	

	@Override
	public void networkStatusChanged(boolean online) {
		// TODO Auto-generated method stub
		TitlebarUpdater titlebarUpdater = new TitlebarUpdater();
		titlebarUpdater.update(this);
		titlebarUpdater = null;
		
		if(!online)
			Toast.makeText(this, R.string.netUnavailableToast, Toast.LENGTH_LONG).show();
		else
		{
			CurrentViewUpdater currenvViewUpdater = new CurrentViewUpdater();
			currenvViewUpdater.update(this);
			currenvViewUpdater = null;
			
			/* update locality if Location is available */
			if(mCurrentLocation != null)
			{
				GeocodeAddressTask geocodeAddressTask = new GeocodeAddressTask(this.getApplicationContext(), this);
				geocodeAddressTask.parallelExecute(mCurrentLocation);
			}
		}
	}

	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		SnapshotManager snapManager = new SnapshotManager();
		snapManager.save(outState, this);
		snapManager = null;
		mToggleButtonGroupHelper.saveButtonsState(outState);
	}

	protected void onRestoreInstanceState(Bundle inState)
	{
		super.onRestoreInstanceState(inState);
		/* restores from the bundle if the bundle creation timestamp is reasonably
		 * close to current timestamp
		 */
		SnapshotManager snapManager = new SnapshotManager();
		snapManager.restore(inState, this);
		snapManager = null;
		mToggleButtonGroupHelper.restoreButtonsState(inState);
	}

	public void getSituation()
	{
		m_downloadManager.getSituation();
	}

	public void getTodayForecast()
	{
		m_downloadManager.getTodayForecast();
	}

	public void getTomorrowForecast()
	{
		m_downloadManager.getTomorrowForecast();
	}

	public void getTwoDaysForecast()
	{
		m_downloadManager.getTwoDaysForecast();
	}

	void radar()
	{
		m_downloadManager.getRadarImage();
	}

	void satellite()
	{
		//TextView textView = (TextView) findViewById(R.id.mainTextView);
	}

	void webcams()
	{
		/* The download manager checks whether the webcam data is old or not.
		 * If it is old, new data will be downloaded. If not, it does nothing.
		 * If data is not old, we can populate the webcam overlay on the map
		 * with the cached data.
		 * 
		 * 
		 */
		WebcamDataCache webcamData = WebcamDataCache.getInstance();
		if(!webcamData.dataIsTooOld())
		{
			/* false: do not save on cache because we are already reusing cached data */
			WebcamMapUpdater webcamUpdater = new WebcamMapUpdater();
			webcamUpdater.update(this, webcamData.getFromCache(StringType.WEBCAMLIST_OSMER), StringType.WEBCAMLIST_OSMER, false);
			webcamUpdater.update(this, webcamData.getFromCache(StringType.WEBCAMLIST_OTHER), StringType.WEBCAMLIST_OTHER, false);
			webcamUpdater = null;
		}
		m_downloadManager.getWebcamList();
	}

	@Override
	public void onDownloadProgressUpdate(int step, int total)
	{
		TitlebarUpdater tbu = new TitlebarUpdater();
		tbu.update(this);
		tbu = null;
		setProgress((int) (ProgressBarParams.MAX_PB_VALUE * step /  total));
	}

	@Override
	public void onTextUpdate(String txt, StringType t)
	{
		switch(t)
		{
		case HOME: case TODAY: case TOMORROW: case TWODAYS:
			TextViewUpdater textViewUpdater = new TextViewUpdater();
			textViewUpdater.update(this, txt, t);
			textViewUpdater = null;
			break;
		case WEBCAMLIST_OSMER:
		case WEBCAMLIST_OTHER:
			/* true: save data on cache: onTextUpdate is called after a Download Task, so the data
			 * passed to WebcamMapUpdater is fresh: store it into cache.
			 */
			WebcamMapUpdater webcamUpdater = new WebcamMapUpdater();
			webcamUpdater.update(this, txt, t, true);
			webcamUpdater = null;
			break;
		default:
			/* situation image will be updated directly by the cache, since SituationImage is 
			 * listening to the ObservationCache through the LatestObservationCacheChangeListener
			 * interface.
			 */
			m_observationsCache.store(txt, t);
			break;
		}
	}

	@Override
	public void onDownloadStart(DownloadReason reason)
	{
		if(reason == DownloadReason.Init)
			Toast.makeText(this, R.string.downloadingToast, Toast.LENGTH_SHORT).show();
		else if(reason == DownloadReason.Incomplete)
			Toast.makeText(this, R.string.completingDownloadToast, Toast.LENGTH_SHORT).show();
		else if(reason == DownloadReason.DataExpired)
			Toast.makeText(this, R.string.dataExpiredToast, Toast.LENGTH_SHORT).show();

		setProgressBarVisibility(true);
		ProgressBarParams.currentValue = 0;
		setProgress(0);
	}

	@Override
	public void onTextUpdateError(StringType t, String errorMessage)
	{
		InfoHtmlBuilder infoHtmlBuilder = new InfoHtmlBuilder();
		String text = infoHtmlBuilder.wrapErrorIntoHtml(errorMessage, getResources());
		TextViewUpdater tvu = new TextViewUpdater();
		tvu.update(this, text, t);
		infoHtmlBuilder = null;
		tvu = null;
		NetworkGuiErrorManager ngem = new NetworkGuiErrorManager();
		ngem.onError(this, errorMessage);
		ngem = null;
	}

	@Override
	public void onBitmapUpdate(Bitmap bmp, BitmapType bType)
	{
		ImageViewUpdater imageViewUpdater = new ImageViewUpdater();
		imageViewUpdater.update(this, bmp, bType);
		imageViewUpdater = null;
	}

	@Override
	public void onBitmapUpdateError(BitmapType bType, String errorMessage)
	{
		NetworkGuiErrorManager ngem = new NetworkGuiErrorManager();
		ngem.onError(this, errorMessage);
		ngem = null;
	}

	@Override
	public void onStateChanged(long previousState, long state) 
	{
		if(((state & DownloadStatus.WEBCAM_OSMER_DOWNLOADED) != 0) &&  ((state & DownloadStatus.WEBCAM_OTHER_DOWNLOADED) != 0 ))
			Toast.makeText(getApplicationContext(), R.string.webcam_lists_downloaded, Toast.LENGTH_SHORT).show();

	}
	/* executed when a new locality / address becomes available.
	 */
	public void onGeocodeAddressUpdate(LocationInfo locInfo)
	{
		if(locInfo.error.isEmpty())
		{
			((ODoubleLayerImageView) findViewById(R.id.homeImageView)).onLocalityChanged(locInfo.locality, locInfo.subLocality, locInfo.address);
			((ODoubleLayerImageView) findViewById(R.id.todayImageView)).onLocalityChanged(locInfo.locality, locInfo.subLocality, locInfo.address);
			((ODoubleLayerImageView) findViewById(R.id.tomorrowImageView)).onLocalityChanged(locInfo.locality, locInfo.subLocality, locInfo.address);
			((ODoubleLayerImageView) findViewById(R.id.twoDaysImageView)).onLocalityChanged(locInfo.locality, locInfo.subLocality, locInfo.address);
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		LocationComparer locationComparer = new LocationComparer();
		if(locationComparer.isBetterLocation(location, mCurrentLocation))
		{	
			((ODoubleLayerImageView) findViewById(R.id.homeImageView)).onLocationChanged(location);
			((ODoubleLayerImageView) findViewById(R.id.todayImageView)).onLocationChanged(location);
			((ODoubleLayerImageView) findViewById(R.id.tomorrowImageView)).onLocationChanged(location);
			((ODoubleLayerImageView) findViewById(R.id.twoDaysImageView)).onLocationChanged(location);
			mCurrentLocation = location;
			if(this.m_downloadManager.state().name() == StateName.Online)
			{
				GeocodeAddressTask geocodeAddressTask = new GeocodeAddressTask(this.getApplicationContext(), this);
				geocodeAddressTask.parallelExecute(mCurrentLocation);
			}
		}
		locationComparer = null;
	}

	@Override
	public void onProviderDisabled(String provider) 
	{

	}

	/** 
	 * Called when the provider is enabled by the user.
	 */
	@Override
	public void onProviderEnabled(String provider) 
	{
		/* hack to make MyLocationOverlay fix the location with GPS even when GPS is enabled
		 * after MyLocationOverlay.enableMyLocation was called (problem turned out with Galaxy
		 * S3/Android 4.1)
		 */
		((OMapView) findViewById(R.id.mapview)).onPositionProviderEnabled(provider);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) 
	{

	}

	@Override
	/**
	 * called by textToImage.TextChangeListener when a new text has been set on a text view.
	 * This method, according to the string type (today, tomorrow or 2 days forecast
	 */
	public void onTextDecoded(StringType t, int resId) 
	{
		if(resId != 0)
		{
			//			Log.i("OsmerActivity:onTextDecoded", "got " + resId);
			ToggleButton b = null;
			switch(t)
			{
			case TODAY:
				b = (ToggleButton) findViewById(R.id.buttonToday);
				break;
			case TOMORROW:
				b = (ToggleButton) findViewById(R.id.buttonTomorrow);
				break;
			case TWODAYS:
				b = (ToggleButton) findViewById(R.id.buttonTwoDays);
				break;
			default:
				break;
			}

			b.setBackgroundResource(resId);
		}
	}

	/** Installs listeners for the button click events 
	 * 
	 */
	protected void installButtonListener()
	{
		mToggleButtonGroupHelper = new ToggleButtonGroupHelper(this);
		mInstallButtonListener();
	}

	protected void installOnTouchListener()
	{
		OnTouchListenerInstaller otli = new OnTouchListenerInstaller();
		otli.install(this);
		otli = null;
	}

	private void mRestoreFromInternalStorage() 
	{
		/* save text views */
		OTextView ov = (OTextView) findViewById(R.id.homeTextView);
		ov.restoreFromInternalStorage();
		ov = (OTextView) findViewById(R.id.todayTextView);
		ov.restoreFromInternalStorage();
		ov = (OTextView) findViewById(R.id.tomorrowTextView);
		ov.restoreFromInternalStorage();
		ov = (OTextView) findViewById(R.id.twoDaysTextView);
		ov.restoreFromInternalStorage();
		
		/* save images */
		ODoubleLayerImageView dliv = (ODoubleLayerImageView) findViewById(R.id.homeImageView);
		dliv.restoreFromInternalStorage();
		dliv = (ODoubleLayerImageView) findViewById(R.id.todayImageView);
		dliv.restoreFromInternalStorage();
		dliv = (ODoubleLayerImageView) findViewById(R.id.tomorrowImageView);
		dliv.restoreFromInternalStorage();
		dliv = (ODoubleLayerImageView) findViewById(R.id.twoDaysImageView);
		dliv.restoreFromInternalStorage();
	}
	
	void executeObservationTypeSelectionDialog(ObservationTime oTime)
	{
		ObservationTypeGetter oTypeGetter = new ObservationTypeGetter();
		oTypeGetter.get(this, oTime, -1);
		oTypeGetter = null;
	}


	@Override
	public void onSatelliteEnabled(boolean en) 
	{
	}

	@Override
	public void onMeasureEnabled(boolean en) {
		ToggleButton b = (ToggleButton) findViewById(R.id.measureToggleButton);
		b.setChecked(en);
	}

	public void onSelectionDone(ObservationType type, ObservationTime oTime) 
	{
		/* switch the working mode of the map view */
		OMapView map = (OMapView) findViewById(R.id.mapview);
		map.setMode(new MapViewMode(type, oTime));
		if(type != ObservationType.WEBCAM)
			map.updateObservations(m_observationsCache.getObservationData(oTime));
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Button b = (Button) v;
		OViewFlipper viewFlipper = (OViewFlipper) this.findViewById(R.id.viewFlipper1);
		viewFlipper.setOutAnimation(null);
		viewFlipper.setInAnimation(null);

		ViewFlipper buttonsFlipper = (ViewFlipper) findViewById(R.id.buttonsFlipper);

		//if(!mToggleButtonGroupHelper.isOn(v.getId()))
		{
			/* if not already checked, changed flipper page */
			/* change flipper child. Title is updated by the FlipperChildChangeListener */
			switch(b.getId())
			{
			case R.id.buttonHome:
				viewFlipper.setDisplayedChild(FlipperChildren.HOME);
				buttonsFlipper.setDisplayedChild(0);
				/* set checked  the clicked button */
				mToggleButtonGroupHelper.setClicked(b);
				break;
			case R.id.buttonToday:
				viewFlipper.setDisplayedChild(FlipperChildren.TODAY);
				mToggleButtonGroupHelper.setClicked(b);
				break;
			case R.id.buttonTomorrow:
				viewFlipper.setDisplayedChild(FlipperChildren.TOMORROW);
				mToggleButtonGroupHelper.setClicked(b);
				break;
			case R.id.buttonTwoDays:
				viewFlipper.setDisplayedChild(FlipperChildren.TWODAYS);
				mToggleButtonGroupHelper.setClicked(b);
				break;
			case R.id.buttonMap:
			case R.id.buttonMapInsideDaily:
				((ToggleButton)findViewById(R.id.measureToggleButton)).setChecked(false);
				/* remove itemized overlays (observations), if present, and restore radar view */
				((OMapView) findViewById(R.id.mapview)).setMode(new MapViewMode(ObservationType.RADAR, ObservationTime.DAILY));
				viewFlipper.setDisplayedChild(FlipperChildren.MAP);
				buttonsFlipper.setDisplayedChild(1);
				//				/* do not set the map button clicked, but the radar one 
				//				 * because the buttonsFlipper child has changed.
				//				 */
				//				mToggleButtonGroupHelper.setClicked(findViewById(R.id.buttonRadar));
				break;
			case R.id.buttonRadar:
				mToggleButtonGroupHelper.setClicked(findViewById(R.id.buttonRadar));
				break;
			case R.id.buttonMapInsideLatest:
				((ToggleButton)findViewById(R.id.measureToggleButton)).setChecked(false);

				/* remove itemized overlays (observations), if present, and restore radar view */
				((OMapView) findViewById(R.id.mapview)).setMode(new MapViewMode(ObservationType.RADAR, ObservationTime.LATEST));

				viewFlipper.setDisplayedChild(FlipperChildren.MAP);
				buttonsFlipper.setDisplayedChild(1);
				//				/* do not set the map button clicked, but the radar one 
				//				 * because the buttonsFlipper child has changed.
				//				 */
				//				mToggleButtonGroupHelper.setClicked(findViewById(R.id.buttonRadar));
				break;
			case R.id.measureToggleButton:
			case R.id.satelliteViewButton:
				/* no call to mToggleButtonGroupHelper.setClicked(b); */
				break;
			case R.id.radarInfoButton:
				ToggleButton radarInfoB = (ToggleButton) v;
				View radarInfoTextView = findViewById(R.id.radarInfoTextView);
				if(radarInfoB.isChecked())
					radarInfoTextView.setVisibility(View.VISIBLE);
				else
					radarInfoTextView.setVisibility(View.GONE);
				break;

			case R.id.centerMapButton:
				((OMapView) findViewById(R.id.mapview)).centerMap();
				break;
			default:
				mToggleButtonGroupHelper.setClicked(b);
				break;
			}

			/* hints on buttons */
			switch(b.getId())
			{
			case R.id.buttonHome:
			case R.id.buttonToday:
			case R.id.buttonTomorrow:	
			case R.id.buttonTwoDays:
				if(mSettings.isSwipeHintEnabled() && mSwipeHintCount == 0)
				{
					Toast.makeText(this, R.string.hint_swipe, Toast.LENGTH_LONG).show();
					mSwipeHintCount++; /* don't be silly, just once per start */
				}
				break;

			case R.id.buttonDailySky:
			case R.id.buttonHumMean:
			case R.id.buttonWMax:
			case R.id.buttonWMean:
			case R.id.buttonDailyRain:	
			case R.id.buttonTMin:	
			case R.id.buttonTMax:
			case R.id.buttonLatestSky:
			case R.id.buttonHumidity:
			case R.id.buttonWind:
			case R.id.buttonPressure:
			case R.id.buttonLatestRain:	
			case R.id.buttonSnow:	
			case R.id.buttonTemp:
				if(mSettings.isObsScrollIconsHintEnabled())
				{
					/* this is shown just once */
					Toast.makeText(this, R.string.hint_scroll_buttons, Toast.LENGTH_LONG).show();
					mSettings.setObsScrollIconsHintEnabled(false);
				}

				break;
			default:
				break;	
			}
		}


		/* buttons that can have effect even offline */
		switch(b.getId())
		{
		case R.id.buttonDailySky:
			onSelectionDone(ObservationType.SKY, ObservationTime.DAILY);
			break;
		case R.id.buttonHumMean:
			onSelectionDone(ObservationType.MEAN_HUMIDITY, ObservationTime.DAILY);
			break;
		case R.id.buttonWMax:
			onSelectionDone(ObservationType.MAX_WIND, ObservationTime.DAILY);
			break;
		case R.id.buttonWMean:
			onSelectionDone(ObservationType.MEAN_WIND, ObservationTime.DAILY);
			break;
		case R.id.buttonDailyRain:
			onSelectionDone(ObservationType.RAIN, ObservationTime.DAILY);
			break;
		case R.id.buttonTMin:
			onSelectionDone(ObservationType.MIN_TEMP, ObservationTime.DAILY);
			break;
		case R.id.buttonTMean:
			onSelectionDone(ObservationType.MEAN_TEMP, ObservationTime.DAILY);
			break;
		case R.id.buttonTMax:
			onSelectionDone(ObservationType.MAX_TEMP, ObservationTime.DAILY);
			break;

			/* latest */
		case R.id.buttonLatestSky:
			onSelectionDone(ObservationType.SKY, ObservationTime.LATEST);
			break;
		case R.id.buttonHumidity:
			onSelectionDone(ObservationType.HUMIDITY, ObservationTime.LATEST);
			break;
		case R.id.buttonWind:
			onSelectionDone(ObservationType.WIND, ObservationTime.LATEST);
			break;
		case R.id.buttonPressure:
			onSelectionDone(ObservationType.PRESSURE, ObservationTime.LATEST);
			break;
		case R.id.buttonLatestRain:
			onSelectionDone(ObservationType.RAIN, ObservationTime.LATEST);
			break;
		case R.id.buttonSea:
			onSelectionDone(ObservationType.SEA, ObservationTime.LATEST);
			break;
		case R.id.buttonSnow:
			onSelectionDone(ObservationType.SNOW, ObservationTime.LATEST);
			break;
		case R.id.buttonTemp:
			onSelectionDone(ObservationType.TEMP, ObservationTime.LATEST);
			break;

			/* webcam */
		case R.id.buttonWebcam:
			onSelectionDone(ObservationType.WEBCAM, ObservationTime.WEBCAM);
			break;

			/* satellite or map on MapView */
		case R.id.satelliteViewButton:
			OMapView omv = (OMapView) findViewById(R.id.mapview);
			omv.setSatellite(((ToggleButton)b).isChecked());
			break;

		case R.id.measureToggleButton:
			OMapView omv1 = (OMapView) findViewById(R.id.mapview);
			boolean buttonChecked = ((ToggleButton)b).isChecked();

			if(buttonChecked && mSettings.isMapMoveToMeasureHintEnabled())
			{
				Toast.makeText(this, R.string.hint_move_to_measure_on_map, Toast.LENGTH_LONG).show();
			}
			else if(buttonChecked && !mSettings.isMapMoveToMeasureHintEnabled() 
					&& mSettings.isMapMoveLocationToMeasureHintEnabled())
			{
				Toast.makeText(this, R.string.hint_move_location_to_measure_on_map, Toast.LENGTH_LONG).show();
			}
			omv1.setMeasureEnabled(buttonChecked);
			break;	
		}

		/* 
		 * FIXME 
		 * daily and latest observations are cached in the ObservationCache, so it is 
		 * not compulsory to be online to show them.
		 */
		if(b.getId() == R.id.buttonDailyObs)
		{
			buttonsFlipper.setDisplayedChild(2);
			onSelectionDone(ObservationType.SKY, ObservationTime.DAILY);
			mToggleButtonGroupHelper.setClicked(findViewById(R.id.buttonDailySky));
			if(mSettings.isMapMarkerHintEnabled() && mTapOnMarkerHintCount == 0)
			{
				Toast.makeText(this, R.string.hint_tap_on_map_markers, Toast.LENGTH_LONG).show();
				mTapOnMarkerHintCount++; /* do not be too annoying */
			}
		}
		else if(b.getId() == R.id.buttonLastObs)
		{
			buttonsFlipper.setDisplayedChild(3);
			onSelectionDone(ObservationType.SKY, ObservationTime.LATEST);
			mToggleButtonGroupHelper.setClicked(findViewById(R.id.buttonLatestSky));
		}
		/* try to download only if online */
		else if(m_downloadManager.state().name() == StateName.Online)
		{
			if(b.getId() ==  R.id.buttonMap || b.getId() == R.id.buttonMapInsideDaily
					|| b.getId() == R.id.buttonMapInsideLatest)
			{
				//		int id = mToggleButtonGroupHelper.buttonOn();
				//	if(id == R.id.buttonRadar)
				radar();
			}
			else if(b.getId() == R.id.buttonRadar)
			{
				/* remove itemized overlays (observations), if present, and restore radar view */
				((OMapView) findViewById(R.id.mapview)).setMode(new MapViewMode(ObservationType.RADAR, ObservationTime.DAILY));
				radar();
			}
			else if(b.getId() == R.id.buttonToday)
			{
				getTodayForecast();
			}
			else if(b.getId() == R.id.buttonTomorrow)
			{
				getTomorrowForecast();
			}
			else if(b.getId() == R.id.buttonTwoDays)
			{
				getTwoDaysForecast();
			}
			else if(b.getId() == R.id.buttonHome)
			{

			}
			else if(b.getId() == R.id.buttonWebcam)
			{
				webcams();
			}
		}

		/* hide measure and radar button from the map view when not in
		 * radar mode
		 */
		View measureButton =  findViewById(R.id.measureToggleButton);
		ToggleButton radarInfoButton = (ToggleButton) findViewById(R.id.radarInfoButton);
		if(mToggleButtonGroupHelper.isOn(R.id.buttonRadar))
		{
			measureButton.setVisibility(View.VISIBLE);
			radarInfoButton.setVisibility(View.VISIBLE);
		}
		else
		{
			radarInfoButton.setChecked(false);
			radarInfoButton.setVisibility(View.GONE);
			measureButton.setVisibility(View.GONE);
			/* always hide radar info text view from map when not in radar mode */
			findViewById(R.id.radarInfoTextView).setVisibility(View.GONE);
		}

		TitlebarUpdater titleUpdater = new TitlebarUpdater();
		titleUpdater.update(this);
		titleUpdater = null;
	}

	public DownloadManager stateMachine() { return m_downloadManager; }

	public ToggleButtonGroupHelper getToggleButtonGroupHelper() 
	{
		return mToggleButtonGroupHelper;
	}

	@Override
	public void onFlipperChildChangeEvent(int child) {

		TitlebarUpdater titleUpdater = new TitlebarUpdater();
		titleUpdater.update(this);
		titleUpdater = null;
		
		switch(child)
		{
		case FlipperChildren.HOME:
			mToggleButtonGroupHelper.setClicked(findViewById(R.id.buttonHome));
			break;
		case FlipperChildren.TODAY:
			mToggleButtonGroupHelper.setClicked(findViewById(R.id.buttonToday));
			break;
		case FlipperChildren.TOMORROW:
			mToggleButtonGroupHelper.setClicked(findViewById(R.id.buttonTomorrow));
			break;
		case FlipperChildren.TWODAYS:
			mToggleButtonGroupHelper.setClicked(findViewById(R.id.buttonTwoDays));
			break;
		case FlipperChildren.MAP:
			ViewFlipper buttonsFlipper = (ViewFlipper) findViewById(R.id.buttonsFlipper);
			buttonsFlipper.setDisplayedChild(1);
			mToggleButtonGroupHelper.setClicked(findViewById(R.id.buttonRadar));
			break;
		default:
			break;
		}
	}

	public Location getCurrentLocation()
	{
		return mCurrentLocation;
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}



	private void mInstallButtonListener() 
	{
		ToggleButton buttonHome = (ToggleButton)findViewById(R.id.buttonHome);
		mToggleButtonGroupHelper.addButton(R.id.buttonHome);
		buttonHome.setOnClickListener(this);

		ToggleButton buttonToday = (ToggleButton)findViewById(R.id.buttonToday);
		mToggleButtonGroupHelper.addButton(R.id.buttonToday);
		buttonToday.setOnClickListener(this);

		ToggleButton buttonTomorrow = (ToggleButton)findViewById(R.id.buttonTomorrow);
		mToggleButtonGroupHelper.addButton(R.id.buttonTomorrow);
		buttonTomorrow.setOnClickListener(this);

		ToggleButton buttonTwoDays = (ToggleButton)findViewById(R.id.buttonTwoDays);
		mToggleButtonGroupHelper.addButton(R.id.buttonTwoDays);
		buttonTwoDays.setOnClickListener(this);

		ToggleButton buttonMap = (ToggleButton)findViewById(R.id.buttonMap);
		buttonMap.setOnClickListener(this);
		mToggleButtonGroupHelper.addButton(R.id.buttonMap);
		
		/* switches between map and satellite view on MapView.
		 * measure mode
		 * radar info button
		 * do not add to ToggleButtonGroupHelper
		 */
		ToggleButton satelliteViewButtonOnMap = (ToggleButton) findViewById(R.id.satelliteViewButton);
		satelliteViewButtonOnMap.setOnClickListener(this);

		ToggleButton measureMode = (ToggleButton) findViewById(R.id.measureToggleButton);
		measureMode.setOnClickListener(this);
		
		ToggleButton buttonInfoRadar = (ToggleButton) findViewById(R.id.radarInfoButton);
		buttonInfoRadar.setOnClickListener(this);		
		
		ToggleButton radarButton = (ToggleButton) findViewById(R.id.buttonRadar);
		radarButton.setOnClickListener(this);
		mToggleButtonGroupHelper.addButton(R.id.buttonRadar);
		
		ToggleButton dailyObsButton = (ToggleButton) findViewById(R.id.buttonDailyObs);
		dailyObsButton.setOnClickListener(this);
		mToggleButtonGroupHelper.addButton(R.id.buttonDailyObs);
		ToggleButton lastObsButton = (ToggleButton) findViewById(R.id.buttonLastObs);
		lastObsButton.setOnClickListener(this);
		mToggleButtonGroupHelper.addButton(R.id.buttonLastObs);

		/* daily and latest observations button */
		ToggleButton dailySkyButton = (ToggleButton) findViewById(R.id.buttonDailySky);
		dailySkyButton.setOnClickListener(this);
		mToggleButtonGroupHelper.addButton(R.id.buttonDailySky);

		ToggleButton buttonHumidity = (ToggleButton) findViewById(R.id.buttonHumidity);
		buttonHumidity.setOnClickListener(this);
		mToggleButtonGroupHelper.addButton(R.id.buttonHumidity);

		ToggleButton buttonHumidityMean = (ToggleButton) findViewById(R.id.buttonHumMean);
		buttonHumidityMean.setOnClickListener(this);
		mToggleButtonGroupHelper.addButton(R.id.buttonHumMean);

		ToggleButton buttonLatestSky = (ToggleButton) findViewById(R.id.buttonLatestSky);
		buttonLatestSky.setOnClickListener(this);
		mToggleButtonGroupHelper.addButton(R.id.buttonLatestSky);

		ToggleButton buttonPressure = (ToggleButton) findViewById(R.id.buttonPressure);
		buttonPressure.setOnClickListener(this);
		mToggleButtonGroupHelper.addButton(R.id.buttonPressure);

		ToggleButton buttonDailyRain = (ToggleButton) findViewById(R.id.buttonDailyRain);
		buttonDailyRain.setOnClickListener(this);
		mToggleButtonGroupHelper.addButton(R.id.buttonDailyRain);

		ToggleButton buttonSea = (ToggleButton) findViewById(R.id.buttonSea);
		buttonSea.setOnClickListener(this);
		mToggleButtonGroupHelper.addButton(R.id.buttonSea);

		ToggleButton buttonSnow = (ToggleButton) findViewById(R.id.buttonSnow);
		buttonSnow.setOnClickListener(this);
		mToggleButtonGroupHelper.addButton(R.id.buttonSnow);


		ToggleButton buttonTemp = (ToggleButton) findViewById(R.id.buttonTemp);
		buttonTemp.setOnClickListener(this);
		mToggleButtonGroupHelper.addButton(R.id.buttonTemp);


		ToggleButton buttonTMax = (ToggleButton) findViewById(R.id.buttonTMax);
		buttonTMax.setOnClickListener(this);
		mToggleButtonGroupHelper.addButton(R.id.buttonTMax);


		ToggleButton buttonTMean = (ToggleButton) findViewById(R.id.buttonTMean);
		buttonTMean.setOnClickListener(this);
		mToggleButtonGroupHelper.addButton(R.id.buttonTMean);


		ToggleButton buttonTMin = (ToggleButton) findViewById(R.id.buttonTMin);
		buttonTMin.setOnClickListener(this);
		mToggleButtonGroupHelper.addButton(R.id.buttonTMin);


		ToggleButton buttonWind = (ToggleButton) findViewById(R.id.buttonWind);
		buttonWind.setOnClickListener(this);
		mToggleButtonGroupHelper.addButton(R.id.buttonWind);


		ToggleButton buttonWMax = (ToggleButton) findViewById(R.id.buttonWMax);
		buttonWMax.setOnClickListener(this);
		mToggleButtonGroupHelper.addButton(R.id.buttonWMax);


		ToggleButton buttonWMean = (ToggleButton) findViewById(R.id.buttonWMean);
		buttonWMean.setOnClickListener(this);
		mToggleButtonGroupHelper.addButton(R.id.buttonWMean);


		ToggleButton buttonLatestRain = (ToggleButton) findViewById(R.id.buttonLatestRain);
		buttonLatestRain.setOnClickListener(this);
		mToggleButtonGroupHelper.addButton(R.id.buttonLatestRain);


		ToggleButton buttonMapInsideDaily = (ToggleButton) findViewById(R.id.buttonMapInsideDaily);
		buttonMapInsideDaily.setOnClickListener(this);
		mToggleButtonGroupHelper.addButton(R.id.buttonMapInsideDaily);


		ToggleButton buttonMapInsideLatest = (ToggleButton) findViewById(R.id.buttonMapInsideLatest);
		buttonMapInsideLatest.setOnClickListener(this);
		mToggleButtonGroupHelper.addButton(R.id.buttonMapInsideLatest);
		
		ToggleButton buttonWebcam = (ToggleButton) findViewById(R.id.buttonWebcam);
		buttonWebcam.setOnClickListener(this);
		mToggleButtonGroupHelper.addButton(R.id.buttonWebcam);
		
		/* center map button (v. 1.1.5) */
		findViewById(R.id.centerMapButton).setOnClickListener(this);	
	}
	
	/* private members */
	private DownloadManager m_downloadManager;
	private LocationManager m_locationManager; 
	private Location mCurrentLocation;
	private ObservationsCache m_observationsCache;
	private MenuActionsManager mMenuActionsManager;
	ToggleButtonGroupHelper mToggleButtonGroupHelper;
	private Settings mSettings;
	private int mSwipeHintCount, mTapOnMarkerHintCount;


	Urls m_urls;

	/// temp
	int availCnt = 0;

}
