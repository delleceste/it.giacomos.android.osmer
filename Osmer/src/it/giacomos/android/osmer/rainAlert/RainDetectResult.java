package it.giacomos.android.osmer.rainAlert;

public class RainDetectResult 
{
	public RainDetectResult(boolean willR, float _dbz)
	{
		dbz = _dbz;
		willRain = willR;
	}
	
	public float dbz;
	public boolean willRain;
}
