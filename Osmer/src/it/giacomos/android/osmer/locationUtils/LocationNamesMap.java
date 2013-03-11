package it.giacomos.android.osmer.locationUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.lang.String;
import com.google.android.maps.GeoPoint;

public class LocationNamesMap {

	public LocationNamesMap()
	{
		mMap = new HashMap<String, GeoPoint>();
		
		mMap.put("Udine", new GeoPoint(46064313, 13236250));
		mMap.put("Trieste", new GeoPoint(45649882, 13767350));
		mMap.put("Gradisca d'Is.", new GeoPoint(45940168, 13623733));
		mMap.put("Pordenone", new GeoPoint(45962652, 12655043));
		mMap.put("Tarvisio", new GeoPoint(46505402, 13578493));
		mMap.put("Cividale d.F.", new GeoPoint(46090648, 13435000));
		mMap.put("Grado", new GeoPoint(45678462, 13395962));
		mMap.put("GradoMare", new GeoPoint(45675688, 13394426));
		mMap.put("Aurisina", new GeoPoint(45740805, 13669846));
		mMap.put("Claut", new GeoPoint(46217788, 12472057));
		mMap.put("Lignano", new GeoPoint(45690989, 13138748));
		mMap.put("Faedis", new GeoPoint(46152939, 13346416));
		mMap.put("S.Vito al Tgl.", new GeoPoint(45915116, 12856410));
		mMap.put("Tolmezzo", new GeoPoint(46406251, 13012646));
		mMap.put("Paluzza", new GeoPoint(46534680, 13020762));
		mMap.put("Pontebba", new GeoPoint(46504354, 13305738));
		mMap.put("Zoncolan (1750 m)", new GeoPoint(46500000	,12916670));
		mMap.put("M.Zoncolan", new GeoPoint(46500000	,12916670));
		mMap.put("Zoncolan", new GeoPoint(46500000	,12916670));
		mMap.put("Forni di Sopra", new GeoPoint(46423140,	12583460));
		mMap.put("Barcis", new GeoPoint(46190718,12559284));
		mMap.put("Talmassons", new GeoPoint(45930543,13119952));
		mMap.put("Monfalcone", new GeoPoint(45805047,13533173));
		mMap.put("Fagagna", new GeoPoint(46117736,1308185));
		mMap.put("Enemonzo", new GeoPoint(46411656,12887197));
		mMap.put("Gemona d.F.", new GeoPoint(46289147,13145738));
		mMap.put("Chievolis", new GeoPoint(46254552,12735398));
		mMap.put("Vivaro", new GeoPoint(46078226,12776906));	
		mMap.put("M.Matajur", new GeoPoint(46212500,13529722));
		mMap.put("Coritis", new GeoPoint(46360673,13354213));
		mMap.put("S.Vito al Tgl.", new GeoPoint(45917978,12857311));
		mMap.put("Brugnera", new GeoPoint(45901993,12526491));
		mMap.put("Piancavallo", new GeoPoint(46107436,12522217));
		mMap.put("Ligosullo", new GeoPoint(46539706,13076066));
		mMap.put("Sauris", new GeoPoint(46466675, 12697044));
		
	}
	
	public Vector<String> locationsForLevel(int level)
	{
		Vector<String> locations = new Vector<String>();
		switch(level)
		{
		case 13:
			locations.add("Enemonzo");
			locations.add("Chievolis");
			locations.add("M.Matajur");
			locations.add("Brugnera");
		case 12:
			locations.add("Barcis");
			locations.add("Talmassons");
			locations.add("Monfalcone");
			locations.add("Coritis");
			locations.add("Piancavallo");
			locations.add("M.Zoncolan");
			locations.add("Faedis");
		case 11:
			locations.add("S.Vito al Tgl.");
			locations.add("Tolmezzo");
			locations.add("Paluzza");
			locations.add("Pontebba");
			locations.add("Vivaro");
			locations.add("Cividale d.F.");
			locations.add("S.Vito al Tgl.");
			/* do not put break here: add also level 1 locations */
		case 10:
			locations.add("Capriva d.F.");
			locations.add("Grado");
			locations.add("Gemona d.F.");
			locations.add("Lignano");
			
			/* do not put break here: add also level 1 locations */
		case 9:
		default:
			locations.add("Udine");
			locations.add("Trieste");
			locations.add("Gorizia");
			locations.add("Pordenone");
			locations.add("Tarvisio");
			break;
		}
		
		return locations;
	}
	
	public GeoPoint get(String location)
	{
		return mMap.get(location);
	}
	
	private HashMap<String, GeoPoint> mMap = null;	
}
