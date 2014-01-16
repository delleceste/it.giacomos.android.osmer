package it.giacomos.android.osmer.pro.service.sharedData;

public class ReportNotification extends NotificationData
{
	private boolean mIsValid;
	
	
	public ReportNotification(String input)
	{
		super();
		String parts[] = input.split("::");
		latitude = -1;
		longitude = -1;
		if(parts.length == 5 && parts[0].compareTo("N") == 0)
		{
			try
			{
				latitude = Double.parseDouble(parts[3]);
				longitude = Double.parseDouble(parts[4]);
			}
			catch(NumberFormatException nfe)
			{
				
			}
			username = parts[2];
			datetime = parts[1];
			mIsValid = makeDate(datetime) && latitude > 0 && longitude > 0;
			
		}
		else
			mIsValid = false;
	}

	@Override
	public short getType() {
		return NotificationData.TYPE_REPORT;
	}
	
	public boolean isValid()
	{
		return mIsValid;
	}
}