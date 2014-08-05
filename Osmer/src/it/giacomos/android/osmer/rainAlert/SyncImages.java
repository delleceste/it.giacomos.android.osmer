package it.giacomos.android.osmer.rainAlert;

import it.giacomos.android.osmer.network.state.Urls;
import it.giacomos.android.osmer.widgets.map.animation.FileHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class SyncImages
{
	private String mErrorMsg = "";
	private static final String CLI = "afe0983der38819073rxc1900lksjd";

	public SyncImages()
	{
	}
	
	/**
	 * This method queries the server about the last two radar image filenames.
	 * If one or both files already exist locally, nothing is done. Otherwise, the
	 * missing files are downloaded from the server and saved locally.
	 * 
	 * @return filenames an array of two strings. The first string is the path of the previous 
	 * file, the second is the path of the last file.
	 */
	public String[] sync(String url, String cacheDir) 
	{
		boolean file1Success = true, file0Success = true;
		String document = "";
		String[] filepaths = null;
		HttpClient httpClient = new DefaultHttpClient();
        HttpPost request = new HttpPost(url);
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("cli", CLI));
        /* test! */
        postParameters.add(new BasicNameValuePair("before_datetime", "2014-07-30 02:41:00"));
        
        UrlEncodedFormEntity form;
		try {
			form = new UrlEncodedFormEntity(postParameters);
	        request.setEntity(form);
	        HttpResponse response = httpClient.execute(request);
	        StatusLine statusLine = response.getStatusLine();
	        if(statusLine.getStatusCode() < 200 || statusLine.getStatusCode() >= 300)
	        	mErrorMsg = statusLine.getReasonPhrase();
	        else if(statusLine.getStatusCode() < 0)
	        	mErrorMsg = "Server error";
	        /* check the echo result */
	        HttpEntity entity = response.getEntity();
	        document = EntityUtils.toString(entity);
	        /* test if the document contains two "->" */
	        if(document.length() - document.replace("->", "").length() == 4)
	        {
	        	String [] lines = document.split("\n");
	        	/* previous image */
	        	String date1 = lines[0].split("->")[0];
	        	String remoteFilePath1 = lines[0].split("->")[1];
	        	/* last image */
	        	String date0 = lines[1].split("->")[0];
	        	String remoteFilePath0 = lines[1].split("->")[1];
	        	
	        	FileHelper fileHelper = new FileHelper();
	        	String file1 = "radar-" + remoteFilePath1.substring(remoteFilePath1.lastIndexOf('/') + 1);
	        	String file0 = "radar-" + remoteFilePath0.substring(remoteFilePath0.lastIndexOf('/') + 1);
	        	if(!fileHelper.exists(file1, cacheDir))
	        		/* try to download it */
	        		file1Success = SaveImage(remoteFilePath1, file1, cacheDir);
	        	if(!fileHelper.exists(file0, cacheDir))
	        		file0Success = SaveImage(remoteFilePath0, file0, cacheDir);
	        	
	        	/* if file0 or file1 already exist, then the file1Success and file0Success retain their 'true'
	        	 * value from initialization at the beginning of the method.
	        	 */
	        	if(file1Success && file0Success)
	        	{
	        		/* successfully downloaded and saved images into storage */
	        		filepaths = new String[2];
	        		filepaths[0] = file1;
	        		filepaths[1] = file0;
	        		/* remove unneeded files left over by precedent downloads */
	        		ArrayList<String> neededFiles = new ArrayList<String>();
	        		neededFiles.add(file1);
	        		neededFiles.add(file0);
	        		int removed = removeUnneededFiles(neededFiles, cacheDir);
	        		Log.e("SyncImages.doInBackground", "Successfully saved " + file1 + " and " + 
	        				file0 + " into " + cacheDir + " and removed files " + removed);
	        	}
	        	else
	        	{
	        		Log.e("SyncImages.doInBackground", "FAILED to save " + file1 + " and " + file0 + " into " + cacheDir);
	        	}
	        }
	        
		} 
		catch (UnsupportedEncodingException e) 
		{
			mErrorMsg = e.getLocalizedMessage();
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			mErrorMsg = e.getLocalizedMessage();
			e.printStackTrace();
		} catch (IOException e) {
			mErrorMsg = e.getLocalizedMessage();
			e.printStackTrace();
		}
		return filepaths;
	}

	private int removeUnneededFiles(ArrayList<String> needed, String cacheDirPath)
	{
		boolean deleted;
		int removed = 0;
		File dir = new File(cacheDirPath);
		File [] files = dir.listFiles();
		for(File file : files)
		{
			String fName = file.getName();
			if(fName.startsWith("radar-")  && !needed.contains(fName))
			{
				deleted = file.delete();
				if(deleted)
				{
						Log.e("SyncImages.removeUnneededFiles", "successfully removed unneeded file " + fName);
						removed++;
				}
				else
					Log.e("SyncImages.removeUnneededFiles", "failed to delete unneeded file " + fName);
			}	
			
		}
		return removed;
	}
	
	private boolean SaveImage(String relativePath, String outFileName, String cacheDirP)
	{
		FileHelper fileHelper = new FileHelper();
		try
		{
			try{
				int nRead;
				/* get radar image */
				URL url = new URL(new Urls().radarHistoricalImagesFolderUrl() + "/" + relativePath);
				InputStream inputStream = (InputStream) url.getContent();
				/* get bytes from input stream */
				ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
				byte [] bytes = new byte[1024];
				while ((nRead = inputStream.read(bytes, 0, bytes.length)) != -1) {
					byteBuffer.write(bytes, 0, nRead);
				}
				byteBuffer.flush();
				/* back to bytes again */
				bytes = byteBuffer.toByteArray();
				if(!fileHelper.storeRadarImage(outFileName, bytes, cacheDirP))
				{
					mErrorMsg = "Error saving image on external storage " +
							fileHelper.getErrorMessage();
					Log.e("SyncImages.doInBackground", "failed to save " + outFileName + " on external storage: " + fileHelper.getErrorMessage());
				}
				else
					return true;
			}
			catch(MalformedURLException e)
			{
				mErrorMsg = "Malformed URL: \"" + relativePath + "\":\n\"" + e.getLocalizedMessage() + "\"";
				Log.e("SyncImages.doInBackground", "Malformed URL" + mErrorMsg);
			}

		}
		catch(IOException e)
		{
			mErrorMsg = "IOException: URL: \"" + relativePath + "\":\n\"" + e.getLocalizedMessage() + "\"";
		}
		return false;
	}
	
}
