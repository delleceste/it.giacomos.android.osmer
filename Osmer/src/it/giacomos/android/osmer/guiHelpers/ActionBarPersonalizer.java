package it.giacomos.android.osmer.guiHelpers;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import it.giacomos.android.osmer.ForecastFragment;
import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.SituationFragment;
import it.giacomos.android.osmer.TabsAdapter;
import it.giacomos.android.osmer.ViewType;

public class ActionBarPersonalizer implements ActionBarTabChangeListener 
{
	
	public static final int FORECAST = 0;
	public  static final int RADAR = 1;
	public  static final int DAILY_OBS = 2;
	public  static final int LATEST_OBS = 3;
	public  static final int WEBCAM = 4;
	
	private TabsAdapter mTabsAdapter;
		
	public void setActionBarStateManager(ActionBarStateManager stateManager)
	{
		mActionBarStateManager = stateManager;
	}
	
	public int getType() 
	{
		return mType;
	}

	public ActionBarPersonalizer(OsmerActivity a)
	{
		mActivity = a;
		mActionBarStateManager = null;
	}
	
	public void setNavigationItem(int index)
	{
		ActionBar actionBar = mActivity.getActionBar();
		Log.e("setNavigationItem", "setting selected navigation indx " + index);
		actionBar.setSelectedNavigationItem(index);
	}
	
	/* switches to tab view and selects the given tab
	 *
	 */
	public void setTabSelected(int index)
	{
		ActionBar actionBar = mActivity.getActionBar();
		if(index < actionBar.getTabCount())
		{
			Log.e("setTabSelected", "setting selected navigation indx " + index);
			actionBar.setSelectedNavigationItem(index);
		}
	}
	
