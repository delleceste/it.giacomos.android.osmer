package it.giacomos.android.osmer.fragments;

import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.R.id;
import it.giacomos.android.osmer.R.layout;
import it.giacomos.android.osmer.R.string;
import it.giacomos.android.osmer.network.Data.DataPool;
import it.giacomos.android.osmer.network.Data.DataPoolBitmapListener;
import it.giacomos.android.osmer.network.Data.DataPoolTextListener;
import it.giacomos.android.osmer.network.state.BitmapType;
import it.giacomos.android.osmer.network.state.ViewType;
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
		Log.e("SituationFragment " , "constructor");
		mSituationImage = null;
		mHomeTextView = null;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
	{
		View view = null;
		/* Get the reference to the data pool in order to register for events */
		DataPool dataPool = DataPool.Instance();
		view = inflater.inflate(R.layout.home, null);
		mHomeTextView  = (HomeTextView)view.findViewById(R.id.homeTextView);
		mSituationImage = (SituationImage) view.findViewById(R.id.homeImageView);	
		Log.e("ForecastFragment " , "onCreateView type home " + container);
		dataPool.registerTextListener(ViewType.HOME, this);
		((OsmerActivity) getActivity()).getObservationsCache().setLatestObservationCacheChangeListener(mSituationImage);
		mType = R.string.home_title;
		return view;
	}
	
	public void onDestroy()
	{
		super.onDestroy();
		Log.e("ForecastFragment: onDestroy", "unbinding drawables on " + mType + " and unregistering listeners on DataPool");
		if(mSituationImage != null)
		{
			DataPool.Instance().unregisterBitmapListener(mSituationImage.getBitmapType());
			mSituationImage.cleanup();
			DataPool.Instance().unregisterTextListener(mHomeTextView.getViewType());
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
