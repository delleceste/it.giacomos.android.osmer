package it.giacomos.android.osmer;

import it.giacomos.android.osmer.guiHelpers.ActionBarTabChangeListener;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;

public class TabsAdapter  extends FragmentPagerAdapter
implements ActionBar.TabListener, ViewPager.OnPageChangeListener
{

	private final ActionBar mActionBar;
	private final ViewPager mViewPager;
	private final OsmerActivity mActivity;
	private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
	private ActionBarTabChangeListener mActionBarTabChangeListener;


	static final class TabInfo {
		private final Class<?> clss;
		private final Bundle args;

		TabInfo(Class<?> _class, Bundle _args) {
			clss = _class;
			args = _args;
		}
	}
	
	public TabsAdapter(FragmentActivity activity, ViewPager pager) {
		super(activity.getSupportFragmentManager());
		mActivity = (OsmerActivity) activity;
		mActionBar = activity.getActionBar();
		mViewPager = pager;
		mViewPager.setAdapter(this);
		mViewPager.setOnPageChangeListener(this);
	}

	public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
		TabInfo info = new TabInfo(clss, args);
		tab.setTag(info);
		tab.setTabListener(this);
		mTabs.add(info);
		mActionBar.addTab(tab);
		notifyDataSetChanged();
	}

	@Override
	public Fragment getItem(int position) 
	{    
        TabInfo info = mTabs.get(position);
		Log.e("getItem", "getting position " + position + " tabs size " + mTabs.size() + " info " + info + " name "
				+ info.clss.getName());
		Log.e("getItem", "instantiating " + info.clss.getName() + " int type from args " + info.args.getInt("type"));
        return Fragment.instantiate(mActivity.getApplicationContext(), info.clss.getName(), info.args);     
	}

	@Override
	public int getCount() 
	{
		return mTabs.size();
	}

	@Override
	public void onPageScrollStateChanged(int arg0) 
	{

	}

	@Override
	public void onPageScrolled(int position, float arg1, int arg2) 
	{
//		if(position < 3)
//			mActionBar.setSelectedNavigationItem(position);
	}

	public void setActionBarTabChangeListener(ActionBarTabChangeListener l)
	{
		mActionBarTabChangeListener = l;
	}
	
	@Override
	public void onPageSelected(int position) 
	{
		Log.e("onPageSelected", "position " + position);
		if(position < 4 && mActionBar.getNavigationMode() == ActionBar.NAVIGATION_MODE_TABS)
			mActionBar.setSelectedNavigationItem(position);
	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {

	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction arg1) 
	{
		Object tag = tab.getTag();
		ViewType viewType = ViewType.HOME;
        for (int i = 0; i < mTabs.size(); i++) 
        {
            if (mTabs.get(i) == tag) 
            {
            	Log.e("onTabSelected", "calling setCurrentItem on view Pager: " + i);
             //   mViewPager.setCurrentItem(i);
                if(i == 1) /* i == 0 -> ViewType.HOME, but already initialized */
                	viewType = ViewType.TODAY;
                else if(i == 2)
                	viewType = ViewType.TOMORROW;
                else if(i == 3)
                	viewType = ViewType.TWODAYS;
                
                mActivity.switchView(viewType);
                /* ActionBarPersonalizer is an ActionBarTabChangedListener and 
                 * sets the tab index on the ActionBarStateManager in order to
                 * be able to restore the tab index through screen orientation changes.
                 */
                mActionBarTabChangeListener.onActionBarTabChanged(i);
            }
        }
	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {

	}
}
