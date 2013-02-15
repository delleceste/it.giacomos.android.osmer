package it.giacomos.android.osmer.widgets;

import java.lang.String;

public interface StateSaver {
	boolean saveOnInternalStorage();
	boolean restoreFromInternalStorage();
	boolean isRestoreSuccessful();
	
	String makeFileName();

}
