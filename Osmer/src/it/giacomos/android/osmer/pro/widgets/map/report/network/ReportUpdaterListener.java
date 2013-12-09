package it.giacomos.android.osmer.pro.widgets.map.report.network;

public interface ReportUpdaterListener 
{
	public void onReportUpdateDone(String doc);
	
	public void onReportUpdateError(String error);
}
