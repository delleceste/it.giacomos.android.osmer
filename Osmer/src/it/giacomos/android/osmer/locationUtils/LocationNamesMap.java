package it.giacomos.android.osmer.locationUtils;

import java.util.HashMap;
import java.util.Vector;
import java.lang.String;
import com.google.android.gms.maps.model.LatLng;

public class LocationNamesMap {

	public LocationNamesMap()
	{
		mMap = new HashMap<String, LatLng>();
		
		mMap.put("Udine", new LatLng(46.064313, 13.236250));
		mMap.put("Trieste", new LatLng(45.649882, 13.767350));
		mMap.put("Gradisca d'Is.", new LatLng(45.940168, 13.623733));
		mMap.put("Pordenone", new LatLng(45.962652, 12.655043));
		mMap.put("Tarvisio", new LatLng(46.505402, 13.578493));
		mMap.put("Cividale d.F.", new LatLng(46.090648, 13.435000));
		mMap.put("Grado", new LatLng(45.678462, 13.395962));
		mMap.put("GradoMare", new LatLng(45.675688, 13.394426));
		mMap.put("Aurisina", new LatLng(45.740805, 13.669846));
		mMap.put("Claut", new LatLng(46.217788, 12.472057));
		mMap.put("Lignano", new LatLng(45.690989, 13.138748));
		mMap.put("Faedis", new LatLng(46.152939, 13.346416));
		mMap.put("S.Vito al Tgl.", new LatLng(45.915116, 12.856410));
		mMap.put("Tolmezzo", new LatLng(46.406251, 13.012646));
		mMap.put("Paluzza", new LatLng(46.534680, 13.020762));
		mMap.put("Pontebba", new LatLng(46.504354, 13.305738));
		mMap.put("Zoncolan (1750 m)", new LatLng(46.500000, 12.916670));
		mMap.put("M.Zoncolan", new LatLng(46.500000	,12.916670));
		mMap.put("Zoncolan", new LatLng(46.500000	,12.916670));
		mMap.put("Forni di Sopra", new LatLng(46.423140, 12.583460));
		mMap.put("Barcis", new LatLng(46.190718,12.559284));
		mMap.put("Talmassons", new LatLng(45.930543,13.119952));
		mMap.put("Monfalcone", new LatLng(45.805047,13.533173));
		mMap.put("Fagagna", new LatLng(46.117736,13.08185));
		mMap.put("Enemonzo", new LatLng(46.411656,12.887197));
		mMap.put("Gemona d.F.", new LatLng(46.289147, 13.145738));
		mMap.put("Chievolis", new LatLng(46.254552, 12.735398));
		mMap.put("Vivaro", new LatLng(46.078226, 12.776906));	
		mMap.put("M.Matajur", new LatLng(46.212500, 13.529722));
		mMap.put("Coritis", new LatLng(46.360673, 13.354213));
		mMap.put("S.Vito al Tgl.", new LatLng(45.917978, 12.857311));
		mMap.put("Brugnera", new LatLng(45.901993, 12.526491));
		mMap.put("Piancavallo", new LatLng(46.107436, 12.522217));
		mMap.put("Ligosullo", new LatLng(46.539706,13.076066));
		mMap.put("Sauris", new LatLng(46.466675, 12.697044));
		mMap.put("Capriva d.F.", new LatLng(45.9423, 13.5145));
		mMap.put("Gorizia", new LatLng(45.9413046, 13.6215457));
		mMap.put("Borgo Grotta Gigante", new LatLng(45.705993, 13.763003));
		mMap.put("Gradisca d'Is.", new LatLng(45.892425, 13.500194));
	}
	
	public Vector<String> locationsForLevel(int level)
	{
		Vector<String> locations = new Vector<String>();
		switch(level)
		{
		case 11: case 12: case 13: case 14: case 15: case 16:
		case 17: case 18: case 19: case 20:
		case 21: case 22: case 23: case 24: case 25:
			locations.add("Enemonzo");
			locations.add("Chievolis");
			locations.add("M.Matajur");
			locations.add("Brugnera");
			locations.add("Claut");
			locations.add("Ligosullo");
		case 10:
			locations.add("Talmassons");
			locations.add("Monfalcone");
			locations.add("Coritis");
			locations.add("Piancavallo");
			locations.add("Faedis");
			locations.add("Gradisca d'Is.");
			locations.add("Fagagna");
			locations.add("Forni di Sopra");
		case 9:
			locations.add("S.Vito al Tgl.");
			locations.add("Barcis");
			locations.add("Paluzza");
			locations.add("Pontebba");
			locations.add("Vivaro");
			locations.add("Borgo Grotta Gigante");
			locations.add("Cividale d.F.");
		case 8:
			locations.add("Grado");
			locations.add("Gemona d.F.");
			locations.add("Lignano");
			locations.add("Tolmezzo");
			locations.add("M.Zoncolan");
			
			/* do not put break here: add also level 1 locations */

		default:
			locations.add("Udine");
			locations.add("Trieste");
			locations.add("Gorizia");
			locations.add("Pordenone");
			locations.add("Tarvisio");
			locations.add("Capriva d.F.");
			break;
		}
		
		return locations;
	}
	
	public LatLng get(String location)
	{
		if(mMap.containsKey(location))
			return mMap.get(location);
		return null;
	}
	
	private HashMap<String, LatLng> mMap = null;	
}
