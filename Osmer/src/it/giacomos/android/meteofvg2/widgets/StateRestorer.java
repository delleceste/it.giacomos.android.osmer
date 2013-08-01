package it.giacomos.android.meteofvg2.widgets;

import java.lang.String;

public interface StateRestorer {
	boolean restoreFromInternalStorage();
	boolean isRestoreSuccessful();
	
	String makeFileName();

}
