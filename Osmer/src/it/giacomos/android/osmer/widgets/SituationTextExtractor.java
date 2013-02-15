package it.giacomos.android.osmer.widgets;

import it.giacomos.android.osmer.Regexps;

import java.lang.String;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SituationTextExtractor {
	public SituationTextExtractor()
	{
		
	}
	
	public String process(String txt)
	{
		Pattern p = Pattern.compile(Regexps.SITUATION);
		Matcher m = p.matcher(txt);

		if(m.find())
			return m.group(1);
		else
			return "Error extracting situation forecast in \"" + txt + "\"\n";
	}
}
