package it.giacomos.android.osmer;

import it.giacomos.android.osmer.personalMessageActivity.PersonalMessageActivity;
import it.giacomos.android.osmer.personalMessageActivity.PersonalMessageData;
import it.giacomos.android.osmer.service.ServiceManager;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class ApplicationBlocker 
{
	public ApplicationBlocker(OsmerActivity osmerActivity, PersonalMessageData data) 
	{
		/* stop service */
		ServiceManager serviceManager = new ServiceManager();
		Log.e("OsmerActivity.onPersonalMessageUpdate", "app blocked: disabling service: " +
				" was running "+ serviceManager.isServiceRunning(osmerActivity));
		boolean ret = serviceManager.setEnabled(osmerActivity, false);
		if(ret)
			Toast.makeText(osmerActivity, R.string.service_stopped_app_blocked, Toast.LENGTH_LONG).show();
		
		Intent i = new Intent(osmerActivity, PersonalMessageActivity.class);
		i.putExtra("title", data.title);
		i.putExtra("message", data.message);
		i.putExtra("date", data.date);
		osmerActivity.startActivity(i);
		
		osmerActivity.finish();
	}
}
