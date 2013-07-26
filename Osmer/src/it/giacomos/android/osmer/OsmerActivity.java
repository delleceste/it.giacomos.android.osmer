package it.giacomos.android.osmer;
import com.google.android.maps.MapActivity;

import it.giacomos.android.osmer.downloadManager.DownloadManager;
import it.giacomos.android.osmer.downloadManager.DownloadReason;
import it.giacomos.android.osmer.downloadManager.DownloadStatus;
import it.giacomos.android.osmer.downloadManager.state.StateName;
import it.giacomos.android.osmer.downloadManager.state.Urls;
import it.giacomos.android.osmer.guiHelpers.ActionBarPersonalizer;
import it.giacomos.android.osmer.guiHelpers.ActionBarStateManager;
import it.giacomos.android.osmer.guiHelpers.ImageViewUpdater;
import it.giacomos.android.osmer.guiHelpers.MenuActionsManager;
import it.giacomos.android.osmer.guiHelpers.NetworkGuiErrorManager;
import it.giacomos.android.osmer.guiHelpers.ObservationTypeGetter;
import it.giacomos.android.osmer.guiHelpers.OnTouchListenerInstaller;
import it.giacomos.android.osmer.guiHelpers.TextViewUpdater;
import it.giacomos.android.osmer.guiHelpers.TitlebarUpdater;
import it.giacomos.android.osmer.guiHelpers.WebcamMapUpdater;
import it.giacomos.android.osmer.guiHelpers.DrawerItemClickListener;
import it.giacomos.android.osmer.instanceSnapshotManager.SnapshotManager;
import it.giacomos.android.osmer.locationUtils.Constants;
import it.giacomos.android.osmer.locationUtils.GeocodeAddressTask;
import it.giacomos.android.osmer.locationUtils.GeocodeAddressUpdateListener;
import it.giacomos.android.osmer.locationUtils.LocationComparer;
import it.giacomos.android.osmer.locationUtils.LocationInfo;
import it.giacomos.android.osmer.observations.ObservationTime;
import it.giacomos.android.osmer.observations.ObservationType;
import it.giacomos.android.osmer.observations.ObservationsCache;
import it.giacomos.android.osmer.webcams.WebcamDataCache;
import it.giacomos.android.osmer.widgets.AnimatedImageView;
import it.giacomos.android.osmer.widgets.InfoHtmlBuilder;
import it.giacomos.android.osmer.widgets.ODoubleLayerImageView;
import it.giacomos.android.osmer.widgets.OTextView;
import it.giacomos.android.osmer.widgets.OViewFlipper;
import it.giacomos.android.osmer.widgets.map.MapViewMode;
import it.giacomos.android.osmer.widgets.map.OMapFragment;
import it.giacomos.android.osmer.widgets.SituationImage;
import it.giacomos.android.osmer.preferences.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnDismissListener;
import android.widget.PopupMenu.OnMenuItemClickListener;
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
public class OsmerActivity extends Activity 
implements OnClickListener, 
DownloadUpdateListener,
FlipperChildChangeListener,
SnapshotManagerListener,
LocationListener,
GeocodeAddressUpdateListener,
OnMenuItemClickListener, 
OnDismissListener,
MapFragmentListener
{
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//		Log.i("onCreate", "application created");
		super.onCreate(savedInstanceState);
//		Log.e("Activity onCreate ", "CREATED");

		requestWindowFeature(Window.FEATURE_PROGRESS);
		this.setProgressBarVisibility(true);

		setContentView(R.layout.main);
//		Object test=R.styleable; 
		init();
	}

	public void onResume()
	{	
//		Log.e("onResume ", "application resumed");
		super.onResume();

		/* registers network status monitor broadcast receiver (for this it needs `this')
		 * Must be called _after_ instance restorer's onResume!
		 */
		m_downloadManager.onResume(this);		

		/* Location Manager */
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
			}
			catch(IllegalArgumentException iae)
			{
				Log.e("OsmerActivity: onResume: requestLocationUpdates", iae.getLocalizedMessage());
			}
		}

		/* create WebcamDataCache with the Context */
		WebcamDataCache.getInstance(this.getCacheDir());
	}

	public void onPause()
	{
		super.onPause();
		/* unregisters network status monitor broadcast receiver (for this it needs `this')
		 */
		m_downloadManager.onPause(this);
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

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public void onStop()
	{
		/* From Android documentation:
		 * Note that this method may never be called, in low memory situations where 
		 * the system does not have enough memory to keep your activity's process running 
		 * after its onPause() method is called. 
		 */
		super.onStop();
	}

	protected void onDestroy()
	{
		((SituationImage) findViewById(R.id.homeImageView)).unbindDrawables();
		/* clear HashMaps and bitmaps in hash values */
		((SituationImage) findViewById(R.id.homeImageView)).cleanup();
		/* ODoubleLayerImageView */
		((ODoubleLayerImageView) findViewById(R.id.todayImageView)).unbindDrawables();
		((ODoubleLayerImageView) findViewById(R.id.tomorrowImageView)).unbindDrawables();
		((ODoubleLayerImageView) findViewById(R.id.twoDaysImageView)).unbindDrawables();
		if(mRefreshAnimatedImageView != null)
			mRefreshAnimatedImageView.hide();
		/* cancel async tasks that may be running when the application is destroyed */
		m_downloadManager.stopPendingTasks();
		super.onDestroy();
	}

	public void onRestart()
	{
//		Log.e("Activity onRestart ", "application restarted");

		super.onRestart();
	}

	public void onStart()
	{
//		Log.e("Activity onStart ", "application started");

		super.onStart();
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) 
	{
		/* save refresh state of the refresh animated circular scrollbar in order to 
		 * restore its state and visibility right after the menu is recreated.
		 */
		boolean refreshWasVisible = (mRefreshAnimatedImageView != null && 
				mRefreshAnimatedImageView.getVisibility() == View.VISIBLE);
		mRefreshAnimatedImageView = null;
		mButtonsActionView = menu.findItem(R.id.actionbarmenu).getActionView();
		mRefreshAnimatedImageView = (AnimatedImageView) mButtonsActionView.findViewById(R.id.refresh_animation);
		if(refreshWasVisible)
			mRefreshAnimatedImageView.start();
		mInitButtonMapsOverflowMenu();
		
		/* set visibility and state on map buttons */
		return super.onPrepareOptionsMenu(menu);
	}

	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) 
	{
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		this.closeOptionsMenu();
		if(mMenuActionsManager == null)
			mMenuActionsManager = new MenuActionsManager(this);

		boolean ret = mMenuActionsManager.itemSelected(item);
		return ret;
	}

	@Override
	public void onBackPressed()
	{
		/* first of all, if there's a info window on the map, close it */
		int displayedChild = ((ViewFlipper) findViewById(R.id.viewFlipper1)).getDisplayedChild();
		OMapFragment map = (OMapFragment) getFragmentManager().findFragmentById(R.id.mapview);
		if(displayedChild == 4  &&  map.isInfoWindowVisible()) /* map view visible */
			map.hideInfoWindow();
		else
		{
			switch(displayedChild)
			{
			case 4:
				mActionBarStateManager.backToForecast(mActionBarPersonalizer);
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

	public void onGoogleMapReady()
	{
		
	}
	
	public void init()
	{
		mTapOnMarkerHintCount = 0;
		mRefreshAnimatedImageView = null;

		OMapFragment map = (OMapFragment) getFragmentManager().findFragmentById(R.id.mapview);
		
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

		/* install onTouchListener */
		installOnTouchListener();
		((OViewFlipper) findViewById(R.id.viewFlipper1)).setOnChildPageChangedListener(this);

		mDrawerItems = getResources().getStringArray(R.array.drawer_text_items);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		/* Action bar stuff.  */
		mActionBarPersonalizer = new ActionBarPersonalizer(this);
		mActionBarStateManager = new ActionBarStateManager();
		/* set the state manager on the personalizer. */
		mActionBarPersonalizer.setActionBarStateManager(mActionBarStateManager);
		mActionBarPersonalizer.drawerItemChanged(0);
		// Set the adapter for the list view
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, mDrawerItems));

		// Set the list's click listener
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener(this));
		mTitle = getTitle();
		mDrawerTitle = getResources().getString(R.string.drawer_open);

		DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) 
		{

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) 
			{
				((OViewFlipper) findViewById(R.id.viewFlipper1)).setDrawerVisible(false);
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) 
			{
				/* block view touch event */
				((OViewFlipper) findViewById(R.id.viewFlipper1)).setDrawerVisible(true);
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		};

		// Set the drawer toggle as the DrawerListener
		drawerLayout.setDrawerListener(mDrawerToggle);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		/* Before calling onResume on download manager */
		m_observationsCache.restoreFromStorage(this);

		mRestoreFromInternalStorage();
		mSettings = new Settings(this);
		mMenuActionsManager = null;
		mCurrentLocation = null;

		/* set html text on Radar info text view */
		TextView radarInfoTextView = (TextView)findViewById(R.id.radarInfoTextView);
		radarInfoTextView.setText(Html.fromHtml(getResources().getString(R.string.radar_info)));
		radarInfoTextView.setVisibility(View.GONE);
	}	

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	public void networkStatusChanged(boolean online) {
		// TODO Auto-generated method stub
		TitlebarUpdater titlebarUpdater = new TitlebarUpdater();
		titlebarUpdater.update(this);
		titlebarUpdater = null;

		if(!online)
		{
			Toast.makeText(getApplicationContext(), R.string.netUnavailableToast, Toast.LENGTH_LONG).show();	
		}
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
		/* to restore action bar and displayed mode */
		mActionBarStateManager.saveState(outState);
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
		mActionBarStateManager.restoreState(inState, mActionBarPersonalizer);
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
			webcamUpdater.update(this, webcamData.getFromCache(ViewType.WEBCAMLIST_OSMER), ViewType.WEBCAMLIST_OSMER, false);
			webcamUpdater.update(this, webcamData.getFromCache(ViewType.WEBCAMLIST_OTHER), ViewType.WEBCAMLIST_OTHER, false);
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
		double progressValue = ProgressBarParams.MAX_PB_VALUE * step /  total;
		setProgress((int) progressValue);
		ProgressBarParams.currentValue = progressValue;
		if(mRefreshAnimatedImageView != null && ProgressBarParams.currentValue == ProgressBarParams.MAX_PB_VALUE)
			mRefreshAnimatedImageView.hide(); /* stops and hides */
		else if(mRefreshAnimatedImageView != null)
			mRefreshAnimatedImageView.start();
	}

	@Override
	public void onTextUpdate(String txt, ViewType t)
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
			Toast.makeText(getApplicationContext(), R.string.downloadingToast, Toast.LENGTH_SHORT).show();
		else if(reason == DownloadReason.Incomplete)
			Toast.makeText(getApplicationContext(), R.string.completingDownloadToast, Toast.LENGTH_SHORT).show();
		else if(reason == DownloadReason.DataExpired)
			Toast.makeText(getApplicationContext(), R.string.dataExpiredToast, Toast.LENGTH_SHORT).show();

		setProgressBarVisibility(true);
		ProgressBarParams.currentValue = 0;
		setProgress(0);
		if(mRefreshAnimatedImageView != null)
			mRefreshAnimatedImageView.resetErrorFlag();
	}

	@Override
	public void onTextUpdateError(ViewType t, String errorMessage)
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
//		((OMapFragment) getFragmentManager().findFragmentById(R.id.mapview)).onPositionProviderEnabled(provider);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) 
	{

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

	public void onSelectionDone(ObservationType type, ObservationTime oTime) 
	{
//		Log.e("onSelectionDone", "setting map mode in onSelectionDone " + type + " " + oTime);
		/* switch the working mode of the map view */
		OMapFragment map = (OMapFragment) getFragmentManager().findFragmentById(R.id.mapview);
		map.setMode(new MapViewMode(type, oTime));
		if(type != ObservationType.WEBCAM)
			map.updateObservations(m_observationsCache.getObservationData(oTime));
		((OViewFlipper)findViewById(R.id.viewFlipper1)).setDisplayedChild(FlipperChildren.MAP, false);
	}

	@Override
	public boolean onMenuItemClick(MenuItem menuItem) 
	{
		/* must manually check an unchecked item, and vice versa.
		 * a checkbox or radio button does not change its state automatically.
		 */
		if(menuItem.isCheckable())
			menuItem.setChecked(!menuItem.isChecked());
		OMapFragment omv = (OMapFragment) getFragmentManager().findFragmentById(R.id.mapview);
		switch(menuItem.getItemId())
		{
		case R.id.centerMapButton:
			omv.centerMap();
			break;	
		case R.id.mapNormalViewButton:
			omv.setNormalViewEnabled(menuItem.isChecked());
			break;		
		case R.id.satelliteViewButton:
			omv.setSatEnabled(menuItem.isChecked());
			break;			
		case R.id.terrainViewButton:
			omv.setTerrainEnabled(menuItem.isChecked()); 
			break;
		case R.id.radarInfoButton:
			View radarInfoTextView = findViewById(R.id.radarInfoTextView);
			if(menuItem.isChecked())
				radarInfoTextView.setVisibility(View.VISIBLE);
			else
				radarInfoTextView.setVisibility(View.GONE);
			break;
		case R.id.measureToggleButton:
			if(menuItem.isChecked() && mSettings.isMapMoveToMeasureHintEnabled())
			{
				Toast.makeText(getApplicationContext(), R.string.hint_move_to_measure_on_map, Toast.LENGTH_LONG).show();
			}
			omv.setMeasureEnabled(menuItem.isChecked());
			break;
		default:
				break;
		}
		return false;
	}
	
	@Override
	public void onClick(View v)
	{
		
		switch(v.getId())
		{
		case R.id.actionOverflow:
			ToggleButton buttonMapsOveflowMenu = (ToggleButton) mButtonsActionView.findViewById(R.id.actionOverflow);
			buttonMapsOveflowMenu.setChecked(true);
			mCreateMapOptionsPopupMenu();
			break;
		default:
			break;		
		}
	}

	@Override
	public void onDismiss(PopupMenu popupMenu) 
	{
		/* uncheck button when popup is dismissed */
		ToggleButton buttonMapsOveflowMenu = (ToggleButton) mButtonsActionView.findViewById(R.id.actionOverflow);
		buttonMapsOveflowMenu.setChecked(false);
	} 
	
	/* called by onPrepareOptionsMenu every time the action bar is recreated
	 * @param buttonsActionView the button (view) where the menu is anchored.
	 * 
	 */
	private void mInitButtonMapsOverflowMenu() 
	{
		if(mButtonsActionView == null)
			return;
		
		/* button for maps menu */
		ToggleButton buttonMapsOveflowMenu = (ToggleButton) mButtonsActionView.findViewById(R.id.actionOverflow);
		switch(mCurrentViewType)
		{
		case HOME:
		case TODAY:
		case TOMORROW:
		case TWODAYS:
			if(buttonMapsOveflowMenu != null)
				buttonMapsOveflowMenu.setVisibility(View.GONE);
			break;
		default:
			buttonMapsOveflowMenu.setOnClickListener(this);
			buttonMapsOveflowMenu.setVisibility(View.VISIBLE);
			break;
		}
		
	}
	
	private void mCreateMapOptionsPopupMenu() 
	{
		OMapFragment map = (OMapFragment) getFragmentManager().findFragmentById(R.id.mapview);
		ToggleButton buttonMapsOveflowMenu = (ToggleButton) mButtonsActionView.findViewById(R.id.actionOverflow);
		PopupMenu mapOptionsMenu = new PopupMenu(this, buttonMapsOveflowMenu);
		Menu menu = mapOptionsMenu.getMenu();
		mapOptionsMenu.getMenuInflater().inflate(R.menu.map_options_popup_menu, menu);
		menu.findItem(R.id.measureToggleButton).setVisible(mCurrentViewType == ViewType.RADAR);
		menu.findItem(R.id.radarInfoButton).setVisible(mCurrentViewType == ViewType.RADAR);
		menu.findItem(R.id.measureToggleButton).setChecked(map.isMeasureEnabled());
		menu.findItem(R.id.radarInfoButton).setChecked(findViewById(R.id.radarInfoTextView).getVisibility() == View.VISIBLE);
		
		switch(mCurrentViewType)
		{
		case HOME: case TODAY: case TOMORROW: case TWODAYS:
			menu.findItem(R.id.satelliteViewButton).setVisible(false);
			menu.findItem(R.id.terrainViewButton).setVisible(false);
			menu.findItem(R.id.mapNormalViewButton).setVisible(false);
			menu.findItem(R.id.centerMapButton).setVisible(false);
			break;
		default:
			menu.findItem(R.id.satelliteViewButton).setVisible(true);
			menu.findItem(R.id.terrainViewButton).setVisible(true);
			menu.findItem(R.id.mapNormalViewButton).setVisible(true);
			menu.findItem(R.id.centerMapButton).setVisible(true);
			menu.findItem(R.id.satelliteViewButton).setChecked(map.isSatEnabled());
			menu.findItem(R.id.terrainViewButton).setChecked(map.isTerrainEnabled());
			menu.findItem(R.id.mapNormalViewButton).setChecked(map.isNormalViewEnabled());
			break;
		}
		
		mapOptionsMenu.setOnMenuItemClickListener(this);
		mapOptionsMenu.setOnDismissListener(this);
		mapOptionsMenu.show();
	}
	
	public void switchView(ViewType id) 
	{
		// TODO Auto-generated method stub
		OViewFlipper viewFlipper = (OViewFlipper) this.findViewById(R.id.viewFlipper1);
		viewFlipper.setOutAnimation(null);
		viewFlipper.setInAnimation(null);
		
		mCurrentViewType = id;

		/* if not already checked, changed flipper page */
		/* change flipper child. Title is updated by the FlipperChildChangeListener */
		switch(id)
		{
		case HOME:
			viewFlipper.setDisplayedChild(FlipperChildren.HOME, false);
			/* set checked  the clicked button */
			break;
		case TODAY:
			viewFlipper.setDisplayedChild(FlipperChildren.TODAY, false);
			break;
		case TOMORROW:
			viewFlipper.setDisplayedChild(FlipperChildren.TOMORROW, false);
			break;
		case TWODAYS:
			viewFlipper.setDisplayedChild(FlipperChildren.TWODAYS, false);
			break;
		case MAP:		
		case RADAR:
			/* remove itemized overlays (observations), if present, and restore radar view */
//			Log.e("switchView", "setting map mode in switchView RADAR/DAILY");
			((OMapFragment) getFragmentManager().findFragmentById(R.id.mapview)).setMode(new MapViewMode(ObservationType.RADAR, ObservationTime.DAILY));
			viewFlipper.setDisplayedChild(FlipperChildren.MAP, false);
			break;

		case ACTION_CENTER_MAP:
			((OMapFragment) getFragmentManager().findFragmentById(R.id.mapview)).centerMap();
			break;
		default:
			break;
		}

		/* ViewTypes that can have effect even when offline
		 * daily and latest observations are cached in the ObservationCache, so it is 
		 * not compulsory to be online to show them.
		 */	
		switch(id)
		{
		case DAILY_SKY:
			onSelectionDone(ObservationType.SKY, ObservationTime.DAILY);
			if(mSettings.isMapMarkerHintEnabled() && mTapOnMarkerHintCount == 0)
			{
				Toast.makeText(getApplicationContext(), R.string.hint_tap_on_map_markers, Toast.LENGTH_LONG).show();
				mTapOnMarkerHintCount++; /* do not be too annoying */
			}
			break;
		case DAILY_HUMIDITY:
			onSelectionDone(ObservationType.MEAN_HUMIDITY, ObservationTime.DAILY);
			break;
		case DAILY_WIND_MAX:
			onSelectionDone(ObservationType.MAX_WIND, ObservationTime.DAILY);
			break;
		case DAILY_WIND:
			onSelectionDone(ObservationType.MEAN_WIND, ObservationTime.DAILY);
			break;
		case DAILY_RAIN:
			onSelectionDone(ObservationType.RAIN, ObservationTime.DAILY);
			break;
		case DAILY_MIN_TEMP:
			onSelectionDone(ObservationType.MIN_TEMP, ObservationTime.DAILY);
			break;
		case DAILY_MEAN_TEMP:
			onSelectionDone(ObservationType.MEAN_TEMP, ObservationTime.DAILY);
			break;
		case DAILY_MAX_TEMP:
			onSelectionDone(ObservationType.MAX_TEMP, ObservationTime.DAILY);
			break;

			/* latest */
		case LATEST_SKY:
			onSelectionDone(ObservationType.SKY, ObservationTime.LATEST);
			break;
		case LATEST_HUMIDITY:
			onSelectionDone(ObservationType.HUMIDITY, ObservationTime.LATEST);
			break;
		case LATEST_WIND:
			onSelectionDone(ObservationType.WIND, ObservationTime.LATEST);
			break;
		case LATEST_PRESSURE:
			onSelectionDone(ObservationType.PRESSURE, ObservationTime.LATEST);
			break;
		case LATEST_RAIN:
			onSelectionDone(ObservationType.RAIN, ObservationTime.LATEST);
			break;
		case LATEST_SEA:
			onSelectionDone(ObservationType.SEA, ObservationTime.LATEST);
			break;
		case LATEST_SNOW:
			onSelectionDone(ObservationType.SNOW, ObservationTime.LATEST);
			break;
		case LATEST_TEMP:
			onSelectionDone(ObservationType.TEMP, ObservationTime.LATEST);
			break;

			/* webcam */
		case WEBCAM:
			onSelectionDone(ObservationType.WEBCAM, ObservationTime.WEBCAM);
			break;
		}	
		/* try to download only if online */
		if(m_downloadManager.state().name() == StateName.Online)
		{
			if(id == ViewType.RADAR || id == ViewType.MAP)
			{
				radar(); /* map mode has already been set if type is MAP or RADAR */
			}
			else if(id == ViewType.TODAY)
			{
				getTodayForecast();
			}
			else if(id == ViewType.TOMORROW)
			{
				getTomorrowForecast();
			}
			else if(id == ViewType.TWODAYS)
			{
				getTwoDaysForecast();
			}
			else if(id == ViewType.HOME)
			{

			}
			else if(id == ViewType.WEBCAM)
			{
				webcams();
			}
		}
		
		TitlebarUpdater titleUpdater = new TitlebarUpdater();
		titleUpdater.update(this);
		titleUpdater = null;
		
		/* show or hide maps menu button according to the current view type */
		mInitButtonMapsOverflowMenu();
	}

//	public void updateMapButtonsState()
//	{
//		switch(mCurrentViewType)
//		{
//		case HOME:
//		case TODAY:
//		case TOMORROW:
//		case TWODAYS:
//			mActionBarButtons.hideAllButtons();
//			break;
//		default:
//			mActionBarButtons.setSatViewVisible(true);
//			mActionBarButtons.setCenterMapVisible(true);
//			break;
//		}
//		/* hide measure and radar button from the map view when not in
//		 * radar mode
//		 */
//		mActionBarButtons.setRadarInfoVisible(mCurrentViewType == ViewType.RADAR);
//		mActionBarButtons.setMeasureVisible(mCurrentViewType == ViewType.RADAR);
//
//		/* hide Radar information text view on google map view */
//		View radarInfoTextView = findViewById(R.id.radarInfoTextView);
//		if(mActionBarButtons.radarInfoEnabled())
//			radarInfoTextView.setVisibility(View.VISIBLE);
//		else
//			radarInfoTextView.setVisibility(View.GONE);
//	}
	
	public DownloadManager stateMachine() { return m_downloadManager; }

	@Override
	public void onFlipperChildChangeEvent(int child) {

		OMapFragment map = null;
		MapViewMode mapMode = null;
		TitlebarUpdater titleUpdater = new TitlebarUpdater();
		titleUpdater.update(this);
		titleUpdater = null;
		int index = -1;

		switch(child)
		{
		case FlipperChildren.HOME:
			index = 0;
			mDrawerList.setItemChecked(0, true);
			break;
		case FlipperChildren.TODAY:
			mDrawerList.setItemChecked(0, true);
			index = 1;
			break;
		case FlipperChildren.TOMORROW:
			mDrawerList.setItemChecked(0, true);
			index = 2;
			break;
		case FlipperChildren.TWODAYS:
			mDrawerList.setItemChecked(0, true);
			index = 3;
			break;
		case FlipperChildren.MAP:
			/* map can be in different modes: radar, webcam or observations */
			map = (OMapFragment) getFragmentManager().findFragmentById(R.id.mapview);
			mapMode = map.getMode();
			if(mapMode.currentType == ObservationType.RADAR)
			{
				mDrawerList.setItemChecked(1, true);
			}
			else if(mapMode.currentMode == ObservationTime.DAILY)
			{
				mDrawerList.setItemChecked(2, true);
			}
			else if(mapMode.currentMode == ObservationTime.LATEST)
			{
				mDrawerList.setItemChecked(3, true);
			}
			else if(mapMode.currentType == ObservationType.WEBCAM)
			{
				mDrawerList.setItemChecked(4, true);
			}
			break;
		default:
			break;
		}
		if(index > -1)
			mActionBarPersonalizer.setTabSelected(index);
	}

	public Location getCurrentLocation()
	{
		return mCurrentLocation;
	}

	public  String[] getDrawerItems()
	{
		return mDrawerItems;
	}

	public ListView getDrawerListView()
	{
		return mDrawerList;
	}

	public ActionBarPersonalizer getActionBarPersonalizer()
	{
		return mActionBarPersonalizer;
	}

	public AnimatedImageView getRefreshAnimatedImageView()
	{
		return mRefreshAnimatedImageView;
	}

	/* private members */
	int mTapOnMarkerHintCount;
	private DownloadManager m_downloadManager;
	private LocationManager m_locationManager; 
	private Location mCurrentLocation;
	private ObservationsCache m_observationsCache;
	private MenuActionsManager mMenuActionsManager;
	private Settings mSettings;

	private ListView mDrawerList;
	private String[] mDrawerItems;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mTitle, mDrawerTitle;
	private ActionBarStateManager mActionBarStateManager;
	private ActionBarPersonalizer mActionBarPersonalizer;
	private AnimatedImageView mRefreshAnimatedImageView;
	private ViewType mCurrentViewType;
	/* ActionBar menu button and menu */
	private View mButtonsActionView;
	Urls m_urls;

	int availCnt = 0;
}
