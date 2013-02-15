//package it.giacomos.android.osmer.widgets.mapview;
//
//import android.content.Context;
//import android.graphics.Canvas;
//import android.util.Log;
//
//import com.google.android.maps.MapView;
//import com.google.android.maps.MyLocationOverlay;
//
//public class MyLocationOverlay extends MyLocationOverlay implements OOverlayInterface {
//
//	public MyLocationOverlay(Context context, MapView mapView) {
//		super(context, mapView);
//		// TODO Auto-generated constructor stub
//	}
//
//	public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when)
//	{
//		super.draw(canvas, mapView, shadow, when);
//		Log.e("OMyLocationOverlay", "draw rect " + canvas.getClipBounds().toShortString());
//		return false;
//	}
//	
//	@Override
//	public int type() {
//		// TODO Auto-generated method stub
//		return OverlayType.MYLOCATION;
//	}
//
//}
