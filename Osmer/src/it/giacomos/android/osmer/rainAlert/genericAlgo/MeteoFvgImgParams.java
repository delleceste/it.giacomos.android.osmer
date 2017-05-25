package it.giacomos.android.osmer.rainAlert.genericAlgo;

import it.giacomos.android.osmer.rainAlert.interfaces.ImgParamsInterface;

public class MeteoFvgImgParams implements ImgParamsInterface {

	public static double RAIN_THRESHOLD = 20.0;
	
	@Override
	public String getUnit() {
		return "dBZ";
	}

	@Override
	public double getThreshold() {
		return RAIN_THRESHOLD;
	}

	@Override
	public double getBigIncreaseValue() {
		return 10;
	}
	
	
	private boolean closeColors(int [] a1, int [] a2)
	{
		
		 int  r1 = a1[0];
		int g1 = a1[1];
		int r2 = a2[0];
		int g2 = a2[1];
		int  b1 = a1[2];
		int  b2 = a2[2];
		if( Math.abs(r1 - r2) < 10 && Math.abs( g1 -  g2) < 10 && Math.abs(b1 -  b2) < 10)
			return true;
		return false;
	}
	
	@Override
	public double getIntensityForColor(int[] arr_rgb)
	{
		/* green to yellow (255, 255, 8) */
		int [] g1 = {0, 64, 0};
		int []  g2 = {0, 120, 0};
		int []  g3 =  {0, 192, 0};
		int []  g4 =  {0, 255, 0};
		int []  g5 = {152, 255, 0};

		/* yellow */
		int [] y1 = {255, 255, 8};

		/* orange */
		int [] o1 = {255, 200, 0};
		int [] o2 = {252, 104, 0};

		/* red */
		int [] r1 = {255, 0, 0};
		int [] r2 = {160, 8, 0};

		/* brown */
		int [] b1 = {112, 88, 0};
		int [] b2 ={160, 120, 0};
		
		double dbz = 0;
		
		if(closeColors(arr_rgb,  g1))
			dbz = 12.5;
		else if(closeColors(arr_rgb,  g2))
			dbz = 17.5;
		else if(closeColors(arr_rgb,  g3))
			dbz = 22.5;
		else if(closeColors(arr_rgb,  g4))
			dbz = 27.5;
		else if(closeColors(arr_rgb,  g5))
			dbz = 32.5;
		else if(closeColors(arr_rgb, y1))
			dbz = 37.5;
		else if(closeColors(arr_rgb,  o1))
			dbz = 42.5;
		else if(closeColors(arr_rgb, o2))
			dbz = 47.5;
		else if(closeColors(arr_rgb,  r1))
			dbz = 52.5;
		else if(closeColors(arr_rgb,  r2))
			dbz = 57.5;
		else if(closeColors(arr_rgb,  b1))
			dbz = 62.5;
		else if(closeColors(arr_rgb,  b2))
			dbz = 67.5;
		return dbz;
				
	}
	
	
}
