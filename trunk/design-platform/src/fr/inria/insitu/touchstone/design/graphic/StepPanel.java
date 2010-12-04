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
package fr.inria.insitu.touchstone.design.graphic;

import java.awt.Dimension;

import javax.swing.JPanel;

import fr.inria.insitu.touchstone.design.motor.Experiment;
import fr.inria.insitu.touchstone.design.motor.Step;


public abstract class StepPanel<S extends Step> extends JPanel {
	
	private static final long serialVersionUID = 42L;
	
	protected Experiment experiment;
	private int depth;
	protected boolean needJSP = true;
	
	private DesignPlatform designPlatform;
	
	public StepPanel(DesignPlatform designPlatform, Experiment experiment, int depth){
		this.experiment = experiment;
		this.depth = depth;
		this.designPlatform = designPlatform;
	}
	
	/**
	 * 
	 * @return the step constructed with the data displayed in the StepPanel
	 */
	public S getStep(){
		return null;
	}
	
	/** 
	 * @return an int representing the depth of the step associated to this StepPanel in the experiment
	 */
	public int getDepth(){
		return depth;
	}
	
	/**
	 * 
	 * @return the experiment being constructed
	 */
	public Experiment getExperiment() {
		return experiment;
	}
	
	/**
	 * Set the root of the experiment being constructed.
	 * @param experiment
	 */
	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}
	
	/** 
	 * @return true if the StepPanel should be displayed in a JScrollPane
	 */
	public boolean needScrollPane(){
		return needJSP;
	}
	
	public abstract void display();
	
	public abstract void save();
	
	/**
	 * @return null if the step can be created from the StepPanel, else the string that describes the problem.
	 */
	public String getStatus(){		
		return null;
	}		
	
	public DesignPlatform getDesignPlatform() {
		return designPlatform;
	}
	
	public abstract void updateExperimentPreview();
	
	public abstract void hiliteExperimentPreview();
	
	@Override
	public Dimension getPreferredSize() {
		Dimension d = super.getPreferredSize();
		if(d.height < 500) d.height = 500;
		if(d.width < 800) d.width = 800;
		return d;
	}
	
}
