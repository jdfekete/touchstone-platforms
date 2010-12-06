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
import java.awt.event.InputEvent;
import java.io.File;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.Timer;

import org.xml.sax.Locator;

import com.illposed.osc.OSCMessage;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import fr.inria.insitu.touchstone.run.Factor;
import fr.inria.insitu.touchstone.run.Measure;
import fr.inria.insitu.touchstone.run.Platform;
import fr.inria.insitu.touchstone.run.Platform.EndCondition;
import fr.inria.insitu.touchstone.run.endConditions.AbstractEndCondition;
import fr.inria.insitu.touchstone.run.exp.defaults.ExperimentPartFactory;
import fr.inria.insitu.touchstone.run.exp.parse.DecomposeExpressionLexer;
import fr.inria.insitu.touchstone.run.exp.parse.DecomposeExpressionParser;
import fr.inria.insitu.touchstone.run.exp.parse.EndConditionLexer;
import fr.inria.insitu.touchstone.run.exp.parse.EndConditionParser;
import fr.inria.insitu.touchstone.run.exp.parse.GeneralDataExpXMLParse;
import fr.inria.insitu.touchstone.run.exp.parse.ModifiedEndConditionParser;
import fr.inria.insitu.touchstone.run.exp.parse.PartialXMLParse;
import fr.inria.insitu.touchstone.run.input.AxesEvent;
import fr.lri.swingstates.debug.StateMachineVisualization;
import fr.lri.swingstates.events.VirtualEvent;
import fr.lri.swingstates.sm.State;
import fr.lri.swingstates.sm.StateMachine;
import fr.lri.swingstates.sm.Transition;
import fr.lri.swingstates.sm.transitions.Event;
import fr.lri.swingstates.sm.*;
import fr.lri.swingstates.sm.transitions.*;
import fr.lri.swingstates.events.*;

/**
 * The experiment environment as a state machine.
 * 
 * @author Caroline Appert
 */
@SuppressWarnings("unused")
public class Experiment extends StateMachine {
	/** Logger. */
	private static final Logger   LOG                    = Logger.getLogger(Experiment.class.getName());

	private String nextCriterion;
	private Locator nextLocator;

	private LinkedList<Object[]> systemEvents;
	private int eventIndex = 0;

	private static Intertitle DEFAULT_INTERBLOCK = new Intertitle();
	private static Intertitle DEFAULT_INTERTRIAL = new Intertitle();

	//	The environment
	private Intertitle currentIntertrial;
	private Intertitle currentInterblock;
	private Intertitle experimentSetUp;
	private Block currentBlock;
	private String currentParticipant;

	private LinkedList<String> measureNames = new LinkedList<String>();
	private LinkedList<String> measureTypes = new LinkedList<String>();
	private String headerLogComment = "";
	private boolean first = true;
	private int numBlock = 0;
	private int numTrial = 0;
	private File script;

	private GeneralDataExpXMLParse generalData;

	/** The experiment name measure. */
	public static String MEASURE_EXPERIMENT_NAME = "experiment"; // always logged
	/** The participant name. */
	public static String MEASURE_EXPERIMENT_PARTICIPANT = "participant"; // always logged
	/** The current block number. */
	public static String MEASURE_EXPERIMENT_BLOCK = "block"; // always logged
	/** The current trial number. */
	public static String MEASURE_EXPERIMENT_TRIAL = "trial"; // always logged
	/** The number of blocks in the current run. */
	public static String MEASURE_EXPERIMENT_NB_BLOCK = "nbBlocks";
	/** The number of trials in the current block. */
	public static String MEASURE_EXPERIMENT_NB_TRIAL = "nbTrials";
	/** True if in practice, false otherwise. */
	public static String MEASURE_PRACTICE = "inPractice";
	/** Elapsed time since the beginning of the experiment. */
	public static String MEASURE_TIME = "time";
	/** Instantaneous time. */
	public static String MEASURE_CURRENT_TIME = "currentTime";

	public static String[] MEASURES_ALWAYS_DEFINED = 
	{ MEASURE_EXPERIMENT_NAME, MEASURE_EXPERIMENT_PARTICIPANT, MEASURE_EXPERIMENT_BLOCK, MEASURE_EXPERIMENT_TRIAL };
	public static String[] MEASURES_ALWAYS_AVAILABLE = 
	{ MEASURE_EXPERIMENT_NAME, MEASURE_EXPERIMENT_PARTICIPANT, MEASURE_EXPERIMENT_BLOCK, MEASURE_EXPERIMENT_TRIAL,
		MEASURE_EXPERIMENT_NB_BLOCK, MEASURE_EXPERIMENT_NB_TRIAL, MEASURE_PRACTICE};

