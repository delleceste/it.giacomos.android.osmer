package it.giacomos.android.meteofvg2.downloadManager.state;

import it.giacomos.android.meteofvg2.ViewType;
import it.giacomos.android.meteofvg2.downloadManager.DownloadManagerUpdateListener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

public class TextTask extends AsyncTask<URL, Integer, String> {

	/** the constructor */
	public TextTask(TextListener textListener, ViewType t)
	{
		m_textUpdateListener = textListener;
		m_errorMessage = "";
		m_type = t;
		mReferer = null;
	}

	public void setReferer(String ref)
	{
		mReferer = ref;
	}

	public boolean error()
	{
		return !m_errorMessage.isEmpty();
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public final AsyncTask<URL, Integer, String> parallelExecute (URL... urls)
	{
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) /* 11 */
			return this.executeOnExecutor(THREAD_POOL_EXECUTOR, urls);
		else
			return this.execute(urls);
	}
	
	public void onPostExecute(String doc)
	{
		m_textUpdateListener.onTextUpdate(doc, m_type, m_errorMessage, this);
	}

	public void onCancelled(String doc)
	{
		if(doc != null)
			doc = null;
	}
	
	String errorMessage() { return m_errorMessage; }

	protected String doInBackground(URL... urls) 
	{
		String doc = "";
		m_errorMessage = "";
		if(urls.length == 1)
		{
			URLConnection urlConnection = null;
			try {
				urlConnection = urls[0].openConnection();
				/* need http referer */
				if(mReferer != null)
					urlConnection.setRequestProperty("Referer", mReferer);

				BufferedReader in = new BufferedReader(
						new InputStreamReader(
								urlConnection.getInputStream(), Charset.forName("ISO-8859-1")));
				String inputLine;

				while ((inputLine = in.readLine()) != null) 
					doc += inputLine + "\n";
				in.close();
				publishProgress(100);
			} 
			catch (IOException e) {
				// TODO Auto-generated catch block
				m_errorMessage = "Error downloading text document: "  + e.toString();
			}
		}
		return doc;
	}

	private ViewType m_type;
	private TextListener m_textUpdateListener;
	private String m_errorMessage;
	private String mReferer;
}
