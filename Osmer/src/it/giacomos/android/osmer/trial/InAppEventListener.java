package it.giacomos.android.osmer.trial;

public interface InAppEventListener 
{	
	public void onTrialDaysRemaining(int days);
	
	public void onAppPurchased(boolean ok);
}
