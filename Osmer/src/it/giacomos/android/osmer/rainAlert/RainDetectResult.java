package it.giacomos.android.osmer.rainAlert;

public class RainDetectResult 
{
	public RainDetectResult()
	{
		dbz = 0.0f;
		willRain = false;
		deltas_matrix = null;
		last_dbz_matrix = null;
	}
	
	public float deltas_matrix[][];
	public float last_dbz_matrix[][];
	
	public float dbz;
	public boolean willRain;
}
