package it.giacomos.android.osmer.pro.widgets.map.report;

import android.util.Log;
import it.giacomos.android.osmer.pro.observations.ObservationData;

public class ObservationDataExtractor 
{
	private ObservationData mObservationData;

	public ObservationDataExtractor(ObservationData od)
	{
		mObservationData = od;
	}

	/*
	 * 	PIOGGIA	  	meno di 5 mm
		PIOGGIA	da 5 a 10 mm
		PIOGGIA	da 10 a 30 mm
		PIOGGIA	da 30 a 100 mm
		PIOGGIA	più di 100 mm
		NEVE	debole
		NEVE	moderata
		NEVE	forte
	 */
	public int getSkyIndex()
	{
		int index = 0;
		float rain = 0.0f;
		float snow = 0.0f;
		String sky = mObservationData.sky;

		try 
		{
			rain = Float.parseFloat(mObservationData.rain.replaceAll("[A-Za-z_\\s<>=\\[\\]/]*", ""));
		}
		catch (NumberFormatException e)
		{
			/* rain observation may be --- */
		}
		try 
		{
			snow = Float.parseFloat(mObservationData.snow.replaceAll("[A-Za-z_\\s<>=\\[\\]/]*", ""));
		}
		catch (NumberFormatException e)
		{

		}

		/* choose the right icon now! */
		if(rain == 0.0f)
		{
			if(sky.contains("sereno"))
				index = 1;
			else if(sky.contains("poco") && sky.contains("nuv"))
				index = 2;
			else if(sky.contains("variabil"))
				index = 3;
			else if(sky.contains("nuvoloso"))
				index = 4;
			else if(sky.contains("coperto"))
				index = 5;
			else if(sky.contains("neve"))
				index = 11;
		}
		else if(rain > 0.0f) /* rain */
		{
			if(rain < 5)
				index = 6;
			else if(rain <= 10)
				index = 7;
			else if(rain <= 30)
				index = 8;
			else if(rain <= 100)
				index = 9;
			else
				index = 10;
		}

		if(sky.contains("temporal"))
			index = 14;

		if(snow > 0.0f)
		{
			if(snow <= 10.0)
				index = 11;
			else if(snow <= 20.0)
				index = 12;
			else if(snow > 20.0)
				index = 13;
		}

		if(sky.contains("foschi"))
			index = 16;
		else if(sky.contains("nebbia"))
			index = 15;

		return index;
	}

	public int getWindIndex()
	{
		int index = 0; /* do not report wind by default */
		float km2mts = 1000.0f/3600.0f;
		String w = mObservationData.wind;
		float wind = 0.0f;
		Log.e("ObservationDataExtaca", "wind " + w);
		w = w.replaceAll("[A-Za-z_\\s<>=\\[\\]/]*", "");
		Log.e("ObservationDataExtaca", "wind dopo " + w);
		try 
		{
			wind = Float.parseFloat(w) * km2mts;
		}
		catch (NumberFormatException e)
		{
			/* --- for example */
		}
		if(wind == 0.0) 
			index = 1;
		else if(wind < 6)
			index = 3;
		else if(wind > 6)
			index = 4;
		return index;
	}

	public String getTemperature()
	{
		String temp = mObservationData.temp.replaceAll("[A-Za-z_\\s<>=]*", "");;
		return temp;
	}
}
