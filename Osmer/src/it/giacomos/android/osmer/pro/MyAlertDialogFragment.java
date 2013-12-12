package it.giacomos.android.osmer.pro;


import it.giacomos.android.osmer.R.string;
import android.R;
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
		MyAlertDialogFragment.newInstance(string.error_message, message, R.drawable.ic_dialog_alert)
			.show(a.getSupportFragmentManager(), "ErrorDialog");
	}
	
	public static void MakeGenericInfo(int message, FragmentActivity a)
	{
		MyAlertDialogFragment.newInstance(string.info, message, R.drawable.ic_dialog_info)
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

    // Return a Dialog to the DialogFragment.
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) 
    {
    	int title = getArguments().getInt("title");
    	int message = getArguments().getInt("message");
    	int iconId = getArguments().getInt("icon");

        return new AlertDialog.Builder(getActivity())
                .setIcon(iconId)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(string.ok_button,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            
                        }
                    }
                ).create();
    }
}

