/*
 * Software-Entwicklung H. Jakubzik, Lizenz Version 1.0 (Uni Heidelberg)
 *
 *
 * Copyright (c) 2004 Heiko Jakubzik.  All rights reserved.
 *
 * http://www.heiko-jakubzik.de/
 * http://www.heiko-jakubzik.de/SignUp/
 * http://www.heiko-jakubzik.de/SignUp/hd/
 * mailto:heiko.jakubzik@t-online.de
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is not permitted.
 *
 * Lizenznehmer der Universitaet Heidelberg erwerben mit der Lizenz
 * das Recht, den Quellcode der Internetanwendung "SignUp" einzusehen,
 * zu veraendern und fuer den Eigenbedarf neu zu kompilieren.
 *
 * Die Weitergabe von Softwarekomponenten des Pakets durch die
 * Lizenznehmerin ist weder als Quelltext noch in kompilierter Form
 * ganz oder teilweise gestattet.
 *
 * Diese Lizenz erstreckt sich auf die Internetanwendung "SignUp",
 * also
 *
 * 1. das Datenschema,
 * 2. die Bibliothek "SignUp.jar"
 * 3. das Webarchiv  "SignUpXP.war"
 * 4. die Resource Bundles und Interfaces,
 * 5. das Access Frontend "SignUpXP",
 * 6. Konvertierungs-Skripte, XML und XSL Dateien,
 *    sowie alle vbs und hta Anwendungen zu Konvertierung
 *    von Excel und XML Dateien in SignUp-kompatibles Format,
 * 7. C# .NET Frontend Komponenten,
 * 8. die Webservice Spezifikation,
 * 9. die Dokumentation inkl. Bilder
 *
 * Das Rechtemanagement zur Neukompilierung unterliegt der
 * Kontrolle der Universtitaet Heidelberg. Fuer Kompilationen,
 * die von der Lizenznehmerin durchgefuehrt wurden, besteht
 * kein Anspruch auf Nachbesserung.
 *
 * Der Lizenzgeber behaelt es sich vor, die Lizenz auf
 * ein "Open Source"-Modell umzustellen.
 *
 * Bei Aenderungen am Datenmodell oder an Objekten _dieses_ Pakets
 * (com.shj.signUp.data) wird _dringend_ Ruecksprache mit dem
 * Autor der Software empfohlen.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL H. JAKUBZIK SOFTWARE-DEVELOPEMENT
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 *
 * CODE STRUCTURE:
 *
 *
 * @version 5-21-07x
 * change date              change author           change description
 * ===================================================================
 * version 5-21
 *
 * May-19-2004		    	h. jakubzik
 * May-24-2004				h. jakubzik				invented format XmlHtml.
 *
 */

package de.shj.UP.data;


