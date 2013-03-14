package it.giacomos.android.osmer.guiHelpers;

import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.widgets.OViewFlipper;

public class OnTouchListenerInstaller {
	
	public void install(OsmerActivity a)
	{
		OViewFlipper flipper = (OViewFlipper) a.findViewById(R.id.viewFlipper1);
		a.findViewById(R.id.homeImageView).setOnTouchListener(flipper);
		a.findViewById(R.id.homeTextView).setOnTouchListener(flipper);
		a.findViewById(R.id.todayImageView).setOnTouchListener(flipper);
		a.findViewById(R.id.todayTextView).setOnTouchListener(flipper);	
		a.findViewById(R.id.tomorrowTextView).setOnTouchListener(flipper);
		a.findViewById(R.id.tomorrowImageView).setOnTouchListener(flipper);
		a.findViewById(R.id.twoDaysTextView).setOnTouchListener(flipper);
		a.findViewById(R.id.twoDaysImageView).setOnTouchListener(flipper);
	}
}
