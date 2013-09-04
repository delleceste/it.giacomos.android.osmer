package it.giacomos.android.osmer.pro.forecastRepr;

import it.giacomos.android.osmer.pro.locationUtils.LocationNamesMap;

import com.google.android.gms.maps.model.LatLng;

public class LatLngCalculator {

	public LatLng get(String tag)
	{
		LatLng ll = null;
		LocationNamesMap locMap = new LocationNamesMap();
		if(tag.compareTo("A1") == 0)
			ll = locMap.get("Tolmezzo");
		else if(tag.compareTo("A2") == 0)
			ll = locMap.get("Tarvisio");
		if(tag.compareTo("A3") == 0)
			ll = locMap.get("Chievolis");
		else if(tag.compareTo("A4") == 0)
			ll = locMap.get("Prealpi Giulie");
		

		else if(tag.compareTo("A5") == 0)
			ll = locMap.get("Pordenone");
		else if(tag.compareTo("A6") == 0)
			ll = locMap.get("Udine");
		else if(tag.compareTo("A7") == 0)
			ll = locMap.get("Gorizia");
		else if(tag.compareTo("A8") == 0)
			ll = locMap.get("Lignano");
		else if(tag.compareTo("A9") == 0)
			ll = locMap.get("Trieste");
		

		else if(tag.compareTo("F1") == 0)
			ll = locMap.get("Monti");
		else if(tag.compareTo("F2") == 0)
			ll = locMap.get("Alta Pianura");
		else if(tag.compareTo("F3") == 0)
			ll = locMap.get("Bassa Pianura");
		else if(tag.compareTo("F4") == 0)
			ll = locMap.get("Costa");
		
		else if(tag.compareTo("L1") == 0)
			ll = locMap.get("Gemona");
		else if(tag.compareTo("L2") == 0)
			ll = locMap.get("Carso");
		else if(tag.compareTo("L3") == 0)
			ll = locMap.get("Claut");
		else if(tag.compareTo("L4") == 0)
			ll = locMap.get("Cividale");
		
		else if(tag.compareTo("L5") == 0)
			ll = locMap.get("Sappada");
		else if(tag.compareTo("L6") == 0)
			ll = locMap.get("Piancavallo");
		else if(tag.compareTo("L7") == 0)
			ll = locMap.get("Tarvisio");
		/* others are discarded for now ... */
		
		return ll;
	}
}
