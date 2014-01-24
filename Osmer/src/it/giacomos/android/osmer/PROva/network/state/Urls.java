package it.giacomos.android.osmer.PROva.network.state;

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
		return "http://www.osmer.fvg.it/IT/SYN/TabellaStazioniDallaMezzanotte.php?save=1";
	}

	public String latestTableUrl()
	{
		return "http://www.osmer.fvg.it/IT/SYN/TabellaStazioniUltimOra.php?save=1";
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
	
	public String getReportUrl()
	{
		return "http://www.giacomos.it/meteo.fvg/get_report.php";
	}
	
	public String getPostReportRequestUrl() {
		
		return "http://www.giacomos.it/meteo.fvg/report_request.php";
	}
	
	public String getReportsAndRequestUpdatesForMyLocationUrl()
	{
		return "http://www.giacomos.it/meteo.fvg/get_reports_and_requests_for_my_location.php";
	}
	
	public String getRemovePostUrl() 
	{
		return "http://www.giacomos.it/meteo.fvg/remove_post.php";
	}
	
	public String minTempUrl()
	{
		return "";
	}
	
	public String maxTempUrl()
	{

		return "";
	}
	
	public String humidityUrl()
	{
		return "";
	}
	
	public String rainUrl()
	{

		return "";
	}
	
	public String windUrl()
	{

		return "";
	}
	
	public String pressureUrl()
	{

		return "";
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
