package it.giacomos.android.osmer.pro.widgets.map.report;

import java.util.ArrayList;

import android.util.Log;

public class ReportDataFactory 
{
	public ReportData[] parse(String txt)
	{
		ReportData[] ret = null;

		if(txt.length() > 0)
		{
			/* a line is made up like this 
			 * 2013-11-30 12:07:42::giacomo::45.6525389::13.7837237::1::1::8.3::a b c
			 * ReportData(String u, String d, String l, String c, String t, int s, int w)
			 */
			String [] lines = txt.split("\n");
			String line;
			double lat, lon;
			int sky, wind;
			if(lines.length > 0)
				ret = new ReportData[lines.length];

			for(int i = 0; i < lines.length; i++)
			{
				line = lines[i];
				ReportData rd = null;
				String [] parts = line.split("::", -1);
				Log.e("ReportDataFactory.parse", line + ", " +parts.length);
				
				sky = wind = -1;
				if(parts.length > 7)
				{
					try
					{
						sky = Integer.parseInt(parts[4]);
						wind = Integer.parseInt(parts[5]);
					}
					catch(NumberFormatException e)
					{
						Log.e("ReportDataFactory: error converting sky and wind indexes", e.toString());
					}
					try{
						lat = Float.parseFloat(parts[2]);
						lon = Float.parseFloat(parts[3]);
						/* a "-" for the locality for now */
						rd = new ReportData(parts[1], parts[0], "-", parts[7], parts[6], sky, wind, lat, lon);
						ret[i] = rd;
					}
					catch(NumberFormatException e)
					{
						Log.e("ReportDataFactory: error getting latitude or longitude", e.toString());
					}


				}
			}
		}

		return ret;
	}

}
