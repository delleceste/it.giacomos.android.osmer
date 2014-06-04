package it.giacomos.android.osmer;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.model.LatLng;

import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.fragments.MapFragmentListener;
import it.giacomos.android.osmer.interfaceHelpers.MenuActionsManager;
import it.giacomos.android.osmer.interfaceHelpers.NetworkGuiErrorManager;
import it.giacomos.android.osmer.interfaceHelpers.RadarImageTimestampTextBuilder;
import it.giacomos.android.osmer.interfaceHelpers.TitlebarUpdater;
import it.giacomos.android.osmer.locationUtils.LocationInfo;
import it.giacomos.android.osmer.locationUtils.LocationService;
import it.giacomos.android.osmer.network.DownloadManager;
import it.giacomos.android.osmer.network.DownloadReason;
import it.giacomos.android.osmer.network.DownloadStateListener;
import it.giacomos.android.osmer.network.DownloadStatus;
import it.giacomos.android.osmer.network.Data.DataPool;
import it.giacomos.android.osmer.network.Data.DataPoolCacheUtils;
import it.giacomos.android.osmer.network.Data.DataPoolErrorListener;
import it.giacomos.android.osmer.network.state.BitmapType;
import it.giacomos.android.osmer.network.state.StateName;
import it.giacomos.android.osmer.network.state.Urls;
import it.giacomos.android.osmer.network.state.ViewType;
import it.giacomos.android.osmer.observations.MapMode;
import it.giacomos.android.osmer.observations.NearestObservationData;
import it.giacomos.android.osmer.observations.ObservationData;
import it.giacomos.android.osmer.observations.ObservationType;
import it.giacomos.android.osmer.observations.ObservationsCache;
import it.giacomos.android.osmer.pager.ActionBarManager;
import it.giacomos.android.osmer.pager.DrawerItemClickListener;
import it.giacomos.android.osmer.pager.MyActionBarDrawerToggle;
import it.giacomos.android.osmer.pager.TabsAdapter;
import it.giacomos.android.osmer.pager.ViewPagerPages;
import it.giacomos.android.osmer.preferences.*;
import it.giacomos.android.osmer.service.ServiceManager;
import it.giacomos.android.osmer.trial.BuyProActivity;
import it.giacomos.android.osmer.trial.ExpirationChecker;
import it.giacomos.android.osmer.trial.ExpirationCheckerListener;
import it.giacomos.android.osmer.trial.InAppEventListener;
import it.giacomos.android.osmer.trial.TrialExpiringNotification;
import it.giacomos.android.osmer.webcams.WebcamDataHelper;
import it.giacomos.android.osmer.widgets.AnimatedImageView;
import it.giacomos.android.osmer.widgets.OAnimatedTextView;
import it.giacomos.android.osmer.widgets.map.MapViewMode;
import it.giacomos.android.osmer.widgets.map.OMapFragment;
import it.giacomos.android.osmer.widgets.map.RadarOverlayUpdateListener;
import it.giacomos.android.osmer.widgets.map.ReportRequestListener;
import it.giacomos.android.osmer.widgets.map.animation.RadarAnimationListener;
import it.giacomos.android.osmer.widgets.map.report.ObservationDataExtractor;
import it.giacomos.android.osmer.widgets.map.report.RemovePostConfirmDialog;
import it.giacomos.android.osmer.widgets.map.report.ReportActivity;
import it.giacomos.android.osmer.widgets.map.report.ReportRequestDialogFragment;
import it.giacomos.android.osmer.widgets.map.report.network.PostActionResultListener;
import it.giacomos.android.osmer.widgets.map.report.network.PostReport;
import it.giacomos.android.osmer.widgets.map.report.network.PostType;
import it.giacomos.android.osmer.widgets.map.report.tutorialActivity.TutorialPresentationActivity;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
ReportRequestListener, 
InAppEventListener/*  trial version */
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
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

