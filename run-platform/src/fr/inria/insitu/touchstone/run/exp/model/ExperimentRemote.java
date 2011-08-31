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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import fr.inria.insitu.touchstone.run.Platform;

public class ExperimentRemote extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private Experiment experiment; 
	private JButton previousTrial;
	private JButton nextTrial;
	private JButton previousBlock;
	private JButton nextBlock;
	private JButton resendOSCMessage;
	private RemoteListener remoteListener;
	
	private class RemoteListener implements ActionListener {
		public RemoteListener() { }
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == previousTrial) {
				previousTrial();
				return;
			}
			if(e.getSource() == nextTrial) {
				nextTrial();
				return;
			}
			if(e.getSource() == previousBlock) {
				previousBlock();
				return;
			}
			if(e.getSource() == nextBlock) {
				nextBlock();
				return;
			}
			if(e.getSource() == resendOSCMessage) {
				resendLastOSCMessage();
				return;
			}
		}
	}
	
	public ExperimentRemote(Experiment experiment) {
		super("Experiment remote");
		this.experiment = experiment;
		setAlwaysOnTop(true);
		getContentPane().setLayout(new BorderLayout());
		JPanel previousAndNextButtons = new JPanel(new GridLayout(2, 2));
		remoteListener = new RemoteListener();
		previousTrial = new JButton("previous trial");
		previousTrial.addActionListener(remoteListener);
		previousAndNextButtons.add(previousTrial);
		nextTrial = new JButton("next trial");
		nextTrial.addActionListener(remoteListener);
		previousAndNextButtons.add(nextTrial);
		previousBlock = new JButton("previous block");
		previousBlock.addActionListener(remoteListener);
		previousAndNextButtons.add(previousBlock);
		nextBlock = new JButton("next block");
		nextBlock.addActionListener(remoteListener);
		previousAndNextButtons.add(nextBlock);
		resendOSCMessage = new JButton("resend last OSC message"); // resend the last OSC message that has been sent to all clients
		resendOSCMessage.addActionListener(remoteListener);
		getContentPane().add(previousAndNextButtons, BorderLayout.CENTER);
		getContentPane().add(resendOSCMessage, BorderLayout.SOUTH);
		pack();
		setVisible(true);
	}
	
	private void endCurrentExperimentPart() {
		if(experiment.getCurrentState().getName().compareTo("interblock") == 0) {
			experiment.getCurrentBlock().done();
		} else if(experiment.getCurrentState().getName().compareTo("intertrial") == 0) {
			experiment.getCurrentBlock().done(); 
			experiment.getCurrentBlock().done();
		} else if(experiment.getCurrentState().getName().compareTo("trial") == 0) {
			experiment.getCurrentBlock().done();
		}
	}
	
	public void nextTrial() {
		Integer numBlock = (Integer)Platform.getInstance().getMeasureValue(Experiment.MEASURE_EXPERIMENT_BLOCK);
    	Integer numTrial = (Integer)Platform.getInstance().getMeasureValue(Experiment.MEASURE_EXPERIMENT_TRIAL);
    	Integer nbTrials = (Integer)Platform.getInstance().getMeasureValue(Experiment.MEASURE_EXPERIMENT_NB_TRIAL);
		if(numTrial.intValue() == nbTrials.intValue()) experiment.goTo(numBlock+1, 1);
    	else experiment.goTo(numBlock, numTrial+1);
		endCurrentExperimentPart();
	}
	
	public void previousTrial() {
		Integer numBlock = (Integer)Platform.getInstance().getMeasureValue(Experiment.MEASURE_EXPERIMENT_BLOCK);
    	Integer numTrial = (Integer)Platform.getInstance().getMeasureValue(Experiment.MEASURE_EXPERIMENT_TRIAL);
    	if(numTrial == 1) {
    		String participant = (String)Platform.getInstance().getMeasureValue(Experiment.MEASURE_EXPERIMENT_PARTICIPANT);
    		int nbTrials = experiment.getNbTrials(participant, numBlock);
    		experiment.goTo(numBlock-1, nbTrials);
    	} else {
    		experiment.goTo(numBlock, numTrial-1);
    	}
    	endCurrentExperimentPart();
	}
	
	public void nextBlock() {
		Integer numBlock = (Integer)Platform.getInstance().getMeasureValue(Experiment.MEASURE_EXPERIMENT_BLOCK);
    	experiment.goTo(numBlock+1, 1);
    	endCurrentExperimentPart();
	}
	
	public void previousBlock() {
		Integer numBlock = (Integer)Platform.getInstance().getMeasureValue(Experiment.MEASURE_EXPERIMENT_BLOCK);
    	experiment.goTo(numBlock-1, 1);
    	endCurrentExperimentPart();
	}
	
	public void resendLastOSCMessage() {
		Platform.getInstance().reSendLastOSCMessage();
	}

}
