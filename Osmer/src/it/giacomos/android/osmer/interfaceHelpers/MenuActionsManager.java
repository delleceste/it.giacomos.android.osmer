package it.giacomos.android.osmer.interfaceHelpers;

import it.giacomos.android.osmer.locationUtils.GeocodeAddressUpdateListener;
import it.giacomos.android.osmer.locationUtils.LocationInfo;
import it.giacomos.android.osmer.preferences.SettingsActivity;
import it.giacomos.android.osmer.MyAlertDialogFragment;
import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;


public class MenuActionsManager implements GeocodeAddressUpdateListener
{
	public static final int TEXT_DIALOG = 10;
	
	private static final String AUTHOR_URL = "http://www.giacomos.it/curriculum/index.html";
	private static final String DOC_URL = "http://www.giacomos.it/android/meteofvg/index.html";
	
	public MenuActionsManager(OsmerActivity activity)
	{
		mActivity = activity;
	}
	
	public  boolean itemSelected(MenuItem item)
	{
		Resources res = mActivity.getApplicationContext().getResources();
//		Bundle data = null;
		int itemId = item.getItemId();

		if(itemId == R.id.menu_info)
		{
			//		data = new Bundle();
			String text = "<h6>" + res.getString(R.string.app_name);
			try
			{
				PackageInfo info = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0);
				text += ", " + res.getString(R.string.version) + ": " + info.versionName;

			} catch (NameNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			text += "</h6>\n" + res.getString(R.string.popup_app_info);

			//		data.putString("text", text);
			//		data.putString("title", res.getString(R.string.popup_info_title));
			MyAlertDialogFragment.MakeGenericInfo(Html.fromHtml(text).toString(), mActivity);
		}
			
		else if(itemId ==   R.id.menu_data_update_info)
		{
			//	data.putString("title", res.getString(R.string.popup_data_update_title));
			MyAlertDialogFragment.MakeGenericInfo(Html.fromHtml(mActivity.getString(R.string.popup_data_update_info)).toString(), mActivity);
		}
		else if(itemId ==    R.id.menu_online_guide)
		{
			Intent idoc = new Intent(Intent.ACTION_VIEW);
			idoc.setData(Uri.parse(DOC_URL));
			mActivity.startActivity(idoc);
		}
		else if(itemId ==    R.id.menu_author_info)
		{
			Intent iauthor = new Intent(Intent.ACTION_VIEW);
			iauthor.setData(Uri.parse(AUTHOR_URL));
			mActivity.startActivity(iauthor);
		}
		else if(itemId ==    R.id.menu_config_info)
		{
			ConfigInfo configInfo = new ConfigInfo();
			MyAlertDialogFragment.MakeGenericInfo(Html.fromHtml(configInfo.gatherInfo(mActivity)).toString(), mActivity);
			configInfo = null;
		}

		else if(itemId ==    R.id.menu_settings)
		{
			Intent prefsActivityIntent = new Intent(mActivity, SettingsActivity.class);
			mActivity.startActivityForResult(prefsActivityIntent, OsmerActivity.SETTINGS_ACTIVITY_FOR_RESULT_ID);	
		}
		return true;
	}

	@Override
	public void onGeocodeAddressUpdate(LocationInfo locationInfo, String id_unused_here) 
	{
		String message = "";
		Resources res = mActivity.getResources();
		message = "<h4>" + locationInfo.locality + "</h4>";
		
		if(locationInfo.subLocality != null)
			message += "<h5>" + locationInfo.subLocality + "</h5>\n";
		
		message +=	"<p>"
					+ locationInfo.address + "</p>\n"
					+ "<ul><li>"
					+ res.getString(R.string.pos_accuracy) + ": " + 
					locationInfo.accuracy + res.getString(R.string.meters) + "</li>\n<li> " + res.getString(R.string.from) +
					" " + locationInfo.provider + "</li></ul>";
		
		if(!locationInfo.error.isEmpty())
			message += "<h4>" + res.getString(R.string.locality_unavailable) + ":</h4> <p>" + 
		    res.getString(R.string.error_message) + ": <cite>" + 
					locationInfo.error + "</cite></p>";

		Bundle data = new Bundle();
		data.putString("title", res.getString(R.string.popup_position_title));
		data.putString("text", message);
		mActivity.showDialog(TEXT_DIALOG, data);
	}

	private OsmerActivity mActivity;
}
