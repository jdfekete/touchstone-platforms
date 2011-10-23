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

import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <code>CharacterFactor</code> is the base class to extend in order
 * to define new categorical factors whose value is a String or a Java object.
 * 
 * <p>
 * To define a new categorical factor, you must define the factor and its
 * values. Suppose you want to define a factor technique having the two 
 * values {SDAZ,OZ}. First, define the factor itself:
 * <pre>
 * &#47;**
 * * &#64;touchstone.factor T
 * *	name: "Technique"
 * *	help: "The pointing technique"
 * *&#47;
 * public class TechniqueFactor extends CharacterFactor {
 *	public TechniqueFactor() { super("T"); }
 * }
 * </pre>
 * Second, define the values. By default, they are simple strings but you can also define
 * more complex values using the touchstone tags as below:
 * <pre>
 * &#47;**
 * * &#64;touchstone.value SDAZ
 * *	factor: T
 * *	name : "SpeedDependantAutomaticZooming"
 * *	help: "rate-based scrolling with zoom factor adapted to the scrolling speed"
 * *&#47;
 * public class SDAZTechnique implements Technique {
 *	public SDAZTechnique() { super("SDAZ"); }
 *	... // methods for the SDAZ navigation technique
 * }
 * </pre>
 * <pre>
 * &#47;**
 * * &#64;touchstone.value OZ
 * *	factor: T
 * *	name : "OrthoZoom"
 * *	help: "scrolling along the colinear dimension while zooming along the orthogonal direction"
 * *&#47;
 * public class OZTechnique implements Technique {
 *	public OZTechnique() { super("OZ"); }
 *	... // methods for the OZ navigation technique
 * }
 * </pre>
 * <p>
 * Now, an instruction in the XML experiment script which contains 
 * <code>values="T=SDAZ"</code> (i.e.
 * <code>&lt;block values="T=SDAZ" ... /&gt;</code>) will cause the run platform
 * storing the <code>SDAZTechnique</code> object for factor T so that the
 * developer in the run platform can use the following instruction to obtain this object:
 * 
 * <pre>
 * Technique technique = (Technique)Platform.getInstance().getFactorValue("T");
 * </pre>
 * 
 * <code>technique</code> now refers to a <code>SDAZTechnique</code> object 
 * (n.b. <code>SDAZTechnique</code> implements the interface <code>Technique</code>
 * that contains common methods for multi-scale navigation techniques). Note that this
 * kind of programming allows to avoid multiple tests on the value of a <code>CharacterFactor</code>
 * (e.g. for installing the correct technique given an value id such as "OZ" or "SDAZ").
 * </p>
 * 
 * 
 * <p>
 * This mechanism also allows one to add new values to a predefined factor. For example,
 * to add the standard Pan&Zoom technique, one can export a plugin containing the 
 * following class:
 * <pre>
 * &#47;**
 * * &#64;touchstone.value PZ
 * *	factor: T
 * *	name : "Pan and Zoom"
 * *	help: "scroll using panning and zoom using the mouse wheel"
 * *&#47;
 * public class PZTechnique implements Technique {
 *	public PZTechnique() { super("PZ"); }
 * }
 * </pre>
 * </p>
 * 
 * @see NumericalFactor
 * 
 * @author Caroline Appert
 *
 */
public abstract class CharacterFactor extends Factor {

	private static final Logger LOG = Logger.getLogger(CharacterFactor.class.getName());
	
	/**
	 * The mapping between a value id and its corresponding Java object.
	 */
	private HashMap<String, Object> values = new HashMap<String, Object>();
	
	/**
	 * Builds a factor given its id.
	 * @param id the factor id (that must be used in the experiment script).
	 */
	public CharacterFactor(String id) {
		super(id);
	}
    
}
