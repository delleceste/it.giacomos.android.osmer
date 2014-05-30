package it.giacomos.android.osmer.preferences;

import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.service.ServiceManager;
import android.app.Activity;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;

public class SettingsFragment extends PreferenceFragment  implements OnPreferenceChangeListener 
{

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.addPreferencesFromResource(R.xml.preferences);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		init(getActivity());
	}

	/** Initialize summaries according to the stored values */
	public void init(Activity a)
	{
		String svalue;
		findPreference("NOTIFICATION_SERVICE_ENABLED").setOnPreferenceChangeListener(this);
		findPreference("RAIN_NOTIFICATION_ENABLED").setOnPreferenceChangeListener(this);

		/* initialize edit text fields */
		EditTextPreference tep = (EditTextPreference )findPreference("SERVICE_SLEEP_INTERVAL_MINS");
		svalue = tep.getSharedPreferences().getString("SERVICE_SLEEP_INTERVAL_MINS", "5");
		String s = getResources().getString(R.string.pref_service_sleep_interval_summary_checks_every);
		s += " " + svalue + " " + getResources().getString(R.string.minutes);
		tep.setSummary(s);
		tep.setOnPreferenceChangeListener(this);

		tep = (EditTextPreference) findPreference("MIN_TIME_BETWEEN_NOTIFICATIONS_RainNotificationTag");
		svalue = tep.getSharedPreferences().getString("MIN_TIME_BETWEEN_NOTIFICATIONS_RainNotificationTag", "5");
		s = getResources().getString(R.string.pref_service_sleep_interval_summary_checks_every);
		s += " " + svalue + " " + getResources().getString(R.string.minutes);
		tep.setSummary(s);
		tep.setOnPreferenceChangeListener(this);
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) 
	{
		int interval = -1;
		if(preference.getKey().equalsIgnoreCase("NOTIFICATION_SERVICE_ENABLED"))
		{
			boolean checked = newValue.equals(true);
			Log.e("onPreferenceChange", "starting service: " + checked);
			return mStartNotificationService(checked);
		}
		else if(preference.getKey().equalsIgnoreCase("SERVICE_SLEEP_INTERVAL_MINS"))
		{
			interval = 5;
			try
			{
				interval = Integer.parseInt(newValue.toString());
				if(interval >= 1 && interval <= 180)
				{
					String s = getResources().getString(R.string.pref_service_sleep_interval_summary_checks_every);
					s += " " + newValue.toString() + " " + getResources().getString(R.string.minutes);
					preference.setSummary(s);
					/* restart service, if running */
					ServiceManager serviceManager = new ServiceManager();
					Log.e("onPreferenceChange", " service running " + serviceManager.isServiceRunning(getActivity()));
					if(serviceManager.isServiceRunning(getActivity()))
					{
						Log.e("onPreferenceChange", " service running " + serviceManager.isServiceRunning(getActivity()));
						serviceManager.setEnabled(getActivity(), false);
						return mStartNotificationService(true);
					}
				}
			}
			catch(NumberFormatException e)
			{
				
			}

		}
		else if(preference.getKey().equalsIgnoreCase("MIN_TIME_BETWEEN_NOTIFICATIONS_RainNotificationTag"))
		{
			interval = 5;
			try
			{
				interval = Integer.parseInt(newValue.toString());
				if(interval >= 1 && interval <= 180)
				{
					String s = getResources().getString(R.string.pref_rain_alert_summary_checks_every);
					s += " " + newValue.toString() + " " + getResources().getString(R.string.minutes);
					preference.setSummary(s);
					return true; /* ok */
				}
			}
			catch(NumberFormatException e)
			{
				
			}
		}
		else if(preference.getKey().equalsIgnoreCase("RAIN_NOTIFICATION_ENABLED"))
			return true;
		
		if(interval > 0) /* the user has edited an interval */
			Toast.makeText(getActivity(), R.string.notificationIntervalBetween0And180, Toast.LENGTH_LONG).show();
		
		return false;
	}	

	private boolean mStartNotificationService(boolean startService) 
	{
		ServiceManager serviceManager = new ServiceManager();
		Log.e("SettingsActivity.mStartNotificationService", "enabling service: " + startService +
				" was running "+ serviceManager.isServiceRunning(getActivity()));

		boolean ret = serviceManager.setEnabled(getActivity(), startService);
		if(ret && startService)
			Toast.makeText(getActivity(), R.string.notificationServiceStarted, Toast.LENGTH_LONG).show();
		else if(ret && !startService)
			Toast.makeText(getActivity(), R.string.notificationServiceStopped, Toast.LENGTH_LONG).show();
		else if(!ret && startService)
			Toast.makeText(getActivity(), R.string.notificationServiceWillStartOnNetworkAvailable, Toast.LENGTH_LONG).show();

		return (startService && ret || !startService);
	}

}