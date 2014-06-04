package it.giacomos.android.osmer.guiHelpers;

import android.text.util.Linkify;
import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.StringType;
import it.giacomos.android.osmer.widgets.OTextView;

public class TextViewUpdater {
	public void update(OsmerActivity a, String text, StringType t)
	{
		OTextView tv = null;
		if(t == StringType.HOME)
			tv = (OTextView) a.findViewById(R.id.homeTextView);
		else if(t == StringType.TODAY)
			tv = (OTextView) a.findViewById(R.id.todayTextView);
		else if(t == StringType.TOMORROW)
			tv = (OTextView) a.findViewById(R.id.tomorrowTextView);
		else if(t == StringType.TWODAYS)
			tv = (OTextView) a.findViewById(R.id.twoDaysTextView);
		if(tv != null)
		{
			tv.setHtml((text));
		}
	}
}
