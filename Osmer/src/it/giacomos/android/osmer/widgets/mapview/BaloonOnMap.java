package it.giacomos.android.osmer.widgets.mapview;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.observations.ObservationData;
import it.giacomos.android.osmer.observations.ObservationTime;
import it.giacomos.android.osmer.observations.ObservationType;
import it.giacomos.android.osmer.observations.SkyDrawableIdPicker;

public class BaloonOnMap {
	public BaloonOnMap(OMapView mapView, ObservationData od)
	{
		
	}

	public BaloonOnMap(MapView mapView, ObservationData observationData, GeoPoint point) 
	{
		OMapView oMapView = (OMapView) mapView;
		MapBaloon oldBaloon = (MapBaloon) mapView.findViewById(R.id.mapbaloon);
		if(oldBaloon != null)
		{
			/* remove old baloon */
			mapView.removeView(oldBaloon);
			/* This view is invisible, and it doesn't take any space for layout purposes. */
			oldBaloon.setVisibility(View.GONE);
		}

		LayoutInflater  layoutInflater = (LayoutInflater) mapView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        MapBaloon mapBaloon = (MapBaloon) layoutInflater.inflate(R.layout.mapbaloon, null);
        MapView.LayoutParams layoutParams = new MapView.LayoutParams(
        		300, LayoutParams.WRAP_CONTENT,
         		point, MapView.LayoutParams.BOTTOM_CENTER);
        mapBaloon.setLayoutParams(layoutParams);  
        
        mapBaloon.setTitle(observationData.location);
        mapBaloon.setText(makeText(observationData, oMapView));
        mapBaloon.setIcon(SkyDrawableIdPicker.get(observationData.sky));
        mapView.addView(mapBaloon);
        mapBaloon.setVisibility(View.VISIBLE);   
	}
	
	private String makeText(ObservationData od, OMapView mapView)
	{
		Resources res = mapView.getResources();
		MapViewMode m = mapView.getMode();
		String txt;
		
		txt = od.time + " - " + res.getString(R.string.sky) + ": " + od.sky + "\n";
		
		if(m.currentMode == ObservationTime.DAILY)
		{
			if(m.currentType == ObservationType.SKY || 
					m.currentType == ObservationType.MIN_TEMP ||  
					m.currentType == ObservationType.MAX_TEMP ||  
					m.currentType == ObservationType.MEAN_TEMP)
			{
				boolean hasTMin, hasTMax;
				hasTMin = od.has(ObservationType.MIN_TEMP);
				hasTMax = od.has(ObservationType.MAX_TEMP);
				/* tmin and tmax abbreviated on the same line */
				if(hasTMin)
					txt += res.getString(R.string.min_temp_abbr) + ": " + od.tMin;
				if(hasTMax)
					txt += res.getString(R.string.max_temp_abbr) + ": " + od.tMax;
				if(hasTMin || hasTMax) /* newline if necessary */
					txt += "\n";
				if(od.has(ObservationType.MEAN_TEMP))
					txt += res.getString(R.string.mean_temp) + ": " + od.tMed + "\n";
			}
			else if(m.currentType == ObservationType.RAIN)
			{
				if(od.has(ObservationType.RAIN))
					txt += res.getString(R.string.rain) + ": " + od.rain + "\n";	
			}
			else if(m.currentType == ObservationType.MAX_WIND || m.currentType == ObservationType.MEAN_WIND)
			{
				if(od.has(ObservationType.MEAN_WIND))
					txt += res.getString(R.string.mean_wind) + ": " + od.vMed + "\n";
				if(od.has(ObservationType.MAX_WIND))
					txt += res.getString(R.string.max_wind) + ": " + od.vMax + "\n";
			}
			else if(m.currentType == ObservationType.MEAN_HUMIDITY)
			{
				if(od.has(ObservationType.MEAN_HUMIDITY))
					txt += res.getString(R.string.mean_humidity) + ": " + od.uMed + "\n";
			}
		}
		else 
		{
			if(m.currentType == ObservationType.SKY || 
					m.currentType == ObservationType.TEMP ||  
					m.currentType == ObservationType.SEA ||  
					m.currentType == ObservationType.SNOW || 
					m.currentType == ObservationType.RAIN)
			{
				if(od.has(ObservationType.TEMP))
					txt += res.getString(R.string.temp) + ": " + od.temp + "\n";
				if(od.has(ObservationType.SEA))
					txt += res.getString(R.string.sea) + ": " + od.sea + "\n";
				if(od.has(ObservationType.SNOW))
					txt += res.getString(R.string.snow) + ": " + od.snow + "\n";
				if(od.has(ObservationType.RAIN))
				{
					String rain = od.rain;
					rain = rain.replaceAll("[^\\d+\\.)]", "");
					/* when observation type is rain, show rain even if rain is 0.0 */
					if(Float.parseFloat(rain) > 0.0f || m.currentType == ObservationType.RAIN)
						txt += res.getString(R.string.rain) + ": " + od.rain + "\n";
				}
			}
			else if(m.currentType == ObservationType.HUMIDITY && od.has(ObservationType.HUMIDITY))
				txt += res.getString(R.string.humidity) + ": " + od.humidity + "\n";
			else if(m.currentType == ObservationType.PRESSURE && od.has(ObservationType.PRESSURE))
				txt += res.getString(R.string.pressure) + ": " + od.pressure + "\n";
			
			else if(m.currentType == ObservationType.WIND && od.has(ObservationType.WIND))
				txt += res.getString(R.string.wind) + ": " + od.wind + "\n";
		}

		return txt;
	}

}
