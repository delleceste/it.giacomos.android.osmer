package it.giacomos.android.meteofvg2.guiHelpers;
import it.giacomos.android.meteofvg2.OsmerActivity;
import it.giacomos.android.meteofvg2.R;
import it.giacomos.android.meteofvg2.widgets.OViewFlipper;

public class ButtonStatusUpdater {
	public ButtonStatusUpdater(OsmerActivity a)
	{
		OViewFlipper vf = (OViewFlipper) a.findViewById(R.id.viewFlipper1);
		int displayedChild = vf.getDisplayedChild();
		
	}
	
}
