package it.giacomos.android.osmer.widgets.map.report;

import android.content.Context;

public interface ReportDialogFragmentListener 
{
	void onOkClicked(String user, int sky, int wind, String temp, String comment, Context ctx);
}