	/**
	 * The command name of the <code>ActionEvent</code> sent when the experiment is finished.
	 */
	public static final String EXPERIMENT_FINISHED = "finished";
	/**
	 * The command name of the <code>ActionEvent</code> sent when the experiment is started.
	 */
	public static final String EXPERIMENT_STARTED = "started";

	private LinkedList<ActionListener> actionListeners = new LinkedList<ActionListener>();

	/**
	 * Add an <code>ActionListener</code> to this experiment.
	 * An experiment sends events when:
	 * <ul>
	 * <li> It is finished (command: <code>EXPERIMENT_FINISHED</code>)
	 * <li> It starts (command: <code>EXPERIMENT_STARTED</code>)
	 * </ul>
	 * 
	 * @param listener The <code>ActionListener</code> to add
	 */
	public void addActionListener(ActionListener listener) {
		actionListeners.add(listener);
	}

	/**
	 * Remove an <code>ActionListener</code> from this experiment.
	 * 
	 * @param listener The <code>ActionListener</code> to remove
	 */
	public void removeActionListener(ActionListener listener) {
		actionListeners.remove(listener);
	}

	/**
	 * Fire an <code>ActionEvent</code> to all the
	 * <code>ActionListener</code>s attached to this experiment.
	 * 
	 * @param event the command of the <code>ActionEvent</code>
	 */
	private void fireActionEvent(String event) {
		ActionEvent ae = new ActionEvent(this, event.hashCode(), event);
		for(Iterator<ActionListener> i = actionListeners.iterator(); i.hasNext(); ) {
			i.next().actionPerformed(ae);
		}
	}

	protected final void sendOSCMessage(String type) {
		OSCMessage message = new OSCMessage();
		message.setAddress(type);
		Platform.getInstance().sendOSCMessage(message);		
	}
	
	class SystemEvent extends VirtualEvent {

		private static final long serialVersionUID = 1L;

		Class<?> cl;
		String criterion;
		Locator locator;

		/**
		 * Builds a SystemEvent.
		 * @param n The name of the event
		 * @param c The class of the corresponding experiment part
		 * @param crit The criterion (expression on <code>EndCondition</code>s)
		 * @param locator The locator
		 */
		public SystemEvent(String n, Class<?> c, String crit, Locator locator) {
			super(n);
			cl = c;
			criterion = crit;
			this.locator= locator;
		}

	}

	public Experiment() {
		super();
		Platform.getInstance().addMeasure(new Measure(Experiment.MEASURE_EXPERIMENT_NAME) {
			public Object getValue() {
				return generalData.getExperimentName();
			}
		});
		Platform.getInstance().addMeasure(new Measure(Experiment.MEASURE_EXPERIMENT_PARTICIPANT) {
			public Object getValue() {
				return currentParticipant;
			}
		});
		Platform.getInstance().addMeasure(new Measure(Experiment.MEASURE_EXPERIMENT_BLOCK) {
			public Object getValue() {
				return new Integer(numBlock + 1);
			}
		});
		Platform.getInstance().addMeasure(new Measure(Experiment.MEASURE_EXPERIMENT_TRIAL) {
			public Object getValue() {
				return new Integer(numTrial + 1);
			}
		});
		Platform.getInstance().addMeasure(new Measure(Experiment.MEASURE_EXPERIMENT_NB_BLOCK) {
			public Object getValue() {
				return generalData.getNbBlocks(currentParticipant);
			}
		});
		Platform.getInstance().addMeasure(new Measure(Experiment.MEASURE_EXPERIMENT_NB_TRIAL) {
			public Object getValue() {
				return generalData.getNbTrials(currentParticipant, numBlock + 1);
			}
		});
		Platform.getInstance().addMeasure(new Measure(Experiment.MEASURE_PRACTICE) {
			public Object getValue() {
				return currentBlock.isPractice();
			}
		});
		Platform.getInstance().addMeasure(new Measure(Experiment.MEASURE_TIME) {
			public Object getValue() {
				return new Long(System.currentTimeMillis()-Platform.getInstance().getStartTime());
			}
		});
		Platform.getInstance().addMeasure(new Measure(Experiment.MEASURE_CURRENT_TIME) {
			public Object getValue() {
				return new Long(System.currentTimeMillis());
			}
		});
		Platform.getInstance().setExperiment(this);

		if(DEBUG) StateMachineVisualization.windowVisualization(this);
	}

