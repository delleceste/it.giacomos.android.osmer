package it.giacomos.android.osmer.personalMessageActivity;

import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.R.string;
import it.giacomos.android.osmer.service.ServiceManager;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class PersonalMessageManager 
{
	public PersonalMessageManager(OsmerActivity osmerActivity, PersonalMessageData data) 
	{
		/* stop service */
		ServiceManager serviceManager = new ServiceManager();
//		Log.e("PersonalMessageManager", "created: is service running: " 
//				+ serviceManager.isServiceRunning(osmerActivity));
		boolean ret = serviceManager.setEnabled(osmerActivity, false);
		if(ret)
			Toast.makeText(osmerActivity, R.string.service_stopped_app_blocked, Toast.LENGTH_LONG).show();
		
		Intent i = new Intent(osmerActivity, PersonalMessageActivity.class);
		i.putExtra("title", data.title);
		i.putExtra("message", data.message);
		i.putExtra("date", data.date);
		osmerActivity.startActivity(i);
		
		if(data.blocking)
			osmerActivity.finish();
	}
}
