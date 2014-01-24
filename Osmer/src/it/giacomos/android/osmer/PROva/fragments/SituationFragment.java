package it.giacomos.android.osmer.PROva.fragments;

import it.giacomos.android.osmer.PROva.OsmerActivity;
import it.giacomos.android.osmer.PROva.R;
import it.giacomos.android.osmer.PROva.locationUtils.LocationService;
import it.giacomos.android.osmer.PROva.network.Data.DataPool;
import it.giacomos.android.osmer.PROva.network.Data.DataPoolCacheUtils;
import it.giacomos.android.osmer.PROva.network.Data.DataPoolTextListener;
import it.giacomos.android.osmer.PROva.network.state.ViewType;
import it.giacomos.android.osmer.PROva.observations.ObservationsCache;
import it.giacomos.android.osmer.PROva.preferences.Settings;
import it.giacomos.android.osmer.PROva.trial.BuyProActivity;
import it.giacomos.android.osmer.PROva.trial.ExpirationChecker;
import it.giacomos.android.osmer.PROva.trial.ExpirationCheckerListener;
import it.giacomos.android.osmer.PROva.trial.TrialExpiringNotification;
import it.giacomos.android.osmer.PROva.widgets.HomeTextView;
import it.giacomos.android.osmer.PROva.widgets.OTextView;
import it.giacomos.android.osmer.PROva.widgets.SituationImage;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SituationFragment extends Fragment implements DataPoolTextListener,
ExpirationCheckerListener, OnClickListener
{
	private SituationImage mSituationImage;
	private OTextView mHomeTextView;
	private ExpirationChecker mExpirationChecker;
	
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
	}
	
	public void onResume()
	{	
		super.onResume();
		/* trial version */
		/* registers for net status monitor, so inside onResume */
		mExpirationChecker = new ExpirationChecker(this, getActivity());
		mExpirationChecker.start(getActivity());
		updateTrialDaysRemainingText(new Settings(getActivity()).getTrialDaysLeft());
		Button buyButton = (Button) getActivity().findViewById(R.id.btBuy);
		if(buyButton != null) /* not present in landscape mode */
			buyButton.setOnClickListener(this);
	}
	
	public void onPause()
	{
		super.onPause();
		/* trial version */
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
		return view;
	}
	
	/** 
	 * This function is the 1st one related to the trial version
	 */
	@Override
	public void onTrialDaysRemaining(int days) 
	{
		new Settings(getActivity()).setTrialDaysLeft(days);
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
		if(days <= 0)
		{
			Toast.makeText(getActivity(), R.string.trial_expired, Toast.LENGTH_LONG).show();
			Intent activityIntent = new Intent(getActivity(), BuyProActivity.class);
			startActivity(activityIntent);
			getActivity().finish();
		}
		else if(days < 3)
		{
			expiTV.setTextColor(Color.RED);
			TrialExpiringNotification ten = new TrialExpiringNotification();
			ten.show(getActivity(), days);
		}
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
