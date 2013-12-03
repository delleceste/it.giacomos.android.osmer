package it.giacomos.android.osmer.pro.bootReceiver;

import java.util.Calendar;

import it.giacomos.android.osmer.pro.bgService.BGService;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver
{
	@Override
    public void onReceive(Context context, Intent intent) {  
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) 
        {
        	Log.e(">>>>>>>>>>>> BootReceiver", "entering onReceive");
        	Intent ii = new Intent(context, BGService.class);
        	PendingIntent pii = PendingIntent.getService(context, 2222, ii, PendingIntent.FLAG_CANCEL_CURRENT);  
        	Calendar cal = Calendar.getInstance();
        	cal.add(Calendar.SECOND, 60);
        	//registering our pending intent with alarmmanager
        	AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        	am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 3000, pii);
        }
    }
}
