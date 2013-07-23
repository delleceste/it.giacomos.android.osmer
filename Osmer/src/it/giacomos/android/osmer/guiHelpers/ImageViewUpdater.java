package it.giacomos.android.osmer.guiHelpers;

import android.graphics.Bitmap;
import it.giacomos.android.osmer.BitmapType;
import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.observations.ObservationTime;
import it.giacomos.android.osmer.observations.ObservationType;
import it.giacomos.android.osmer.widgets.ODoubleLayerImageView;
import it.giacomos.android.osmer.widgets.map.MapViewMode;
import it.giacomos.android.osmer.widgets.map.OMapFragment;

public class ImageViewUpdater {

	public void update(OsmerActivity a, Bitmap bmp, BitmapType t)
	{
		ODoubleLayerImageView ov = null;
		if(t == BitmapType.TODAY)
		{
			ov = (ODoubleLayerImageView) a.findViewById(R.id.todayImageView);
			ov.setBitmap(bmp);
		}
		else if(t == BitmapType.TOMORROW)
		{
			ov = (ODoubleLayerImageView) a.findViewById(R.id.tomorrowImageView);
			ov.setBitmap(bmp);
		}
		else if(t == BitmapType.TWODAYS)
		{
			ov = (ODoubleLayerImageView) a.findViewById(R.id.twoDaysImageView);
			ov.setBitmap(bmp);
		}
		else if(t == BitmapType.RADAR)
		{
			OMapFragment mapView = (OMapFragment) a.getFragmentManager().findFragmentById(R.id.mapview);
			/* update bitmap */
			mapView.setRadarImage(bmp);
		}
	}
}
