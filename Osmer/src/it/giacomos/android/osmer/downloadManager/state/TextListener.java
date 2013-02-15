package it.giacomos.android.osmer.downloadManager.state;

import it.giacomos.android.osmer.StringType;

public interface TextListener {
	public void onTextUpdate(String bmp, StringType st, String errorMessage);
}
