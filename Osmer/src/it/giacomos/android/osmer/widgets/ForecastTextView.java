package it.giacomos.android.osmer.widgets;

import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.network.state.ViewType;
import android.content.Context;
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
		setHtmlAt(mCurrentIndex);
	}
	
	public void setHtmlAt(int index)
	{
		mCurrentIndex = index;
		if(mStringMap == null)
			return;
		if(mStringMap.length > index)
			setHtml(mStringMap[index]);
		else if(mStringMap.length > 0)
			setHtml(mStringMap[0]);
		else
			setHtml(getResources().getString(R.string.data_missing) + " id " + index);
	}
	
	@Override
	public void onTextChanged(String txt, ViewType t, boolean fromCache) 
	{
		setData(txt);
	}
	
	@Override
	public void onAreaTouched(int id) 
	{
		mCurrentIndex = id;
		Log.e("ForecastTextView", "onAreaTouched " + id);
		setHtmlAt(id);
	}
}
