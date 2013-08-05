package it.giacomos.android.osmer.downloadManager.Data;

import it.giacomos.android.osmer.BitmapType;
import it.giacomos.android.osmer.DownloadStateListener;
import it.giacomos.android.osmer.ViewType;
import it.giacomos.android.osmer.downloadManager.DownloadReason;

import java.util.HashMap;

import android.graphics.Bitmap;


public class DataPool implements DownloadListener 
{

	private static DataPool m_instance = null;
	
	private HashMap<BitmapType, BitmapData> mBitmapData;
	
	private HashMap<ViewType, StringData> mStringData;
	
	private HashMap<BitmapType, DataPoolBitmapListener> mBitmapListeners;
	
	private HashMap<ViewType, DataPoolTextListener> mTextListeners;

	private DataPool()
	{
		mBitmapData = new HashMap<BitmapType, BitmapData>();
		mStringData = new HashMap<ViewType, StringData>();
		mBitmapListeners = new HashMap<BitmapType, DataPoolBitmapListener>();
		mTextListeners = new HashMap<ViewType, DataPoolTextListener>();
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
	
	public void registerTextListener(ViewType vt, DataPoolTextListener txtL)
	{
		mTextListeners.put(vt, txtL);
		/* immediately notify if data is present */
		if(mStringData.containsKey(vt))
		{
			StringData sd = mStringData.get(vt);
			if(sd.isValid())
				txtL.onTextChanged(sd.text);
			else
				txtL.onTextError(sd.error);
		}
	}
	
	public void registerBitmapListener(BitmapType bt, DataPoolBitmapListener bmpL)
	{
		mBitmapListeners.put(bt, bmpL);
		if(mBitmapData.containsKey(bt))
		{
			BitmapData bd = mBitmapData.get(bt);
			if(bd.isValid())
				bmpL.onBitmapChanged(bd.bitmap);
			else
				bmpL.onBitmapError(bd.error);
		}
	}

	@Override
	public void onBitmapUpdate(Bitmap bmp, BitmapType t) 
	{
		mBitmapData.put(t, new BitmapData(bmp));
		if(mBitmapListeners.containsKey(t))
			mBitmapListeners.get(t).onBitmapChanged(bmp);
	}

	@Override
	public void onBitmapUpdateError(BitmapType t, String error) 
	{
		mBitmapData.put(t, new BitmapData(null, error));
		if(mBitmapListeners.containsKey(t))
			mBitmapListeners.get(t).onBitmapError(error);
	}

	@Override
	public void onTextUpdate(String text, ViewType t) 
	{
		mStringData.put(t, new StringData(text));
		if(mTextListeners.containsKey(t))
			mTextListeners.get(t).onTextChanged(text);
	}

	@Override
	public void onTextUpdateError(ViewType t, String error) 
	{
		mStringData.put(t, new StringData(null, error));
		if(mTextListeners.containsKey(t))
			mTextListeners.get(t).onTextError(error);
	}
 
}
