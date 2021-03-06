package it.giacomos.android.osmer.widgets.map.report;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.provider.Settings.Secure;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.gcm.GcmRegistrationManager;
import it.giacomos.android.osmer.network.state.Urls;
import it.giacomos.android.osmer.preferences.Settings;
import it.giacomos.android.osmer.widgets.map.report.network.PostReportRequestTask;
import it.giacomos.android.osmer.widgets.map.report.network.PostType;

public class ReportRequestDialogClickListener implements OnClickListener {

	private ReportRequestDialogFragment mReportRequestDialogFragment;

	public ReportRequestDialogClickListener(ReportRequestDialogFragment reportRequestDialogFragment) 
	{
		mReportRequestDialogFragment = reportRequestDialogFragment;
	}

	@Override
	public void onClick(DialogInterface dialogI, int whichButton) 
	{
		Dialog d = (Dialog) dialogI;
		OsmerActivity oActivity = (OsmerActivity) mReportRequestDialogFragment.getActivity();
		LatLng llng = mReportRequestDialogFragment.getLatLng();
		if(llng == null) /* should not be null since the dialog waits for location before enabling the ok button */
			return;
		
		if(whichButton == AlertDialog.BUTTON_POSITIVE)
		{
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
				//Log.e("ReportRequestDialogClickListener.onClick", " locality " + locality);
				PostReportRequestTask postReportRequestTask = new PostReportRequestTask(user, locality, llng.latitude, llng.longitude, oActivity);
				String deviceId = Secure.getString(mReportRequestDialogFragment.getActivity().getContentResolver(), Secure.ANDROID_ID);
				String registrationId = new GcmRegistrationManager().getRegistrationId(oActivity);
				postReportRequestTask.setDeviceId(deviceId);
				postReportRequestTask.setRegistrationId(registrationId);
				postReportRequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Urls().getPostReportRequestUrl());
			}
			else
			{
				oActivity.onPostActionResult(true, oActivity.getResources().getString(R.string.reportMustInsertUserName), PostType.REQUEST);
			}
		}
		else
		{
			EditText et = (EditText) d.findViewById(R.id.etRequestName);
			Settings s = new Settings(oActivity);
			s.setReporterUserName(et.getText().toString());
			oActivity.onMyReportRequestDialogCancelled(llng);
		}
	}

}
