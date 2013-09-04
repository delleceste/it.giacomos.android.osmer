package it.giacomos.android.osmer.pro.forecastRepr;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

import it.giacomos.android.osmer.R;
import android.content.res.Resources;
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
		LatLngCalculator llcalc = new LatLngCalculator();
		for(ForecastDataInterface fdi : data)
		{
			LatLng ll = llcalc.get(fdi.getName());
			if(ll != null) /* name of the location is taken into account */
			{
				/* all ForecastDataInterface objects must have a LatLng */
				fdi.setLatLng(ll);
				if(fdi.getType() == ForecastDataType.AREA)
				{
					Area a = (Area) fdi;
					if(a.sky == 0)
						a.setDrawable(mResources.getDrawable(R.drawable.weather_clear));
					else if(a.sky == 1) /* poco nuvoloso */
						a.setDrawable(mResources.getDrawable(R.drawable.weather_few_clouds));
					else if(a.sky == 2) /* variabile */
						a.setDrawable(mResources.getDrawable(R.drawable.weather_clouds));
					else if(a.sky == 3) /* nuvoloso */
						a.setDrawable(mResources.getDrawable(R.drawable.weather_clouds));
					else if(a.sky == 4) /* coperto */
						a.setDrawable(mResources.getDrawable(R.drawable.weather_many_clouds));
					else if(a.sky == 5) /* sole/nebbia */
						a.setDrawable(mResources.getDrawable(R.drawable.weather_mist));
					
				}
				/* from strips we take temperatures and rain and storms probability (if there is 
				 * space to represent the last two quantities)
				 */
				else if(fdi.getType() == ForecastDataType.STRIP)
				{
					Strip s = (Strip ) fdi;
					
				}
				/* from locality we take into account special snow and storms for now, nothing else 
				 * 
				 */
				else if(fdi.getType() == ForecastDataType.LOCALITY)
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
				area = (Area) fdi;
				if(line.startsWith("PP"))
					area.rainProb = Html.fromHtml(line.replace("PP", ""));
				else if(line.startsWith("PT"))
					area.stormProb = Html.fromHtml(line.replace("PT", ""));
				else if(line.startsWith("T1"))
					area.t1000 = line.replace("T1", "");
				else if(line.startsWith("T2"))
					area.t1000 = line.replace("T2", "");
				else if(line.startsWith("ZT"))
					area.t1000 = line.replace("ZT", "");
				else if(line.startsWith("C")) /* Cielo */
					area.sky = Integer.parseInt(line.replace("C", ""));
				else if(line.startsWith("P"))
					area.rain = Integer.parseInt(line.replace("P", ""));
				else if(line.startsWith("T")) /* Temporale */
					area.storm = Integer.parseInt(line.replace("T", ""));
				else if(line.startsWith("B")) /* neBbia */
					area.mist = Integer.parseInt(line.replace("B", ""));
				else if(line.startsWith("V")) /* vento */
					area.wind = Integer.parseInt(line.replace("V", ""));
			}
			else if(fdi.getType() == ForecastDataType.STRIP)
			{	
				strip = (Strip) fdi;
				if(line.startsWith("T1"))
					strip.t1000 = line.replace("T1", "");
				else if(line.startsWith("T2"))
					strip.t1000 = line.replace("T2", "");
				else if(line.startsWith("Tm"))
					strip.tMin = line.replace("Tm", "");
				else if(line.startsWith("TM"))
					strip.tMax = line.replace("TM", "");
			}
			else if(fdi.getType() == ForecastDataType.LOCALITY)
			{
				loc = (Locality ) fdi;
				if(line.startsWith("N")) /* neve */
					loc.particularSnow = Integer.parseInt(line.replace("N", ""));
				else if(line.startsWith("T")) /* temporali particolari */
					loc.particularStorm = Integer.parseInt(line.replace("T", ""));

				else if(line.startsWith("Tm"))
					loc.tMin = line.replace("Tm", "");
				else if(line.startsWith("TM"))
					loc.tMax = line.replace("TM", "");
			}
			
		}
		
		buildDrawables(ret);
		
		return ret;
	}
}
