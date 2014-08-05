package it.giacomos.android.osmer.rainAlert;

import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class RainNotificationBuilder {

	public Notification build(Context ctx, double dbZ, int iconId, double latitude, double longitude)
	{
		String message;
		Intent resultIntent = new Intent(ctx, OsmerActivity.class);
		resultIntent.putExtra("ptLatitude", latitude);
		resultIntent.putExtra("ptLongitude", longitude);

		Log.e("RainNotificationBuilder", "setting extra NotificationRainAlert");
		resultIntent.putExtra("NotificationRainAlert", true);

		if(dbZ < 27)
		{
			message = ctx.getResources().getString(R.string.notificationRainAlert);
		}
		else if(dbZ < 42)
		{
			message = ctx.getResources().getString(R.string.notificationRainModerate);
		}
		else
		{
			message =ctx. getResources().getString(R.string.notificationRainIntense);
		}

		int notificationFlags = Notification.DEFAULT_SOUND|
				Notification.FLAG_SHOW_LIGHTS;
		NotificationCompat.Builder notificationBuilder =
				new NotificationCompat.Builder(ctx)
		.setSmallIcon(iconId)
		.setAutoCancel(true)
		.setLights(Color.RED, 1000, 1000)
		.setContentTitle(ctx.getResources().getString(R.string.app_name))
		.setContentText(message).setDefaults(notificationFlags);

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(OsmerActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);

		PendingIntent resultPendingIntent =
				stackBuilder.getPendingIntent( 0, PendingIntent.FLAG_UPDATE_CURRENT);

		notificationBuilder.setContentIntent(resultPendingIntent);
		notificationBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

		return notificationBuilder.build();

	}
}
