package it.giacomos.android.osmer.guiHelpers;

import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.observations.ObservationTime;
import it.giacomos.android.osmer.observations.ObservationType;
import it.giacomos.android.osmer.widgets.mapview.MapViewMode;
import android.util.Log;
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
	public ObservationTypeGetter(OsmerActivity a, ObservationTime oTime, int currentSelection)
	{
		mObservationTime = oTime;
		mActivity = a;
		Resources res = a.getResources();
		final CharSequence[] items;
		if(oTime == ObservationTime.DAILY)
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
		if(mObservationTime == mObservationTime.DAILY)
			builder.setTitle(R.string.select_obs_type_daily);
		else
			builder.setTitle(R.string.select_obs_type_latest);

		builder.setSingleChoiceItems(items, currentSelection, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) 
			{
				ObservationType t = ObservationType.SKY;
				
				if(mObservationTime == ObservationTime.DAILY)
				{
					switch(item)
					{
					case 0:
						t = ObservationType.SKY;
						break;
					case 1:
						t = ObservationType.MIN_TEMP;
						break;
					case 2:
						t = ObservationType.MEAN_TEMP;
						break;
					case 3:
						t = ObservationType.MAX_TEMP;
						break;
					case 4:
						t = ObservationType.MEAN_HUMIDITY;
						break;
					case 5:
						t = ObservationType.MEAN_WIND;
						break;
					case 6:
						t = ObservationType.MAX_WIND;
						break;
					case 7:
						t = ObservationType.RAIN;
						break;
					default:
						break;
					}
				}
				else
				{
					switch(item)
					{
					case 0:
						t = ObservationType.SKY;
						break;
					case 1:
						t = ObservationType.TEMP;
						break;
					case 2:
						t = ObservationType.HUMIDITY;
						break;
					case 3:
						t = ObservationType.PRESSURE;
						break;
					case 4:
						t = ObservationType.WIND;
						break;
					case 5:
						t = ObservationType.RAIN;
						break;
					case 6:
						t = ObservationType.SEA;
						break;
					case 7:
						t = ObservationType.SNOW;
						break;
					default:
						break;
					}
				}
				mActivity.onSelectionDone(t, mObservationTime);
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private OsmerActivity mActivity;
	private ObservationTime mObservationTime;
}
