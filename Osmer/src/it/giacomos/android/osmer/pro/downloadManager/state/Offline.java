package it.giacomos.android.osmer.pro.downloadManager.state;

import it.giacomos.android.osmer.pro.downloadManager.DownloadManager;
import it.giacomos.android.osmer.pro.downloadManager.DownloadManagerUpdateListener;
import it.giacomos.android.osmer.pro.downloadManager.DownloadStatus;

public class Offline extends State {

	public Offline(DownloadManagerUpdateListener l) {
		super(l);
		DownloadStatus.Instance().isOnline = false;
	}

	public StateName name() { return StateName.Offline; }
}
