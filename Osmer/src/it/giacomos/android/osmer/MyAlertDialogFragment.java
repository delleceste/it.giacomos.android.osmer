package it.giacomos.android.osmer;


import it.giacomos.android.osmer.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class MyAlertDialogFragment extends DialogFragment implements DialogInterface.OnClickListener
{

	public static final int OPTION_OPEN_GEOLOCALIZATION_SETTINGS = 1;
	public static final int OPTION_OPEN_NETWORK_SETTINGS = 2;
	public static final int OPTION_GRANT_LOCATION_PERMISSION = 3;
	
	private int mOption = 0;

	public static void MakeGenericError(int message, FragmentActivity a, int option)
	{
		MyAlertDialogFragment.newInstance(R.string.error_message, message, R.drawable.ic_dialog_alert, option)
		.show(a.getSupportFragmentManager(), "ErrorDialog");
	}

	public static void MakeGenericError(int message, FragmentActivity a)
	{
		MyAlertDialogFragment.newInstance(R.string.error_message, message, R.drawable.ic_dialog_alert)
		.show(a.getSupportFragmentManager(), "ErrorDialog");
	}

	public static void MakeGenericError(String message, FragmentActivity a)
	{
		MyAlertDialogFragment.newInstance(R.string.error_message, message, R.drawable.ic_dialog_alert)
		.show(a.getSupportFragmentManager(), "ErrorDialog");
	}

	public static void MakeGenericInfo(int message, FragmentActivity a)
	{
		MyAlertDialogFragment.newInstance(R.string.info, message, R.drawable.ic_dialog_info)
		.show(a.getSupportFragmentManager(), "InfoDialog");
	}

	public static void MakeGenericInfo(String message, FragmentActivity a)
	{
		MyAlertDialogFragment.newInstance(R.string.info, message, R.drawable.ic_dialog_info)
		.show(a.getSupportFragmentManager(), "InfoDialog");
	}
	
	public static void MakeGenericInfo(int message, FragmentActivity a, int option)
	{
		MyAlertDialogFragment.newInstance(R.string.info, message, R.drawable.ic_dialog_info, option)
		.show(a.getSupportFragmentManager(), "InfoDialog");
	}

	public static MyAlertDialogFragment newInstance(int title, int message, int icon, int option) {
		MyAlertDialogFragment frag = new MyAlertDialogFragment();
		Bundle args = new Bundle();
		args.putInt("title", title);
		args.putInt("message", message);
		args.putInt("icon", icon);
		args.putInt("option", option);
		frag.setArguments(args);
		return frag;
	}

	public static MyAlertDialogFragment newInstance(int title, int message, int icon) {
		MyAlertDialogFragment frag = new MyAlertDialogFragment();
		Bundle args = new Bundle();
		args.putInt("title", title);
		args.putInt("message", message);
		args.putInt("icon", icon);
		frag.setArguments(args);
		return frag;
	}

	public static MyAlertDialogFragment newInstance(int title, String message, int icon) {
		MyAlertDialogFragment frag = new MyAlertDialogFragment();
		Bundle args = new Bundle();
		args.putInt("title", title);
		args.putString("message_str", message);
		args.putInt("icon", icon);
		frag.setArguments(args);
		return frag;
	}

	// Return a Dialog to the DialogFragment.
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{
		int message = -1;
		String msg = "";
		Bundle bu =  getArguments();
		int title = bu.getInt("title");
		if(bu.containsKey("message"))
			message = bu.getInt("message");
		else if(bu.containsKey("message_str"))
			msg = bu.getString("message_str");

		int iconId = getArguments().getInt("icon");

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setIcon(iconId);
		builder.setTitle(title);
		if(message > -1)
			builder.setMessage(message);
		else if(!msg.isEmpty())
			builder.setMessage(msg);

		if(bu.containsKey("option") && bu.getInt("option") == OPTION_OPEN_GEOLOCALIZATION_SETTINGS)
		{
			mOption = bu.getInt("option");
			builder.setPositiveButton(R.string.open_geoloc_settings, this);
			builder.setNeutralButton(R.string.cancel_button, this);
		}
		else if(bu.containsKey("option") && bu.getInt("option") == OPTION_OPEN_NETWORK_SETTINGS)
		{
			mOption = bu.getInt("option");
			builder.setPositiveButton(R.string.open_network_settings, this);
			builder.setNeutralButton(R.string.cancel_button, this);	
		}
		else if(bu.containsKey("option") && bu.getInt("option") == OPTION_GRANT_LOCATION_PERMISSION)
		{
			Log.e("onCreateDialog", "grant location perm");
			mOption = bu.getInt("option");
			builder.setPositiveButton(R.string.proceed, this);
			builder.setNeutralButton(R.string.nothanks, this);	
		}
		else
		{
			builder.setPositiveButton(R.string.ok_button, this);
		}
		return builder.create();
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		if(which == DialogInterface.BUTTON_POSITIVE)
		{
			Intent intent = null;
			if(mOption == OPTION_OPEN_GEOLOCALIZATION_SETTINGS)
				intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			else if(mOption == OPTION_OPEN_NETWORK_SETTINGS)
				intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
			else if(mOption == OPTION_GRANT_LOCATION_PERMISSION)
			{
				Log.e("onCreateDialog", "grant location perm");
				LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(
					    new Intent(OsmerActivity.ACTION_GRANT_LOCATION_PERMISSION));
			}
			if(intent != null)
			{
				intent.putExtra("option", mOption);
				try{
					this.startActivity(intent);
				}
				catch(ActivityNotFoundException e)
				{
					
				}
			}
		}
	}
}

