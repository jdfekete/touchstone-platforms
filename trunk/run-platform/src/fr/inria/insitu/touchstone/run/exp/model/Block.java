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

import fr.inria.insitu.touchstone.run.Measure;
import fr.inria.insitu.touchstone.run.Platform;
import fr.inria.insitu.touchstone.run.Platform.EndCondition;
import fr.inria.insitu.touchstone.run.endConditions.ErrorEndCondition;

/**
 * Block is a block of an experiment.
 * 
 * @touchstone.measure MT
 *      name: "Movement time"
 *      help: "The end time of the current trial"
 *      type: integer
 * @touchstone.measure HIT
 *      name: "Success"
 *      help: "Hit or Miss of the current trial"
 *      type: string
 * @touchstone.measure time
 *      name: "Time from start"
 *      help: "The time elapsed since the evaluation started, in milliseconds"
 *      type: integer
 * @touchstone.measure trialStarted
 *      name: "Time when trial started"
 *      help: "The time when the trial started"
 *      type: integer
 * @touchstone.measure trialEnded
 *      name: "Time when trial ended"
 *      help: "The time when the trial ended"
 *      type: integer
 * @touchstone.measure currentTime
 *      name: "Instantaneous time"
 *      help: "The current time when a line is logged"
 *      type: integer
 * @touchstone.measure experiment
 *      name: "Experiment name"
 *      type: string
 *      help: "The name name_experiment of the experiment found in <experiment id=name_experiment .../>"
 * @touchstone.measure participant
 *      name: "Experiment participant"
 *      type: string
 *      help: "The id of the current participant found in the <run id=id_participant.../>"
 * @touchstone.measure block
 *      name: "Experiment block"
 *      type: integer
 *      help: "The number of the current block"
 * @touchstone.measure trial
 *      name: "Experiment trial"
 *      type: integer
 *      help: "The number of the current trial"
 * @touchstone.measure nbBlocks
 *      name: "Number of blocks for current participant"
 *      type: integer
 *      help: "The number of blocks for current participant"
 * @touchstone.measure nbTrials
 *      name: "Number of trials for current block"
 *      type: integer
 *      help: "The number of trials for current block"
 * @touchstone.measure inPractice
 *      name: "practice block"
 *      type: string
 *      help: "True if in practice, false otherwise"
                  
 * @author Caroline Appert
 *
 */
public class Block extends ExperimentPart {
	private long time;
	private long trialStarted;
	private long trialEnded;
	private boolean practice;
	
	/**
	 * Builds a Block.
	 */
	public Block() {
		super();
	}
	
	void setPractice(boolean practice) {
		this.practice = practice;
	}
	
	boolean isPractice() {
		return practice;
	}
    
    /**
     * @return the MT
     */
    public long getMT() {
        return System.currentTimeMillis() - time;
    }
	
	//	all the following methods can be redefined in subclasses.
	// typically, a derived class will implement a state machine managing input events :
	// beginBlock would set up the canvas and create the state machine
	// endBlock would destroy the state machine
	// beginTrial would configure the canvas for the next trial
	// endTrial would clean up after a trial
	// The state machine should at some point call done() to signal that the trial is over.
	
    final void doBeginBlock() {
    	Platform.getInstance().addMeasure(new Measure("MT") {
            public Object getValue() {
                return new Long(getMT());
            }
            public void resetValue() {
                time = System.currentTimeMillis();
            }
        });
        Platform.getInstance().addMeasure(new Measure("HIT") {
            public Object getValue() {
                return getEndCondition().getEndCondition();
            }
        });
        Platform.getInstance().addMeasure(new Measure("trialStarted") {
			public Object getValue() {
				return new Long(trialStarted);
			}
		});
        Platform.getInstance().addMeasure(new Measure("trialEnded") {
			public Object getValue() {
				return new Long(trialEnded);
			}
		});
        beginBlock();
		if (getPlatform().isOSCEnabled()) {
			sendOSCMessage(Platform.OSC_START_BLOCK_ADDRESS);
		}
    }
    
	/**
	 * Calls when this block begins.
	 * By default, this method does nothing.
	 * Overrides it to specify a treatment when the block begins,
	 * typically to set parameters of the platform that depend
	 * on factors values.
	 */
	public void beginBlock () { 
		// called before starting a new block, with data from script file
	}
	
	final void doBeginTrial() {
		beginTrial();
		if (getPlatform().isOSCEnabled()) {
			sendOSCMessage(Platform.OSC_START_TRIAL_ADDRESS);
		}
		getPlatform().setEndCondition(getEndCondition());
		getPlatform().resetAllMeasures();
		trialStarted = System.currentTimeMillis();
		getPlatform().addActionListener(endListener);
		if(getEndCondition()!=null) getEndCondition().start();
		getPlatform().resumeCinematicLog();
	}
	
	final void doEndBlock () { 
		endBlock();
		if (getPlatform().isOSCEnabled()) {
			sendOSCMessage(Platform.OSC_END_BLOCK_ADDRESS);
		}
	}
	
	/**
	 * Calls when this block ends.
	 * By default, this method does nothing.
	 * Overrides it to specify a treatment when the block ends,
	 * typically to set parameters of the platform that depend
	 * on factors values.
	 */
	public void endBlock () { 
		// called when the block ends
	}
	
	/**
	 * Calls when a trial of this block begins.
	 * By default, this method does nothing.
	 * Overrides it to specify a treatment when the block begins,
	 * typically to set parameters of the platform that depend
	 * on factors values.
	 */
	public void beginTrial () { 
		// called before starting a new trial, with data from script file
	}
	
	final void doEndTrial() {
		trialEnded = System.currentTimeMillis();
		getExperiment().log();
		getPlatform().suspendCinematicLog();
		getPlatform().removeActionListener(endListener);
		if(getEndCondition()!=null) getEndCondition().stop();
		endTrial(getEndCondition());
		if (getPlatform().isOSCEnabled()) {
			sendOSCMessage(Platform.OSC_END_TRIAL_ADDRESS);
		}
		if(getEndCondition() instanceof ErrorEndCondition)
			error(getEndCondition());
	}
	
	/**
	 * Calls when a trial of this block ends.
	 * By default, this method does nothing.
	 * Overrides it to specify a treatment when the block ends,
	 * typically to set parameters of the platform that depend
	 * on factors values.
	 * 
	 * @param ec The EndCondition
	 */
	public void endTrial (EndCondition ec) { 
		// called when trial is done
	}
	
	/**
	 * Calls when a trial of this block ends with an error.
	 * By default, this method does nothing.
	 * Overrides it to specify a treatment of errors.
	 * 
	 * @param ec The EndCondition
	 */
	public void error(EndCondition ec) { 
		// called when trial ends with an error
	}
	
	/**
	 * Sets the end condition to a default <code>HitMissEndCondition</code>
	 * with a hit value.
	 */
	public void hit() {
		setEndCondition(new HitMissEndCondition(HitMissEndCondition.HIT));
		done();
	}
	
	/**
	 * Sets the end condition to a default <code>HitMissEndCondition</code>
	 * with a miss value.
	 */
	public void miss() {
		setEndCondition(new HitMissEndCondition(HitMissEndCondition.MISS));
		done();
	}

	public long getStartTime() {
		return trialStarted;
	}
	
	/**
	 * {@inheritDoc}
	 */
//	public void axesChanged(AxesEvent e) {
//		getPlatform().getCinematicLogger().log();
//	}
	
}
