package it.giacomos.android.osmer.pro.widgets.map.report.network;

public interface ReportPublishedListener {

	void onPostActionResult(boolean canceled, boolean error, String message,
			PostType postType);
}