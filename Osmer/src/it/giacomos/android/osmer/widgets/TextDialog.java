package it.giacomos.android.osmer.widgets;

import it.giacomos.android.osmer.R;

import android.app.Dialog;
import android.content.Context;

public class TextDialog extends Dialog
{
	
	public TextDialog(Context context, String title, String mess) 
	{
		super(context);
		mTitle = title;
		mMessage = mess;
	}

	public void setup(String title, String mess)
	{
		mTitle = title;
		mMessage = mess;
	}
	
	
	private String mTitle = "", mMessage = "";
}
