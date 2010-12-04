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
package fr.inria.insitu.touchstone.run.input;

import java.util.EventObject;
import java.util.Iterator;
import java.util.Set;

import fr.inria.insitu.touchstone.run.Platform;

/**
 * <b>AxesEvent</b> reports a list of axes that changed
 * in an InputEnvironment.
 * 
 */
@SuppressWarnings("serial")
public class AxesEvent extends EventObject {
	protected Set<String> axesModified;
	protected InputEnvironment source;
	
	/**
	 * Creates an AxesEvent with all the fields.
	 * @param axesModified the list of axes modified at this tine
	 * @param env the InputEnvironment where that occured
	 */
	public AxesEvent(
			Set<String> axesModified, 
			InputEnvironment env) {
		super(Platform.getInstance());
		this.axesModified = axesModified;
		this.source = env;
	}
	
	/**
	 * Returns the controller name portion of a specified axis.
	 * 
	 * <p>The controller name if the part of the name before the dot,
	 * so for the axis <code>Mouse.x</code>, it returns <code>"Mouse"</code>.
	 * 
	 * @param axis
	 *            the axis
	 * 
	 * @return the controller name portion of the axis
	 */
	public static String getAxisComponent(String axis) {
		int dot = axis.indexOf('.');
		if (dot == -1) return "";
		return axis.substring(0, dot);
	}
	
	/**
	 * Returns the component name portion of a specified axis.
	 * 
	 * <p>The component name is the part of the name after the dot,
	 * do for the axis <code>Mouse.x</code>, it returns <code>"x"</code>.
	 * 
	 * @param axis
	 *            the axis
	 * 
	 * @return the component name portion of the axis
	 */
	public static String getAxisController(String axis) {
		int dot = axis.indexOf('.');
		if (dot == -1) return axis;
		return axis.substring(dot+1);        
	}
	
	/**
	 * @return the source
	 */
	public InputEnvironment getSource() {
		return source;
	}
	
	/**
	 * @param source the source to set
	 */
	public void setSource(InputEnvironment source) {
		this.source = source;
	}
	
	/**
	 * @return the axesModified
	 */
	public Set<String> getAxesModified() {
		return axesModified;
	}
	
	/**
	 * @param axesModified the axesModified to set
	 */
	public void setAxesModified(Set<String> axesModified) {
		this.axesModified = axesModified;
	}
	
	/**
	 * Returns true if the specified axis has been modified since
	 * the last event.
	 * @param axis the axis
	 * @return true if the specified axis has been modified since
	 * the last event
	 */
	public boolean isAxisModified(String axis) {
		return axesModified != null && axesModified.contains(axis);
	}
	
	/**
	 * @return Returns an iterator over the axis modified since
	 * the last event
	 */
	public Iterator<String> axesModifiedIterator() {
		if (axesModified != null) {
			return axesModified.iterator();
		}
		return null;
	}
	
	/**
	 * @return the later time when one of the axis has been modified.
	 */
	public long getLastTime() {
		long last = 0;
		for (Iterator<String> iter = axesModifiedIterator(); iter.hasNext(); ) {
			String axis = iter.next();
			last = Math.max(last, getSource().getAxisTime(axis));
		}
		return last;
	}
	
	/**
	 * @return Returns an Iterator over the axis names.
	 * @see fr.inria.insitu.touchstone.run.input.InputEnvironment#axisIterator()
	 */
	public Iterator<String> axisIterator() {
		return source.axisIterator();
	}
	
	/**
	 * Return the last time the specified axis has been set.
	 * @param axis the axis
	 * @return the last time the specified axis has been set
	 * @see fr.inria.insitu.touchstone.run.input.InputEnvironment#getAxisTime(java.lang.String)
	 */
	public long getAxisTime(String axis) {
		return source.getAxisTime(axis);
	}
	
	/**
	 * Returns the value fo the specified axis.
	 * @param axis the axis
	 * @return the value fo the specified axis
	 * @see fr.inria.insitu.touchstone.run.input.InputEnvironment#getAxisValue(java.lang.String)
	 */
	public double getAxisValue(String axis) {
		return source.getAxisValue(axis);
	}
	
	/**
	 * Returns true if the specified axis is defined.
	 * @param axis the axis
	 * @return returns true if the specified axis is defined
	 * @see fr.inria.insitu.touchstone.run.input.InputEnvironment#isAxisDefined(java.lang.String)
	 */
	public boolean isAxisDefined(String axis) {
		return source.isAxisDefined(axis);
	}

}
