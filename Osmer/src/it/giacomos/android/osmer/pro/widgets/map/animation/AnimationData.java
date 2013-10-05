package it.giacomos.android.osmer.pro.widgets.map.animation;

import java.util.Calendar;

public class AnimationData {

	public AnimationData(String tim, String fName)
	{
		int lastIndexOfColon = tim.lastIndexOf(':');
		int indexOfFirstHyphen = tim.indexOf('-');
		String yearstr = tim.substring(0, indexOfFirstHyphen);
		if(lastIndexOfColon > -1)
			time = tim.substring(0, lastIndexOfColon);
		else
			time = tim;
		/* remove year if the same as current year */
		int year = Calendar.getInstance().get(Calendar.YEAR);
		if(year == Integer.parseInt(yearstr))
			time = time.substring(indexOfFirstHyphen + 1);
		fileName = fName;
	}
	
	public String fileName;
	public String time;
}
