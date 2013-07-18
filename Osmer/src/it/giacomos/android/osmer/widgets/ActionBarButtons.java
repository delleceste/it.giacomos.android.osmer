package it.giacomos.android.osmer.widgets;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ToggleButton;

public class ActionBarButtons 
{
	public void hideAllButtons()
	{
		setCenterMapVisible(false);
		setRadarInfoVisible(false);
		setMeasureVisible(false);
		setSatViewVisible(false);
	}
	
	public boolean radarInfoEnabled()
	{
		return mRadarInfoEnabled;
	}
	
	public void setMeasureEnabled(boolean en)
	{
		mMeasureEnabled = en;
		if(btMeasure != null)
			btMeasure.setEnabled(mMeasureEnabled);
	}
	
	public void setRadarInfoEnabled(boolean en)
	{
		mRadarInfoEnabled = en;
		if(btRadarInfo != null)
			btRadarInfo.setEnabled(mRadarInfoEnabled);
	}
	
	public void setSatViewEnabled(boolean en)
	{
		mSatViewEnabled = en;
		if(btSatView != null)
			btSatView.setEnabled(mSatViewEnabled);
	}
	
	public void setCenterMapVisible(boolean v)
	{
		mCenterMapVisible = v;
		if(btCenterMap != null && v)
			btCenterMap.setVisibility(View.VISIBLE);
		else if(btCenterMap != null)
			btCenterMap.setVisibility(View.GONE);
	}
	
	public void setRadarInfoVisible(boolean v)
	{
		mRadarInfoVisible = v;
		if(btRadarInfo != null && v)
			btRadarInfo.setVisibility(View.VISIBLE);
		else if(btRadarInfo != null)
			btRadarInfo.setVisibility(View.GONE);
	}
	
	public void setSatViewVisible(boolean v)
	{
		mSatViewVisible = v;
		if(btSatView != null && v)
			btSatView.setVisibility(View.VISIBLE);
		else if(btSatView != null)
			btSatView.setVisibility(View.GONE);
	}
	
	public void setMeasureVisible(boolean v)
	{
		mMeasureVisible = v;
		if(btMeasure != null && v)
			btMeasure.setVisibility(View.VISIBLE);
		else if(btMeasure != null)
			btMeasure.setVisibility(View.GONE);
	}
	
	public ActionBarButtons()
	{
		btCenterMap = null;
		btSatView = null;
		btMeasure = null;
		btRadarInfo = null;
		mMeasureEnabled = false;
		mSatViewEnabled = false; 
		mRadarInfoEnabled = false;
		mMeasureVisible =false; 
		mSatViewVisible = false;
		mRadarInfoVisible = false;
		mCenterMapVisible = false;
	}
	
	public boolean isNull()
	{
		return btCenterMap == null;
	}
	
	public boolean isValid()
	{
		return !isNull();
	}
	
	public void setButtons(OnClickListener listener, ToggleButton centerMap, ToggleButton satView, ToggleButton measure, ToggleButton radarInfo)
	{
		btCenterMap = centerMap;
		btSatView = satView;
		btMeasure = measure;
		btRadarInfo = radarInfo;
		/* set click listener (OsmerActivity) */
		btCenterMap.setOnClickListener(listener);
		btSatView.setOnClickListener(listener);
		btMeasure.setOnClickListener(listener);
		btRadarInfo.setOnClickListener(listener);
	}

	public ToggleButton btCenterMap, btSatView, btMeasure, btRadarInfo;
	
	private boolean mMeasureEnabled, mSatViewEnabled, mRadarInfoEnabled;
	private boolean mMeasureVisible, mSatViewVisible, mRadarInfoVisible, mCenterMapVisible;
}
