package it.giacomos.android.osmer.pro.purhcase;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
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
			"MIIBIjANBgkqh",
			"kiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0E5",
			"DdylVTIeMZJ03GBRj7IFs",
			"IQR6OZdPBntfPtVp141rPIQWi9+MyA",
			"BD788kJLs7YfXb2MPrL0+gfpgWcdgt9Mm1vXJqC6km61DrBhwsQLVYFJcc+",
			"o6sixOveabtJHaisHfBNVJBNnTo7ISiJ4H28G/Thyhy75ZkWWunBiDX",
			"5gnh1Qh1huMvCsLjJhRXXhF58LDkyMwyTg+Wq5n5AmXOAz5uD1tSdWXSgJXrr+KLbEYzbz0RL",
			"nyzMKwmUYHtFfGgwN8cd5UT9sts/3JB0fTirxw6GVxM9tuSggOgq7Neo",
			"6yXJ0zVWChWmcwrQ2axNAQV+ZcjMx+a+9JyRyBOMjuocQIDAQAB",
			
			/* 0 */	"AAOCAQ8AMIIBCgKCAQEAj", 
			/* 1 */		"MIIBIjANBgkqhkiG9w0BAQEF", 
			/* 2 */		"N9TZt", 
			/* 3 */		"Xx",
			/* 4 */		"TfTeEJzYfAdz1ldhqq", 
			/* 05 */		"+ppc", 
			/* 6 */		"DAQAB", 
			/* 7 */		"dmXnDa3hztspJDTcE1fabE/",
			/* 8 */			"Gpi7A7J8Jw6yOxtflT",
			/* 9 */			"Vxpz2fnMjXkSwnUXFiXyDg0TgJ", 
			/* 10 */			"ZCOkeQtomkwEVxODG56T", 
			/* 11 */	"brmIBwR7gkXEh2qDMD", 
			/* 12 */	"31A4w", 
			/* 13 */	"EsMtXDrc0TKoKewdHYoQfOt", 
			/* 14 */	"6NUMNsRl16gWKOVBHWlFXb",
			/* 15 */	"MioWeEI2+LO6FI", 
			/* 16 */	"jeJUtzGO", 
			/* 17 */	"AN", 
			/* 18 */	"iajfsyKeg6VzU",
			/* 19 */	"8tBeZ7OjfKoNlq9",
			/* 20 */	"lji/lwP1q7H2j", 
			/* 21 */	"Z8i", 
			/* 22 */	"ydgfRckqFAI", 
			/* 23 */	"6aFuBV+k4F", 
			/* 24 */	"YwqpqtpXLaremtK1k8zYiS+", 
			/* 25 */	"Cs", 
			/* 26 */	"I8M",
			/* 27 */	"1nMKVQ9uOWqle3EaQvmme",
			/* 28 */	"g4GQI", 
			/* 29 */	"1nKFy0i0thSvnG/", 
		};

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
		if(mIabHelper != null)
			mIabHelper.dispose();
		mIabHelper = null;
	}

	private String mMakePublicKey()
	{
		String pk = "";
		pk += b[1] + b[0] + b[7 * 2] +
				b[2] + b[22] + b[27];
		for(int i = 3; i < 6; i++)
			pk += b[i];
		for(int i = 7; i < 13; i++)
			pk += b[i];
		pk += b[24];
		for(int i = 13; i < 17; i++)
		{
			if( i != 14)
				pk += b[i];
		}
		pk += b[29];
		for(int i = 17; i <= 21; i++)
			pk += b[i];
		pk += b[23];
		int i = 25;
		while (i < 27)
		{
			pk += b[i];
			i++;
		}
		pk += b[28];

		return pk + b[6];
	}

	public void purchase(Activity activity)
	{
		mActivity = activity;
		mMode = MODE_PURCHASE;		
		mIabHelper = new IabHelper(activity, mMakePublicKey());
		mIabHelper.startSetup(this);
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

