package it.giacomos.android.osmer.pro.forecastRepr;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

import it.giacomos.android.osmer.R;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.text.Html;
import android.util.Log;

public class ForecastDataFactory 
{
	private Resources mResources;

	public ForecastDataFactory(Resources res)
	{
		mResources = res;
	}

	private void buildDrawables(ArrayList<ForecastDataInterface> data)
	{
		int numLayers;
		int layerIdx;
		LatLngCalculator llcalc = new LatLngCalculator();
		for(ForecastDataInterface fdi : data)
		{
			LatLng ll = llcalc.get(fdi.getId());
			if(ll != null) /* name of the location is taken into account */
			{
				/* all ForecastDataInterface objects must have a LatLng */
				fdi.setLatLng(ll);
				if(fdi.getType() == ForecastDataType.AREA)
				{
					numLayers = 1;
					layerIdx = 0;

					Area a = (Area) fdi;
					if(a.rain != 100)
						numLayers++;
					if(a.snow != 100)
						numLayers++;
					if(a.storm != 100)
						numLayers++;
					if(a.mist != 100)
						numLayers++;

					Log.e("ForecastDataFactory.buildDrawables", "There are layers " + numLayers +
							"rain " + a.rain + " snow " + a.snow + " storm " + a.storm + 
							" mist " + a.mist + " AREA " + a.getId());
					Drawable layers[] = new Drawable[numLayers];
					/* first layer: sky */
					if(a.sky == 0)
					{
						layers[0] = mResources.getDrawable(R.drawable.weather_clear);
						layerIdx = 1;
					}
					else if(a.sky == 1) /* poco nuvoloso */
					{
						layers[0] = mResources.getDrawable(R.drawable.weather_few_clouds);
						layerIdx = 1;
					}
					else if(a.sky == 2) /* variabile */
					{
						layers[0] = mResources.getDrawable(R.drawable.weather_clouds);
						layerIdx = 1;
					}
					else if(a.sky == 3) /* nuvoloso */
					{
						layers[0] = mResources.getDrawable(R.drawable.weather_sky_3);
						layerIdx = 1;
					}
					else if(a.sky == 4) /* coperto */
					{
						layers[0] = mResources.getDrawable(R.drawable.weather_sky_4);
						layerIdx = 1;
					}
					else if(a.sky == 5) /* sole/nebbia */
					{
						layers[0] = mResources.getDrawable(R.drawable.weather_mist);
						layerIdx = 1;
					}
					/* rain */
					if(a.rain == 6)
					{
						layers[layerIdx] = mResources.getDrawable(R.drawable.weather_rain_6);
						layerIdx++;
					}
					else if(a.rain == 7)
					{
						layers[layerIdx] = mResources.getDrawable(R.drawable.weather_rain_7);
						layerIdx++;
					}
					else if(a.rain == 8)
					{
						layers[layerIdx] = mResources.getDrawable(R.drawable.weather_rain_8);
						layerIdx++;
					}
					else if(a.rain == 9)
					{
						layers[layerIdx] = mResources.getDrawable(R.drawable.weather_rain_9);
						layerIdx++;
					}
					else if(a.rain == 36)
					{
						layers[layerIdx] = mResources.getDrawable(R.drawable.weather_rain_36);
						layerIdx++;
					}
					/* snow */
					if(a.snow == 10)
					{
						layers[layerIdx] = mResources.getDrawable(R.drawable.weather_snow_10);
						layerIdx++;
					}
					else if(a.snow == 11)
					{
						layers[layerIdx] = mResources.getDrawable(R.drawable.weather_snow_11);
						layerIdx++;
					}
					else if(a.snow == 12)
					{
						layers[layerIdx] = mResources.getDrawable(R.drawable.weather_snow_12);
						layerIdx++;
					}
					/* storm: only one symbol: 13 */
					if(a.storm == 13)
					{
						layers[layerIdx] = mResources.getDrawable(R.drawable.weather_storm_13);
						layerIdx++;
					}
					/* mist: nebbia e foschia */
					if(a.mist == 14)
					{
						layers[layerIdx] = mResources.getDrawable(R.drawable.weather_mist_14);
						layerIdx++;
					}
					else if(a.mist == 15)
					{
						layers[layerIdx] = mResources.getDrawable(R.drawable.weather_mist_15);
						layerIdx++;
					}

					LayerDrawable layeredSymbol = new LayerDrawable(layers);
					a.setSymbol(layeredSymbol);

				}
				/* from strips we take temperatures and rain and storms probability (if there is 
				 * space to represent the last two quantities)
				 */
				else if(fdi.getType() == ForecastDataType.STRIP) /* Fascia, F1, F2... */
				{
					Strip s = (Strip ) fdi;

				}
				/* from locality we take into account special snow and storms for now, nothing else 
				 * 
				 */
				else if(fdi.getType() == ForecastDataType.LOCALITY) /* localita`... L1, L2... */
				{
					Locality l = (Locality) fdi;

				}
			}
		}
	}

