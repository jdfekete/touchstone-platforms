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

public class MeasureSet extends Step implements Serializable {

	private static final long serialVersionUID = 42L;
	
	private Vector<Measure> measures = new Vector<Measure>();
	
	public MeasureSet() {
		super();
	}

	protected Object clone() throws CloneNotSupportedException {
		MeasureSet copy = new MeasureSet();
		for (Iterator<Measure> iterator = measures.iterator(); iterator.hasNext();)
			copy.addMeasure((Measure)iterator.next().clone());
		return copy;
	}
	
	public void addMeasure(Measure measure){
		measures.add(measure);
	}
	public void removeMeasure(Measure measure){
		measures.remove(measure);
	}	
	
	public Vector<Measure> getMeasures() {
		return measures;
	}
	
	
	public boolean equals(Object obj) {
		if (obj instanceof MeasureSet)
			return measures.equals(((MeasureSet)obj).getMeasures());
		else 
			return false;
	}
	
	public String toXML(){
		String result = "";
		for (Measure m : measures)
			result += m.toXML();
		return result;
	}

	public void toXML(DocumentImpl xmlDoc, Element parent) {
		for (Measure m : measures)
			m.toXML(xmlDoc, parent);
	}	
}