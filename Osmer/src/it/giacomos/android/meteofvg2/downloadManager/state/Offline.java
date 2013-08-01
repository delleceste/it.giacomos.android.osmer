package it.giacomos.android.meteofvg2.downloadManager.state;

import it.giacomos.android.meteofvg2.downloadManager.DownloadManager;
import it.giacomos.android.meteofvg2.downloadManager.DownloadManagerUpdateListener;
import it.giacomos.android.meteofvg2.downloadManager.DownloadStatus;

public class Offline extends State {

	public Offline(DownloadManagerUpdateListener l) {
		super(l);
		DownloadStatus.Instance().isOnline = false;
	}

	public StateName name() { return StateName.Offline; }
}
