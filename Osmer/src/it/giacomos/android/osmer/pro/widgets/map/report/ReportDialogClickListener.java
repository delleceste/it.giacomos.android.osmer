package it.giacomos.android.osmer.pro.widgets.map.report;

import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.pro.OsmerActivity;
import it.giacomos.android.osmer.pro.preferences.Settings;
import it.giacomos.android.osmer.pro.widgets.map.report.network.PostReport;
import android.app.Dialog;
import android.content.DialogInterface;
import android.location.Location;
import android.util.Log;
import android.widget.EditText;
import android.widget.Spinner;

public class ReportDialogClickListener implements DialogInterface.OnClickListener {

	private ReportDialogFragment mDialogFragment;
	public ReportDialogClickListener(ReportDialogFragment rdf)
	{
		mDialogFragment = rdf;
	}

	@Override
	public void onClick(DialogInterface dialogI, int id) 
	{
		Location loc = ((OsmerActivity)mDialogFragment.getActivity()).getLocationService().getCurrentLocation();
		if(loc == null)
			return;
		String user, temp, comment = "";
		int sky = -1 , wind = -1;
		Dialog d = (Dialog) dialogI;
		EditText te = (EditText) d.findViewById(R.id.ettemp);
		temp = te.getText().toString();
		te = (EditText) d.findViewById(R.id.etUserName);
		/* save the reporter user name if changed */
		Settings se = new Settings(mDialogFragment.getActivity().getApplicationContext());
		if(se.getReporterUserName().compareTo(te.getText().toString()) != 0)
			se.setReporterUserName(te.getText().toString());
		
		user = te.getText().toString();
		te = (EditText) d.findViewById(R.id.etComment);
		comment = te.getText().toString();
		if(!user.isEmpty())
		{
			Spinner sp = (Spinner) d.findViewById(R.id.spinSky);
			sky = sp.getSelectedItemPosition();
			sp = (Spinner) d.findViewById(R.id.spinWind);
			wind = sp.getSelectedItemPosition();
			new PostReport(user, loc.getLatitude(), loc.getLongitude(), sky, wind, temp, comment, mDialogFragment.getActivity().getApplicationContext());
		}
		else
			Log.e("ReportDialogClickListener.onClick", "user name is empty");
	}

}
