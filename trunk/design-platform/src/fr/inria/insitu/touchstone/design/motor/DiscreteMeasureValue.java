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
import java.util.Vector;

public class DiscreteMeasureValue extends MeasureValue implements Serializable {

	private Vector<String> values = new Vector<String>();

	public DiscreteMeasureValue(Vector<String> values) {
		super();
		this.values = values;
	}
	
	// {val1, ..., valn}
	public DiscreteMeasureValue(String pattern, MeasureType type) throws Exception {
		String blanksRemoved = pattern.trim();
		if(blanksRemoved.charAt(0) != '{') throw new Exception("Expression "+blanksRemoved+" does not start with a '{'");
		int index = blanksRemoved.indexOf('}');
		if(index != (blanksRemoved.length()-1)) throw new Exception("Expression "+blanksRemoved+" does not end with a '}'");
		String onlyValues = blanksRemoved.substring(1, blanksRemoved.length()-1);
		String[] parts = onlyValues.split(",");
		if(parts.length == 0) throw new Exception("Expression "+blanksRemoved+" is malformed.");
		for (int i = 0; i < parts.length; i++) {
			String val = parts[i].trim();
			if(type.equals(MeasureType.Integer))
				Integer.parseInt(val);
			if(type.equals(MeasureType.Float))
				Double.parseDouble(val);
			values.add(val);
			
		}
	}
	
	String getRandomValue() {
		int randomIndex = (int)Math.round(Math.random()*(values.size()));
		randomIndex = randomIndex == values.size() ? randomIndex - 1 : randomIndex;
		return values.get(randomIndex);
	}

	public String toString() {
		String str = "{";
		for (int i = 0; i < values.size(); i++) {
			String val = values.get(i);
			if(i != 0)
				str+=(", "+val);
			else
				str+=val;
		}
		return str+"}";
	}
}
