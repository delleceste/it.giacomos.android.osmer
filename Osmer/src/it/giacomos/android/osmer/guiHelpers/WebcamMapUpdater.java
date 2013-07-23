package it.giacomos.android.osmer.guiHelpers;

import java.util.ArrayList;

import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.ViewType;
import it.giacomos.android.osmer.webcams.*;
import it.giacomos.android.osmer.widgets.map.*;

public class WebcamMapUpdater 
{
	public void update(OsmerActivity a, String text, ViewType t, boolean saveOnCache)
	{
		if(!text.isEmpty())
		{
			ArrayList<WebcamData> wcData = null;
			OMapFragment map = (OMapFragment) a.getFragmentManager().findFragmentById(R.id.mapview);
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
