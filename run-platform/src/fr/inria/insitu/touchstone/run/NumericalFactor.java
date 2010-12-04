/*   TouchStone run platform is a software to run lab experiments. It is         *
 *   published under the terms of a BSD license (see details below)              *
 *   Author: Caroline Appert (appert@lri.fr)                                     *
 *   Copyright (c) 2010 Caroline Appert and INRIA, France.                       *
 *   TouchStone run platform reuses parts of an early version which were         *
 *   programmed by Jean-Daniel Fekete under the terms of a MIT (X11) Software    *
 *   License (Copyright (C) 2006 Jean-Daniel Fekete and INRIA, France)           *
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
package fr.inria.insitu.touchstone.run;

/**
 * <code>NumericalFactor</code> is the base class to extend in order
 * to define new numerical factors whose value is a number.
 * 
 * <p>
 * To define a new numerical factor, you just have to tag a subclass of
 * <code>NumericalFactor</code>:
 * <pre>
 * &#47;**
 * * &#64;touchstone.factor N
 * *	name: "nbElements"
 * *	help: "Number of elements in the set"
 * *&#47;
 * public class NFactor extends NumericalFactor {
 *	public NFactor() { super("N"); }
 * }
 * </pre>
 * </p>
 * 
 * <p>
 * When the value of a numerical factor is set from the XML experiment
 * script, it first attempts to parse the string as an integer and, if it fails,
 * it attempts to parse the string as a double. Once the string value is parsed,
 * the value of a <code>NumericalFactor</code> is set with the parsed number.
 * </p>
 * 
 * <p>
 * Then, to retrieve the value of this <code>NumericalFactor</code>, the developer 
 * can call (as with a <code>CharacterFactor</code>) the <code>getFactorValue(String idFactor)</code>
 * to get the current numerical value of this factor, e.g. :
 * <pre>
 * Platform.getInstance().getFactor("N").getLongValue();
 * </pre>
 * or
 * <pre>
 * Platform.getInstance().getFactor("N").getIntValue();
 * </pre>
 * or
 * <pre>
 * Platform.getInstance().getFactor("N").getDoubleValue();
 * </pre>
 * </p>
 * 
 * @author Caroline Appert
 *
 */
public class NumericalFactor extends Factor {

	/**
	 * Builds a <code>NumericalFactor</code>.
	 * @param id the ident of this factor.
	 */
	public NumericalFactor(String id) {
		super(id);
	}
	
	/**
	 * Returns the value of this <code>Factor</code> given the id value.
	 * @param idValue The id value
	 * @return the value having key <code>idValue</code> of this factor.
	 */
	protected final Object getValue(String idValue) {
		try {
			return Integer.parseInt(idValue);
		} catch(NumberFormatException exc1) {
			try {
				return Long.parseLong(idValue);
			} catch(NumberFormatException exc2) {
				return Double.parseDouble(idValue);
			}
		}
	}

}
