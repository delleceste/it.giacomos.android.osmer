package it.giacomos.android.osmer.widgets.map;

import com.google.android.gms.maps.model.LatLng;

import it.giacomos.android.osmer.widgets.map.report.network.PostType;

/** OsmerActivity implements this interface 
 * 
 * @author giacomo
 *
 */
public interface ReportRequestListener 
{
	public void onMyReportRequestTriggered(LatLng pointOnMap, String mMyRequestMarkerLocality);
	public void onMyReportLocalityChanged(String locality);
	public void onMyReportRequestDialogCancelled(LatLng position);
	public void onMyPostRemove(LatLng position, PostType type);
	/* the following one will start the report activity */
	public void onMyReportPublish();
}
