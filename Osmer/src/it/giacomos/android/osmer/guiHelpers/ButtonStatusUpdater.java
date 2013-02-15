package it.giacomos.android.osmer.guiHelpers;
import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.widgets.OViewFlipper;

public class ButtonStatusUpdater {
	public ButtonStatusUpdater(OsmerActivity a)
	{
		OViewFlipper vf = (OViewFlipper) a.findViewById(R.id.viewFlipper1);
		int displayedChild = vf.getDisplayedChild();
		
	}
	
}
