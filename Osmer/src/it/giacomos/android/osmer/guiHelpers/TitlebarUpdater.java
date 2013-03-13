package it.giacomos.android.osmer.guiHelpers;

import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.downloadManager.DownloadStatus;
import it.giacomos.android.osmer.widgets.OViewFlipper;
import android.view.Window;

public class TitlebarUpdater {
	public TitlebarUpdater(OsmerActivity a)
	{
		DownloadStatus ds = DownloadStatus.Instance();
		TitleBuilder titleBuilder = new TitleBuilder();
		a.setTitle(titleBuilder.makeTitle(a));
		
		/* title icon */
		if(ds.downloadErrorCondition())
			a.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.task_attention);
		else if(ds.isOnline)
			a.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.online);
		else if(!ds.isOnline)
			a.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.offline);
		
		titleBuilder = null;
	}
}
