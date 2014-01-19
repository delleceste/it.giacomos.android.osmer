package it.giacomos.android.osmer.pro.widgets.map.report;

import java.util.ArrayList;

import android.util.Log;

public class DataParser 
{
	public ReportData[] parseReports(String txt)
	{
		ReportData[] ret = null;
		ArrayList<ReportData> tmpArray = new ArrayList<ReportData>();

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

			for(int i = 0; i < lines.length; i++)
			{
				line = lines[i];
				if(line.startsWith("R::")) /* report */
				{
					ReportData rd = null;
					String [] parts = line.split("::", -1);
				//	Log.e("DataParser.parseReports", line + ", " +parts.length);

					sky = wind = -1;
					if(parts.length > 10) /* should be 11, since locality has been added */
					{
						try
						{
							sky = Integer.parseInt(parts[6]);
							wind = Integer.parseInt(parts[7]);
						}
						catch(NumberFormatException e)
						{
							Log.e("ReportDataFactory: error converting sky and wind indexes", e.toString());
						}
						try{
							lat = Float.parseFloat(parts[4]);
							lon = Float.parseFloat(parts[5]);
							/*
							 * ReportData(String user, String datet, String l, String c, 
							 * String t, int s, int w, double lat, double longi, String writa)
							 */
							/* a "-" for the locality for now */
							/* parts[1] is writable */
							rd = new ReportData(parts[3], parts[2], parts[10], parts[9], parts[8], 
									sky, wind, lat, lon, parts[1]);
							tmpArray.add(rd);
						}
						catch(NumberFormatException e)
						{
							Log.e("ReportDataFactory: error getting latitude or longitude", e.toString());
						}
					}
					if(rd == null) /* a parse error occurred: invalidate all document parsing */
					{
						tmpArray.clear();
						break;
					}
				}
			}
		}

		if(tmpArray.size() > 0)
			ret = tmpArray.toArray(new ReportData[tmpArray.size()]);
		
		return ret;
	}
	
	public RequestData[] parseRequests(String txt)
	{
		RequestData[] ret = null;
		ArrayList<RequestData> tmpArray = new ArrayList<RequestData>();

		if(txt.length() > 0)
		{
			/* a line is made up like this 
			 * 2013-11-30 12:07:42::giacomo::45.6525389::13.7837237::1::1::8.3::a b c
			 * ReportData(String u, String d, String l, String c, String t, int s, int w)
			 */
			String [] lines = txt.split("\n");
			String line;
			double lat, lon;

			for(int i = 0; i < lines.length; i++)
			{
				line = lines[i];
				if(line.startsWith("Q::")) /* request */
				{
					RequestData rd = null;
					String [] parts = line.split("::", -1);
				//	Log.e("DataParser.parseRequests", line + ", " +parts.length);

					if(parts.length > 6) /* should be 7 */
					{
						try{
							lat = Double.parseDouble(parts[4]);
							lon = Double.parseDouble(parts[5]);
							/* parts[1] is writable */
							/* RequestData(String d, String user, double la, double lo, String wri, boolean isSatisfied) */
							rd = new RequestData(parts[2], parts[3], parts[6], lat, lon, parts[1], true);
							tmpArray.add(rd);
						}
						catch(NumberFormatException e)
						{
							Log.e("DataParser: error getting latitude or longitude", e.toString());
						}


					}
					if(rd == null) /* a parse error occurred: invalidate all document parsing */
						return null;
				}
			}
		}

		if(tmpArray.size() > 0)
			ret = tmpArray.toArray(new RequestData[tmpArray.size()]);
		
		return ret;
	}
	

}
