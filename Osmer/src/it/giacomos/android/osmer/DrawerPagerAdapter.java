package it.giacomos.android.osmer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

public class DrawerPagerAdapter extends FragmentPagerAdapter {

	public DrawerPagerAdapter(FragmentActivity activity, ViewPager pager) 
	{
		super(activity.getSupportFragmentManager());
		Log.e("DrawerPagerAdapter ", "constructor " );
	}

	@Override
	public Fragment getItem(int position) 
	{
		Log.e("DrawerPagerAdapter getItem", "position " + position);
		return null;
	}

	@Override
	public int getCount() 
	{
		return 4;
	}

}
