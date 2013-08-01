package it.giacomos.android.meteofvg2.guiHelpers;

import java.util.Calendar;

import it.giacomos.android.meteofvg2.R;
import it.giacomos.android.meteofvg2.widgets.map.RadarOverlay;
import android.content.res.Resources;
import android.text.Html;

public class RadarImageTimestampTextBuilder 
{

	public CharSequence buildText(long currentTimestampMillis,
			long radarTimestampMillis, 
			Resources resources, 
			boolean isFirstExecution) 
	{
		CharSequence text;
		if(currentTimestampMillis - radarTimestampMillis < RadarOverlay.ACCEPTABLE_RADAR_DIFF_TIMESTAMP_MILLIS)
		{
			Calendar calendar = Calendar.getInstance();
		    calendar.setTimeInMillis(radarTimestampMillis);
		    text = android.text.format.DateFormat.format("kk:mm", calendar.getTime());
		    text = resources.getString(R.string.radarUpdatedOn)
					+ " <b>" + text + "</b>";
		}
		else
		{
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(radarTimestampMillis);
			text = android.text.format.DateFormat.format("dd MMM kk:mm", calendar.getTime());
			text = resources.getString(R.string.radarImageOld)
					+ " (<b>" + text + "</b>)";
			if(isFirstExecution)
				text  = text + "\n" + resources.getString(R.string.radarImageBlackWhiteHint);
		}
		return Html.fromHtml(text.toString());
	}

}
