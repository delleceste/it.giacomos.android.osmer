package it.giacomos.android.osmer.PROva.observations;

public class ObservationData {
	
	public String get(ObservationType t)
	{
		if (t == ObservationType.MIN_TEMP) {
			return tMin;
		} else if (t == ObservationType.MAX_TEMP) {
			return tMax;
		} else if (t == ObservationType.HUMIDITY) {
			return humidity;
		} else if (t == ObservationType.RAIN) {
			return rain;
		} else if (t == ObservationType.WIND) {
			return wind;
		} else if (t == ObservationType.SNOW) {
			return snow;
		} else if (t == ObservationType.PRESSURE) {
			return pressure;
		} else if (t == ObservationType.SEA) {
			return sea;
		} else if (t == ObservationType.AVERAGE_TEMP) {
			return tMed;
		} else if (t == ObservationType.SKY) {
			return sky;
		} else if (t == ObservationType.TEMP) {
			return temp;
		} else if (t == ObservationType.AVERAGE_WIND) {
			return vMed;
		} else if (t == ObservationType.MAX_WIND) {
			return vMax;
		} else if (t == ObservationType.AVERAGE_HUMIDITY) {
			return uMed;
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
