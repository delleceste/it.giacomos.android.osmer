package it.giacomos.android.osmer.guiHelpers;

import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.downloadManager.DownloadStatus;
import it.giacomos.android.osmer.widgets.OViewFlipper;
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
