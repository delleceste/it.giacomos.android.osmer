package it.giacomos.android.osmer.widgets;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class OTextView extends TextView implements StateSaver{

	public OTextView(Context context, AttributeSet attrs) {
		super(context, attrs);	
		mRestoreSuccessful = false;
		this.setTextColor(Color.BLACK);
		this.setPadding(10, 10, 10, 10);
		//setMovementMethod(LinkMovementMethod.getInstance());
	}

	@Override
	public boolean saveOnInternalStorage() 
	{
		if(mHtml == null)
			return false;
		
		FileOutputStream fos;
		try {
			fos = getContext().openFileOutput(makeFileName(), Context.MODE_PRIVATE);
			try {
				fos.write(mHtml.getBytes());
				fos.close();
				return true; /* success */
			} 
			catch (IOException e) 
			{
			}
		} 
		catch (FileNotFoundException e) {
			/* nada que hacer */
		}
		return false;
	}

	public boolean restoreFromInternalStorage()
	{
		String html = "";
		/* Open a private file associated with this Context's application package for reading. */
		try {
			String line;
			BufferedReader in = new BufferedReader(new FileReader(getContext().getFilesDir().getAbsolutePath() + "/" + makeFileName()));
			try {
				line = in.readLine();
				while(line != null)
				{
					html += line + "\n";
					line = in.readLine();
				}
			} 
			catch (IOException e) {}		
		} 
		catch (FileNotFoundException e) {}
		
		setHtml(html);
		return true;
	}
	
	public String formatText(String s)
	{
		return s;
	}
	
	public final void setHtml(String html)
	{
		/* store raw html for save and restore purposes */
		mHtml = html;
		setText(Html.fromHtml(html));
	}
	
	public Parcelable onSaveInstanceState()
	{
		Parcelable p = super.onSaveInstanceState();
		Bundle bundle = new Bundle();
		bundle.putParcelable("OTextViewState", p);
		if(mHtml != null)
			bundle.putString("html", mHtml);
		return bundle;
	}
	
	public void onRestoreInstanceState (Parcelable state)
	{
		Bundle b = (Bundle) state;
		String htm = b.getString("html");
		if(htm != null)
		{
			/* directly set saved html, do not call setHtml here:
			 * saved text is already ready to use.
			 */
			setHtml(htm);
			mRestoreSuccessful = true;
		}
		super.onRestoreInstanceState(b.getParcelable("OTextViewState"));
	}
	
	public String getHtml() {
		return mHtml;
	}

	private String mHtml;
	
	public boolean isRestoreSuccessful() {
		return mRestoreSuccessful;
	}
	
	public String makeFileName()
	{
		return "textViewHtml_" + this.getId() + "txt";
	}
	
	private boolean mRestoreSuccessful;
}