/**
 * This class is to help text-markup and make it suitable for
 * these three purposes:
 * (1) HTML
 * (2) TeX
 * (3) UnivIS
 * (4) Text	(not formatting at all)
 * (5) XmlHtml
 * {
 *   (6) Ascii
 *   (7) RTF
 *	 (8) ...
 * }
 *
 * Here is a rough survey of formats that can be transformed
 * using this little utility object:
 *
 * Html				UnivIS			TeX
 * ---------------------------------------------
 * <br />			[Leerzeile]		[Leerzeile]
 *
 * <b>x</b>			*b*				\textbf{x}
 *
 * <i>x</i>			*i*				\textit{x}
 *
 * <ul>    							\begin{itemize}
 *  <li>x</li>		 - 				\item x
 *  <li>y</li>		 -				\item y
 * </ul>							\end{itemize}
 *
 * <sup>y</sup>		^x^				\( ^{x} \)
 *
 * <sub>y</sub>		_x_				\( _{x} \)
 *
 * --------------------------------------------
 * --------------------------------------------
 * UnivIS' special link-format is not supported.
 * Links are returned the way they are handed in.
 *
 * The general idea in this class is to have
 * m_strInput, the input, in any format.
 *
 * method .determineFormat() is used to determine
 * the String's format. It looks if any of the
 * specific markups (see table above) are in the
 * String, and sets the format to 'Unknown' if
 * there are not. This costs performance, of course.
 * You can also specify the String's format on
 * construction.
 *
 * For conversion, the main method is .reMark()
 * it looks for text between left- and right markers,
 * e.g. for text between <b> and the </b> that occurs
 * next in m_strInput. It then converts the substring
 * according to settings in the table m_TABLE (see .init()).
 *
 * The converted Strings are stored in a local array, so
 * converting the same String several times is of no additional
 * performance cost.
 *
 * Unrecognized markup is left untouched. Also, if you
 * convert to Text, there is (as of yet) no intelligent
 * search for tags; only the known tags are erased, the
 * rest is left alone.
 *
 * ADDITIONAL FORMATS (pure ascii, RTF etc.) can easily be installed
 * setting more values in m_TABLE (see .init()). The more
 * formats you install, the more performance you need to determine
 * the input format, though. That's why I only installed Html, TeX
 * and UnivIS for a start.
 *
 * There is also at least one and a half drawbacks, and one awkwardness in conversion:
 *
 * 1. the DRAWBACK is that a conversion FROM TeX into anything else that
 *    will FAIL (=result in wrong markup), if the TeX code has markup
 *    for bold AND italic characters. (This is due to TeX marking "end-of-bold"
 *    identical to "end-of-italic", with a "}". (RTF does that, too).
 *
 * 2. the half-drawback is that in format detection, preference will be given
 *    to UnivIS over TeX. If formats seem identical, UnivIS is assumed. (It does
 *    not really matter for anything, so it counts only half).
 *
 * 3. the awkwardness is in enumerations. Since TeX and Html mark the beginning of
 *    an enumeration, and its ending, and each item (with "begin{itemize}", "\item" and
 *    "ul", "li" respectively), and UnivIS only marks items (with newline + "-"), the
 *    conversion has this awkwardness. UnivIS format will get an additional "\\shjEnumStart"
 *    and "\\shjEnumStop" to mark enumerations. Hence method "markEnumerationBorders",
 *    hence calls to this method and other awkward checks.
 *
 * NOTE: much of this is handled via Xml, for the sake of
 *       conformity, there is an 'internal' format called XmlHtml.
 **/
public class Markup{

	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 1.   P R I V A T E  D E C L A R A T I O N S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	public String m_strDebug			= "";

	//private String m_strInternalRep		= "";
	private String m_strInput			= "";

	private boolean m_blnInitialized	= false;

	private static final byte m_FormatCount = 4;
	private static final byte m_TypeCount   = 6;
	private static String[][][] m_TABLE		= new String[m_FormatCount+1][m_TypeCount+1][1+1];
	private String m_strOutput[]			= new String[m_FormatCount+1];

	private byte   m_FORMAT						= -1;

	public static final byte FORMAT_HTML		= 0;
	public static final byte FORMAT_TeX			= 1;
	public static final byte FORMAT_UnivIS		= 2;
	public static final byte FORMAT_Text		= 3;
	public static final byte FORMAT_XmlHtml		= 4;	// artificial: <li />-tag, and <br />-tag, otherwise like html
	public static final byte FORMAT_RTF			= 5;	// not implemented
	public static final byte FORMAT_Ascii		= 6;	// not implemented (idea was _ for underline etc.)
	public static final byte FORMAT_UNKNOWN		= 7;

	private static final byte T_BOLD			= 6;
	private static final byte T_ITALIC			= 5;
	private static final byte T_SUPERSCRIPT		= 4;
	private static final byte T_SUBSCRIPT		= 3;
	private static final byte T_LINEBREAK		= 2;
	private static final byte T_ENUMERATION		= 1;
	private static final byte T_ITEM			= 0;

	private static final byte LEFT				= 0;
	private static final byte RIGHT				= 1;



	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 2.   P U B L I C   D E C L A R A T I O N S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	// ------- EMPTY

	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 3.   P R I V A T E   P R O P E R T I E S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	// ------- EMPTY


	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 4.   P U B L I C  P R O P E R T I E S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	public byte getFormat(){
		return m_FORMAT;
	}

	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 5.   P R I V A T E  M E T H O D S (UTILS)
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------


