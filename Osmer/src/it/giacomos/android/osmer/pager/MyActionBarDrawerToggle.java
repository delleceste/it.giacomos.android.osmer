package it.giacomos.android.osmer.pager;

import it.giacomos.android.osmer.pro.R;
import android.app.Activity;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

public class MyActionBarDrawerToggle extends ActionBarDrawerToggle
{
	private final Activity mActivity;
	
	public MyActionBarDrawerToggle(Activity a, DrawerLayout drawerLayout,
			int iconDrawer, int iconDrawerOpen, int iconDrawerClose)
	{
		super(a, drawerLayout, iconDrawer, iconDrawerOpen, iconDrawerClose);
		mActivity = a;
	}
	
	/** Called when a drawer has settled in a completely closed state. */
	public void onDrawerClosed(View view) 
	{
		mActivity.getActionBar().setTitle(mActivity.getActionBar().getTitle());
		mActivity.invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
	}

	/** Called when a drawer has settled in a completely open state. */
	public void onDrawerOpened(View drawerView) 
	{
		mActivity.getActionBar().setTitle(mActivity.getResources().getString(R.string.drawer_open));
		mActivity.invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
	}
}
