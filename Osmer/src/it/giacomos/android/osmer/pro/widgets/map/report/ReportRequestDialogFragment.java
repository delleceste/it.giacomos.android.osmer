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
implements  LocationServiceAddressUpdateListener
{
	private View mDialogView;
	private String mLocality;
	private LatLng mLatLng;

	public void setLatLng(LatLng llng)
	{
		mLatLng = llng;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{

		Log.e("ReportRequestDialogFragment", "onCreateDialog");
		this.setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
		mLocality = "";
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

		TextView textView = (TextView) mDialogView.findViewById(R.id.tvDialogRequestTitle);
		textView.setText(mDialogView.getContext().getString(R.string.reportDialogRequestTitle) + "-");
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
				mCheckLocationLocalityAvailable();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start,
					int count, int after) {}

		});

		/* negative button: save the user name */
		builder = builder.setNegativeButton(R.string.reportDialogCancelButton, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogI, int id) {
				Dialog dialog = (Dialog) dialogI;
				EditText et = (EditText) dialog.findViewById(R.id.etRequestName);
				Settings s = new Settings(getActivity());
				s.setReporterUserName(et.getText().toString());
			}
		});


		// Create the AlertDialog object and return it
		Dialog dialog = builder.create();
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Light);
		if(userName.isEmpty())
			Toast.makeText(getActivity(), R.string.reportMustInsertUserName, Toast.LENGTH_LONG).show();

		return dialog;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		Log.e("ReportRequestDialogFragment", "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
		/* register for locality name updates and location updates */
		LocationService locationService = ((OsmerActivity) getActivity()).getLocationService();
		locationService.registerLocationServiceAddressUpdateListener(this);
	}

	@Override
	public void onDestroyView()
	{
		Log.e("ReportRequestDialogFragment.onDestroyView", "removing location service address update listener");
		LocationService locationService = ((OsmerActivity) getActivity()).getLocationService();
		locationService.removeLocationServiceAddressUpdateListener(this);
		super.onDestroyView();
	}

	@Override
	public void onLocalityChanged(String locality, String subLocality,
			String address) 
	{
		TextView textView = (TextView) mDialogView.findViewById(R.id.tvDialogRequestTitle);
		textView.setText(mDialogView.getContext().getString(R.string.reportDialogRequestTitle) + " " + locality);
		textView = (TextView) mDialogView.findViewById(R.id.tvRequestLocationName);
		textView.setText(locality);
		mLocality = locality;
		LocationService locationService = ((OsmerActivity) getActivity()).getLocationService();
		locationService.removeLocationServiceAddressUpdateListener(this);
		mCheckLocationLocalityAvailable();
	}

	private void mCheckLocationLocalityAvailable()
	{
		TextView tv = (TextView) mDialogView.findViewById(R.id.tvRequestLocationName);
		if(!mLocality.isEmpty() && mLatLng != null)
		{
			tv.setText(mLocality);
			EditText et = (EditText) mDialogView.findViewById(R.id.etRequestName);
			et.getText().toString();
			Log.e("len", "lungo " + et.getText().toString().length());
			Button positiveButton = ((AlertDialog)getDialog()).getButton(Dialog.BUTTON_POSITIVE);
			if(positiveButton != null)
			{
				positiveButton.setEnabled(et.getText().toString().length() > 0);
				Log.e("CheckLocationLocalityAvailable", "dialog button is NOT null! --> no scandalo");
			}
			else
				Log.e("mCheckLocationLocalityAvailable", "dialog is null! scandalo");
		}
		else
			tv.setText(this.getResources().getString(R.string.reportDialogRequestLocationUnavailable));
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

