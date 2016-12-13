package it.giacomos.android.osmer.forecastRepr;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

import it.giacomos.android.osmer.R;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.text.Html;

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
			LatLng windLL = llcalc.getWindLatLng(fdi.getId());
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

					//					Log.e("ForecastDataFactory.buildDrawables", "There are layers " + numLayers +
					//							"rain " + a.rain + " snow " + a.snow + " storm " + a.storm + 
					//							" mist " + a.mist + " AREA " + a.getId());
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
					/* rain
					 * 1. If there is no snow and no storm to represent together with rain, we can 
					 * draw the rain icon that has drops distributed all over the horizontal space
					 */
					if(a.rain == 6 && a.snow == 100 && a.storm != 13 ) /* put drops well distantiated from each other */
						layers[layerIdx] = mResources.getDrawable(R.drawable.weather_rain_6);
					else if(a.rain == 7 && a.snow == 100 && a.storm != 13 )
						layers[layerIdx] = mResources.getDrawable(R.drawable.weather_rain_7);
					else if(a.rain == 8 && a.snow == 100 && a.storm != 13 )
						layers[layerIdx] = mResources.getDrawable(R.drawable.weather_rain_8);
					else if(a.rain == 9 && a.snow == 100 && a.storm != 13 )
						layers[layerIdx] = mResources.getDrawable(R.drawable.weather_rain_9);
					else if(a.rain == 36 && a.snow == 100 && a.storm != 13 )
						layers[layerIdx] = mResources.getDrawable(R.drawable.weather_rain_36);

					/* if there is snow or a storm to show together with rain we choose the 
					 * version of the rain icon with the drops moved to the left, so that the
					 * drops do not overlap with the lightning and/or the snow.
					 */
					else if(a.rain == 6) /* put the drops icon with drops on the left */
						layers[layerIdx] = mResources.getDrawable(R.drawable.weather_rain2_6);
					else if(a.rain == 7)
						layers[layerIdx] = mResources.getDrawable(R.drawable.weather_rain2_7);
					else if(a.rain == 8)
						layers[layerIdx] = mResources.getDrawable(R.drawable.weather_rain2_8);
					else if(a.rain == 9)
						layers[layerIdx] = mResources.getDrawable(R.drawable.weather_rain2_9);
					else if(a.rain == 36)
						layers[layerIdx] = mResources.getDrawable(R.drawable.weather_rain2_36);

					/* if there is rain, increment the layer counter */
					if((a.rain >= 6 && a.rain <= 9 ) || a.rain == 36)
						layerIdx++;

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
					
					if(layerIdx > 0)
					{
						LayerDrawable layeredSymbol = new LayerDrawable(layers);
						a.setSymbol(layeredSymbol);
					}


					/* generate wind symbol */
					Bitmap windBmp = null;
					if(a.wind == 16) /* N moderato */
						windBmp = BitmapFactory.decodeResource(mResources, R.drawable.weather_wind2_16);
					else if(a.wind == 17) /* NE moderato */
						windBmp = BitmapFactory.decodeResource(mResources, R.drawable.weather_wind2_17);
					else if(a.wind == 18) /* ENE moderato */
						windBmp = BitmapFactory.decodeResource(mResources, R.drawable.weather_wind2_18);
					else if(a.wind == 19) /* E moderato */
						windBmp = BitmapFactory.decodeResource(mResources, R.drawable.weather_wind2_19);
					else if(a.wind == 20) /* SE moderato */
						windBmp = BitmapFactory.decodeResource(mResources, R.drawable.weather_wind2_20);
					else if(a.wind == 21) /* S moderato */
						windBmp = BitmapFactory.decodeResource(mResources, R.drawable.weather_wind2_21);
					else if(a.wind == 22) /* SW moderato */
						windBmp = BitmapFactory.decodeResource(mResources, R.drawable.weather_wind2_22);
					else if(a.wind == 23) /* W moderato */
						windBmp = BitmapFactory.decodeResource(mResources, R.drawable.weather_wind2_23);
					else if(a.wind == 24) /* NW moderato */
						windBmp = BitmapFactory.decodeResource(mResources, R.drawable.weather_wind2_24);
					else if(a.wind == 25) /* N forte */
						windBmp = BitmapFactory.decodeResource(mResources, R.drawable.weather_wind2_25);
					else if(a.wind == 26) /* NE forte */
						windBmp = BitmapFactory.decodeResource(mResources, R.drawable.weather_wind2_26);
					else if(a.wind == 27) /* ENE forte */
						windBmp = BitmapFactory.decodeResource(mResources, R.drawable.weather_wind2_27);
					else if(a.wind == 28) /* E forte */
						windBmp = BitmapFactory.decodeResource(mResources, R.drawable.weather_wind2_28);				
					else if(a.wind == 29) /* SE forte */
						windBmp = BitmapFactory.decodeResource(mResources, R.drawable.weather_wind2_29);
					else if(a.wind == 30) /* S forte */
						windBmp = BitmapFactory.decodeResource(mResources, R.drawable.weather_wind2_30);
					else if(a.wind == 31) /* SW forte */
						windBmp = BitmapFactory.decodeResource(mResources, R.drawable.weather_wind2_31);
					else if(a.wind == 32) /* W forte */
						windBmp = BitmapFactory.decodeResource(mResources, R.drawable.weather_wind2_32);
					else if(a.wind == 33) /* NW forte */
						windBmp = BitmapFactory.decodeResource(mResources, R.drawable.weather_wind2_33);
					else if(a.wind == 34) /* ENE molto forte */
						windBmp = BitmapFactory.decodeResource(mResources, R.drawable.weather_wind2_red_34);
					else if(a.wind == 35) /* brezza */
						windBmp = BitmapFactory.decodeResource(mResources, R.drawable.weather_wind2_35);
					if(windBmp != null)
					{
						a.setWindSymbol(windBmp);
					}

					if(windLL != null)
						a.setWindLocationLatLng(windLL);

				} /* end if(fdi.getType() == ForecastDataType.AREA) */

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
					if(l.particularSnow == 10)
						l.setSnowBitmap(BitmapFactory.decodeResource(mResources, R.drawable.weather_particular_snow_10));
					else if(l.particularSnow == 11)
						l.setSnowBitmap(BitmapFactory.decodeResource(mResources, R.drawable.weather_particular_snow_11));
					else if(l.particularSnow == 12)
						l.setSnowBitmap(BitmapFactory.decodeResource(mResources, R.drawable.weather_particular_snow_12));
					if(l.particularStorm == 13)
						l.setLightningBitmap(BitmapFactory.decodeResource(mResources, R.drawable.weather_particular_storm_50x50_13));

				}

			}  /* end if(ll != null) */

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
				else if(line.matches("Z\\d+"))
				{
					fdi = new Zone(line, mResources);
					ret.add(fdi);
				}
				/* must look for fdi != null in case of malformed data (for instance when you connect to a 
				 * protected wi fi network and you are redirected to a login web page).
				 */
				else if(fdi != null && fdi.getType() == ForecastDataType.AREA)
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
						/* wind intensity and direction, 2000 and 3000m */
						else if(line.startsWith("v2i") && line.length() > 3)
							area.w2i = line.replace("v2i", "");
						else if(line.startsWith("v3i") && line.length() > 3)
							area.w3i = line.replace("v3i", "");
						else if(line.startsWith("v2d") && line.length() > 3)
							area.w2d = line.replace("v2d", "");
						else if(line.startsWith("v3d") && line.length() > 3)
							area.w3d = line.replace("v3d", "");

						else if(line.startsWith("C") && line.length() > 1) /* Cielo */
							area.sky = Integer.parseInt(line.replace("C", ""));
						else if(line.startsWith("P") && line.length() > 1)
							area.rain = Integer.parseInt(line.replace("P", ""));
						else if(line.startsWith("N") && line.length() > 1)
							area.snow = Integer.parseInt(line.replace("N", ""));
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
				else if(fdi != null && fdi.getType() == ForecastDataType.STRIP)
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
				else if(fdi != null && fdi.getType() == ForecastDataType.LOCALITY)
				{
					try{
						loc = (Locality ) fdi;
						if(line.startsWith("np") && line.length() > 2) /* neve particolare */
							loc.particularSnow = Integer.parseInt(line.replace("np", ""));
						else if(line.startsWith("tp") && line.length() > 1) /* temporali particolari */
							loc.particularStorm = Integer.parseInt(line.replace("tp", ""));
						else if(line.startsWith("tm") && line.length() > 2)
							loc.tMin = line.replace("tm", "");
						else if(line.startsWith("tM") && line.length() > 2)
							loc.tMax = line.replace("tM", "");
					}
					catch(NumberFormatException nfe)
					{

					}
				}
				else if(fdi != null && fdi.getType() == ForecastDataType.ZONE)
				{
					try{
						Zone  z = (Zone) fdi;
						if(line.startsWith("e00") && line.length() > 3)
							z.evo00 = Integer.parseInt(line.replace("e00", ""));
						else if(line.startsWith("e12") && line.length() > 3)
							z.evo12 = Integer.parseInt(line.replace("e12", ""));
						else if(line.startsWith("e24") && line.length() > 3)
							z.evo24 = Integer.parseInt(line.replace("e24", ""));
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