//		Log.e("OsmerActivity.onCreate", "onCreate called");
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
//		Log.e("OsmerActivity.onResume", "onResume called");
		if(!mGoogleServicesAvailable)
			return;

		/* (re)connect the location update client */
		mLocationService.connect();
		m_downloadManager.onResume(this);

		/* remove notifications from the notification bar */
		NotificationManager mNotificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancelAll();
		
		if(this.mSettings.importantDialogToShow())
		{
			MyAlertDialogFragment.MakeGenericInfo(R.string.important_dialog_message, this);
			this.mSettings.setImportantDialogToShow(false);
		}
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
		if(extras != null)
		{
			if(extras.getBoolean("NotificationReportRequest")
					|| extras.getBoolean("NotificationReport"))
				forceDrawerItem = mDrawerItems.length - 1;
			else if(extras.getBoolean("NotificationRainAlert"))
				forceDrawerItem = 1; /* radar */
		}
		mActionBarManager.init(savedInstanceState, forceDrawerItem);
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		int drawerItem = -1;
		Bundle extras = intent.getExtras();
		/* force to switch to Reports mode */
		if(extras != null)
		{
			if(extras.getBoolean("NotificationReportRequest")
					|| extras.getBoolean("NotificationReport"))
				drawerItem = 5;
			else if(extras.getBoolean("NotificationRainAlert"))
				drawerItem = 1; /* radar */
			
			Log.e("OsmerActivity.onNewIntent", "switching to item " + drawerItem);
			mActionBarManager.drawerItemChanged(drawerItem);
		}
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
			if(mMapOptionsMenu != null)
				mMapOptionsMenu.dismiss();
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


	@Override
	public void onAppPurchased(boolean ok) 
	{
		if(ok)
		{
			
		}
		
	}
	
	/* 
	 * This method is triggered by SituationFragment only when the application
	 * has not been purchased.
	 */
	@Override
	public void onTrialDaysRemaining(int days) 
	{
		mSettings.setTrialDaysLeft(days);
		mTrialDaysChanged(days);
	}
	
	private void mTrialDaysChanged(int days)
	{
		if(days <= 0)
		{
			Toast.makeText(this, R.string.trial_expired, Toast.LENGTH_LONG).show();
			Intent activityIntent = new Intent(this, BuyProActivity.class);
			startActivity(activityIntent);
			/* stop notification service, if running.
			 * The ConnectivityChangedReceiver will not start the service anymore
			 * if the trial period has expired.
			 */
			mStartNotificationService(false);
			/* finish app */
			this.finish();
		}
		if(days < 3)
		{
			TrialExpiringNotification ten = new TrialExpiringNotification();
			ten.show(this, days);
		}
	}
	
	public void init()
	{
		mProgressBarStep = mProgressBarTotSteps = 0;
		mAdditionalProgressBarStep = mProgressBarAdditionalSteps = 0;
		mCurrentViewType = ViewType.HOME;
		mViewPager = new ViewPager(this);
		mViewPager.setId(R.id.pager);

		mLocationService = new LocationService(getApplicationContext());
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

		/* don't try to remove this unless you know what ur doing. It scrambles up
		 * the restoring of the tab selection on screen rotation.
		 */
		mActionBarManager.drawerItemChanged(0);

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

		/* show touch forecast icons hint until a MapWithForecastImage disables this */
		if(mSettings.isForecastIconsHintEnabled())
			Toast.makeText(getApplicationContext(), R.string.hint_forecast_icons, Toast.LENGTH_LONG).show();

		/* to show alerts inside onPostResume, after onActivityResult */
		mMyPendingAlertDialog = null;
		mReportConditionsAccepted = mSettings.reportConditionsAccepted();
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

	}

	public void onCameraReady()
	{
		Intent i = getIntent();
		Bundle extras = i.getExtras();
		if(extras != null && extras.containsKey("ptLatitude") && extras.containsKey("ptLongitude"))
		{
			getMapFragment().moveTo(extras.getDouble("ptLatitude"), extras.getDouble("ptLongitude"));
			i.removeExtra("ptLatitude");
			i.removeExtra("ptLongitude");
		}
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
		/* invoked when the user has successfully published its request or cancelled one of his reports
		 * also. Since the PostType.REPORT is realized through another Activity, when this activity
		 * is resumed, it performs an update of the report automatically (see onNetworkBecomesAvailable,
		 * which calls MapViewUpdater.
		 */
		if(mCurrentViewType == ViewType.REPORT && postType != PostType.REPORT)
		{
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
		ReportRequestDialogFragment rrdf = (ReportRequestDialogFragment) 
				getSupportFragmentManager().findFragmentByTag("ReportRequestDialogFragment");
		if(rrdf != null)
			rrdf.setLocality(locality);
	}

	/** implements ReportRequestListener interface
	 * 
	 */
	@Override
	public void onMyReportRequestTriggered(LatLng pointOnMap, String locality) 
	{
		OMapFragment omf = getMapFragment();
		Location myLocation = getLocationService().getCurrentLocation();
		if(myLocation == null || omf.pointTooCloseToMyLocation(myLocation, pointOnMap))
		{
			MyAlertDialogFragment.MakeGenericError(R.string.reportPointTooCloseToMyLocation, this);
		}
		else
		{
			ReportRequestDialogFragment rrdf = ReportRequestDialogFragment.newInstance(locality);
			rrdf.setData(pointOnMap, locality);
			rrdf.show(getSupportFragmentManager(), "ReportRequestDialogFragment");
			/* tell map fragment that the report request dialog has been closed.
			 * The true parameter means the dialog was accepted.
			 */
			getMapFragment().myReportRequestDialogClosed(false, pointOnMap);
		}
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

	/** implements ReportRequestListener.onMyReportPublish method interface.
	 *  This is invoked when a user touches a baloon on a ReporOverlay request marker.
	 * 
	 */
	public void onMyReportPublish()
	{
		startReportActivity();
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
		if((mapMode != MapMode.REPORT) || (mapMode == MapMode.REPORT && mReportConditionsAccepted))
			map.setMode(new MapViewMode(observationType, mapMode));
		else if(mapMode == MapMode.REPORT)
		{
			mDrawerList.setItemChecked(0, true);
			mActionBarManager.drawerItemChanged(0);
			mStartTutorialActivity();
		}
		if(mapMode == MapMode.DAILY_OBSERVATIONS || mapMode == MapMode.LATEST_OBSERVATIONS)
			map.updateObservations(m_observationsCache.getObservationData(mapMode));

	}

	private void mStartTutorialActivity()
	{
		Intent i = new Intent(this, TutorialPresentationActivity.class);
		i.putExtra("startedFromMainActivity", true);
		i.putExtra("conditionsAccepted", mReportConditionsAccepted);
		this.startActivityForResult(i, TUTORIAL_ACTIVITY_FOR_RESULT_ID);
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
		case R.id.syncServiceAction:
			MyAlertDialogFragment.MakeGenericInfo(R.string.dialogNotificationServiceEnableMovedToSettings, this);
			break;
		case R.id.reportUpdateAction:
			/* this forces an update, even if just updated */
			updateReport(true);
			break;
		case R.id.reportHelpAction:
			/* show map tilt transparent overlay next time too */
			mSettings.setTiltTutorialShown(false);
			mStartTutorialActivity();
			break;
		default:
			break;
		}
		return false;
	}

	private void mStartNotificationService(boolean startService) 
	{
		ServiceManager serviceManager = new ServiceManager();
		Log.e("OsmerActivity.onClick", "enabling service: " + startService +
				" was running "+ serviceManager.isServiceRunning(this));
		boolean ret = serviceManager.setEnabled(this, startService);
		if(ret && startService)
			Toast.makeText(this, R.string.notificationServiceStarted, Toast.LENGTH_LONG).show();
		else if(ret && !startService)
			Toast.makeText(this, R.string.notificationServiceStopped, Toast.LENGTH_LONG).show();
		else if(!ret && startService)
			Toast.makeText(this, R.string.notificationServiceWillStartOnNetworkAvailable, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onClick(View v)
	{
		if (v.getId() == R.id.actionOverflow) 
		{
			stopRadarAnimation();
			mCreateMapOptionsPopupMenu(true);
		} 
		else if(v.getId() == R.id.actionNewReport)
		{
			startReportActivity();	
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
		Button newReportButton = (Button) mButtonsActionView.findViewById(R.id.actionNewReport);

		if(newReportButton != null)
			newReportButton.setVisibility(View.GONE);
		switch(mCurrentViewType)
		{
		case HOME:
		case TODAY:
		case TOMORROW:
		case TWODAYS:
			if(buttonMapsOveflowMenu != null)
				buttonMapsOveflowMenu.setVisibility(View.GONE);
			break;
		case REPORT:
			newReportButton.setVisibility(View.VISIBLE);
			newReportButton.setOnClickListener(this);
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
		mMapOptionsMenu = new PopupMenu(this, buttonMapsOveflowMenu);
		Menu menu = mMapOptionsMenu.getMenu();
		mMapOptionsMenu.getMenuInflater().inflate(R.menu.map_options_popup_menu, menu);
		menu.findItem(R.id.measureToggleButton).setVisible(mCurrentViewType == ViewType.RADAR);
		menu.findItem(R.id.radarInfoButton).setVisible(mCurrentViewType == ViewType.RADAR);
		menu.findItem(R.id.measureToggleButton).setChecked(map.isMeasureEnabled());
		menu.findItem(R.id.radarInfoButton).setChecked(findViewById(R.id.radarInfoTextView).getVisibility() == View.VISIBLE);
		/* animation available only in radar mode */
		menu.findItem(R.id.radarAnimationAction).setVisible(mCurrentViewType == ViewType.RADAR);
		/* report action */
		menu.findItem(R.id.reportUpdateAction).setVisible(mCurrentViewType == ViewType.REPORT);
		/* tutorial */
		menu.findItem(R.id.reportHelpAction).setVisible(mCurrentViewType == ViewType.REPORT);
		/* enable sync action */
		menu.findItem(R.id.syncServiceAction).setVisible(mCurrentViewType == ViewType.REPORT 
				|| mCurrentViewType == ViewType.RADAR);

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
			mMapOptionsMenu.setOnMenuItemClickListener(this);
			mMapOptionsMenu.setOnDismissListener(this);
			mMapOptionsMenu.show();
		}
	}

	public void switchView(ViewType id) 
	{
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
		LocationInfo loci = mLocationService.getCurrentLocationInfo();
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
			if(loci != null)
			{
				i.putExtra("locality", loci.locality);
			}
//			else
//				Log.e("OsmerActivity.startReportActivity", "startingActivity without locality");
			this.startActivityForResult(i, REPORT_ACTIVITY_FOR_RESULT_ID);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
//		Log.e("onActivityResult", "result " + resultCode);
		if(requestCode == REPORT_ACTIVITY_FOR_RESULT_ID)
		{
			if(resultCode == Activity.RESULT_OK)
			{
				Bundle extras = data.getExtras();
				String locality = extras.getString("locality");

				Location reportLocation = new Location("");
				reportLocation.setLatitude(data.getDoubleExtra("latitude", 0));
				reportLocation.setLongitude(data.getDoubleExtra("longitude", 0));

				/* ok */
				String deviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
				new PostReport(data.getStringExtra("user"), deviceId, 
						locality, reportLocation.getLatitude(), reportLocation.getLongitude(), 
						data.getIntExtra("sky", 0), data.getIntExtra("wind", 0), 
						data.getStringExtra("temperature"), data.getStringExtra("comment"), this);
			}
		}
		else if(requestCode == TUTORIAL_ACTIVITY_FOR_RESULT_ID)
		{
//			Log.e("OsmerActivity.onActivityResult", "resultCode " + resultCode + " data " + data + " OK " +
//					RESULT_OK + " cancelled " + RESULT_CANCELED);
			boolean conditionsAccepted = false;
			if(data != null)
				conditionsAccepted = data.getBooleanExtra("conditionsAccepted", false);
			if(conditionsAccepted != mReportConditionsAccepted)
			{
				mReportConditionsAccepted = conditionsAccepted;
				mSettings.setReportConditionsAccepted(conditionsAccepted);
				mStartNotificationService(conditionsAccepted && mSettings.notificationServiceEnabled());
			}

//			Log.e("OsmerActivity.onActivityResult", "conditionsAccepted " + conditionsAccepted);
			if(conditionsAccepted)
			{
				mDrawerList.performItemClick(mDrawerList, 5, mDrawerList.getItemIdAtPosition(5));
//				mDrawerList.setItemChecked(5, true);
//				mActionBarManager.drawerItemChanged(5);
			}
			else
			{
				mDrawerList.performItemClick(mDrawerList, 0, mDrawerList.getItemIdAtPosition(0));
				//mDrawerList.setItemChecked(5, true);
				//mActionBarManager.drawerItemChanged(5);
			}
		}
	}

	public void makePendingAlertErrorDialog(String error)
	{
		mMyPendingAlertDialog = new MyPendingAlertDialog(MyAlertDialogType.ERROR,  error);
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
	private PopupMenu mMapOptionsMenu;

	private int mProgressBarStep, mProgressBarTotSteps;
	private int mAdditionalProgressBarStep, mProgressBarAdditionalSteps;

	private boolean mReportConditionsAccepted;

	ViewPager mViewPager;
	TabsAdapter mTabsAdapter;
	LinearLayout mMainLayout;

	public static final int REPORT_ACTIVITY_FOR_RESULT_ID = Activity.RESULT_FIRST_USER + 100;
	public static final int TUTORIAL_ACTIVITY_FOR_RESULT_ID = Activity.RESULT_FIRST_USER + 101;
	public static final int SETTINGS_ACTIVITY_FOR_RESULT_ID = Activity.RESULT_FIRST_USER + 102;

	private MyPendingAlertDialog mMyPendingAlertDialog;


	int availCnt = 0;
}
