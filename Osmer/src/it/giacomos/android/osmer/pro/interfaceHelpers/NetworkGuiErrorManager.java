package it.giacomos.android.osmer.pro.interfaceHelpers;

import it.giacomos.android.osmer.pro.OsmerActivity;
import it.giacomos.android.osmer.PROva.R;
import it.giacomos.android.osmer.pro.widgets.AnimatedImageView;

import java.lang.String;
import android.widget.Toast;

public class NetworkGuiErrorManager {
	public void onError(OsmerActivity a, String message)
	{
		Toast.makeText(a.getApplicationContext(), a.getResources().getString(R.string.netErrorToast) + "\n" + message, Toast.LENGTH_LONG).show();
		/* error may arrive before onPrepareOptionsMenu and so check for null */
		AnimatedImageView raiv = a.getRefreshAnimatedImageView();
		if(raiv != null)
			raiv.displayError();
	}

}
