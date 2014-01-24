package it.giacomos.android.osmer.PROva.widgets;

import it.giacomos.android.osmer.PROva.network.Data.DataPoolTextListener;
import it.giacomos.android.osmer.PROva.network.state.ViewType;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.TextView;

/** Text change listener is installed here because this class restores from internal storage on her own
 * If it wasn't for that, the text change event may be triggered elsewhere (for the text to image decoding)
 * @author giacomo
 *
 */
public class OTextView extends TextView implements DataPoolTextListener
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

	@Override
	public void onTextChanged(String txt, ViewType t, boolean fromCache) 
	{
		setHtml(txt);
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
