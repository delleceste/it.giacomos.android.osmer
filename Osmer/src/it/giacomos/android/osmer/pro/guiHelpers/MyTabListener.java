package it.giacomos.android.osmer.pro.guiHelpers;

import it.giacomos.android.osmer.pro.OsmerActivity;
import it.giacomos.android.osmer.pro.R;
import it.giacomos.android.osmer.pro.ViewType;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.util.Log;

public class MyTabListener implements ActionBar.TabListener 
{

	private OsmerActivity mActivity;
	
	private ActionBarStateManager mActionBarStateManager;
	
	public void setActionBarStateManager(ActionBarStateManager stateManager)
	{
		mActionBarStateManager = stateManager;
	}
	
	public MyTabListener(OsmerActivity a)
	{
		mActivity = a;
		mActionBarStateManager = null;
	}
	
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction arg1) {
		
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction arg1) 
	{
		// TODO Auto-generated method stub
		int tag = (Integer) tab.getTag();
		switch(tag)
		{
		case R.string.home_title:
			mActivity.switchView(ViewType.HOME);
			break;
		case R.string.today_title:
			mActivity.switchView(ViewType.TODAY);
			break;
		case R.string.tomorrow_title:
			mActivity.switchView(ViewType.TOMORROW);
			break;
		case R.string.two_days_title:
			mActivity.switchView(ViewType.TWODAYS);
			break;		
		}
		mActionBarStateManager.onActionBarTabChanged(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}

}
