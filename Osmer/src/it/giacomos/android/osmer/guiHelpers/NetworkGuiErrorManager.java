package it.giacomos.android.osmer.guiHelpers;

import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;

import java.lang.String;

import android.util.Log;
import android.view.Window;
import android.widget.Toast;

public class NetworkGuiErrorManager {
	public void onError(OsmerActivity a, String message)
	{
		Toast.makeText(a, R.string.netTextErrorToast + message, Toast.LENGTH_LONG).show();
		a.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.task_attention);
	}

}
