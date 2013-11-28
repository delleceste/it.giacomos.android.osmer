package it.giacomos.android.osmer.pro.interfaceHelpers;

import it.giacomos.android.osmer.pro.OsmerActivity;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.pro.locationUtils.GeocodeAddressTask;
import it.giacomos.android.osmer.pro.locationUtils.GeocodeAddressUpdateListener;
import it.giacomos.android.osmer.pro.locationUtils.LocationInfo;
import it.giacomos.android.osmer.pro.widgets.SituationImage;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.Toast;


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
		Bundle data = null;
		switch(item.getItemId())
		{
		case R.id.menu_info:
			data = new Bundle();
			String text = "<h6>" + res.getString(R.string.app_name);
			try {
				PackageInfo info = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0);
				text +=  ", " + res.getString(R.string.version) + ": " + info.versionName;
				
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			text += "</h6>\n" + res.getString(R.string.popup_app_info);
			
			data.putString("text", text);
			data.putString("title", res.getString(R.string.popup_info_title));
			mActivity.showDialog(TEXT_DIALOG, data);
			break;
			
		case R.id.menu_data_update_info:
			data = new Bundle();
			data.putString("text", res.getString(R.string.popup_data_update_info));
			data.putString("title", res.getString(R.string.popup_data_update_title));
			mActivity.showDialog(TEXT_DIALOG, data);
			break;
		case R.id.menu_online_guide:
			Intent idoc = new Intent(Intent.ACTION_VIEW);
			idoc.setData(Uri.parse(DOC_URL));
			mActivity.startActivity(idoc);
			break;
		case R.id.menu_author_info:
			Intent iauthor = new Intent(Intent.ACTION_VIEW);
			iauthor.setData(Uri.parse(AUTHOR_URL));
			mActivity.startActivity(iauthor);
			break;
			
		case R.id.menu_config_info:
			data = new Bundle();
			data.putString("title", res.getString(R.string.menu_config_info));
			ConfigInfo configInfo = new ConfigInfo();
			data.putString("text", configInfo.gatherInfo(mActivity));
			mActivity.showDialog(TEXT_DIALOG, data);
			configInfo = null;
			break;
		}
		return true;
	}

	@Override
	public void onGeocodeAddressUpdate(LocationInfo locationInfo) 
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
