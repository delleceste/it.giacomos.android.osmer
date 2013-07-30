package it.giacomos.android.osmer.widgets;

import it.giacomos.android.osmer.ViewType;
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
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/** Text change listener is installed here because this class restores from internal storage on her own
 * If it wasn't for that, the text change event may be triggered elsewhere (for the text to image decoding)
 * @author giacomo
 *
 */
public class OTextView extends TextView implements StateRestorer
{

	public OTextView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);	
		mRestoreSuccessful = false;
		this.setTextColor(Color.BLACK);
		this.setPadding(10, 10, 10, 10);
		mStringType = ViewType.HOME;
		mHtml  = null;
	}

	private boolean mSaveOnInternalStorage() 
	{
		if(mHtml == null)
			return false;
		

		Log.e("mSaveOnInternalStorage", this.getId() +", string type " + this.mStringType);
		FileOutputStream fos;
		try {
			fos = getContext().getApplicationContext().openFileOutput(makeFileName(), Context.MODE_PRIVATE);
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
		Log.e("restoreFromInternalStorage", this.getId() +", string type " + this.mStringType);
		String html = "";
		/* Open a private file associated with this Context's application package for reading. */
		try {
			String line;
			BufferedReader in = new BufferedReader(new FileReader(getContext().getApplicationContext().getFilesDir().getAbsolutePath() + "/" + makeFileName()));
			
			try {
				line = in.readLine();
				while(line != null)
				{
					html += line + "\n";
					line = in.readLine();
				}
				if(html.length() > 0)
					html = html.substring(0, html.length() - 1);
				mRestoreSuccessful = true;
			} 
			catch (IOException e) {
				
			}		
		} 
		catch (FileNotFoundException e) {}
		
		setHtml(html, false); /* false: do not save on internal storage again! */
		return true;
	}
	
	public String formatText(String s)
	{
		return s;
	}
	
	/* invoked by TextViewUpdater after that the TextTask has completed */
	public final void setHtml(String html, boolean saveOnInternalStorage)
	{
		if(mHtml == null || !html.equals(mHtml))
		{
			Spanned fromHtml = Html.fromHtml(html);
			mHtml = html;
			setText(fromHtml);
			/* each time text is updated, save it, if the flag is true */
			if(saveOnInternalStorage)
				mSaveOnInternalStorage();
		}
	}
	
	public void setStringType(ViewType t)
	{
		mStringType = t;
	}
	
	public ViewType getStringType()
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
	
	private boolean mRestoreSuccessful;	
	private ViewType mStringType;
}
