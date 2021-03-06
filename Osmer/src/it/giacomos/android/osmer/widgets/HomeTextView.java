package it.giacomos.android.osmer.widgets;

import android.content.Context;
import android.util.AttributeSet;

public class HomeTextView extends OTextView{

	public HomeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public String formatText(String doc) {
		String text = new String("error");
		SituationTextExtractor situationextExtractor = new SituationTextExtractor();
		text = new InfoHtmlBuilder().wrapSituationIntoHtml(situationextExtractor.process(doc), getResources());
		return text;
	}
}
