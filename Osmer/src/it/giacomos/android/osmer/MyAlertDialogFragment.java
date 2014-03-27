package it.giacomos.android.osmer;


import it.giacomos.android.osmer.pro.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

public class MyAlertDialogFragment extends DialogFragment 
{

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
		
		builder.setPositiveButton(R.string.ok_button,
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		});
		return builder.create();
	}
}

