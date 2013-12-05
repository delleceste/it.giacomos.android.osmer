package it.giacomos.android.osmer.pro.service.sharedData;

public class ReportRequestNotification extends NotificationData
{
	public String datetime, username, locality;
	private boolean mValidString;
	
	public boolean isValid()
	{
		return mValidString && latitude > 0 && longitude > 0 && getDate() != null;
	}
	
	public ReportRequestNotification(String input)
	{
		super();
		
		String parts[] = input.split("::", -1);
		mValidString = (parts.length == 5);
		if(mValidString)
		{
			datetime = parts[0];
			username = parts[1];
			try
			{
				latitude = Double.parseDouble(parts[2]);
				longitude = Double.parseDouble(parts[3]);
			}
			catch(NumberFormatException e)
			{
				
			}
			locality = parts[4];
			makeDate(datetime);
		}
	}

	@Override
	public short getType() {
		
		return NotificationData.TYPE_REQUEST;
	}
}
