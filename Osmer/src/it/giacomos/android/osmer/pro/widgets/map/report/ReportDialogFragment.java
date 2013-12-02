package it.giacomos.android.osmer.pro.widgets.map.report;

import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.pro.OsmerActivity;
import it.giacomos.android.osmer.pro.locationUtils.LocationService;
import it.giacomos.android.osmer.pro.locationUtils.LocationServiceAddressUpdateListener;
import it.giacomos.android.osmer.pro.preferences.Settings;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ReportDialogFragment extends DialogFragment 
implements OnClickListener, LocationServiceAddressUpdateListener
{
	private View mDialogView;
	private String mLocality;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{
		this.setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
		mLocality = "-";
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//		builder.setMessage(R.string.reportDialogMessage)
//		.setTitle(R.string.reportDialogTitle);
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		mDialogView = inflater.inflate(R.layout.report_dialog, null);
		builder.setView(mDialogView);

		/* Report! and Cangel buttons */
		builder.setPositiveButton(R.string.reportDialogSendButton, new ReportDialogClickListener(this));

		String[] textItems = getResources().getStringArray(R.array.report_sky_textitems);
		IconTextSpinnerAdapter skySpinnerAdapter = 
					new IconTextSpinnerAdapter(this.getActivity().getApplicationContext(), 
						R.layout.report_icon_text_spinner_row, 
						textItems, getActivity());
		skySpinnerAdapter.setType(IconTextSpinnerAdapter.SPINNER_SKY);
		Spinner spinner = (Spinner) mDialogView.findViewById(R.id.spinSky);
		spinner.setAdapter(skySpinnerAdapter);
		spinner.setSelection(1);
		
		textItems = getResources().getStringArray(R.array.report_wind_textitems);
		IconTextSpinnerAdapter windSpinnerAdapter = 
				new IconTextSpinnerAdapter(this.getActivity().getApplicationContext(), 
					R.layout.report_icon_text_spinner_row, 
					textItems, getActivity());
		windSpinnerAdapter.setType(IconTextSpinnerAdapter.SPINNER_WIND);
		spinner = (Spinner) mDialogView.findViewById(R.id.spinWind);
		spinner.setAdapter(windSpinnerAdapter);
		spinner.setSelection(1);
		
		CheckBox cb = (CheckBox) mDialogView.findViewById(R.id.cbTemp);
		cb.setOnClickListener(this);
		mDialogView.findViewById(R.id.ettemp).setEnabled(false);

		/* populate Name field with last value */
		Settings s = new Settings(getActivity().getApplicationContext());
		String userName = s.getReporterUserName();
		EditText et = (EditText) mDialogView.findViewById(R.id.etUserName);
		et.setText(userName);
		et.addTextChangedListener(new TextWatcher() {

	        	public void onTextChanged(CharSequence cs, int start, int before, int count) {}

				@Override
				public void afterTextChanged(Editable ed) {
					Log.e("TextWatcher.afterTextChanged", "afterTextChangeth");
					setEnabled(ed.length() > 0);
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {}

		});
		if(userName.isEmpty())
		{
			mDialogView.findViewById(R.id.cbTemp).setEnabled(false);
			mDialogView.findViewById(R.id.spinWind).setEnabled(false);
			mDialogView.findViewById(R.id.spinSky).setEnabled(false);
			Toast.makeText(getActivity(), R.string.reportMustInsertUserName, Toast.LENGTH_LONG).show();
		}
		/* negative button: save the user name */
		builder.setNegativeButton(R.string.reportDialogCancelButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogI, int id) {
                Dialog dialog = (Dialog) dialogI;
                EditText et = (EditText) dialog.findViewById(R.id.etUserName);
                Settings s = new Settings(getActivity());
                s.setReporterUserName(et.getText().toString());
            }
        });

		
		// Create the AlertDialog object and return it
		Dialog dialog = builder.create();
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Light);
		return dialog;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		/* register for locality name updates */
		LocationService locationService = ((OsmerActivity) getActivity()).getLocationService();
		locationService.registerLocationServiceAddressUpdateListener(this);
	}
	
	public String getLocality()
	{
		return mLocality;
	}
	
	@Override
	public void onDestroyView()
	{
		Log.e("ReportDialogFragment.onDestroyView", "removing location service address update listener");
		LocationService locationService = ((OsmerActivity) getActivity()).getLocationService();
		locationService.removeLocationServiceAddressUpdateListener(this);
		super.onDestroyView();
	}

	@Override
	public void onClick(View cb)
	{
		CheckBox check = (CheckBox) cb;
		View view = getDialog().findViewById(R.id.ettemp);
		view.setEnabled(check.isChecked());
	}

	private void setEnabled(boolean en)
	{
		getDialog().findViewById(R.id.cbTemp).setEnabled(en);
		getDialog().findViewById(R.id.spinWind).setEnabled(en);
		getDialog().findViewById(R.id.spinSky).setEnabled(en);
		if(!en)
			Toast.makeText(getActivity(), R.string.reportMustInsertUserName, Toast.LENGTH_LONG).show();
		
	}

	@Override
	public void onLocalityChanged(String locality, String subLocality,
			String address) 
	{
		TextView titleTV = (TextView) mDialogView.findViewById(R.id.tvTitle);
		titleTV.setText(mDialogView.getContext().getString(R.string.reportDialogTitle) + ": " + locality);
		mLocality = locality;
	}

}
