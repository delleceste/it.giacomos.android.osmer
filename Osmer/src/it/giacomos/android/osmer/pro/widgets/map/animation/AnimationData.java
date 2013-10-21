package it.giacomos.android.osmer.pro.widgets.map.animation;

import java.util.Calendar;

/* since version 2.5, the php script "get_radar_files.php" prepares a timestamp 
 * string which is ready to use, without the need to parse it.
 */
public class AnimationData {

	public AnimationData(String tim, String fName)
	{
		int lastIndexOfColon = tim.lastIndexOf(':');
		int indexOfFirstHyphen = tim.indexOf('-');
		String yearstr = "";

		if(lastIndexOfColon > -1 && indexOfFirstHyphen > -1)
		{
			yearstr = tim.substring(0, indexOfFirstHyphen);
			time = tim.substring(0, lastIndexOfColon);
			/* remove year if the same as current year */
			int year = Calendar.getInstance().get(Calendar.YEAR);
			if(year == Integer.parseInt(yearstr))
				time = time.substring(indexOfFirstHyphen + 1);
		}
		else
			time = tim;
		
		fileName = fName;
	}
	
	public String fileName;
	public String time;
}
