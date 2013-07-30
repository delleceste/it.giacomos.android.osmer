package it.giacomos.android.osmer.instanceSnapshotManager;

import android.os.Bundle;
import android.util.Log;
import it.giacomos.android.osmer.OsmerActivity;

public class SnapshotManager {
	
	public SnapshotManager()
	{
		
	}
	
	public void restore(Bundle b, OsmerActivity a)
	{
		DownloadStatusRestorer downloadStatusRestorer = new DownloadStatusRestorer(b);
		downloadStatusRestorer.restore(a);
		downloadStatusRestorer.updateDownloadStatus(a);
		downloadStatusRestorer = null;
	}
	
	public void save(Bundle b, OsmerActivity a)
	{
		InstanceSaver is = new InstanceSaver();
		is.save(b, a);
		is = null;
	}
	
	
}