	/**
	 * The problem that this method solves is the following:
	 * beginning and ending of an enumeration is marked
	 * in Html ('ul'), in TeX ('\begin {itemize}'), but
	 * not in UnivIS-format (just '-' at beginning of line).
	 * In order to translate enumerations correctly, an
	 * artificial '\shjEnumStart' and '\shjEnumStop' are
	 * inserted at the specific positions.
	 * This method goes through 'str_IN' and marks the first
	 * occurrence of '\n-' with '\shjEnumStart', and the last
	 * occurrenct of this enumeration with '\shjEnumStop', and
	 * so forth. This is a costly operation in a performance
	 * critical environment. So it should be called only if
	 * 1) input format is UnivIS,
	 * 2) output format is not UnivIS,
	 * 3) at least one '\n-'-occurrence in text.
	 * For performance reasons, this method does NOT check
	 * ANY of these three conditions again.
	 * (reMark checks).
	 **/
	private String markEnumerationBorders(String str_IN){

	  // declarations
	  String 		strReturn		=		str_IN;
	  int 			intPos			=		0;
	  int 			intPosEnd		=		0;
	  boolean		blnGoOn			= 		true;

	  final String	strSTARTMARKER		= "\\shjEnumStart";
	  final String	strENDMARKER		= "\\shjEnumStop";
	  int 		intStartmarkerLength	= strSTARTMARKER.length();
	  int		intSafety				= 0;

	  // start of algorithm
	  intPos 							= strReturn.indexOf( "\n-" );

	  // look as long as there is an "\n-" char-sequence in strReturn, but
	  // not more than 100 times:
	  while((intPos >= 0) && (intPos <= strReturn.length()) && (intSafety++ < 100)){

	    // insert startmarker before occurrence of "\n-"
	    strReturn = strReturn.substring(0, intPos) + strSTARTMARKER  + strReturn.substring(intPos);

	    intPosEnd = intPos + intStartmarkerLength;
	    blnGoOn   = true;

	    // loop through all occurrences of "\n-" until there is one "\n*", where
	    // * is any character but '-'.
	    // Then insert ENDMARKER after this.
	    while(blnGoOn && (intSafety++ < 100)){
	      intPosEnd	= strReturn.indexOf("\n", intPosEnd + 1);
	      try {
			blnGoOn   = ( (strReturn.charAt( intPosEnd + 1 ) == '-') && (intPosEnd >= 0) && (intPosEnd <= strReturn.length()) );
		} catch (RuntimeException e) {
			intSafety++;
		}
	    }

	    // in case no endmarker was set, make sure to put one at the end.
	    if(intPosEnd < 0) intPosEnd = strReturn.length();

	    strReturn = strReturn.substring(0, intPosEnd) + strENDMARKER  + strReturn.substring(intPosEnd);
	    intPos	  = strReturn.indexOf("\n-", intPosEnd + 1 );

	  }//end while

	  return strReturn;
	}

	/**
	 * In ambiguous cases (found an empty line, for example), UnivIS is
	 * given precedence over TeX.
	 */
	private void determineFormat(){

		// see if Left-Markup occurs (sequence is, importantly, HTML, TeX, UnivIS
		if(this.m_FORMAT==-1){
			for(byte ii=0;ii<=m_FormatCount;ii++){
				for(int jj=0;jj<=m_TypeCount;jj++){
					if(this.m_strInput.indexOf(m_TABLE[ii][jj][LEFT])>0){
						this.m_FORMAT = ii;
						jj = m_TypeCount	+ 1;
						ii = m_FormatCount  + 1;
						break;
					}
				}
			}
		}

		if(this.m_FORMAT==-1) this.m_FORMAT = FORMAT_UNKNOWN;
	}


