package it.giacomos.android.osmer;

import it.giacomos.android.osmer.downloadManager.Data.DataPool;
import it.giacomos.android.osmer.downloadManager.Data.DataPoolBitmapListener;
import it.giacomos.android.osmer.downloadManager.Data.DataPoolTextListener;
import it.giacomos.android.osmer.widgets.ODoubleLayerImageView;
import it.giacomos.android.osmer.widgets.ForecastTextView;
import it.giacomos.android.osmer.widgets.SituationImage;
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
		Log.e("ForecastFragment " , "constructor");
		mImageView = null;
		mTextView = null;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
	{
		View view = null;
		
		Bundle args = getArguments();
		mType = args.getInt("type");
		
		/* Get the reference to the data pool in order to register for events */
		DataPool dataPool = DataPool.Instance();
		
		if(mType == R.string.today_title)
		{
			view = inflater.inflate(R.layout.today, null);
			mTextView = (ForecastTextView)view.findViewById(R.id.todayTextView);
			mTextView.setViewType(ViewType.TODAY);
			mImageView = (ODoubleLayerImageView) view.findViewById(R.id.todayImageView);
			mImageView.setBitmapType(BitmapType.TODAY);
			Log.e("ForecastFragment " , "onCreateView type today " + container);
			dataPool.registerBitmapListener(BitmapType.TODAY, this);
			dataPool.registerTextListener(ViewType.TODAY, this);
		}
		else if(mType == R.string.tomorrow_title)
		{
			view = inflater.inflate(R.layout.tomorrow, null);
			mTextView = (ForecastTextView)view.findViewById(R.id.tomorrowTextView);
			mTextView.setViewType(ViewType.TOMORROW);
			mImageView = (ODoubleLayerImageView) view.findViewById(R.id.tomorrowImageView);
			mImageView.setBitmapType(BitmapType.TOMORROW);
			Log.e("ForecastFragment " , "onCreateView type tomorrow " + container);
			dataPool.registerBitmapListener(BitmapType.TOMORROW, this);
			dataPool.registerTextListener(ViewType.TOMORROW, this);
		}
		else if(mType == R.string.two_days_title)
		{
			view = inflater.inflate(R.layout.twodays, null);
			mTextView = (ForecastTextView)view.findViewById(R.id.twoDaysTextView);
			mTextView.setViewType(ViewType.TWODAYS);
			mImageView = (ODoubleLayerImageView) view.findViewById(R.id.twoDaysImageView);
			mImageView.setBitmapType(BitmapType.TWODAYS);
			Log.e("ForecastFragment " , "onCreateView type 2 days " + container);
			dataPool.registerBitmapListener(BitmapType.TWODAYS, this);
			dataPool.registerTextListener(ViewType.TWODAYS, this);
		}
		return view;
	}
	
	public void onDestroy()
	{
		super.onDestroy();
		if(mImageView != null)
		{
			Log.e("ForecastFragment: onDestroy", "NOT unbinding drawables on " + mType + " and unregistering listeners on DataPool");
			DataPool.Instance().unregisterBitmapListener(mImageView.getBitmapType());
			mImageView.unbindDrawables();
			/* if mImageView is not null, then also mTextView is not null */
			DataPool.Instance().unregisterTextListener(mTextView.getViewType());
		}
	}

	@Override
	public void onBitmapChanged(Bitmap bmp, BitmapType t, boolean fromCache) 
	{
		Log.e("onBitmapChanged", "bitmap null? " + (bmp == null) + " fromCache" + fromCache);
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
