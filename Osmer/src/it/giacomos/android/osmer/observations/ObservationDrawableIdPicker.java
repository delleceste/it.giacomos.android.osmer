package it.giacomos.android.osmer.observations;

import it.giacomos.android.osmer.R;

public class ObservationDrawableIdPicker {
	
	/** 
	 * Depending on the oType parameter (MIN_TEMP, MEAN_TEMP..., HUMIDITY, MEAN_HUMIDITY... RAIN)
	 * this method returns a drawable to be used on the map for the itemized overlay.
	 * This is the marker used to draw all the overlay items in the map view.
	 * For instance, if oType is MIN_TEMP, a marker with a blue thermometer will be returned,
	 * if oType is RAIN, a cloud with rain will be returned, and so on.
	 * 
	 * The default marker is the "weather none available" icon, supposed to be used to initialize
	 * all the markers of the oType "SKY" observation. 
	 * 
	 * @param oType the ObservationType parameter (MIN_TEMP, MEAN_TEMP..., HUMIDITY, MEAN_HUMIDITY... RAIN)
	 * 
	 * @return the drawable id to be used to obtain the Drawable resource
	 */
	public static int pick(ObservationType oType)
	{
		int id = -1;
		switch(oType)
		{
		
		case TEMP:
		case MAX_TEMP:
		case MEAN_TEMP:
		case MIN_TEMP:
			id = R.drawable.temp_off;
			break;
		case HUMIDITY:
		case MEAN_HUMIDITY:
			id = R.drawable.drop_off;
			break;
		case RAIN:
			id = R.drawable.rain_off;
			break;
		case PRESSURE:
			id = R.drawable.gauge_off;
			break;
		case SNOW:
			id = R.drawable.snow_off;
			break;
		case SEA:
			id = R.drawable.sea_temp_off;
			break;
		case WIND:
		case MEAN_WIND:
		case MAX_WIND:
			id = R.drawable.wind_off;
			break;
		default:
			id = R.drawable.weather_none_available;
			break;
		}
		return id;
	}
}
