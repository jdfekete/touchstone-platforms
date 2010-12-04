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
package fr.inria.insitu.touchstone.run.exp.model;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.illposed.osc.OSCMessage;

import fr.inria.insitu.touchstone.run.Platform;
import fr.inria.insitu.touchstone.run.Platform.EndCondition;
import fr.inria.insitu.touchstone.run.endConditions.ErrorEndCondition;

/**
 * ExperimentPart is a part of an experiment.
 * 
 * @author Caroline Appert
 *
 */
public class ExperimentPart {

	ActionListener endListener = new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			if(arg0.getSource() instanceof ErrorEndCondition)
				onError(((EndCondition)arg0.getSource()).getEndCondition());
			done();
		}

	};
	
	private Experiment experiment;
	private EndCondition endCondition = null;
	
	/**
	 * Builds a ExperimentPart.
	 */
	public ExperimentPart() { }

	/**
	 * Returns the experiment.
	 * @return The experiment.
	 */
	public Experiment getExperiment() {
		if(experiment == null) {
			return Platform.getExperiment();
		}
		return experiment;
	}
	
	/**
	 * Links this part to an experiment.
	 * @param exp The experiment
	 */
	void setExperiment(Experiment exp) {
		experiment = exp;
	}
	
	/**
	 * Returns the platform.
	 * @return The platform.
	 */
	public Platform getPlatform() {
		return Platform.getInstance();
	}
	
//	this should be called by the state machine of the interaction technique when the current interaction is done:
	/**
	 * Calls to end this part and process a new event
	 * parsed from the experiment script file.
	 */
	public final void done () {
		experiment.processEvent("done");
	}
	

	
	/**
	 * Sets the end condition of this experiment part.
	 * @param ec The end condition
	 */
	public void setEndCondition(EndCondition ec) {
		endCondition = ec;
		getPlatform().setEndCondition(endCondition);
	}

	/**
	 * Returns the end condition of this experiment part.
	 * @return The end condition. If this end condition has not explicitely been set, it sets a default <code>HitMissEndCondition</code> and returns it.
	 */
	public EndCondition getEndCondition() {
		if(endCondition == null)
			setEndCondition(new HitMissEndCondition(HitMissEndCondition.HIT));
		return endCondition;
	}
	
	/**
	 * Method called when an error is detected.
	 * By default, this method does nothing, overrides it
	 * to specify an error treatment.
	 * 
	 * @param errorType The name of the error
	 */
	public void onError(String errorType) {
	}
	
	/**
	 * Method called when OSC is enabled, to get the arguments of the message 
	 * that will be sent to OSC clients during an experiment phase. Returns null by default, 
	 * overrides it to specify your own arguments.
	 * 
	 * @param messageType the type of the message that will be sent (see OSC messages address constants in {@link Platform}).
	 * @return array of arguments to add to the message (accepted types are Float, String, Integer, BigInteger and arrays of this types).
	 */
	public Object[] getOSCMessageArguments(String messageType) {
		return null;
	}
	
	protected final void sendOSCMessage(String type) {
		OSCMessage message = new OSCMessage();
		message.setAddress(type);
		Object[] arguments = getOSCMessageArguments(type);
		if (arguments != null) {
			for (Object arg : arguments) {
				if (arg != null) {
					message.addArgument(arg);
				}
			}
		}
		Platform.getInstance().sendOSCMessage(message);		
	}

}
