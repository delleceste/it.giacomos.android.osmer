package it.giacomos.android.osmer.pro.widgets;

import it.giacomos.android.osmer.pro.regexps.Regexps;

import java.lang.String;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** @author giacomo
 * 
 * Extracts useful information from the .info text documents such as
 * http://www.meteo.fvg.it/IT/HOME/Domani.info
 * 
 *
 */
public class InfoTextExtractor {
	
	public InfoTextExtractor()
	{
		
	}
	
	public void process(String s)
	{
		m_text = s;
		m_emissionDate = "--:--:--";
		m_emissionHour = "--:--";
		m_reliability = "";
		
		Pattern p = Pattern.compile(Regexps.DATE);
		Matcher m = p.matcher(m_text);
		if(m.find())
			m_date = m.group(1);
		
		p = Pattern.compile(Regexps.EMISSION_DATE);
		m = p.matcher(m_text);
		if(m.find())
			m_emissionDate = m.group(1);
		
		p = Pattern.compile(Regexps.EMISSION_HOUR);
		m = p.matcher(m_text);
		if(m.find())
			m_emissionHour = m.group(1);

		p = Pattern.compile(Regexps.RELIABILITY);
		m = p.matcher(m_text);
		if(m.find())
			m_reliability = m.group(1);
		
		p = Pattern.compile(Regexps.INFO_TXT);
		m = p.matcher(m_text);
		if(m.find())
			m_text = m.group(1);

	}
	
	public String date() { return m_date; }
	
	public String reliability() { return m_reliability; }
	
	public String text() { return m_text; }
	
	public String emissionDate() { return m_emissionDate; }
	
	public String emissionHour() { return m_emissionHour; }
	
	private String m_date, m_reliability, m_text, m_emissionDate, m_emissionHour;

}
