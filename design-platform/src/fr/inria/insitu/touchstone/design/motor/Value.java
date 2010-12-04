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

public class Value implements Cloneable, Serializable {
	
	private static final long serialVersionUID = 42L;
	
	private String shortValue = "";
	private String fullValue = "";
	private Factor factor = null;

	public static char separator = '=';
	
	public static final Value BLOCK_VALUE = new Value("Block value", "Block value", null);
	public static final Value SAMPLE = new Value("Sample", "Sample", null);

	/**
	 * Create a factor's value
	 * @param shortValue the short name of the value
	 * @param fullValue the full name of the value
	 * @param factor associated factor
	 */
	public Value(String shortValue, String fullValue, Factor factor){
		this.shortValue = shortValue;
		this.fullValue = fullValue;
		this.factor = factor;
	}
	
	public Object clone() {
		return new Value(shortValue, fullValue, factor);
	}
	
	public String toString() {
		if(factor != null) {
			String res = ""+factor+separator+shortValue;
			if(res.compareTo(""+separator)==0) res = ""; // factor factice
			return res;
		} else
			return shortValue;
	}

	public String getShortValue() {
		return shortValue;
	}

	public String getFullValue() {
		return fullValue;
	}
	
	public Factor getFactor() {
		return factor;
	}
	
	
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if (obj.getClass() == Value.class){
			Value v = (Value) obj;
			
			if (v.shortValue.equals(shortValue)
					&& v.fullValue.equals(fullValue)
					&& v.factor.getRole().equals(factor.getRole())
					&& v.factor.getFullName().equals(factor.getFullName())
					&& v.factor.getShortName().equals(factor.getShortName())
					&& v.factor.getType().equals(factor.getType())
					&& (v.factor.getValues().size()==factor.getValues().size())
				)
				return true;
			else
				return false;
		}
		else
			return false;
	}

	public void toXML(DocumentImpl xmlDoc, Element parent) {
		Element value = xmlDoc.createElementNS(null, "value");
		value.setAttributeNS(null, "id", getShortValue());
		value.setAttributeNS(null, "name", getFullValue());
		parent.appendChild(value);
	}

	public void setFactor(Factor factor) {
		this.factor = factor;
	}

	public boolean isSample() {
		return shortValue.compareTo(SAMPLE.shortValue) == 0
		&& fullValue.compareTo(SAMPLE.fullValue) == 0;
	}	

	public boolean isBlockValue() {
		return shortValue.compareTo(BLOCK_VALUE.shortValue) == 0
		&& fullValue.compareTo(BLOCK_VALUE.fullValue) == 0;
	}
	
}
