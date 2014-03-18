package it.giacomos.android.osmer.pro.trial;

public interface InAppEventListener 
{	
	public void onTrialDaysRemaining(int days);
	
	public void onAppPurchased(boolean ok);
}
