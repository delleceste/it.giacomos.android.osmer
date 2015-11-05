package it.giacomos.android.osmer.news;

public class NewsData 
{	
	public NewsData(String date, String time, String text, String url, boolean persistent) 
	{
		mUrl = url;
		mDate = date;
		mTime = time;
		mText = text;
		mPersistent = persistent;
	}

	public String getText()
	{
		return mText;
	}
	
	public String getDate()
	{
		return mDate;
	}
	
	public String getTime()
	{
		return mTime;
	}
	
	public boolean hasTime()
	{
		return !mTime.isEmpty();
	}
	
	public String getUrl()
	{
		return mUrl;
	}
	
	public boolean isPersistent()
	{
		return mPersistent;
	}
	
	private String mUrl, mDate, mTime, mText;
	boolean mPersistent;
}
