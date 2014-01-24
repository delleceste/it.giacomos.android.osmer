package it.giacomos.android.osmer.PROva.interfaceHelpers;

import it.giacomos.android.osmer.PROva.OsmerActivity;
import it.giacomos.android.osmer.PROva.network.DownloadStatus;

public class TitlebarUpdater {
	public void update(OsmerActivity a)
	{
		TitleBuilder titleBuilder = new TitleBuilder();
		a.setTitle(titleBuilder.makeTitle(a));
		titleBuilder = null;
	}
}
