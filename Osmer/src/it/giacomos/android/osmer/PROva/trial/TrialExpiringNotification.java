package it.giacomos.android.osmer.PROva.trial;


import it.giacomos.android.osmer.PROva.R;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class TrialExpiringNotification 
{
	public static final int TRIAL_EXPIRING_NOTIFICATION_ID = 100100;
	
	public TrialExpiringNotification() 
	{
		
	}
	
	public void show(Context ctx, int days)
	{
		Resources res = ctx.getResources();
		String msg = "";
		int iconId = R.drawable.ic_notification_expired;

		msg = res.getString(R.string.trial_version_expiring) + ": " + 
				days + " " +
				res.getString(R.string.days_left) + "\n" +
				res.getString(R.string.touch_to_buy);

		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(ctx)
		.setSmallIcon(iconId)
		.setContentTitle(res.getString(R.string.app_name))
		.setContentText(msg);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(ctx, BuyProActivity.class);
		resultIntent.putExtra("daysLeft", days);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(BuyProActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent( 0,
				PendingIntent.FLAG_UPDATE_CURRENT );
		mBuilder.setContentIntent(resultPendingIntent);
		
		// mId allows you to update the notification later on.
		NotificationManager mNotificationManager =
				(NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(TRIAL_EXPIRING_NOTIFICATION_ID, mBuilder.build());
	}
	
	public void remove(Context ctx)
	{
		NotificationManager mNotificationManager =
				(NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(TRIAL_EXPIRING_NOTIFICATION_ID);
	}

}
