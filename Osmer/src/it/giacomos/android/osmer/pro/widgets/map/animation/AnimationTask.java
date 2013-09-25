package it.giacomos.android.osmer.pro.widgets.map.animation;

import it.giacomos.android.osmer.pro.network.state.Urls;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import android.os.AsyncTask;
import android.util.Log;

public class AnimationTask extends AsyncTask <URL, Integer, Integer>
{
	private String mExternalStorageDirPath;
	private String m_errorMessage;
	private String mDownloadUrls;
	private AnimationTaskListener mAnimationTaskListener;
	private int mTotSteps;

	public AnimationTask(AnimationTaskListener atl, String externalStorageDirPath)
	{
		mAnimationTaskListener = atl;
		mTotSteps = 0;
		mDownloadUrls = "";
		mExternalStorageDirPath = externalStorageDirPath;
	}

	public void setDownloadUrls(String doc)
	{
		mDownloadUrls = doc;
	}

	@Override
	protected Integer doInBackground(URL... urls) 
	{
		int stepCnt = 0;
		m_errorMessage = "";
		InputStream inputStream;
		byte [] bytes;
		int nRead;
		URL imgUrl;
		Urls myurls = new Urls();
		String surl; /* url as string */
		int removedFilesCount;
		
		FileHelper fileHelper = new FileHelper();
		if(!fileHelper.isExternalFileSystemDirReadableWritable())
		{
			m_errorMessage = "External storage unavailable for read/write";
			mTotSteps = 0;
			publishProgress(0);
			/* leave function */
			return 0;
		}
		
		mTotSteps = 1; /* 1 step is to download the url list text file */
		if(mDownloadUrls.isEmpty())
		{
			String line;
			m_errorMessage = "";
			if(urls.length == 1)
			{
				URLConnection urlConnection = null;
				try {
					urlConnection = urls[0].openConnection();
					inputStream = urlConnection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
					while((line = reader.readLine()) != null)
					{
						mTotSteps++;
						mDownloadUrls += line + "\n";
					}
				}
				catch (IOException e) 
				{
					mDownloadUrls = null;
					m_errorMessage = "IOException: URL: \"" + urls[0].toString() + "\":\n\"" + e.getLocalizedMessage() + "\"";
					mTotSteps = 0;
					publishProgress(0);
					return 0;
				}
			}
		}
		stepCnt = 1;
		/* progress == 1 means text file downloaded */
		this.publishProgress(stepCnt);
		
		String [] lines = mDownloadUrls.split("\n");
		String [] filenames = new String[lines.length];
		for(int i = 0; i < lines.length; i++)
		{
			if(lines[i].contains("->"))
			{
				String [] parts = lines[i].split("->");
				filenames[i] = parts[1];
			}
		}
		
		/* remove unnecessary files on external storage, that were previously 
		 * downloaded
		 */
		ArrayList<String> neededFiles = new ArrayList<String>(Arrays.asList(filenames));
		removedFilesCount = fileHelper.removeUnneededFiles(neededFiles, mExternalStorageDirPath);
		Log.e("AnimationTask.doInBackground", "removed unneeded files " + removedFilesCount);
		
		for(String fName : filenames)
		{
			if(fileHelper.exists(fName, mExternalStorageDirPath))
			{
				/* file exists, no need to download it */
				stepCnt++;
			}
			else
			{
				surl = myurls.radarHistoricalImagesFolderUrl() + fName;
				/* download it and save on file */
				try
	        	{
					try{
						imgUrl = new URL(surl);
		        		inputStream = (InputStream) imgUrl.getContent();
		        		/* get bytes from input stream */
		        		ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
		        		bytes = new byte[1024];
		        		while ((nRead = inputStream.read(bytes, 0, bytes.length)) != -1) {
		        			byteBuffer.write(bytes, 0, nRead);
		        		}
		        		byteBuffer.flush();
		        		/* back to bytes again */
		        		bytes = byteBuffer.toByteArray();
		        		if(!fileHelper.storeRadarImage(fName, bytes, mExternalStorageDirPath))
		        		{
		        			m_errorMessage = "Error saving image on external storage " +
		        					fileHelper.getErrorMessage();
		        			Log.e("AnimationTask.doInBackground", "failed to save " + fName + " on external storage: " + fileHelper.getErrorMessage());
		        			
		        			break;
		        		}
		        		else
		        			Log.e("AnimationTask.doInBackground", "got and saveth " + surl + "[" + stepCnt + "/" + mTotSteps + "]");
		        		
		        		/* increment stepCnt */
		        		stepCnt++;
					}
					catch(MalformedURLException e)
					{
						m_errorMessage = "Malformed URL: \"" + surl + "\":\n\"" + e.getLocalizedMessage() + "\"";
						Log.e("AnimationTask.doInBackground", "Malformed URL" + m_errorMessage);
						/* leave on error */
						break;
					}
					
	        	}
	        	catch(IOException e)
	        	{
	        		m_errorMessage = "IOException: URL: \"" + surl + "\":\n\"" + e.getLocalizedMessage() + "\"";
	        		Log.e("AnimationTask.doInBackground", m_errorMessage);
	        		/* leave on error */
	        		break;
	        	}
			}
			
			publishProgress(stepCnt);
		}
		

		return stepCnt;
	}

	protected void onProgressUpdate(Integer... progress) 
	{
		mAnimationTaskListener.onProgressUpdate(progress[0], mTotSteps);
		if(progress[0] == 1)
			mAnimationTaskListener.onUrlsReady(mDownloadUrls);

		/* calculate the step at which the animation can start */
		if(progress[0] > 0.4f * (float) mTotSteps)
		{
			Log.e("AnimationTask.onProgressUpdate", "can start animation " + progress[0] + " > " + 0.4f * (float) mTotSteps);
			mAnimationTaskListener.animationCanStart();
		}
	}

	protected void onPostExecute(Integer result) 
	{
		if(result == 0) /* error */
			mAnimationTaskListener.onDownloadError(m_errorMessage);
		else if(result < mTotSteps)
			mAnimationTaskListener.onDownloadError(m_errorMessage);
		else
			mAnimationTaskListener.onDownloadComplete();
	}

	public void onCancelled(Integer res)
	{

	}

}
