package it.giacomos.android.osmer.guiHelpers;

import android.os.Bundle;
import android.util.Log;

public class ActionBarStateManager 
{
	public ActionBarStateManager() 
	{
		mActionBarTab = 0;
		mSpinnerPosition = 0;
		mActionBarType = ActionBarPersonalizer.FORECAST;
	}
	
	public void saveState(Bundle bu)
	{
		if(mActionBarType == ActionBarPersonalizer.FORECAST)
			bu.putInt("tabTag", mActionBarTab);
		bu.putInt("spinnerPosition", mSpinnerPosition);
		bu.putInt("actionBarType", mActionBarType);
	}
	
	public void restoreState(Bundle b, ActionBarPersonalizer abp)
	{
		mActionBarTab = b.getInt("tabTag", 0);
		mSpinnerPosition = b.getInt("spinnerPosition", 0);
		mActionBarType = b.getInt("actionBarType", 0);
		
		
		if(mActionBarType == ActionBarPersonalizer.FORECAST)
		{
			Log.e("restoreState (ActionBarStateManager)", "setTabSelected " + mActionBarTab);
			abp.setTabSelected(mActionBarTab);
		}
		else if(mActionBarType == ActionBarPersonalizer.DAILY_OBS 
				|| mActionBarType == ActionBarPersonalizer.LATEST_OBS)
		{
			abp.setNavigationItem(mSpinnerPosition);
		}
		else if(mActionBarType == ActionBarPersonalizer.RADAR)
		{
			abp.setTabSelected(0);
		}
		else if(mActionBarType == ActionBarPersonalizer.WEBCAM)
		{
			abp.setTabSelected(0);
		}
		/* change the action bar type and mode */
		Log.e("restoreState (ActionBarStateManager)", "calling drawerItemChanged " + mActionBarType);
		abp.drawerItemChanged(mActionBarType);
	}
	
	public int getSelectedTab()
	{
		return mActionBarTab;
	}
	
	public void backToForecast(ActionBarPersonalizer abp)
	{
		abp.drawerItemChanged(ActionBarPersonalizer.FORECAST);
		/* restore last selected tab */
		abp.setNavigationItem(mActionBarTab);
	}
	
	public void onActionBarTabChanged(int tabTag)
	{
		mActionBarTab = tabTag;
	}
	
	public void onActionBarTypeChanged(int type)
	{
		mActionBarType = type;
	}
	
	public void onSpinnerItemChanged(int spinnerPosition)
	{
		mSpinnerPosition = spinnerPosition;
	}
	
	private int mSpinnerPosition;
	private int mActionBarType;
	private int mActionBarTab;

}
