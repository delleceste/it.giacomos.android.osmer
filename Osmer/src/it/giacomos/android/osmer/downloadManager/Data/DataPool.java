package it.giacomos.android.osmer.downloadManager.Data;

import it.giacomos.android.osmer.BitmapType;
import it.giacomos.android.osmer.DataPoolErrorListener;
import it.giacomos.android.osmer.ViewType;
import java.util.HashMap;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;


public class DataPool implements DownloadListener 
{

	private static DataPool m_instance = null;
	
	private HashMap<BitmapType, BitmapData> mBitmapData;
	
	private HashMap<ViewType, StringData> mStringData;
	
	private HashMap<BitmapType, DataPoolBitmapListener> mBitmapListeners;
	
	private HashMap<ViewType, DataPoolTextListener> mTextListeners;
	
	private DataPoolErrorListener mDataPoolErrorListener;

	public HashMap<BitmapType, BitmapData> getBitmapData()
	{
		return mBitmapData;
	}
	
	public HashMap<ViewType, StringData>  getStringData()
	{
		return mStringData;
	}
	
	private DataPool()
	{
		mBitmapData = new HashMap<BitmapType, BitmapData>();
		mStringData = new HashMap<ViewType, StringData>();
		mBitmapListeners = new HashMap<BitmapType, DataPoolBitmapListener>();
		mTextListeners = new HashMap<ViewType, DataPoolTextListener>();
	}
	
	public void load(Context ctx)
	{
		DataPoolCacheUtils dpcu = new DataPoolCacheUtils();
		dpcu.loadFromStorage(this, ctx);
	}
	
	public void store(Context ctx)
	{
		DataPoolCacheUtils dpcu = new DataPoolCacheUtils();
		dpcu.saveToStorage(this, ctx);
	}
	
	public static DataPool Instance()
	{
		if(m_instance == null)
			m_instance = new DataPool();
		return m_instance;
	}
	
	public boolean isTextValid(ViewType vt)
	{
		return mStringData.containsKey(vt) && mStringData.get(vt).text != null;
	}
	
	public boolean isBitmapValid(BitmapType bt)
	{
		return mBitmapData.containsKey(bt) && mBitmapData.get(bt).bitmap != null;
	}
	
	public void registerErrorListener(DataPoolErrorListener l)
	{
		mDataPoolErrorListener = l;
	}
	
	public void unregisterTextListener(ViewType vt)
	{
		if(vt != null && mTextListeners.containsKey(vt))
			mTextListeners.remove(vt);
	}
	
	public void unregisterBitmapListener(BitmapType bt)
	{
		if(bt != null && mBitmapListeners.containsKey(bt))
			mBitmapListeners.remove(bt);
	}
	
	public void registerTextListener(ViewType vt, DataPoolTextListener txtL)
	{
		mTextListeners.put(vt, txtL);
		/* immediately notify if data is present */
		if(mStringData.containsKey(vt))
		{
			StringData sd = mStringData.get(vt);
			if(sd.isValid())
				txtL.onTextChanged(sd.text, vt, sd.fromCache);
			else
				txtL.onTextError(sd.error, vt);
		}
	}
	
	public void registerBitmapListener(BitmapType bt, DataPoolBitmapListener bmpL)
	{
		mBitmapListeners.put(bt, bmpL);
		if(mBitmapData.containsKey(bt))
		{
			BitmapData bd = mBitmapData.get(bt);
			if(bd.isValid())
				bmpL.onBitmapChanged(bd.bitmap, bt, bd.fromCache);
			else
				bmpL.onBitmapError(bd.error, bt);
		}
	}

	@Override
	public void onBitmapUpdate(Bitmap bmp, BitmapType t) 
	{
		mBitmapData.put(t, new BitmapData(bmp)); /* put in hash */
		if(mBitmapListeners.containsKey(t))
			mBitmapListeners.get(t).onBitmapChanged(bmp, t, false);
	}

	@Override
	public void onBitmapUpdateError(BitmapType t, String error) 
	{
		mBitmapData.put(t, new BitmapData(null, error)); /* put in hash */
		if(mBitmapListeners.containsKey(t))
			mBitmapListeners.get(t).onBitmapError(error, t);
		mDataPoolErrorListener.onBitmapUpdateError(t, error);
	}
	
	@Override
	public void onTextUpdate(String text, ViewType t) 
	{
		StringData sd = mStringData.get(t);
		StringData newSd = new StringData(text);
		if(!newSd.equals(sd))
		{
			mStringData.put(t, newSd); /* put in hash */
			if(mTextListeners.containsKey(t))
				mTextListeners.get(t).onTextChanged(text, t, false);
		}
		else
			Log.e("DataPool.onTextUpdate", "not updated: data not changed!");
	}

	public void onCacheTextUpdate(String text, ViewType t)
	{
		StringData sd = mStringData.get(t);
		StringData newSd = new StringData(text);
		newSd.fromCache = true; /* mark as coming from cache */
		if(!newSd.equals(sd))
		{
			mStringData.put(t, newSd); /* put in hash */
			if(mTextListeners.containsKey(t))
				mTextListeners.get(t).onTextChanged(text, t, true);
		}
		else
			Log.e("DataPool.onCacheTextUpdate", "not updated: data not changed!");
	}
	
	public void onCacheBitmapUpdate(Bitmap bmp, BitmapType t) 
	{
		BitmapData bd = new BitmapData(bmp);
		bd.fromCache = true; /* mark as coming from cache */
		mBitmapData.put(t, bd); /* put in hash */
		if(mBitmapListeners.containsKey(t))
			mBitmapListeners.get(t).onBitmapChanged(bmp, t, true);
	}

	@Override
	public void onTextUpdateError(ViewType t, String error) 
	{
		mStringData.put(t, new StringData(null, error));
		if(mTextListeners.containsKey(t))
			mTextListeners.get(t).onTextError(error, t);
		mDataPoolErrorListener.onTextUpdateError(t, error);
	}
 
}
