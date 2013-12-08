package it.giacomos.android.osmer.pro.widgets.map.report;

import android.support.v4.app.DialogFragment;

import com.google.android.gms.maps.model.LatLng;

import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.pro.OsmerActivity;
import it.giacomos.android.osmer.pro.locationUtils.LocationService;
import it.giacomos.android.osmer.pro.locationUtils.LocationServiceAddressUpdateListener;
import it.giacomos.android.osmer.pro.locationUtils.LocationServiceUpdateListener;
import it.giacomos.android.osmer.pro.preferences.Settings;
import android.app.AlertDialog;
import android.app.Dialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class ReportRequestDialogFragment extends DialogFragment 
{
	private View mDialogView;
	private String mLocality;
	private LatLng mLatLng;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{

		Log.e("ReportRequestDialogFragment", "onCreateDialog");
		this.setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		//		builder.setMessage(R.string.reportDialogMessage)
		//		.setTitle(R.string.reportDialogTitle);
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		mDialogView = inflater.inflate(R.layout.report_request_dialog, null);
		builder = builder.setView(mDialogView);

		/* Report! and Cangel buttons */
		builder = builder.setPositiveButton(R.string.reportDialogRequestSendButton, new ReportRequestDialogClickListener(this));

		/* negative button: save the user name */
		builder = builder.setNegativeButton(R.string.reportDialogCancelButton, new ReportRequestDialogClickListener(this));

		// Create the AlertDialog object and return it
		final AlertDialog alertDialog = builder.create();
		alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Light);
		/* populate Name field with last value */
		Settings s = new Settings(getActivity().getApplicationContext());
		String userName = s.getReporterUserName();

		EditText et = (EditText) mDialogView.findViewById(R.id.etRequestName);
		et.setText(userName);
		et.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence cs, int start, int before, int count) {}

			@Override
			public void afterTextChanged(Editable ed) {
				Log.e("TextWatcher.afterTextChanged", "afterTextChangeth");
				mCheckUsernameNotEmpty(alertDialog);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start,
					int count, int after) {}

		});

		setLocality(mLocality);

		if(userName.isEmpty())
			Toast.makeText(getActivity(), R.string.reportMustInsertUserName, Toast.LENGTH_LONG).show();

		mCheckUsernameNotEmpty(alertDialog);
		return alertDialog;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		Log.e("ReportRequestDialogFragment", "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
		/* register for locality name updates and location updates */
	}

	public void setData(LatLng pointOnMap, String locality)
	{
		Log.e("setData", "called set data with " + locality);
		mLatLng = pointOnMap;
		/* the name obtained by geocode address task in ReportOverlay. May be "-" */
		mLocality = locality;
	}

	public void setLocality(String locality) 
	{
		if(mDialogView != null)
		{
			TextView textView = (TextView) mDialogView.findViewById(R.id.tvDialogRequestTitle);
			textView.setText(mDialogView.getContext().getString(R.string.reportDialogRequestTitle) + " " + locality);
			textView = (TextView) mDialogView.findViewById(R.id.tvRequestLocationName);
			if(!locality.isEmpty())
				textView.setText(locality);
			else
				textView.setText(this.getResources().getString(R.string.reportDialogRequestLocationUnavailable));
		}
	}

	private void mCheckUsernameNotEmpty(AlertDialog ad)
	{
		Button positiveButton = ad.getButton(Dialog.BUTTON_POSITIVE);
		if(positiveButton != null)
		{
			EditText et = (EditText) mDialogView.findViewById(R.id.etRequestName);
			positiveButton.setEnabled(et.getText().toString().length() > 0);
			Log.e("mCheckUsernameNotEmpty", "dialog button is NOT null! --> no scandalo");
		}
		else
			Log.e("mCheckUsernameNotEmpty", "dialog is null! scandalo");
	}

	public LatLng getLatLng()
	{
		return mLatLng;
	}

	public String getLocality()
	{
		return mLocality;
	}
}