	/**
	 * Substitute or Replace this for that in String str_IN.
	 * @return String with substitutions in effect. Returns empty String ('') for a null-input.
	 * @param str_IN String that may contain character-sequences to be substituted
	 * @param strThis_IN String to look for.
	 * @param strForThat_IN String to substitute for 'strThis_IN' wherever it occurs.
	 **/
	private String shjSubstitute(String str_IN, String strThis_IN, String strForThat_IN){

		// interface: do nothing if there's nothing to be done.
		if ( str_IN == null ) return "";
		if ( str_IN.indexOf( strThis_IN, 0 ) < 0 ) return str_IN;

		// declarations
		String 		strReturn		=		str_IN;
		int 		intPos			=		0;
		int 		intLength 		= 		Math.max(strThis_IN.length(), strForThat_IN.length());
		boolean 	blnStart		=		true;

		// loop until last character was substituted.
		while(intPos>=0){
			if(blnStart){
				// initializations
				intPos 				= 		strReturn.indexOf(strThis_IN, 0);
				blnStart			=		false;
			}else{
				intPos 				= 		strReturn.indexOf(strThis_IN, intPos + intLength);
			}

			if(intPos>=0){
				strReturn 			= 		strReturn.substring(0,intPos) +
											strForThat_IN +
											strReturn.substring(intPos+strThis_IN.length());
			}
		}//end while

		return strReturn;
	}

	/**
	 * This method replaces strLeft and strRight (in strWhat) with strLeftNew and strRightNew, leaving
	 * the middle intact.
	 * Example: reMark("Hallo [b]Du[/b]", "[b]", "[/b]", "\textb{", "}") will result in "Hallo textb{Du},"
	 * so re-mark formatting from Html to TeX.
	 * reMark (currently) follows a left-to-right strategy when in handels the input. This works fine
	 * with Html->TeX, UnivIS->Html, etc., but it has its drawbacks with, say, TeX -> Html, or RTF -> Html. The reason
	 * is simply that in TeX (or RTF), the end of a bold-sequence is marked like the end of an italics-sequence.
	 * So whenever there's nested formatting -- bold and italics (which is bad style anyway), there
	 * may be problems. a \textb{bold and \textit{italic} text} might be translated (wrongly) into
	 * something like [b]bold and \textit{italic[/b] text}, keeping half the markup.
	 **/
	private String reMark(String strWhat, String strLeft, String strRight, String strLeftNew, String strRightNew){

		int intPosLeft	= 0;
		int intPosRight = 0;

		int intSizeLeft = strLeft.length();
		int intSizeRight= strRight.length();
		long lngSize	= strWhat.length();

		String strDebug = "";
		String left		= "";
		String middle	= "";
		String right	= "";

		intPosLeft = strWhat.indexOf(strLeft);
		//strDebug   += "<p>Input: '" + strWhat + "'</p>";

		if( (intPosLeft >= 0)&&(strRight.equals("")) ){
			return shjSubstitute( strWhat, strLeft, strLeftNew );
		}

		while(intPosLeft>=0){
			intPosRight = strWhat.indexOf(strRight, intPosLeft + intSizeLeft + 1);
			// intPosRight = strWhat.lastIndexOf(strRight);

			if(intPosRight >=0){
			  left		= strWhat.substring(0, intPosLeft);
			  middle	= strWhat.substring(intPosLeft + intSizeLeft, intPosRight);
			  right		= strWhat.substring(intPosRight + intSizeRight);

			  strDebug += "<p>Left: '" + left + "'</p>";
			  strDebug += "<p>Middle: '" + middle + "'</p>";
			  strDebug += "<p>Right: '" + right + "'</p>";

			  strWhat = left + strLeftNew + middle + strRightNew + right;

			  intPosLeft =  ( (lngSize > intPosRight + intSizeRight) ? strWhat.indexOf(strLeft, intPosRight + intSizeRight + 1) : -1);
			}else{intPosLeft=-1;}

		}
		return strWhat;
	}

	private void clean(){
		for(int ii=0;ii <= m_FormatCount;ii++){
		  m_strOutput[ii] = null;
		}

		m_FORMAT	  = -1;
		m_strInput	  = "";
	}

