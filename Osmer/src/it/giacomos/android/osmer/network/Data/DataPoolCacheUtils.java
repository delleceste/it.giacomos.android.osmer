package it.giacomos.android.osmer.network.Data;

import it.giacomos.android.osmer.pro.R;
import it.giacomos.android.osmer.network.state.BitmapType;
import it.giacomos.android.osmer.network.state.ViewType;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class DataPoolCacheUtils 
{
	final HashMap<BitmapType, Integer> bitmapIdMap = new HashMap<BitmapType, Integer>();
	final HashMap<ViewType, Integer> textIdMap = new HashMap<ViewType, Integer>();

	public DataPoolCacheUtils()
	{
		/* to be compatible with the past versions who used to save on the internal storage
		 * the images and text with the id of the associated views
		 */
		textIdMap.put(ViewType.TODAY_SYMTABLE, R.id.todayImageView);
		textIdMap.put(ViewType.TOMORROW_SYMTABLE, R.id.tomorrowImageView);
		textIdMap.put(ViewType.TWODAYS_SYMTABLE, R.id.twoDaysImageView);
		textIdMap.put(ViewType.THREEDAYS_SYMTABLE, R.id.threeDaysImageView);
		textIdMap.put(ViewType.FOURDAYS_SYMTABLE, R.id.fourDaysImageView);

		/* bitmapIdMap must contain entries also for bitmap types that are treated in
		 * a special way, such as the radar image, whose file name must be 
		 * lastRadarImage.bmp in order to stay compatible with previous versions of the
		 * app. The value -1 is not actually used (see makeFileName
		 */
		bitmapIdMap.put(BitmapType.RADAR, -1);

		textIdMap.put(ViewType.HOME, R.id.homeTextView);
		textIdMap.put(ViewType.TODAY, R.id.todayTextView);
		textIdMap.put(ViewType.TOMORROW, R.id.tomorrowTextView);
		textIdMap.put(ViewType.TWODAYS, R.id.twoDaysTextView);
		textIdMap.put(ViewType.THREEDAYS, R.id.threeDaysTextView);
		textIdMap.put(ViewType.FOURDAYS, R.id.fourDaysTextView);
		/* As stated above for the radar bitmap, let's put two fictitious entries for
		 * the observations (daily and latest) and for the webcams (osmer and other)
		 */
		textIdMap.put(ViewType.DAILY_TABLE, -1);
		textIdMap.put(ViewType.LATEST_TABLE, -1);
		textIdMap.put(ViewType.WEBCAMLIST_OSMER, -1);
		textIdMap.put(ViewType.WEBCAMLIST_OTHER, -1);
	}

	/* restore all data from the storage */
	public String loadFromStorage(ViewType viewType, Context ctx) 
	{
//		long startT = System.currentTimeMillis();
		String txt = "";
		int nRead;
		/* text items */
		String charset;
		if(viewType == ViewType.WEBCAMLIST_OTHER)
			charset = "UTF-8";
		else
			charset = "ISO-8859-1";
		ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
		try
		{
			byte [] buf = new byte[128];
			InputStream is = new FileInputStream(ctx.getFilesDir().getAbsolutePath() 
					+ "/" + makeFileName(viewType));
			while((nRead = is.read(buf)) != -1)
				byteBuffer.write(buf, 0, nRead);
			byteBuffer.flush();
			buf = byteBuffer.toByteArray();
			txt = new String(buf, charset);
		}
		catch (IOException ex) {

		}		
//		Log.e("DataPoolCacheUtils.loadFromStorage", "loading string for " + viewType + " took " + (System.currentTimeMillis() - startT));
		return txt;
	}

	public Bitmap loadFromStorage(BitmapType bitmapType, Context ctx) 
	{
//		long startT = System.currentTimeMillis();
//		Log.e("DataPoolCacheUtils.loadFromStorage", "loading bitmap for " + bitmapType);
		Bitmap bmp = null;
		/* Decode a file path into a bitmap. If the specified file name is null, 
		 * or cannot be decoded into a bitmap, the function returns null. 
		 */
		File filesDir = ctx.getFilesDir();
		bmp = BitmapFactory.decodeFile(filesDir.getAbsolutePath() + "/" + makeFileName(bitmapType));
//		Log.e("DataPoolCacheUtils.loadFromStorage", "loading bitmap for  " + bitmapType + " took " + (System.currentTimeMillis() - startT));
		return bmp;
	}

	/* save all data to storage */
	public void saveToStorage(byte[] bytes, BitmapType bitmapType, Context ctx)
	{
//		long startT = System.currentTimeMillis();
		try
		{
			FileOutputStream fos;
			fos = ctx.openFileOutput(makeFileName(bitmapType), Context.MODE_PRIVATE);
			fos.write(bytes);
			fos.close();
		} 
		catch (FileNotFoundException e) {
			/* nada que hacer */
		}
		catch (IOException e) {
			e.printStackTrace();
		}
//		Log.e("DataPoolCacheUtils.saveToStorage", "saving bitmap for " + bitmapType + " took " + (System.currentTimeMillis() - startT));
	}

	public void saveToStorage(byte[] bytes, ViewType viewType, Context ctx)
	{
//		long startT = System.currentTimeMillis();
		String charset;
		if(viewType == ViewType.WEBCAMLIST_OTHER)
			charset = "UTF-8";
		else
			charset = "ISO-8859-1";
		if(bytes != null)
		{
			try
			{
				String filename = ctx.getFilesDir().getAbsolutePath() 
						+ "/" + makeFileName(viewType);
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), charset)); 
//				Log.e("saveToStorage", "writing " + filename + " type " + viewType);
				out.write(new String(bytes, charset));
				out.close();
			}
			catch (FileNotFoundException e) {
				Log.e("DataPoolCacheUtils.saveToStorage", e.getLocalizedMessage());
				/* nada que hacer */
			}
			catch (IOException e) 
			{
				Log.e("DataPoolCacheUtils.saveToStorage", e.getLocalizedMessage());
			}
//			Log.e("DataPoolCacheUtils.saveToStorage", "saving string for " + viewType + " took " + (System.currentTimeMillis() - startT));
		}
	}

	public String makeFileName(ViewType vt)
	{
		/* special file names for daily and latest tables */
		if(vt == ViewType.LATEST_TABLE)
			return "latest_observations.txt";
		else if(vt == ViewType.DAILY_TABLE)
			return  "daily_observations.txt";
		else if(vt == ViewType.WEBCAMLIST_OSMER)
			return "webcams_osmer.txt";
		else if(vt == ViewType.WEBCAMLIST_OTHER)
			return "webcams_other.txt";
		else
			return "textViewHtml_" + textIdMap.get(vt) + ".txt";
	}

	public String makeFileName(BitmapType bt)
	{
		if(bt == BitmapType.RADAR)
			return "lastRadarImage.bmp";
		return "image_" + bitmapIdMap.get(bt) + ".bmp";
	}
}
