package it.giacomos.android.osmer.guiHelpers;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;

public class ActionBarPersonalizer {
	
	public ActionBarPersonalizer(OsmerActivity a)
	{
		mActivity = a;
	}
	
	public void drawerItemChanged(int id)
	{
		Log.i("drawerItemChanged", "invokde with id " + id);
		mSpinnerAdapter = null;
		mOnNavigationListener = null;
		switch(id)
		{
		case 0:
			mSpinnerAdapter = ArrayAdapter.createFromResource(mActivity, R.array.forecast_text_items,
			          android.R.layout.simple_spinner_dropdown_item);
			
			mOnNavigationListener = new OnNavigationListener() {
				  // Get the same strings provided for the drop-down's ArrayAdapter
				  String[] strings = mActivity.getResources().getStringArray(R.array.forecast_text_items);

				  @Override
				  public boolean onNavigationItemSelected(int position, long itemId) 
				  {
				    switch(position)
				    {
				    case 0:
				    	mActivity.switchView(0);
				    	break;
				    case 1:
				    	mActivity.switchView(10);
				    	break;
				    case 2:
				    	mActivity.switchView(11);
				    	break;
				    case 3:
				    	mActivity.switchView(12);
				    	break;
				    }
				    return true;
				  }
				};
			
			break;
			
		case 2:
			mSpinnerAdapter = ArrayAdapter.createFromResource(mActivity, R.array.dailyobs_text_items,
			          android.R.layout.simple_spinner_dropdown_item);
			
			mOnNavigationListener = new OnNavigationListener() {
				  // Get the same strings provided for the drop-down's ArrayAdapter
				  String[] strings = mActivity.getResources().getStringArray(R.array.dailyobs_text_items);

				  @Override
				  public boolean onNavigationItemSelected(int position, long itemId) 
				  {
				    switch(position)
				    {
				    case 0:
				    	mActivity.switchView(30);
				    	break;
				    case 1:
				    	mActivity.switchView(31);
				    	break;
				    case 2:
				    	mActivity.switchView(32);
				    	break;
				    case 3:
				    	mActivity.switchView(33);
				    	break;
				    }
				    return true;
				  }
				};
			
			break;
			
		}
		if(mOnNavigationListener != null && mSpinnerAdapter != null)
			mActivity.getActionBar().setListNavigationCallbacks(mSpinnerAdapter, mOnNavigationListener);
	}
	
	
	private OsmerActivity mActivity;
	private SpinnerAdapter mSpinnerAdapter;
	private OnNavigationListener mOnNavigationListener;
}
