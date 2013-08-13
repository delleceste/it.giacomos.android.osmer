package it.giacomos.android.osmer.fragments;

import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.R.id;
import it.giacomos.android.osmer.R.layout;
import it.giacomos.android.osmer.R.string;
import it.giacomos.android.osmer.network.Data.DataPool;
import it.giacomos.android.osmer.network.Data.DataPoolBitmapListener;
import it.giacomos.android.osmer.network.Data.DataPoolCacheUtils;
import it.giacomos.android.osmer.network.Data.DataPoolTextListener;
import it.giacomos.android.osmer.network.state.BitmapType;
import it.giacomos.android.osmer.network.state.ViewType;
import it.giacomos.android.osmer.observations.ObservationsCache;
import it.giacomos.android.osmer.widgets.HomeTextView;
import it.giacomos.android.osmer.widgets.ODoubleLayerImageView;
import it.giacomos.android.osmer.widgets.OTextView;
import it.giacomos.android.osmer.widgets.SituationImage;
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

		DataPoolCacheUtils dataCacheUtils = new DataPoolCacheUtils();
		/* register as a listener of DataPool */
		DataPool dataPool = ((OsmerActivity) getActivity()).getDataPool();
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
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
	{
		View view = null;
		/* Get the reference to the data pool in order to register for events */
		view = inflater.inflate(R.layout.home, null);
		mHomeTextView  = (HomeTextView)view.findViewById(R.id.homeTextView);
		Log.e("SituationImage.onCreateView", "creating situation image");
		mSituationImage = (SituationImage) view.findViewById(R.id.homeImageView);
		mSituationImage.setBitmapType(BitmapType.HOME);mType = R.string.home_title;
		return view;
	}
	
	public void onDestroy()
	{
		super.onDestroy();
		if(mSituationImage != null)
		{
			DataPool dataPool = ((OsmerActivity) getActivity()).getDataPool();
			dataPool.unregisterBitmapListener(mSituationImage.getBitmapType());
			Log.e("SituationImage.onDestroy", "cleaning up image");
			mSituationImage.cleanup();
			/* unregister text listener */
			dataPool.unregisterTextListener(mHomeTextView.getViewType());
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
