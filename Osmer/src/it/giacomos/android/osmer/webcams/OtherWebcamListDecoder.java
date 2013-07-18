package it.giacomos.android.osmer.webcams;


import it.giacomos.android.osmer.ViewType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.android.maps.GeoPoint;

import android.text.Html;
import android.util.Log;

public class OtherWebcamListDecoder implements WebcamListDecoder 
{

	@Override
	public ArrayList<WebcamData> decode(String rawData, boolean saveOnCache) 
	{
		ArrayList<WebcamData> wcData = new ArrayList<WebcamData>();
		Document dom;
		DocumentBuilderFactory factory;
		DocumentBuilder builder;
		InputStream is;
		factory = DocumentBuilderFactory.newInstance();
		try {
			builder = factory.newDocumentBuilder();
			try 
			{
				is = new ByteArrayInputStream(rawData.getBytes("UTF-8"));
				try 
				{
					dom = builder.parse(is);
					NodeList markerNodes = dom.getElementsByTagName("marker");
					for(int i = 0; i < markerNodes.getLength(); i++)
					{
						Element marker = (Element) markerNodes.item(i);
						if(marker != null)
						{
							WebcamData wd = new WebcamData();
							wd.location = marker.getAttribute("nome");
							wd.text = Html.fromHtml(marker.getAttribute("testo")).toString();
							wd.url = marker.getAttribute("link");
							wd.isOther = (marker.getAttribute("category") != "osmer");
							String slat = marker.getAttribute("lat");
							String slong = marker.getAttribute("lng");
							if(!slat.isEmpty() && !slong.isEmpty())
							{
								try{
									int lat = (int) ( Float.parseFloat(slat) * 1e6);
									int longit = (int) ( Float.parseFloat(slong) * 1e6);
									wd.geoPoint = new GeoPoint(lat, longit);
								}
								catch(NumberFormatException nfe)
								{
									wd.geoPoint = null;
								}
							}
							if(!wd.location.isEmpty() && !wd.url.isEmpty() && wd.geoPoint != null)
							{
//								Log.i("OtherWebcamListDecoder: decode()" , "decoded:" + wd.toString());
								wcData.add(wd);
							}
							else
								wd = null; /* can free */
						}
					}
				} 
				catch (SAXException e) 
				{
					Log.e("OtherWebcamListDecoder: decode()", e.getLocalizedMessage());
				} 
				catch (IOException e) 
				{	
					Log.e("OtherWebcamListDecoder: decode()", e.getLocalizedMessage());
				}
			} 
			catch (UnsupportedEncodingException e) 
			{
				Log.e("OtherWebcamListDecoder: decode()", e.getLocalizedMessage());
			}
		} 
		catch (ParserConfigurationException e1) 
		{
			Log.e("OtherWebcamListDecoder: decode()", e1.getLocalizedMessage());
		}
		
		if(saveOnCache && wcData.size() > 0) /* cache cleaned file */
			WebcamDataCache.getInstance().saveToCache(rawData, ViewType.WEBCAMLIST_OTHER);
		
		return wcData;
	}

}
