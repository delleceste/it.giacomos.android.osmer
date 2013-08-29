package it.giacomos.android.osmer.pro.fragments;

import it.giacomos.android.osmer.pro.OsmerActivity;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.pro.locationUtils.LocationService;
import it.giacomos.android.osmer.pro.network.Data.DataPool;
import it.giacomos.android.osmer.pro.network.Data.DataPoolBitmapListener;
import it.giacomos.android.osmer.pro.network.Data.DataPoolCacheUtils;
import it.giacomos.android.osmer.pro.network.Data.DataPoolTextListener;
import it.giacomos.android.osmer.pro.network.state.BitmapType;
import it.giacomos.android.osmer.pro.network.state.ViewType;
import it.giacomos.android.osmer.pro.observations.ObservationsCache;
import it.giacomos.android.osmer.pro.widgets.HomeTextView;
import it.giacomos.android.osmer.pro.widgets.OTextView;
import it.giacomos.android.osmer.pro.widgets.SituationImage;
import android.support.v4.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SituationFragment extends Fragment implements DataPoolTextListener, DataPoolBitmapListener 
{

	private int mType;
	private SituationImage mSituationImage;
	private OTextView mHomeTextView;
	
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
		mHomeTextView.setHtml(mHomeTextView.formatText(text));
		
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
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
	{
		View view = null;
		/* Get the reference to the data pool in order to register for events */
		view = inflater.inflate(R.layout.home, null);
		mHomeTextView  = (HomeTextView)view.findViewById(R.id.homeTextView);
		mSituationImage = (SituationImage) view.findViewById(R.id.homeImageView);
		mSituationImage.setBitmapType(BitmapType.HOME);mType = R.string.home_title;
		return view;
	}
	
	public void onDestroy()
	{
		super.onDestroy();
		if(mSituationImage != null)
		{
			OsmerActivity oActivity = ((OsmerActivity) getActivity());
			DataPool dataPool = oActivity.getDataPool();
			dataPool.unregisterBitmapListener(mSituationImage.getBitmapType());
			Log.e("SituationImage.onDestroy", "cleaning up image");
			mSituationImage.cleanup();
			/* unregister text listener */
			dataPool.unregisterTextListener(mHomeTextView.getViewType());
			LocationService locationService = oActivity.getLocationService();
			locationService.removeLocationServiceAddressUpdateListener(mSituationImage);
			locationService.removeLocationServiceUpdateListener(mSituationImage);
		}
	}

	@Override
	public void onBitmapChanged(Bitmap bmp, BitmapType t, boolean fromCache) 
	{
		mSituationImage.setBitmap(bmp);
	}

	@Override
	public void onBitmapError(String error, BitmapType t)
	{

	}

	@Override
	public void onTextChanged(String txt, ViewType t, boolean fromCache) 
	{
		mHomeTextView.setHtml(mHomeTextView.formatText(txt));
	}

	@Override
	public void onTextError(String error, ViewType t) 
	{
		mHomeTextView.setHtml(mHomeTextView.formatText(error));
	}
}
