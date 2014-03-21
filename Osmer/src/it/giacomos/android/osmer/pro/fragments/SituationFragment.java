package it.giacomos.android.osmer.pro.fragments;

import it.giacomos.android.osmer.pro.OsmerActivity;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.pro.locationUtils.LocationService;
import it.giacomos.android.osmer.pro.network.Data.DataPool;
import it.giacomos.android.osmer.pro.network.Data.DataPoolCacheUtils;
import it.giacomos.android.osmer.pro.network.Data.DataPoolTextListener;
import it.giacomos.android.osmer.pro.network.state.ViewType;
import it.giacomos.android.osmer.pro.observations.ObservationsCache;
import it.giacomos.android.osmer.pro.preferences.Settings;
import it.giacomos.android.osmer.pro.purhcase.InAppUpgradeManager;
import it.giacomos.android.osmer.pro.purhcase.InAppUpgradeManagerListener;
import it.giacomos.android.osmer.pro.trial.ExpirationChecker;
import it.giacomos.android.osmer.pro.trial.ExpirationCheckerListener;
import it.giacomos.android.osmer.pro.trial.InAppEventListener;
import it.giacomos.android.osmer.pro.widgets.HomeTextView;
import it.giacomos.android.osmer.pro.widgets.OTextView;
import it.giacomos.android.osmer.pro.widgets.SituationImage;
import android.support.v4.app.Fragment;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

