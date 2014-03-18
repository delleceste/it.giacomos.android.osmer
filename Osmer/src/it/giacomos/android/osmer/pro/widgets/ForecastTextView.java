package it.giacomos.android.osmer.pro.widgets;

import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.pro.network.state.ViewType;
import android.content.Context;
import android.util.AttributeSet;

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
		if(mStringMap.length > index)
		{
			mCurrentIndex = index;
			setHtml(mStringMap[index]);
		}
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
		setHtmlAt(id);
	}
}
