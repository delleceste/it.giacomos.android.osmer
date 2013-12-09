package it.giacomos.android.osmer.pro.widgets.map.report;

import com.google.android.gms.maps.model.LatLng;

import it.giacomos.android.osmer.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

public class ReportRequestCancelConfirmDialog extends DialogFragment 
{
	private LatLng mLatLng = null;
	
	public void setLatLng(LatLng point)
	{
		mLatLng = point;
	}
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) 
	{

        return new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(this.getString(R.string.reportRemoveConfirm))
                .setPositiveButton(R.string.yes,
                    new DialogInterface.OnClickListener() 
                {
                        public void onClick(DialogInterface dialog, int whichButton) 
                        {
                            Log.e("ReportRequestCancelConfirmDialog.onClick", "removing");
                        }
                    }
                )
                .setNegativeButton(R.string.no,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) 
                        {
                        	Log.e("ReportRequestCancelConfirmDialog.onClick", "not removing");
                        }
                    }
                )
                .create();
    }

}
