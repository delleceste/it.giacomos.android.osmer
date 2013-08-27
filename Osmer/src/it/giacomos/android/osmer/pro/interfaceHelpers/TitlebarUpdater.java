package it.giacomos.android.osmer.pro.interfaceHelpers;

import it.giacomos.android.osmer.pro.OsmerActivity;
import it.giacomos.android.osmer.pro.network.DownloadStatus;

public class TitlebarUpdater {
	public void update(OsmerActivity a)
	{
		TitleBuilder titleBuilder = new TitleBuilder();
		a.setTitle(titleBuilder.makeTitle(a));
		titleBuilder = null;
	}
}
