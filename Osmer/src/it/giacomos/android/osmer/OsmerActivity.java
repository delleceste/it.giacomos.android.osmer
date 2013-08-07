package it.giacomos.android.osmer;

import it.giacomos.android.osmer.downloadManager.DownloadManager;
import it.giacomos.android.osmer.downloadManager.DownloadReason;
import it.giacomos.android.osmer.downloadManager.DownloadStatus;
import it.giacomos.android.osmer.downloadManager.Data.DataPool;
import it.giacomos.android.osmer.downloadManager.state.StateName;
import it.giacomos.android.osmer.downloadManager.state.Urls;
import it.giacomos.android.osmer.guiHelpers.ActionBarManager;
import it.giacomos.android.osmer.guiHelpers.MenuActionsManager;
import it.giacomos.android.osmer.guiHelpers.NetworkGuiErrorManager;
import it.giacomos.android.osmer.guiHelpers.ObservationTypeGetter;
import it.giacomos.android.osmer.guiHelpers.RadarImageTimestampTextBuilder;
import it.giacomos.android.osmer.guiHelpers.TitlebarUpdater;
import it.giacomos.android.osmer.guiHelpers.WebcamMapUpdater;
import it.giacomos.android.osmer.guiHelpers.DrawerItemClickListener;
import it.giacomos.android.osmer.instanceSnapshotManager.SnapshotManager;
import it.giacomos.android.osmer.locationUtils.GeocodeAddressTask;
import it.giacomos.android.osmer.locationUtils.GeocodeAddressUpdateListener;
import it.giacomos.android.osmer.locationUtils.LocationInfo;
import it.giacomos.android.osmer.locationUtils.LocationService;
import it.giacomos.android.osmer.observations.ObservationTime;
import it.giacomos.android.osmer.observations.ObservationType;
import it.giacomos.android.osmer.observations.ObservationsCache;
import it.giacomos.android.osmer.webcams.WebcamDataCache;
import it.giacomos.android.osmer.widgets.AnimatedImageView;
import it.giacomos.android.osmer.widgets.OAnimatedTextView;
import it.giacomos.android.osmer.widgets.ODoubleLayerImageView;
import it.giacomos.android.osmer.widgets.map.MapViewMode;
import it.giacomos.android.osmer.widgets.map.OMapFragment;
import it.giacomos.android.osmer.preferences.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
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
import android.widget.LinearLayout;
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
public class OsmerActivity extends FragmentActivity 
implements OnClickListener, 
DownloadStateListener,
SnapshotManagerListener,
OnMenuItemClickListener, 
OnDismissListener,
MapFragmentListener,
RadarOverlayUpdateListener,
DataPoolErrorListener
{
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_PROGRESS);
		this.setProgressBarVisibility(true);

		setContentView(R.layout.main);
		init();
	}

	public void onResume()
	{	
		super.onResume();
		/* registers network status monitor broadcast receiver (for this it needs `this')
		 * Must be called _after_ instance restorer's onResume!
		 */
		m_downloadManager.onResume(this);
		
		/* create WebcamDataCache with the Context */
		WebcamDataCache.getInstance(this.getCacheDir());
	}

	public void onPause()
	{
		super.onPause();
		/* unregisters network status monitor broadcast receiver (for this it needs `this')
		 */
		m_downloadManager.onPause(this);
		LocationService.Instance().disconnect();
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

		mActionBarManager.init(savedInstanceState);
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
		/* drawables are unbinded inside ForecastFragment's onDestroy() */
		if(mRefreshAnimatedImageView != null)
			mRefreshAnimatedImageView.hide();
		/* cancel async tasks that may be running when the application is destroyed */
		m_downloadManager.stopPendingTasks();
		/* save downloaded data to storage */
		DataPool.Instance().store(getApplicationContext());
		super.onDestroy();
	}

	public void onRestart()
	{
		super.onRestart();
	}

	public void onStart()
	{
		super.onStart();
		/* create the location update client and connect it to the location service */
		LocationService locationService = LocationService.Instance();
		locationService.init(this);
		locationService.connect();
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) 
	{
		Log.e("onPrepareOptionsMenu", " action bar navigation mode " + getActionBar().getNavigationMode());
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
		int displayedChild = ((ViewFlipper) findViewById(R.id.viewFlipper)).getDisplayedChild();
		OMapFragment map = (OMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapview);
		if(displayedChild == 1  &&  map.isInfoWindowVisible()) /* map view visible */
			map.hideInfoWindow();
		else if(displayedChild == 1)
		{
			mDrawerList.setItemChecked(0, true);
			mActionBarManager.drawerItemChanged(0);
		}
		else
			super.onBackPressed();
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

	/*
	 * Handle results returned to the FragmentActivity
	 * by Google Play services
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		// Decide what to do based on the original request code
		switch (requestCode) 
		{
		case LocationService.CONNECTION_FAILURE_RESOLUTION_REQUEST :
			/*
			 * If the result code is Activity.RESULT_OK, try
			 * to connect again
			 */
			switch (resultCode) 
			{
			case Activity.RESULT_OK :
				/*
				 * Try the request again
				 */
				break;
			}
		}
	}

	public void onGoogleMapReady()
	{

	}

	public void init()
	{
		mCurrentViewType = ViewType.HOME;
		mViewPager = new ViewPager(this);
		mViewPager.setId(R.id.pager);
		/* Set the number of pages that should be retained to either side of 
		 * the current page in the view hierarchy in an idle state
		 */
		//		mViewPager.setOffscreenPageLimit(3);
		mMainLayout = (LinearLayout) findViewById(R.id.mainLayout);
		mMainLayout.addView(mViewPager);

		mSettings = new Settings(this);
		mTapOnMarkerHintCount = 0;
		mRefreshAnimatedImageView = null;

		OMapFragment map = (OMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapview);

		/* DataPool. Obtain the instance and load data from storage */
		DataPool dataPool = DataPool.Instance();
		dataPool.registerErrorListener(this); /* listen for network errors */
		dataPool.load(getApplicationContext());

		/* download manager */
		m_downloadManager = new DownloadManager(this);
		m_downloadManager.setDownloadListener(dataPool);

		m_observationsCache = new ObservationsCache();

		/* map updates the observation data in ItemizedOverlay when new observations are available
		 *
		 */
		m_observationsCache.installObservationsCacheUpdateListener(map);
		/* observations are updated by the DataPool data center */
		dataPool.registerTextListener(ViewType.DAILY_TABLE, m_observationsCache);
		dataPool.registerTextListener(ViewType.LATEST_TABLE, m_observationsCache);

		mDrawerItems = getResources().getStringArray(R.array.drawer_text_items);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		/* Action bar stuff.  */
		mActionBarManager = new ActionBarManager(this);
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
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) 
			{
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		};

		// Set the drawer toggle as the DrawerListener
		drawerLayout.setDrawerListener(mDrawerToggle);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mActionBarManager.drawerItemChanged(0);

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

			/* trigger an update of the locality if Location is available */
			LocationService locationService = LocationService.Instance();
			if(locationService.getCurrentLocation() != null)
				LocationService.Instance().updateGeocodeAddress();
		}
	}

	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		SnapshotManager snapManager = new SnapshotManager();
		snapManager.save(outState, this);
		snapManager = null;
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
	public void onTextUpdateError(ViewType t, String errorMessage)
	{
		NetworkGuiErrorManager ngem = new NetworkGuiErrorManager();
		ngem.onError(this, errorMessage);
		ngem = null;
	}

	@Override
	public void onBitmapUpdateError(BitmapType bType, String errorMessage)
	{
		NetworkGuiErrorManager ngem = new NetworkGuiErrorManager();
		ngem.onError(this, errorMessage);
		ngem = null;
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
	public void onStateChanged(long previousState, long state) 
	{
		if(((state & DownloadStatus.WEBCAM_OSMER_DOWNLOADED) != 0) &&  ((state & DownloadStatus.WEBCAM_OTHER_DOWNLOADED) != 0 ))
			Toast.makeText(getApplicationContext(), R.string.webcam_lists_downloaded, Toast.LENGTH_SHORT).show();

	}

	void executeObservationTypeSelectionDialog(ObservationTime oTime)
	{
		ObservationTypeGetter oTypeGetter = new ObservationTypeGetter();
		oTypeGetter.get(this, oTime, -1);
		oTypeGetter = null;
	}

	public void onSelectionDone(ObservationType type, ObservationTime oTime) 
	{
		/* switch the working mode of the map view */
		mViewPager.setCurrentItem(FlipperChildren.MAP);
		OMapFragment map = (OMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapview);
		map.setMode(new MapViewMode(type, oTime));
		if(type != ObservationType.WEBCAM)
			map.updateObservations(m_observationsCache.getObservationData(oTime));
	}

	@Override
	public boolean onMenuItemClick(MenuItem menuItem) 
	{
		/* must manually check an unchecked item, and vice versa.
		 * a checkbox or radio button does not change its state automatically.
		 */
		if(menuItem.isCheckable())
			menuItem.setChecked(!menuItem.isChecked());
		OMapFragment omv = (OMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapview);
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
				Toast.makeText(getApplicationContext(), R.string.hint_move_to_measure_on_map, Toast.LENGTH_LONG).show();
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
		OMapFragment map = (OMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapview);
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
		Log.e("switchView", "view type " + id);
		mCurrentViewType = id;
		ViewFlipper viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);

		/* if not already checked, changed flipper page */
		/* change flipper child. Title is updated by the FlipperChildChangeListener */
		switch(id)
		{
		case HOME:
			viewFlipper.setDisplayedChild(0);
			mViewPager.setCurrentItem(FlipperChildren.HOME);
			break;
		case TODAY:
			viewFlipper.setDisplayedChild(0);
			mViewPager.setCurrentItem(FlipperChildren.TODAY);
			break;
		case TOMORROW:
			viewFlipper.setDisplayedChild(0);
			mViewPager.setCurrentItem(FlipperChildren.TOMORROW);
			break;
		case TWODAYS:
			mActionBarManager.drawerItemChanged(0);
			mViewPager.setCurrentItem(FlipperChildren.TWODAYS);
			break;
		case MAP:		
		case RADAR:			
			viewFlipper.setDisplayedChild(1);
			mViewPager.setCurrentItem(FlipperChildren.MAP);
			/* remove itemized overlays (observations), if present, and restore radar view */
			((OMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapview)).setMode(new MapViewMode(ObservationType.RADAR, ObservationTime.DAILY));
			break;

		case ACTION_CENTER_MAP:
			((OMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapview)).centerMap();
			break;
		default:
			viewFlipper.setDisplayedChild(1);
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

	public DownloadManager stateMachine() { return m_downloadManager; }

	public ObservationsCache getObservationsCache()
	{
		return m_observationsCache;
	}

	public ViewPager getViewPager()
	{
		return mViewPager;
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

	public ActionBarManager getActionBarPersonalizer()
	{
		return mActionBarManager;
	}

	public AnimatedImageView getRefreshAnimatedImageView()
	{
		return mRefreshAnimatedImageView;
	}

	@Override
	public void onRadarImageUpdated() 
	{
		Settings settings = new Settings(this);
		long radarTimestampMillis = settings.getRadarImageTimestamp();
		long currentTimestampMillis = System.currentTimeMillis();
		CharSequence text = new RadarImageTimestampTextBuilder().buildText(currentTimestampMillis, 
				radarTimestampMillis, getResources(), mSettings.isFirstExecution());
		OAnimatedTextView radarTimestampText = (OAnimatedTextView) findViewById(R.id.radarTimestampTextView);
		if(radarTimestampText != null)
			radarTimestampText.setText(text);
	}

	
	/* private members */
	int mTapOnMarkerHintCount;
	private DownloadManager m_downloadManager;
	private Location mCurrentLocation;
	private ObservationsCache m_observationsCache;
	private MenuActionsManager mMenuActionsManager;
	private Settings mSettings;

	private ListView mDrawerList;
	private String[] mDrawerItems;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mTitle, mDrawerTitle;
	private ActionBarManager mActionBarManager;
	private AnimatedImageView mRefreshAnimatedImageView;
	private ViewType mCurrentViewType;
	/* ActionBar menu button and menu */
	private View mButtonsActionView;
	Urls m_urls;

	private FragmentTabHost mTabHost;




	ViewPager mViewPager;
	TabsAdapter mTabsAdapter;
	LinearLayout mMainLayout;


	int availCnt = 0;
}
