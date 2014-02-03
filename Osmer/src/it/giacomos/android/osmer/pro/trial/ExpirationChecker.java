package it.giacomos.android.osmer.pro.trial;


import it.giacomos.android.osmer.pro.network.NetworkStatusMonitor;
import it.giacomos.android.osmer.pro.network.NetworkStatusMonitorListener;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.provider.Settings.Secure;
import android.util.Log;

public class ExpirationChecker implements NetworkStatusMonitorListener,
ExpirationCheckTaskListener
{
	public static final int TRIAL_DAYS = 30;
	
//	private final long DAY_MILLIS = 24 * 3600 * 1000;
	
	private final long DAY_MILLIS = 1 * 30 * 1000;
	
//	private final long DAY_MILLIS = 1 * 60 * 1000;
	
	
	private final String mUrl = "http://www.giacomos.it/meteo.fvg/checkexpiry.php";
	private NetworkStatusMonitor mNetworkStatusMonitor;
	private ExpirationCheckerListener mExpirationCheckerListener;
	private boolean mNetworkCheck, mMonitorRegistered;
	private ExpirationCheckTask mExpirationCheckTask;
	private String mDeviceId;
	private SharedPreferences mSharedPrefs;
	private int mDaysLeft;

	public ExpirationChecker(ExpirationCheckerListener expirationCheckerListener,
			Context ctx) 
	{
		Log.e("ExpirationChecker.ExpirationChecker", "registering listener");
		mExpirationCheckerListener = expirationCheckerListener;
		mNetworkCheck = false;
		mMonitorRegistered = false;
		/* get last time checked */
		mSharedPrefs = ctx.getSharedPreferences(ctx.getPackageName(), 
				Context.MODE_PRIVATE);
		mDaysLeft = mSharedPrefs.getInt("TRIAL_DAYS_LEFT", TRIAL_DAYS);
	}

	public void start(Context ctx)
	{
		long lastCheckedTimeMillis = mSharedPrefs.getLong("LAST_EXPIRATION_CHECKED_TIME_MILLIS",
				0);
		long currentTimeMillis = System.currentTimeMillis();

		/* check just once a day */
		mNetworkCheck = (currentTimeMillis - lastCheckedTimeMillis) > DAY_MILLIS;

		if(mNetworkCheck && !mMonitorRegistered)
		{
			mDeviceId = Secure.getString(ctx.getContentResolver(), Secure.ANDROID_ID);
			if(mNetworkStatusMonitor == null)
				mNetworkStatusMonitor = new NetworkStatusMonitor(this);
			Log.e("ExpirationChecker.start", "registering for network updates");
			ctx.registerReceiver(mNetworkStatusMonitor, 
					new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
			mMonitorRegistered = true;
		}
		else
			Log.e("ExpirationChecker.start", "not yet time to check for expiration");
	}

	public void stop(Context ctx)
	{
		mExpirationCheckerListener = null;
		if(mNetworkCheck && mNetworkStatusMonitor != null)
		{
			if(mExpirationCheckTask!= null && 
					mExpirationCheckTask.getStatus() != AsyncTask.Status.FINISHED)
				mExpirationCheckTask.cancel(false);
			
			if(mMonitorRegistered)
			{
				Log.e("ExpirationChecker.stop", "unregistering for network updates");
				ctx.unregisterReceiver(mNetworkStatusMonitor);
				mMonitorRegistered = false;
			}
		}
		else
			Log.e("ExpirationChecker.stop", "'twas not yet time to check for expiration");
	}

	public int getDaysLeft()
	{
		return mDaysLeft;
	}
	
	@Override
	public void onNetworkBecomesAvailable() 
	{
		Log.e("ExpirationChecker.onNetworkBecomesAvailable", "checking expiration time");
		mExpirationCheckTask = new ExpirationCheckTask(this, mDeviceId);
		mExpirationCheckTask.execute(mUrl);
	}

	@Override
	public void onNetworkBecomesUnavailable() 
	{
		
	}

	@Override
	/** invoked by AsyncTask's onPostExecute if successful and not
	 *  cancelled.
	 *  As long as we have network errors or malformed responses from the server,
	 *  leave the expiration days unchanged, and check later.
	 */
	public void onExpirationCheckTaskComplete(boolean success, String doc) 
	{
		if(success)
		{
			try{
				int daysLeft = Integer.parseInt(doc);
				SharedPreferences.Editor ed = mSharedPrefs.edit();
				/* update days left */
				ed.putInt("TRIAL_DAYS_LEFT", daysLeft);
				/* mark the last check has been done now */
				ed.putLong("LAST_EXPIRATION_CHECKED_TIME_MILLIS", System.currentTimeMillis());
				ed.commit();
				Log.e("ExpirationChecker.onExpirationCheckTaskComplete",
						"successfully checked. Remaining days: " + daysLeft);
				
				/* notify service */
				mDaysLeft = daysLeft;
				if(mExpirationCheckerListener != null)
					mExpirationCheckerListener.onTrialDaysRemaining(daysLeft);
			}
			catch(NumberFormatException e)
			{
				/* very embarassing */
			}
		}
		else
			Log.e("ExpirationChecker.onExpirationCheckTaskComplete",
					"error getting days left: " + doc);
	}
}
