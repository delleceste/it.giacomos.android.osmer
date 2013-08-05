package it.giacomos.android.osmer.downloadManager.Data;

import it.giacomos.android.osmer.BitmapType;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.ViewType;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.graphics.Bitmap;
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
		textIdMap.put(ViewType.HOME, R.id.tomorrowTextView);
		textIdMap.put(ViewType.HOME, R.id.twoDaysTextView);
	}
	
	public void loadFromStorage(DataPool dp, Context ctx) 
	{
		
	}
	
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
		/* save text to storage */
		
	}

	public String makeFileName(ViewType vt)
	{
		return "textViewHtml_" + textIdMap.get(vt) + ".txt";
	}

	public String makeFileName(BitmapType bt)
	{
		return "image_" + bitmapIdMap.get(bt) + ".bmp";
	}
}
