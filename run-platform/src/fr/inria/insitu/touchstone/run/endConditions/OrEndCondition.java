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
package fr.inria.insitu.touchstone.run.endConditions;

import fr.inria.insitu.touchstone.run.Platform.EndCondition;

/**
 * A OrEndCondition fires an action event 
 * when an input event checks one condition
 * OR another condition. In an experiment script, use the operator |.
 * 
 * @author Caroline Appert
 *
 */
public class OrEndCondition extends BinaryEndCondition {
	protected volatile EndCondition checked = null;
	
	/**
	 * Builds a OrEndCondition.
	 * @param ec1 The first condition
	 * @param ec2 The second condition
	 */
	public OrEndCondition(EndCondition ec1, EndCondition ec2) {
		super(ec1, ec2);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean eval(boolean v1, boolean v2) {
		if(v1 && checked == null) {
			checked = endCondition1;
		}
		if(v2 && checked == null) {
			checked = endCondition2;
		}
		return v1 || v2;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getEndCondition() {
		if(checked == null) return null;
		return checked.getEndCondition();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void start() {
		super.start();
		checked = null;
	}
}
