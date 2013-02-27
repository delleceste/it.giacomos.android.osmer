package it.giacomos.android.osmer.downloadManager.state;

import java.util.Locale;
import java.lang.String;

public class Urls {
	public Urls() { }
	
	public String situationUrl()
	{
		return "http://www.meteo.fvg.it/IT/HOME/SituazioneGenerale.txt";
	}
	

	public String todayUrl()
	{
		return "http://www.meteo.fvg.it/IT/HOME/Oggi.info";
	}

	public String tomorrowUrl()
	{
		return "http://www.meteo.fvg.it/IT/HOME/Domani.info";
	}

	public String twoDaysUrl()
	{
		return "http://www.meteo.fvg.it/IT/HOME/Dopodomani.info";
	}

	public String todayImageUrl()
	{
		return "http://www.osmer.fvg.it/IT/HOME/IMAGES/Oggi.gif";
	}

	public String tomorrowImageUrl()
	{
		return "http://www.osmer.fvg.it/IT/HOME/IMAGES/Domani.gif";
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

	public String twoDaysImageUrl()
	{
		return "http://www.osmer.fvg.it/IT/HOME/IMAGES/Dopodomani.gif";
	}

	public String radarImageUrl() 
	{
		return "http://www.meteo.fvg.it/COMMON/RAD/GOOGLE.gif";
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
	
	public static String webcamImagesPath()
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
