package de.shj.UP.beans.config.student;

import de.shj.UP.data.Modul;
import de.shj.UP.data.Leistung;

/**
* This bean encapsulates a simple 1X1 matrix of a  com.shj.signUp.data.Modul to a 
* com.shj.signUp.data.Leistung.
* It is used by StudentPruefungBean to hand back information on missing credits in a given modul.
* In order to do so, there are a number of pending worker-beans, so to speak, all 
* of which share the following diagram:<br /><br />
* 
* StudentPruefungBean<br />
* |<br />
* |----- PruefungXModulBean<br />
* |<br />
* |<br />
* |----- <b>ModulXLeistung</b><br />
* <br />
**/
public class ModulXLeistung{

	private Modul m_modul;
	private Leistung m_leistung;
	
	public Modul Modul(){
		return m_modul;
	}
	
	public Leistung Leistung(){
		return m_leistung;
	}
	
	public ModulXLeistung(Modul m, Leistung l){
		this.m_modul=m;
		this.m_leistung=l;
	}
}
