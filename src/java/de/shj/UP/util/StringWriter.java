package de.shj.UP.util;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class StringWriter {
	
	private String m_sTextToWrite = "";
	
	public void write(String sPath) throws Exception{
		BufferedWriter	out		= new BufferedWriter(new FileWriter(sPath));
		out.write(m_sTextToWrite);
		out.close();
	}
	
	public StringWriter(String sTextToWrite){
		m_sTextToWrite = sTextToWrite;
	}
}
