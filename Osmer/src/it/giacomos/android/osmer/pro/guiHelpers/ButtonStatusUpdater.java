package it.giacomos.android.osmer.pro.guiHelpers;
import it.giacomos.android.osmer.pro.OsmerActivity;
import it.giacomos.android.osmer.pro.R;
import it.giacomos.android.osmer.pro.widgets.OViewFlipper;

public class ButtonStatusUpdater {
	public ButtonStatusUpdater(OsmerActivity a)
	{
		OViewFlipper vf = (OViewFlipper) a.findViewById(R.id.viewFlipper1);
		int displayedChild = vf.getDisplayedChild();
		
	}
	
}
