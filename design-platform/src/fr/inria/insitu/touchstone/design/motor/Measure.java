/*   TouchStone design platform is a software to design protocols for lab        *
 *   experiments. It is published under the terms of a BSD license               *
 *   (see details below)                                                         *
 *   Author: Caroline Appert (appert@lri.fr)                                     *
 *   Copyright (c) 2010 Caroline Appert and INRIA, France.                       *
 *   TouchStone design platform reuses parts of an early version which were      *
 *   programmed by Matthis Gilbert.                                              *
 *********************************************************************************/
/* Redistribution and use in source and binary forms, with or without            * 
 * modification, are permitted provided that the following conditions are met:   *

 *  - Redistributions of source code must retain the above copyright notice,     *
 *    this list of conditions and the following disclaimer.                      *
 *  - Redistributions in binary form must reproduce the above copyright notice,  *
 *    this list of conditions and the following disclaimer in the documentation  *
 *    and/or other materials provided with the distribution.                     *
 *  - Neither the name of the INRIA nor the names of its contributors   *
 * may be used to endorse or promote products derived from this software without *
 * specific prior written permission.                                            *

 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"   *
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE     *
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE    *
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE     *
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR           *
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF          *
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS      *
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN       *
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)       *
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE    *
 * POSSIBILITY OF SUCH DAMAGE.                                                   *
 *********************************************************************************/
package fr.inria.insitu.touchstone.design.motor;

import java.io.Serializable;

import org.w3c.dom.Element;

import com.sun.org.apache.xerces.internal.dom.DocumentImpl;

public class Measure implements Serializable {

	private static final long serialVersionUID = 42L;
	
	private boolean log = true;
	private boolean cinematic =true ;
	private String parent = "";
	private MeasureType type = MeasureType.String;
	private String id = "";
	private String name = "";

	private MeasureValue possibleValue; 
	
	public Measure(boolean cinematic, boolean log, String id,
			String parent, MeasureType type, String name) {
		super();
		this.cinematic = cinematic;
		this.log = log;
		this.parent = parent;
		this.type = type;
		this.id = id;
		this.name = name;
	}
	
	public boolean equals(Object obj) {
		if ((obj!=null)&&(obj.getClass() == Measure.class)){
			Measure measure = (Measure) obj;
			return ((this.log == measure.isLog())
					&&(cinematic==measure.isCinematic())
					&&(parent.equalsIgnoreCase(measure.getParent()))
					&&(type.equals(measure.getType()))
					&&(id.equalsIgnoreCase(measure.getId()))
					&&(name.equalsIgnoreCase(measure.getName()))
			);
		}
		else 
			return false;
	}

	
	public Object clone() {
		return new Measure(cinematic, log, id, parent, type, name);
	}

	public boolean isLog() {
		return log;
	}
	public void setLog(boolean log) {
		this.log = log;
	}
	public boolean isCinematic() {
		return cinematic;
	}
	public void setCinematic(boolean cinematic) {
		this.cinematic = cinematic;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public MeasureType getType() {
		return type;
	}
	public void setType(MeasureType type) {
		this.type = type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		return (log ? "[trial] " : "")+(cinematic ? "[event] " : "")+id;
	}
	
	public String oldToString() {
		return super.toString();
	}

	public String toXML(){
		String idMeasure = parent == null || parent.length() == 0 ? id :
			parent+"."+id;
		String result ="<measure id=\""+idMeasure+"\" name=\""+name+"\" type=\""+type+"\" ";
		if (log&&cinematic)  
			result += "cine_log = \"ok\"";
		else if (log)
			result+="log=\"ok\"";
		else if (cinematic)
			result+="cine=\"ok\"";
		result += "/> \n";
		return result;
	}

	public void toXML(DocumentImpl xmlDoc, Element parent) {
		Element measure = xmlDoc.createElementNS(null, "measure");
		if(this.parent.length() == 0)
			measure.setAttributeNS(null, "id", id);
		else
			measure.setAttributeNS(null, "id", this.parent+"."+id);
		measure.setAttributeNS(null, "name", name);
		measure.setAttributeNS(null, "type", ""+type);
		if (log&&cinematic)  
			measure.setAttributeNS(null, "cine_log", "ok");
		else if (log)
			measure.setAttributeNS(null, "log", "ok");
		else if (cinematic)
			measure.setAttributeNS(null, "cine", "ok");
		parent.appendChild(measure);
	}

	public MeasureValue getPossibleValue() {
		return possibleValue;
	}

	public void setPossibleValue(MeasureValue possibleValue) {
		this.possibleValue = possibleValue;
	}

}
