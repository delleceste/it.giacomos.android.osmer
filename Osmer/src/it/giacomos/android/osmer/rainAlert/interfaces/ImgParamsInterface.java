package it.giacomos.android.osmer.rainAlert.interfaces;

public interface ImgParamsInterface 
{
	public double getIntensityForColor(int[] array_rgb);
	
	public String getUnit();
	
	public double getThreshold();

	public double getBigIncreaseValue();
}
