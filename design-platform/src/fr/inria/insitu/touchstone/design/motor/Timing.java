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

public class Timing extends Step implements Serializable {

	private static final long serialVersionUID = 42L;
	
	private Vector<String> criteria = new Vector<String>();
	private Vector<String> practiceCriteria = new Vector<String>();
	private Vector<Intertitle> intertitles = new Vector<Intertitle>();
	private Vector<Intertitle> practiceIntertitles = new Vector<Intertitle>();
	private Vector<Integer> estimatedTime = new Vector<Integer>();
	private Vector<Integer> practiceEstimatedTime = new Vector<Integer>();
	
	public static final int INDEX_BEGIN_BLOCKS = 3;
	
	public Timing(Vector<String> criteria, Vector<Intertitle> intertitles, Vector<Integer> estimatedTime, Vector<String> practiceCriteria, Vector<Intertitle> practiceIntertitles, Vector<Integer> practiceEstimatedTime) {
		super();
		this.criteria = criteria;
		this.intertitles = intertitles;
		this.estimatedTime = estimatedTime;
		this.practiceCriteria = practiceCriteria;
		this.practiceIntertitles = practiceIntertitles;
		this.practiceEstimatedTime = practiceEstimatedTime;
	}
	
	protected Object clone() throws CloneNotSupportedException {
		Vector<String> criteriaCopy = new Vector<String>();
		for (Iterator<String> iterator = criteria.iterator(); iterator.hasNext();)
			criteriaCopy.add(iterator.next());
		Vector<Intertitle> intertitlesCopy = new Vector<Intertitle>();
		for (Iterator<Intertitle> iterator = intertitles.iterator(); iterator.hasNext();) {
			Intertitle next = iterator.next();
			if(next == null) {
				intertitlesCopy.add(null);
			} else
				intertitlesCopy.add((Intertitle)next.clone());
		}
		Vector<Integer> estimatedTimeCopy = new Vector<Integer>();
		for (Iterator<Integer> iterator = estimatedTime.iterator(); iterator.hasNext();)
			estimatedTimeCopy.add(iterator.next());
		
		Vector<String> criteriaPracticeCopy = new Vector<String>();
		for (Iterator<String> iterator = practiceCriteria.iterator(); iterator.hasNext();)
			criteriaPracticeCopy.add(iterator.next());
		Vector<Intertitle> intertitlesPracticeCopy = new Vector<Intertitle>();
		for (Iterator<Intertitle> iterator = practiceIntertitles.iterator(); iterator.hasNext();) {
			Intertitle next = iterator.next();
			if(next == null) {
				intertitlesPracticeCopy.add(null);
			} else
				intertitlesPracticeCopy.add((Intertitle)next.clone());
		}
		Vector<Integer> estimatedPracticeTimeCopy = new Vector<Integer>();
		for (Iterator<Integer> iterator = practiceEstimatedTime.iterator(); iterator.hasNext();)
			estimatedPracticeTimeCopy.add(iterator.next());
		return new Timing(criteriaCopy, intertitlesCopy, estimatedTimeCopy, criteriaPracticeCopy, intertitlesPracticeCopy, estimatedPracticeTimeCopy);
	}

	public Vector<String> getCriteria() {
		return criteria;
	}

	public Vector<Intertitle> getIntertitles() {
		return intertitles;
	}

	public Vector<Integer> getEstimatedTime() {
		return estimatedTime;
	}
	
	public Vector<Intertitle> getPracticeIntertitles() {
		return practiceIntertitles;
	}

	public Vector<Integer> getPracticeEstimatedTime() {
		return practiceEstimatedTime;
	}

	public boolean equals(Object obj) {
		return obj.getClass() == Timing.class;
	}
	
	public Intertitle getSetupClass() {
		return intertitles.get(0);
	}
	
	public String getSetupCriterion() {
		return criteria.get(0);
	}
	
	public String getCriterionTrial() {
		return criteria.get(1);
	}
	
	public Intertitle getIntertrialClass() {
		return intertitles.get(2);
	}
	
	public String getIntertrialCriterion() {
		return criteria.get(2);
	}

	public Vector<String> getPracticeCriteria() {
		return practiceCriteria;
	}
	
}
