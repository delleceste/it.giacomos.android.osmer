package it.giacomos.android.osmer.webcams;

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
import com.google.android.gms.maps.model.LatLng;
import android.text.Html;
import android.util.Log;

public class OtherWebcamListDecoder implements WebcamListDecoder 
{

	@Override
	public ArrayList<WebcamData> decode(String rawData) 
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
									float lat =  ( Float.parseFloat(slat));
									float longit =  ( Float.parseFloat(slong));
									wd.latLng = new LatLng(lat, longit);
								}
								catch(NumberFormatException nfe)
								{
									wd.latLng = null;
								}
							}
							if(!wd.location.isEmpty() && !wd.url.isEmpty() && wd.latLng != null)
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
					Log.e("OtherWebcamListDecoder SAXException: decode()", e.getLocalizedMessage());
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
		return wcData;
	}

}
