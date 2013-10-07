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

public class AnimationTask extends AsyncTask <String, Integer, Integer>
{
	private String mExternalStorageDirPath;
	private String m_errorMessage;
	private String mDownloadUrls;
	private AnimationTaskListener mAnimationTaskListener;
	private int mTotSteps;

	public AnimationTask(String externalStorageDirPath)
	{
		mAnimationTaskListener = null;
		mTotSteps = 0;
		mDownloadUrls = "";
		mExternalStorageDirPath = externalStorageDirPath;
	}

	void setAnimationTaskListener(AnimationTaskListener atl)
	{
		mAnimationTaskListener = atl;
	}

	public void setDownloadUrls(String doc)
	{
		mDownloadUrls = doc;
	}

	public String getDownloadUrls()
	{
		return mDownloadUrls;
	}

	@Override
	protected Integer doInBackground(String... urls) 
	{
		int stepCnt = 0;
		m_errorMessage = "";
		InputStream inputStream;
		byte [] bytes;
		int nRead;
		URL url;
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
					url = new URL(urls[0]);
					urlConnection = url.openConnection();
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
					if(!isCancelled())
						publishProgress(0);
					return 0;
				}
			}
			Log.e("AnimationTask", "downloaded text file for URLS - totSteps "  + mTotSteps);
		}
		else
		{
			mTotSteps += mDownloadUrls.length() - mDownloadUrls.replace("\n", "").length();
			Log.e("AnimationTask", "no need to download text file for URLS - totSteps " + mTotSteps);
		}

		stepCnt = 1;
		/* progress == 1 means text file downloaded */
		if(!isCancelled())
			this.publishProgress(stepCnt);
		else
			return stepCnt;

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

//				if(stepCnt == 8)
//				{
//					Log.e("AnimationTask", "sleeping on stepCnt 8");
//					int i = 0;
//					while(i < 16)
//					{
//						try {
//							if(isCancelled())
//							{
//								Log.e("ANimationTask.doInBackground", "task cancelled while sleeping!");
//								break;
//							}
//							Thread.sleep(1000);
//							Log.e("AnimationTask", "slept " + i + "secs!");
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						i++;
//					}
//				}

			}
			else
			{
				surl = myurls.radarHistoricalImagesFolderUrl() + fName;
				/* download it and save on file */
				try
				{
					try{
						url = new URL(surl);
						inputStream = (InputStream) url.getContent();
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

						/* if the task is cancelled, return before incrementing stepCnt and publishProgress, because the task
						 * may have been cancelled on screen rotation. This ensures that the current step is not taken into account
						 * (can be incomplete if the activity is destroyed here) when the activity is restarted.
						 */
						if(isCancelled())
							return stepCnt;

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
			if(!isCancelled())
				publishProgress(stepCnt);
			else
			{
				Log.e("AnimationTask.doInBackground", "task cancelled during for at fName " + fName);
				break;
			}
		}
		return stepCnt;
	}

	protected void onProgressUpdate(Integer... progress) 
	{
		if(mAnimationTaskListener != null)
		{
			mAnimationTaskListener.onProgressUpdate(progress[0], mTotSteps);
			if(progress[0] == 1)
				mAnimationTaskListener.onUrlsReady(mDownloadUrls);
		}
	}

	protected void onPostExecute(Integer result) 
	{
		if(mAnimationTaskListener == null)
			return;
		if(result == 0) /* error */
			mAnimationTaskListener.onDownloadError(m_errorMessage);
		else if(result < mTotSteps)
			mAnimationTaskListener.onDownloadError(m_errorMessage);
		else
			mAnimationTaskListener.onDownloadComplete();
	}

	public void onCancelled(Integer step)
	{ 
		if(mAnimationTaskListener == null)
			return;
		Log.e("AnimationTask.onCancelled", "task was cancelled at step " + step);
		mAnimationTaskListener.onTaskCancelled();
	}

}
