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

import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import fr.inria.insitu.touchstone.run.Platform.EndCondition;
import fr.inria.insitu.touchstone.run.utils.BasicFactory;

/**
 * <code>Factor</code> is the base class to extend in order
 * to define new factors. Usually, it is not recommended to use
 * this class but rather its two subclasses (<code>CharacterFactor</code>
 * and <code>NumericalFactor</code>). 
 * 
 * @see CharacterFactor
 * @see NumericalFactor
 * @author Caroline Appert
 *
 */
public abstract class Factor {

	private static final Logger LOG = Logger.getLogger(Factor.class.getName());
	protected Object value;
	protected String id;

	protected static Vector<Factor> allFactors = new Vector<Factor>();

	/**
	 * Builds a factor given its id.
	 * @param id the factor id (that must be used in the experiment script).
	 */
	protected Factor(String id) {
		this.id = id;
		allFactors.add(this);
//		System.out.println("add factor "+id);
	}

	/**
	 * @return the current value of this <code>Factor</code>.
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Called when the value of this factor is set from an experiment script.
	 * Overrides it to specify specific treatments when setting the value a factor.
	 * @param value the value to set.
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	public Double getDoubleValue() {
		if(getValue() instanceof Number)
			return ((Number)getValue()).doubleValue();
		else {
			return Double.parseDouble(getValue().toString());
		}
	}

	public Long getLongValue() {
		if(getValue() instanceof Number)
			return ((Number)getValue()).longValue();
		else {
			return (long)Double.parseDouble(getValue().toString());
		}
	}

	public Integer getIntValue() {
		return getLongValue().intValue();
	}

	public String getStringValue() {
		if(getValue() == null) return "null";
		return getValue().toString();
	}

	/**
	 * Called when the value of this factor is set from an experiment script.
	 * @param value the key of the value to set.
	 */
	public void setKeyValue(String keyValue) {
		BasicFactory factoryForFactor = FactoriesForValues.getFactor(id);
		if(factoryForFactor != null)
			try {
				Object value = factoryForFactor.createFor(keyValue, null); 
				setValue(value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		else
			setValue(keyValue);
	}

	public static Factor getFactor(String idFactor) {
		for (Iterator<Factor> iterator = allFactors.iterator(); iterator.hasNext();) {
			Factor factor = iterator.next();
			if(factor.id.equals(idFactor)) {
				return factor;
			}
		}
		return null;
	}

}
