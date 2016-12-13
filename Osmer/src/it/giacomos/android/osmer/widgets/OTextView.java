package it.giacomos.android.osmer.widgets;

import it.giacomos.android.osmer.network.state.ViewType;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.TextView;


public class OTextView extends TextView
{

	public OTextView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);	
		this.setTextColor(Color.BLACK);
		this.setPadding(10, 10, 10, 10);
		mStringType = ViewType.HOME;
		mHtml  = null;
	}

	/* invoked after that the TextTask has completed */
	public final void setHtml(String html)
	{
		if(mHtml == null || !html.equals(mHtml))
		{
			Spanned fromHtml = Html.fromHtml(html);
			mHtml = html;
			setText(fromHtml);
		}
	}

	public void onTextError(String error, ViewType t)
	{

	}

	public void setViewType(ViewType t)
	{
		mStringType = t;
	}

	public ViewType getViewType()
	{
		return mStringType;
	}

	public String getHtml() {
		return mHtml;
	}

	private String mHtml;
	private ViewType mStringType;

}
