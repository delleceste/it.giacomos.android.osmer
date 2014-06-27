package it.giacomos.android.osmer.gcm;

import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.service.sharedData.NotificationData;
import it.giacomos.android.osmer.service.sharedData.NotificationDataFactory;
import it.giacomos.android.osmer.service.sharedData.RainNotification;
import it.giacomos.android.osmer.service.sharedData.ReportRequestNotification;
import it.giacomos.android.osmer.service.sharedData.ServiceSharedData;

import java.util.ArrayList;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

public class GcmBroadcastReceiver extends BroadcastReceiver 
{
	@Override
	public void onReceive(Context ctx, Intent intent) 
	{
		
		Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(ctx);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) 
        {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) 
            {
                Log.e("GcmBroadcastReceiver.onReceive" , extras.toString());
            } 
            else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) 
            {
            	Log.e("GcmBroadcastReceiver.onReceive" , extras.toString());
            
            }
            else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) 
            {
            	boolean notified = false;
        		short requestsCount = 0;
        		String dataAsString = intent.getExtras().getString("message");
        		//	if(error)
        		Log.e("GcmBroadcastReceiver.onReceive", "data: \"" + dataAsString + "\"");

        		ServiceSharedData sharedData = ServiceSharedData.Instance(ctx);
        		NotificationManager mNotificationManager =
        				(NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        		ArrayList<NotificationData> notifications = new NotificationDataFactory().parse(dataAsString);
        		for(NotificationData notificationData : notifications)
        		{
        			/* Rain alert notifications are marked valid only if they represent an alert 
        			 * (there's a chance it's going to rain).
        			 */
        			if(notificationData.isValid() && notificationData.isRainAlert() && 
        					!((RainNotification) notificationData).IsGoingToRain())
        			{
        				Log.e("GcmBroadcastReceiver.onReceive", "rain alert notification to be cancelled");
        				RainNotification rainNotif = (RainNotification) notificationData;
        				mNotificationManager.cancel(rainNotif.getTag(), rainNotif.makeId());
        				Log.e("GcmBroadcastReceiver.onReceive", "RAIN notification setting notified " + notificationData.getTag() + ", " + notified);
        				sharedData.updateCurrentRequest(notificationData, notified);
        			}
        			else if(notificationData.isValid())
        			{
        				boolean alreadyNotifiedEqual = sharedData.alreadyNotifiedEqual(notificationData);
        				if(!alreadyNotifiedEqual && !sharedData.arrivesTooEarly(notificationData, ctx))
        				
        				{
        					Log.e("GcmBroadcastReceiver.onReceive", "notification can be considereth new " + notificationData.username);
        					/* and notify */
        					String message = "";
        					int iconId, ledColor;
        					// Creates an explicit intent for an Activity in your app
        					Intent resultIntent = new Intent(ctx, OsmerActivity.class);
        					resultIntent.putExtra("ptLatitude", notificationData.latitude);
        					resultIntent.putExtra("ptLongitude", notificationData.longitude);

        					if(notificationData.isRequest())
        					{
        						requestsCount++;
        						resultIntent.putExtra("NotificationReportRequest", true);
        						ReportRequestNotification rrnd = (ReportRequestNotification) notificationData;
        						message = ctx.getResources().getString(R.string.notificatonNewReportRequest) 
        								+ " " + notificationData.username;
        						if(rrnd.locality.length() > 0)
        							message += " - " + rrnd.locality;
        						iconId = R.drawable.ic_launcher_statusbar_request_new;
        						ledColor = Color.argb(255, 255, 255, 0); /* cyan notification */
        						//   Logger.log("RDS task ok.new req.notif " + notificationData.username);
        					}
        					else if(notificationData.isRainAlert())
        					{
        						RainNotification rainNotif = (RainNotification) notificationData;
        						iconId = R.drawable.ic_launcher_statusbar_rain;
        						ledColor = Color.argb(255, 0, 0, 0); /* red notification */
        						if(rainNotif.IsGoingToRain())
        						{
        							float dbZ = rainNotif.getLastDbZ();
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
        								message = ctx.getResources().getString(R.string.notificationRainIntense);
        							}
        						}
        					}
        					else
        					{
        						resultIntent.putExtra("NotificationReport", true);
        						message = ctx.getResources().getString(R.string.notificationNewReportArrived) 
        								+ " "  + notificationData.username;
        						iconId = R.drawable.ic_launcher_statusbar_report_new;
        						ledColor = Color.argb(0, 255, 0, 0);
        						//   Logger.log("RDS task ok.new req.notif " + notificationData.username);
        					}

        					//					int notificationFlags = Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS|
        					//							Notification.FLAG_SHOW_LIGHTS;
        					int notificationFlags = Notification.DEFAULT_SOUND|
        							Notification.FLAG_SHOW_LIGHTS;
        					NotificationCompat.Builder notificationBuilder =
        							new NotificationCompat.Builder(ctx)
        					.setSmallIcon(iconId)
        					.setAutoCancel(true)
        					.setTicker(message)
        					.setLights(0x0000ff00, 500, 500)
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
        					// mId allows you to update the notification later on.

        					int notifId = notificationData.makeId();
        					String notifTag = notificationData.getTag();
        					Notification notification = notificationBuilder.build();
        					notification.ledARGB = ledColor;
        					notification.ledOnMS = 800;
        					notification.ledOffMS = 2200;
        					/* remove previous similar notifications if present */
        					mNotificationManager.cancel(notifTag, notifId);
        					mNotificationManager.notify(notifTag, notifId,  notification);
        					notified = true;
        					/* update notification data */
        					Log.e("GcmBroadcastReceiver.onReceive", "notification setting notified " + notificationData.getTag() + ", " + notified);
        					sharedData.updateCurrentRequest(notificationData, notified);
        				}
        				else
        				{
        					//   Logger.log("RDS task ok. notif not new " + notificationData.username);
        					// log("task ok. notif not new " + notificationData.username);
        					Log.e("GcmBroadcastReceiver.onReceive", "notification IS NOT NEW " + notificationData.getType());
        				}
        			}
        			else
        			{
        				// log("service task: notification not valid: " + dataAsString);
        				Toast.makeText(ctx, "Notification not valid! " + 
        						dataAsString, Toast.LENGTH_LONG).show();
        			}
        		} /* for(NotificationData notificationData : notifications) */
        		
        		/* a request has been withdrawn, remove notification, if present */
        		if(requestsCount == 0)
        		{
        			/* remove notification, if present */
        			NotificationData currentNotification = sharedData.getNotificationData(NotificationData.TYPE_REQUEST);
        			if(currentNotification != null) /* a notification is present */
        			{
        				// Log.e("ReportDataService.onServiceDataTaskComplete", " removing notification with id " + currentNotification.makeId());
        				mNotificationManager.cancel(currentNotification.getTag(), currentNotification.makeId());

        				/* mark as consumed. The currentNotification is not removed from sharedData because sharedData
        				 * keeps it there in order not to bother us with possibly new notifications incoming in a near
        				 * future. currentNotification thus needs to be stored in order to be used by 
        				 * canBeConsideredNew() sharedData method.
        				 * On the other hand, the map view tests this variable in order to show or not a marker.
        				 */
        				currentNotification.setConsumed(true);
        			}
        		}
            }
        } /* !extras.isEmpty */

	}

}