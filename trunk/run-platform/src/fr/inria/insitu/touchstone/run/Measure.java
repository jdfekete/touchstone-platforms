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
 * <p>
 * <code>Measure</code> is the base class to extend in order
 * to define new measures in the touchstone platform.
 * </p>
 * 
 * <p>
 * To define a new measure, you just have to build an instance
 * of <code>Measure</code> and register it to the platform
 * using the method <code>addMeasure</code> of the class
 * <code>Platform</code>. Suppose you define a view object that
 * is able to provide a <i>scale</i> measure. Your view class
 * can be:
 * <pre>
 * // Declares that a view is able to export a scale measure
 * &#47;**
 * * &#64;touchstone.measure scale
 * *	name: "Scale ratio"
 * *	help: "The current view scaling ratio"
 * *	type: number
 * *&#47; 
 * public class AbstractView {
 * 	...
 * 	public AbstractView() {
 * 		...
 * 		// Builds a new Measure object and registers it to the platform.
 * 		Platform.getInstance().addMeasure(new Measure("scale") {
 *			public Object getValue() {
 *				return getScale();
 *			}
 *		});
 *	}
 * }
 * </pre>
 * 
 * <p>
 * Experiment instructions such as <code>&lt;measure id=&lt;measure_id&gt; ... /&gt;</code>
 * asks the run platform to log a the measure <code>&lt;measure_id&gt;</code>.
 * <ul> 
 * <li> <code>&lt;measure id=scale ... log="ok" /&gt;</code>
 * makes the run platform log the value of the measure <code>scale</code>
 * be logged each time a trial ends (<i>trial level</i>). 
 * <li> <code>&lt;measure id=&lt;measure_id&gt; ... cine_log="ok" /&gt;</code>
 * makes the run platform log the value of the measure <code>scale</code>
 * be logged each time it changes (<i>cinematic level</i>).
 * </ul>
 * The experiment script can ask to log any measure registered in the
 * platform or any axis managed by the current input manager. Run 
 * <code>LaunchExperiment</code> to know which axes are currently available. 
 * </p>
 * 
 * <p>
 * The tags &#64;touchstone.measure are used to describe the 
 * new measures so that they can be selected in the design
 * platform.
 * </p>
 * 
 * @author Caroline Appert
 *
 */
public class Measure {
	
	private String id;
	protected Object value = null;
	
	/**
	 * Builds a new Measure.
	 * @param id The measure id
	 */
	public Measure(String id) {
		this.id = id;
	}

	/**
	 * @return the id of the measure.
	 */
	public final String getID() {
		return id;
	}
	
	/**
	 * @return the value of the measure.
	 */
	public Object getValue() {
		return value;
	}
	
	/**
	 * Resets the value of this measure.
	 * This method is called each time a trial begins.
	 */
	public void resetValue() { }
	
	/**
	 * Sets the value of this measure.
	 * WARNING (2012-01-20): this method no longer writes a new line in the event log. Use the <code>setValueAndLog</code> to write a new line in the event log.  
	 * A measure has such a method because factors are also
	 * registered as measures to be logged and their value
	 * are set from the experiment script.
	 * @param value The value to set
	 */
	public void setValue(Object value) {
		this.value = value;
		if(Platform.getInstance().getCinematicLogger().contains(id))
			Platform.getInstance().getCinematicLogger().log();
	}
	
	/**
	 * Sets the value of this measure and record a line in the event log.
	 * A measure has such a method because factors are also
	 * registered as measures to be logged and their value
	 * are set from the experiment script.
	 * @param value The value to set
	 */
	public void setValueAndLog(Object value) {
		this.value = value;
		if(Platform.getInstance().getCinematicLogger().contains(id))
			Platform.getInstance().getCinematicLogger().log();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object obj) {
		return id.equals(((Measure)obj).id);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {
		return id.hashCode();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return id+"="+getValue();
	}
	
	/**
	 * Increments this measure if it is a <code>IntegerMeasure</code>.
	 * If it is not a <code>IntegerMeasure</code>, this method returns null.
	 * @param delta the delta to add to the current value.
	 * @return the value of this measure as an Integer.
	 */
	public long incr(long delta) {
		if(getValue() instanceof Integer)
			setValue(getIntValue() + delta);
		else if(getValue() instanceof Long)
			setValue(getLongValue() + delta);
		return getLongValue();
	}
	
	/**
	 * Increments this measure if it is a <code>IntegerMeasure</code>.
	 * If it is not a <code>IntegerMeasure</code>, this method returns null.
	 * @param delta the delta to add to the current value.
	 * @return the value of this measure as an Integer.
	 */
	public long incr(int delta) {
		return incr((long)delta);
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

}
