package it.giacomos.android.osmer;

import it.giacomos.android.osmer.downloadManager.DownloadStatus;
import it.giacomos.android.osmer.locationUtils.Constants;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.location.Location;
import android.location.LocationManager;
import java.text.DateFormat;
public class ConfigInfo {

	public static String gatherInfo(Activity mActivity) 
	{
		String info = "";
		LocationManager lm = (LocationManager) mActivity.getSystemService(Activity.LOCATION_SERVICE);
		List<String> availProviders = lm.getProviders(false); /* true: enabled only providers */
		info += "<h5>Location manager</h5>";
		info += "<p>";
		
		info += "<strong>Current location available</strong>:<br/>";
		Location location = ((OsmerActivity)mActivity).getCurrentLocation();
		if(location == null)
			info += "- no<br/>";
		else
		{
			info += "- yes<br/>";
			info += "- current provider: " + location.getProvider() + "<br/>";
			info += "- accuracy: " + location.getAccuracy() + "m <br/>";
			Date date = new Date(location.getTime());
			DateFormat df = DateFormat.getDateTimeInstance();
			String time = df.format(date);
			info += "- last loc. timestamp: " + time + " <br/>";
		}
		
		
		info += "<strong>Available providers</strong>:<br/>";
		
		for(String s: availProviders)
			info += "- " + s + ";<br/>";
		info += "\n";
		
		List<String> enabledProviders = lm.getProviders(true);
		info += "Enabled providers:<br/>";
		
		for(String s: enabledProviders)
			info += "- " + s + ";<br/>";
		info += "";
		
		info += "<p><strong>Location update strategy (except Google map)</strong><br/>" +
		"  New location is considered better if:<br/>" +
		"* comes from GPS and is not significantly less accurate;<br/>" +
		"* is significantly newer (timestamp based);<br/>" +
		"* is more accurate;<br/>" +
		"* is newer but not less accurate;<br/>" +
		"* is newer <em>and</em> not significantly less accurate <em>and</em> same location provider;<br/></p>";
		
		info += "<p><strong>where</strong>:<br/>" +
		"* newer: delta = (currentLoc.time - previousLoc.time) > 0;<br/>" +
		"* significantly newer: delta >" + Constants.LOCATION_COMPARER_INTERVAL + "ms;<br/>" +
		"* significantly older: delta < -" + Constants.LOCATION_COMPARER_INTERVAL + "ms;<br/>" +
		"* significantly less accurate: newLoc.accuracy - prevLoc.accuracy > " 
			+ Constants.LOCATION_COMPARER_ACCURACY + "(meters);<br/>";
		
		info += "<p><strong>Location update request parameters</strong>:<br/>" +
				"* NETWORK, minTime: " + Constants.LOCATION_UPDATES_NETWORK_MIN_TIME + "ms<br/>" +
				"* NETWORK, minDistance: " + Constants.LOCATION_UPDATES_NETWORK_MIN_DIST + "m<br/>" +
				"* GPS, minTime: " + Constants.LOCATION_UPDATES_GPS_MIN_TIME + "ms<br/>" +
				"* GPS, minDistance: " + Constants.LOCATION_UPDATES_GPS_MIN_DIST + "m<br/>"; 
		
		//		"* PASSIVE, minTime: " + Constants.LOCATION_UPDATES_PASSIVE_MIN_TIME + "ms<br/>" +
			//	"* PASSIVE, minDistance: " + Constants.LOCATION_UPDATES_PASSIVE_MIN_DIST + "m<br/>";
		
		info += "<br/><strong>Google Map location update strategy</strong><br/>" +
				"Managed by MyLocationOverlay Google Map API v1";
		
		info += "</p>";
		
		info += "<h5>Data update policy</h5>";
		info += "<p>" +
				"Automatic: on network available <strong>or</strong> <cite>onResume</cite> <strong>and</strong> after at least " +
				DownloadStatus.DOWNLOAD_OLD_TIMEOUT + "ms from previous successful update; <br/>" +
				"" +
				"</p>";
		
		info += "<h5>Data retrieval strategy</h5>";
		info += "<p>" +
				"<strong>Multi threaded</strong>, parallel download achieved with <strong>AsyncTask</strong>s.<br/> " +
				"THREAD_POOL_EXECUTOR Executor on Android build version > HONEYCOMB;" +
				"<br/>Last downloaded data is saved on storage for <em>offline</em> viewing." +
				"</p>";
		
		info += "<h5>Google Maps API</h5>";
		info += "<p>" +
				"Using Google Maps API v1" +
				"</p>";
		
		return info;
	}

}
