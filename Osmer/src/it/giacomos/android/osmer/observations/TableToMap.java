package it.giacomos.android.osmer.observations;

import java.util.HashMap;

import it.giacomos.android.osmer.network.state.ViewType;

public class TableToMap {
	public HashMap <String, ObservationData> convert(String table, final ViewType t)
	{
		HashMap <String, ObservationData> map = new HashMap<String, ObservationData>();
		/* parse table 
		 *
		 */
		String [] lines = table.split("\n");
		
		/* cnt points to the first line containing Barcis */
		for(int i = 0; i < lines.length; i++)
		{
			String[] parts = lines[i].split("\t");
			ObservationData o = new ObservationData();
			if(t == ViewType.DAILY_TABLE)
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
					o.vMax = parts[7] + " [km/h]";
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
