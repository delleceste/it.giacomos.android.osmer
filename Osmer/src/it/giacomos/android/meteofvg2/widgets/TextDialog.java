package it.giacomos.android.meteofvg2.widgets;

import it.giacomos.android.meteofvg2.R;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;

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
