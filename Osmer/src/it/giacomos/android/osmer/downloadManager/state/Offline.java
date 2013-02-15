package it.giacomos.android.osmer.downloadManager.state;

import it.giacomos.android.osmer.downloadManager.DownloadManager;
import it.giacomos.android.osmer.downloadManager.DownloadManagerUpdateListener;
import it.giacomos.android.osmer.downloadManager.DownloadStatus;

public class Offline extends State {

	public Offline(DownloadManagerUpdateListener l) {
		super(l);
		DownloadStatus.Instance().isOnline = false;
	}

	public StateName name() { return StateName.Offline; }
}