	private void init(){
		// -----------------html---------------------------------------------FORMAT_XmlHtml
		m_TABLE[FORMAT_HTML][T_BOLD][LEFT]			= "<b>";
		m_TABLE[FORMAT_HTML][T_BOLD][RIGHT]			= "</b>";	// bold
		m_TABLE[FORMAT_HTML][T_ITALIC][LEFT]		= "<i>";
		m_TABLE[FORMAT_HTML][T_ITALIC][RIGHT]		= "</i>";   // italics
		m_TABLE[FORMAT_HTML][T_SUPERSCRIPT][LEFT]	= "<sup>";
		m_TABLE[FORMAT_HTML][T_SUPERSCRIPT][RIGHT]	= "</sup>";	// superscript
		m_TABLE[FORMAT_HTML][T_SUBSCRIPT][LEFT]		= "<sub>";
		m_TABLE[FORMAT_HTML][T_SUBSCRIPT][RIGHT]	= "</sub>"; // subscript
		m_TABLE[FORMAT_HTML][T_LINEBREAK][LEFT]		= "<br>";
		m_TABLE[FORMAT_HTML][T_LINEBREAK][RIGHT]	= ""; 		// linebreak
		m_TABLE[FORMAT_HTML][T_ENUMERATION][LEFT]	= "<ul>";
		m_TABLE[FORMAT_HTML][T_ENUMERATION][RIGHT]	= "</ul>"; 	// enumeration
		m_TABLE[FORMAT_HTML][T_ITEM][LEFT]			= "<li>";
		m_TABLE[FORMAT_HTML][T_ITEM][RIGHT]			= ""; 		// list-item

		// -----------------xmlhtml---------------------------------------------
		m_TABLE[FORMAT_XmlHtml][T_BOLD][LEFT]		= "<b>";
		m_TABLE[FORMAT_XmlHtml][T_BOLD][RIGHT]		= "</b>";	// bold
		m_TABLE[FORMAT_XmlHtml][T_ITALIC][LEFT]		= "<i>";
		m_TABLE[FORMAT_XmlHtml][T_ITALIC][RIGHT]	= "</i>";   // italics
		m_TABLE[FORMAT_XmlHtml][T_SUPERSCRIPT][LEFT]= "<sup>";
		m_TABLE[FORMAT_XmlHtml][T_SUPERSCRIPT][RIGHT]= "</sup>";	// superscript
		m_TABLE[FORMAT_XmlHtml][T_SUBSCRIPT][LEFT]	= "<sub>";
		m_TABLE[FORMAT_XmlHtml][T_SUBSCRIPT][RIGHT]	= "</sub>"; // subscript
		m_TABLE[FORMAT_XmlHtml][T_LINEBREAK][LEFT]	= "<br />";
		m_TABLE[FORMAT_XmlHtml][T_LINEBREAK][RIGHT]	= ""; 		// linebreak
		m_TABLE[FORMAT_XmlHtml][T_ENUMERATION][LEFT]= "<ul>";
		m_TABLE[FORMAT_XmlHtml][T_ENUMERATION][RIGHT]= "</ul>"; 	// enumeration
		m_TABLE[FORMAT_XmlHtml][T_ITEM][LEFT]		= "<li />";
		m_TABLE[FORMAT_XmlHtml][T_ITEM][RIGHT]		= ""; 		// list-item

		// -----------------UnivIS-----------------------------------------
		m_TABLE[FORMAT_UnivIS][T_BOLD][LEFT]		= "*";
		m_TABLE[FORMAT_UnivIS][T_BOLD][RIGHT]		= "*";	// bold
		m_TABLE[FORMAT_UnivIS][T_ITALIC][LEFT]		= "|";
		m_TABLE[FORMAT_UnivIS][T_ITALIC][RIGHT]		= "|";   // italics
		m_TABLE[FORMAT_UnivIS][T_SUPERSCRIPT][LEFT]	= "^";
		m_TABLE[FORMAT_UnivIS][T_SUPERSCRIPT][RIGHT]= "^";	// superscript
		m_TABLE[FORMAT_UnivIS][T_SUBSCRIPT][LEFT]	= "_";
		m_TABLE[FORMAT_UnivIS][T_SUBSCRIPT][RIGHT]	= "_";  // subscript
		m_TABLE[FORMAT_UnivIS][T_LINEBREAK][LEFT]	= "\n\n";
		m_TABLE[FORMAT_UnivIS][T_LINEBREAK][RIGHT]	= ""; 	// linebreak
		m_TABLE[FORMAT_UnivIS][T_ENUMERATION][LEFT]	= "\\shjEnumStart";
		m_TABLE[FORMAT_UnivIS][T_ENUMERATION][RIGHT]= "\\shjEnumStop"; 	// enumeration
		m_TABLE[FORMAT_UnivIS][T_ITEM][LEFT]		= "\n- ";
		m_TABLE[FORMAT_UnivIS][T_ITEM][RIGHT]		= ""; 	// list-item

		// -----------------TeX-----------------------------------------
		m_TABLE[FORMAT_TeX][T_BOLD][LEFT]			= "\\textbf{";
		m_TABLE[FORMAT_TeX][T_BOLD][RIGHT]			= "}";	// bold
		m_TABLE[FORMAT_TeX][T_ITALIC][LEFT]			= "\\textit{";
		m_TABLE[FORMAT_TeX][T_ITALIC][RIGHT]		= "}";   // italics
		m_TABLE[FORMAT_TeX][T_SUPERSCRIPT][LEFT]	= "\\( ^{";
		m_TABLE[FORMAT_TeX][T_SUPERSCRIPT][RIGHT]	= "} \\)";	// superscript
		m_TABLE[FORMAT_TeX][T_SUBSCRIPT][LEFT]		= "\\( _{";
		m_TABLE[FORMAT_TeX][T_SUBSCRIPT][RIGHT]		= "} \\)";  // subscript
		m_TABLE[FORMAT_TeX][T_LINEBREAK][LEFT]		= "\n\n";
		m_TABLE[FORMAT_TeX][T_LINEBREAK][RIGHT]		= ""; 	// linebreak
		m_TABLE[FORMAT_TeX][T_ENUMERATION][LEFT]	= "\\begin{itemize}";
		m_TABLE[FORMAT_TeX][T_ENUMERATION][RIGHT]	= "\\end{itemize}"; 	// enumeration
		m_TABLE[FORMAT_TeX][T_ITEM][LEFT]			= "\\item ";
		m_TABLE[FORMAT_TeX][T_ITEM][RIGHT]			= ""; 	// list-item

		// -----------------Text-----------------------------------------
		m_TABLE[FORMAT_Text][T_BOLD][LEFT]			= "";
		m_TABLE[FORMAT_Text][T_BOLD][RIGHT]			= "";	// bold
		m_TABLE[FORMAT_Text][T_ITALIC][LEFT]		= "";
		m_TABLE[FORMAT_Text][T_ITALIC][RIGHT]		= "";   // italics
		m_TABLE[FORMAT_Text][T_SUPERSCRIPT][LEFT]	= "";
		m_TABLE[FORMAT_Text][T_SUPERSCRIPT][RIGHT]	= "";	// superscript
		m_TABLE[FORMAT_Text][T_SUBSCRIPT][LEFT]		= "";
		m_TABLE[FORMAT_Text][T_SUBSCRIPT][RIGHT]	= "";  // subscript
		m_TABLE[FORMAT_Text][T_LINEBREAK][LEFT]		= "";
		m_TABLE[FORMAT_Text][T_LINEBREAK][RIGHT]	= ""; 	// linebreak
		m_TABLE[FORMAT_Text][T_ENUMERATION][LEFT]	= "";
		m_TABLE[FORMAT_Text][T_ENUMERATION][RIGHT]	= ""; 	// enumeration
		m_TABLE[FORMAT_Text][T_ITEM][LEFT]			= "";
		m_TABLE[FORMAT_Text][T_ITEM][RIGHT]			= ""; 	// list-item

		m_blnInitialized							= true;
	}
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 6.   P U B L I C  M E T H O D S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------

