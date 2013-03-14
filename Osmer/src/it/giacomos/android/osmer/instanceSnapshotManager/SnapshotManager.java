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
		InstanceRestorer instanceRestorer = new InstanceRestorer(b);
		instanceRestorer.restore(a);
		instanceRestorer.updateDownloadStatus(a);
		instanceRestorer = null;
	}
	
	public void save(Bundle b, OsmerActivity a)
	{
		InstanceSaver is = new InstanceSaver();
		is.save(b, a);
		is = null;
	}
	
	
}
