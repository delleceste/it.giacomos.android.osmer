package it.giacomos.android.osmer.widgets;

import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.network.state.ViewType;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;

public class ForecastTextView extends OTextView implements AreaTouchListener 
{
	private String[] mStringMap;
	private int mCurrentIndex;
	
	public ForecastTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mCurrentIndex = 0;
	}
	
	public void setData(String txt)
	{
		mStringMap = txt.split("<SEP>");
		/* update text if necessary */
		update();
	}
	
	public void update()
	{
		if(mStringMap == null)
			return;
		if(mStringMap.length > mCurrentIndex)
			setHtml(mStringMap[mCurrentIndex]);
		else if(mStringMap.length > 0)
			setHtml(mStringMap[0]);
		else
			setHtml(getResources().getString(R.string.data_missing) + " id " + mCurrentIndex);
	}
	
	@Override
	public void onAreaTouched(int id) 
	{
		mCurrentIndex = id;
		update();
	}
}