	/**
	 * For use without having to create a new object each time:
	 * @return String in the format specified through parameter FORMAT.
	 * @param str_IN: String to convert,
	 * @param FORMAT: format for output (see constructor for details).
	 **/
	public String convert(String str_IN, byte FORMAT){
		if(! (m_strInput.equals(str_IN) ) ){
		  this.clean();
		  this.m_strInput = str_IN;
		  if( !m_blnInitialized ) this.init();
		  determineFormat();
		}
		return this.to(FORMAT);
	}

	/**
	 * For use without having to create a new object each time:
	 * @return String in the format specified through parameter FORMAT.
	 * @param str_IN: String to convert,
	 * @param FORMAT_IN: format of str_IN,
	 * @param FORMAT_OUT: format for output (see constructor for details).
	 **/
	public String convert(String str_IN, byte FORMAT_IN, byte FORMAT_OUT){
		if(! (m_strInput.equals(str_IN) ) ){
		  this.clean();
		  this.m_strInput = str_IN;
		  if( !m_blnInitialized ) this.init();
		  m_FORMAT		= FORMAT_IN;
	    }
	    return this.to (FORMAT_OUT);
	}

	/**
	 * @return the String with markup in Html, TeX, or UnivIS. If
	 * the format of the loaded String is indeterminate (contains no markup),
	 * the input-String is returned unchanged.
  	 * @param byte f can have one of these values:<br>
  	 *  private static final byte FORMAT_HTML		= 0;<br>
	 *  private static final byte FORMAT_TeX		= 1;<br>
	 *  private static final byte FORMAT_UnivIS		= 2;<br>
	 *  private static final byte FORMAT_RTF		= 3 [not yet implemented];<br>
	 *  private static final byte FORMAT_Ascii		= 4;[not yet implemented]<br>
	 *  private static final byte FORMAT_Text		= 5;[not yet implemented]
	 **/
	public String to(byte f){

		if((m_FORMAT == FORMAT_UNKNOWN) || (m_FORMAT == f) ||(m_FORMAT == FORMAT_Text)) return this.m_strInput;

		if(m_strOutput[f]==null){
		  String strReturn = m_strInput;

		  // if the transformation is from UnivIS to something else,
		  // and if the String contains an enumeration, there
		  if ((m_FORMAT == FORMAT_UnivIS) && (f != FORMAT_UnivIS) && (m_strInput.indexOf("\n-")>=0 )){
			  strReturn = markEnumerationBorders( strReturn );
		  }

		  for(int ii=0;ii <= m_TypeCount; ii++){
		  	  strReturn = reMark(strReturn, m_TABLE[m_FORMAT][ii][LEFT], m_TABLE[m_FORMAT][ii][RIGHT], m_TABLE[f][ii][LEFT], m_TABLE[f][ii][RIGHT]);
		  }

		  // get rid of \\shjEnum-- escape sequences in
		  // UnivIS-format:
		  if(f == FORMAT_UnivIS) strReturn = shjSubstitute((shjSubstitute( strReturn, "\\shjEnumStart", "" )), "\\shjEnumStop", "");
		  m_strOutput[f] = strReturn;
	     }
		 return m_strOutput[f];
	}

	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 7.   C O N S T R U C T O R S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	public Markup(){}

	/**
	 * Instantiate object and tell it what its format is. Format can
	 * be one of these values:
  	 *  private static final byte FORMAT_HTML		= 0;<br>
	 *  private static final byte FORMAT_TeX		= 1;<br>
	 *  private static final byte FORMAT_UnivIS		= 2;<br>
	 *  private static final byte FORMAT_RTF		= 3 [not yet implemented];<br>
	 *  private static final byte FORMAT_Ascii		= 4;[not yet implemented]<br>
	 *  private static final byte FORMAT_Text		= 5;[not yet implemented]
	 **/
	public Markup(String strInput, byte format){
		this.m_strInput	= strInput;
		init();
		this.m_FORMAT	= format;
	}

	/**
	 * Instantiate object and let it find out about its format by itself.
	 **/
	public Markup(String strInput){
		this.m_strInput = strInput;
		init();
		determineFormat();
	}

  }//end class

