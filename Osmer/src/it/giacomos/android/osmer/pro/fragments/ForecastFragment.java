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
import it.giacomos.android.osmer.pro.widgets.ForecastTextView;
import it.giacomos.android.osmer.pro.widgets.ODoubleLayerImageView;
import android.support.v4.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ForecastFragment extends Fragment implements DataPoolTextListener, DataPoolBitmapListener 
{
	private int mType;
	private ODoubleLayerImageView mImageView;
	private ForecastTextView mTextView;
	
	public ForecastFragment() 
	{
		super();
		mImageView = null;
		mTextView = null;
//		Log.e("ForecastFragment", "constructor");
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		String text = "";
		Bitmap bitmap = null;
		OsmerActivity activity = (OsmerActivity) getActivity();
		/* register as a listener of DataPool */
		DataPool dataPool = activity.getDataPool();
		DataPoolCacheUtils dataCacheUtils = new DataPoolCacheUtils();
		if(mType == R.string.today_title)
		{
			text = dataCacheUtils.loadFromStorage(ViewType.TODAY, getActivity().getApplicationContext());
			bitmap = dataCacheUtils.loadFromStorage(BitmapType.TODAY, getActivity().getApplicationContext());
			dataPool.registerBitmapListener(BitmapType.TODAY, this);
			dataPool.registerTextListener(ViewType.TODAY, this);
		}
		else if(mType == R.string.tomorrow_title)
		{
			dataPool.registerBitmapListener(BitmapType.TOMORROW, this);
			dataPool.registerTextListener(ViewType.TOMORROW, this);
			text = dataCacheUtils.loadFromStorage(ViewType.TOMORROW, getActivity().getApplicationContext());
			bitmap = dataCacheUtils.loadFromStorage(BitmapType.TOMORROW, getActivity().getApplicationContext());
		}
		else if(mType == R.string.two_days_title)
		{
			dataPool.registerBitmapListener(BitmapType.TWODAYS, this);
			dataPool.registerTextListener(ViewType.TWODAYS, this);
			text = dataCacheUtils.loadFromStorage(ViewType.TWODAYS, getActivity().getApplicationContext());
			bitmap = dataCacheUtils.loadFromStorage(BitmapType.TWODAYS, getActivity().getApplicationContext());
		}
		mTextView.setHtml(mTextView.formatText(text));
		mImageView.setBitmap(bitmap);
		dataCacheUtils = null;
		
		/* register image view for location updates */
		LocationService locationService = activity.getLocationService();
		locationService.registerLocationServiceAddressUpdateListener(mImageView);
		locationService.registerLocationServiceUpdateListener(mImageView);
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
	{
		View view = null;
		
		Bundle args = getArguments();
		mType = args.getInt("type");

		if(mType == R.string.today_title)
		{
			view = inflater.inflate(R.layout.today, null);
			mTextView = (ForecastTextView)view.findViewById(R.id.todayTextView);
			mTextView.setViewType(ViewType.TODAY);
			mImageView = (ODoubleLayerImageView) view.findViewById(R.id.todayImageView);
			mImageView.setBitmapType(BitmapType.TODAY);
		}
		else if(mType == R.string.tomorrow_title)
		{
			view = inflater.inflate(R.layout.tomorrow, null);
			mTextView = (ForecastTextView)view.findViewById(R.id.tomorrowTextView);
			mTextView.setViewType(ViewType.TOMORROW);
			mImageView = (ODoubleLayerImageView) view.findViewById(R.id.tomorrowImageView);
			mImageView.setBitmapType(BitmapType.TOMORROW);
			}
		else if(mType == R.string.two_days_title)
		{
			view = inflater.inflate(R.layout.twodays, null);
			mTextView = (ForecastTextView)view.findViewById(R.id.twoDaysTextView);
			mTextView.setViewType(ViewType.TWODAYS);
			mImageView = (ODoubleLayerImageView) view.findViewById(R.id.twoDaysImageView);
			mImageView.setBitmapType(BitmapType.TWODAYS);}

		return view;
	}
	
	public void onDestroy()
	{
		super.onDestroy();
		if(mImageView != null)
		{
			OsmerActivity activity = (OsmerActivity) getActivity();
			activity.getDataPool().unregisterBitmapListener(mImageView.getBitmapType());
			mImageView.unbindDrawables();
			/* if mImageView is not null, then also mTextView is not null */
			activity.getDataPool().unregisterTextListener(mTextView.getViewType());
			/* remove location updates */
			LocationService locationService = activity.getLocationService();
			locationService.removeLocationServiceAddressUpdateListener(mImageView);
			locationService.removeLocationServiceUpdateListener(mImageView);
		}
	}

	@Override
	public void onBitmapChanged(Bitmap bmp, BitmapType t, boolean fromCache) 
	{
//		Log.e("onBitmapChanged", "bitmap null? " + (bmp == null) + " fromCache" + fromCache);
		mImageView.setBitmap(bmp);
	}

	@Override
	public void onBitmapError(String error, BitmapType t) 
	{

	}

	@Override
	public void onTextChanged(String txt, ViewType t, boolean fromCache) 
	{
		mTextView.setHtml(mTextView.formatText(txt));
	}

	@Override
	public void onTextError(String error, ViewType t) 
	{
		mTextView.setHtml(mTextView.formatText(error));
	}
}
