package it.giacomos.android.osmer.pro.service.sharedData;

public class ReportRequestNotification extends NotificationData
{
	public String datetime, username, locality;
	public boolean isRequest;
	private boolean mValidString;
	
	public boolean isValid()
	{
		return mValidString && latitude > 0 && longitude > 0 && getDate() != null;
	}
	
	public ReportRequestNotification(String input)
	{
		super();
		
		String parts[] = input.split("::", -1);
		mValidString = (parts.length == 7);
		if(mValidString)
		{
			isRequest = (parts[0].compareTo("Q") == 0);
			datetime = parts[2];
			username = parts[3];
			try
			{
				latitude = Double.parseDouble(parts[4]);
				longitude = Double.parseDouble(parts[5]);
			}
			catch(NumberFormatException e)
			{
				
			}
			locality = parts[6];
			makeDate(datetime);
		}
	}

	@Override
	public short getType() {
		
		return NotificationData.TYPE_REQUEST;
	}
}
