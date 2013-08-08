package it.giacomos.android.osmer.instanceSnapshotManager;


import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.network.DownloadStatus;
import android.app.ActionBar;
import android.os.Bundle;

public class InstanceSaver {
	protected void save(Bundle b, OsmerActivity a)
	{		
		DownloadStatus ds = DownloadStatus.Instance();
		/* a couple of timestamps to mark bundle save time and
		 * state machine last completed download time.
		 */
		b.putLong("bundleSavedOn", System.currentTimeMillis());
		b.putLong("lastDownloadTimestamp", ds.lastUpdateCompletedOn());
		b.putBoolean("downloadErrorCondition", ds.downloadErrorCondition());
		
		/* save action bar spinner position */
		if(a.getActionBar().getNavigationMode() == ActionBar.NAVIGATION_MODE_LIST)
			b.putInt("spinnerPosition", a.getActionBar().getSelectedNavigationIndex());
	}
}
