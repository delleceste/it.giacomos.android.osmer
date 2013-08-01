package it.giacomos.android.meteofvg2.guiHelpers;

import it.giacomos.android.meteofvg2.OsmerActivity;
import it.giacomos.android.meteofvg2.R;

import java.lang.String;

import android.util.Log;
import android.view.Window;
import android.widget.Toast;

public class NetworkGuiErrorManager {
	public void onError(OsmerActivity a, String message)
	{
		Toast.makeText(a.getApplicationContext(), R.string.netTextErrorToast + message, Toast.LENGTH_LONG).show();
		a.getRefreshAnimatedImageView().displayError();
	}

}
