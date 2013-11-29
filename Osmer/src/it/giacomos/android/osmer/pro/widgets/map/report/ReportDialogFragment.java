package it.giacomos.android.osmer.pro.widgets.map.report;

import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.pro.preferences.Settings;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class ReportDialogFragment extends DialogFragment implements OnClickListener
{
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(R.string.reportDialogMessage)
		.setTitle(R.string.reportDialogTitle);
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		View dialogView = inflater.inflate(R.layout.report_dialog, null);
		builder.setView(dialogView);

		/* Report! and Cangel buttons */
		builder.setPositiveButton(R.string.reportDialogSendButton, new ReportDialogClickListener(this));

		String[] textItems = getResources().getStringArray(R.array.report_sky_textitems);
		IconTextSpinnerAdapter skySpinnerAdapter = 
					new IconTextSpinnerAdapter(this.getActivity().getApplicationContext(), 
						R.layout.report_icon_text_spinner_row, 
						textItems, getActivity());
		skySpinnerAdapter.setType(IconTextSpinnerAdapter.SPINNER_SKY);
		Spinner spinner = (Spinner) dialogView.findViewById(R.id.spinSky);
		spinner.setAdapter(skySpinnerAdapter);
		spinner.setSelection(1);
		
		textItems = getResources().getStringArray(R.array.report_wind_textitems);
		IconTextSpinnerAdapter windSpinnerAdapter = 
				new IconTextSpinnerAdapter(this.getActivity().getApplicationContext(), 
					R.layout.report_icon_text_spinner_row, 
					textItems, getActivity());
		windSpinnerAdapter.setType(IconTextSpinnerAdapter.SPINNER_WIND);
		spinner = (Spinner) dialogView.findViewById(R.id.spinWind);
		spinner.setAdapter(windSpinnerAdapter);
		spinner.setSelection(1);
		
		CheckBox cb = (CheckBox) dialogView.findViewById(R.id.cbTemp);
		cb.setOnClickListener(this);
		dialogView.findViewById(R.id.ettemp).setEnabled(false);

		/* populate Name field with last value */
		Settings s = new Settings(getActivity().getApplicationContext());
		String userName = s.getReporterUserName();
		EditText et = (EditText) dialogView.findViewById(R.id.etUserName);
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
			dialogView.findViewById(R.id.cbTemp).setEnabled(false);
			dialogView.findViewById(R.id.spinWind).setEnabled(false);
			dialogView.findViewById(R.id.spinSky).setEnabled(false);
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
		return builder.create();
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

}
