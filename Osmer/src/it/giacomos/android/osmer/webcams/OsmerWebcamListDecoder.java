package it.giacomos.android.osmer.webcams;


import java.util.ArrayList;
import it.giacomos.android.osmer.locationUtils.LocationNamesMap;
import java.util.regex.*;
import it.giacomos.android.osmer.Regexps;
import it.giacomos.android.osmer.StringType;
import it.giacomos.android.osmer.downloadManager.state.Urls;

public class OsmerWebcamListDecoder implements WebcamListDecoder 
{

	@Override
	public ArrayList<WebcamData> decode(String rawtxt, boolean saveOnCache) 
	{
		ArrayList<WebcamData> wcData = new ArrayList<WebcamData>();
		LocationNamesMap namesMap = new LocationNamesMap();
		
		/* the 3 things we need to build webcam data */
	//	Pattern filenameP = Pattern.compile(Regexps.WEBCAM_FILENAME);
		Pattern locationP = Pattern.compile(Regexps.WEBCAM_LOCATION);
		Pattern textP = Pattern.compile(Regexps.WEBCAM_TEXT);
		String filteredText = "", line = "";
		String [] lines = rawtxt.split("\n");
		String fileName = "";

		int i = 0;
		/* clear the text! */
		while(i < lines.length)
		{
			line = lines[i];
			Matcher locationMatcher = locationP.matcher(line);
	//		Matcher filenameMatcher = filenameP.matcher(line);
			Matcher textMatcher = textP.matcher(line);
			if(locationMatcher.find(0) 
					/* || filenameMatcher.find(0) */
					|| textMatcher.find(0))
				filteredText += line + "\n";
	
			i++;
		}
		
		i = 0;
		
		/* now work on cleaned text */
		lines = filteredText.split("\n");
		while(i + 1 < lines.length )
		{
			WebcamData wd = new WebcamData();
			line = lines[i];
			Matcher locationMatcher = locationP.matcher(line);
			if(locationMatcher.find())
				wd.location = locationMatcher.group(1);
			
			wd.geoPoint = namesMap.get(wd.location);
			
			fileName = wd.location;
			if(!fileName.isEmpty())
				wd.url = Urls.webcamImagesPath() + fileName + ".jpg";
			
			/* next line contains file name */
			line = lines[i + 1];
	//		Matcher filenameMatcher = filenameP.matcher(line);
	//		if(filenameMatcher.find())
	//			fileName = filenameMatcher.group(1);
			
			
			/* next line contains text  */
			line = lines[i + 1];
			Matcher textMatcher = textP.matcher(line);
			if(textMatcher.find())
				wd.text = textMatcher.group(1);
			
			i = i + 2;
			
			/* add valid data only to the list */
			if(!wd.url.isEmpty() && !wd.location.isEmpty() && !wd.text.isEmpty() && wd.geoPoint != null)
				wcData.add(wd);
		}
		if(saveOnCache && wcData.size() > 0) /* cache cleaned file */
			WebcamDataCache.getInstance().saveToCache(filteredText, StringType.WEBCAMLIST_OSMER);
		
		return wcData;
	}

}
