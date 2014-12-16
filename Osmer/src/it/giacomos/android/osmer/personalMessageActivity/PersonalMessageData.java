package it.giacomos.android.osmer.personalMessageActivity;

public class PersonalMessageData 
{
	public String date, message, title;
	
	public boolean blocking;
	
	public PersonalMessageData(String d, String t, String m)
	{
		blocking = false;
		date = d;
		message = m;
		title = t;
	}

	public PersonalMessageData() 
	{
		blocking = false;
		date = "";
		message = "";
		title = "";
	}

}
