package it.giacomos.android.osmer.pager;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import it.giacomos.android.osmer.OsmerActivity;

public class MyActionBarDrawerToggle extends ActionBarDrawerToggle
{
	private final AppCompatActivity mActivity;
	
	public MyActionBarDrawerToggle(OsmerActivity osmerActivity, DrawerLayout drawerLayout,
			int iconDrawer, int iconDrawerOpen, int iconDrawerClose)
	{
		super(osmerActivity, drawerLayout, iconDrawer, iconDrawerOpen);
		mActivity = osmerActivity;
	}
	
	/** Called when a drawer has settled in a completely closed state. */
	public void onDrawerClosed(View view) 
	{
		mActivity.supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
	}

	/** Called when a drawer has settled in a completely open state. */
	public void onDrawerOpened(View drawerView) 
	{
		mActivity.supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
	}
}
