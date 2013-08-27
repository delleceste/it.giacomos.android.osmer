package it.giacomos.android.osmer.guiHelpers;

import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.preferences.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class UpgradeAlertDialogManager implements OnCheckedChangeListener,
	OnDismissListener
{
	public UpgradeAlertDialogManager(Activity a)
	{
		mActivity = a;
		mDialog = null;
	}
	
	public Dialog getAtStartup(int titleStringId, int messageStringId)
	{
		Settings s = new Settings(mActivity);
		boolean isUpgradeDialogEnabled = s.isUpgradeDialogEnabled();
		/* >= android 4.0.1? */
		boolean isAndroid4 = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1;
		if(isUpgradeDialogEnabled && isAndroid4)
			return getDialog(titleStringId, messageStringId, true);
		return null;
	}
	
	public Dialog getDialog(String titleString, String messageString, boolean withCheckbox)
	{
		LayoutInflater li = mActivity.getLayoutInflater();
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		View popupView = li.inflate(R.layout.popup_upgrade, null);
		builder.setView(popupView).setNegativeButton(R.string.ok_button, 
				new DialogInterface.OnClickListener() 
		{
               public void onClick(DialogInterface dialog, int id) 
               {
                   dialog.cancel();
               }
		});
		
		((TextView)popupView.findViewById(R.id.textViewUpgrade)).
			setText(Html.fromHtml(messageString));
		((TextView)popupView.findViewById(R.id.textViewUpgrade)).
			setMovementMethod(LinkMovementMethod.getInstance());
		
		if(withCheckbox)
		{
			popupView.findViewById(R.id.checkBoxDontShowAgain).setVisibility(View.VISIBLE);
			CheckBox cb = (CheckBox) popupView.findViewById(R.id.checkBoxDontShowAgain);
			cb.setOnCheckedChangeListener(this);
		}
		else
			popupView.findViewById(R.id.checkBoxDontShowAgain).setVisibility(View.GONE);
		
		mDialog = builder.create();
		mDialog.setTitle(titleString);
		mDialog.setOnDismissListener(this);
		return mDialog;
	}
	
	public Dialog getDialog(int titleStringId, int messageStringId, boolean withCheckbox)
	{
		return getDialog(mActivity.getString(titleStringId),
				mActivity.getString(messageStringId), withCheckbox);
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		// TODO Auto-generated method stub
		CheckBox cb = (CheckBox) ((Dialog) dialog).findViewById(R.id.checkBoxDontShowAgain);
		if(cb.isChecked())
			Toast.makeText(mActivity, R.string.to_show_upgrade_message_again_toast, 
					Toast.LENGTH_LONG).show();
	}

	@Override
	public void onCheckedChanged(CompoundButton btn, boolean checked) {
		// TODO Auto-generated method stub
		if(checked)
		{
			btn.setChecked(checked);
			Settings s = new Settings(mActivity);
			s.setUpgradeDialogEnabled(false);
		}	
	}
	
	public void closeDialog()
	{
		if(mDialog != null)
			mDialog.dismiss();
	}
	
	private Activity mActivity;
	private Dialog mDialog;
}
