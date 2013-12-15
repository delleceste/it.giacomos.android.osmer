package it.giacomos.android.osmer.pro.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class ConnectivityChangedReceiver extends BroadcastReceiver
{
	@Override
    public void onReceive(Context context, Intent intent) 
	{  
				
		final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(context, ReportDataService.class);
    	PendingIntent myPendingIntent = PendingIntent.getService(context, 0, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);  
    	NetworkInfo netinfo = connMgr.getActiveNetworkInfo();
    	
    	Calendar cal = Calendar.getInstance();
		File f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		
    	if(netinfo != null && connMgr.getActiveNetworkInfo().isConnectedOrConnecting())
        {
        	Log.e(">>>>>>>>>>>> ConnectivityChangedReceiver", "+++++++++ net connecting");
        	cal.add(Calendar.SECOND, 5);
        	//registering our pending intent with alarmmanager
        	am.set(AlarmManager.RTC, cal.getTimeInMillis(), myPendingIntent);       
        	
        	/////////////////////////////////////// LOG TEST //////////////////////////////////////////////////
        	////////////////////////////////////////////////////////////////////////////////////////////////////
        	try {
        		PrintWriter out;
    			out = new PrintWriter(new BufferedWriter(new FileWriter(f.getAbsolutePath() + "/Meteo.FVG.Service.log", true)));
    			out.append("+ receiver: net up on " + cal.getTime().toLocaleString() + "\n");
    			out.close();
    		} catch (FileNotFoundException e1) 
    		{
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		} 
    		catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        	////////////////////////////////////////////////////////////////////////////////////////////////////
        	
        }
        else
        {
        	am.cancel(myPendingIntent);
        	context.stopService(myIntent);
        	Log.e(">>>>>>>>>>>> ConnectivityChangedReceiver", "------------removing repeating intent");
//        	Toast.makeText(context, "Network down. Meteo.FVG service stopped", Toast.LENGTH_SHORT).show();
        	
        	
        	/////////////////////////////////////// LOG TEST //////////////////////////////////////////////////
        	////////////////////////////////////////////////////////////////////////////////////////////////////
        	try {
        		PrintWriter out;
    			out = new PrintWriter(new BufferedWriter(new FileWriter(f.getAbsolutePath() + "/Meteo.FVG.Service.log", true)));
    			out.append("- receiver: net down on " + cal.getTime().toLocaleString() + "\n");
    			out.close();
    		} catch (FileNotFoundException e1) 
    		{
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		} 
    		catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        	////////////////////////////////////////////////////////////////////////////////////////////////////
        }
    }
}
