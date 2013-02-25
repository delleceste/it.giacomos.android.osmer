/**
 * 
 */
package it.giacomos.android.osmer.downloadManager.state;

import android.os.AsyncTask;
import android.util.Log;
import it.giacomos.android.osmer.BitmapType;
import it.giacomos.android.osmer.downloadManager.DownloadManagerUpdateListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * @author giacomo
 *
 */
public class BitmapTask extends AsyncTask<URL, Integer, Bitmap> 
{
	/** the constructor */
	public BitmapTask(BitmapListener bitmapUpdateListener, BitmapType bt)
	{
		m_stateUpdateListener = bitmapUpdateListener;
		m_errorMessage = "";
		m_bitmapType = bt;
	}
	
	protected Bitmap doInBackground(URL... urls) 
	{
		Bitmap bitmap = null;
        if(urls.length == 1)
        {
        	URL url = urls[0];
        	try
        	{
        		InputStream inputStream = (InputStream) url.getContent();
        		bitmap = BitmapFactory.decodeStream(inputStream);
        	}
        	catch(IOException e)
        	{
        		m_errorMessage = "\"" + url.toString() + "\":\n" + e.getLocalizedMessage();
        	}
        	publishProgress(100);
        }    
        return bitmap;
	}
	
	public void onPostExecute(Bitmap bmp)
	{
		m_bitmap = bmp;
		m_stateUpdateListener.onBitmapUpdate(bmp, m_bitmapType, m_errorMessage);
	}

	public Bitmap bitmap() 
	{
		return m_bitmap;
	}
	
	public BitmapType bitmapType()
	{
		return m_bitmapType;
	}
	
	void setBitmapType(BitmapType bt)
	{
		m_bitmapType = bt;
	}
	
	private BitmapType m_bitmapType;
	private Bitmap m_bitmap; 
	private BitmapListener m_stateUpdateListener;
	private String m_errorMessage;

}
