package it.giacomos.android.osmer.pro.widgets.map.report;

import it.giacomos.android.osmer.PROva.R;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class IconTextSpinnerAdapter extends ArrayAdapter<String> {

	private Activity mActivity;
	
	
	static final int SPINNER_SKY = 0;
	static final int SPINNER_WIND = 1;
	
	/*
	<string name="sky0">clear</string>
	<string name="sky1">few clouds</string>
	<string name="sky2">variable</string>
	<string name="sky3">cloudy</string>
	<string name="sky4">covered</string>
	
	<string name="rain6">weak</string>
	<string name="rain7_abbrev">moder.</string>
	<string name="rain8_abbrev">abund.</string>
	<string name="rain9">intense</string>
	<string name="rain36">very intense</string>
	
	<string name="snow10">moderate</string>
	<string name="snow11">abundant</string>
	<string name="snow12">intense</string>
	
	<string name="storm">storm</string>
	
	<string name="mist14">fog</string>
	<string name="mist15">mist</string>
	
	*/
	
	private int arr_images[];

	public IconTextSpinnerAdapter(Context context, int resource,
			String[] strings, Activity activity) 
	{
		super(context, resource, strings);
		mActivity = (Activity) activity;
	}

	@Override
	public View getDropDownView(int position, View convertView,ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	public View getCustomView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = mActivity.getLayoutInflater();
		View row=inflater.inflate(R.layout.report_icon_text_spinner_row, parent, false);
		TextView label=(TextView)row.findViewById(R.id.text);
		label.setText(getItem(position));

		ImageView icon = (ImageView) row.findViewById(R.id.icon);
		icon.setImageResource(arr_images[position]);

		return row;
	}
	
	public void setType(int t)
	{
		if(t == SPINNER_SKY)
			arr_images =  new int[] { -1, /* index 0 */
				R.drawable.weather_sky_0, /* 1 */
				R.drawable.weather_few_clouds, /* 2 */
				R.drawable.weather_clouds,  /* 3 */
				R.drawable.weather_sky_3,  /* 4 */
				R.drawable.weather_sky_4, /* 5 */
				R.drawable.weather_rain_cloud_6, 
				R.drawable.weather_rain_cloud_7,
				R.drawable.weather_rain_cloud_8,
				R.drawable.weather_rain_cloud_9, 
				R.drawable.weather_rain_cloud_36, /* 10 */
				R.drawable.weather_snow_10, /* 11 */
				R.drawable.weather_snow_11,  /* 12 */
				R.drawable.weather_snow_12,  /* 13 */
				R.drawable.weather_storm_13, /* 14 */
				R.drawable.weather_mist_14,  /* 15 */
				R.drawable.weather_mist_15  /* 16 */
				};
		else
			arr_images = new int[] {  -1, 
				R.drawable.weather_wind_calm,
				R.drawable.weather_wind_35, R.drawable.weather_wind_17,
				R.drawable.weather_wind_26, R.drawable.weather_wind2_red_34 
		};
	}
}
