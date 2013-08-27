package it.giacomos.android.osmer.pro.regexps;

public class Regexps {
	public static final String SITUATION = "SITUAZIONEGENERALE_TESTO=(.*)";
	/* Oggi.info, Domani.info, Dopodomani.info */
	public static final String DATE = "DATA DI RIFERIMENTO:\\s*(.*?)\\s*DATA";
	public static final String RELIABILITY = "ATTENDIBILITA':\\s*\\n\\s*([0-9]{1,3})\\s*";
	public static final String EMISSION_DATE = "DATA DI EMISSIONE:\\s*(.*?)\\n";
	public static final String EMISSION_HOUR = "ORA DI EMISSIONE:\\s*(.*?)\\n";
	public static final String INFO_TXT = "TESTO:\\n(.*?)\\n";

	/*
	 * ([A-Za-z'\.\-\s]*)\s+(\d+:\d+)\s+([A-Za-z\.\-\s]*)\s+(\-{0,1}\d+\.{0,1}\d*|---)\s+(\-{0,1}\d+\.{0,1}\d*|---)\s+(\-{0,1}\d+\.{0,1}\d*|---)
	 *  \s+(\d+\.{0,1}\d*|---)\s+(\d+\.{0,1}\d*|---)\s+(\d+\.{0,1}\d*|---)\s+(\d+\.{0,1}\d*|---)
	 */
	public static final String DAILY_TABLE = 
			"([A-Za-z'\\.\\-\\s]*)\\s+(\\d+:\\d+)\\s+([A-Za-z\\.\\-\\s]*)\\s+(\\-{0,1}\\d+\\.{0,1}\\d*|---)\\s+(\\-{0,1}\\d+\\.{0,1}\\d*|---)\\s+(\\-{0,1}\\d+\\.{0,1}\\d*|---)\\s+(\\d+\\.{0,1}\\d*|---)\\s+(\\d+\\.{0,1}\\d*|---)\\s+(\\d+\\.{0,1}\\d*|---)\\s+(\\d+\\.{0,1}\\d*|---)";

	/* latest table regexp unescaped
	 * ([A-Za-z'\.\-\s*\(\)0-9]*)\s+(\d+:\d+)\s+([A-Za-z\.\-\s]*)\s+(\d+\.{0,1}\d*|---)\s+(\-{0,1}\d+\.{0,1}\d*|---)\s+(\d+|---)\s+(\d+\s+[SENOW\-\s]*|---\s+---)\s+(\d+\.{0,1}\d*|---)\s+(\d+\.{0,1}\d*|---)\s+([<>=]*\d+\.{0,1}\d*|---)
	 * 
	 */
	public static final String LATEST_TABLE = 
			"([A-Za-z'\\.\\-\\s*\\(\\)0-9]*)\\s+(\\d+:\\d+)\\s+([A-Za-z\\.\\-\\s]*)\\s+(\\-{0,1}\\d+\\.{0,1}\\d*|---)\\s+(\\d+\\.{0,1}\\d*|---)\\s+(\\d+|---)\\s+(\\d+\\s+[SENOW\\-\\s]*|---\\s+---)\\s+(\\d+\\.{0,1}\\d*|---)\\s+(\\d+\\.{0,1}\\d*|---)\\s+([<>=]*\\d+\\.{0,1}\\d*|---)";

	/* webcam extraction regexps from DatiWebcams1.php */
	
	/* if\s+\(nome\s+==\s+\"([a-zA-Z0-9_\-\.\s]+)\"\) */
	public static final String WEBCAM_LOCATION = "if\\s+\\(nome\\s+==\\s+\\\"([a-zA-Z0-9_\\-\\.\\s]+)\\\"\\)";
	
	/* \s*var\s+nome1\s*\=\"([A-Za-z0-9_\-\.\s]+)\"\s* 
	 *
	 */
	public static final String WEBCAM_FILENAME = "\\s*var\\s+nome1\\s*\\=\\\"([A-Za-z0-9_\\-\\.\\s]+)\\\"\\s*";
	
	/* 
	 *    \s*var\s+str\s*\=\"([A-Za-z0-9_\-\.\s/:]+)\"\s*
	 */
	public static final String WEBCAM_TEXT = "\\s*var\\s+str\\s*\\=\\\"([A-Za-z0-9_\\-\\.\\s/:]+)\\\"\\s*";
	
}