public class SituationFragment extends Fragment implements DataPoolTextListener,
ExpirationCheckerListener, OnClickListener, InAppUpgradeManagerListener
{
	private SituationImage mSituationImage;
	private OTextView mHomeTextView;
	private ExpirationChecker mExpirationChecker;
	private InAppEventListener mTrialDaysLeftListener; /* trial version */
	private InAppUpgradeManager mInAppUpgradeManager;
	private String mInAppUpgradeManagerErrorMsg;
	private boolean mIsUnlimited;

	public SituationFragment() 
	{
		super();
		mSituationImage = null;
		mHomeTextView = null;
	}

	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		OsmerActivity oActivity = (OsmerActivity) getActivity();
		DataPoolCacheUtils dataCacheUtils = new DataPoolCacheUtils();
		/* register as a listener of DataPool */
		DataPool dataPool = oActivity.getDataPool();
		dataPool.registerTextListener(ViewType.HOME, this);
		String text = dataCacheUtils.loadFromStorage(ViewType.HOME, getActivity().getApplicationContext());
		mHomeTextView.setHtml(text);

		/* Get the reference to the observations cache */
		ObservationsCache observationsCache = ((OsmerActivity) getActivity()).getObservationsCache();
		/* register for latest observation cache changes.
		 * Since OsmerActivity.init has initialized the ObservationsCache maps with the cached
		 * observation tables, mSituationImage will be immediately notified of the observations
		 * change and so it will initialize with the cached observations.
		 */
		observationsCache.setLatestObservationCacheChangeListener(mSituationImage);
		/* register image view for location updates */
		LocationService locationService = oActivity.getLocationService();
		locationService.registerLocationServiceAddressUpdateListener(mSituationImage);
		locationService.registerLocationServiceUpdateListener(mSituationImage);

		mExpirationChecker = null;
		mTrialDaysLeftListener = (InAppEventListener) getActivity();
	}

	public void onResume()
	{	
		super.onResume();
		mInAppUpgradeManager = new InAppUpgradeManager();
		mInAppUpgradeManager.addInAppUpgradeManagerListener(this);
		mInAppUpgradeManager.checkIfPurchased(getActivity());
	}

	public void onPause()
	{
		super.onPause();
		/* trial version */
		if(mExpirationChecker != null)
			mExpirationChecker.stop(getActivity()); /* unregister from network status monitor */
		if(mInAppUpgradeManager != null)
			mInAppUpgradeManager.dispose();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View view = null;
		/* Get the reference to the data pool in order to register for events */
		view = inflater.inflate(R.layout.home, null);
		mHomeTextView  = (HomeTextView)view.findViewById(R.id.homeTextView);
		mSituationImage = (SituationImage) view.findViewById(R.id.child1ImageView);
		mSituationImage.setViewType(ViewType.HOME);

		return view;
	}

	private View mSetupTrialInterface()
	{
		float trialViewHeightDp;
		final float scale = getResources().getDisplayMetrics().density;
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View trialView = inflater.inflate(R.layout.trial_layout, null);

		if(getResources().getConfiguration().orientation ==
				Configuration.ORIENTATION_PORTRAIT)
		{
			View view = getActivity().findViewById(R.id.mainScrollView);
			trialViewHeightDp = 40f;
			//trialView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int) (trialViewHeightDp * scale + 0.5f)));
			trialView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			ViewGroup vg = (ViewGroup) view.findViewById(R.id.homeRelativeLayout);
			vg.addView(trialView, 0);

		}
		else if(getResources().getConfiguration().orientation ==
				Configuration.ORIENTATION_LANDSCAPE)
		{
			View view = getActivity().findViewById(R.id.homeRelativeLayout);
			trialViewHeightDp = 64f; /* 50dp for landscape because they are put one below each other */
//			trialView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int) (trialViewHeightDp * scale + 0.5f)));
			trialView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			ViewGroup vg = (ViewGroup) view.findViewById(R.id.scrollLayout);
			vg.addView(trialView, 0);
		}
		return trialView;
	}
	
	/** 
	 * This function is the 1st one related to the trial version.
	 * Implements ExpirationCheckerListener. 
	 * ExpirationChecker is created and started only if the application _is_ _NOT_ unlimited,
	 * that is, if it has not been purchased.
	 */
	@Override
	public void onTrialDaysRemaining(int days) 
	{
		mTrialDaysLeftListener.onTrialDaysRemaining(days);
		updateTrialDaysRemainingText(days);
	}

	/** 
	 * This function is the 2nd  one related to the trial version
	 */
	private void updateTrialDaysRemainingText(int days)
	{
		String expiringMsg = getString(R.string.trial_version) + ": " + days + " " +
				getString(R.string.days_left);
		TextView expiTV = (TextView) getActivity().findViewById(R.id.tvdaysLeft);
		if(days < 3)
			expiTV.setTextColor(Color.RED);
		expiTV.setText(expiringMsg);
	}

	public void onDestroy()
	{
		super.onDestroy();
		if(mSituationImage != null)
		{
			OsmerActivity oActivity = ((OsmerActivity) getActivity());
			DataPool dataPool = oActivity.getDataPool();
			mSituationImage.cleanup();
			/* unregister text listener */
			dataPool.unregisterTextListener(mHomeTextView.getViewType());
			LocationService locationService = oActivity.getLocationService();
			locationService.removeLocationServiceAddressUpdateListener(mSituationImage);
			locationService.removeLocationServiceUpdateListener(mSituationImage);
		}
	}

	@Override
	public void onTextChanged(String txt, ViewType t, boolean fromCache) 
	{
		mHomeTextView.setHtml(txt);
	}

	@Override
	public void onTextError(String error, ViewType t) 
	{
		mHomeTextView.setHtml(error);
	}

	@Override
	public void onClick(View v) 
	{
		if(v.getId() == R.id.btBuy)
		{
			mInAppUpgradeManager.purchase(getActivity());
		}
	}

	@Override
	public void onPurchaseComplete(boolean ok, String error, boolean purchased) 
	{
		if(ok && purchased)
			getActivity().findViewById(R.id.trialLayout).setVisibility(View.GONE);
		/* cache the information locally */
		new Settings(this.getActivity()).setApplicationPurchased(purchased && ok);
	}

	@Override
	public void onCheckComplete(boolean ok, String error, boolean purchased) 
	{
		if(!ok)
			mInAppUpgradeManagerErrorMsg = error;
		else
			mInAppUpgradeManagerErrorMsg = "";

		mIsUnlimited = purchased;
		
		
		/// TEST!
		/// mIsUnlimited = true;
		
		/* cache the information locally (the service uses this shared preferences cached value) */
		new Settings(this.getActivity()).setApplicationPurchased(mIsUnlimited);
		
		View trialView = getActivity().findViewById(R.id.trialLayout);
		
		/* if the application hasn't been purchased yet, check the expiration time.
		 * Otherwise, no expiration time check is performed.
		 */
		if(!mIsUnlimited) /* check if trial days are left */
		{
			int daysLeft = new Settings(getActivity()).getTrialDaysLeft();
			if(trialView == null)
				trialView = mSetupTrialInterface();
			trialView.setVisibility(View.VISIBLE);
//			Log.e("SituationFragment.onCheckComplete", "this version is not unlimited: looking for expiration time...");
			/* registers for net status monitor, so inside onResume */
			mExpirationChecker = new ExpirationChecker(this, getActivity());
			
			if(mExpirationChecker.timeToCheck())
				mExpirationChecker.start(getActivity());
			else /* notify activity with stored value */
				mTrialDaysLeftListener.onTrialDaysRemaining(daysLeft);
			
			updateTrialDaysRemainingText(daysLeft);
			Button buyButton = (Button) getActivity().findViewById(R.id.btBuy);
			if(buyButton != null) /* not present in landscape mode */
				buyButton.setOnClickListener(this);
		}
		else
		{
			/* hide trial text and buttons. The app is unlimited. Nothing to do */
			if(trialView != null)
				trialView.setVisibility(View.GONE);
		}
	}

	@Override
	public void onInAppSetupComplete(boolean success, String message) {
		// TODO Auto-generated method stub

	}
}