	public ArrayList<ForecastDataInterface> getForecastData(String data)
	{
		Strip strip = null;
		Area area = null;
		Locality loc = null;
		ForecastDataInterface fdi = null;
		ArrayList<ForecastDataInterface> ret = new ArrayList<ForecastDataInterface>();
		if(data.length() > 10) /* may be enough > 0, let's say 10 */
		{
			String [] lines = data.split("\n");
			for(String line : lines)
			{
				if(line.matches("A\\d+")) /* area */
				{
					/* create an Area with the name provided in the matching line
					 * (A1, A2...., A9)
					 */
					fdi = new Area(line);
					ret.add(fdi);
				}
				else if(line.matches("F\\d+"))
				{
					fdi = new Strip(line);
					ret.add(fdi);
				}
				else if(line.matches("L\\d+"))
				{
					fdi = new Locality(line);
					ret.add(fdi);
				}
				else if(fdi.getType() == ForecastDataType.AREA)
				{
					try{
						area = (Area) fdi;
						if(line.startsWith("pp"))
							area.rainProb = Html.fromHtml(line.replace("pp", ""));
						else if(line.startsWith("pt") && line.length() > 2)
							area.stormProb = Html.fromHtml(line.replace("pt", ""));
						else if(line.startsWith("t1") && line.length() > 2)
							area.t1000 = line.replace("t1", "");
						else if(line.startsWith("t2") && line.length() > 2)
							area.t1000 = line.replace("t2", "");
						else if(line.startsWith("zt") && line.length() > 2)
							area.t1000 = line.replace("zt", "");

						else if(line.startsWith("C") && line.length() > 1) /* Cielo */
							area.sky = Integer.parseInt(line.replace("C", ""));
						else if(line.startsWith("P") && line.length() > 1)
							area.rain = Integer.parseInt(line.replace("P", ""));
						else if(line.startsWith("T") && line.length() > 1) /* Temporale */
							area.storm = Integer.parseInt(line.replace("T", ""));
						else if(line.startsWith("B") && line.length() > 1) /* neBbia */
							area.mist = Integer.parseInt(line.replace("B", ""));
						else if(line.startsWith("V") && line.length() > 1) /* vento */
							area.wind = Integer.parseInt(line.replace("V", ""));
					}
					catch(NumberFormatException nfe)
					{

					}
				}
				else if(fdi.getType() == ForecastDataType.STRIP)
				{	
					strip = (Strip) fdi;
					if(line.startsWith("t1") && line.length() > 2)
						strip.t1000 = line.replace("t1", "");
					else if(line.startsWith("t2") && line.length() > 2)
						strip.t2000 = line.replace("t2", "");
					else if(line.startsWith("tm") && line.length() > 2)
						strip.tMin = line.replace("tm", "");
					else if(line.startsWith("tM") && line.length() > 2)
						strip.tMax = line.replace("tM", "");
				}
				else if(fdi.getType() == ForecastDataType.LOCALITY)
				{
					try{
						loc = (Locality ) fdi;
						if(line.startsWith("N") && line.length() > 1) /* neve */
							loc.particularSnow = Integer.parseInt(line.replace("N", ""));
						else if(line.startsWith("T") && line.length() > 1) /* temporali particolari */
							loc.particularStorm = Integer.parseInt(line.replace("T", ""));
						else if(line.startsWith("tm") && line.length() > 2)
							loc.tMin = line.replace("tm", "");
						else if(line.startsWith("tm") && line.length() > 2)
							loc.tMax = line.replace("tm", "");
					}
					catch(NumberFormatException nfe)
					{

					}
				}

			}

			buildDrawables(ret);
			
		} /* data length is nonzero (supposed > 10). Otherwise ret will be not null but zero sized */

		return ret;
	}
}
