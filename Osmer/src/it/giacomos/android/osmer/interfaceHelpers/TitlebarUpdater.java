package it.giacomos.android.osmer.interfaceHelpers;

import android.support.v7.widget.Toolbar;
import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;

public class TitlebarUpdater {
	public void update(OsmerActivity a)
	{
		TitleBuilder titleBuilder = new TitleBuilder();
		a.setTitle(titleBuilder.makeTitle(a));
		a.getSupportActionBar().setTitle(a.getTitle());
		Toolbar toolb = (Toolbar) a.findViewById(R.id.toolbar);
		toolb.setTitle(a.getTitle());
		titleBuilder = null;
	}
}
