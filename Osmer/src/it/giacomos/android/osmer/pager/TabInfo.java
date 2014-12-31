package it.giacomos.android.osmer.pager;

import android.os.Bundle;

public class TabInfo 
{
	public final Class<?> clss;
	public final Bundle args;
	public final CharSequence mTitle;
	public final int mIndicatorColor;
	public final int mDividerColor;

	public TabInfo(Class<?> _class, Bundle _args, CharSequence tit, int indicatorColor, int dividerColor) 
	{
		mTitle = tit;
		mIndicatorColor = indicatorColor;
		mDividerColor = dividerColor;

		clss = _class;
		args = _args;
	}

	/**
	 * @return the title which represents this tab. In this sample this is used directly by
	 * {@link android.support.v4.view.PagerAdapter#getPageTitle(int)}
	 */
	public CharSequence getTitle() {
		return mTitle;
	}

	/**
	 * @return the color to be used for indicator on the {@link SlidingTabLayout}
	 */
	public int getIndicatorColor() {
		return mIndicatorColor;
	}

	/**
	 * @return the color to be used for right divider on the {@link SlidingTabLayout}
	 */
	public int getDividerColor() {
		return mDividerColor;
	}
}