package it.giacomos.android.osmer.pro.webcams;

import it.giacomos.android.osmer.pro.ViewType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;

public class WebcamDataCache 
{
	private static final int UPTODATE_INTERVAL = 30;
	private static final int UPDATE_TOOOLD_INTERVAL = 1800;
	
	private WebcamDataCache()
	{
		
	}
	
	/** Saves the string s into the cache.
	 * If successful, it stores the current date time into the cache file named 
	 * lastWebcamDataUpdate.
	 *  
	 * Note that if only one of the two tasks are accomplished (osmer or other), then
	 * the successful store will be marked with the current date even if it is not actually
	 * complete.
	 * 
	 * The user will have to wait UPTODATE_INTERVAL seconds before another real update, but
	 * this is ok, considering that we do not want to overload network if it's slow.
	 * 
	 * @param s
	 * @param t
	 * @return true if save was successful, false otherwise
	 */
	public boolean saveToCache(String s, ViewType t)
	{
		String filename;
		boolean ret = false;
		if(t == ViewType.WEBCAMLIST_OSMER)
			filename = "webcams_osmer.txt";
		else if(t == ViewType.WEBCAMLIST_OTHER)
			filename = "webcams_other.txt";
		else
			return ret;
		
		try {
			FileOutputStream fos = new FileOutputStream(new File(mCacheDir, filename));
			try {
				fos.write(s.getBytes());
				fos.close();
//				Log.i("WebcamDataCache: saveToCache", "written on " + filename);
				ret = true;
			} 
			catch (IOException e) { }
		} 
		catch (FileNotFoundException e)  { }
		
		/* now mark the last successful store */
		if(ret == true)
		{
			try {
				FileOutputStream fos = new FileOutputStream(new File(mCacheDir, "lastWebcamDataUpdate.dat"));
				try {
					String date = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
					fos.write(date.getBytes());
//					Log.i("WebcamDataCache: saveToCache", "marked save successful on " + date);
					fos.close();
					ret = true;
				} 
				catch (IOException e) { }
			} 
			catch (FileNotFoundException e)  { }
		}
		return ret;
	}
	
	public String getFromCache(ViewType t)
	{
		String text = "";
		String filename;
		if(t == ViewType.WEBCAMLIST_OSMER)
			filename = "webcams_osmer.txt";
		else if(t == ViewType.WEBCAMLIST_OTHER)
			filename = "webcams_other.txt";
		else
			return text;
		
		try {
			FileInputStream fis = new FileInputStream(new File(mCacheDir, filename));
			BufferedReader in = new BufferedReader(new InputStreamReader(fis));
			String inputLine;

			try {
				while ((inputLine = in.readLine()) != null) 
					text += inputLine + "\n";
				text = text.substring(0, text.length() - 1);
				in.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
				
		} 
		catch (FileNotFoundException e) 
		{		
			
		}
		if(text.isEmpty())
			Log.i("WebcamDataCache: saveToCache", "could not read from cache");
		return text;
		
	}
	
	public  boolean dataIsTooOld()
	{
		long secs = getSecondsFromLastUpdate();
		return secs > UPDATE_TOOOLD_INTERVAL;
	}
	
	public boolean dataIsOld()
	{
		boolean old = true;
		long secs = getSecondsFromLastUpdate();
		old =  (secs > UPTODATE_INTERVAL);
//		
//	    if(old == false)
//	    	Log.i("WebcamDataCache: dataIsOld()", "data not old");
//	    else
//	    	Log.i("WebcamDataCache: dataIsOld()", "data is old");
	    
		return old;
	}
	
	protected long getSecondsFromLastUpdate()
	{
		long secs = UPDATE_TOOOLD_INTERVAL + 1;
		try {
			FileInputStream fis = new FileInputStream(new File(mCacheDir, "lastWebcamDataUpdate.dat"));
			BufferedReader in = new BufferedReader(new InputStreamReader(fis));
			try {
				String inputLine = in.readLine();
				DateFormat df = DateFormat.getDateTimeInstance();
				in.close();
				
				try {
					Date date = df.parse(inputLine);
					
				    Calendar rightNow = Calendar.getInstance();
				    Date curDate = rightNow.getTime();
				    Log.i("WebcamDataCache: dataIsOld()", "curDate: " + curDate.toString() + "cached date " + date.toString());
				    secs = (curDate.getTime() - date.getTime()) / 1000;
				} 
				catch (ParseException e) 
				{
				}
			} 
			catch (IOException e) 
			{
			}
		} 
		catch (FileNotFoundException e) 
		{		
		}
		return secs;
	}
	
	public static WebcamDataCache getInstance(File cacheDir)
	{
		if(mInstance == null)
			mInstance = new WebcamDataCache();
		
		if(cacheDir != null)
			mInstance.setCacheDir(cacheDir);
		
		return mInstance;
	}
	
	public static WebcamDataCache getInstance()
	{
		return mInstance;
	}
	
	public void setCacheDir(File cacheDir)
	{
		mCacheDir = cacheDir;
	}
	
	static private WebcamDataCache mInstance = null;
	File mCacheDir;
}
