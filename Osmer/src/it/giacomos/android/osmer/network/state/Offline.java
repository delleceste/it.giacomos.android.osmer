package it.giacomos.android.osmer.network.state;

import it.giacomos.android.osmer.network.DownloadManagerUpdateListener;
import it.giacomos.android.osmer.network.DownloadStatus;

public class Offline extends State {

	public Offline(DownloadManagerUpdateListener l, DownloadStatus ds) {
		super(l, ds);
		dDownloadStatus.isOnline = false;
	}

	public StateName name() { return StateName.Offline; }
}
