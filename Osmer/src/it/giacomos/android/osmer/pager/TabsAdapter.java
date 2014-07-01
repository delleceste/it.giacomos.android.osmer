package it.giacomos.android.osmer.pager;

import it.giacomos.android.osmer.OsmerActivity;

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

public class TabsAdapter  extends FragmentPagerAdapter
implements ActionBar.TabListener, ViewPager.OnPageChangeListener
{

	private final ActionBar mActionBar;
	private final ViewPager mViewPager;
	private final OsmerActivity mActivity;
	private boolean mEnabled;
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

	public void enable() 
	{
		mEnabled = true;
		mViewPager.setOnPageChangeListener(this);	
		notifyDataSetChanged();
	}
	
	public void disable()
	{
		mEnabled = false;
		mViewPager.setOnPageChangeListener(null);
		notifyDataSetChanged();
	}
	
	public TabsAdapter(FragmentActivity activity, ViewPager pager) {
		super(activity.getSupportFragmentManager());
		mActivity = (OsmerActivity) activity;
		mActionBar = activity.getActionBar();
		mViewPager = pager;
		mViewPager.setAdapter(this);
		mViewPager.setOnPageChangeListener(this);
		mEnabled = false;
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
//		Log.e("getItem", "position is " + position);
        return Fragment.instantiate(mActivity.getApplicationContext(), info.clss.getName(), info.args);     
	}

	@Override
	public int getCount() 
	{
		if(mEnabled)
			return mTabs.size();
		else
			return 0;
	}

	@Override
	public int getItemPosition(Object object)
	{
		if(!mEnabled)
			return POSITION_NONE;
		else
			return super.getItemPosition(object);
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
		if(position < mTabs.size() && mActionBar.getNavigationMode() == ActionBar.NAVIGATION_MODE_TABS)
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
