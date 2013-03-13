package it.giacomos.android.osmer.widgets.mapview;

import it.giacomos.android.osmer.observations.SkyDrawableIdPicker;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class SkyOverlayItemPicker {

	public OverlayItem get(GeoPoint gp, String location, String data, Resources resources) 
	{
		
		OverlayItem o = new OverlayItem(gp, location, "");
		SkyDrawableIdPicker skyDrawableIdPicker = new SkyDrawableIdPicker();
		int drawableId = skyDrawableIdPicker.get(data);
		if(drawableId > -1 )
		{
			Drawable dra = resources.getDrawable(drawableId);
			if(dra != null)
			{
				dra.setBounds(0, 0, dra.getIntrinsicWidth(), dra.getIntrinsicHeight());
				o.setMarker(dra);
			}
		}
		skyDrawableIdPicker = null;
		return o;
	}
	
}
