package it.giacomos.android.osmer.instanceSnapshotManager;

import android.os.Bundle;

import it.giacomos.android.osmer.BitmapType;
import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.ViewType;
import it.giacomos.android.osmer.downloadManager.DownloadStatus;
import it.giacomos.android.osmer.widgets.ODoubleLayerImageView;
import it.giacomos.android.osmer.widgets.OTextView;

public class InstanceRestorer {
	protected InstanceRestorer(Bundle b)
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
		DownloadStatus ds = DownloadStatus.Instance();
		ds.state = DownloadStatus.INIT;
		/* ask each interesting view whether it succeeded in restoring the state */
		
		/* OTextViews */
		OTextView otw = (OTextView) a.findViewById(R.id.homeTextView);
		ds.updateState(ViewType.HOME, otw.isRestoreSuccessful());
		
		otw = (OTextView) a.findViewById(R.id.todayTextView);
		ds.updateState(ViewType.TODAY, otw.isRestoreSuccessful());
		
		otw = (OTextView) a.findViewById(R.id.tomorrowTextView);
		ds.updateState(ViewType.TOMORROW, otw.isRestoreSuccessful());
		
		otw = (OTextView) a.findViewById(R.id.twoDaysTextView);
		ds.updateState(ViewType.TWODAYS, otw.isRestoreSuccessful());
		
		/* ODoubleLayerImageViews */
		ODoubleLayerImageView iv = (ODoubleLayerImageView) a.findViewById(R.id.todayImageView);
		ds.updateState(BitmapType.TODAY, iv.isRestoreSuccessful());
		
		iv = (ODoubleLayerImageView) a.findViewById(R.id.tomorrowImageView);
		ds.updateState(BitmapType.TOMORROW, iv.isRestoreSuccessful());
		
		iv = (ODoubleLayerImageView) a.findViewById(R.id.twoDaysImageView);
		ds.updateState(BitmapType.TWODAYS, iv.isRestoreSuccessful());

		/* download marked complete/uncomplete */
		ds.setDownloadErrorCondition(ds.downloadErrorCondition());
		ds.setFullForecastDownloadRequested(ds.downloadComplete()); /* no need to full download */
		/* restore last download timestamp on the state machine */
		ds.setLastUpdateCompletedOn(m_bundle.getLong("lastDownloadTimestamp"));
	}

	
	private Bundle m_bundle;
}
