package it.giacomos.android.osmer.PROva.webcams;


import java.util.ArrayList;

public interface WebcamListDecoder {
	
	public ArrayList<WebcamData> decode(String rawData);

}
