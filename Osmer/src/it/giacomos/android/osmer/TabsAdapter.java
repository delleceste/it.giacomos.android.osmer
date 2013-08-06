package it.giacomos.android.osmer;

import it.giacomos.android.osmer.guiHelpers.ActionBarTabChangeListener;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;

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
	
	public void clear()
	{
		mViewPager.setOnPageChangeListener(null);
		mTabs.clear();
		notifyDataSetChanged();
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
        return Fragment.instantiate(mActivity.getApplicationContext(), info.clss.getName(), info.args);     
	}

	@Override
	public int getCount() 
	{
		return mTabs.size();
	}

	@Override
	public int getItemPosition(Object object)
	{
		int p;
		if(mTabs.size() == 0)
			p = POSITION_NONE;
		else
			p = super.getItemPosition(object);
		return p;
		
	}
	
	@Override
	public void onPageScrollStateChanged(int arg0) 
	{

	}

	@Override
	public void onPageScrolled(int position, float arg1, int arg2) 
	{

	}

	public void setActionBarTabChangeListener(ActionBarTabChangeListener l)
	{
		mActionBarTabChangeListener = l;
	}
	
	@Override
	public void onPageSelected(int position) 
	{
		if(position < 4 && mActionBar.getNavigationMode() == ActionBar.NAVIGATION_MODE_TABS)
		{
			mViewPager.setCurrentItem(position);
			mActionBar.setSelectedNavigationItem(position);
		}
	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {

	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) 
	{
		Object tag = tab.getTag();
        for (int i = 0; i < mTabs.size(); i++) 
        {
            if (mTabs.get(i) == tag) 
            {
                mActionBarTabChangeListener.onActionBarTabChanged(i);
            }
        }
	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {

	}
}
