package it.giacomos.android.osmer.fragments;

import it.giacomos.android.osmer.locationUtils.LocationService;
import it.giacomos.android.osmer.network.Data.DataPool;
import it.giacomos.android.osmer.network.Data.DataPoolCacheUtils;
import it.giacomos.android.osmer.network.Data.DataPoolTextListener;
import it.giacomos.android.osmer.network.state.ViewType;
import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.widgets.ForecastTextView;
import it.giacomos.android.osmer.widgets.MapWithForecastImage;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ScrollView;

public class ForecastFragment extends Fragment implements DataPoolTextListener, Runnable
{
	private ViewType mType;
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
    public void onSaveInstanceState(Bundle outState) 
	{
        super.onSaveInstanceState(outState);
        if(mImageView != null)
        	mImageView.saveTouchEventData(outState);
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
		
			if(!dataPool.isTextValid(mType))
			{
				text = dataCacheUtils.loadFromStorage(mType, getActivity().getApplicationContext());
				mTextView.setData(text);
			}
			/* if there already is data for the given ViewType, the listener is immediately called */
			dataPool.registerTextListener(mType, this);
		

		dataCacheUtils = null;

		/* register image view for location updates */
		LocationService locationService = activity.getLocationService();
		locationService.registerLocationServiceAddressUpdateListener(mImageView);
		locationService.registerLocationServiceUpdateListener(mImageView);

		mImageView.restoreTouchEventState(savedInstanceState);        
		
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
		if(mType == ViewType.TODAY)
		{
			if(!dataPool.isTextValid(ViewType.TODAY_SYMTABLE))
			{
				symtab = dataCacheUtils.loadFromStorage(ViewType.TODAY_SYMTABLE, getActivity().getApplicationContext());
				mImageView.setSymTable(symtab);
			}
			/* if there already is data for the given ViewType, the listener is immediately called */
			dataPool.registerTextListener(ViewType.TODAY_SYMTABLE, this);
		}
		else if(mType == ViewType.TOMORROW)
		{
			dataPool.registerTextListener(ViewType.TOMORROW_SYMTABLE, this);
			if(!dataPool.isTextValid(ViewType.TOMORROW_SYMTABLE))	
			{
				symtab = dataCacheUtils.loadFromStorage(ViewType.TOMORROW_SYMTABLE, getActivity().getApplicationContext());
				mImageView.setSymTable(symtab);
			}
		}
		else if(mType == ViewType.TWODAYS)
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
		else if(mType == ViewType.THREEDAYS)
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
		else if(mType == ViewType.FOURDAYS)
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
		
		mImageView.setForecastImgTouchEventListener(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View view = null;

		Bundle args = getArguments();
		mType = ViewType.valueOf(args.getString("ViewType"));

		if(mType == ViewType.TODAY)
		{
			view = inflater.inflate(R.layout.today, null);
			mTextView = (ForecastTextView)view.findViewById(R.id.todayTextView);
			mTextView.setViewType(ViewType.TODAY);
			mImageView = (MapWithForecastImage) view.findViewById(R.id.todayImageView);
			mImageView.setViewType(ViewType.TODAY_SYMTABLE);
			/* area touch listener only for today, tomorrow, two days */
			mImageView.setAreaTouchListener(mTextView);
		}
		else if(mType == ViewType.TOMORROW)
		{
			view = inflater.inflate(R.layout.tomorrow, null);
			mTextView = (ForecastTextView)view.findViewById(R.id.tomorrowTextView);
			mTextView.setViewType(ViewType.TOMORROW);
			mImageView = (MapWithForecastImage) view.findViewById(R.id.tomorrowImageView);
			mImageView.setViewType(ViewType.TOMORROW_SYMTABLE);
			/* area touch listener only for today, tomorrow, two days */
			mImageView.setAreaTouchListener(mTextView);
		}
		else if(mType == ViewType.TWODAYS)
		{
			view = inflater.inflate(R.layout.twodays, null);
			mTextView = (ForecastTextView)view.findViewById(R.id.twoDaysTextView);
			mTextView.setViewType(ViewType.TWODAYS);
			mImageView = (MapWithForecastImage) view.findViewById(R.id.twoDaysImageView);
			mImageView.setViewType(ViewType.TWODAYS_SYMTABLE);
			/* area touch listener only for today, tomorrow, two days */
			mImageView.setAreaTouchListener(mTextView);
		}
		else if(mType == ViewType.THREEDAYS)
		{
			view = inflater.inflate(R.layout.threedays, null);
			mTextView = (ForecastTextView)view.findViewById(R.id.threeDaysTextView);
			mTextView.setViewType(ViewType.THREEDAYS);
			mImageView = (MapWithForecastImage) view.findViewById(R.id.threeDaysImageView);
			mImageView.setViewType(ViewType.THREEDAYS_SYMTABLE);
		}
		else if(mType == ViewType.FOURDAYS)
		{
			view = inflater.inflate(R.layout.fourdays, null);
			mTextView = (ForecastTextView)view.findViewById(R.id.fourDaysTextView);
			mTextView.setViewType(ViewType.FOURDAYS);
			mImageView = (MapWithForecastImage) view.findViewById(R.id.fourDaysImageView);
			mImageView.setViewType(ViewType.FOURDAYS_SYMTABLE);
		}
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
		Log.e("ForecastFragment.onTextChanged", " view type " + t + " FromCache " + fromCache);
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
		Log.e("ForecastFragment.onTextError", " view type " + t );
		mTextView.setHtml(error);
	}
}
