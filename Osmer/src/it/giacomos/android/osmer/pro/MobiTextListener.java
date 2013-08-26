package it.giacomos.android.osmer.pro;

import java.lang.String;

public interface MobiTextListener {
	public void onTextUpdate(String s, ViewType t);
	public void onTextProgressUpdate(int value);

}
