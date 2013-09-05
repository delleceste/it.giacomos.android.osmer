package it.giacomos.android.osmer.pro.instanceSnapshotManager;

import android.os.Bundle;

import it.giacomos.android.osmer.pro.OsmerActivity;
import it.giacomos.android.osmer.pro.network.DownloadStatus;
import it.giacomos.android.osmer.pro.network.Data.DataPool;
import it.giacomos.android.osmer.pro.network.state.BitmapType;
import it.giacomos.android.osmer.pro.network.state.ViewType;

public class DownloadStatusRestorer {
	protected DownloadStatusRestorer(Bundle b)
	{
		m_bundle = b;
	}
	
	/** a Bundle is considered restorable if saved a couple of minutes before.
	 * 
	 * @return
	 */
	public boolean isRestorable()
	{
		long bundleCreatedOn = m_bundle.getLong("bundleSavedOn");
		long now = System.currentTimeMillis();
		if(now - bundleCreatedOn > 120000)
			return false;
		return true;
	}
	
	public void restore(OsmerActivity a)
	{	
		if(isRestorable()) /* saved less than a couple of minutes ago */
		{
			updateDownloadStatus(a);
		}
	}

	public void updateDownloadStatus(OsmerActivity a)
	{
		DownloadStatus ds = a.getDownloadStatus();
		ds.state = DownloadStatus.INIT;
		DataPool dataPool = a.getDataPool();
		/* ask each interesting view whether it succeeded in restoring the state */
		
		ds.updateState(ViewType.HOME, dataPool.isTextValid(ViewType.HOME));
		ds.updateState(ViewType.TODAY, dataPool.isTextValid(ViewType.TODAY));
		ds.updateState(ViewType.TOMORROW, dataPool.isTextValid(ViewType.TOMORROW));
		ds.updateState(ViewType.TWODAYS, dataPool.isTextValid(ViewType.TWODAYS));
		
		ds.updateState(ViewType.TODAY_SYMTABLE, dataPool.isTextValid(ViewType.TODAY_SYMTABLE));
		ds.updateState(ViewType.TOMORROW_SYMTABLE, dataPool.isTextValid(ViewType.TOMORROW_SYMTABLE));
		ds.updateState(ViewType.TWODAYS_SYMTABLE, dataPool.isTextValid(ViewType.TWODAYS_SYMTABLE));

		/* download marked complete/uncomplete */
		ds.setDownloadErrorCondition(ds.downloadErrorCondition());
		ds.setFullForecastDownloadRequested(ds.downloadComplete()); /* no need to full download */
		/* restore last download timestamp on the state machine */
		ds.setLastUpdateCompletedOn(m_bundle.getLong("lastDownloadTimestamp"));
	}
	
	private Bundle m_bundle;
}