	public void drawerItemChanged(int id)
	{
		Log.e("drawerItemChaneged in ActionBarPersonalizer", "drawerOtemChanged to " + id);
		mSpinnerAdapter = null;
		mOnNavigationListener = null;
		ActionBar actionBar = mActivity.getActionBar();
		switch(id)
		{
		case 0:
			mType = FORECAST;
			Resources res = mActivity.getResources();
			/* TABS navigation mode may have been already set by the init() method in 
			 * OsmerActivity.java
			 */
			if(actionBar.getNavigationMode() != ActionBar.NAVIGATION_MODE_TABS)
			{
				actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
				/* tabs were removed. Navigation mode was changed: create a new TabsAdapter */
				if(mTabsAdapter != null)
					mTabsAdapter.clear();
				mTabsAdapter = null;
				mTabsAdapter = new TabsAdapter(mActivity, mActivity.getViewPager());	
				mTabsAdapter.setActionBarTabChangeListener(this);
			}
			mActivity.setTitle(R.string.situation);

			if(actionBar.getTabCount() == 0)
			{
				Bundle bToday = new Bundle();
				Bundle bTomorrow = new Bundle();
				Bundle bTwodays = new Bundle();
				
				bToday.putInt("type", R.string.today_title);
				bTomorrow.putInt("type", R.string.tomorrow_title);
				bTwodays.putInt("type", R.string.two_days_title);
				
				mTabsAdapter.addTab(actionBar.newTab().setText(res.getString(R.string.situation)),
		                SituationFragment.class, null);
		        mTabsAdapter.addTab(actionBar.newTab().setText(res.getString(R.string.today_title)),
		                ForecastFragment.class, bToday);
		        mTabsAdapter.addTab(actionBar.newTab().setText(res.getString(R.string.tomorrow_title)),
		        		ForecastFragment.class, bTomorrow);
		        mTabsAdapter.addTab(actionBar.newTab().setText(res.getString(R.string.two_days_title)),
		        		ForecastFragment.class, bTwodays);
			}
			mActivity.setTitle(R.string.home_title);
			break;

		case 1:
			Log.e("drawerItemChanged", "switching to radar");
			mType = RADAR;
			mTabsAdapter.clear();
			actionBar.selectTab(actionBar.getTabAt(0));
			actionBar.removeAllTabs();
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			mActivity.switchView(ViewType.RADAR);
			mActivity.setTitle(R.string.radar_title);
			break;
			
		case 2:
			mType = DAILY_OBS;
			mTabsAdapter.clear();
			mActivity.setTitle(R.string.observations_title_daily);
			actionBar.removeAllTabs();
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			mSpinnerAdapter = ArrayAdapter.createFromResource(mActivity, R.array.dailyobs_text_items,
					android.R.layout.simple_spinner_dropdown_item);

			mOnNavigationListener = new OnNavigationListener() 
			{
				@Override
				public boolean onNavigationItemSelected(int position, long itemId) 
				{
					Log.e("ActionBarPersonalizer", "onNavigationItemSelected " + position);
					switch(position)
					{
					case 0:
						mActivity.switchView(ViewType.DAILY_SKY);
						break;
					case 1:
						mActivity.switchView(ViewType.DAILY_MIN_TEMP);
						break;
					case 2:
						mActivity.switchView(ViewType.DAILY_MEAN_TEMP);
						break;
					case 3:
						mActivity.switchView(ViewType.DAILY_MAX_TEMP);
						break;
					case 4:
						mActivity.switchView(ViewType.DAILY_HUMIDITY);
						break;
					case 5:
						mActivity.switchView(ViewType.DAILY_WIND);
						break;
					case 6:
						mActivity.switchView(ViewType.DAILY_WIND_MAX);
						break;
					case 7:
						mActivity.switchView(ViewType.DAILY_RAIN);
						break;
					}
					mActionBarStateManager.onSpinnerItemChanged(position);
					return true;
				}
			};
			break;

		case 3:
			mType = LATEST_OBS;
			mTabsAdapter.clear();
			mActivity.setTitle(R.string.observations_title_latest);
			actionBar.removeAllTabs();
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			mSpinnerAdapter = ArrayAdapter.createFromResource(mActivity, R.array.latestobs_text_items,
					android.R.layout.simple_spinner_dropdown_item);

			mOnNavigationListener = new OnNavigationListener() {
				/*
					Latest
        <item>Sky</item>
        <item>Temperature</item>
        <item>Pressure</item>
        <item>Wind speed</item>
        <item>Rain</item>
        <item>Snow</item>
        <item>Humidity</item>
				 */

				@Override
				public boolean onNavigationItemSelected(int position, long itemId) 
				{
					Log.e("onNavigationItemSelected", "switch view to " + itemId);
					switch(position) 
					{
					case 0:
						mActivity.switchView(ViewType.LATEST_SKY);
						break;
					case 1:
						mActivity.switchView(ViewType.LATEST_TEMP);
						break;
					case 2:
						mActivity.switchView(ViewType.LATEST_PRESSURE);
						break;
					case 3:
						mActivity.switchView(ViewType.LATEST_WIND);
						break;
					case 4:
						mActivity.switchView(ViewType.LATEST_RAIN);
						break;
					case 5:
						mActivity.switchView(ViewType.LATEST_SNOW);
						break;
					case 6:
						mActivity.switchView(ViewType.LATEST_HUMIDITY);
						break;
					}
					mActionBarStateManager.onSpinnerItemChanged(position);
					return true;
				}
			};

			break;
			
		case 4:
			mType = WEBCAM;
			mTabsAdapter.clear();
			actionBar.removeAllTabs();
			mActivity.setTitle(R.string.title_webcam);
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			mActivity.switchView(ViewType.WEBCAM);
			
			break;
		}
		
		if(mOnNavigationListener != null && mSpinnerAdapter != null)
			mActivity.getActionBar().setListNavigationCallbacks(mSpinnerAdapter, mOnNavigationListener);
		
		mActionBarStateManager.onActionBarTypeChanged(mType);
	}

	@Override
	public void onActionBarTabChanged(int tab) 
	{
		mActionBarStateManager.onActionBarTabChanged(tab);
		ViewType viewType = ViewType.HOME;
		if(tab == 1) /* i == 0 -> ViewType.HOME, but already initialized */
        	viewType = ViewType.TODAY;
        else if(tab == 2)
        	viewType = ViewType.TOMORROW;
        else if(tab == 3)
        	viewType = ViewType.TWODAYS;
        mActivity.switchView(viewType);
	}

	private OsmerActivity mActivity;
	private SpinnerAdapter mSpinnerAdapter;
	private OnNavigationListener mOnNavigationListener;
	private int mType;
	private ActionBarStateManager mActionBarStateManager;

}
