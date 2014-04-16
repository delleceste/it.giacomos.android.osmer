package it.giacomos.android.osmer.trial;

import it.giacomos.android.osmer.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;



public class PurchaseDialogFragment extends DialogFragment 
{
	
	public static PurchaseDialogFragment newInstance(int title, int message, int icon) {
		PurchaseDialogFragment frag = new PurchaseDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        args.putInt("message", message);
        args.putInt("icon", icon);
        frag.setArguments(args);
        return frag;
    }

	 @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        int title = getArguments().getInt("title");
	        int iconId = getArguments().getInt("icon");
	        int messageId = getArguments().getInt("message");
	        return new AlertDialog.Builder(getActivity())
	                .setIcon(iconId)
	                .setTitle(title)
	                .setMessage(messageId)
	                .setPositiveButton(R.string.ok_button, (DialogInterface.OnClickListener) getActivity())
	                .create();
	    }

}
