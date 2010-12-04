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
import java.util.Iterator;
import java.util.Vector;

import org.w3c.dom.Element;

import com.sun.org.apache.xerces.internal.dom.DocumentImpl;

public class Factor implements Cloneable, Serializable {

	private static final long serialVersionUID = 42L;
	
	private String fullName="";
	private String shortName="";
	private FactorRole role = FactorRole.key;
	private MeasureType type = MeasureType.String;
	private String tag = "Within Subject";
	private String help = null;
	private Vector<Value> values = new Vector<Value>();

	public Factor(){}
	
	public Factor(String fullName, String shortName, FactorRole role, MeasureType type, Vector<Value> values, String tag) {
		super();
		this.fullName = fullName;
		this.shortName = shortName;
		this.role = role;
		this.type = type;
		this.values = values;
		this.tag = tag;
	}

	public Object clone() throws CloneNotSupportedException {
		Factor f = new Factor();
		f.setFullName(fullName);
		f.setShortName(shortName);
		f.setRole(role);
		f.setType(type);
		for (Iterator<Value> iterator = values.iterator(); iterator.hasNext();) {
			Value next = iterator.next();
			f.addValue(next.getShortValue(), next.getFullValue());
		}
		return f;
	};

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	
	public String toString() {
		return shortName;
	}	

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public FactorRole getRole() {
		return role;
	}

	public void setRole(FactorRole role) {
		this.role = role;
	}

	public MeasureType getType() {
		return type;
	}

	public void setType(MeasureType type) {
		this.type = type;
	}

	public Vector<Value> getValues() {
		return values;
	}

	public void addValue(Value v){
		for (Iterator<Value> iterator = values.iterator(); iterator.hasNext();) {
			Value next = iterator.next();
			if(next.getShortValue().compareTo(v.getShortValue()) == 0)
				return;
		}
		values.add(v);
	}
	
	public void addValue(String shortName, String fullName){
		addValue(new Value(shortName,fullName,this));
	}

	
	public boolean equals(Object obj) {
		if ((obj != null)&&(obj.getClass() == Factor.class)){
			Factor factor = (Factor) obj;

			if (((factor.fullName == null && fullName == null) || (factor.fullName != null && factor.fullName.equals(fullName)))
					&& (factor.shortName.equals(shortName))
					&& ((factor.type == null && type == null) || (factor.type != null && factor.type.equals(type)))
					&& (factor.values.equals(values))
			)
				return true;
			else
				return false;
		}
		else
			return false;
	}
	
	public String toXML(){
		String result = "";
		result += "<factor id=\""+shortName+"\" name=\""+fullName+"\" kind=\""+role+"\" type=\""+type+"\" tag=\""+tag+"\">\n";
		for (Value v : values)
			result += "\t<value id=\""+v.getShortValue()+"\"/>\n";
		result+="</factor>\n";
		return result;
	}
	
	public void toXML(DocumentImpl xmlDoc, Element parent){
		Element factor = xmlDoc.createElementNS(null, "factor");
		factor.setAttributeNS(null, "id", shortName);
		factor.setAttributeNS(null, "name", fullName);
		factor.setAttributeNS(null, "kind", ""+role);
		factor.setAttributeNS(null, "type", ""+type);
		factor.setAttributeNS(null, "tag", tag);
		for (Value v : values)
			v.toXML(xmlDoc, factor);
		parent.appendChild(factor);
	}

	public String getHelp() {
		return help;
	}

	public void setHelp(String help) {
		this.help = help;
	}
	
}

