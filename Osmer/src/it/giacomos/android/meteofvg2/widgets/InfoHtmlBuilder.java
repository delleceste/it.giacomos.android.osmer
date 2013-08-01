package it.giacomos.android.meteofvg2.widgets;

import java.util.Calendar;

import android.content.res.Resources;
import android.text.Html;
import it.giacomos.android.meteofvg2.R;
import it.giacomos.android.meteofvg2.R.string;

public class InfoHtmlBuilder {
	public InfoHtmlBuilder()
	{
		
	}

	public String wrapErrorIntoHtml(String err, Resources res)
	{
		String html = "";
		Calendar cal = Calendar.getInstance(res.getConfiguration().locale);
		html += "<div id=\"header\"><em>" + res.getString(R.string.network_error) 
				+ " <cite>"+ cal.getTime().toLocaleString() + "</cite></em> </div>";
		html += "<div id=\"text\"><p>" + err + "</p></div>";
		return html;
	}
	
	public String wrapSituationIntoHtml(String s, Resources res)
	{
		String html = "";
		Calendar cal = Calendar.getInstance(res.getConfiguration().locale);
		html += "<div id=\"header\"><strong>" + res.getString(R.string.situation)
				+ " <cite>"+ cal.getTime().toLocaleString() + "</strong> - " + res.getString(R.string.data_source) + "</cite> </div>";
		html += "<div id=\"text\"><p>" + s + "</div>";
		return html;
	}
	
	String buildHtml(String fields[], Resources res)
	{
		String html = "invalid field number";
		if(fields.length == 5)
		{
			html = "<div id=\"header\"><h5>" + fields[DATE] + "</h5>  ";
			html += "<strong>" + res.getString(R.string.emission_date) + " " + fields[EMISSION_DATE];
			html += " " + res.getString(R.string.hours) + " "+ fields[EMISSION_HOUR];
			html += "</strong> - <cite>";
			if(!fields[RELIABILITY].isEmpty())
				html += res.getString(R.string.reliability) + " " + fields[RELIABILITY] + "%, ";
			
			html +=	res.getString(R.string.data_source) + "</cite></div>";
			html += "<div id=\"text\"><p>" + fields[TEXT] + "</div>";
			//html += res.getString(R.string.data_source) + "<cite></p></div>";
		}
		return html;
	}
	
	public  final int DATE = 0;
	public  final int EMISSION_DATE = 1;
	public  final int EMISSION_HOUR = 2;
	public  final int RELIABILITY = 3;
	public  final int TEXT = 4;
}
