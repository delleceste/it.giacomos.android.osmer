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
import org.xml.sax.SAXException;

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
		
		

		return wcData;
	}

}
