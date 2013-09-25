package it.giacomos.android.osmer.pro.network.state;

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
		return "http://www.giacomos.it/meteo.fvg/data/oggi.html";
	}

	public String tomorrowUrl()
	{
		return "http://www.giacomos.it/meteo.fvg/data/domani.html";
	}

	public String twoDaysUrl()
	{
		return "http://www.giacomos.it/meteo.fvg/data/dopodomani.html";
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
		return "http://www.meteo.fvg.it/COMMON/RAD/GOOGLE.gif";
	}
	
	public String radarHistoricalImagesFolderUrl()
	{
		return "http://www.giacomos.it/meteo.fvg/data/radar/";
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
