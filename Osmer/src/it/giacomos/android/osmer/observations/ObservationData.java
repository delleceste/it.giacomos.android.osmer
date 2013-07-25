package it.giacomos.android.osmer.observations;

public class ObservationData {
	
	public String get(ObservationType t)
	{
		switch(t)
		{
		case MIN_TEMP:
			return tMin;
		case MAX_TEMP:
			return tMax;
		case HUMIDITY:
			return humidity;
		case RAIN:
			return rain;
		case WIND:
			return wind;
		case SNOW:
			return snow;
		case PRESSURE:
			return pressure;
		case SEA:
			return sea;
		case MEAN_TEMP:
			return tMed;
		case SKY:
			return sky;
		case TEMP:
			return temp;
		case MEAN_WIND:
			return vMed;
		case MAX_WIND:
			return vMax;
		case MEAN_HUMIDITY:
			return uMed;
		case WEBCAM:
			return "webcam";
		}
		return null;
	}
	
	public boolean has(ObservationType t)
	{
		String data = get(t);
		return data != null && data != "" && !data.contains("---");
	}
	
	public String location;
	public String time;
	public String sky;
	public String tMin; /* daily */
	public String tMax; /* daily */
	public String tMed; /* daily */
	public String temp; /* latest */
	public String humidity;
	public String uMed;
	public String vMed;
	public String vMax;
	public String rain;
	public String sea;
	public String snow;
	public String pressure; /* latest */
	public String wind;
}
