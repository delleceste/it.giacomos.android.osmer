package it.giacomos.android.osmer.pro.observations;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

import it.giacomos.android.osmer.pro.network.state.ViewType;
import it.giacomos.android.osmer.pro.regexps.Regexps;

public class TableToMap {
	public HashMap <String, ObservationData> convert(String table, final ViewType t)
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
			//Log.e("TableToMap", "Processing line " + lines[i]);
			ObservationData o = new ObservationData();
			if(t == ViewType.DAILY_TABLE)
			{
				pattern = Pattern.compile(Regexps.DAILY_TABLE);
				Matcher m = pattern.matcher(lines[i]);
				if(m.find() && m.groupCount() == 10)
				{
					o.location = m.group(1).trim();
					o.time = m.group(2).trim();
					o.sky = m.group(3).trim();
					o.tMin = m.group(4).trim() + "\u00b0C";
					o.tMed = m.group(5).trim() + "\u00b0C";
					o.tMax = m.group(6).trim() + "\u00b0C";
					o.uMed = m.group(7).trim() + "%";
					o.vMed = m.group(8).trim() + " [km/h]";
					o.vMax = m.group(9).trim() + " [km/h]";
					o.rain = m.group(10).trim() + "mm";
		//			Log.e("TableToMap", "inserting into DAILY map location " + o.location);
					map.put(o.location, o);
				}
			//	else
			//		Log.e("no match", "no match in DAILY_TABLE line " + lines[i]);
			}
			else
			{
				pattern = Pattern.compile(Regexps.LATEST_TABLE);
				Matcher m = pattern.matcher(lines[i]);
				if(m.find() && m.groupCount() == 10)
				{
					o.location = m.group(1).trim();
					o.time = m.group(2).trim();
					o.sky = m.group(3).trim();
					o.temp = m.group(4).trim() + "\u00b0C";
					o.humidity = m.group(5).trim() + "%";
					o.pressure = m.group(6).trim() + "hPA";
					o.wind = m.group(7).trim() + " [km/h]";
					o.rain = m.group(8).trim() + "mm";
					o.sea = m.group(9).trim() + "\u00b0C";
					o.snow = m.group(10).trim() + "cm";
					map.put(o.location, o);
				//Log.e("TableToMap", "inserting into LATEST map location " + o.location);
				}
				//else
				//	Log.e("no match", "no match in LATEST_TABLE line " + lines[i]);
			}	
		}
	//	Log.e("TableToMap", "Processing lines end  lines processed " + t);
		return map;	
	}

}
