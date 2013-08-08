package it.giacomos.android.osmer.network.state;

import it.giacomos.android.osmer.network.DownloadManager;
import it.giacomos.android.osmer.network.DownloadManagerUpdateListener;
import it.giacomos.android.osmer.network.DownloadStatus;

public class Offline extends State {

	public Offline(DownloadManagerUpdateListener l) {
		super(l);
		DownloadStatus.Instance().isOnline = false;
	}

	public StateName name() { return StateName.Offline; }
}