	/**
	 * Builds a Experiment.
	 * @param script The file containing the script that describes this experiment
	 */
	public Experiment(File script) {
		this();
		this.script = script;
		generalData = new GeneralDataExpXMLParse(script);
	}

	private static final int BEGIN_RUN = 0;
	private static final int END_RUN = 1;
	private static final int BEGIN_BLOCK = 2;
	private static final int END_BLOCK = 3;
	private static final int BEGIN_TRIAL = 4;
	private static final int END_TRIAL = 5;
	private static final int BEGIN_INTERBLOCK = 6;
	private static final int END_INTERBLOCK = 7;
	private static final int BEGIN_INTERTRIAL = 8;
	private static final int END_INTERTRIAL = 9;
	private static final int OTHER = 10;
	private static final int BEGIN_SETUP = 11;
	private static final int END_SETUP = 12;
	private static final int GOTO_START_STATE = 13;
	private static final int GOTO_BLOCK_STATE = 14;


	private static final boolean DEBUG = false;

	private static int getType(Object[] event) {
		String nameEvent = ((String) event[0]);
		if(nameEvent.compareTo("run") == 0) {
			return event.length != 2 ? BEGIN_RUN : END_RUN;
		}
		if(nameEvent.compareTo("block") == 0 || nameEvent.compareTo("practice") == 0) {
			return event.length != 2 ? BEGIN_BLOCK : END_BLOCK;
		}
		if(nameEvent.compareTo("trial") == 0) {
			return event.length != 2 ? BEGIN_TRIAL : END_TRIAL;
		}
		if(nameEvent.compareTo("interblock") == 0) {
			return event.length != 2 ? BEGIN_INTERBLOCK : END_INTERBLOCK;
		}
		if(nameEvent.compareTo("intertrial") == 0) {
			return event.length != 2 ? BEGIN_INTERTRIAL : END_INTERTRIAL;
		}
		if(nameEvent.compareTo("setup") == 0) {
			return event.length != 2 ? BEGIN_SETUP : END_SETUP;
		}
		if(nameEvent.compareTo("goto start") == 0) {
			return GOTO_START_STATE;
		}
		if(nameEvent.compareTo("goto block") == 0) {
			return GOTO_BLOCK_STATE;
		}
		return OTHER;
	}

	/**
	 * Starts the experiment.
	 * @param participant The participant id
	 * @param blockBegin The block number
	 * @param trialBegin The trial number
	 */
	public void start(String participant, int blockBegin, int trialBegin) {
		new PartialXMLParse(this, script, participant, 1, 1);
		boolean in = false;
//		numBlock = blockBegin-1;
//		numTrial = trialBegin-1;
		numBlock = -1;
		numTrial = -1;
		currentParticipant = participant;
		Platform.getInstance().setVisible(true);
		fireActionEvent(EXPERIMENT_STARTED);
		sendOSCMessage(Platform.OSC_START_EXPERIMENT_ADDRESS);
		processSystemEvent();
		goTo(blockBegin, trialBegin);
	}

	/**
	 * @return The enumeration of participants identifiers.
	 */
	public Enumeration<String> getParticipantsIDs() {
		return generalData.getParticipantsID();
	}

	public int getNbBlocks(String participant) {
		return generalData.getNbBlocks(participant);
	}

	public int getNbTrials(String participant, int block) {
		return generalData.getNbTrials(participant, block);
	}

