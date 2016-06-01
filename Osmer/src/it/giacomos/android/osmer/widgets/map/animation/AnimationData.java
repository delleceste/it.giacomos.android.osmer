package it.giacomos.android.osmer.widgets.map.animation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/* since version 2.5, the php script "get_radar_files.php" prepares a timestamp 
 * string which is ready to use, without the need to parse it.
 */
public class AnimationData {

	public AnimationData(String tim, String fName)
	{
		/* tim arrives in the format DAY/MONTH */
		int year = Calendar.getInstance().get(Calendar.YEAR);
		String format = "yyyy/dd/MM HH:mm";
		String input = String.valueOf(year) + "/" + tim;
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		try {
			Date date = sdf.parse(input);
			String outFormat = "dd.MM.yyyy HH:mm";
			sdf = new SimpleDateFormat(outFormat);
			sdf.setTimeZone(TimeZone.getDefault());
			time = sdf.format(date);
			
		} catch (ParseException e) {
			e.printStackTrace();
			time = tim;
		}
		
		fileName = fName;
	}
	
	public String fileName;
	public String time;
}
