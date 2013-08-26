package it.giacomos.android.osmer.pro.guiHelpers;

import android.text.util.Linkify;
import it.giacomos.android.osmer.pro.OsmerActivity;
import it.giacomos.android.osmer.pro.R;
import it.giacomos.android.osmer.pro.ViewType;
import it.giacomos.android.osmer.pro.widgets.OTextView;

public class TextViewUpdater {
	public void update(OsmerActivity a, String text, ViewType t)
	{
		OTextView tv = null;
		if(t == ViewType.HOME)
			tv = (OTextView) a.findViewById(R.id.homeTextView);
		else if(t == ViewType.TODAY)
			tv = (OTextView) a.findViewById(R.id.todayTextView);
		else if(t == ViewType.TOMORROW)
			tv = (OTextView) a.findViewById(R.id.tomorrowTextView);
		else if(t == ViewType.TWODAYS)
			tv = (OTextView) a.findViewById(R.id.twoDaysTextView);
		if(tv != null)
		{
			/* true: after updating the text, save it on the internal storage */
			tv.setHtml(tv.formatText(text), true);
		}
	}
}
