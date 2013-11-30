package it.giacomos.android.osmer.pro.widgets.map.report;

import java.util.ArrayList;

import com.google.android.gms.maps.model.MarkerOptions;

public interface ReportOverlayTaskListener {
	public void onReportOverlayTaskFinished(ArrayList<MarkerOptions> markerOptionsArray);
}
