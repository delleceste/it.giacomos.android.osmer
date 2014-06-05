package it.giacomos.android.osmer.downloadManager.state;

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

	public String todayImageUrl()
	{
		return "http://www-old.meteo.fvg.it/IT/HOME/IMAGES/Oggi.gif";
	}

	public String tomorrowImageUrl()
	{
		return "http://www-old.meteo.fvg.it/IT/HOME/IMAGES/Domani.gif";
	}

	public String dailyTableUrl()
	{
		return "http://www.giacomos.it/meteo.fvg/daily_observations.txt";
	}

	public String latestTableUrl()
	{
		return "http://www.giacomos.it/meteo.fvg/latest_observations.txt";
	}

	public String twoDaysImageUrl()
	{
		return "http://www-old.meteo.fvg.it/IT/HOME/IMAGES/Dopodomani.gif";
	}

	public String radarImageUrl() 
	{
		return "http://www-old.meteo.fvg.it/COMMON/RAD/GOOGLE.gif";
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
		return "http://www-old.meteo.fvg.it/COMMON/WEBCAM/Webcam";
	}
	
	public String webcamMapData()
	{
		return "http://www-old.meteo.fvg.it/GOOGLE/DatiWebcams1.php";
	}
	
	public String webcamsListXML()
	{
		return "http://www-old.meteo.fvg.it/GOOGLE/WebcamsList.xml";
	}
	
	
}
