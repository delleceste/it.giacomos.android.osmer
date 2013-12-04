package it.giacomos.android.osmer.pro.widgets.map.report;

import com.google.android.gms.maps.model.LatLng;

import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.pro.OsmerActivity;
import it.giacomos.android.osmer.pro.network.state.Urls;
import it.giacomos.android.osmer.pro.preferences.Settings;
import it.giacomos.android.osmer.pro.widgets.map.report.network.PostReport;
import it.giacomos.android.osmer.pro.widgets.map.report.network.PostReportRequestTask;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.location.Location;
import android.os.AsyncTask;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class ReportRequestDialogClickListener implements OnClickListener {

	private ReportRequestDialogFragment mReportRequestDialogFragment;
	
	public ReportRequestDialogClickListener(ReportRequestDialogFragment reportRequestDialogFragment) 
	{
		mReportRequestDialogFragment = reportRequestDialogFragment;
	}

	@Override
	public void onClick(DialogInterface dialogI, int arg1) 
	{
		Dialog d = (Dialog) dialogI;
		OsmerActivity oActivity = (OsmerActivity) mReportRequestDialogFragment.getActivity();
		Location loc = oActivity.getLocationService().getCurrentLocation();
		if(loc == null) /* should not be null since the dialog waits for location before enabling the ok button */
			return;
		String user;
		String locality = "";
		CheckBox cb = (CheckBox) d.findViewById(R.id.cbIncludeLocationName);
		EditText editText = (EditText) d.findViewById(R.id.etRequestName);
		if(cb.isChecked()) /* send locality */
			locality = mReportRequestDialogFragment.getLocality();
		user = editText.getText().toString();
		
		/* save the reporter user name if changed */
		Settings se = new Settings(mReportRequestDialogFragment.getActivity().getApplicationContext());
		if(se.getReporterUserName().compareTo(editText.getText().toString()) != 0)
			se.setReporterUserName(editText.getText().toString());
		
		if(!user.isEmpty())
		{
			PostReportRequestTask postReportRequestTask = new PostReportRequestTask(user, locality, loc.getLatitude(), loc.getLongitude(), oActivity);
			String deviceId = Secure.getString(mReportRequestDialogFragment.getActivity().getContentResolver(), Secure.ANDROID_ID);
			postReportRequestTask.setDeviceId(deviceId);
			postReportRequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Urls().getPostReportRequestUrl());
		}
		else
			Log.e("ReportDialogClickListener.onClick", "user name is empty");

	}

}
