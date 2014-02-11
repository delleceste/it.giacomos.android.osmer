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
import it.giacomos.android.osmer.pro.trial.BuyProActivity;
import it.giacomos.android.osmer.pro.trial.ExpirationChecker;
import it.giacomos.android.osmer.pro.trial.ExpirationCheckerListener;
import it.giacomos.android.osmer.pro.trial.TrialDaysLeftListener;
import it.giacomos.android.osmer.pro.trial.TrialExpiringNotification;
import it.giacomos.android.osmer.pro.widgets.HomeTextView;
import it.giacomos.android.osmer.pro.widgets.OTextView;
import it.giacomos.android.osmer.pro.widgets.SituationImage;
import android.support.v4.app.Fragment;
import android.content.Intent;
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
import android.widget.Toast;

public class SituationFragment extends Fragment implements DataPoolTextListener,
ExpirationCheckerListener, OnClickListener
{
	private SituationImage mSituationImage;
	private OTextView mHomeTextView;
	private ExpirationChecker mExpirationChecker;
	private TrialDaysLeftListener mTrialDaysLeftListener; /* trial version */

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
		mTrialDaysLeftListener = (TrialDaysLeftListener) getActivity();
	}

	public void onResume()
	{	
		super.onResume();
		/* trial version */
		if(getActivity().getPackageName().contains("PROva"))
		{
			/* registers for net status monitor, so inside onResume */
			mExpirationChecker = new ExpirationChecker(this, getActivity());
			mExpirationChecker.start(getActivity());
			updateTrialDaysRemainingText(new Settings(getActivity()).getTrialDaysLeft());
			Button buyButton = (Button) getActivity().findViewById(R.id.btBuy);
			if(buyButton != null) /* not present in landscape mode */
				buyButton.setOnClickListener(this);
		}
	}

	public void onPause()
	{
		super.onPause();
		/* trial version */
		if(mExpirationChecker != null)
			mExpirationChecker.stop(getActivity()); /* unregister from network status monitor */
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

		if(getActivity().getPackageName().contains("PROva"))
		{
			float trialViewHeightDp;
			final float scale = getResources().getDisplayMetrics().density;
			View trialView = inflater.inflate(R.layout.trial_layout, null);
			
			if(getResources().getConfiguration().orientation ==
					Configuration.ORIENTATION_PORTRAIT)
			{
				trialViewHeightDp = 40f;
				trialView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int) (trialViewHeightDp * scale + 0.5f)));
				ViewGroup vg = (ViewGroup) view.findViewById(R.id.homeRelativeLayout);
				vg.addView(trialView, 0);

			}
			else if(getResources().getConfiguration().orientation ==
					Configuration.ORIENTATION_LANDSCAPE)
			{
				trialViewHeightDp = 64f; /* 50dp for landscape because they are put one below each other */
				trialView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int) (trialViewHeightDp * scale + 0.5f)));
				ViewGroup vg = (ViewGroup) view.findViewById(R.id.scrollLayout);
				vg.addView(trialView, 0);
			}
		}
		return view;
	}

	/** 
	 * This function is the 1st one related to the trial version
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
			Intent buyActivityIntent = new Intent(getActivity(), BuyProActivity.class);
			startActivity(buyActivityIntent);
		}

	}
}
