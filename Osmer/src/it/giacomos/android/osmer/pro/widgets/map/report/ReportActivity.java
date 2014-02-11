package it.giacomos.android.osmer.pro.widgets.map.report;

import it.giacomos.android.osmer.PROva.R;
import it.giacomos.android.osmer.pro.Logger;
import it.giacomos.android.osmer.pro.OsmerActivity;
import it.giacomos.android.osmer.pro.observations.ObservationData;
import it.giacomos.android.osmer.pro.preferences.Settings;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

public class ReportActivity extends Activity implements OnClickListener
{
	private double mLatitude, mLongitude;
	private String mLocality;
	
	public ReportActivity()
	{
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_PROGRESS);
		this.setProgressBarVisibility(true);

		setContentView(R.layout.report_activity_layout);

		((Button)findViewById(R.id.bSend)).setOnClickListener(this);

		((Button)findViewById(R.id.bCancel)).setOnClickListener(this);

		String[] textItems = getResources().getStringArray(R.array.report_sky_textitems);
		IconTextSpinnerAdapter skySpinnerAdapter = 
				new IconTextSpinnerAdapter(this, 
						R.layout.report_icon_text_spinner_row, 
						textItems, this);
		skySpinnerAdapter.setType(IconTextSpinnerAdapter.SPINNER_SKY);
		Spinner spinner = (Spinner) findViewById(R.id.spinSky);
		spinner.setAdapter(skySpinnerAdapter);
		spinner.setSelection(1);

		textItems = getResources().getStringArray(R.array.report_wind_textitems);
		IconTextSpinnerAdapter windSpinnerAdapter = 
				new IconTextSpinnerAdapter(this, 
						R.layout.report_icon_text_spinner_row, 
						textItems, this);
		windSpinnerAdapter.setType(IconTextSpinnerAdapter.SPINNER_WIND);
		spinner = (Spinner) findViewById(R.id.spinWind);
		spinner.setAdapter(windSpinnerAdapter);
		spinner.setSelection(1);

		CheckBox cb = (CheckBox) findViewById(R.id.cbTemp);
		cb.setOnClickListener(this);
		cb = (CheckBox) findViewById(R.id.cbReportIncludeLocality);
		cb.setChecked(true);
		cb.setOnClickListener(this);
		findViewById(R.id.ettemp).setEnabled(false);

		/* populate Name field with last value */
		Settings s = new Settings(this);
		String userName = s.getReporterUserName();
		EditText et = (EditText) findViewById(R.id.etUserName);
		et.setText(userName);
		et.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence cs, int start, int before, int count) {}

			@Override
			public void afterTextChanged(Editable ed) {
				setEnabled(ed.length() > 0);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start,
					int count, int after) {}

		});
		setEnabled(!userName.isEmpty());

		Intent i = getIntent();
		initByLocation(i.getStringExtra("temp"), i.getIntExtra("sky", 0), i.getIntExtra("wind", 0));
		mLatitude = i.getDoubleExtra("latitude", 0);
		mLongitude = i.getDoubleExtra("longitude", 0);
		mLocality = i.getStringExtra("locality");
		TextView localityTv = (TextView) findViewById(R.id.tvLocality);
		if(mLocality != null)
			localityTv.setText(mLocality);
		else 
			localityTv.setText(R.string.locality_unavailable);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}

	@Override
	public void onClick(View view)
	{
		EditText etTemp = null, teUserName, etComment;
		teUserName = (EditText) findViewById(R.id.etUserName);

		if(view.getId() == R.id.cbTemp)
		{
			CheckBox check = (CheckBox) view;
			etTemp = (EditText) findViewById(R.id.ettemp);
			etTemp.setEnabled(check.isChecked());
		}
		else if(view.getId() == R.id.bSend || view.getId() == R.id.bCancel)
		{
			Settings se = new Settings(this);
			if(se.getReporterUserName().compareTo(teUserName.getText().toString()) != 0)
				se.setReporterUserName(teUserName.getText().toString());
		}
		else if(view.getId() == R.id.cbReportIncludeLocality)
		{
			CheckBox cb = (CheckBox) view;
			findViewById(R.id.tvLocality).setEnabled(cb.isChecked());
		}

		if(view.getId() == R.id.bSend)
		{

			String user, temp, comment = "";

			int sky = -1 , wind = -1;


			CheckBox cb = (CheckBox) findViewById(R.id.cbTemp);
			if(cb.isChecked()) /* pick temperature only if cb is checked */
			{
				etTemp = (EditText) findViewById(R.id.ettemp);
				temp = etTemp.getText().toString();
			}
			else
				temp = "";


			user = teUserName.getText().toString();
			etComment = (EditText) findViewById(R.id.etComment);
			comment = etComment.getText().toString();
			Spinner sp = (Spinner) findViewById(R.id.spinSky);
			sky = sp.getSelectedItemPosition();
			sp = (Spinner) findViewById(R.id.spinWind);
			wind = sp.getSelectedItemPosition();
			if(comment.isEmpty() && wind == 0 && sky == 0 && !cb.isChecked())
				Toast.makeText(this, R.string.reportAtMost1FieldFilled, Toast.LENGTH_LONG).show();
			else
			{
				
			}

			Intent intent = new Intent();
			intent.putExtra("comment", comment);
			intent.putExtra("user", user);
			intent.putExtra("sky", sky);
			intent.putExtra("wind", wind);
			intent.putExtra("temperature", temp);
			intent.putExtra("latitude", mLatitude);
			intent.putExtra("longitude", mLongitude);
			
			CheckBox cbIncludeLocationName = (CheckBox) findViewById(R.id.cbReportIncludeLocality);
			if(cbIncludeLocationName.isChecked() && mLocality != null)
				intent.putExtra("locality", mLocality);
			else
				intent.putExtra("locality", "-");
			
			setResult(Activity.RESULT_OK, intent);
			finish();
		}
		else if(view.getId() == R.id.bCancel)
		{
			setResult(Activity.RESULT_CANCELED, null);
			finish();
		}

	}

	private void setEnabled(boolean en)
	{
		findViewById(R.id.cbTemp).setEnabled(en);
		findViewById(R.id.spinWind).setEnabled(en);
		findViewById(R.id.spinSky).setEnabled(en);
		findViewById(R.id.bSend).setEnabled(en);
		if(!en)
			findViewById(R.id.etUserName).setBackgroundResource(R.drawable.background_with_border);
		else
			findViewById(R.id.etUserName).setBackgroundColor(Color.WHITE);
		if(!en)
			Toast.makeText(this, R.string.reportMustInsertUserName, Toast.LENGTH_LONG).show();

	}

	private void initByLocation(String temp, int sky, int wind) 
	{
		((Spinner)findViewById(R.id.spinSky)).setSelection(sky, true);
		((Spinner)findViewById(R.id.spinWind)).setSelection(wind, true);
		((EditText)findViewById(R.id.ettemp)).setText(temp);
	}
}
