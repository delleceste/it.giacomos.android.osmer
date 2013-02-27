package it.giacomos.android.osmer.guiHelpers;

import java.util.ArrayList;

import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.StringType;
import it.giacomos.android.osmer.webcams.*;
import it.giacomos.android.osmer.widgets.mapview.*;

public class WebcamMapUpdater 
{
	public WebcamMapUpdater(OsmerActivity a, String text, StringType t, boolean saveOnCache)
	{
		if(!text.isEmpty())
		{
			switch(t)
			{
			case WEBCAMLIST_OSMER:
				OsmerWebcamListDecoder osmerDec = new OsmerWebcamListDecoder();
				ArrayList<WebcamData> wcData = osmerDec.decode(text, saveOnCache);
				OMapView map = (OMapView) a.findViewById(R.id.mapview);
				map.updateWebcamList(wcData);
				break;
			case WEBCAMLIST_OTHER:
				break;
			default:
				break;
			}
		}
	}
}
