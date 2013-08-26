package it.giacomos.android.osmer.pro.guiHelpers;

import it.giacomos.android.osmer.pro.OsmerActivity;
import it.giacomos.android.osmer.pro.R;
import it.giacomos.android.osmer.pro.downloadManager.DownloadStatus;
import it.giacomos.android.osmer.pro.widgets.OViewFlipper;
import android.view.Window;

public class TitlebarUpdater {
	public void update(OsmerActivity a)
	{
		DownloadStatus ds = DownloadStatus.Instance();
		TitleBuilder titleBuilder = new TitleBuilder();
		a.setTitle(titleBuilder.makeTitle(a));
		titleBuilder = null;
	}
}
