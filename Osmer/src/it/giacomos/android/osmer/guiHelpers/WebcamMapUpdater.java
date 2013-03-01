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
			ArrayList<WebcamData> wcData = null;
			OMapView map = (OMapView) a.findViewById(R.id.mapview);
			switch(t)
			{
			case WEBCAMLIST_OSMER:
				OsmerWebcamListDecoder osmerDec = new OsmerWebcamListDecoder();
				wcData = osmerDec.decode(text, saveOnCache);
				map.updateWebcamList(wcData);
				break;
			case WEBCAMLIST_OTHER:
				OtherWebcamListDecoder otherDec = new OtherWebcamListDecoder();
				wcData = otherDec.decode(text, saveOnCache);
				map.updateWebcamList(wcData);
				break;
			default:
				break;
			}
		}
	}
}
