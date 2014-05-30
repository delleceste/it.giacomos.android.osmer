package it.giacomos.android.osmer.service.sharedData;

import java.util.ArrayList;

public class NotificationDataFactory 
{
	public ArrayList<NotificationData> parse(String input)
	{
		ArrayList<NotificationData> nDataArray = new ArrayList<NotificationData>();
		for(String line : input.split("\n"))
		{
			String parts[] = line.split("::", -1);
			if(parts.length == 7)
				nDataArray.add(new ReportRequestNotification(line));
			else if(parts.length == 5)
				nDataArray.add(new ReportNotification(line));
			else if(parts.length == 4) /* R::date-time::1|0::dbz */
				nDataArray.add(new RainNotification(line));
			else
				/* nothing to do here */
				;
		}
		return nDataArray;
	}
}
