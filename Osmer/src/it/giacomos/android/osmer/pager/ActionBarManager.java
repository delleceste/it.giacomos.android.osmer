package it.giacomos.android.osmer.pager;

import android.app.ActionBar;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import it.giacomos.android.osmer.fragments.ForecastFragment;
import it.giacomos.android.osmer.fragments.SituationFragment;
import it.giacomos.android.osmer.network.state.ViewType;
import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;

public class ActionBarManager implements ActionBarTabChangeListener 
{
	
	public static final int FORECAST = 0;
	public  static final int RADAR = 1;
	public  static final int DAILY_OBS = 2;
	public  static final int LATEST_OBS = 3;
	public  static final int WEBCAM = 4;
	public static final int REPORT = 5;
	
	private TabsAdapter mTabsAdapter;
	private ActionBarListItemNavigationListener mActionBarListItemNavigationListener;
	
	public int getType() 
	{
		return mType;
	}

	public ActionBarManager(OsmerActivity a)
	{
		mActivity = a;
		mActionBarListItemNavigationListener = new ActionBarListItemNavigationListener(mActivity);
	}
	
	public void setNavigationItem(int index)
	{
		ActionBar actionBar = mActivity.getActionBar();
		actionBar.setSelectedNavigationItem(index);
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

	/* called after restore instance state, initializes the application according
	 * to the saved state of the action bar in the previous execution.
	 */
	public void init(Bundle savedInstanceState, int forceDrawerItem) 
	{
		boolean actionBarTabs = false;
		ActionBar actionBar = mActivity.getActionBar();
		ListView drawer = mActivity.getDrawerListView();
		int selectedDrawerItem = drawer.getCheckedItemPosition();
		if(selectedDrawerItem < 0)
			selectedDrawerItem = 0;
		
	//	Log.e("ActionBarManager.init", "selected Drawer Item " + selectedDrawerItem + " force " + forceDrawerItem);
		/* avoid calling drawerItemChanged if selectedDrawerItem is 0 because
		 * drawerItemChanged has already been called by OsmerActivity.init at 
		 * this point.
		 */
		if(forceDrawerItem < 0 && selectedDrawerItem != 0) /* otherwise call drawerItemChange afterwards */
			drawerItemChanged(selectedDrawerItem);
		if(savedInstanceState != null && actionBar.getNavigationMode() == ActionBar.NAVIGATION_MODE_TABS)
			actionBarTabs = true;
		else if(savedInstanceState == null)
			actionBarTabs = true;
		if(actionBarTabs)
		{
			int selectedTabIndex;
			ActionBar.Tab selectedTab = actionBar.getSelectedTab();
			if(selectedTab  != null)
			{
				selectedTabIndex = actionBar.getSelectedTab().getPosition();
			}
			else
				selectedTabIndex = 0;
			/* switch to correct tab */
			onActionBarTabChanged(selectedTabIndex);
			/* check the first item of the drawer */
			drawer.setItemChecked(0, true);
		}
		else
		{
			if(actionBar.getNavigationMode() == ActionBar.NAVIGATION_MODE_LIST)
			{
				int selected = savedInstanceState.getInt("spinnerPosition");
				mActionBarListItemNavigationListener.onNavigationItemSelected(selected, -1);
				actionBar.setSelectedNavigationItem(selected);
			} 
		}
		if(forceDrawerItem > 0) 
		{
			drawer.setItemChecked(forceDrawerItem, true);
			drawerItemChanged(forceDrawerItem);
		}
	}
	
	public void drawerItemChanged(int id)
	{
		mSpinnerAdapter = null;
		ActionBar actionBar = mActivity.getActionBar();
		switch(id)
		{
		case 0:
			mType = FORECAST;

			if(actionBar.getTabCount() == 0)
				mInitActionBarTabs();
			
			if(actionBar.getNavigationMode() != ActionBar.NAVIGATION_MODE_TABS)
			{
				actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
				/* enable the adapter. This reestablishes the tab count on the adapter */
				mTabsAdapter.enable();
			}
			break;

		case 1:
			mType = RADAR;
			if(mTabsAdapter != null)
				mTabsAdapter.disable();
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			mActivity.switchView(ViewType.RADAR);
			break;
			
		case 2:
			mType = DAILY_OBS;
			if(mTabsAdapter != null)
				mTabsAdapter.disable();
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			mSpinnerAdapter = ArrayAdapter.createFromResource(mActivity, R.array.dailyobs_text_items,
					android.R.layout.simple_spinner_dropdown_item);
			mActionBarListItemNavigationListener.setMode(ViewType.DAILY_TABLE);
			/* activity switchView is called by the listener + navigation callback */
			break;

		case 3:
			mType = LATEST_OBS;
			if(mTabsAdapter != null)
				mTabsAdapter.disable();
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			mSpinnerAdapter = ArrayAdapter.createFromResource(mActivity, R.array.latestobs_text_items,
					android.R.layout.simple_spinner_dropdown_item);
			mActionBarListItemNavigationListener.setMode(ViewType.LATEST_TABLE);
			/* activity switchView is called by the listener + navigation callback */
			break;
			
		case 4:
			mType = WEBCAM;
			if(mTabsAdapter != null)
				mTabsAdapter.disable();
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			mActivity.switchView(ViewType.WEBCAM);
			break;
		case 5:
			mType = REPORT;
			if(mTabsAdapter != null)
				mTabsAdapter.disable();
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			mActivity.switchView(ViewType.REPORT);
			break;
		}
		
		if(mSpinnerAdapter != null)
			actionBar.setListNavigationCallbacks(mSpinnerAdapter, mActionBarListItemNavigationListener);
	}

	@Override
	public void onActionBarTabChanged(int tab) 
	{
		ViewType viewType = ViewType.HOME;
		if(tab == 1) /* i == 0 -> ViewType.HOME, but already initialized */
        	viewType = ViewType.TODAY;
        else if(tab == 2)
        	viewType = ViewType.TOMORROW;
        else if(tab == 3)
        	viewType = ViewType.TWODAYS;
        mActivity.switchView(viewType);
	}

	private void mInitActionBarTabs()
	{
		Resources res = mActivity.getResources();
		ActionBar actionBar = mActivity.getActionBar();
		Bundle bToday = new Bundle();
		Bundle bTomorrow = new Bundle();
		Bundle bTwodays = new Bundle();
		
		bToday.putInt("type", R.string.today_title);
		bTomorrow.putInt("type", R.string.tomorrow_title);
		bTwodays.putInt("type", R.string.two_days_title);
		
		mTabsAdapter = new TabsAdapter(mActivity, mActivity.getViewPager());	
		mTabsAdapter.setActionBarTabChangeListener(this);
		mTabsAdapter.addTab(actionBar.newTab().setText(res.getString(R.string.situation)),
                SituationFragment.class, null);
        mTabsAdapter.addTab(actionBar.newTab().setText(res.getString(R.string.today_title)),
                ForecastFragment.class, bToday);
        mTabsAdapter.addTab(actionBar.newTab().setText(res.getString(R.string.tomorrow_title)),
        		ForecastFragment.class, bTomorrow);
        mTabsAdapter.addTab(actionBar.newTab().setText(res.getString(R.string.two_days_title)),
        		ForecastFragment.class, bTwodays);
	}
	
	private OsmerActivity mActivity;
	private SpinnerAdapter mSpinnerAdapter;
	private int mType;
}
