package it.giacomos.android.osmer.pro.widgets.map.report;

import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.pro.widgets.map.MapBaloonInfoWindowAdapter;

import java.util.ArrayList;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

public class ReportOverlayTask extends AsyncTask<DataInterface, Integer, ArrayList<MarkerOptions> > 
{
	private Context mContext;
	private ReportOverlayTaskListener mReportOverlayTaskListener;
	
	public ReportOverlayTask(Context ctx, ReportOverlayTaskListener rotl)
	{
		super();
		mContext = ctx;
		mReportOverlayTaskListener = rotl;
	}
	
	@Override
	protected ArrayList<MarkerOptions> doInBackground(DataInterface... params) 
	{
		if(params == null)
			return null;
		
		Log.e("doInBackground" , "params size " + params.length);
		int sky, windIdx, iconId  = -1;
		int dataSiz = params.length;
		String text = "";
		String title = "";
		String skystr = "";
		String wind[] = mContext.getResources().getStringArray(R.array.report_wind_textitems);
		ArrayList<MarkerOptions> markerOptionsList = new ArrayList<MarkerOptions>();
		MarkerOptions markerOptions;
		Resources res = mContext.getResources();
		for(int i = 0; i < dataSiz; i++)
		{
			if(this.isCancelled())
				break;
			
			skystr = "";
			ReportData rd = params[i];
			sky = rd.sky;
			if(sky == 1)
			{
				iconId = R.drawable.weather_sky_0;
				skystr += res.getString(R.string.sky0);
			}
			else if(sky == 2)
			{
				iconId = R.drawable.weather_few_clouds;
				skystr += res.getString(R.string.sky1);
			}
			else if(sky == 3)
			{
				iconId = R.drawable.weather_clouds;
				skystr += res.getString(R.string.sky2);
			}
			else if(sky == 4)
			{
				iconId = R.drawable.weather_sky_3;
				skystr += res.getString(R.string.sky3);
			}
			else if(sky == 5)
			{
				iconId = R.drawable.weather_sky_4;
				skystr += res.getString(R.string.sky4);
			}
			else if(sky == 6)
			{
				iconId = R.drawable.weather_rain_cloud_6;
				skystr += res.getString(R.string.rain) + " " + res.getString(R.string.rain6);
			}
			else if(sky == 7)
			{
				iconId = R.drawable.weather_rain_cloud_7;
				skystr += res.getString(R.string.rain) + " " + res.getString(R.string.rain7_abbrev);
			}
			else if(sky == 8)
			{
				iconId = R.drawable.weather_rain_cloud_8;
				skystr += res.getString(R.string.rain) + " " + res.getString(R.string.rain8_abbrev);
			}
			else if(sky == 9)
			{
				iconId = R.drawable.weather_rain_cloud_9;
				skystr += res.getString(R.string.rain) + " " + res.getString(R.string.rain9);
			}
			else if(sky == 9)
			{
				iconId = R.drawable.weather_rain_cloud_36;
				skystr += res.getString(R.string.rain) + " " + res.getString(R.string.rain36);
			}
			else if(sky == 10)
			{
				iconId = R.drawable.weather_snow_10;
				skystr += res.getString(R.string.snow) + " " + res.getString(R.string.snow10);
			}
			else if(sky == 11)
			{
				iconId = R.drawable.weather_snow_11;
				skystr += res.getString(R.string.snow) + " " + res.getString(R.string.snow11);
			}
			else if(sky == 12)
			{
				iconId = R.drawable.weather_snow_12;
				skystr += res.getString(R.string.snow) + " " + res.getString(R.string.snow12);
			}
			else if(sky == 13)
			{
				iconId = R.drawable.weather_particular_storm_50x50_13;
				skystr += res.getString(R.string.storm);
			}
			else if(sky == 14)
			{
				iconId = R.drawable.weather_mist_14;
				skystr += res.getString(R.string.mist14);
			}
			else if(sky == 15)
			{
				iconId = R.drawable.weather_mist_15;
				skystr += res.getString(R.string.mist15);
			}
			
			title = rd.username;
			if(rd.locality.length() > 1)
				title += " - " + rd.locality;
			
			text = rd.datetime + "\n";
			
			if(skystr.length() > 0)
				text += skystr + "\n";
			
			if(rd.wind > 0)
				text += mContext.getResources().getString(R.string.wind) + ": " + wind[rd.wind] + "\n";
			
			try{
				Float.parseFloat(rd.temperature);
				text += mContext.getResources().getString(R.string.temp)  + ": " + rd.temperature + "C\n";
				
			}
			catch(NumberFormatException e)
			{
				
			}
			if(rd.comment.length() > 0)
				text += mContext.getResources().getString(R.string.reportComment) + ":\n" + rd.comment;
			
			markerOptions = new MarkerOptions();
			markerOptions.position(new LatLng(rd.latitude, rd.longitude));
			markerOptions.title(title);
			markerOptions.snippet(text);

			if(iconId > -1)
			{
				/* for sky no label, so do not use obsBmpFactory */
				BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(iconId);
				if(bitmapDescriptor != null)
					markerOptions.icon(bitmapDescriptor);
			}
			else
			{
				windIdx = rd.wind;
				if(windIdx == 1)
					iconId = R.drawable.weather_wind_calm;
				else if(windIdx == 2) /* breeze */
					iconId = R.drawable.weather_wind_35;
				else if(windIdx == 3)
					iconId = R.drawable.weather_wind_17; /* moderato */
				else if(windIdx == 4)
					iconId = R.drawable.weather_wind_26; /* moderato */
				else if(windIdx == 4)
					iconId = R.drawable.weather_wind2_red_34; /* moderato */
				
				if(iconId > -1)
				{
					BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(iconId);
					if(bitmapDescriptor != null)
						markerOptions.icon(bitmapDescriptor);
				}
				else
					markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
			}

				
			markerOptionsList.add(markerOptions);
		}
		return markerOptionsList;
	}
	
	@Override
	public void onCancelled(ArrayList<MarkerOptions> markerOptionsList)
	{
		
	}
	
	@Override
	public void onPostExecute(ArrayList<MarkerOptions> markerOptionsList)
	{
		mReportOverlayTaskListener.onReportOverlayTaskFinished(markerOptionsList);
	}
}
