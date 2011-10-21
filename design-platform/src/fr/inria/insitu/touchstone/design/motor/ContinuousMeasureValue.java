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
import java.text.DecimalFormat;

public class ContinuousMeasureValue extends MeasureValue implements Serializable {
	
	private double minValue;
	private double maxValue;
	private boolean integer;
	
	private DecimalFormat format = new DecimalFormat("###.##");
	
	public ContinuousMeasureValue(
			double minValue,
			double maxValue,
			boolean integer) {
		this.integer = integer;
		this.maxValue = maxValue;
		this.minValue = minValue;
	}
	
	// [minValue, maxValue]
	public ContinuousMeasureValue(String pattern, boolean integer) throws Exception {
		String pat = pattern.trim();
		if(!(pat.charAt(0)=='[' && pat.charAt(pat.length()-1)==']'))
			throw new Exception("The expression for a continuous value should start with a '[' and end with a ']'");
		String[] parts = pat.split(",");
		String minVal = parts[0].trim();
		minVal = minVal.substring(1, minVal.length()).trim();
		this.minValue = Double.parseDouble(minVal);
		String maxVal = parts[1].trim();
		maxVal = maxVal.substring(0, maxVal.length()-1).trim();
		this.maxValue = Double.parseDouble(maxVal);
		if(this.maxValue < this.minValue)
			throw new Exception("The maximum value should be upper to the minumum value.");
		this.integer = integer;
	}
	
	String getRandomValue() {
		double val = Math.random()*(maxValue - minValue) + minValue;
		return integer ? ""+Math.round(val) : format.format(val);
	}

	public double getMinValue() {
		return minValue;
	}

	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

	public boolean isInteger() {
		return integer;
	}

	public void setInteger(boolean integer) {
		this.integer = integer;
	}
	
	public String toString() {
		return "["+minValue+", "+maxValue+"]";
	}

}
