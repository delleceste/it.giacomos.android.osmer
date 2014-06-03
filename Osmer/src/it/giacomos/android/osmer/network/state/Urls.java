package it.giacomos.android.osmer.network.state;

import java.util.Locale;
import java.lang.String;

public class Urls {
	public Urls() { }
	
	public String situationUrl()
	{
		return "http://www.giacomos.it/meteo.fvg/data/situazione.html";
	}
	

	public String todayUrl()
	{
		return "http://www.giacomos.it/meteo.fvg/data/today_full.html";
	}

	public String tomorrowUrl()
	{
		return "http://www.giacomos.it/meteo.fvg/data/tomorrow_full.html";
	}

	public String twoDaysUrl()
	{
		return "http://www.giacomos.it/meteo.fvg/data/twodays_full.html";
	}

	public String todaySymtableUrl()
	{
		return "http://www.giacomos.it/meteo.fvg/data/today_symtable.txt";
	}
	
	public String tomorrowSymtableUrl()
	{
		return "http://www.giacomos.it/meteo.fvg/data/tomorrow_symtable.txt";
	}

	public String twoDaysSymtableUrl()
	{
		return "http://www.giacomos.it/meteo.fvg/data/twodays_symtable.txt";
	}
	
	public String dailyTableUrl()
	{
		return "http://www.giacomos.it/meteo.fvg/daily_observations.txt";
	}

	public String latestTableUrl()
	{
		return "http://www.giacomos.it/meteo.fvg/latest_observations.txt";
	}

	public String latestTableReferer()
	{
		return "http://www.osmer.fvg.it/IT/SYN/TabellaStazioniUltimOra.php";
	}

	public String dailyTableReferer()
	{
		return "http://www.osmer.fvg.it/IT/SYN/TabellaStazioniDallaMezzanotte.php";
	}

	public String radarImageUrl() 
	{
		return "http://www.giacomos.it/meteo.fvg/get_last_radar_image_png.php";
	}
	
	public String radarHistoricalImagesFolderUrl()
	{
		return "http://www.giacomos.it/meteo.fvg/data/radar/";
	}
	
	public String radarHistoricalFileListUrl()
	{
		return "http://www.giacomos.it/meteo.fvg/get_radar_files.php";
	}
	
	public String postReportUrl()
	{
		return "http://www.giacomos.it/meteo.fvg/put_report.php";
	}
	
	/**
	 * since version 2.6.1, invokes get_report_2_6_1 because the report 
	 * response contains the active users list.
	 * 
	 * since version 2.6.3 invokes get_report_2_6_3 that groups active users
	 * by area, returning the most recently active users in an area, excluding
	 * all other users less recent whose distance from the most recent is less
	 * than a threshold in km.
	 * 
	 */
	public String getReportUrl()
	{
		return "http://www.giacomos.it/meteo.fvg/get_report_2_6_3.php";
	}
	
	public String getPostReportRequestUrl() {
		
		return "http://www.giacomos.it/meteo.fvg/report_request.php";
	}
	
	public String getReportsAndRequestUpdatesForMyLocationUrl()
	{
		return "http://www.giacomos.it/meteo.fvg/get_reports_and_requests_for_my_location.php";
	}
	
	public String getReportsRequestUpdatesAndRainProbabilityForMyLocationUrl()
	{
		return "http://www.giacomos.it/meteo.fvg/get_reports_requests_rain_probability_for_my_location.php";
	}
	
	public String getRemovePostUrl() 
	{
		return "http://www.giacomos.it/meteo.fvg/remove_post.php";
	}
	
	public String webcamImagesPath()
	{
		return "http://www.osmer.fvg.it/COMMON/WEBCAM/Webcam";
	}
	
	public String webcamMapData()
	{
		return "http://www.osmer.fvg.it/GOOGLE/DatiWebcams1.php";
	}
	
	public String webcamsListXML()
	{
		return "http://www.osmer.fvg.it/GOOGLE/WebcamsList.xml";
	}

	

	
	
}
