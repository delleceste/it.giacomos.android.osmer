package it.giacomos.android.osmer.observations;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

import it.giacomos.android.osmer.Regexps;
import it.giacomos.android.osmer.StringType;

public class TableToMap {
	public HashMap <String, ObservationData> convert(String table, final StringType t)
	{
		Pattern pattern;
		HashMap <String, ObservationData> map = new HashMap<String, ObservationData>();
		/* parse table */
		/* For n > 0, the resulting array contains at most n entries. 
		 * If this is fewer than the number of matches, the final entry 
		 * will contain all remaining input.
		 * For n < 0, the length of the resulting array is exactly the 
		 * number of occurrences of the Pattern plus one for the text after the final separator. All entries are included. 
		 * 
		 * For n == 0, the result is as for n < 0, except trailing 
		 * empty strings will not be returned. (Note that the case where the 
		 * input is itself an empty string is special, as described 
		 * above, and the limit parameter does not apply there.) 
		 */
		String [] lines = table.split("\n");
	//	Log.e("TableToMap", "Processing lines start   " + t);
		
		/* cnt points to the first line containing Barcis */
		for(int i = 0; i < lines.length; i++)
		{
			String[] parts = lines[i].split("\t");
			ObservationData o = new ObservationData();
			if(t == StringType.DAILY_TABLE)
			{
				if(parts.length == 10)
				{
					o.location = parts[0];
					o.time = parts[1];
					o.sky =  parts[2];;
					o.tMin = parts[3] + "\u00b0C";
					o.tMed = parts[4] + "\u00b0C";
					o.tMax = parts[5] + "\u00b0C";
					o.uMed = parts[6]  + "%";
					o.vMed = parts[7]  + " [km/h]";
					o.vMax = parts[8]  + " [km/h]";
					o.rain = parts[9]  + "mm";
					map.put(o.location, o);
				}
			}
			else
			{
				if(parts.length == 11)
				{
					o.location = parts[0];
					o.time = parts[1];
					o.sky = parts[2];
					o.temp = parts[3] + "\u00b0C";
					o.humidity = parts[4] + "%";
					o.pressure = parts[5] + "hPA";
					o.wind = parts[6] + " [km/h]";
					o.rain = parts[8] + "mm";
					o.sea = parts[9] + "\u00b0C";
					o.snow = parts[10] + "cm";
					map.put(o.location, o);
				}
			}	
		}
		return map;	
	}

}
