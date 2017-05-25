package it.giacomos.android.osmer.network.state;

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

	public String radarImageUrl(String src)
	{
		if(src.compareTo("slo") == 0)
			return "http://www.giacomos.it/meteo.fvg/get_last_slo_radar_image_png.php";
		return "http://www.giacomos.it/meteo.fvg/get_last_radar_image_png.php";
	}
	
	public String radarHistoricalImagesFolderUrl(String radarSource)
	{
		if(radarSource.compareToIgnoreCase("slo") == 0)
			return "http://www.giacomos.it/meteo.fvg/data/radar/slo/";
		else
			return "http://www.giacomos.it/meteo.fvg/data/radar/";
	}
	
	public String radarHistoricalFileListUrl(String src)
	{
		if(src.compareTo("slo") == 0)
			return "http://www.giacomos.it/meteo.fvg/get_slo_radar_files.php";

		return "http://www.giacomos.it/meteo.fvg/get_radar_files.php";
	}
	
	public String postReportUrl()
	{
		return "http://www.giacomos.it/meteo.fvg/publish_report.php";
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
		
		return "http://www.giacomos.it/meteo.fvg/publish_request.php";
	}
	
	public String getUpdateMyLocationUrl()
	{
		return "http://www.giacomos.it/meteo.fvg/update_my_location.php";
	}
	
	public String getRemovePostUrl() 
	{
		return "http://www.giacomos.it/meteo.fvg/remove_post.php";
	}
	
	public String webcamImagesPath()
	{
		return "http://www-old.osmer.fvg.it/COMMON/WEBCAM/Webcam";
	}
	
	public String webcamMapData()
	{
		return "http://www-old.osmer.fvg.it/GOOGLE/DatiWebcams1.php";
	}
	
	public String webcamsListXML()
	{
		/*  NEW: www.meteo.fvg.it/json/webcam_json.php?tipo=2 */
		return "http://www-old.osmer.fvg.it/GOOGLE/WebcamsList.xml";
	}

	public String newsUrl() {
		return "http://www.giacomos.it/meteo.fvg/get_news.php";
	}

	public String meteoFvgBaseUrl()
	{
		return "http://www.giacomos.it/meteo.fvg/";
	}

	public String threeDaysSymtableUrl() 
	{
		return "http://www.giacomos.it/meteo.fvg/data/piu_3_symtable.txt";
	}

	public String fourDaysSymtableUrl() 
	{
		return "http://www.giacomos.it/meteo.fvg/data/piu_4_symtable.txt";
	}

	public String threeDaysUrl() 
	{
		return "http://www.giacomos.it/meteo.fvg/data/piu_3.html";
	}
	
	public String fourDaysUrl() 
	{
		return "http://www.giacomos.it/meteo.fvg/data/piu_4.html";
	}

	public String personalMessageFetchUrl() 
	{
		return "http://www.giacomos.it/meteo.fvg/get_configuration.php";
	}

	public String getMeteoFVGUrl() {
		return "http://m.meteo.fvg.it/home.php";
	}
	
	public String getMeteoFVGAppStoreUrl()
	{
		return "https://play.google.com/store/apps/details?id=it.giacomos.android.osmer";
	}

	public String getMeteoFVGProAppStoreUrl() {
		return "https://play.google.com/store/apps/details?id=it.giacomos.android.osmer.pro";
	}
	
}
