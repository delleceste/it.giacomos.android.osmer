package it.giacomos.android.osmer.pro.bgService;

import java.util.Calendar;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class BGService extends IntentService {

	public BGService() {
		super("BGService");
		// TODO Auto-generated constructor stub
	}

	@Override
	public IBinder onBind(Intent arg0) 
	{
		
		return null;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		Calendar cal = Calendar.getInstance();
		
		Log.e(">>>>>>>>>>>>>>>>>> BGService <<<<<<<<<<<<<<<<<<<<< ", 
				"============== onHandleIntent ================ "+ cal.getTime().toLocaleString());
	}

}
