package it.giacomos.android.osmer.interfaceHelpers;

import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.observations.MapMode;
import it.giacomos.android.osmer.observations.ObservationType;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;

/* NONE,
MIN_TEMP,
MAX_TEMP,
RAIN,
HUMIDITY,
WIND,
SAT,
RADAR;
 */
public class ObservationTypeGetter {
	public void get(OsmerActivity a, MapMode oTime, int currentSelection)
	{
		mMapMode = oTime;
		mActivity = a;
		Resources res = a.getResources();
		final CharSequence[] items;
		if(oTime == MapMode.DAILY_OBSERVATIONS)
		{
			/* 
			 * Riepilogo giornaliero
			 * STAZIONE ORA CIELO T_MIN T_MED T_MAX U_MED  V_MED  V_MAX PIOGGIA
			 */
			items = new CharSequence [] { 
					res.getString(R.string.sky),
					res.getString(R.string.min_temp), 
					res.getString(R.string.mean_temp), 
					res.getString(R.string.max_temp), 
					res.getString(R.string.mean_humidity),
					res.getString(R.string.mean_wind),
					res.getString(R.string.max_wind),
					res.getString(R.string.rain),
			};
		}
		else
		{
			/* istantanei */
			/*
			 * STAZIONE ORA CIELO TEMP. UMIDITA' PRESSIONE VENTO  PIOGGIA MARE NEVE
			 */
			items = new CharSequence [] { 
					res.getString(R.string.sky),
					res.getString(R.string.temp),
					res.getString(R.string.humidity),
					res.getString(R.string.pressure),
					res.getString(R.string.wind),
					res.getString(R.string.rain),
					res.getString(R.string.sea),
					res.getString(R.string.snow),

			};
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(a);
		if(mMapMode == MapMode.DAILY_OBSERVATIONS)
			builder.setTitle(R.string.select_obs_type_daily);
		else
			builder.setTitle(R.string.select_obs_type_latest);

		builder.setSingleChoiceItems(items, currentSelection, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) 
			{
				ObservationType t = ObservationType.SKY;
				
				if(mMapMode == MapMode.DAILY_OBSERVATIONS)
				{
					if (item == 0) {
						t = ObservationType.SKY;
					} else if (item == 1) {
						t = ObservationType.MIN_TEMP;
					} else if (item == 2) {
						t = ObservationType.AVERAGE_TEMP;
					} else if (item == 3) {
						t = ObservationType.MAX_TEMP;
					} else if (item == 4) {
						t = ObservationType.AVERAGE_HUMIDITY;
					} else if (item == 5) {
						t = ObservationType.AVERAGE_WIND;
					} else if (item == 6) {
						t = ObservationType.MAX_WIND;
					} else if (item == 7) {
						t = ObservationType.RAIN;
					} else {
					}
				}
				else if(mMapMode == MapMode.LATEST_OBSERVATIONS)
				{
					if (item == 0) {
						t = ObservationType.SKY;
					} else if (item == 1) {
						t = ObservationType.TEMP;
					} else if (item == 2) {
						t = ObservationType.HUMIDITY;
					} else if (item == 3) {
						t = ObservationType.PRESSURE;
					} else if (item == 4) {
						t = ObservationType.WIND;
					} else if (item == 5) {
						t = ObservationType.RAIN;
					} else if (item == 6) {
						t = ObservationType.SEA;
					} else if (item == 7) {
						t = ObservationType.SNOW;
					} else {
					}
				}
				mActivity.onSelectionDone(t, mMapMode);
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private OsmerActivity mActivity;
	private MapMode mMapMode;
}
