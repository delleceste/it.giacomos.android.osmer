package it.giacomos.android.osmer.pro;

import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.model.LatLng;

import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.pro.fragments.MapFragmentListener;
import it.giacomos.android.osmer.pro.interfaceHelpers.MenuActionsManager;
import it.giacomos.android.osmer.pro.interfaceHelpers.NetworkGuiErrorManager;
import it.giacomos.android.osmer.pro.interfaceHelpers.ObservationTypeGetter;
import it.giacomos.android.osmer.pro.interfaceHelpers.RadarImageTimestampTextBuilder;
import it.giacomos.android.osmer.pro.interfaceHelpers.TitlebarUpdater;
import it.giacomos.android.osmer.pro.locationUtils.LocationInfo;
import it.giacomos.android.osmer.pro.locationUtils.LocationService;
import it.giacomos.android.osmer.pro.network.DownloadManager;
import it.giacomos.android.osmer.pro.network.DownloadReason;
import it.giacomos.android.osmer.pro.network.DownloadStateListener;
import it.giacomos.android.osmer.pro.network.DownloadStatus;
import it.giacomos.android.osmer.pro.network.Data.DataPool;
import it.giacomos.android.osmer.pro.network.Data.DataPoolCacheUtils;
import it.giacomos.android.osmer.pro.network.Data.DataPoolErrorListener;
import it.giacomos.android.osmer.pro.network.state.BitmapType;
import it.giacomos.android.osmer.pro.network.state.StateName;
import it.giacomos.android.osmer.pro.network.state.Urls;
import it.giacomos.android.osmer.pro.network.state.ViewType;
import it.giacomos.android.osmer.pro.observations.MapMode;
import it.giacomos.android.osmer.pro.observations.NearestObservationData;
import it.giacomos.android.osmer.pro.observations.ObservationData;
import it.giacomos.android.osmer.pro.observations.ObservationType;
import it.giacomos.android.osmer.pro.observations.ObservationsCache;
import it.giacomos.android.osmer.pro.pager.ActionBarManager;
import it.giacomos.android.osmer.pro.pager.DrawerItemClickListener;
import it.giacomos.android.osmer.pro.pager.MyActionBarDrawerToggle;
import it.giacomos.android.osmer.pro.pager.TabsAdapter;
import it.giacomos.android.osmer.pro.pager.ViewPagerPages;
import it.giacomos.android.osmer.pro.preferences.*;
import it.giacomos.android.osmer.pro.webcams.WebcamDataHelper;
import it.giacomos.android.osmer.pro.widgets.AnimatedImageView;
import it.giacomos.android.osmer.pro.widgets.OAnimatedTextView;
import it.giacomos.android.osmer.pro.widgets.map.MapViewMode;
import it.giacomos.android.osmer.pro.widgets.map.OMapFragment;
import it.giacomos.android.osmer.pro.widgets.map.RadarOverlayUpdateListener;
import it.giacomos.android.osmer.pro.widgets.map.ReportRequestListener;
import it.giacomos.android.osmer.pro.widgets.map.animation.RadarAnimationListener;
import it.giacomos.android.osmer.pro.widgets.map.report.IconTextSpinnerAdapter;
import it.giacomos.android.osmer.pro.widgets.map.report.ObservationDataExtractor;
import it.giacomos.android.osmer.pro.widgets.map.report.ReportActivity;
import it.giacomos.android.osmer.pro.widgets.map.report.RemovePostConfirmDialog;
import it.giacomos.android.osmer.pro.widgets.map.report.ReportRequestDialogFragment;
import it.giacomos.android.osmer.pro.widgets.map.report.UsersReportUrlBuilder;
import it.giacomos.android.osmer.pro.widgets.map.report.network.PostReport;
import it.giacomos.android.osmer.pro.widgets.map.report.network.PostType;
import it.giacomos.android.osmer.pro.widgets.map.report.network.PostActionResultListener;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
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
OnMenuItemClickListener, 
OnDismissListener,
MapFragmentListener,
RadarOverlayUpdateListener,
DataPoolErrorListener,
RadarAnimationListener,
PostActionResultListener,
ReportRequestListener
{
	private final DownloadManager m_downloadManager;
	private final DownloadStatus mDownloadStatus;

	public OsmerActivity()
	{
		super();
		/* these are final */
		/* Create a download status in INIT state */
		mDownloadStatus = DownloadStatus.Instance();
		m_downloadManager = new DownloadManager(this, mDownloadStatus);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_PROGRESS);
		this.setProgressBarVisibility(true);


		/* create the location update client and connect it to the location service */
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		if(resultCode == ConnectionResult.SUCCESS)
		{
			mGoogleServicesAvailable = true;
			setContentView(R.layout.main);
			init();
		}
		else
		{
			mGoogleServicesAvailable = false;
			GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0).show();
		}

	}

	public void onResume()
	{	
		super.onResume();
		if(!mGoogleServicesAvailable)
			return;

		m_downloadManager.onResume(this);
	}

	public void onPause()
	{
		super.onPause();
		if(!mGoogleServicesAvailable)
			return;

		//		Log.e("OsmerActivity.onPause", "stopping pending tasks");
		/* cancel async tasks that may be running when the application is destroyed */
		m_downloadManager.stopPendingTasks();
		/* unregisters network status monitor broadcast receiver (for this it needs `this')
		 */
		m_downloadManager.onPause(this);
		mLocationService.disconnect();
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		if(!mGoogleServicesAvailable)
			return;
		int forceDrawerItem = -1;
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		/* force to switch to Reports mode */
		if(extras != null && extras.getBoolean("NotificationReportRequest"))
			forceDrawerItem = mDrawerItems.length - 1;
		mActionBarManager.init(savedInstanceState, forceDrawerItem);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if(!mGoogleServicesAvailable)
			return;
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
		if(!mGoogleServicesAvailable)
			return;
	}

	protected void onDestroy()
	{
		if(mGoogleServicesAvailable)
		{
			/* drawables are unbinded inside ForecastFragment's onDestroy() */
			if(mRefreshAnimatedImageView != null)
				mRefreshAnimatedImageView.hide();
			mDataPool.clear();
		}
		super.onDestroy();
	}

	public void onRestart()
	{
		super.onRestart();
		if(!mGoogleServicesAvailable)
			return;
	}

	public void onStart()
	{
		super.onStart();
		if(!mGoogleServicesAvailable)
			return;
	}

	public void init()
	{
		mProgressBarStep = mProgressBarTotSteps = 0;
		mAdditionalProgressBarStep = mProgressBarAdditionalSteps = 0;
		mCurrentViewType = ViewType.HOME;
		mViewPager = new ViewPager(this);
		mViewPager.setId(R.id.pager);

		mLocationService = new LocationService(getApplicationContext(), mDownloadStatus);
		/* Set the number of pages that should be retained to either side of 
		 * the current page in the view hierarchy in an idle state
		 */
		mViewPager.setOffscreenPageLimit(3);
		mMainLayout = (LinearLayout) findViewById(R.id.mainLayout);
		mMainLayout.addView(mViewPager);

		mSettings = new Settings(this);
		mTapOnMarkerHintCount = 0;
		mRefreshAnimatedImageView = null;

		mDrawerItems = getResources().getStringArray(R.array.drawer_text_items);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		/* Action bar stuff.  */
		mActionBarManager = new ActionBarManager(this);
		/* Set the adapter for the list view */
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, mDrawerItems));

		/* Set the list's click listener */
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener(this));
		mTitle = getTitle();

		DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		mDrawerToggle = new MyActionBarDrawerToggle(this, drawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close);
		/*Set the drawer toggle as the DrawerListener */
		drawerLayout.setDrawerListener(mDrawerToggle);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		//mActionBarManager.drawerItemChanged(0);

		mMenuActionsManager = null;
		mCurrentLocation = null;

		/* set html text on Radar info text view */
		TextView radarInfoTextView = (TextView)findViewById(R.id.radarInfoTextView);
		radarInfoTextView.setText(Html.fromHtml(getResources().getString(R.string.radar_info)));
		radarInfoTextView.setVisibility(View.GONE);

		m_observationsCache = new ObservationsCache();
		/* map updates the observation data in ItemizedOverlay when new observations are available
		 *
		 */
		OMapFragment map = getMapFragment();
		m_observationsCache.installObservationsCacheUpdateListener(map);

		/* Create DataPool.  */
		mDataPool = new DataPool(this);
		mDataPool.registerErrorListener(this); /* listen for network errors */
		/* register observations cache on DataPool. Data initialization with DataPoolCacheUtils
		 * is done afterwards, inside onStart.
		 */
		mDataPool.registerTextListener(ViewType.DAILY_TABLE, m_observationsCache);
		mDataPool.registerTextListener(ViewType.LATEST_TABLE, m_observationsCache);

		/* download manager. Instantiated in the constructor because it's final */
		m_downloadManager.setDownloadListener(mDataPool);

		DataPoolCacheUtils dataPoolCacheUtils = new DataPoolCacheUtils();

		/* load cached tables at startup. map is updated because installed as a listener
		 * above. Situation image will register as LatestObservationCacheChangeListener
		 * in SituationFragment.onActivityCreated. It will be immediately notified
		 * inside ObservationsCache.setLatestObservationCacheChangeListener.
		 */
		m_observationsCache.onTextChanged(dataPoolCacheUtils.loadFromStorage(ViewType.DAILY_TABLE, getApplicationContext()),
				ViewType.DAILY_TABLE, true);
		m_observationsCache.onTextChanged(dataPoolCacheUtils.loadFromStorage(ViewType.LATEST_TABLE, getApplicationContext()),
				ViewType.LATEST_TABLE, true);
		/* create the location update client and connect it to the location service */
		mLocationService.connect();

		/* show touch forecast icons hint until a MapWithForecastImage disables this */
		if(mSettings.isForecastIconsHintEnabled())
			Toast.makeText(getApplicationContext(), R.string.hint_forecast_icons, Toast.LENGTH_LONG).show();

		/* remove notifications from the notification bar */
		NotificationManager mNotificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancelAll();
		
		/* to show alerts inside onPostResume, after onActivityResult */
		mMyPendingAlertDialog = null;
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
		if(!mGoogleServicesAvailable)
			super.onBackPressed();
		else
		{
			/* first of all, if there's a info window on the map, close it */
			int displayedChild = ((ViewFlipper) findViewById(R.id.viewFlipper)).getDisplayedChild();
			OMapFragment map = getMapFragment();
			if(displayedChild == 1  &&  map.isInfoWindowVisible()) /* map view visible */
			{
				map.hideInfoWindow();
			}
			else if(displayedChild == 1)
			{
				mDrawerList.setItemChecked(0, true);
				mActionBarManager.drawerItemChanged(0);
			}
			else
				super.onBackPressed();
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
		Log.e("onGoogleMapRead", "intent is null? " + getIntent());

	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	public void networkStatusChanged(boolean online) 
	{
		TitlebarUpdater titlebarUpdater = new TitlebarUpdater();
		titlebarUpdater.update(this);
		titlebarUpdater = null;

		if(!online)
		{
			Toast.makeText(getApplicationContext(), R.string.netUnavailableToast, Toast.LENGTH_LONG).show();	
		}
		else
		{
			MapViewUpdater currenvViewUpdater = new MapViewUpdater();
			currenvViewUpdater.update(this);
			currenvViewUpdater = null;

			/* trigger an update of the locality if Location is available */
			if(mLocationService.getCurrentLocation() != null)
				mLocationService.updateGeocodeAddress();
		}
	}

	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		if(getActionBar().getNavigationMode() == ActionBar.NAVIGATION_MODE_LIST)
			outState.putInt("spinnerPosition", getActionBar().getSelectedNavigationIndex());
	}

	protected void onRestoreInstanceState(Bundle inState)
	{
		super.onRestoreInstanceState(inState);
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
		Toast.makeText(this, R.string.radarUpdateToast, Toast.LENGTH_SHORT).show();
		m_downloadManager.getRadarImage();
	}

	void updateReport(boolean force)
	{
		getMapFragment().updateReport(force);
	}

	void satellite()
	{
		//TextView textView = (TextView) findViewById(R.id.mainTextView);
	}

	void updateWbcamList()
	{
		WebcamDataHelper webcamDataHelper = new WebcamDataHelper();
		if(webcamDataHelper.dataIsOld(getApplicationContext()))
		{
			Toast.makeText(getApplicationContext(), R.string.webcam_updating, Toast.LENGTH_SHORT).show();
			m_downloadManager.getWebcamList();
		}
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
		//		Log.e("onDownloadProgressUpdate", "step " + step + "/" + total);
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

	void executeObservationTypeSelectionDialog(MapMode mapMode)
	{
		ObservationTypeGetter oTypeGetter = new ObservationTypeGetter();
		oTypeGetter.get(this, mapMode, -1);
		oTypeGetter = null;
	}

	/** implemented from PostActionResultListener.
	 * This method is invoked after a http post has been executed and returned, with or without
	 * errors as indicated in the method parameters.
	 */
	@Override
	public void onPostActionResult(boolean error, String message, PostType postType) 
	{
		if(!error && (postType == PostType.REPORT || postType == PostType.REQUEST))
			Toast.makeText(this, R.string.reportDataSentOk, Toast.LENGTH_SHORT).show();
		else if(error)
		{
			String m = this.getResources().getString(R.string.reportError) + "\n" + message;
			Toast.makeText(this, m, Toast.LENGTH_LONG).show();
		}
		/* invoked when the user has successfully published its report or cancelled one of his reports
		 * also.
		 */
		if(mCurrentViewType == ViewType.REPORT)
		{
			Log.e("switchView", "updating report (forceth)");
			getMapFragment().onPostActionResult(error, message, postType);
			updateReport(true);
		}
	}

	/** implements ReportRequestListener interface
	 * 
	 */
	@Override
	public void onMyReportLocalityChanged(String locality) 
	{
		Log.e("OsmerActivity.onMyReportLocalityChanged", "locality " + locality);
		ReportRequestDialogFragment rrdf = (ReportRequestDialogFragment) 
				getSupportFragmentManager().findFragmentByTag("ReportRequestDialogFragment");
		if(rrdf != null)
			rrdf.setLocality(locality);
		else
			Log.e("OsmerActivity.onMyReportLocalityChanged", "FRAGMENT NULL!");
	}

	/** implements ReportRequestListener interface
	 * 
	 */
	@Override
	public void onMyReportRequestTriggered(LatLng pointOnMap, String locality) 
	{
		ReportRequestDialogFragment rrdf = ReportRequestDialogFragment.newInstance(locality);
		rrdf.setData(pointOnMap, locality);
		rrdf.show(getSupportFragmentManager(), "ReportRequestDialogFragment");
		/* tell map fragment that the report request dialog has been closed.
		 * The true parameter means the dialog was accepted.
		 */
		getMapFragment().myReportRequestDialogClosed(false, pointOnMap);
	}

	/** implements ReportRequestListener.onMyPostRemove method interface
	 * 
	 */
	@Override
	public void onMyPostRemove(LatLng position, PostType type) 
	{
		RemovePostConfirmDialog rrccd = new RemovePostConfirmDialog();
		/* the following in order to be notified when the dialog is cancelled or the remove post 
		 * task has been accomplished, successfully or not, according to the results contained 
		 * in the onPostActionResult() method.
		 */
		rrccd.setPostActionResultListener(this);
		rrccd.setLatLng(position);
		rrccd.setType(type);
		rrccd.setDeviceId(Secure.getString(getContentResolver(), Secure.ANDROID_ID));
		rrccd.show(getSupportFragmentManager(), "RemovePostConfirmDialog");
	}

	/** implements ReportRequestListener.onMyReportRequestDialogCancelled method interface
	 * 
	 */
	@Override
	public void onMyReportRequestDialogCancelled(LatLng position) 
	{
		/* tell map fragment that the report request dialog has been closed.
		 * The false parameter means the dialog was canceled.
		 */
		getMapFragment().myReportRequestDialogClosed(false, position);
	}

	public void onSelectionDone(ObservationType observationType, MapMode mapMode) 
	{
		/* switch the working mode of the map view. Already in PAGE_MAP view flipper page */
		OMapFragment map = getMapFragment();
		map.setMode(new MapViewMode(observationType, mapMode));
		if(mapMode == MapMode.DAILY_OBSERVATIONS || mapMode == MapMode.LATEST_OBSERVATIONS)
			map.updateObservations(m_observationsCache.getObservationData(mapMode));
	}

	@Override
	public boolean onMenuItemClick(MenuItem menuItem) 
	{
		/* must manually check an unchecked item, and vice versa.
		 * a checkbox or radio button does not change its state automatically.
		 */
		if(menuItem.isCheckable())
			menuItem.setChecked(!menuItem.isChecked());
		OMapFragment omv = getMapFragment();
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
		case R.id.radarAnimationAction:
			findViewById(R.id.radarTimestampTextView).setVisibility(View.GONE);
			startRadarAnimation();
			break;
		case R.id.reportDialogAction:
			startReportActivity();
			break;
		case R.id.reportUpdateAction:
			/* this forces an update, even if just updated */
			updateReport(true);
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	public void onClick(View v)
	{
		if (v.getId() == R.id.actionOverflow) 
		{
			stopRadarAnimation();
			mCreateMapOptionsPopupMenu(true);
		} 
		else 
		{

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
		if(mButtonsActionView == null || !mGoogleServicesAvailable)
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

	private void mCreateMapOptionsPopupMenu(boolean show) 
	{
		OMapFragment map = getMapFragment();
		ToggleButton buttonMapsOveflowMenu = (ToggleButton) mButtonsActionView.findViewById(R.id.actionOverflow);
		PopupMenu mapOptionsMenu = new PopupMenu(this, buttonMapsOveflowMenu);
		Menu menu = mapOptionsMenu.getMenu();
		mapOptionsMenu.getMenuInflater().inflate(R.menu.map_options_popup_menu, menu);
		menu.findItem(R.id.measureToggleButton).setVisible(mCurrentViewType == ViewType.RADAR);
		menu.findItem(R.id.radarInfoButton).setVisible(mCurrentViewType == ViewType.RADAR);
		menu.findItem(R.id.measureToggleButton).setChecked(map.isMeasureEnabled());
		menu.findItem(R.id.radarInfoButton).setChecked(findViewById(R.id.radarInfoTextView).getVisibility() == View.VISIBLE);
		/* animation available only in radar mode */
		menu.findItem(R.id.radarAnimationAction).setVisible(mCurrentViewType == ViewType.RADAR);
		/* report action */
		menu.findItem(R.id.reportDialogAction).setVisible(mCurrentViewType == ViewType.REPORT);
		menu.findItem(R.id.reportUpdateAction).setVisible(mCurrentViewType == ViewType.REPORT);

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

		if(show)
		{
			mapOptionsMenu.setOnMenuItemClickListener(this);
			mapOptionsMenu.setOnDismissListener(this);
			mapOptionsMenu.show();
		}
	}

	public void switchView(ViewType id) 
	{
		Log.e("switchView", "swticihc to " + id);
		mCurrentViewType = id;
		ViewFlipper viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
		OMapFragment mapFragment = getMapFragment();

		if (id == ViewType.HOME) 
		{
			viewFlipper.setDisplayedChild(0);
			mViewPager.setCurrentItem(ViewPagerPages.HOME);
			mapFragment.setMode(new MapViewMode(ObservationType.NONE, MapMode.HIDDEN));
		} 
		else if (id == ViewType.TODAY) 
		{
			viewFlipper.setDisplayedChild(0);
			mViewPager.setCurrentItem(ViewPagerPages.TODAY);
			mapFragment.setMode(new MapViewMode(ObservationType.NONE, MapMode.HIDDEN));
		} 
		else if (id == ViewType.TOMORROW) 
		{
			viewFlipper.setDisplayedChild(0);
			mViewPager.setCurrentItem(ViewPagerPages.TOMORROW);
			mapFragment.setMode(new MapViewMode(ObservationType.NONE, MapMode.HIDDEN));
		} 
		else if (id == ViewType.TWODAYS) 
		{
			viewFlipper.setDisplayedChild(0);
			mViewPager.setCurrentItem(ViewPagerPages.TWODAYS);
			mapFragment.setMode(new MapViewMode(ObservationType.NONE, MapMode.HIDDEN));
		} 
		else if (id == ViewType.RADAR) 
		{
			viewFlipper.setDisplayedChild(1);
			/* remove itemized overlays (observations), if present, and restore radar view */
			mapFragment.setMode(new MapViewMode(ObservationType.NONE, MapMode.RADAR));
		} 
		else if (id == ViewType.ACTION_CENTER_MAP) 
		{
			mapFragment.centerMap();
		} 
		else 
		{
			Log.e("OsmerActivity.swithciee", "displayting child 1");
			viewFlipper.setDisplayedChild(1);
		}

		if (id == ViewType.DAILY_SKY) {
			onSelectionDone(ObservationType.SKY, MapMode.DAILY_OBSERVATIONS);
			if(mSettings.isMapMarkerHintEnabled() && mTapOnMarkerHintCount == 0)
			{
				Toast.makeText(getApplicationContext(), R.string.hint_tap_on_map_markers, Toast.LENGTH_LONG).show();
				mTapOnMarkerHintCount++; /* do not be too annoying */
			}
		} 
		else if (id == ViewType.DAILY_HUMIDITY) 
			onSelectionDone(ObservationType.AVERAGE_HUMIDITY, MapMode.DAILY_OBSERVATIONS);
		else if (id == ViewType.DAILY_WIND_MAX) 
			onSelectionDone(ObservationType.MAX_WIND, MapMode.DAILY_OBSERVATIONS);
		else if (id == ViewType.DAILY_WIND) 
			onSelectionDone(ObservationType.AVERAGE_WIND, MapMode.DAILY_OBSERVATIONS);
		else if (id == ViewType.DAILY_RAIN) 
			onSelectionDone(ObservationType.RAIN, MapMode.DAILY_OBSERVATIONS);
		else if (id == ViewType.DAILY_MIN_TEMP) 
			onSelectionDone(ObservationType.MIN_TEMP, MapMode.DAILY_OBSERVATIONS);
		else if (id == ViewType.DAILY_MEAN_TEMP) 
			onSelectionDone(ObservationType.AVERAGE_TEMP, MapMode.DAILY_OBSERVATIONS);
		else if (id == ViewType.DAILY_MAX_TEMP) 
			onSelectionDone(ObservationType.MAX_TEMP, MapMode.DAILY_OBSERVATIONS);
		else if (id == ViewType.LATEST_SKY) 
			onSelectionDone(ObservationType.SKY, MapMode.LATEST_OBSERVATIONS);
		else if (id == ViewType.LATEST_HUMIDITY) 
			onSelectionDone(ObservationType.HUMIDITY, MapMode.LATEST_OBSERVATIONS);
		else if (id == ViewType.LATEST_WIND) 
			onSelectionDone(ObservationType.WIND, MapMode.LATEST_OBSERVATIONS);
		else if (id == ViewType.LATEST_PRESSURE) 
			onSelectionDone(ObservationType.PRESSURE, MapMode.LATEST_OBSERVATIONS);
		else if (id == ViewType.LATEST_RAIN) 
			onSelectionDone(ObservationType.RAIN, MapMode.LATEST_OBSERVATIONS);
		else if (id == ViewType.LATEST_SEA) 
			onSelectionDone(ObservationType.SEA, MapMode.LATEST_OBSERVATIONS);
		else if (id == ViewType.LATEST_SNOW) 
			onSelectionDone(ObservationType.SNOW, MapMode.LATEST_OBSERVATIONS);
		else if (id == ViewType.LATEST_TEMP) 
			onSelectionDone(ObservationType.TEMP, MapMode.LATEST_OBSERVATIONS);
		else if (id == ViewType.WEBCAM) 
			onSelectionDone(ObservationType.NONE, MapMode.WEBCAM);
		else if (id == ViewType.REPORT) 
			onSelectionDone(ObservationType.REPORT, MapMode.REPORT);
		/* try to download only if online */
		if(m_downloadManager.state().name() == StateName.Online)
		{
			if(id == ViewType.RADAR)
				radar(); /* map mode has already been set if type is MAP or RADAR */
			else if(id == ViewType.TODAY)
				getTodayForecast();
			else if(id == ViewType.TOMORROW)
				getTomorrowForecast();
			else if(id == ViewType.TWODAYS)
				getTwoDaysForecast();
			else if(id == ViewType.HOME)
			{ }
			else if(id == ViewType.WEBCAM)
				updateWbcamList();
			else if(id == ViewType.REPORT)
			{
				Log.e("switchView", "updating report");
				updateReport(false);
			}
		}

		if(mSettings.isZoneLongPressHintEnabled() && id == ViewType.TODAY)
			Toast.makeText(getApplicationContext(), R.string.hint_zone_longpress, Toast.LENGTH_LONG).show();

		TitlebarUpdater titleUpdater = new TitlebarUpdater();
		titleUpdater.update(this);
		titleUpdater = null;

		/* show or hide maps menu button according to the current view type */
		mInitButtonMapsOverflowMenu();
	}

	public DownloadManager stateMachine() { return m_downloadManager; }

	public void startRadarAnimation()
	{
		OMapFragment omv = getMapFragment();
		omv.startRadarAnimation();
	}

	public void stopRadarAnimation()
	{
		OMapFragment omv = getMapFragment();
		omv.stopRadarAnimation();
	}

	public void startReportActivity()
	{
		Location loc = this.mLocationService.getCurrentLocation();
		if(loc == null)
			 MyAlertDialogFragment.MakeGenericError(R.string.location_not_available, this);
		else if(!mDownloadStatus.isOnline)
			MyAlertDialogFragment.MakeGenericInfo(R.string.reportNeedToBeOnline, this);
		else
		{
			ObservationData obsData = new NearestObservationData().get(loc, m_observationsCache);
			ObservationDataExtractor oex = new ObservationDataExtractor(obsData);
			Intent i = new Intent(this, ReportActivity.class);
			i.putExtra("sky", oex.getSkyIndex());
			i.putExtra("temp", oex.getTemperature());
			i.putExtra("wind", oex.getWindIndex());
			i.putExtra("latitude", loc.getLatitude());
			i.putExtra("longitude", loc.getLongitude());
			this.startActivityForResult(i, REPORT_ACTIVITY_FOR_RESULT_ID);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if(requestCode == REPORT_ACTIVITY_FOR_RESULT_ID)
		{
			if(resultCode == Activity.RESULT_OK)
			{
				/* check if the user hasn't moved too far since she started the report activity */
				Location currentLoc = mLocationService.getCurrentLocation();
				String locality = "";
				LocationInfo loi = mLocationService.getCurrentLocationInfo();
				if(loi != null)
					locality = loi.locality;
				if(currentLoc != null) /* otherwise assume we can't determine */
				{
					Location reportLocation = new Location("");
					reportLocation.setLatitude(data.getDoubleExtra("latitude", 0));
					reportLocation.setLongitude(data.getDoubleExtra("longitude", 0));
					if(currentLoc.distanceTo(reportLocation) <= 3000)
					{
						/* ok */
						String deviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
						new PostReport(data.getStringExtra("user"), deviceId, 
								locality, reportLocation.getLatitude(), reportLocation.getLongitude(), 
								data.getIntExtra("sky", 0), data.getIntExtra("wind", 0), 
								data.getStringExtra("temp"), data.getStringExtra("comment"), this);
					}
					else
					{
						Log.e("onActivityresutl" , "spostato di " +currentLoc.distanceTo(reportLocation) );
						/* need pending alert dialog helper because the dialog cannot be shown inside 
						 * onActivityResult, but instead the transaction (being it a Fragment) can be
						 * made only in onPostResume().
						 */
						mMyPendingAlertDialog = new MyPendingAlertDialog(MyAlertDialogType.ERROR,  R.string.reportMovedTooFar);
					}
				}
				else
					mMyPendingAlertDialog = new MyPendingAlertDialog(MyAlertDialogType.ERROR,  R.string.location_not_available);
			}
		}
	}
	
	@Override
	public void onPostResume()
	{
		super.onPostResume();
		if(mMyPendingAlertDialog != null && mMyPendingAlertDialog.isShowPending())
			mMyPendingAlertDialog.showPending(this);
	}
	
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

	public DataPool getDataPool()
	{
		return mDataPool;
	}

	public DownloadStatus getDownloadStatus()
	{
		return mDownloadStatus;
	}

	public LocationService getLocationService()
	{
		return mLocationService;
	}

	public OMapFragment getMapFragment()
	{
		OMapFragment mapFrag = (OMapFragment)getSupportFragmentManager().findFragmentById(R.id.mapview);
		return mapFrag;
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

	@Override
	public void onRadarAnimationStart() 
	{

	}

	@Override
	public void onRadarAnimationPause() 
	{

	}

	@Override
	public void onRadarAnimationStop() 
	{

	}

	@Override
	public void onRadarAnimationRestored() 
	{

	}

	@Override
	public void onRadarAnimationResumed() 
	{

	}

	@Override
	public void onRadarAnimationProgress(int step, int total) 
	{

	}

	/* private members */
	int mTapOnMarkerHintCount;
	private Location mCurrentLocation;
	private ObservationsCache m_observationsCache;
	private MenuActionsManager mMenuActionsManager;
	private Settings mSettings;

	private ListView mDrawerList;
	private String[] mDrawerItems;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mTitle;
	private ActionBarManager mActionBarManager;
	private AnimatedImageView mRefreshAnimatedImageView;
	private ViewType mCurrentViewType;
	/* ActionBar menu button and menu */
	private View mButtonsActionView;
	private boolean mGoogleServicesAvailable;
	Urls m_urls;

	private DataPool mDataPool;
	private LocationService mLocationService;

	private int mProgressBarStep, mProgressBarTotSteps;
	private int mAdditionalProgressBarStep, mProgressBarAdditionalSteps;

	ViewPager mViewPager;
	TabsAdapter mTabsAdapter;
	LinearLayout mMainLayout;
	
	public static final int REPORT_ACTIVITY_FOR_RESULT_ID = Activity.RESULT_FIRST_USER + 100;
	
	private MyPendingAlertDialog mMyPendingAlertDialog;


	int availCnt = 0;

}
