package it.giacomos.android.osmer.interfaceHelpers;

import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.network.DownloadStatus;

public class TitlebarUpdater {
	public void update(OsmerActivity a)
	{
		TitleBuilder titleBuilder = new TitleBuilder();
		a.setTitle(titleBuilder.makeTitle(a));
		titleBuilder = null;
	}
}
