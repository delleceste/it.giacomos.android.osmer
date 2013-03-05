/**
 * 
 */
package it.giacomos.android.osmer.downloadManager.state;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;
// import android.util.Log;
import it.giacomos.android.osmer.BitmapType;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * @author giacomo
 *
 */
@SuppressLint("NewApi")
public class BitmapTask extends AsyncTask<URL, Integer, Bitmap> 
{
	/** the constructor */
	public BitmapTask(BitmapListener bitmapUpdateListener, BitmapType bt)
	{
		m_stateUpdateListener = bitmapUpdateListener;
		m_errorMessage = "";
		m_bitmapType = bt;
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public final AsyncTask<URL, Integer, Bitmap> parallelExecute (URL... urls)
	{
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			return super.executeOnExecutor(THREAD_POOL_EXECUTOR, urls);
		}
		else
		{
			return super.execute(urls);
		}
	}
	
	public boolean error()
	{
		return !m_errorMessage.isEmpty();
	}
	
	protected Bitmap doInBackground(URL... urls) 
	{
		Bitmap bitmap = null;
		m_errorMessage = "";
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
        		m_errorMessage = "IOException: URL: \"" + url.toString() + "\":\n\"" + e.getLocalizedMessage() + "\"";
        	}
        	publishProgress(100);
        }    
        return bitmap;
	}
	
	public void onCancelled(Bitmap bmp)
	{
		
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
