package it.giacomos.android.osmer;

import it.giacomos.android.osmer.widgets.ODoubleLayerImageView;
import it.giacomos.android.osmer.widgets.OTextView;
import it.giacomos.android.osmer.widgets.SituationImage;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ForecastFragment extends Fragment {

	private int mType;
	private ODoubleLayerImageView mImageView;
	private OTextView mTextView;
	
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
		OTextView ov = null;
		ODoubleLayerImageView dliv = null;
		
		Bundle args = getArguments();
		mType = args.getInt("type");
		Log.e("ForecastFragment: onCreateView", "type is " + mType + " int from byndle "  + args.getInt("type"));
		if(mType == R.string.situation)
		{
			view = inflater.inflate(R.layout.home, null);
			ov = (OTextView)view.findViewById(R.id.homeTextView);
			ov.setStringType(ViewType.HOME);
			dliv = (ODoubleLayerImageView) view.findViewById(R.id.homeImageView);
		}
		else if(mType == R.string.today_title)
		{
			Log.e("ForecastFragment: onCreateView", "inflating today");
			view = inflater.inflate(R.layout.today, null);
			ov = (OTextView)view.findViewById(R.id.todayTextView);
			ov.setStringType(ViewType.TODAY);
			dliv = (ODoubleLayerImageView) view.findViewById(R.id.todayImageView);
		}
		else if(mType == R.string.tomorrow_title)
		{
			Log.e("ForecastFragment: onCreateView", "inflating tomorrow");
			view = inflater.inflate(R.layout.tomorrow, null);
			ov = (OTextView)view.findViewById(R.id.tomorrowTextView);
			ov.setStringType(ViewType.TOMORROW);
			dliv = (ODoubleLayerImageView) view.findViewById(R.id.tomorrowImageView);
		}
		else if(mType == R.string.two_days_title)
		{
			Log.e("ForecastFragment: onCreateView", "inflating 2 days");
			view = inflater.inflate(R.layout.twodays, null);
			ov = (OTextView)view.findViewById(R.id.twoDaysTextView);
			ov.setStringType(ViewType.TWODAYS);
			dliv = (ODoubleLayerImageView) view.findViewById(R.id.twoDaysImageView);
		}
		
		ov.restoreFromInternalStorage();
		dliv.restoreFromInternalStorage();
		
		mImageView = dliv;
		mTextView = ov;
		
		return view;
	}
	
	public void onDestroy()
	{
		super.onDestroy();
		Log.e("ForecastFragment: onDestroy", "unbinding drawables on " + mType);
		if(mImageView != null)
		{
			mImageView.unbindDrawables();
			if(mType == R.id.homeTextView)
				((SituationImage) mImageView).cleanup();
		}
	}
	
}
