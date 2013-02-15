package it.giacomos.android.osmer.widgets.mapview;

import it.giacomos.android.osmer.R;
import android.util.Log;
import android.view.View;

import com.google.android.maps.MapView;

public class BaloonOffMap {
	BaloonOffMap(MapView mapView)
	{
		MapBaloon baloon = (MapBaloon) mapView.findViewById(R.id.mapbaloon);
		if(baloon != null)
		{
			/* remove old baloon */
			mapView.removeView(baloon);
			/* This view is invisible, and it doesn't take any space for layout purposes. */
			baloon.setVisibility(View.GONE);
		}
	}

}
