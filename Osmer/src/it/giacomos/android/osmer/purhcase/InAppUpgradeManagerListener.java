package it.giacomos.android.osmer.purhcase;

public interface InAppUpgradeManagerListener 
{
	public void onPurchaseComplete(boolean ok, String error, boolean b);
	
	public void onCheckComplete(boolean ok, String error, boolean bought);

	public void onInAppSetupComplete(boolean success, String message);
}
