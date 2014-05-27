package it.giacomos.android.osmer.service.sharedData;

public class RainNotification extends NotificationData {

	private boolean mIsValid;
	private boolean mGoingToRain;
	private float mLastDbZ;

	public RainNotification(String text)
	{
		mGoingToRain = false;
		mLastDbZ = 0.0f;
		mIsValid = false;
		
		String [] parts = text.split("::");
		
		if(parts.length > 2 && parts[0].compareTo("R") == 0)
		{
			try
			{
				if(Integer.parseInt(parts[1]) == 1)
				{
					mGoingToRain = true;
					mIsValid = true; /* only alerts are considered valid */
				}
			}
			catch (NumberFormatException nfe)
			{
				/* error in number */
			}
			try
			{
				mLastDbZ = Float.parseFloat(parts[2]);
			}
			catch (NumberFormatException nfe)
			{
				/* error in number */
			}
		}
	}
	
	public float getLastDbZ()
	{
		return mLastDbZ;
	}
	
	public boolean IsGoingToRain()
	{
		return mGoingToRain;
	}
	
	@Override
	public short getType() {
		return TYPE_RAIN;
	}

	@Override
	public boolean isValid() {
		return mIsValid;
	}

	@Override
	public String toString() 
	{

		return null;
	}

}
