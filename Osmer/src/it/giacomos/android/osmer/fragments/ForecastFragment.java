package it.giacomos.android.osmer.fragments;

import it.giacomos.android.osmer.locationUtils.LocationService;
import it.giacomos.android.osmer.network.Data.DataPool;
import it.giacomos.android.osmer.network.Data.DataPoolCacheUtils;
import it.giacomos.android.osmer.network.Data.DataPoolTextListener;
import it.giacomos.android.osmer.network.state.ViewType;
import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.pro.R;
import it.giacomos.android.osmer.widgets.ForecastTextView;
import it.giacomos.android.osmer.widgets.MapWithForecastImage;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ForecastFragment extends Fragment implements DataPoolTextListener, Runnable
{
	private int mType;
	private MapWithForecastImage mImageView;
	private ForecastTextView mTextView;
	private Handler mHandler;

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
		OsmerActivity activity = (OsmerActivity) getActivity();
		/* register as a listener of DataPool */
		DataPool dataPool = activity.getDataPool();
		DataPoolCacheUtils dataCacheUtils = new DataPoolCacheUtils();
		if(mType == R.string.today_title)
		{
			if(!dataPool.isTextValid(ViewType.TODAY))
			{
				text = dataCacheUtils.loadFromStorage(ViewType.TODAY, getActivity().getApplicationContext());
				mTextView.setData(text);
			}
			/* if there already is data for the given ViewType, the listener is immediately called */
			dataPool.registerTextListener(ViewType.TODAY, this);
		}
		else if(mType == R.string.tomorrow_title)
		{
			dataPool.registerTextListener(ViewType.TOMORROW, this);

			if(!dataPool.isTextValid(ViewType.TOMORROW))
			{
				text = dataCacheUtils.loadFromStorage(ViewType.TOMORROW, getActivity().getApplicationContext());
				mTextView.setData(text);
			}
		}
		else if(mType == R.string.two_days_title)
		{
			dataPool.registerTextListener(ViewType.TWODAYS, this);
			if(!dataPool.isTextValid(ViewType.TWODAYS))
			{
				text = dataCacheUtils.loadFromStorage(ViewType.TWODAYS, getActivity().getApplicationContext());
				mTextView.setData(text);
			}
		}
		else if(mType == R.string.three_days_title)
		{
			dataPool.registerTextListener(ViewType.THREEDAYS, this);
			if(!dataPool.isTextValid(ViewType.THREEDAYS))
			{
				text = dataCacheUtils.loadFromStorage(ViewType.THREEDAYS, getActivity().getApplicationContext());
				mTextView.setData(text);
			}
		}
		else if(mType == R.string.four_days_title)
		{
			dataPool.registerTextListener(ViewType.FOURDAYS, this);
			if(!dataPool.isTextValid(ViewType.FOURDAYS))
			{
				text = dataCacheUtils.loadFromStorage(ViewType.FOURDAYS, getActivity().getApplicationContext());
				mTextView.setData(text);
			}
		}

		dataCacheUtils = null;

		/* register image view for location updates */
		LocationService locationService = activity.getLocationService();
		locationService.registerLocationServiceAddressUpdateListener(mImageView);
		locationService.registerLocationServiceUpdateListener(mImageView);

		mHandler = new Handler();
		mHandler.postDelayed(this, 200);
	}

	public void run()
	{
		String symtab = "";
		OsmerActivity activity = (OsmerActivity) getActivity();
		/* register as a listener of DataPool */
		DataPool dataPool = activity.getDataPool();
		DataPoolCacheUtils dataCacheUtils = new DataPoolCacheUtils();
		if(mType == R.string.today_title)
		{
			if(!dataPool.isTextValid(ViewType.TODAY_SYMTABLE))
			{
				symtab = dataCacheUtils.loadFromStorage(ViewType.TODAY_SYMTABLE, getActivity().getApplicationContext());
				mImageView.setSymTable(symtab);
			}
			/* if there already is data for the given ViewType, the listener is immediately called */
			dataPool.registerTextListener(ViewType.TODAY_SYMTABLE, this);
		}
		else if(mType == R.string.tomorrow_title)
		{
			dataPool.registerTextListener(ViewType.TOMORROW_SYMTABLE, this);
			if(!dataPool.isTextValid(ViewType.TOMORROW_SYMTABLE))	
			{
				symtab = dataCacheUtils.loadFromStorage(ViewType.TOMORROW_SYMTABLE, getActivity().getApplicationContext());
				mImageView.setSymTable(symtab);
			}
		}
		else if(mType == R.string.two_days_title)
		{
			dataPool.registerTextListener(ViewType.TWODAYS_SYMTABLE, this);
			if(!dataPool.isTextValid(ViewType.TWODAYS_SYMTABLE))		
			{
				symtab = dataCacheUtils.loadFromStorage(ViewType.TWODAYS_SYMTABLE, getActivity().getApplicationContext());
				/* load symtab even if empty, because in twodays symtable it means data available
				 * in the afternoon.
				 */
				mImageView.setSymTable(symtab);
			}
		}
		else if(mType == R.string.three_days_title)
		{
			dataPool.registerTextListener(ViewType.THREEDAYS_SYMTABLE, this);
			if(!dataPool.isTextValid(ViewType.THREEDAYS_SYMTABLE))		
			{
				symtab = dataCacheUtils.loadFromStorage(ViewType.THREEDAYS_SYMTABLE, getActivity().getApplicationContext());
				/* load symtab even if empty, because in twodays symtable it means data available
				 * in the afternoon.
				 */
				mImageView.setSymTable(symtab);
			}
		}
		else if(mType == R.string.four_days_title)
		{
			dataPool.registerTextListener(ViewType.FOURDAYS_SYMTABLE, this);
			if(!dataPool.isTextValid(ViewType.FOURDAYS_SYMTABLE))		
			{
				symtab = dataCacheUtils.loadFromStorage(ViewType.FOURDAYS_SYMTABLE, getActivity().getApplicationContext());
				/* load symtab even if empty, because in twodays symtable it means data available
				 * in the afternoon.
				 */
				mImageView.setSymTable(symtab);
			}
		}
		dataCacheUtils = null;
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
			mImageView = (MapWithForecastImage) view.findViewById(R.id.todayImageView);
			mImageView.setViewType(ViewType.TODAY_SYMTABLE);
		}
		else if(mType == R.string.tomorrow_title)
		{
			view = inflater.inflate(R.layout.tomorrow, null);
			mTextView = (ForecastTextView)view.findViewById(R.id.tomorrowTextView);
			mTextView.setViewType(ViewType.TOMORROW);
			mImageView = (MapWithForecastImage) view.findViewById(R.id.tomorrowImageView);
			mImageView.setViewType(ViewType.TOMORROW_SYMTABLE);
		}
		else if(mType == R.string.two_days_title)
		{
			view = inflater.inflate(R.layout.twodays, null);
			mTextView = (ForecastTextView)view.findViewById(R.id.twoDaysTextView);
			mTextView.setViewType(ViewType.TWODAYS);
			mImageView = (MapWithForecastImage) view.findViewById(R.id.twoDaysImageView);
			mImageView.setViewType(ViewType.TWODAYS_SYMTABLE);
		}
		else if(mType == R.string.three_days_title)
		{
			view = inflater.inflate(R.layout.threedays, null);
			mTextView = (ForecastTextView)view.findViewById(R.id.threeDaysTextView);
			mTextView.setViewType(ViewType.THREEDAYS);
			mImageView = (MapWithForecastImage) view.findViewById(R.id.threeDaysImageView);
			mImageView.setViewType(ViewType.THREEDAYS_SYMTABLE);
		}
		else if(mType == R.string.four_days_title)
		{
			view = inflater.inflate(R.layout.fourdays, null);
			mTextView = (ForecastTextView)view.findViewById(R.id.fourDaysTextView);
			mTextView.setViewType(ViewType.FOURDAYS);
			mImageView = (MapWithForecastImage) view.findViewById(R.id.fourDaysImageView);
			mImageView.setViewType(ViewType.FOURDAYS_SYMTABLE);
		}

		mImageView.setAreaTouchListener(mTextView);
		return view;
	}

	public void onDestroy()
	{
		super.onDestroy();
		/* in case this is destroyed before handler timeout... */
		if(mHandler != null)
			mHandler.removeCallbacks(this);

		if(mImageView != null)
		{
			OsmerActivity activity = (OsmerActivity) getActivity();
			activity.getDataPool().unregisterTextListener(mImageView.getViewType());
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
	public void onTextChanged(String txt, ViewType t, boolean fromCache) 
	{
		//		Log.e("ForecastFragment.onTextChanged", "viewType " + t + " fromCache " + fromCache);
		if(t == ViewType.TODAY || t == ViewType.TOMORROW || t == ViewType.TWODAYS
				|| t == ViewType.THREEDAYS || t == ViewType.FOURDAYS)
			mTextView.setData(txt);
		else if(t == ViewType.TODAY_SYMTABLE || t == ViewType.TOMORROW_SYMTABLE || t == ViewType.TWODAYS_SYMTABLE
				|| t == ViewType.THREEDAYS_SYMTABLE || t == ViewType.FOURDAYS_SYMTABLE)
			mImageView.setSymTable(txt);

	}

	@Override
	public void onTextError(String error, ViewType t) 
	{
		mTextView.setHtml(error);
	}
}
