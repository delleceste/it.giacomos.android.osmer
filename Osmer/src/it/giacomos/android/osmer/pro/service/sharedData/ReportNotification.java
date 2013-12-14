package it.giacomos.android.osmer.pro.service.sharedData;

public class ReportNotification extends NotificationData
{
	private boolean mIsValid;
	
	
	public ReportNotification(String input)
	{
		super();
		String parts[] = input.split("::");

		if(parts.length == 3 && parts[0].compareTo("N") == 0)
		{
			username = parts[2];
			datetime = parts[1];
			mIsValid = makeDate(datetime);
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
