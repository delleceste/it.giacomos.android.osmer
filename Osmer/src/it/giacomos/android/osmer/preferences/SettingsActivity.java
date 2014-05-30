package it.giacomos.android.osmer.preferences;

import android.app.Activity;
import android.os.Bundle;

public class SettingsActivity extends Activity{
		
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		SettingsFragment settingsFragment = new SettingsFragment();
		getFragmentManager().beginTransaction().replace(android.R.id.content, settingsFragment).commit();
	}

}
