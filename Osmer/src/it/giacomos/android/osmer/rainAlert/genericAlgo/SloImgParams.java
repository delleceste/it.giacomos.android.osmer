package it.giacomos.android.osmer.rainAlert.genericAlgo;

import it.giacomos.android.osmer.rainAlert.interfaces.ImgParamsInterface;

/**
 * Created by giacomo on 11/25/16.
 */

public class SloImgParams implements ImgParamsInterface
{
    public static double RAIN_THRESHOLD = 0.5;

    @Override
    public String getUnit() {
        return "mm/ora";
    }

    @Override
    public double getThreshold() {
        return RAIN_THRESHOLD;
    }

    @Override
    public double getBigIncreaseValue() {
        return 5;
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
		/* blue  */
        int []  b1 = {8, 90, 254}; /* 0.25 */
        int []  b2 = {0, 140, 254}; /* 0.5 */
        int []  b3 =  {6, 174, 253}; /* 0.75 */
        int []  b4 =  {0, 200, 254}; /* 1.0 */



		/* greens */
        int [] g1 = { 4, 216, 131 }; /* 1.5 */
        int [] g2 = { 66, 235, 66 }; /* 2 */
        int [] g3 = { 108, 249, 0 }; /* 3.5 */
        int [] g4 = { 184, 250, 0 }; /* 5 */

        /* yellows */

        int [] y1 = {249, 350, 0 }; /* 10 */
        int [] y2 = {254, 198, 0 }; /* 15 */
        int [] y3 = {254, 132, 0}; /* 32.5 */

		/* reds */
        int [] r1 = {255, 62, 1}; /* 50 */
        int [] r2 = {211,0,0}; /* 75 */
        int [] r3 = {181, 3, 3}; /* 100 */

        /* violet */
        int [] v1 = { 203, 0, 204 }; /* 150 */

		/* brown */

        double intensity = 0;

        if(closeColors(arr_rgb,  b1))
            intensity = 0.25;
        else if(closeColors(arr_rgb,  b2))
            intensity = 0.5;
        else if(closeColors(arr_rgb,  b3))
            intensity = 0.75;
        else if(closeColors(arr_rgb,  b4))
            intensity = 1.0;

         if(closeColors(arr_rgb,  g1))
            intensity = 1.5;
        else if(closeColors(arr_rgb,  g2))
            intensity = 2.0;
        else if(closeColors(arr_rgb,  g3))
            intensity = 3.5;
        else if(closeColors(arr_rgb,  g4))
            intensity = 5.0;

        else if(closeColors(arr_rgb,  y1))
            intensity = 10.0;
        else if(closeColors(arr_rgb,  y2))
            intensity = 15.0;
        else if(closeColors(arr_rgb,  y3))
            intensity = 32.5;

         else if(closeColors(arr_rgb,  r1))
            intensity = 50.0;
        else if(closeColors(arr_rgb,  r2))
            intensity = 75.0;
        else if(closeColors(arr_rgb,  r3))
            intensity = 100;

        else if(closeColors(arr_rgb,  v1))
            intensity = 150;

        return intensity;

    }

}
