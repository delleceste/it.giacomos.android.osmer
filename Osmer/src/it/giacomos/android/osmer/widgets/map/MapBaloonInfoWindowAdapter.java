package it.giacomos.android.osmer.widgets.map;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import it.giacomos.android.osmer.R;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

// import android.util.Log;

public class MapBaloonInfoWindowAdapter implements GoogleMap.InfoWindowAdapter 
{

	public enum Type { OBSERVATIONS, WEBCAM }

	private View mView;
	
	public MapBaloonInfoWindowAdapter(Activity activity) 
	{
		mView = activity.getLayoutInflater().inflate(R.layout.mapbaloon, null);
		mLatLng = null;
		mType = Type.OBSERVATIONS;
	}
	
	@Override
	public View getInfoContents(Marker marker) 
	{
		setText(marker.getSnippet());
		setTitle(marker.getTitle());
		Log.e("MapBaloonInfoWindowAdapter", "showingup");
		return mView;
	}

	@Override
	public View getInfoWindow(Marker arg0) 
	{
		return null;
	}
	
	public Type getType()
	{
		return mType;
	}
	
	public void setType(Type t)
	{
		mType = t;
	}

	public void setTitle(String t)
	{
		TextView tv = (TextView) mView.findViewById(R.id.baloon_title);
		if(tv != null)
		{
			tv.setText(t);
		}
	}
	
	public void setText(String t)
	{
		TextView tv = (TextView) mView.findViewById(R.id.baloon_text);
		if(tv != null)
		{
			tv.setMinLines(t.split("\n").length);
			tv.setMaxLines(t.split("\n").length);
			tv.setText(t);
		}
	}
	
	public void setIcon(Drawable dra)
	{
		ImageView iv = (ImageView) mView.findViewById(R.id.baloon_icon);
		iv.setScaleType(ScaleType.CENTER_CROP);
		iv.setBackgroundDrawable(dra);
	}
	
//	public void setIcon(int id)
//	{
//		ImageView iv = (ImageView) mView.findViewById(R.id.baloon_icon);
//		if(id > -1)
//			iv.setBackgroundDrawable(mResources.getDrawable(id));
//		else
//		{
//			/* put an icon to show on the baloon otherwise the layout is scrambled
//			 * Also, this indicates the lack of data in that time.
//		 	*/
//
//			iv.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.weather_none_available_map));
//		}
//	}
	
	private Type mType;
	
	private LatLng mLatLng;


	
}
