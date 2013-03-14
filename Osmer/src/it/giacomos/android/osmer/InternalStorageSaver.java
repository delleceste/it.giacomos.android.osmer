package it.giacomos.android.osmer;

import it.giacomos.android.osmer.widgets.ODoubleLayerImageView;
import it.giacomos.android.osmer.widgets.OTextView;

/**
 * 
 * @author giacomo
 *
 * You can save files directly on the device's internal storage. By default, files saved to the internal storage are private to your application and other applications cannot
 * access them (nor can the user). When the user uninstalls your application, these files are removed.
 */
public class InternalStorageSaver {
	public void save(OsmerActivity a)
	{
		/* save text views */
		OTextView ov = (OTextView) a.findViewById(R.id.homeTextView);
		ov.saveOnInternalStorage();
		ov = (OTextView) a.findViewById(R.id.todayTextView);
		ov.saveOnInternalStorage();
		ov = (OTextView) a.findViewById(R.id.tomorrowTextView);
		ov.saveOnInternalStorage();
		ov = (OTextView) a.findViewById(R.id.twoDaysTextView);
		ov.saveOnInternalStorage();
		
		/* save images */
		ODoubleLayerImageView dliv = (ODoubleLayerImageView) a.findViewById(R.id.homeImageView);
		dliv.saveOnInternalStorage();
		dliv = (ODoubleLayerImageView) a.findViewById(R.id.todayImageView);
		dliv.saveOnInternalStorage();
		dliv = (ODoubleLayerImageView) a.findViewById(R.id.tomorrowImageView);
		dliv.saveOnInternalStorage();
		dliv = (ODoubleLayerImageView) a.findViewById(R.id.twoDaysImageView);
		dliv.saveOnInternalStorage();
	}
}
