package it.giacomos.android.osmer;

import java.lang.String;

public interface MobiTextListener {
	public void onTextUpdate(String s, StringType t);
	public void onTextProgressUpdate(int value);

}
