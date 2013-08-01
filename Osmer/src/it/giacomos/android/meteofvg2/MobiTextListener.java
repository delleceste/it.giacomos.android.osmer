package it.giacomos.android.meteofvg2;

import java.lang.String;

public interface MobiTextListener {
	public void onTextUpdate(String s, ViewType t);
	public void onTextProgressUpdate(int value);

}
