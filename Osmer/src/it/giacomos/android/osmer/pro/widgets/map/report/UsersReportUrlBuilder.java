package it.giacomos.android.osmer.pro.widgets.map.report;

import android.content.Context;
import android.location.Location;
import android.provider.Settings.Secure;
import android.util.Log;
import it.giacomos.android.osmer.pro.locationUtils.LocationService;
import it.giacomos.android.osmer.pro.network.state.Urls;

/** builds an URL to call get_report.php.
 * It requires the device id, the latitude and the longitude of the current location.
 * @author giacomo
 *
 */
public class UsersReportUrlBuilder {
	public  UsersReportUrlBuilder()
	{
		
	}
	
	public String build(LocationService locationService, Context ctx)
	{
		String url = null;
		if(locationService.isConnected())
		{
			Location l = locationService.getCurrentLocation();
			if(l != null)
			{
				String device_id = Secure.getString(ctx.getContentResolver(), Secure.ANDROID_ID);
				url = new Urls().getReportUrl() + "?d=" + device_id +  "&la=" + l.getLatitude() + "&lo=" + l.getLongitude();
			}
			else
				Log.e("buikld", "location is null");
		}
		else
			Log.e("buikld", "locationService.isConnected() FALSE");
		return url;
	}

}
