package it.giacomos.android.osmer.pro.trial;

import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.pro.preferences.Settings;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;

public class BuyProActivity extends Activity implements OnClickListener
{
	private final String PLAY_STORE_URI = "https://play.google.com/store/apps/details?id=it.giacomos.android.osmer.pro";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buy_pro);
		// Show the Up button in the action bar.
		setupActionBar();
		/* button listener */
		Button button = (Button) findViewById(R.id.btBuyNow);
		button.setOnClickListener(this);
		button = (Button) findViewById(R.id.btNoBuyThanks);
		button.setOnClickListener(this);
		
		new TrialExpiringNotification().remove(this);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		SharedPreferences sp = getSharedPreferences(Settings.PREFERENCES_NAME, Context.MODE_PRIVATE);
		int daysRemaining = sp.getInt("TRIAL_DAYS_LEFT", Settings.TRIAL_DAYS);
		
		TextView tv = (TextView) findViewById(R.id.tvBuyDaysLeft);
		String s;
		Resources r = getResources();
		if(daysRemaining > 0)
		{
			s = r.getString(R.string.trial_version) + ": " + daysRemaining + " " + 
			  r.getString(R.string.days_left);
		}
		else
			s = r.getString(R.string.trial_expired);
		
		tv.setText(s);
			
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.trial_expired, menu);
//		return true;
//	}
//
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) 
	{
		
		if(v.getId() == R.id.btBuyNow)
		{
			Intent i = new Intent(android.content.Intent.ACTION_VIEW);
			i.setData(Uri.parse(PLAY_STORE_URI));
			startActivity(i);
		}
		else if(v.getId() == R.id.btNoBuyThanks)
		{
			NavUtils.navigateUpFromSameTask(this);
		}
	}

}
