package it.giacomos.android.osmer.PROva.widgets;

import java.lang.String;

public interface StateRestorer {
	boolean restoreFromInternalStorage();
	boolean isRestoreSuccessful();
	
	String makeFileName();

}
