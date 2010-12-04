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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.inria.insitu.touchstone.run.Platform;
import fr.inria.insitu.touchstone.run.input.Axes;
import fr.inria.insitu.touchstone.run.input.AxesEvent;
import fr.inria.insitu.touchstone.run.input.AxesListener;
import fr.inria.insitu.touchstone.run.input.InputManager;

/**
 * <code>CinematicLogger</code> manages cinematic logging.
 * (<a href="http://museumi.lri.fr:2002/touchstone/run/ide/developComponents/">Link to tutorial</a>).
 * 
 * <h2> Cinematic log and standard AWT input event system </h2>
 * Here, when a component is registered, the default cinematic measures ("Mouse.x", "Mouse.y", etc.) are set using a standard mouse and key listener. To register a component to the Platform, use:

Platform.getInstance().registerComponent(component); 
Once a component registered, each time it receives an input event, the Platform object writes a cinematic log line and evaluates the end criterion using the method isReached(InputEvent) (see section 4 of this page to get more details about end criteria).

The experiment uses TouchStone generalized input system: In this case, when the XML experiment script asks for logging an input axis, the Platform object automatically installs a listener to monitor this axis. Each time an input axis (physical or virtual) which is listened by the Platform is modified, the Platform writes a cinematic log line and evaluates the end criterion using the method isReached(AxesEvent) (see section 4 of this page to get more details about end criteria and page TouchStone generalized input to get more details about input system).
 * <p>
 * When TouchStone run platform 
 * 
 * @author Caroline Appert
 *
 */
@SuppressWarnings("serial")
public class CinematicLogger extends LinkedList<String> {

	private FileWriter fout; 
	private PrintWriter logOut;
	/** Logger. */
	private static final Logger   LOG                    = Logger.getLogger(CinematicLogger.class.getName());
	private String                logSeparator           = "\t";
	
	private boolean on = false;
	
	private String headerComment = "";
	private String types = "";
	
	ArrayList<Object> lastLoggedLine = null;
	
	private AxesListener listener = new AxesListener() {
		public void axesChanged(AxesEvent e) {
			long time = e.getAxisTime(e.getAxesModified().iterator().next());
			Platform.getInstance().setMeasureValue("currentTime", time);
			log();
		}
	};
	private Axes listened = new Axes();
	
	/**
	 * Builds a CinematicLogger.
	 */
	public CinematicLogger() {
		super();
	}
	
	/**
	 * Add a comment <code># idExperiment:idName</code>
	 * in the header of the cinematic log file.
	 * @param id the experiment id
	 * @param name the experiment name
	 */
	public void setExperiment(String id, String name) {
		headerComment+=("# "+id+":"+name+"\n");
	}
	
	/**
	 * Register a cinematic measure.
	 * @param id The measure id
	 * @param type The measure type
	 * @param name The measure name
	 */
	public void addMeasure(String id, String type, String name) {
		if(contains(id)) return;
		headerComment+=("# "+id+":"+name+"\n");
		types+=type+"\t";
		add(id);
		if(InputManager.getInstance().isAxisDefined(id)) {
			InputManager.getInstance().removeAxesListener(listened, listener);
			listened.add(id);
			InputManager.getInstance().addAxesListener(listened, listener);
		}
	}
	
	/**
	 * Init logging.
	 */
	public void init() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		String date = format.format(new Date());
		File rootDir = new File(Platform.getInstance().getMeasureValue(Experiment.MEASURE_EXPERIMENT_NAME)+File.separator+"logs"+File.separator+"cinematic");
		if(!rootDir.exists()) rootDir.mkdirs();
        
		String filename = rootDir+File.separator+"cinematic-" + Platform.getInstance().getMeasureValue(Experiment.MEASURE_EXPERIMENT_PARTICIPANT) + " - " + date + ".log";
		
		try {
			fout = new FileWriter(filename);
			logOut = new PrintWriter(new BufferedWriter(fout));
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Cannot open file " + filename
					+ " for writing", e);
			return;
		}
		logOut.write(headerComment);
		logOut.write("# Date: "+date+"\n");
		logOut.write("# Participant: "+Platform.getInstance().getMeasureValue(Experiment.MEASURE_EXPERIMENT_PARTICIPANT)+"\n");
		logOut.write("block");
		logOut.write(logSeparator);
		logOut.write("trial");
		logOut.write(logSeparator);
		for(Iterator<String> i = iterator(); i.hasNext(); ) {
			String next = i.next();
			logOut.write(next);
			logOut.write(logSeparator);
		}
		logOut.println();
		logOut.write(types);
		logOut.println();
		logOut.flush();
		lastLoggedLine = new ArrayList<Object>(size()+2);
		for(int i=0; i <(size()+2); i++) {
			lastLoggedLine.add(i, "");
		}
	}
	
	/**
	 * Write one cinematic log line.
	 */
	public void log() {
		if(!on) return;
		boolean log = false;
		int cpt = 2;
		// if it is a new trial, we log 
		Object block = Platform.getInstance().getMeasureValue("block");
		Object trial = Platform.getInstance().getMeasureValue("trial");
		log = !block.equals(lastLoggedLine.get(0))
		|| !trial.equals(lastLoggedLine.get(1));
		// if it is not a new trial, we log only if there is a measure whose value has changed
		if(!log) {
			for(Iterator<String> i = iterator(); i.hasNext(); ) {
				String nextMeasure = i.next();
				Object measured = Platform.getInstance().getMeasureValue(nextMeasure);
				if(measured != null) {
					if(!measured.equals(lastLoggedLine.get(cpt))) {
						log = true;
						break;
					}
				} else {
					if(lastLoggedLine.get(cpt) != null) {
						log = true;
						break;
					}
				}
				cpt++;
			}
		}
		if(!log) return;
		logOut.write(block.toString());
		lastLoggedLine.set(0, block);
		logOut.write(logSeparator);
		logOut.write(trial.toString());
		lastLoggedLine.set(1, trial);
		logOut.write(logSeparator);
		String next;
		cpt = 2;
		Object tmp = "null";
		for(Iterator<String> i = iterator(); i.hasNext(); ) {
			next = i.next();
			tmp = Platform.getInstance().getMeasureValue(next);
			if(tmp == null) tmp = "null";
			logOut.write(tmp.toString());
			lastLoggedLine.set(cpt, tmp);
			if(i.hasNext()) logOut.write(logSeparator);
			cpt++;
		}
		logOut.println();
		logOut.flush();
	}
	
	/**
	 * Resume logging. 
	 */
	public void resume() {
		on = true;
	}
	
	/**
	 * Suspend logging. 
	 */
	public void suspend() {
		on = false;
	}
	
	/**
	 * Stop logging (close the file).
	 */
	public void stop() {
		logOut.close();
	}

}
