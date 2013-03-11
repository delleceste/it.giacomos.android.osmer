package it.giacomos.android.osmer.webcams;
import android.app.Activity;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;


public class AdditionalWebcams 
{
	public AdditionalWebcams(Activity activity)
	{
		mText = "";
		AssetManager assetManager = activity.getAssets();
        InputStream input;
        try {
            input = assetManager.open("additionalWebcams.xml");
             
             int size = input.available();
             byte[] buffer = new byte[size];
             input.read(buffer);
             input.close();
             mText = new String(buffer);
        } 
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	
	public String getText()
	{
		return mText;
	}
	
	private String mText;
}
