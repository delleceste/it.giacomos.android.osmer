package it.giacomos.android.osmer.guiHelpers;

import android.widget.ToggleButton;
import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;

public class ButtonListenerInstaller 
{
	public ButtonListenerInstaller(OsmerActivity a, ToggleButtonGroupHelper mToggleButtonGroupHelper)
	{	

		ToggleButton buttonHome = (ToggleButton)a.findViewById(R.id.buttonHome);
		mToggleButtonGroupHelper.addButton(R.id.buttonHome);
		buttonHome.setOnClickListener(a);


		ToggleButton buttonToday = (ToggleButton)a.findViewById(R.id.buttonToday);
		mToggleButtonGroupHelper.addButton(R.id.buttonToday);
		buttonToday.setOnClickListener(a);


		ToggleButton buttonTomorrow = (ToggleButton)a.findViewById(R.id.buttonTomorrow);
		mToggleButtonGroupHelper.addButton(R.id.buttonTomorrow);
		buttonTomorrow.setOnClickListener(a);


		ToggleButton buttonTwoDays = (ToggleButton)a.findViewById(R.id.buttonTwoDays);
		mToggleButtonGroupHelper.addButton(R.id.buttonTwoDays);
		buttonTwoDays.setOnClickListener(a);


		ToggleButton buttonMap = (ToggleButton)a.findViewById(R.id.buttonMap);
		buttonMap.setOnClickListener(a);
		mToggleButtonGroupHelper.addButton(R.id.buttonMap);
		
		/* switches between map and satellite view on MapView.
		 * do not add to ToggleButtonGroupHelper
		 */
		ToggleButton satelliteViewButtonOnMap = (ToggleButton) a.findViewById(R.id.satelliteViewButton);
		satelliteViewButtonOnMap.setOnClickListener(a);

		ToggleButton measureMode = (ToggleButton) a.findViewById(R.id.measureToggleButton);
		measureMode.setOnClickListener(a);
		
		
		ToggleButton radarButton = (ToggleButton) a.findViewById(R.id.buttonRadar);
		radarButton.setOnClickListener(a);
		mToggleButtonGroupHelper.addButton(R.id.buttonRadar);
		ToggleButton dailyObsButton = (ToggleButton) a.findViewById(R.id.buttonDailyObs);
		dailyObsButton.setOnClickListener(a);
		mToggleButtonGroupHelper.addButton(R.id.buttonDailyObs);
		ToggleButton lastObsButton = (ToggleButton) a.findViewById(R.id.buttonLastObs);
		lastObsButton.setOnClickListener(a);
		mToggleButtonGroupHelper.addButton(R.id.buttonLastObs);

		/* daily and latest observations button */
		ToggleButton dailySkyButton = (ToggleButton) a.findViewById(R.id.buttonDailySky);
		dailySkyButton.setOnClickListener(a);
		mToggleButtonGroupHelper.addButton(R.id.buttonDailySky);

		ToggleButton buttonHumidity = (ToggleButton) a.findViewById(R.id.buttonHumidity);
		buttonHumidity.setOnClickListener(a);
		mToggleButtonGroupHelper.addButton(R.id.buttonHumidity);

		ToggleButton buttonHumidityMean = (ToggleButton) a.findViewById(R.id.buttonHumMean);
		buttonHumidityMean.setOnClickListener(a);
		mToggleButtonGroupHelper.addButton(R.id.buttonHumMean);

		ToggleButton buttonLatestSky = (ToggleButton) a.findViewById(R.id.buttonLatestSky);
		buttonLatestSky.setOnClickListener(a);
		mToggleButtonGroupHelper.addButton(R.id.buttonLatestSky);

		ToggleButton buttonPressure = (ToggleButton) a.findViewById(R.id.buttonPressure);
		buttonPressure.setOnClickListener(a);
		mToggleButtonGroupHelper.addButton(R.id.buttonPressure);

		ToggleButton buttonDailyRain = (ToggleButton) a.findViewById(R.id.buttonDailyRain);
		buttonDailyRain.setOnClickListener(a);
		mToggleButtonGroupHelper.addButton(R.id.buttonDailyRain);

		ToggleButton buttonSea = (ToggleButton) a.findViewById(R.id.buttonSea);
		buttonSea.setOnClickListener(a);
		mToggleButtonGroupHelper.addButton(R.id.buttonSea);

		ToggleButton buttonSnow = (ToggleButton) a.findViewById(R.id.buttonSnow);
		buttonSnow.setOnClickListener(a);
		mToggleButtonGroupHelper.addButton(R.id.buttonSnow);


		ToggleButton buttonTemp = (ToggleButton) a.findViewById(R.id.buttonTemp);
		buttonTemp.setOnClickListener(a);
		mToggleButtonGroupHelper.addButton(R.id.buttonTemp);


		ToggleButton buttonTMax = (ToggleButton) a.findViewById(R.id.buttonTMax);
		buttonTMax.setOnClickListener(a);
		mToggleButtonGroupHelper.addButton(R.id.buttonTMax);


		ToggleButton buttonTMean = (ToggleButton) a.findViewById(R.id.buttonTMean);
		buttonTMean.setOnClickListener(a);
		mToggleButtonGroupHelper.addButton(R.id.buttonTMean);


		ToggleButton buttonTMin = (ToggleButton) a.findViewById(R.id.buttonTMin);
		buttonTMin.setOnClickListener(a);
		mToggleButtonGroupHelper.addButton(R.id.buttonTMin);


		ToggleButton buttonWind = (ToggleButton) a.findViewById(R.id.buttonWind);
		buttonWind.setOnClickListener(a);
		mToggleButtonGroupHelper.addButton(R.id.buttonWind);


		ToggleButton buttonWMax = (ToggleButton) a.findViewById(R.id.buttonWMax);
		buttonWMax.setOnClickListener(a);
		mToggleButtonGroupHelper.addButton(R.id.buttonWMax);


		ToggleButton buttonWMean = (ToggleButton) a.findViewById(R.id.buttonWMean);
		buttonWMean.setOnClickListener(a);
		mToggleButtonGroupHelper.addButton(R.id.buttonWMean);


		ToggleButton buttonLatestRain = (ToggleButton) a.findViewById(R.id.buttonLatestRain);
		buttonLatestRain.setOnClickListener(a);
		mToggleButtonGroupHelper.addButton(R.id.buttonLatestRain);


		ToggleButton buttonMapInsideDaily = (ToggleButton) a.findViewById(R.id.buttonMapInsideDaily);
		buttonMapInsideDaily.setOnClickListener(a);
		mToggleButtonGroupHelper.addButton(R.id.buttonMapInsideDaily);


		ToggleButton buttonMapInsideLatest = (ToggleButton) a.findViewById(R.id.buttonMapInsideLatest);
		buttonMapInsideLatest.setOnClickListener(a);
		mToggleButtonGroupHelper.addButton(R.id.buttonMapInsideLatest);
	}
}



