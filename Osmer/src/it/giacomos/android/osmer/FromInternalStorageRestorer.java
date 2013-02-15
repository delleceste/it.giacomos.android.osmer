package it.giacomos.android.osmer;

import it.giacomos.android.osmer.widgets.ODoubleLayerImageView;
import it.giacomos.android.osmer.widgets.OTextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.String;
import java.util.HashMap;
import java.util.Iterator;

import android.util.Log;

/** 
 * restores relevant text on TextViews and bitmaps on ImageViews, or any
 * other interesting thing you want to display at application startup.
 * 
 * State machine is not modified by this.
 * @author giacomo
 *
 */
public class FromInternalStorageRestorer {
	FromInternalStorageRestorer(OsmerActivity a)
	{
		Log.e("FromInternalStorageRestorer", "RESTORING");
		/* save text views */
		OTextView ov = (OTextView) a.findViewById(R.id.homeTextView);
		ov.restoreFromInternalStorage();
		ov = (OTextView) a.findViewById(R.id.todayTextView);
		ov.restoreFromInternalStorage();
		ov = (OTextView) a.findViewById(R.id.tomorrowTextView);
		ov.restoreFromInternalStorage();
		ov = (OTextView) a.findViewById(R.id.twoDaysTextView);
		ov.restoreFromInternalStorage();
		
		/* save images */
		ODoubleLayerImageView dliv = (ODoubleLayerImageView) a.findViewById(R.id.homeImageView);
		dliv.restoreFromInternalStorage();
		dliv = (ODoubleLayerImageView) a.findViewById(R.id.todayImageView);
		dliv.restoreFromInternalStorage();
		dliv = (ODoubleLayerImageView) a.findViewById(R.id.tomorrowImageView);
		dliv.restoreFromInternalStorage();
		dliv = (ODoubleLayerImageView) a.findViewById(R.id.twoDaysImageView);
		dliv.restoreFromInternalStorage();
		
		
	}
}
