package it.giacomos.android.osmer.guiHelpers;

import it.giacomos.android.osmer.OsmerActivity;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.observations.ObservationTime;
import it.giacomos.android.osmer.observations.ObservationType;

import java.util.ArrayList;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewParent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;

public class ToggleButtonGroupHelper {

	public ToggleButtonGroupHelper(OsmerActivity a)
	{
		mActivity = a;
		mIdList = new ArrayList<Integer>();
	}
	
	public void addButton(int id)
	{
		mIdList.add(id);
	}
	
	public boolean isOn(int id)
	{
		for(int i = 0; i < mIdList.size(); i++)
		{
			if(mIdList.get(i) == id)
				return ((CompoundButton) mActivity.findViewById(id)).isChecked();
		}
		return false;
	}
	
	public int buttonOn()
	{
		for(int i = 0; i < mIdList.size(); i++)
		{
			CompoundButton cb = (CompoundButton) mActivity.findViewById(mIdList.get(i));
			if(cb.isChecked())
				return mIdList.get(i);
		}
		return -1;
	}
	
	public void setClicked(View v) {
		
		// TODO Auto-generated method stub
	//	Log.e("onCheckChanged " + v.getId(), "checked " + ((CompoundButton) v).isChecked());
		for(int i = 0; i < mIdList.size(); i++)
		{
			if(mIdList.get(i) != v.getId())
			{
				CompoundButton cb = (CompoundButton) mActivity.findViewById(mIdList.get(i));
				cb.setChecked(false);
			}
		}
		CompoundButton cb = (CompoundButton) v;
		cb.setChecked(true);
	}
	
	public void saveButtonsState(Bundle b)
	{
		ArrayList<Integer> checkedButtons = new ArrayList<Integer>();
		for(int i = 0; i < mIdList.size(); i++)
		{
			CompoundButton cb = (CompoundButton) mActivity.findViewById(mIdList.get(i));
			if(cb.isChecked())
				checkedButtons.add(mIdList.get(i));
		}
		b.putIntegerArrayList("checkedButtons", checkedButtons);
	}
	
	public void restoreButtonsState(Bundle b)
	{
		ArrayList<Integer> checkedButtons = b.getIntegerArrayList("checkedButtons");
		Log.e("ToggleButtonGroupHelper:", "buttons checked " + checkedButtons.size());
		for(int i = 0; i < checkedButtons.size(); i++)
		{
			Log.e("settingClicked", "id: " + checkedButtons.get(i));
			CompoundButton bt = (CompoundButton) mActivity.findViewById(checkedButtons.get(i));
			View  par = (View) bt.getParent();
			if(par.getId() == R.id.RelativeLayoutButtonsPageDailyObs)
			{
				Log.e("restoreButtonsState", "parent is daily obs");
				mActivity.onClick(mActivity.findViewById(R.id.buttonMap));
				mActivity.onClick(mActivity.findViewById(R.id.buttonDailyObs));
			}
			else if(par.getId() == R.id.RelativeLayoutButtonsPageLatestObs)
			{
				Log.e("restoreButtonsState", "parent is latest obs");
				mActivity.onClick(mActivity.findViewById(R.id.buttonMap));
				mActivity.onClick(mActivity.findViewById(R.id.buttonLastObs));
			}
			else
				Log.e("restoreButtonsState", "no parent interesting");
			
			this.setClicked(mActivity.findViewById(checkedButtons.get(i)));
			mActivity.onClick(mActivity.findViewById(checkedButtons.get(i)));
		}
	}
	
	private ArrayList<Integer> mIdList;
	private OsmerActivity mActivity;


}
