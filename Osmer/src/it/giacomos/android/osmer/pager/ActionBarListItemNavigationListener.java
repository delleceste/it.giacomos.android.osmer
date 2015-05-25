package it.giacomos.android.osmer.pager;

import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.network.state.ViewType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

public class ActionBarListItemNavigationListener implements OnItemSelectedListener
{
	final OsmerActivity mActivity;
	ViewType mMode;

	public ActionBarListItemNavigationListener(OsmerActivity a)
	{
		mActivity = a;
		mMode = ViewType.DAILY_TABLE;
	}

	public void setMode(ViewType m)
	{
		mMode = m;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		if(mMode == ViewType.DAILY_TABLE)
		{
			if (position == 0) {
				mActivity.switchView(ViewType.DAILY_SKY);
			} else if (position == 1) {
				mActivity.switchView(ViewType.DAILY_MIN_TEMP);
			} else if (position == 2) {
				mActivity.switchView(ViewType.DAILY_MEAN_TEMP);
			} else if (position == 3) {
				mActivity.switchView(ViewType.DAILY_MAX_TEMP);
			} else if (position == 4) {
				mActivity.switchView(ViewType.DAILY_HUMIDITY);
			} else if (position == 5) {
				mActivity.switchView(ViewType.DAILY_WIND);
			} else if (position == 6) {
				mActivity.switchView(ViewType.DAILY_WIND_MAX);
			} else if (position == 7) {
				mActivity.switchView(ViewType.DAILY_RAIN);
			}
		}
		else if(mMode == ViewType.LATEST_TABLE)
		{
			if (position == 0) {
				mActivity.switchView(ViewType.LATEST_SKY);
			} else if (position == 1) {
				mActivity.switchView(ViewType.LATEST_TEMP);
			} else if (position == 2) {
				mActivity.switchView(ViewType.LATEST_PRESSURE);
			} else if (position == 3) {
				mActivity.switchView(ViewType.LATEST_WIND);
			} else if (position == 4) {
				mActivity.switchView(ViewType.LATEST_RAIN);
			} else if (position == 5) {
				mActivity.switchView(ViewType.LATEST_SNOW);
			} else if (position == 6) {
				mActivity.switchView(ViewType.LATEST_SEA);
			} else if (position == 7) {
				mActivity.switchView(ViewType.LATEST_HUMIDITY);
			}
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}

}
