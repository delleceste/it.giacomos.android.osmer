package it.giacomos.android.osmer.pro.reportDataService;

public class ReportRequestNotification {

	public String datetime, username, locality;
	double latitude = -1, longitude = -1;
	private boolean mValidString;
	
	public boolean isValid()
	{
		return mValidString && latitude > 0 && longitude > 0;
	}
	
	public ReportRequestNotification(String input)
	{
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
		}
	}
}
