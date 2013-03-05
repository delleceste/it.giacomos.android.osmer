package it.giacomos.android.osmer.textToImage;

import android.util.Log;
import it.giacomos.android.osmer.R;
import it.giacomos.android.osmer.StringType;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextDecoder implements TextChangeListener
{
	public final String VARIABLE = "variab";
	public final String RAIN ="piogg";
	public final String RAIN2 = "precipitaz";
	public final String UNSURE = "incert";
	public final String STORM = "temporal";
	public final String P_STORM = "\\btemporal[ei]{1}\\s+diffus"; // \btemporal[ei]{1}\s+diffus
	public final String P_SNOW = "nev(?:e|icat)"; // nev(?:e|icat)
	public final String P_SHOWERS =  "(?:piogg[iae]{1,2})\\s+(?:abbondan.*|intens.*)"; // "(?:piogg[iae]{1,2})\s+(?:abbondan.*|intens.*)"
	public final String P_SHOWERS2 =  "(?:precipitazion[ie]{1})\\s+(?:abbondan.*|intens.*)"; // "(?:piogg[iae]{1,2})\s+(?:abbondan.*|intens.*)"
	public final String P_FEW_CLOUDS = "\\bpoc(?:o|he)\\s+nu(?:vol|bi)"; //  \bpoc(?:o|he)\s+nu(?:vol|bi)
	public final String P_FEW_CLOUDS2 = "\\bqualche\\s+nu(?:vol|be)";
	public final String P_FEW_CLOUDS3 = "\\balcune\\s+nu(?:vol|bi)"; /// \balcune\s+nu(?:vol|bi)
	public final String P_COVERED = "(?:molto\\s+nuvoloso)|(?:coperto)"; // (?:molto\s+nuvoloso)|(?:coperto)
	public final String P_COVERED2  = "(?:molt[eo])\\s+(?:nuvoloso|nubi)"; // (?:molt[eo])\s+(?:nuvoloso|nubi)
	public final String CLEAR = "sereno";
	
	// \b(?:nebb[iea]{2})|(?:foschi[ae]{1}\s+intens)|(?:foschi[ae]{1}\s+dens)|(?:dens[ae]{1}\s+foschi)
	public final String P_MIST = "\\b(?:nebb[iea]{2})|(?:foschi[ae]{1}\\s+intens)|(?:foschi[ae]{1}\\s+dens)|(?:dens[ae]{1}\\s+foschi)"; 
	
	public TextDecoder(TextDecoderListener ttil)
	{
		mTextDecoderListener = ttil;
	}
	
	@Override
	public void onTextChanged(String text, StringType t) 
	{		
		text = text.toLowerCase(Locale.ITALIAN);

		Pattern snowP = Pattern.compile(P_SNOW);
		Matcher snowM = snowP.matcher(text);
		Pattern manyStormP = Pattern.compile(P_STORM);
		Matcher manyStormM = manyStormP.matcher(text);
		
		boolean snow = snowM.find(0);	
		boolean variable = text.contains(VARIABLE) || text.contains(UNSURE);
		boolean rain = text.contains(RAIN) || text.contains(RAIN2);
		boolean storm = text.contains(STORM);
		boolean manyStorms = manyStormM.find(0);
		
		/* - Pioggia e neve
		 * 
		 */
		if(snow && rain)
		{
			switch(t)
			{
			case TODAY:
				mTextDecoderListener.onTextDecoded(t, R.drawable.weather_snow_rain_state);
				break;
			case TOMORROW:
				mTextDecoderListener.onTextDecoded(t, R.drawable.weather_snow_rain_1_state);
				break;
			case TWODAYS:
				mTextDecoderListener.onTextDecoded(t, R.drawable.weather_snow_rain_2_state);
			}
			return;
		}
		
		/* - Non variabile, con neve 
		 * 
		 */
		if(!variable && snow)
		{
			switch(t)
			{
			case TODAY:
				mTextDecoderListener.onTextDecoded(t, R.drawable.weather_snow_state);
				break;
			case TOMORROW:
				mTextDecoderListener.onTextDecoded(t, R.drawable.weather_snow_1_state);
				break;
			case TWODAYS:
				mTextDecoderListener.onTextDecoded(t, R.drawable.weather_snow_2_state);
			}		
			return;
		}
		
		/* nebbia */
		Pattern mistP = Pattern.compile(P_MIST);
		Matcher mistM = mistP.matcher(text);
		
		boolean mist = mistM.find(0);
		
		/* nebbia o foschie intense
		 * 
		 */
		if(mist)
		{
			switch(t)
			{
			case TODAY:
				mTextDecoderListener.onTextDecoded(t, R.drawable.weather_mist_state);
				break;
			case TOMORROW:
				mTextDecoderListener.onTextDecoded(t, R.drawable.weather_mist_1_state);
				break;
			case TWODAYS:
				mTextDecoderListener.onTextDecoded(t, R.drawable.weather_mist_2_state);
			}		
			return;
		}
		
		
		Pattern p = Pattern.compile(P_SHOWERS);
		Pattern p2 = Pattern.compile(P_SHOWERS2);
		Matcher m = p.matcher(text);
		Matcher m2 = p2.matcher(text);
		
		boolean showers = m.find(0) || m2.find(0);
		
		/* - Temporale: piogge abbondanti e temporali oppure temporali diffusi
		 * 
		 */
		if((showers && storm) || manyStorms)
		{
			switch(t)
			{
			case TODAY:
				mTextDecoderListener.onTextDecoded(t, R.drawable.weather_storm_state);
				break;
			case TOMORROW:
				mTextDecoderListener.onTextDecoded(t, R.drawable.weather_storm_state_1);
				break;
			case TWODAYS:
				mTextDecoderListener.onTextDecoded(t, R.drawable.weather_storm_state_2);
			}
			return;
		}
		
		/* - Piogge abbondanti o intense
		 * 
		 */
		if(showers)
		{
			switch(t)
			{
			case TODAY:
				mTextDecoderListener.onTextDecoded(t, R.drawable.weather_showers_state);
				break;
			case TOMORROW:
				mTextDecoderListener.onTextDecoded(t, R.drawable.weather_showers_1_state);
				break;
			case TWODAYS:
				mTextDecoderListener.onTextDecoded(t, R.drawable.weather_showers_2_state);
			}
			return;
		}
		
		p = Pattern.compile(P_COVERED);
		p2 = Pattern.compile(P_COVERED2);
		m = p.matcher(text);
		m2 = p2.matcher(text);
		
		boolean covered = m.find(0) || m2.find(0);
		
		boolean clear = text.contains(CLEAR);
		
		/* poco nuvoloso -> few clouds regexp */
		p = Pattern.compile(P_FEW_CLOUDS);
		p2 = Pattern.compile(P_FEW_CLOUDS2);
		m = p.matcher(text);
		m2 = p2.matcher(text);
		Pattern p3 = Pattern.compile(P_FEW_CLOUDS3);
		Matcher m3 = p3.matcher(text);
		
		boolean fewclouds = m.find(0) || m2.find(0) || m3.find(0);
		
		/* - Cielo variabile, senza pioggia
		 * 
		 */
		if(variable && !rain)
		{
			switch(t)
			{
			case TODAY:
				mTextDecoderListener.onTextDecoded(t, R.drawable.weather_clouds_state);
				break;
			case TOMORROW:
				mTextDecoderListener.onTextDecoded(t, R.drawable.weather_clouds_1_state);
				break;
			case TWODAYS:
				mTextDecoderListener.onTextDecoded(t, R.drawable.weather_clouds_2_state);
			}
			return;
		}
		
		/* - Cielo variabile e pioggia oppure cielo sereno o poco nuvoloso con (possibilita` di) pioggia
		 * 
		 */
		if((variable && rain) || ( (clear || fewclouds) && rain))
		{
			switch(t)
			{
			case TODAY:
				mTextDecoderListener.onTextDecoded(t, R.drawable.weather_variable_showers_state);
				break;
			case TOMORROW:
				mTextDecoderListener.onTextDecoded(t, R.drawable.weather_variable_showers_state_1);
				break;
			case TWODAYS:
				mTextDecoderListener.onTextDecoded(t, R.drawable.weather_variable_showers_state_2);
			}
			return;
		}
		
		/* - Cielo sereno
		 * 
		 */
		if(clear && !fewclouds && !rain && !covered)
		{
			switch(t)
			{
			case TODAY:
				mTextDecoderListener.onTextDecoded(t, R.drawable.weather_clear_state);
				break;
			case TOMORROW:
				mTextDecoderListener.onTextDecoded(t, R.drawable.weather_clear_1_state);
				break;
			case TWODAYS:
				mTextDecoderListener.onTextDecoded(t, R.drawable.weather_clear_2_state);
			}
			return;
		}
		
		/* - Sereno o poco nuvoloso
		 * 
		 */
		if( (clear || fewclouds) && !rain)
		{
			switch(t)
			{
			case TODAY:
				mTextDecoderListener.onTextDecoded(t, R.drawable.weather_few_clouds_state);
				break;
			case TOMORROW:
				mTextDecoderListener.onTextDecoded(t, R.drawable.weather_few_clouds_1_state);
				break;
			case TWODAYS:
				mTextDecoderListener.onTextDecoded(t, R.drawable.weather_few_clouds_2_state);
			}
			return;
		}
 		
		/* - Cielo coperto ma non pioggia
		 * 
		 */
		if(covered && !rain)
		{
			switch(t)
			{
			case TODAY:
				mTextDecoderListener.onTextDecoded(t, R.drawable.weather_many_clouds_state);
				break;
			case TOMORROW:
				mTextDecoderListener.onTextDecoded(t, R.drawable.weather_many_clouds_1_state);
				break;
			case TWODAYS:
				mTextDecoderListener.onTextDecoded(t, R.drawable.weather_many_clouds_2_state);
			}
			return;
		}
		
		/* - Cielo coperto e pioggia, ma non piogge intense / abbondanti, le quali sono state prese
		 *   in considerazione sopra (showers)
		 * 
		 */
		if(covered && rain)
		{
			switch(t)
			{
			case TODAY:
				mTextDecoderListener.onTextDecoded(t, R.drawable.weather_showers_scattered_state);
				break;
			case TOMORROW:
				mTextDecoderListener.onTextDecoded(t, R.drawable.weather_showers_scattered_1_state);
				break;
			case TWODAYS:
				mTextDecoderListener.onTextDecoded(t, R.drawable.weather_showers_scattered_2_state);
			}
			return;
		}
		
		mTextDecoderListener.onTextDecoded(t, 0);
	}
	
	private TextDecoderListener mTextDecoderListener;

}