	private void setIntertrial(Object[] event) {
		if(event.length == 2) {
			currentIntertrial = null;
		} else {
			try {
				currentIntertrial = (Intertitle) newExperimentPart(event);
				if(currentIntertrial == null) currentIntertrial = DEFAULT_INTERTRIAL;
				Hashtable<String, String> hashtable = (Hashtable<String,String>)event[1];
				String criterion = hashtable.get("criterion");
				setCriterion(currentIntertrial, criterion, (Locator)event[2]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void setInterblock(Object[] event) {
		if(event.length == 2) {
			// means it is a closing tag: </interblock>
			currentInterblock = null;
		} else {
			try {
				currentInterblock = (Intertitle) newExperimentPart(event);
				if(currentInterblock == null) currentInterblock = DEFAULT_INTERBLOCK;
				String criterion = (String) ((Hashtable<?, ?>)event[1]).get("criterion");
				setCriterion(currentInterblock, criterion, (Locator)event[2]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void setSetupExperiment(Object[] event) {
		if(event.length == 2) {
			// means it is a closing tag: </setup>
			return;
		} else {
			try {
				experimentSetUp = (Intertitle) newExperimentPart(event);
				String criterion = (String) ((Hashtable)event[1]).get("criterion");
				// By default a setup is finished immediately (except if an end condition is explicitly specified)
				if(criterion == null)
					experimentSetUp.setEndCondition(new AbstractEndCondition() {
						public boolean isReached(AxesEvent e) { return true; }
						public boolean isReached(InputEvent e) { return true; }
						public boolean isReached(OSCMessage message, long when) { return true; }
						public boolean isReached(Timer timer, long when) { return true; }
					});
				else
					setCriterion(experimentSetUp, criterion, (Locator)event[2]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static EndCondition newCriterion(String criterion, Locator loc) throws Exception {
		EndCondition res = null;
		if(criterion == null) return null;
		EndConditionLexer lexer = new EndConditionLexer(
				new StringReader(criterion));
		if(loc != null) {
			lexer.setFilename(loc.getSystemId());
			lexer.setLine(loc.getLineNumber());
			lexer.setColumn(loc.getColumnNumber());
		}
		ModifiedEndConditionParser parser = new ModifiedEndConditionParser(lexer);
		res = parser.modifiedExpr();
		return res;
	}

	private void setCriterion(ExperimentPart ee, String criterion, Locator loc) {
		if(criterion == null) {
			return;
		}
		//		printLocator(loc);
		EndConditionLexer lexer = new EndConditionLexer(
				new StringReader(criterion));
		lexer.setFilename(loc.getSystemId());
		lexer.setLine(loc.getLineNumber());
		lexer.setColumn(loc.getColumnNumber());
		EndConditionParser parser = new EndConditionParser(lexer);
		EndCondition res = null;
		try {
			res = parser.expr();
			ee.setEndCondition(res);
		} catch (Exception e) {
			System.err.println("Cannot parse criterion "+criterion);
			e.printStackTrace();
		}

	}

	//	private static void printLocator(Locator l) {
	//	System.out.println("Locator: "+l);
	//	System.out.println("\tFile:"+l.getSystemId());
	//	System.out.println("\tLine:"+l.getLineNumber());
	//	System.out.println("\tColumn:"+l.getColumnNumber());
	//	}

	public ExperimentPart newExperimentPart(Object[] event) {
		ExperimentPart res = null;
		String className = ((Hashtable<String,String>)event[1]).get("class");
		Locator loc = (Locator)event[2];
		try {
			res = newExperimentPart(className, loc);
			res.setExperiment(this);
		} catch (Exception e) {
			System.out.println("Cannot parse expression "+className);
			//					e.printStackTrace();
		}
		return res;
	}

	public static ExperimentPart newExperimentPart(String className, Locator loc) throws Exception {
		ExperimentPart res = null;
		if(className == null) return null;
		EndConditionLexer lexer = new EndConditionLexer(
				new StringReader(className));
		if(loc != null) {
			lexer.setFilename(loc.getSystemId());
			lexer.setLine(loc.getLineNumber());
			lexer.setColumn(loc.getColumnNumber());
		}
		ModifiedEndConditionParser parser = new ModifiedEndConditionParser(lexer);
		parser.setFactory(ExperimentPartFactory.getInstance());
		res = (ExperimentPart) parser.modifiedFnCall();
		return res;
	}

	private void storeValues(Object[] event) {
		if(event.length >= 3) {
			String values = ((Hashtable<String,String>)event[1]).get("values");
			if(values != null) {
				Pattern pattern = Pattern.compile(",");
				String[] res = pattern.split(values);
				pattern = Pattern.compile("=");
				for(int i = 0; i < res.length; i++) {
					String[] tmp = pattern.split(res[i]);
					if (tmp.length < 2) {
						System.out.println("cannot store factor values: XML assignment is not of the form xxy=foo, it is " + res[i]);
					}
					Factor f = Platform.getInstance().getFactor(tmp[0].trim());
					if(f != null) {
						f.setKeyValue(tmp[1].trim());
					}
					else {
						// This factor has not been registered when loading factories definition
						// We register it as a measure
						Measure m = new Measure(tmp[0].trim());
						m.setValue(tmp[1].trim());
						Platform.getInstance().addMeasure(m);
					}
				}
			}
		}
	}

	/**
	 * Sets the list of system events to process to run this experiment.
	 * @param systemEvts The list of system events
	 */
	public void setSystemEvents(LinkedList<Object[]> systemEvts) {
		systemEvents = systemEvts;
	}

	private void setCurrentBlock(Block b) {
		currentBlock = b;
	}

	/**
	 * Parses the next event in the script and processes
	 * it in the state machine of this experiment.
	 */
	public void processSystemEvent() {
		if(systemEventsBlocked) return;

		VirtualEvent evt = null;
		// Having an event of length 2 means this event is an ending tag.
		while(evt == null) {
			if(eventIndex == systemEvents.size()) {
				System.out.println("EXPERIMENT FINISHED");
				sendOSCMessage(Platform.OSC_END_EXPERIMENT_ADDRESS);
				fireActionEvent(EXPERIMENT_FINISHED);
				return;
			}
			Object[] nextEvent = systemEvents.get(eventIndex);
			if(DEBUG) System.out.println((String) nextEvent[0]+"?...");
			switch(getType(nextEvent)) {
			case GOTO_START_STATE :
				systemEventsBlocked = true;
				setEnvironment(nextEvent);
				processEvent("reset");
				systemEventsBlocked = false;
				break;
			case GOTO_BLOCK_STATE :
				systemEventsBlocked = true;
				setEnvironment(nextEvent);
				processEvent("reset");
				processEvent("gotoblock");
				systemEventsBlocked = false;
				break;
			case BEGIN_SETUP :
				setSetupExperiment(nextEvent);
//				if(Platform.getInstance().getEndCondition() != null)
					evt = new VirtualEvent("setup");
			case END_SETUP :
				break;
			case BEGIN_INTERTRIAL :
				for (int i = eventIndex; i < systemEvents.size(); i++) {
					Object[] next = systemEvents.get(i);
					if(getType(next) == BEGIN_TRIAL) {
						storeValues(next);
						break;
					}
				}
			case END_INTERTRIAL :
				setIntertrial(nextEvent);
				break;
			case BEGIN_INTERBLOCK :
				for (int i = eventIndex; i < systemEvents.size(); i++) {
					Object[] next = systemEvents.get(i);
					if(getType(next) == BEGIN_BLOCK) {
						storeValues(next);
						break;
					}
				}
			case END_INTERBLOCK :
				setInterblock(nextEvent);
				break;
			case BEGIN_BLOCK :
				Block block;
				try {
					block = (Block) newExperimentPart(nextEvent);
					block.setPractice(((String)nextEvent[0]).compareTo("practice")==0);
					setCurrentBlock(block);
					String criterionTrial = ((Hashtable<String,String>)nextEvent[1]).get("criterionTrial");
					evt = (currentInterblock == null) ? new SystemEvent("block", null, criterionTrial, (Locator)nextEvent[2])
					: new SystemEvent("interblock", null, criterionTrial, (Locator)nextEvent[2]);
					storeValues(nextEvent);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case END_BLOCK :
				numBlock++;
				numTrial = 0;
				evt = new VirtualEvent("done");
				storeValues(nextEvent);
				break;
			case BEGIN_TRIAL :
				evt = (currentIntertrial == null) ? new VirtualEvent("trial") : new VirtualEvent("intertrial");
				storeValues(nextEvent);
				break;
			case END_TRIAL :
				numTrial++;
				break;
			default :
				if(nextEvent.length != 2) storeFactors(nextEvent, (String) nextEvent[0]);
			break;
			}

			eventIndex++;
		}
		if(first) {
			first = false;
			initLogs();
		}
		if(DEBUG) {
			System.out.println("<-- process event "+evt.getNameEvent()
					+" in conditions [Participant: "+Platform.getInstance().getMeasureValue(MEASURE_EXPERIMENT_PARTICIPANT)
					+" #block: "+Platform.getInstance().getMeasureValue(MEASURE_EXPERIMENT_BLOCK)
					+" #trial: "+Platform.getInstance().getMeasureValue(MEASURE_EXPERIMENT_TRIAL)+"] in state "+getCurrentState().getName());
		}
		processEvent(evt);
	}

	// used for XML tags that are not part of run, e.g. experiment, factors and measures declarations, etc.
	private void storeFactors(Object[] nextEvent, String nameEvent) {
		Hashtable<String,String> atts = (Hashtable<String,String>)nextEvent[1];
		if(nameEvent.compareTo("experiment") == 0) {
			String id = atts.get("id");
			String name = atts.get("name");
			Platform.getInstance().getCinematicLogger().setExperiment(id, name);
			headerLogComment += ("# "+id + ":" + name+"\n");
			//			measureNames.add("experiment");
			//			measureTypes.add("nominal");
		} else {
			if(nameEvent.compareTo("factor") == 0) {
				String id = atts.get("id");
				headerLogComment += ("# "+id + ":" + atts.get("name")+"\n");
				measureNames.add(id);
				measureTypes.add(atts.get("type"));
			} else {
				if(nameEvent.compareTo("measure") == 0) {
					String id = atts.get("id");
					String log = atts.get("cine_log");
					if(log!=null) {
						if(log.compareTo("ok") == 0) {
							if(!measureNames.contains(id)) {
								headerLogComment += ("# "+id + ":" + atts.get("name")+"\n");
								measureNames.add(id);
								measureTypes.add(atts.get("type"));
							}
							Platform.getInstance().addCinematicMeasure(id, 
									atts.get("type"), 
									atts.get("name"));
						}
					}
					log = atts.get("log");
					if(log!=null) {
						if(log.compareTo("ok") == 0) {
							if(!measureNames.contains(id)) {
								headerLogComment += ("# "+id + ":" + atts.get("name")+"\n");
								measureNames.add(id);
								measureTypes.add(atts.get("type"));
							}
						}
					}
					log = atts.get("cine");
					if(log!=null) {
						if(log.compareTo("ok") == 0) {
							Platform.getInstance().addCinematicMeasure(id, 
									atts.get("type"), 
									atts.get("name"));
						}
					}
				}
			}
		}
	}

	/**
	 * Processes again the last trials done.
	 * Careful trials will be logged at the current location
	 * in the log file and their number stay at their initial value.
	 * 
	 * Suppose, you have already achieved 4 trials in this block,
	 * log file looks like:
	 * experiment   participant   block   trial   ...<br>
	 * nominal      nominal   ratio   ratio   ...<br>
	 * PZvsOZ       S1        1       1       ...<br>
	 * PZvsOZ       S1        1       2       ...<br>
	 * PZvsOZ       S1        1       3       ...<br>
	 * PZvsOZ       S1        1       4       ...<br>
	 * <code>redoTrials(2)</code> will lead to a log file
	 * looking like:
	 * experiment   participant   block   trial   ...<br>
	 * nominal      nominal   ratio   ratio   ...<br>
	 * PZvsOZ       S1        1       1       ...<br>
	 * PZvsOZ       S1        1       2       ...<br>
	 * PZvsOZ       S1        1       3       ...<br>
	 * PZvsOZ       S1        1       4       ...<br>
	 * PZvsOZ       S1        1       3       ...<br>
	 * PZvsOZ       S1        1       4       ...<br>
	 * 
	 * @param nbTrialsToRedo The number of trials to undo.
	 */
	public void redoTrials(int nbTrialsToRedo) {
		int newEventIndex = eventIndex;
		int nbTrialsBack = 0;
		Object[] event;
		while(true) {
			event = systemEvents.get(newEventIndex);
			if(getType(event) == BEGIN_TRIAL) {
				nbTrialsBack++;
				if(nbTrialsBack == nbTrialsToRedo) break; 
			} else {
				if(getType(event) != END_TRIAL) { 
					// we undo only trials in the same block so we are supposed to only consider BEGIN_TRIAL and END_TRIAL events
					if(newEventIndex < 0) LOG.log(Level.SEVERE, "Cannot undo more than "+nbTrialsBack+" in this block.");
				}
			}
			newEventIndex--;
			if(newEventIndex < 0) LOG.log(Level.SEVERE, "Cannot undo more than "+nbTrialsBack+" in this block.");
		}
		eventIndex = newEventIndex;
		numTrial -= (nbTrialsToRedo - 1); // because we count the current trial
	}

	/**
	 * Processes again all the trials already done in this block.
	 */
	public void redoAllTrialsInBlock() {
		int newEventIndex = eventIndex;
		Object[] event;
		do {
			event = systemEvents.get(newEventIndex);
			newEventIndex--;
		} while(getType(event) == BEGIN_TRIAL || getType(event) == END_TRIAL);
		eventIndex = newEventIndex+2;
		numTrial = 0;
	}

	/**
	 * Jumps to a given trial.
	 * @param numBlock the destination block number
	 * @param numTrial the destination trial number
	 */
	public void goTo(int numBlock, int numTrial) {
		System.out.println("go to ["+numBlock+", "+numTrial+"]");
		
		systemEventsBlocked = true;

		int numBl = 0;
		int numTr = 0;
		boolean found = false;
		for (int i = 0; i < systemEvents.size(); i++) {
			Object[] nextEvent = systemEvents.get(i);
			if(getType(nextEvent) == BEGIN_BLOCK) {
				numBl++;
				if(numBl > numBlock) {
					LOG.log(Level.SEVERE, "goto destination (block="+numBlock+", trial="+numTrial+") does not exist");
					systemEventsBlocked = false;
					return;
				} else {
					if(numBl == numBlock && numTrial == 1) {
						eventIndex = i; 
						found = true;
						break;
					}

				}
				numTr=0;
			} else {
				if(getType(nextEvent) == BEGIN_TRIAL) {
					numTr++;
					if(numBl == numBlock && numTr == numTrial) {
						// destination found
						eventIndex = i; 
						found = true;
						break;
					}
				} 
			}
		}

		if(!found) {
			LOG.log(Level.SEVERE, "goto destination (block="+numBlock+", trial="+numTrial+") does not exist");
			systemEventsBlocked = false;
			return;
		}
		Object[] gotoEvent = new Object[2];
		if(numTrial == 1) gotoEvent[0] = "goto start";
		else gotoEvent[0] = "goto block";
		Hashtable<String,String> atts = new Hashtable<String, String>();
		atts.put("numBlock", ""+(numBlock-1));
		atts.put("numTrial", ""+(numTrial-1));
		gotoEvent[1] = atts;
		          
		systemEvents.add(eventIndex, gotoEvent);
		systemEventsBlocked = false;
	}

	private void setEnvironment(Object[] event) {
		Hashtable<String, String> atts = (Hashtable<String, String>)event[1];
		int newNumBlock = Integer.parseInt(atts.get("numBlock"));
		boolean blockChanged = newNumBlock != this.numBlock;
		this.numBlock = newNumBlock;
		this.numTrial = Integer.parseInt(atts.get("numTrial"));
		
		// set the right intertrial and interblock
		int j = eventIndex;
		for (; j >= 0; j--) {
			Object[] next = systemEvents.get(j);
			if(getType(next) == BEGIN_INTERTRIAL) {
				setIntertrial(next);
				storeValues(next);
				break;
			}
		}
		if(j == 0) currentIntertrial = DEFAULT_INTERTRIAL;
		j = eventIndex;
		for (; j >= 0; j--) {
			Object[] next = systemEvents.get(j);
			if(getType(next) == BEGIN_INTERBLOCK) {
				setInterblock(next);
				storeValues(next);
				break;
			}
		}
		if(j == 0) currentInterblock = DEFAULT_INTERBLOCK;


		// set the right block if necessary
		if(blockChanged && numTrial > 1)
			for (j = eventIndex; j >= 0; j--) {
				if(getType(systemEvents.get(j)) == BEGIN_BLOCK) {
					Block block = (Block) newExperimentPart(systemEvents.get(j));
					block.setPractice(((String)systemEvents.get(j)[0]).compareTo("practice")==0);
					setCurrentBlock(block);
					String criterionTrial = ((Hashtable<String,String>)systemEvents.get(j)[1]).get("criterionTrial");
					storeValues(systemEvents.get(j));
					setCriterion(currentBlock, criterionTrial, (Locator)systemEvents.get(j)[2]);
					currentBlock.doBeginBlock ();
					break;
				}
			}

	}

	boolean systemEventsBlocked = false;

	void log() {
		LinkedList<Object> measureValues = new LinkedList<Object>();
		for(Iterator<String> i = measureNames.iterator(); i.hasNext(); ) {
			String name = i.next();
			Object value = Platform.getInstance().getMeasureValue(name);
			if(value == null) value = Platform.getInstance().getFactorValue(name);
			measureValues.add(value);
		}
		Platform.getInstance().writeLog(measureNames, measureValues);
	}

	void initLogs() {
		measureNames.add(0, MEASURE_EXPERIMENT_NAME);
		measureNames.add(1, MEASURE_EXPERIMENT_PARTICIPANT);
		measureTypes.add(0, "nominal");
		measureTypes.add(1, "nominal");
		// trial logging
		measureNames.add(2, MEASURE_EXPERIMENT_BLOCK);
		measureNames.add(3, MEASURE_EXPERIMENT_TRIAL);
		measureTypes.add(2, "integer");
		measureTypes.add(3, "integer");
		headerLogComment+="# HIT: success\n";
		Platform.getInstance().writeLogHeader(headerLogComment, measureNames, measureTypes);

		// cinematic logging
		Platform.getInstance().initCinematicLog();
	}

	//	we have two kinds of states : 
	//	UserState await user input
	//	SystemState await system input, i.e. reading the script file
	class UserState extends State {
		Transition reset = new Event ("reset", ">> start") { };
	}
	class SystemState extends State {
		/**
		 * {@inheritDoc}
		 */
		public void enter () {
			processSystemEvent();
		}
		Transition reset = new Event ("reset", ">> start") { };
	}

	/**
	 * The initial state.
	 */
	public State start = new SystemState () {

		Transition setup = new Event ("setup", ">> setup") {
			public void action() {
				experimentSetUp.doBeginIntertitle();
			}
		};

		Transition practice = new Event ("practice", ">> block") {
			public void action() {
				SystemEvent sevt = ((SystemEvent)getEvent());
				currentBlock.doBeginBlock ();
				setCriterion(currentBlock, sevt.criterion, sevt.locator);
			}
		};

		Transition block = new Event ("block", ">> block") {
			public void action() {
				SystemEvent sevt = ((SystemEvent)getEvent());
				currentBlock.doBeginBlock ();
				setCriterion(currentBlock, sevt.criterion, sevt.locator);
			}
		};


		Transition interblock = new Event ("interblock", ">> interblock") {
			public void action() {
				currentInterblock.doBeginIntertitle();
				nextCriterion = ((SystemEvent)getEvent()).criterion;
				nextLocator = ((SystemEvent)getEvent()).locator;
			}
		};

		Transition gotoblock = new Event ("gotoblock", ">> block") { };

	};


	/**
	 * The interblock state.
	 */
	public State setup = new UserState () {

		Transition done = new Event ("done", ">> start") {
			public void action() {
				experimentSetUp.doEndIntertitle();
			}
		};
	};

	/**
	 * The interblock state.
	 */
	public State interblock = new UserState () {

		Transition done = new Event ("done", ">> block") {
			public void action() {
				currentInterblock.doEndIntertitle();
				currentBlock.doBeginBlock ();
				setCriterion(currentBlock, nextCriterion, nextLocator);
			}
		};

	};

	/**
	 * The intertrial state.
	 */
	public State intertrial = new UserState () {

		Transition done = new Event ("done", ">> trial") {
			public void action() {
				currentIntertrial.doEndIntertitle();
				currentBlock.doBeginTrial ();
			}
		};

	};

	/**
	 * The block state.
	 */
	public State block = new SystemState () {

		Transition intertrial = new Event ("intertrial", ">> intertrial") {
			public void action() {
				currentIntertrial.doBeginIntertitle();
			}
		};

		Transition trial = new Event ("trial", ">> trial") {
			public void action() {
				currentBlock.doBeginTrial ();
			}
		};

		Transition done = new Event ("done", ">> start") {
			public void action() {
				currentBlock.doEndBlock ();
				setCurrentBlock(null);
			}
		};

	};


	/**
	 * The trial state.
	 */
	public State trial = new UserState () {

		Transition done = new Event ("done", ">> block") {
			public void action() {
				currentBlock.doEndTrial ();
			}
		};

	};

	/**
	 * @return The current intertrial of this experiment.
	 */
	public Intertitle getCurrentIntertrial() {
		return currentIntertrial;
	}

	/**
	 * @return The current interblock of this experiment.
	 */
	public Intertitle getCurrentInterblock() {
		return currentInterblock;
	}

	/**
	 * @return The setup of this experiment.
	 */
	public Intertitle getSetUp() {
		return experimentSetUp;
	}

	/**
	 * @return The current block of this experiment.
	 */
	public Block getCurrentBlock() {
		return currentBlock;
	}

	/**
	 * @return The id of the current participant of this experiment.
	 */
	public String getCurrentParticipant() {
		return currentParticipant;
	}

	public boolean isOSCEnabled() {
		return generalData.isOSCEnabled();
	}

	public int getOSCPortPlatform() {
		return generalData.getOSCPortPlatform();
	}

	public Vector<String> getOSCHostsClients() {
		return generalData.getOSCHostsClients();
	}

	public Vector<Integer> getOSCPortsClients() {
		return generalData.getOSCPortsClients();
	}

}
