package it.giacomos.android.osmer.pro.trial;

import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.pro.preferences.Settings;
import it.giacomos.android.osmer.pro.purhcase.InAppUpgradeManager;
import it.giacomos.android.osmer.pro.purhcase.InAppUpgradeManagerListener;
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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.provider.Settings.Secure;

public class BuyProActivity extends Activity implements 
OnClickListener, InAppUpgradeManagerListener, DialogInterface.OnClickListener
{
	private InAppUpgradeManager mInAppUpgradeManager;
	
	private final String AUTHOR_EMAIL = "delleceste@gmail.com";
	
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
		button = (Button) findViewById(R.id.btRequestAuth);
		button.setOnClickListener(this);
		
		new TrialExpiringNotification().remove(this);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		if(mInAppUpgradeManager != null)
			mInAppUpgradeManager.dispose();
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
			mInAppUpgradeManager = new InAppUpgradeManager();
			mInAppUpgradeManager.addInAppUpgradeManagerListener(this);
			mInAppUpgradeManager.purchase(this);
		}
		else if(v.getId() == R.id.btNoBuyThanks)
		{
			NavUtils.navigateUpFromSameTask(this);
		}
		else if(v.getId() == R.id.btRequestAuth)
		{
			String androidId =  Secure.getString(getContentResolver(), Secure.ANDROID_ID);
			final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			/* Fill it with Data */
			emailIntent.setType("plain/text");
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { AUTHOR_EMAIL } );
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Authorization request for Android Meteo.FVG by " + androidId );
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Hello.\nI kindly request the authorization " +
					"to activate Meteo.FVG for Android for free.\n" +
					"I have read and accepted all the conditions of use of the application.\n\n" +
					"My android id is as follows:\n\n" + 
					androidId +
					"\n\nThanks.\n\nMy full name: \n\n" + 
					"Date: " +
					"\n\nPlease note: requests devoid of the full name and date will be ignored.\n\n");
			
			/* Send it off to the Activity-Chooser */
			startActivity(emailIntent);
		}
	}

	@Override
	public void onPurchaseComplete(boolean ok, String error, boolean purchased) 
	{	
		int iconId = R.drawable.ic_launcher;
		int msgId, titleId;
		if(purchased && ok)
		{
			titleId = R.string.purchase_ok_title;
			msgId = R.string.thanks_for_purchasing;
		}
		else
		{
			iconId = R.drawable.ic_dialog_alert;
			titleId = R.string.purchase_error_title;
			msgId = R.string.purchase_error;
		}
		PurchaseDialogFragment f = PurchaseDialogFragment.newInstance(titleId, msgId, iconId);
		f.show(this.getFragmentManager(), "InfoDialog");
	}

	@Override
	public void onCheckComplete(boolean ok, String error, boolean bought) 
	{
		
	}

	@Override
	public void onInAppSetupComplete(boolean success, String message) 
	{
		
	}

	@Override
	public void onClick(DialogInterface di, int whichButton) 
	{
		NavUtils.navigateUpFromSameTask(this);
	}

}
