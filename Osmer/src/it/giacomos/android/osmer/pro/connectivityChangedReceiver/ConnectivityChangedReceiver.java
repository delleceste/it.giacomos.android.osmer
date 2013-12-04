package it.giacomos.android.osmer.pro.connectivityChangedReceiver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

import it.giacomos.android.osmer.pro.reportDataService.ReportDataService;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

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
		Log.e(">>>> ReportDataService <<<<<< ", "onHandleIntent" + cal.getTime().toLocaleString()
				+ "log in " + f.getAbsolutePath() + "/Meteo.FVG.Service.log" );
		PrintWriter out;
		
    	
    	
    	
    	if(netinfo != null && connMgr.getActiveNetworkInfo().isConnectedOrConnecting())
        {
        	Log.e(">>>>>>>>>>>> ConnectivityChangedReceiver", "+++++++++ net connecting");
        	cal.add(Calendar.SECOND, 5);
        	//registering our pending intent with alarmmanager
        	am.setInexactRepeating(AlarmManager.RTC, cal.getTimeInMillis(), 120000, myPendingIntent);
       
        	
        	/////////////////////////////////////// LOG TEST //////////////////////////////////////////////////
        	////////////////////////////////////////////////////////////////////////////////////////////////////
        	try {
    			out = new PrintWriter(new BufferedWriter(new FileWriter(f.getAbsolutePath() + "/Meteo.FVG.Service.log", true)));
    			out.append("+ ConnectivityChangedReceiver: network up on " + cal.getTime().toLocaleString() + "\n");
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
        	Log.e(">>>>>>>>>>>> ConnectivityChangedReceiver", "------------removing repeating intent");
        	
        	
        	/////////////////////////////////////// LOG TEST //////////////////////////////////////////////////
        	////////////////////////////////////////////////////////////////////////////////////////////////////
        	try {
    			out = new PrintWriter(new BufferedWriter(new FileWriter(f.getAbsolutePath() + "/Meteo.FVG.Service.log", true)));
    			out.append("- ConnectivityChangedReceiver: network down on " + cal.getTime().toLocaleString() + "\n");
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
