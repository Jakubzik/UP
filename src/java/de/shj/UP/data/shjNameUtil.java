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
 * @version 5-27-05x
 * change date              change author           change description
 * ===================================================================
 * version 5-27
 *
 * Nov-01-2004		    	h. jakubzik
 *
 */

package de.shj.UP.data;

/**
 * This class is to help untangle name, first-name and title
 **/
public class shjNameUtil{

	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 1.   P R I V A T E  D E C L A R A T I O N S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	public  String m_strDebug			= "";

	private String m_strInput			= "";

	private String m_strFirst			= "";
	private String m_strLast			= "";
	private String m_strTitle			= "";

	private boolean m_blnInitialized	= false;




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

	/**
	 * @return first name.
	 **/
	public String getFirstName(){
		return m_strFirst;
	}

	/**
	 * @return last name.
	 **/
	public String getLastName(){
		return m_strLast;
	}

	/**
	 * @return (academic) title
	 **/
	public String getTitle(){
		return m_strTitle;
	}

	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 5.   P R I V A T E  M E T H O D S (UTILS)
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------


	/**
	 * Tries to parse the name.
	 * Typical are two types of input:
	 * (1) FirstName [ ] LastName	(recognizable through blank in between),
	 * (2) LastName, FirstName		(recognizable through comma in between).
	 **/
	private void parse(){
		if(m_strInput.indexOf("Prof. Dr.") >= 0){
			m_strTitle = "Prof. Dr.";
			m_strInput = m_strInput.substring(0,m_strInput.indexOf("Prof. Dr.")).trim() + m_strInput.substring(m_strInput.indexOf("Prof. Dr.") + 9).trim();
		}

		if(m_strInput.indexOf("Priv.-Doz. Dr.") >= 0){
			m_strTitle = "Priv.-Doz. Dr.";
			m_strInput = m_strInput.substring(0,m_strInput.indexOf("Priv.-Doz. Dr.")).trim() + m_strInput.substring(m_strInput.indexOf("Priv.-Doz. Dr.") + 14).trim();
		}

		if(m_strInput.indexOf("Prof.") >= 0){
			m_strTitle = "Prof.";
			m_strInput = m_strInput.substring(0,m_strInput.indexOf("Prof.")).trim() + m_strInput.substring(m_strInput.indexOf("Prof.") + 5).trim();
		}

		if(m_strInput.indexOf("Dr.") >= 0){
			m_strTitle = "Dr.";
			m_strInput = m_strInput.substring(0,m_strInput.indexOf("Dr.")).trim() + m_strInput.substring(m_strInput.indexOf("Dr.") + 3).trim();
		}

		if(m_strInput.indexOf(",") >=0){
			m_strFirst = m_strInput.substring(m_strInput.indexOf(",")+1).trim();
			m_strLast  = m_strInput.substring(0, m_strInput.indexOf(",")).trim();
		}else{
		  if(m_strInput.indexOf(" ") >=0){
			m_strLast  = m_strInput.substring(m_strInput.indexOf(" ")+1).trim();
			m_strFirst = m_strInput.substring(0, m_strInput.indexOf(" ")).trim();
		  }
		}

		if(m_strLast.equals("")) m_strLast = m_strInput.trim();
	}


	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	// 7.   C O N S T R U C T O R S
	// ------------------------------ ------------------------------
	// ------------------------------ ------------------------------
	public shjNameUtil(){}

	public shjNameUtil(String strInput){
		this.m_strInput	= strInput;
		this.parse();
	}

  }//end class

