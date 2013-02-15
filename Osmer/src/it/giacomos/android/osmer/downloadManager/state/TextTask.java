package it.giacomos.android.osmer.downloadManager.state;

import it.giacomos.android.osmer.StringType;
import it.giacomos.android.osmer.downloadManager.DownloadManagerUpdateListener;

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

import android.os.AsyncTask;
import android.util.Log;

public class TextTask extends AsyncTask<URL, Integer, String> {

	/** the constructor */
	public TextTask(TextListener textListener, StringType t)
	{
		m_textUpdateListener = textListener;
		m_errorMessage = "Text task error";
		m_type = t;
		mReferer = null;
	}

	public void setReferer(String ref)
	{
		mReferer = ref;
	}

	public void onPostExecute(String doc)
	{
		m_textUpdateListener.onTextUpdate(doc, m_type, m_errorMessage);
	}

	boolean error() { return m_errorMessage != null; }

	String errorMessage() { return m_errorMessage; }

	protected String doInBackground(URL... urls) 
	{
		String doc = "empty doc";
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

		//  DocDownloader downloader = new DocDownloader(urls[0]);    
		//  webPage = downloader.html();
		//  publishProgress(100);
		return doc;
	}

	private StringType m_type;
	private TextListener m_textUpdateListener;
	private String m_errorMessage;
	private String mReferer;
}
