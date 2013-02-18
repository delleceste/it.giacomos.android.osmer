package it.giacomos.android.osmer.textToImage;

import android.util.Log;
import it.giacomos.android.osmer.StringType;

public class TextDecoder implements TextChangeListener
{
	public TextDecoder(TextDecoderListener ttil)
	{
		mTextDecoderListener = ttil;
	}
	
	@Override
	public void onTextChanged(String text, StringType t) {
		/*
		 * 
		 * sistemare questione id / stringType
		 */
		Log.i("TextDecoder::onTextChanged", text + t);
		mTextDecoderListener.onTextDecoded(t, 0);
	}
	
	private TextDecoderListener mTextDecoderListener;

}
