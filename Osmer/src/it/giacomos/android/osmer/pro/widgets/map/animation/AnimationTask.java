package it.giacomos.android.osmer.pro.widgets.map.animation;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import android.os.AsyncTask;

public class AnimationTask extends AsyncTask <URL, Integer, Long>
{
	private String m_errorMessage;
	private AnimationTaskListener mAnimationTaskListener;
	private int mTotSteps;

	public AnimationTask(AnimationTaskListener atl)
	{
		mAnimationTaskListener = atl;
		mTotSteps = 0;
	}
	
	@Override
	protected Long doInBackground(URL... urls) 
	{
		long stepsCnt = 0;
		int linesCnt = 0;
		String doc = "";
		String line;
		m_errorMessage = "";
		InputStream inputStream;
		if(urls.length == 1)
		{
			URLConnection urlConnection = null;
			try {
				urlConnection = urls[0].openConnection();
				inputStream = urlConnection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				while((line = reader.readLine()) != null)
				{
					linesCnt++;
					doc += line + "\n";
				}
				
			}
			catch (IOException e) 
			{
				doc = null;
				m_errorMessage = "IOException: URL: \"" + urls[0].toString() + "\":\n\"" + e.getLocalizedMessage() + "\"";
			}
		}
		
		return stepsCnt;
	}
	
	protected void onProgressUpdate(Integer... progress) 
	{
		mAnimationTaskListener.onProgressUpdate(progress[0], mTotSteps);
    }

    protected void onPostExecute(Long result) 
    {
    	
    }

    public void onCancelled(Long res)
	{
		
	}

}
