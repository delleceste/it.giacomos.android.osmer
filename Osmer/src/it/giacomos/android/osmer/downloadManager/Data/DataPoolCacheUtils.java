package it.giacomos.android.osmer.downloadManager.Data;

import it.giacomos.android.osmer.BitmapType;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.ViewType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

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
		bitmapIdMap.put(BitmapType.TODAY, R.id.todayImageView);
		bitmapIdMap.put(BitmapType.TOMORROW, R.id.tomorrowImageView);
		bitmapIdMap.put(BitmapType.TWODAYS, R.id.twoDaysImageView);

		textIdMap.put(ViewType.HOME, R.id.homeTextView);
		textIdMap.put(ViewType.TODAY, R.id.todayTextView);
		textIdMap.put(ViewType.TOMORROW, R.id.tomorrowTextView);
		textIdMap.put(ViewType.TWODAYS, R.id.twoDaysTextView);
	}

	/* restore all data from the storage */
	public void loadFromStorage(DataPool dp, Context ctx) 
	{
		/* text items */
		Iterator<Entry<ViewType, Integer>> it = textIdMap.entrySet().iterator();
		while(it.hasNext())
		{
			String txt = null;
			String line;
			Map.Entry<ViewType, Integer> e = (Entry<ViewType, Integer>) it.next();
			try
			{
				BufferedReader in = new BufferedReader(new FileReader(ctx.getFilesDir().getAbsolutePath() 
					+ "/" + makeFileName(e.getKey())));
				try {
					line = in.readLine();
					while(line != null)
					{
						txt += line + "\n";
						line = in.readLine();
					}
					if(txt.length() > 0)
						txt = txt.substring(0, txt.length() - 1);
					/* save text on DataPool */
					if(txt != null)
						dp.onCacheTextUpdate(txt, e.getKey());
			}
				catch (IOException ex) {
					
				}		
			} 
			catch (FileNotFoundException exc) {}
		}

		/* 2. Bitmaps */
		Iterator<Entry<BitmapType, Integer>> bit = bitmapIdMap.entrySet().iterator();
		while(bit.hasNext())
		{
			Bitmap bmp = null;
			Map.Entry<BitmapType, Integer> be = (Entry<BitmapType, Integer>) bit.next();
			/* Decode a file path into a bitmap. If the specified file name is null, 
			 * or cannot be decoded into a bitmap, the function returns null. 
			 */
			File filesDir = ctx.getFilesDir();
			bmp = BitmapFactory.decodeFile(filesDir.getAbsolutePath() + "/" + makeFileName(be.getKey()));
			if(bmp != null)
				dp.onCacheBitmapUpdate(bmp, be.getKey());
		}
		
		/* observations */
		String data = "";
		final String[] filenames = { "latest_observations.txt", "daily_observations.txt" };
		for(String filename : filenames)
		{
			data = ""; /* clear! */
			try {
				String line;
				BufferedReader in = new BufferedReader(
						new FileReader(ctx.getFilesDir().getAbsolutePath() + "/" + filename));
				try {
					line = in.readLine();
					while(line != null)
					{
						data += line + "\n";
						line = in.readLine();
					}
				} 
				catch (IOException e) {}		
			} 
			catch (FileNotFoundException e) {}

			/* store the read file or the empty string */
			ViewType observationsStringType;
			if(filename == "latest_observations.txt")
				observationsStringType = ViewType.LATEST_TABLE;
			else
				observationsStringType = ViewType.DAILY_TABLE;

			dp.onCacheTextUpdate(data, observationsStringType);
		}
	}
	
	/* save all data to storage */
	public void saveToStorage(DataPool dp, Context ctx)
	{
		/* save all bitmaps to storage */
		Iterator<Entry<BitmapType, BitmapData>> it = dp.getBitmapData().entrySet().iterator();
		while(it.hasNext())
		{
			Map.Entry<BitmapType, BitmapData> pairs = (Entry<BitmapType, BitmapData>) it.next();
			Bitmap bmp = pairs.getValue().bitmap;
			if(bmp != null)
			{
				try
				{
					FileOutputStream fos;
					Log.e("mSaveOnInternalStorage", makeFileName(pairs.getKey()));
					fos = ctx.openFileOutput(makeFileName(pairs.getKey()), Context.MODE_PRIVATE);
					bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);	
				} 
				catch (FileNotFoundException e) {
					/* nada que hacer */
				}
			}
		}

		/* save text to storage (observations are included) */
		Iterator<Entry<ViewType, StringData>> sit = dp.getStringData().entrySet().iterator();
		while(sit.hasNext())
		{
			Map.Entry<ViewType, StringData> stringpairs = (Entry<ViewType, StringData>) sit.next();
			String txt = stringpairs.getValue().text;
			if(txt != null)
			{
				try
				{
					FileOutputStream fos = ctx.openFileOutput(makeFileName(stringpairs.getKey()), Context.MODE_PRIVATE);
					fos.write(txt.getBytes());
					fos.close();
				}
				catch (IOException e) 
				{
				}

			}
		}
	}

	public String makeFileName(ViewType vt)
	{
		/* special file names for daily and latest tables */
		if(vt == ViewType.LATEST_TABLE)
			return "latest_observations.txt";
		else if(vt == ViewType.DAILY_TABLE)
			return  "daily_observations.txt";
		else
			return "textViewHtml_" + textIdMap.get(vt) + ".txt";
	}

	public String makeFileName(BitmapType bt)
	{
		return "image_" + bitmapIdMap.get(bt) + ".bmp";
	}
}
