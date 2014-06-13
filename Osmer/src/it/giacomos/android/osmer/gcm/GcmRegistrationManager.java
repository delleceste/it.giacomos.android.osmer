package it.giacomos.android.osmer.gcm;

import java.io.IOException;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import it.giacomos.android.osmer.preferences.Settings;

public class GcmRegistrationManager 
{
	/* project number obtained from
	 * https://console.developers.google.com/project/871762795415
	 */
	private String SENDER_ID = "871762795415";
	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	public String getRegistrationId(Context context)
	{
		Settings s = new Settings(context);
	    String registrationId = s.getGcmRegistrationId();
	    if (registrationId.isEmpty()) {
	        Log.e("GcmRegistrationManager.getRegistrationId", "Registration not found.");
	        return "";
	    }
	    // Check if app was updated; if so, it must clear the registration ID
	    // since the existing regID is not guaranteed to work with the new
	    // app version.
	    int registeredVersion = s.getLastGCMRegisteredAppVersionId();
	    int currentVersion = 0;
		try {
			currentVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    if (registeredVersion != currentVersion) {
	    	Log.e("GcmRegistrationManager.getRegistrationId",  "App version changed.");
	        return "";
	    }
	    return registrationId;
	}
	
	public void saveRegistrationId(Context context, String regId)
	{
		Settings s = new Settings(context);
	    s.saveRegistrationId(regId, context);
	}
	
	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */
	public void registerInBackground(Context ctx) 
	{
		final Context context = ctx;
		
	    new AsyncTask<Void, Void, String>() 
	    {
	        @Override
	        protected String doInBackground(Void... params) {
	            String regId = "";
	            try {
	                GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
	                regId = gcm.register(SENDER_ID);

	            } catch (IOException ex) {
	                regId = "";
	                // If there is an error, don't just keep trying to register.
	                // Require the user to click a button again, or perform
	                // exponential back-off.
	                /* NOTE the registration is attempted inside init(), called upon onCreate in
	                 * OsmerActivity.
	                 */
	            }
	            return regId;
	        }

	        @Override
	        protected void onPostExecute(String regId) 
	        {
	        	Settings s = new Settings(context);
	    	    s.saveRegistrationId(regId, context);
	    	    Toast.makeText(context, "Registered on GCM: id " + regId, Toast.LENGTH_LONG).show();
	        }

			
	    }.execute(null, null, null);
	    
	}
}
