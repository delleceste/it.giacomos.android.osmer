package it.giacomos.android.osmer.widgets;

import it.giacomos.android.osmer.StringType;
import it.giacomos.android.osmer.textToImage.TextChangeListener;

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
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/** Text change listener is installed here because this class restores from internal storage on her own
 * If it wasn't for that, the text change event may be triggered elsewhere (for the text to image decoding)
 * @author giacomo
 *
 */
public class OTextView extends TextView implements StateSaver
{

	public OTextView(Context context, AttributeSet attrs) {
		super(context, attrs);	
		mRestoreSuccessful = false;
		this.setTextColor(Color.BLACK);
		this.setPadding(10, 10, 10, 10);
		mTextChangeListener = null;
		mStringType = StringType.HOME;
		mHtml  = null;
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
				if(html.length() > 0)
					html = html.substring(0, html.length() - 1);
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
//		if(mHtml != null)
//			Log.i("setHtml mHtml was", "\"" + mHtml + "\"");
//		else
//			Log.i("setHtml mHtml is null", "null mHtml");
//		
//		Log.i("setHtml new html is ", "\"" + html + "\"");
		if(mHtml == null || !html.equals(mHtml))
		{
			Spanned fromHtml = Html.fromHtml(html);
			if(mTextChangeListener != null)
				mTextChangeListener.onTextChanged(fromHtml.toString(), mStringType);

			mHtml = html;
//			Log.i("setHtml", "calling setText()");
			setText(fromHtml);
		}
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
	
	public void setStringType(StringType t)
	{
		mStringType = t;
	}
	
	public StringType getStringType()
	{
		return mStringType;
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
	
	/*
	 * it.giacomos.android.osmer.textToImage.TextChangeListener
	 */
	public void setTextChangeListener(TextChangeListener l)
	{
		Log.i("setTextChangeListener", "initializing text change listener");
		mTextChangeListener = l;
	}
	
	private boolean mRestoreSuccessful;
	
	private TextChangeListener mTextChangeListener;
	
	private StringType mStringType;
}
