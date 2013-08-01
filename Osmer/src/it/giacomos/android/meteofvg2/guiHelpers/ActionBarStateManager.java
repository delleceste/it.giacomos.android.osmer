package it.giacomos.android.meteofvg2.guiHelpers;

import android.app.ActionBar;
import android.os.Bundle;

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
		bu.putInt("tabTag", mActionBarTab);
		bu.putInt("spinnerPosition", mSpinnerPosition);
		bu.putInt("actionBarType", mActionBarType);
	}
	
	public void restoreState(Bundle b, ActionBarPersonalizer abp)
	{
		mActionBarTab = b.getInt("tabTag");
		mSpinnerPosition = b.getInt("spinnerPosition");
		mActionBarType = b.getInt("actionBarType");
		
		/* change the action bar type and mode */
		abp.drawerItemChanged(mActionBarType);
		
		if(mActionBarType == ActionBarPersonalizer.FORECAST)
		{
			abp.setTabSelected(mActionBarTab);
		}
		else if(mActionBarType == ActionBarPersonalizer.DAILY_OBS 
				|| mActionBarType == ActionBarPersonalizer.LATEST_OBS)
		{
			abp.setNavigationItem(mSpinnerPosition);
		}
		else if(mActionBarType == ActionBarPersonalizer.RADAR)
		{
			
		}
		else if(mActionBarType == ActionBarPersonalizer.WEBCAM)
		{
			
		}
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
