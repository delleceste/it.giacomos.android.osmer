/**
 * 
 */
package it.giacomos.android.osmer.observations;

import it.giacomos.android.osmer.pro.R;

import java.util.Calendar;
import java.util.Date;
import android.util.Log;


/**
 * @author giacomo
 *
 */
public class SkyDrawableIdPicker {

	/**
	 * 
	 */
	public int get(String sky) 
	{
		int id = -1;
		/* what time is it? decide icons */
		Date d = Calendar.getInstance().getTime();
		int sunset, dawn;
		int month = d.getMonth();
		if((month > 2 && month < 5) || month > 8 && month < 11)
		{
			dawn = 6;
			sunset = 19;
		}
		else if(month > 4 && month < 9)
		{
			dawn = 5;
			sunset = 21;
		}
		else if(month == 12)
		{
			dawn = 7;
			sunset = 17;
		}
		else
		{
			dawn = 7;
			sunset = 18;
		}
		int hours = d.getHours();
		boolean night;
		if(hours >= dawn && hours <= sunset)
			night = false;
		else
			night = true;
		
		/* choose the right icon now! */
		if(sky.contains("sereno"))
		{
			if(night)
				id = (R.drawable.weather_clear_night);
			else
				id = (R.drawable.weather_clear);
		}
		else if(sky.contains("poco") && sky.contains("nuv"))
		{
			if(night)
				id = (R.drawable.weather_few_clouds_night);
			else
				id = (R.drawable.weather_few_clouds);
		}
		else if(sky.contains("nuvoloso"))
		{
			if(night)
				id = (R.drawable.weather_clouds_night);
			else
			{
				id = (R.drawable.weather_clouds);
			}
		}
		else if(sky.contains("coperto"))
		{
			/* the same for day and night */
			id = (R.drawable.weather_many_clouds);
		}
		else if(sky.contains("variabil"))
		{
			if(night)
				id = (R.drawable.weather_variable_showers_night);
			else
				id = (R.drawable.weather_variable_showers);
		}	
		else if(sky.contains("piogg"))
		{
			id = (R.drawable.weather_showers);
		}
		else if(sky.contains("neve"))
		{
				id = (R.drawable.weather_snow);
		}
		else if(sky.contains("neve") && sky.contains("piogg"))
		{
				id = (R.drawable.snow_rain);
		}
		else if(sky.contains("temporal"))
		{
			if(night)
				id = (R.drawable.weather_storm_night);
			else
				id = (R.drawable.weather_storm);
		}
		else if(sky.contains("grandin"))
		{
			id = (R.drawable.hail);
		}
		else if(sky.contains("piogg") && sky.contains("ghiac"))
		{
			id = (R.drawable.freezing_rain);
		}
		else if(sky.contains("nebbia"))
			id = (R.drawable.weather_mist);
		
		return id;
	}
}
