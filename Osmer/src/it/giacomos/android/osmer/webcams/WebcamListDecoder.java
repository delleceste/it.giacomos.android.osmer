package it.giacomos.android.osmer.webcams;


import java.util.ArrayList;

public interface WebcamListDecoder {
	
	public ArrayList<WebcamData> decode(String rawData);

}
