package it.giacomos.android.meteofvg2.guiHelpers;

import it.giacomos.android.meteofvg2.OsmerActivity;
import it.giacomos.android.meteofvg2.R;
import it.giacomos.android.meteofvg2.downloadManager.DownloadStatus;
import it.giacomos.android.meteofvg2.widgets.OViewFlipper;
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
