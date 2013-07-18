package it.giacomos.android.osmer.guiHelpers;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.content.res.Resources;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.ViewType;
import it.giacomos.android.osmer.guiHelpers.MyTabListener;
import java.util.HashMap;

public class ActionBarPersonalizer {
	
	public static final int FORECAST = 0;
	public  static final int RADAR = 1;
	public  static final int DAILY_OBS = 2;
	public  static final int LATEST_OBS = 3;
	public  static final int WEBCAM = 4;
	
	private HashMap<Integer, Integer> mIconHash = new HashMap<Integer, Integer>();
	
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
		actionBar.setSelectedNavigationItem(index);
	}
	
	public void setTabIcon(int index, int resource)
	{
		Resources res = mActivity.getResources();
		mIconHash.put(index, resource);
		ActionBar actionBar = mActivity.getActionBar();
		/* update icon (may be necessary) */
//		if(actionBar.getTabCount() == 4 && index < 4)
//		{
//			ActionBar.Tab tab = actionBar.getTabAt(index);
//			tab.setIcon(res.getDrawable(resource));
//		}
	}
	
	/* switches to tab view and selects the given tab
	 *
	 */
	public void setTabSelected(int index)
	{
		ActionBar actionBar = mActivity.getActionBar();
		if(index < actionBar.getTabCount())
			actionBar.setSelectedNavigationItem(index);
	}
	
	public void drawerItemChanged(int id)
	{
		mSpinnerAdapter = null;
		mOnNavigationListener = null;
		ActionBar actionBar = mActivity.getActionBar();
		switch(id)
		{
		case 0:
			mType = FORECAST;
			Resources res = mActivity.getResources();
			Log.e("drawerItemChanged", "setting navigation mode TABS");
			/* TABS navigation mode may have been already set by the init() method in 
			 * OsmerActivity.java
			 */
			if(actionBar.getNavigationMode() != ActionBar.NAVIGATION_MODE_TABS)
				actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			mActivity.setTitle(R.string.situation);

			if(actionBar.getTabCount() == 0)
			{
				MyTabListener tabListener = new MyTabListener(mActivity);
				tabListener.setActionBarStateManager(mActionBarStateManager);

				Tab homeTab = actionBar.newTab().setText(res.getString(R.string.situation)).
						setTabListener(tabListener)
						.setTag(R.string.home_title);
				actionBar.addTab(homeTab);

				Tab todayTab = actionBar.newTab().setText(res.getString(R.string.today_title)).
						setTabListener(tabListener)
						.setTag(R.string.today_title);
				actionBar.addTab(todayTab);

				Tab tomorrowTab = actionBar.newTab().setText(res.getString(R.string.tomorrow_title)).
						setTabListener(tabListener)
						.setTag(R.string.tomorrow_title);
				actionBar.addTab(tomorrowTab);

				Tab twodaysTab = actionBar.newTab().setText(res.getString(R.string.two_days_title)).
						setTabListener(tabListener)
						.setTag(R.string.two_days_title);
				actionBar.addTab(twodaysTab);
			}
			else
				Log.w("drawerItemChanged", "WARNIGN: already popylated with tabs!!!!!!!!!!!!");
//			for(int i = 1; i < actionBar.getTabCount(); i++)
//				if(mIconHash.containsKey(i))
//					actionBar.getTabAt(i).setIcon(res.getDrawable(mIconHash.get(i)));

			break;

		case 1:
			mType = RADAR;
			actionBar.removeAllTabs();
			Log.e("drawerItemChanged", "setting navigation mode STANDARD");
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			mActivity.switchView(ViewType.RADAR);
			mActivity.setTitle(R.string.radar_title);
			
			
			break;
			
		case 2:
			mType = DAILY_OBS;
			mActivity.setTitle(R.string.observations_title_daily);
			actionBar.removeAllTabs();
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			Log.e("drawerItemChanged", "setting navigation mode NAVIGATION_MODE_LIST");
			mSpinnerAdapter = ArrayAdapter.createFromResource(mActivity, R.array.dailyobs_text_items,
					android.R.layout.simple_spinner_dropdown_item);

			mOnNavigationListener = new OnNavigationListener() 
			{
				@Override
				public boolean onNavigationItemSelected(int position, long itemId) 
				{
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
			mActivity.setTitle(R.string.observations_title_latest);
			actionBar.removeAllTabs();
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			Log.e("drawerItemChanged", "setting navigation mode NAVIGATION_MODE_LIST");
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
			actionBar.removeAllTabs();
			mActivity.setTitle(R.string.title_webcam);
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			Log.e("drawerItemChanged", "setting navigation mode NAVIGATION_MODE_STANDARD");
			mActivity.switchView(ViewType.WEBCAM);
			
			break;
		}
		
		if(mOnNavigationListener != null && mSpinnerAdapter != null)
			mActivity.getActionBar().setListNavigationCallbacks(mSpinnerAdapter, mOnNavigationListener);
		
		mActionBarStateManager.onActionBarTypeChanged(mType);
	}


	private OsmerActivity mActivity;
	private SpinnerAdapter mSpinnerAdapter;
	private OnNavigationListener mOnNavigationListener;
	private TabListener mTabListener;
	private int mType;
	private ActionBarStateManager mActionBarStateManager;
}
