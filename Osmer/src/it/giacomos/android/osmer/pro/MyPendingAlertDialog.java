package it.giacomos.android.osmer.pro;

import java.util.HashMap;

import android.support.v4.app.FragmentActivity;


public class MyPendingAlertDialog 
{
	
	private MyAlertDialogType mType;
	private int mMsgId;
	
	public MyPendingAlertDialog(MyAlertDialogType t, int id)
	{
		mType = t;
		mMsgId = id;
	}
	
	public boolean isShowPending()
	{
		return mMsgId > -1;
	}
	
	public void showPending(FragmentActivity a)
	{
		if(mType == MyAlertDialogType.ERROR)
			MyAlertDialogFragment.MakeGenericError(mMsgId, a);
		else if(mType == MyAlertDialogType.INFO)
			MyAlertDialogFragment.MakeGenericInfo(mMsgId, a);
		
		mMsgId = -1; /* cancels isShowPending */
	}
	
}
