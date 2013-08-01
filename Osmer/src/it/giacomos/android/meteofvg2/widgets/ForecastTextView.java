package it.giacomos.android.meteofvg2.widgets;

import android.content.Context;
import android.util.AttributeSet;

public class ForecastTextView extends OTextView {

	public ForecastTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public String formatText(String html)
	{
		InfoTextExtractor ite = new InfoTextExtractor();
		ite.process(html);
		InfoHtmlBuilder htmlBuilder = new InfoHtmlBuilder();
		String[] fields = new String[5];
		fields[0] = ite.date();
		fields[1] = ite.emissionDate();
		fields[2] = ite.emissionHour();
		fields[3] = ite.reliability();
		fields[4] = ite.text();
		return htmlBuilder.buildHtml(fields, getResources());
	}
}
