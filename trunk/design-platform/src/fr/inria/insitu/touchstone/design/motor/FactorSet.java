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


public class FactorSet extends Step implements Cloneable, Serializable {

	private static final long serialVersionUID = 42L;
	
	private Vector<Factor> factors;

	public FactorSet() {
		super();
		this.factors = new Vector<Factor>();
	}
	
	public FactorSet(Vector<Factor> factors) {
		super();
		this.factors = factors;
	}
	
	public boolean containsFactor(String id) {
		for (Iterator<Factor> iterator = factors.iterator(); iterator.hasNext();) {
			Factor next = iterator.next();
			if(next.getShortName().compareTo(id) == 0)
				return true;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	protected Object clone() throws CloneNotSupportedException {
		Vector<Factor> clonedFactors = new Vector<Factor>();
		for (Iterator<Factor> iterator = factors.iterator(); iterator.hasNext();) {
			Factor next = iterator.next();
			clonedFactors.add((Factor)next.clone());
		}
		return new FactorSet(clonedFactors);
	}
	
	/**
	 * Add a factor to this FactorSet
	 * @param factor the factor to be added
	 */
	public void addFactor(Factor factor){
		factors.add(factor);
	}

	/**
	 * Remove a factor from this FactorSet
	 * @param factor the factor to be removed
	 */
	public void removeFactor(Factor factor){
		factors.remove(factor);
	}

	/**
	 * 
	 * @return the factors of this FactorSet
	 */
	public Vector<Factor> getFactors(){
		return factors;
	}

	/**
	 * 
	 * @return the number of Within Subject Factors in this FactorSet
	 */
	public int getNumberOfWithinSubjFactors(){
		int i = 0;
		for (Factor f : factors)
			if (f.getTag().equalsIgnoreCase("Within Subject"))
				i++;
		return i;
	}

	/**
	 * 
	 * @return A vector containing the within subject factors in this FactorSet
	 */
	public Vector<Factor> getWithinSubjectFactors(){
		Vector<Factor> result = new Vector<Factor>();
		for (Factor f : factors)
			if (f.getTag().equalsIgnoreCase("Within Subject"))
				result.add(f);
		return result;		
	}

	/**
	 * 
	 * @return A vector containing the between subject factors in this FactorSet
	 */
	public Vector<Factor> getBetweenSubjectFactors(){
		Vector<Factor> result = new Vector<Factor>();
		for (Factor f : factors)
			if (f.getTag().equalsIgnoreCase("Between Subject"))
				result.add(f);
		return result;		
	}

	public String toXML(){
		String result = "";
		for (Factor f : factors )
			result += f.toXML();
		return result;
	}
	
	public void toXML(DocumentImpl xmlDoc, Element parent){
		for (Factor f : factors )
			f.toXML(xmlDoc, parent);
	}
	
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj.getClass() == FactorSet.class){
			FactorSet fs = (FactorSet) obj;
			if ((fs.getFactors().size() == this.getFactors().size())
					&&(fs.getWithinSubjectFactors().size()== this.getWithinSubjectFactors().size())){
				return true;
			}
			else
				return false;
		}
		else
			return false;
	}
	
}
