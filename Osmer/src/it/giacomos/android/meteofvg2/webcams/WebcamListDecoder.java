package it.giacomos.android.meteofvg2.webcams;


import java.util.ArrayList;

public interface WebcamListDecoder {
	
	public ArrayList<WebcamData> decode(String rawData, boolean saveOnCache);

}
