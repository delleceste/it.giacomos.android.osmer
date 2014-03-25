package it.giacomos.android.osmer.pro.purhcase;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import it.giacomos.android.osmer.pro.purhcase.iabHelper.IabHelper;
import it.giacomos.android.osmer.pro.purhcase.iabHelper.IabResult;
import it.giacomos.android.osmer.pro.purhcase.iabHelper.Inventory;
import it.giacomos.android.osmer.pro.purhcase.iabHelper.Purchase;

public class InAppUpgradeManager implements IabHelper.OnIabPurchaseFinishedListener,
IabHelper.OnIabSetupFinishedListener, IabHelper.QueryInventoryFinishedListener
{
	private IabHelper mIabHelper;
	private int mMode;
	private Activity mActivity;

	public static int MODE_CHECK = 0, MODE_PURCHASE = 1;

	private final String SKU_UNLIMITED = "it.giacomos.android.osmer.unlimited";
	// private final String SKU_UNLIMITED = "android.test.canceled";
	private final String DEVELOPER_PAYLOAD_FOR_UNLIMITED_PURCHASE = "urwpvffdygbva//&bcecfc-3489trrhy451201;.1";
	private final int UNLIMITED_PURCHASE_ID = 420225;

	
	
	private final String[] b = 
		{
		/* 0 */	"kiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0E5",
				"MIIBIjANBgkqh",
				"DdylVTIeMZJ03GBRj7IFs",
		/* 3 */	"IQR6OZdPBntfPtVp141rPIQWi9+MyA",
				"BD788kJLs7YfXb2MPrL0+gfpgWcdgt9Mm1vXJqC6km61DrBhwsQLVYFJcc+",
				"o6sixOveabtJHaisHfBNVJBNnTo7ISiJ4H28G/Thyhy75ZkWWunBiDX",
		/* 6 */	"nyzMKwmUYHtFfGgwN8cd5UT9sts/3JB0fTirxw6GVxM9tuSggOgq7Neo",
				"5gnh1Qh1huMvCsLjJhRXXhF58LDkyMwyTg+Wq5n5AmXOAz5uD1tSdWXSgJXrr+KLbEYzbz0RL",
		/* 8 */	"6yXJ0zVWChWmcwrQ2axNAQV+ZcjMx+a+9JyRyBOMjuocQIDAQAB",
		};

	private String mMakePublicKey()
	{
		String pk = "";
		pk += b[1] + b[0];
		for(int i = 2; i < 6; i++)
			pk += b[i];
		pk += b[7] + b[6];

		return pk + b[1 * 2 * 4];
	}
	
	private ArrayList<InAppUpgradeManagerListener> mInAppUpgradeManagerListeners;

	public InAppUpgradeManager()
	{
		mMode = MODE_CHECK;
		mInAppUpgradeManagerListeners = new ArrayList<InAppUpgradeManagerListener>();
	}

	public void addInAppUpgradeManagerListener(InAppUpgradeManagerListener l)
	{
		mInAppUpgradeManagerListeners.add(l);
	}
	
	public void removeInAppUpgradeManagerListener(InAppUpgradeManagerListener l)
	{
		mInAppUpgradeManagerListeners.remove(l);
	}

	public void dispose()
	{
		mInAppUpgradeManagerListeners.clear();
		if(mIabHelper != null)
			mIabHelper.dispose();
		mIabHelper = null;
	}

	public void purchase(Activity activity)
	{
		mActivity = activity;
		mMode = MODE_PURCHASE;		
		mIabHelper = new IabHelper(activity, mMakePublicKey());
		mIabHelper.startSetup(this);
		Log.e("InAppUpgradeManager.purchase", "startSetup invoketh");
	}

	public void checkIfPurchased(Context context)
	{
		mMode = MODE_CHECK;
		mActivity = null;
		Log.e("InAppUpgradeManager.checkIfPurchased", "checking");
		mIabHelper = new IabHelper(context, mMakePublicKey());
		mIabHelper.startSetup(this);
	}

	@Override
	public void onQueryInventoryFinished(IabResult result, Inventory inv) 
	{
		boolean purchased = false;
		boolean success = result.isSuccess();
		String message = result.getMessage();

		purchased = (success && inv.hasPurchase(SKU_UNLIMITED));
		
		for(InAppUpgradeManagerListener l : mInAppUpgradeManagerListeners)
			l.onCheckComplete(success, message, purchased);


		/* looking at IabHelper code, it should be safe to dispose the IabHelper here */
		if(mIabHelper != null)
			mIabHelper.dispose();
		mIabHelper = null;
	}

	@Override
	public void onIabSetupFinished(IabResult result) 
	{
		boolean success = result.isSuccess();
		if(!success)
		{
			String message = result.getMessage();
			Log.e("onIabSetupFinished", "result failed " + message);
			for(InAppUpgradeManagerListener l : mInAppUpgradeManagerListeners)
				l.onInAppSetupComplete(success, message);
		}
		else
		{
			if(mMode == MODE_PURCHASE)
			{
				mIabHelper.launchPurchaseFlow(mActivity, 
						SKU_UNLIMITED, UNLIMITED_PURCHASE_ID, 
						this, DEVELOPER_PAYLOAD_FOR_UNLIMITED_PURCHASE);
			}
			else if(mMode == MODE_CHECK)
			{
				mIabHelper.queryInventoryAsync(this);
			}
		}
	}

	@Override
	public void onIabPurchaseFinished(IabResult result, Purchase purchase) 
	{
		boolean success = result.isSuccess();
		boolean purchased = false;
		String msg = result.getMessage();
		if(success)
		{
			purchased = (purchase.getSku().equals(SKU_UNLIMITED) 
					&& purchase.getDeveloperPayload().equals(DEVELOPER_PAYLOAD_FOR_UNLIMITED_PURCHASE)); /* good */
			if(!purchased)
				msg = msg += "\nBad params.";
		}

		for(InAppUpgradeManagerListener l : mInAppUpgradeManagerListeners)
			l.onPurchaseComplete(success, msg, purchased);
		
		/* looking at IabHelper code, it should be safe to dispose the IabHelper here */
		mIabHelper.dispose();
		mIabHelper = null;
	}
	
}

