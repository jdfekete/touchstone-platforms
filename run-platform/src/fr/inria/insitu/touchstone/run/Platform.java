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

import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipFile;

import javax.swing.Timer;
import javax.swing.event.EventListenerList;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;

import fr.inria.insitu.touchstone.run.exp.model.CinematicLogger;
import fr.inria.insitu.touchstone.run.exp.model.Experiment;
import fr.inria.insitu.touchstone.run.input.Axes;
import fr.inria.insitu.touchstone.run.input.AxesEvent;
import fr.inria.insitu.touchstone.run.input.AxesListener;
import fr.inria.insitu.touchstone.run.input.AxisExpr;
import fr.inria.insitu.touchstone.run.input.InputManager;
import fr.inria.insitu.touchstone.run.utils.KeyMapJInputAWT;

/**
 * <b>Platform</b> is the root class of the project.
 */

public class Platform extends Frame 
implements ActionListener, AxesListener, OSCListener, Plugin {

	private static final long serialVersionUID = -6647687767110640249L;

	//OSC addresses constants
	/** OSC root address */
	public static final String OSC_ROOT_ADDRESS			= "/touchstone";
	/** OSC experiment address */
	public static final String OSC_EXPERIMENT_ADDRESS			= OSC_ROOT_ADDRESS + "/experiment";
	/** OSC start experiment address*/
	public static final String OSC_START_EXPERIMENT_ADDRESS	= OSC_EXPERIMENT_ADDRESS + "/start";
	/** OSC end experiment address*/
	public static final String OSC_END_EXPERIMENT_ADDRESS	= OSC_EXPERIMENT_ADDRESS + "/end";
	/** OSC end condition address*/
	public static final String OSC_END_CONDITION_ADDRESS	= OSC_ROOT_ADDRESS + "/endcondition";
	/** OSC intertitle address*/
	public static final String OSC_INTERTITLE_ADDRESS	= OSC_ROOT_ADDRESS + "/intertitle";
	/** OSC start intertitle address*/
	public static final String OSC_START_INTERTITLE_ADDRESS	= OSC_INTERTITLE_ADDRESS + "/start";
	/** OSC end intertitle address*/
	public static final String OSC_END_INTERTITLE_ADDRESS	= OSC_INTERTITLE_ADDRESS + "/end";
	/** OSC block address*/
	public static final String OSC_BLOCK_ADDRESS	= OSC_ROOT_ADDRESS + "/block";
	/** OSC start block address*/
	public static final String OSC_START_BLOCK_ADDRESS	= OSC_BLOCK_ADDRESS + "/start";
	/** OSC end block address*/
	public static final String OSC_END_BLOCK_ADDRESS	= OSC_BLOCK_ADDRESS + "/end";
	/** OSC trial address*/
	public static final String OSC_TRIAL_ADDRESS	= OSC_ROOT_ADDRESS + "/trial";
	/** OSC start trial address*/
	public static final String OSC_START_TRIAL_ADDRESS	= OSC_TRIAL_ADDRESS + "/start";
	/** OSC end trial address*/
	public static final String OSC_END_TRIAL_ADDRESS	= OSC_TRIAL_ADDRESS + "/end";
	
	/** Default OSC input port */
    public static final int DEFAULT_OSC_PORT				   = 8888;
	
	/** Logger. */
	private static final Logger   LOG                    = Logger.getLogger(Platform.class.getName());
	
	/** Property pageWidth. */
    public static final String  PROPERTY_PAGE_WIDTH    = "pageWidth";
    /** Property pageHeight. */
    public static final String  PROPERTY_PAGE_HEIGHT   = "pageHeight";
    /** Property documentName. */
    public static final String  PROPERTY_DOCUMENT_NAME = "documentName";
    /** Property backgroundLayer. */
    public static final String  PROPERTY_DOCUMENT      = "backgroundLayer";
    /** Property viewName. */
    public static final String  PROPERTY_VIEW_NAME     = "viewName";
    /** Property view. */
    public static final String  PROPERTY_VIEW          = "view";
    /** Property target. */
    public static final String  PROPERTY_TARGET        = "target";
    /** Property tracker. */
    public static final String  PROPERTY_TRACKER       = "tracker";
    /** Property endCondition. */
    public static final String  PROPERTY_END_CONDITION = "endCondition";
    /** Property pollDelay. */
    public static final String  PROPERTY_POLL_DELAY    = "pollDelay";
    /** Property viewName. */
    public static final String  PROPERTY_MODEL_NAME    = "modelName";

    /** Action triggered by an end condition. */
    public static final String  ACTION_END_CONDITION   = "endCondition";

    /** Name of the standard input keyboard ESC key. */
    public static final String  INPUT_ESC              = "Keyboard.Escape";
    
    // Measures and Factors

    /** The global instance. */
    private static Platform     instance;

    private EndCondition        endCondition;

    private boolean 			exitOnEscape = true;
    
    private EventListenerList   eventListenerList;

    private int                 pollDelay;

    private InputManager        inputManager;

    private ArrayList<List<String>>           logs;
    private ArrayList<String>           logHeaders;
    private PrintWriter         logOut;
    private String              logSeparator           = "\t";

    private ArrayList<Measure>  measuresList;

    private long                startTime;

    private CinematicLogger     cinematicLogger        = new CinematicLogger();
    private Experiment          experiment             = null;

    private ArrayList<Plugin>   plugins                = new ArrayList<Plugin>();
	
	public static boolean JINPUT_ON = false;
	
	public static boolean OSC_ON = false;
	private int currentOSCPort = DEFAULT_OSC_PORT;
	private OSCPortIn oscInputPort;
	private Hashtable<OSCClient, OSCPortOut> oscClients;

    public static String[] measuresMouse = {"Mouse.x", "Mouse.y", "Mouse.Left", "Mouse.Middle", "Mouse.Right", "Mouse.Wheel"};
	/**
	 * <b>EndCondition</b> is used by Platform to test for the end condition.
	 * 
	 * <p>
	 * An <code>EndCondition</code> is registered to the <code>Platform</code>
	 * root object using the method <code>setEndCondition</code>. Once registered,
	 * an endCondition is evaluated using its methods:
	 * <ol>
	 * <li> <code>isReached(AxesEvent)</code> each time an input axis has changed
	 * <li> <code>isReached(InputEvent)</code> each time an standard AWT input event has occured
	 * <li> <code>isReached(Timer,long)</code> each time a timer expires.
	 * </ol>
	 * As soon as an endCondition is reached, the <code>Platform</code> object fires an
	 * <code>ActionEvent</code> to all its registered <code>ActionListener</code>. The event
	 * action message is <code>Platform.ACTION_END_CONDITION</code>.
	 * </p>
	 * 
	 * @see Platform#setEndCondition(fr.inria.insitu.touchstone.run.Platform.EndCondition)
	 * @see Platform#ACTION_END_CONDITION
	 */
	public interface EndCondition {
		
		/**
		 * Returns true when the end condition is reached.
		 * 
		 * @param timer the timer
         * @param when the time when the timer fired
         *           
		 * @return true when the end condition is reached
		 */
		boolean isReached(Timer timer, long when);
		
		/**
		 * Returns true when the end condition is reached.
		 * 
		 * @param message the message
         * @param when the time when the message was received
         *           
		 * @return true when the end condition is reached
		 */
		boolean isReached(OSCMessage message, long when);
		
		/**
		 * @return the reason why the condition ended
		 */
		String getEndCondition();
		
		/**
		 * Starts this end condition.
		 * The <code>ExperimentLauncher</code> calls it each time a trial or intertitle begins.
		 * Typically, this method is used to register the required listeners on platform.
		 */
		void start();
		
		/**
		 * Stops this end condition.
		 * The <code>ExperimentLauncher</code> calls it each time a trial or intertitle ends.
		 * Typically, this method is used to unregister the required listeners on platform.
		 */
		void stop();
		
		/**
		 * Returns true when the end condition is reached.
		 * 
		 * @param e the event
		 * @return true when the end condition is reached
		 */
		boolean isReached(AxesEvent e);
		
		/**
		 * Returns true when the end condition is reached.
		 * 
		 * @param e the event
		 * @return true when the end condition is reached
		 */
		boolean isReached(InputEvent e);
	}
	
	
	/**
	 * Constructor.
	 */
	protected Platform() {
		inputManager = InputManager.getInstance();
		setUndecorated(true);
		measuresList = new ArrayList<Measure>();
		setLayout(null);
		setBackground(Color.LIGHT_GRAY);
		startTime = System.currentTimeMillis();
		eventListenerList = new EventListenerList();
		addAxesListener(new Axes(INPUT_ESC), this);
		addAxesListener(new Axes("Window.width"), this);
		addAxesListener(new Axes("Window.height"), this);
	}
	
	/**
	 * Definition of an OSC Client, for caching purposes.
	 * @author Stuf
	 *
	 */
	class OSCClient {
		String host;
		int port;
		
		public OSCClient(String host, int port) {
			this.host = host;
			this.port = port;
		}
		
		@Override
		public int hashCode() {
			return host.hashCode() + port;
		}
		
		@Override
		public boolean equals(Object o) {
			if (o == null)
				return false;
			if (this == o)
				return true;
			if (o instanceof OSCClient) {
				OSCClient o2 = (OSCClient)o;
				return (host.equals(o2.host)) && (port == o2.port);
			}
			return false;
		}
	}
	
	private String lastOSCHost = null;
	private int lastOSCPort = 0;
	private OSCMessage lastOSCMessage = null;
	
	/**
	 * Re-send the last OSC message.
	 */
	public void reSendLastOSCMessage() {
		if (lastOSCMessage == null) {
			System.err.println("Can not send last OSC message (last message is null)...");
			return;			
		}
		if (lastOSCHost != null) {//last sent message was for a specific host...
			sendOSCMessage(lastOSCHost, lastOSCPort, lastOSCMessage);
		} else {//last message was sent to all known clients
			sendOSCMessage(lastOSCMessage);
		}
	}
	
	/**
	 * Send an OSC message to all the clients in the list.
	 * 
	 * @param message the message
	 */
	public void sendOSCMessage(OSCMessage message) {
		if (!OSC_ON) {
			System.err.println("Can not send OSC message. OSC is disabled...");
			return;
		}
		lastOSCHost = null;
		lastOSCPort = 0;
		lastOSCMessage = message;
		for (OSCPortOut outPort : oscClients.values()) {
			try {
				//System.out.println("Sending message " + message.getAddress() + " to " + host + "(" + port +")");
					outPort.send(message);
				} catch (IOException e) {
					e.printStackTrace();
				}			
		}
	}
	
	/**
	 * Send an SOC message to the specified client. The client is added to the clients list if not yet in the list.
	 * 
	 * @param host the client host
	 * @param port the client port
	 * @param message the message
	 */
	public void sendOSCMessage(String host, int port, OSCMessage message) {
		if (!OSC_ON) {
			System.err.println("Can not send OSC message. OSC is disabled...");
			return;
		}
		if (host == null || host.equals(""))
			return;
		OSCClient client = new OSCClient(host, port);
		OSCPortOut outPort = oscClients.get(client);
		if (outPort == null) {
			outPort = addOSCClient(host, port);
		}
		if (outPort != null) {
			lastOSCHost = host;
			lastOSCPort = port;
			lastOSCMessage = message;
			try {
//System.out.println("Sending message " + message.getAddress() + " to " + host + "(" + port +")");
				outPort.send(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Add an OSC client and create an OSCPortOut for it. Can be used to retrieve the output port of 
	 * the client if it was already created.
	 * 
	 * @param host client host
	 * @param port client port
	 * @return the OSCPortOut output port of this client.
	 */
	public OSCPortOut addOSCClient(String host, int port) {
		OSCClient client = new OSCClient(host, port);
		OSCPortOut outPort = oscClients.get(client);
		if (outPort == null) {
			try {
				System.out.println("Create client for " + host + "(" + port +")");
				outPort = new OSCPortOut(InetAddress.getByName(host), port);
				oscClients.put(client, outPort);
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}	
		return outPort;
	}
	
	/**
	 * Enable OSC listening on the given port. If already enabled, the previous connection is closed (and all existing client are removed).
	 * 
	 * @param port input port.
	 */
	public void enableOSC(int port) {
		if (OSC_ON) {
			disableOSC();
		}
		try {
			oscInputPort = new OSCPortIn(port);
			oscInputPort.addListener(OSC_ROOT_ADDRESS + "/.*", this);
			oscInputPort.startListening();
			oscClients = new Hashtable<OSCClient, OSCPortOut>();
			currentOSCPort = port;
			OSC_ON = true;
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Enable OSC listening on the default port. If already enabled, the previous connection is closed (and all existing client are removed).
	 * 
	 */
	public void enableOSC() {
		enableOSC(DEFAULT_OSC_PORT);
	}
	
	/**
	 * Get the current OSC port number (set by enableOSC(int port) if successful).
	 * @return
	 */
	public int getCurrentOSCPort() {
		return currentOSCPort;
	}
	
	/**
	 * Disable OSC listening and remove all clients.
	 */
	public void disableOSC() {
		if (oscInputPort != null) {
			oscInputPort.removeListener(this);
			oscInputPort.stopListening();
			oscInputPort.close();
			oscInputPort = null;
		}
		if (oscClients != null) {
			for (OSCPortOut client : oscClients.values()) {
				client.close();
			}
			oscClients.clear();
			oscClients = null;
		}
		OSC_ON = false;
	}
	
	/**
	 * Is OSC enabled?
	 * @return true if enabled, false otherwise.
	 */
	public boolean isOSCEnabled() {
		return OSC_ON;
	}
	
	public void enableJInput() {
		JINPUT_ON = true;
		inputManager.setToDefaultConfiguration();
		File confFile = new File("input.conf");
		if(confFile.exists()) installGeneralizedInput(confFile);
	}
	
	public void disableJInput() {
		JINPUT_ON = false;
		inputManager.setToDefaultConfiguration();
	}
	
	public boolean isjInputEnabled() {
		return JINPUT_ON;
	}
	
	/**
	 * Enables generalized input.
	 * @param inputConfiguration The configuration file
 	 */
	public void installGeneralizedInput(File inputConfiguration) {
		installGeneralizedInput(inputConfiguration, LaunchExperiment.DEFAULT_AWT, LaunchExperiment.DEFAULT_AWT);
	}
	
	void installGeneralizedInput(File configurationFile, String defaultMouse, String defaultKb) {
		getInputManager().setToDefaultConfiguration();
		setPollDelay(10); // 10 milliseconds
		try {
			getInputManager().setDefaultMouse(defaultMouse);
//			getInputManager().setDefaultKeyboard(defaultKb);
			loadInputConfiguration(configurationFile);
		} catch(Exception e) {
//			e.printStackTrace();
			;
		}
        inputManager.start();
	}
	
	/**
	 * @return the instance of the Platform.
	 */
	public static Platform getInstance() {
		if (instance == null) {
			instance = new Platform();
		}
		return instance;
	}
	
	/**
	 * @return Returns the endCondition.
	 */
	public EndCondition getEndCondition() {
		return endCondition;
	}
	
	/**
	 * @param endCondition
	 *            The endCondition to set.
	 */
	public void setEndCondition(EndCondition endCondition) {
		if (this.endCondition != endCondition) {
			EndCondition old = this.endCondition;
			this.endCondition = endCondition;
			firePropertyChange(PROPERTY_END_CONDITION, old, endCondition);
		}
	}
	
	public void setExitOnEscape(boolean exitOnEscape) {
		this.exitOnEscape = exitOnEscape;
	}
	
	public boolean isExitOnEscape() {
		return this.exitOnEscape;
	}
	
	/**
	 * Evaluates the end condition, returning true if it considers the technique
	 * should terminate.
	 * 
	 * @param e
	 *            the axis event
	 * @return true if the current end condition is reached
	 */
	public boolean evalEndCondition(EventObject e) {
		Object value_escape = getMeasureValue(INPUT_ESC);
		if(this.exitOnEscape && value_escape != null) {
			if (Double.parseDouble(value_escape.toString()) == 1)
				System.exit(0);
		}
		if (endCondition == null) {
			return false;
		}
		boolean finished = false;
		if(e instanceof AxesEvent) {
			finished = finished || endCondition.isReached((AxesEvent)e);
		}
		else if(e instanceof InputEvent) finished = finished || endCondition.isReached((InputEvent)e);
		if (finished) {
			fireActionListener(endCondition, ACTION_END_CONDITION);
			return true;
		}
		return false;
	}
	
	/**
	 * Evaluates the end condition to a given specified value.
	 * 
	 * @param eval
	 *            true if the end condition must be considered as reached, false otherwise
	 * @return true if the current end condition is reached
	 */
	public boolean evalEndCondition(boolean eval) {
		if (eval)
			fireActionListener(endCondition, ACTION_END_CONDITION);
		return eval;
	}
	
	
	private class AWTListener implements MouseListener, MouseMotionListener, KeyListener, MouseWheelListener {

		Component comp;
		boolean mouseEnabled = true;
		boolean keyboardEnabled = true;
		
		AWTListener() { }
		
		/**
		 * Registers a component for cinematic logging.
		 * @param c the component to log.
		 */
		public void registerComponent(Component c) {
			if(comp == c) return;
			if(comp != null) unregisterComponent(comp);
			comp = c;
			c.addMouseListener(this);
			c.addMouseMotionListener(this);
			c.addMouseWheelListener(this);
			c.addKeyListener(this);
			c.requestFocus();
		}
		
		/**
		 * Unregisters a component from event logging.
		 * @param c the component to unregister.
		 */
		public void unregisterComponent(Component c) {
			c.removeMouseListener(this);
			c.removeMouseMotionListener(this);
			c.removeMouseWheelListener(this);
			c.removeKeyListener(this);
			comp = null;
		}
		
		void disableKeyboard() {
			keyboardEnabled = false;
		}
		
		void enableKeyboard() {
			keyboardEnabled = true;
		}
		
		void disableMouse() {
			mouseEnabled = false;
		}
		
		void enableMouse() {
			mouseEnabled = true;
		}
		
		private void setStandardMeasures(MouseEvent e) {
			if(!mouseEnabled) return; 
//			System.out.println("("+e.getX()+", "+e.getY()+")");
			Platform.getInstance().setAxisValue("Mouse.x", new Integer(e.getX()));
			Platform.getInstance().setAxisValue("Mouse.y", new Integer(e.getY()));
			String axis;
			switch(e.getButton()) {
			case MouseEvent.BUTTON1 : 
				axis = "Mouse.Left"; break;
			case MouseEvent.BUTTON2 :
				axis = "Mouse.Middle"; break;
			case MouseEvent.BUTTON3 :
				axis = "Mouse.Right"; break;
			default : return;
			}
			if(e.getID() == MouseEvent.MOUSE_PRESSED)
				Platform.getInstance().setAxisValue(axis, new Integer(1));
			else if(e.getID() == MouseEvent.MOUSE_RELEASED)
				Platform.getInstance().setAxisValue(axis, new Integer(0));
			else if(e.getID() == MouseEvent.MOUSE_WHEEL)
				if(Platform.getInstance().isAxisDefined("Mouse.Wheel")) Platform.getInstance().setAxisValue("Mouse.Wheel", ((MouseWheelEvent)e).getWheelRotation());
		}
		
		private void setStandardMeasures(KeyEvent e) {
			if(!keyboardEnabled) return;
			String axis = "Keyboard."+KeyMapJInputAWT.keyDescriptor(e.getKeyCode());
//			System.out.println("key axis: "+axis+" - "+e.getKeyCode());
			if(e.getID() == KeyEvent.KEY_PRESSED)
				Platform.getInstance().setAxisValue(axis, 1);
			else if(e.getID() == KeyEvent.KEY_RELEASED)
				Platform.getInstance().setAxisValue(axis, 0);
		}
		
		/**
		 * {@inheritDoc}
		 */
		public void mouseClicked(MouseEvent e) {
			setStandardMeasures(e);
			setMeasureValue("currentTime", e.getWhen(), false);
			Platform.getInstance().evalEndCondition(e);
		}

		/**
		 * {@inheritDoc}
		 */
		public void mouseEntered(MouseEvent e) {
			setStandardMeasures(e);
			setMeasureValue("currentTime", e.getWhen(), false);
			Platform.getInstance().evalEndCondition(e);
		}

		/**
		 * {@inheritDoc}
		 */
		public void mouseExited(MouseEvent e) {
			setStandardMeasures(e);
			setMeasureValue("currentTime", e.getWhen(), false);
			Platform.getInstance().evalEndCondition(e);
		}

		/**
		 * {@inheritDoc}
		 */
		public void mousePressed(MouseEvent e) {
			setStandardMeasures(e);
			setMeasureValue("currentTime", e.getWhen(), false);
			Platform.getInstance().evalEndCondition(e);
		}

		/**
		 * {@inheritDoc}
		 */
		public void mouseReleased(MouseEvent e) {
			setStandardMeasures(e);
			setMeasureValue("currentTime", e.getWhen(), false);
			Platform.getInstance().evalEndCondition(e);
		}

		/**
		 * {@inheritDoc}
		 */
		public void mouseDragged(MouseEvent e) {
			setStandardMeasures(e);
			setMeasureValue("currentTime", e.getWhen(), false);
			Platform.getInstance().evalEndCondition(e);
		}

		/**
		 * {@inheritDoc}
		 */
		public void mouseMoved(MouseEvent e) {
			setStandardMeasures(e);
			setMeasureValue("currentTime", e.getWhen(), false);
			Platform.getInstance().evalEndCondition(e);
		}

		/**
		 * {@inheritDoc}
		 */
		public void keyPressed(KeyEvent e) {
//			System.out.println("!!!keyPressed!!!");
			setStandardMeasures(e);
			setMeasureValue("currentTime", e.getWhen(), false);
			Platform.getInstance().evalEndCondition(e);
		}

		/**
		 * {@inheritDoc}
		 */
		public void keyReleased(KeyEvent e) {
			setStandardMeasures(e);
			setMeasureValue("currentTime", e.getWhen(), false);
			Platform.getInstance().evalEndCondition(e);
		}

		/**
		 * {@inheritDoc}
		 */
		public void keyTyped(KeyEvent e) {
			setMeasureValue("currentTime", e.getWhen(), false);
			Platform.getInstance().evalEndCondition(e);
		}

		/**
		 * {@inheritDoc}
		 */
		public void mouseWheelMoved(MouseWheelEvent e) {
			setStandardMeasures(e);
			setMeasureValue("currentTime", e.getWhen(), false);
			Platform.getInstance().evalEndCondition(e);
		}

	}
	private AWTListener awtListener = new AWTListener();
	
	public void disableAWTMouse() {
		awtListener.disableMouse();
	}
	public void enableAWTMouse() {
		awtListener.enableMouse();
	}
	public void disableAWTKeyboard() {
		awtListener.disableKeyboard();
	}
	public void enableAWTKeyboard() {
		awtListener.enableKeyboard();
	}
	
	/**
	 * Makes the platform listens for this component to evaluate the current end condition.
	 * @param c the component to listen
	 */
	public void registerComponent(Component c) {
		awtListener.registerComponent(c);
	}
	
	/**
	 * Makes the platform stops to listen for this component to evaluate the current end condition.
	 * @param c the component to unregister
	 */
	public void unregisterComponent(Component c) {
		awtListener.unregisterComponent(c);
	}
	
	public Component getRegisteredComponent() {
		return awtListener.comp;
	}
	
	/**
	 * Evaluates the end condition, returning true if it considers the technique
	 * should terminate.
	 * 
	 * @param timer the Timer
     * @param when the time when the timer was fired
	 * @return true if the technique should terminate
	 */
	public boolean evalEndCondition(Timer timer, long when) {
		if (endCondition == null) {
			return false;
		}
		if (endCondition.isReached(timer, when)) {
			fireActionListener(endCondition, ACTION_END_CONDITION);
			return true;
		}
		return false;
	}
	
	/**
	 * Evaluates the end condition, returning true if it considers the technique
	 * should terminate.
	 * 
	 * @param message the OSC message
     * @param when the message was received
	 * @return true if the technique should terminate
	 */
	public boolean evalEndCondition(OSCMessage message, long when) {
		if (endCondition == null) {
			return false;
		}
		if (endCondition.isReached(message, when)) {
			fireActionListener(endCondition, ACTION_END_CONDITION);
			return true;
		}
		return false;
	}
	
	/**
	 * Adds the specified action listener.
	 * 
	 * @param l
	 *            the action listener
	 */
	public void addActionListener(ActionListener l) {
		eventListenerList.add(ActionListener.class, l);
	}
	
	/**
	 * Removes the specified action listener.
	 * 
	 * @param l
	 *            the action listener.
	 */
	public void removeActionListener(ActionListener l) {
		eventListenerList.remove(ActionListener.class, l);
	}
	
	/**
	 * Fires all the installed action listeners.
	 * 
	 * @param source
	 * 			 the source
	 * @param action
	 *            the action name
	 * @param when
	 *            the time
	 * 
	 */
	public void fireActionListener(Object source, String action, long when) {
		ActionListener[] listeners = eventListenerList
		.getListeners(ActionListener.class);
		if (listeners == null || listeners.length == 0)
			return;
		ActionEvent e = new ActionEvent(
				source,
				ActionEvent.ACTION_PERFORMED,
				action,
				when,
				0);
		for (int i = 0; i < listeners.length; i++) {
			listeners[i].actionPerformed(e);
		}
		
	}
	
	/**
	 * Fires all the installed action listeners.
	 * 
	 * @param source
	 * 			 the source
	 * @param action
	 *            the action name
	 */
	public void fireActionListener(Object source, String action) {
		fireActionListener(source, action, System.currentTimeMillis());
	}
	
	/************************************/
	/********** LOG management **********/
	/************************************/
	
	/**
	 * Starts a log file with a specified lists of measure names and their
	 * associated types. They will be copied into the log file as the two first
	 * lines.
	 * 
	 * @param filename
	 *            the filename of the log file
	 * @param headerComment
	 * 			 the header comment of the log file
	 * @param measureNames
	 *            the list of measure names
	 * @param measureTypes
	 *            the list of measure types
	 */
	@SuppressWarnings("unchecked")
	public void writeLogHeader(String filename, String headerComment, List measureNames, List measureTypes) {
		int size = measureNames.size();
		if (size != measureTypes.size()) {
			LOG.warning("Size for measure names and types differ");
			size = Math.min(size, measureTypes.size());
		}
		logs = new ArrayList<List<String>>();
		logHeaders = new ArrayList(measureNames);
		FileWriter fout;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		String date = format.format(new Date());
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
		for (int i = 0; i < logHeaders.size(); i++) {
			if (i != 0) {
				logOut.write(logSeparator);
			}
			logOut.write(logHeaders.get(i).toString());
		}
		logOut.println();
		for (int i = 0; i < measureTypes.size(); i++) {
			if (i != 0) {
				logOut.write(logSeparator);
			}
			logOut.write(measureTypes.get(i).toString());
		}
		logOut.println();
		logOut.flush();
	}
	
	/**
	 * Starts a log file with a specified lists of measure names and their
	 * associated types. They will be copied into the log file as the two first
	 * lines. A default file name is used.
	 * 
	 * @param headerComment
	 * 			 the header comment of the log file
	 * @param measureNames
	 *            the list of measure names
	 * @param measureTypes
	 *            the list of measure types
	 */
	public void writeLogHeader(String headerComment, List<String> measureNames, List<String> measureTypes) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        File rootDir = new File(getMeasureValue(Experiment.MEASURE_EXPERIMENT_NAME)+File.separator+"logs"+File.separator+"trial");
		if(!rootDir.exists()) rootDir.mkdirs();
		
		String filename = rootDir+File.separator+"log-" + Platform.getInstance().getMeasureValue(Experiment.MEASURE_EXPERIMENT_PARTICIPANT) + "-" + format.format(new Date()) + ".log";
		writeLogHeader(filename, headerComment, measureNames, measureTypes);
	}
	
	/**
	 * Record a list of measures into the log file and into the log queue.
	 * 
	 * @param measureNames
	 *            the measure names
	 * @param measures
	 *            the measures as a list of values
	 * @return the index of the log queue
	 */
	@SuppressWarnings("unchecked")
	public int writeLog(List measureNames, List measures) {
		if (logOut == null) {
			LOG.warning("No LOG open");
			return -1;
		}
		String[] measuresOrdered = new String[logHeaders.size()];
		for(int i = 0; i < logHeaders.size(); i++) {
			int index = measureNames.indexOf(logHeaders.get(i));
			if(index != -1) {
//                LOG.info("(String) measures.get(index): "+measures.get(index)+" - "+logHeaders.get(i));
//				System.out.println("(String) measures.get(index): "+measures.get(index)+" - "+logHeaders.get(i));
				measuresOrdered[i] = ""+measures.get(index);
			} else {
				measuresOrdered[i] = "NaN";
				LOG.warning("The value for the measure "+logHeaders.get(i)
						+ " is not logged");
			}
			
		}
		logs.add(Arrays.asList(measuresOrdered));
		for (int i = 0; i < measuresOrdered.length; i++) {
			logOut.print(measuresOrdered[i]);
			if(i != (measuresOrdered.length - 1)) logOut.print(logSeparator);
		}
		logOut.println();
		logOut.flush();
		return logs.size();
	}
	
	/**
	 * Returns the specified measure at the specified index.
	 * 
	 * @param id
	 *            the trial index
	 * @param measureName
	 *            the measure name
	 * @return the measure
	 */
	public Object getLog(int id, String measureName) {
		int i = logHeaders.indexOf(measureName);
		return ((List<?>) logs.get(id)).get(i);
	}
	
	/**
	 * Finish logging, closing the logging file.
	 */
	public void endLog() {
		if (logOut != null) {
			logOut.close();
			logOut = null;
		}
	}
	
	/************************************/
	/******** Events management *********/
	/************************************/
	
	/**
	 * Registers a timer <code>timer</code> in the platform. If
	 * <code>ec</code> is the end condition of the platform, the method
	 * <code>ec.isFinished(timer)</code> is called when the timer expires.
	 * 
	 * @param timer
	 *            The timer
	 * @return this platform.
	 */
	public Platform addTimer(Timer timer) {
		timer.addActionListener(this);
		return this;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void axesChanged(AxesEvent e) { }
	
	/**
	 * {@inheritDoc}
	 */
	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() instanceof Timer) {
			evalEndCondition((Timer) evt.getSource(), evt.getWhen());
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void acceptMessage(Date when, OSCMessage message) {
//		System.out.println("Received OSC message: " + message.getAddress());		
		if (message.getAddress().contains(OSC_END_CONDITION_ADDRESS)) {
			long time = System.currentTimeMillis();
			if (when != null)
				time = when.getTime();
			evalEndCondition(message, time);
		}
	}
	
	/************************************/
	/********* Cinematic Logs ********/
	/************************************/
	
	/**
	 * @return the cinematic logger.
	 */
	public CinematicLogger getCinematicLogger() {
		if(cinematicLogger == null)
			cinematicLogger = new CinematicLogger();
		return cinematicLogger;
	}
	
	/**
	 * Register a cinematic measure.
	 * @param measureID The measure id
	 * @param type The measure type
	 * @param measureName The measure name
	 */
	public void addCinematicMeasure(final String measureID, String type, String measureName) {
		if(inputManager.isAxisDefined(measureID)) {
			if(getMeasureValue(measureID) == null) {
				addMeasure(new Measure(measureID) {
					public Object getValue() {
						return new Double(
						        inputManager.getAxisValue(getID()));
					}
				});
			}
		}
		cinematicLogger.addMeasure(measureID, type, measureName);
	}
	
	/**
	 * Start cinematic logs.
	 */
	public void initCinematicLog() {
		if(cinematicLogger == null) {
			cinematicLogger = new CinematicLogger();
		}
		cinematicLogger.init();
	}
	
	/**
	 * Stop cinematic logs.
	 */
	public void stopCinematicLog() {
		cinematicLogger.stop();
	}
	
	/**
	 * Resume cinematic logs.
	 */
	public void resumeCinematicLog() {
		cinematicLogger.resume();
	}
	
	/**
	 * Suspend cinematic logs.
	 */
	public void suspendCinematicLog() {
		cinematicLogger.suspend();
	}
	
	
	/************************************/
	/******** Input management **********/
	/************************************/
	
	/**
	 * @return the pollDelay
	 */
	public int getPollDelay() {
		return pollDelay;
	}
	
	/**
	 * @param pollDelay
	 *            the pollDelay to set
	 */
	public void setPollDelay(int pollDelay) {
		if (pollDelay == this.pollDelay)
			return;
		int old = this.pollDelay;
		this.pollDelay = pollDelay;
		inputManager.setPollDelay(pollDelay);
		firePropertyChange(PROPERTY_POLL_DELAY, old, pollDelay);
	}

	/**
	 * @return the inputManager
	 */
	public InputManager getInputManager() {
		return inputManager;
	}
	
	/**
	 * Adds an axis listener for all the defined axes.
	 * @param listener the listener
	 * @see fr.inria.insitu.touchstone.run.input.InputManager#addAxesListener(fr.inria.insitu.touchstone.run.input.AxesListener)
	 */
	public void addAxesListener(AxesListener listener) {
		inputManager.addAxesListener(listener);
	}
	
	/**
	 * Adds an axis listener for a specified axis.
	 * @param axes the axis name set
	 * @param listener the listener
	 * @see fr.inria.insitu.touchstone.run.input.InputManager#addAxesListener(Axes, AxesListener)
	 */
	public void addAxesListener(Axes axes, AxesListener listener) {
		if(inputManager != null) inputManager.addAxesListener(axes, listener);
	}
	
	/**
	 * The developer can define a constant value for a specified axis through this method.<br>
	 * This method SHOULD NOT be used: to manually set the value of an axis, please use the method <code>setAxisValue</code> in class <code>Platform</code>. 
	 * @param axis the axis name
	 * @param value the value
	 * @see fr.inria.insitu.touchstone.run.Platform#setAxisValue(String, double)
	 */
	public void setAxisValue(String axis, double value) {
		if(inputManager == null) return; 
		if(!inputManager.isAxisDefined(axis))
			try {
				defineVirtualAxis(axis, ""+value);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		inputManager.setAxisValue(axis, value, true);
	}
	
	/**
	 * Defines a virtual axis with the specified axis name
	 * and the specified expression.
	 * @param name the name
	 * @param expr the expression
	 * @see fr.inria.insitu.touchstone.run.input.InputManager#defineVirtualAxis(java.lang.String, fr.inria.insitu.touchstone.run.input.AxisExpr)
	 */
	public void defineVirtualAxis(String name, AxisExpr expr) {
		inputManager.defineVirtualAxis(name, expr);
	}
	
	/**
	 * Defines a virtual axis with the specified axis name
	 * and the specified expression in a simple syntax.
	 * @param name the axis name
	 * @param expr the expression string
	 * @throws ParseException when the expression string cannot be parsed
	 * @see fr.inria.insitu.touchstone.run.input.InputManager#defineVirtualAxis(java.lang.String, java.lang.String)
	 */
	public void defineVirtualAxis(String name, String expr) throws ParseException {
		inputManager.defineVirtualAxis(name, expr);
	}
	
	/**
	 * Returns the last time when the virtual axis has been set.
	 * @param name the axis name
	 * @return the last time when the axis has been set
	 * @see fr.inria.insitu.touchstone.run.input.InputManager#getAxisTime(String)
	 */
	public long getAxisTime(String name) {
		return inputManager.getAxisTime(name);
	}
	
	/**
	 * Returns the last value of the virtual axis.
	 * @param name the axis name
	 * @return the last value of the virtual axis
	 * @see fr.inria.insitu.touchstone.run.input.InputManager#getAxisValue(String)
	 */
	public double getAxisValue(String name) {
		return inputManager.getAxisValue(name);
	}
	
	/**
	 * Returns true if the axis exists.
	 * @param axis the axis name
	 * @return true if the axis exists
	 * @see fr.inria.insitu.touchstone.run.input.InputManager#isAxisDefined(java.lang.String)
	 */
	public boolean isAxisDefined(String axis) {
		return inputManager.isAxisDefined(axis);
	}
	
	/**
	 * Parses a string expression and returns an AxisExpr.
	 * @param expr the expression in string format
	 * @return the AxisExpr constructed from the string
	 * @throws ParseException when the sting cannot be parsed
	 * @see fr.inria.insitu.touchstone.run.input.InputManager#parse(java.lang.String)
	 */
	public AxisExpr parse(String expr) throws ParseException {
		return inputManager.parse(expr);
	}
	
	/**
	 * Removes a registered axis listener.
	 * @param listener the listener
	 * @see fr.inria.insitu.touchstone.run.input.InputManager#removeAxesListener(fr.inria.insitu.touchstone.run.input.AxesListener)
	 */
	public void removeAxesListener(AxesListener listener) {
		inputManager.removeAxesListener(listener);
	}
	
	/**
	 * Removes an axis listener registered for a specified axis.
	 * @param axes the axis name set
	 * @param listener the listener
	 * @see fr.inria.insitu.touchstone.run.input.InputManager#removeAxesListener(Axes, AxesListener)
	 */
	public void removeAxesListener(Axes axes, AxesListener listener) {
		inputManager.removeAxesListener(axes, listener);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return "Core";
	}
	
	private static final Properties PROPERTIES = new Properties();
	static {
		PROPERTIES.setProperty(
				PROPERTY_NAME,
		"Core");
		PROPERTIES.setProperty(
				PROPERTY_AUTHOR, 
		"Caroline Appert & Jean-Daniel Fekete");
		PROPERTIES.setProperty(
				PROPERTY_URL,
		"http://insitu.lri.fr/touchstone");
		PROPERTIES.setProperty(
				PROPERTY_DESCRIPTION,
		"Core plugin");
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Properties getProperties() {
		return PROPERTIES;
	}
	
    /**
     * {@inheritDoc}
     */
    public Axes getAxes() {
        Axes axes = new Axes();
        // TODO load in all jars
        InputStream in = 
        	Platform.class.getClassLoader().getResourceAsStream("resources/axes.properties");
        if (in != null)
            try {
                BufferedReader bin = new BufferedReader(new InputStreamReader(in));
                
                while(true) {
                    String line = bin.readLine();
                    if (line == null) break;
                    if (line.startsWith("#")) continue;
                    int index = line.indexOf(':');
                    if (index == -1) continue;
                    String[] s = line.substring(index+1).split(" ");
                    for (int i = 0; i < s.length; i++) {
                        String a = s[i];
                        axes.add(a.trim());
                    }
                }
        }
        catch(Exception e) {
            LOG.log(Level.WARNING, "Cannot load axes from resources/axes.properties", e);
        } 
        return axes;
    }
	
	/**
	 * {@inheritDoc}
	 */
	public void install(Platform p) {
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void desinstall(Platform p) {
	}

	/**
	 * Returns the value of a given measure as an object.
	 * Consider using the method <code>getMeasure</code> instead followed by one of the method <code>getXXXValue</code> of class <code>Measure</code>.
	 * @param idMeasure The measure id.
	 * @return The value of the measure <code>name</code>
	 * @see Platform#getMeasure(String)
	 * @see Measure#getIntValue()
	 * @see Measure#getLongValue()
	 * @see Measure#getDoubleValue()
	 * @see Measure#getStringValue()
	 */
	public Object getMeasureValue(String idMeasure) {
		Measure m = getMeasure(idMeasure);
		if(m != null) return m.getValue();
		Factor f = Factor.getFactor(idMeasure);
		if(f != null) return f.getValue();
		return null;
	}
	
	/**
	 * Returns a measure given its id.
	 * @param name The measure id.
	 * @return The measure having the id <code>name</code>
	 */
	public Measure getMeasure(String name) {
		for(Iterator<Measure> i = measuresList.iterator(); i.hasNext(); ) {
			Measure next = i.next();
			if(next.getID().compareTo(name) == 0) {
				return next;
			}
		}
		return null;
	}
	
    /**
     * Sets the value of the specified measure.
     * If the measure does not exist, this method
     * creates a new measure and registers it to the
     * platform (this measure will return the value <code>value</code>).
     * @param measureID the measure id
     * @param value the value
     */
    public void setMeasureValue(String measureID, Object value) {
    	setMeasureValue(measureID, value, true);
    }
    
    /**
     * Sets the value of the specified measures.
     * If the measures does not exist, this method
     * creates new measures and registers them to the
     * platform (the measures will return the value <code>value</code>).
     * @param measureIDs the measure ids
     * @param values the values
     */
    public void setMeasureValue(String[] measureIDs, Object[] values) {
    	setMeasureValue(measureIDs, values, true);
    }
    
    /**
     * Sets the value of the specified measure.
     * If the measure does not exist, this method
     * creates a new measure and registers it to the
     * platform (this measure will return the value <code>value</code>).
     * @param measureID the measure id
     * @param value the value
     */
    private void setMeasureValue(String measureID, Object value, boolean logLine) {
    	boolean found = false;
    	Measure measure = null;
    	for(Iterator<Measure> i = measuresList.iterator(); i.hasNext(); ) {
    		measure = i.next();
			if(measure.getID().compareTo(measureID) == 0) {
				measure.setValue(value);
				found = true;
				break;
			}
		}
    	if(!found) {
    		measure = new Measure(measureID);
    		measure.setValue(value);
    		addMeasure(measure);
    	}
    	if(logLine && cinematicLogger != null && measureID.compareTo("currentTime") != 0) {
//    		setMeasureValue("currentTime", System.currentTimeMillis());
    		if(cinematicLogger.contains(measureID)
    				&& !InputManager.getInstance().isAxisDefined(measureID)) 
    			cinematicLogger.log();
    	}
    }
    
    /**
     * Sets the value of the specified measures.
     * If the measures does not exist, this method
     * creates new measures and registers them to the
     * platform (the measures will return the value <code>value</code>).
     * @param measureIDs the measure ids
     * @param values the values
     */
    private void setMeasureValue(String[] measureIDs, Object[] values, boolean logLine) {
    	int maxI = Math.max(measureIDs.length, values.length);
    	boolean cinelog = false;
    	for (int index = 0; index < maxI; index++) {
    		String measureID = measureIDs[index];
    		Object value = values[index];
	    	boolean found = false;
	    	Measure measure = null;
	    	for(Iterator<Measure> i = measuresList.iterator(); i.hasNext(); ) {
	    		measure = i.next();
				if(measure.getID().compareTo(measureID) == 0) {
					measure.setValue(value);
					found = true;
					break;
				}
			}
	    	if(!found) {
	    		measure = new Measure(measureID);
	    		measure.setValue(value);
	    		addMeasure(measure);
	    	}
	    	if(logLine && cinematicLogger != null && measureID.compareTo("currentTime") != 0) {
//	    		setMeasureValue("currentTime", System.currentTimeMillis());
	    		if(cinematicLogger.contains(measureID)
	    				&& !InputManager.getInstance().isAxisDefined(measureID)) {
	    			cinelog = true;
	    		}
	    	}
    	}
    	if (cinelog) {
    			cinematicLogger.log();
    	}
    }
	
	/**
	 * Resets the value of a measure.
	 * @param name the id measure to set
	 * @see fr.inria.insitu.touchstone.run.Measure#resetValue()
	 */
	public void resetMeasure(String name) {
		for(Iterator<Measure> i = measuresList.iterator(); i.hasNext(); ) {
			Measure next = i.next();
			if(next.getID().compareTo(name) == 0) 
				next.resetValue();
		}
	}
	
	/**
	 * Resets all the registered measures.
	 */
	public void resetAllMeasures() {
		for(Iterator<Measure> i = measuresList.iterator(); i.hasNext(); ) {
			i.next().resetValue();
		}
	}
	
	/**
	 * Registers a measure on the platform.
	 * @param m the measure.
	 * @return true if there was no measure already registered with the id of <code>m</code>,
	 * false otherwise. In false case, the new measure replaces the old one. 
	 */
	public boolean addMeasure(Measure m) {
		measuresList.add(0, m);
		Iterator<Measure> i = measuresList.listIterator(0);
		i.next();
		int cpt = 1;
		while(i.hasNext()) {
			Measure next = i.next();
			if(next.getID().compareTo(m.getID()) == 0) {
				measuresList.remove(cpt);
				return false;
			}
			cpt++;
		}
		return true;
	}
	
	/**
	 * Tests if a measure is already defined.
	 * @param id the measure id.
	 * @return true if there is a measure already registered with the id <code>id</code>,
	 * false otherwise. 
	 */
	public boolean isMeasureDefined(String id) {
		for (int i = 0; i < Experiment.MEASURES_ALWAYS_DEFINED.length; i++) {
			if(Experiment.MEASURES_ALWAYS_DEFINED[i].compareTo(id) == 0) return true;
		}
		for(Iterator<Measure> i = measuresList.iterator(); i.hasNext(); ) {
			Measure next = i.next();
			if(next.getID().compareTo(id) == 0) return true;
		}
		return false;
	}
	
	/**
	 * Tests if a measure is already defined.
	 * @param id the measure id.
	 * @return true if there is a measure already registered with the id <code>id</code>,
	 * false otherwise. 
	 */
	public boolean isMeasureAvailable(String id) {
		for (int i = 0; i < Experiment.MEASURES_ALWAYS_AVAILABLE.length; i++) {
			if(Experiment.MEASURES_ALWAYS_AVAILABLE[i].compareTo(id) == 0) return true;
		}
		for(Iterator<Measure> i = measuresList.iterator(); i.hasNext(); ) {
			Measure next = i.next();
			if(next.getID().compareTo(id) == 0) return true;
		}
		return false;
	}
	
	/**
	 * Registers a double measure on the platform.
	 * @param id the measure id.
	 * @return true if there was no measure already registered with the <code>id</code>,
	 * false otherwise. In false case, the new measure replaces the old one. 
	 * @see DoubleMeasure
	 */
	public boolean addDoubleMeasure(String id) {
		DoubleMeasure m = new DoubleMeasure(id);
		return addMeasure(m);
	}
	
	/**
	 * Registers an integer measure on the platform.
	 * @param id the measure id.
	 * @return true if there was no measure already registered with the id <code>id</code>,
	 * false otherwise. In false case, the new measure replaces the old one. 
	 * @see IntegerMeasure
	 */
	public boolean addIntegerMeasure(String id) {
		IntegerMeasure m = new IntegerMeasure(id);
		return addMeasure(m);
	}
    
	/**
	 * Unregisters a measure from the platform.
	 * @param idMeasure the measure id.
	 * @return true if the measure was previously registered, false if this measure is not found. 
	 */
	public boolean removeMeasure(String idMeasure) {
		Measure m = getMeasure(idMeasure);
		return measuresList.remove(m);
		
//		Iterator<Measure> i = measuresList.iterator();
//		int cpt = 1;
//		while(i.hasNext()) {
//			Measure next = i.next();
//			if(next.getID().compareTo(idMeasure) == 0) {
//				
//				System.out.println("removeMeasure..."+measuresList.remove(cpt+1)+" / "+idMeasure);
//				return true;
//			}
//			cpt++;
//		}
//		return false;
	}
	
	private void loadInputConfiguration(File file) 
	throws FileNotFoundException, IOException, ParseException {
        if (file == null) 
            throw new FileNotFoundException("null file");
		FileReader fin = new FileReader(file);
		BufferedReader in = new BufferedReader(fin);
		int lineno = 0;
		while (true) {
			String line = in.readLine();
			lineno++;
            if (line == null) break;
			if(line.length()==0) continue;
			if (line.startsWith("#")) continue;
			String[] binding = line.split(":");
			if(binding.length != 0) {
				if (binding.length != 2) {
					LOG.log(Level.SEVERE, file.getName()+":"+lineno+":bad input configuration line ignore");
				}
				else {
					if(isAxisDefined(binding[0]))
						setAxisValue(binding[0], Double.parseDouble(binding[1]));
					else
						defineVirtualAxis(binding[0], binding[1]);
				}
			}
		}
	}
	
	/**
	 * Returns a factor given its id.
	 * @param idFactor the factor id.
	 * @return the factor whose id is <code>idFactor</code>
	 */
	public Factor getFactor(String idFactor) {
		return Factor.getFactor(idFactor);
	}
	
	/**
	 * Returns the value of a factor given a factor id.
	 * @param idFactor the factor id.
	 * @return the value of the factor whose id is <code>idFactor</code>
	 */
	public Object getFactorValue(String idFactor) {
		Factor f = Factor.getFactor(idFactor);
		if(f != null) return f.getValue();
		Measure m = getMeasure(idFactor);
		if(m != null) return m.getValue();
		return null;
	}
	
	/**
	 * @return the experiment.
	 */
	public static Experiment getExperiment() {
		if(Platform.getInstance().experiment == null)
			Platform.getInstance().experiment = new Experiment();
		return Platform.getInstance().experiment;
	}

	/**
	 * Set the experiment.
	 * @param experiment the experiment.
	 */
	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}

	/**
	 * The class loader of the experiment. By default, it is set to the Platform class loader.
	 */
	public static ClassLoader classloader = Platform.class.getClassLoader();
	
    Plugin addPluginClass(String className) throws Exception {
        Class<?> c = Class.forName(className);
        Plugin p = (Plugin)c.newInstance();
        return addPlugin(p);
    }
    
    /**
     * Registers a plug-in into the <code>Platform</code>.
     * @param jarName The name of the jar file.
     * @return The <code>Plugin</code> object.
     * @throws Exception if the <code>Plugin</code> can not be loaded.
     */
    public Plugin addPluginJar(String jarName) throws Exception {
    	try {
        JarFile jar = new JarFile(new File(jarName), false, ZipFile.OPEN_READ);
        System.out.println("Searching for plugin in "+jarName);
        for (Enumeration<JarEntry> e = jar.entries(); e.hasMoreElements(); ) {
            JarEntry je = e.nextElement();
            if (je.isDirectory()) continue;
            String name = je.getName();
            System.out.println("name: "+name);
            if (name.endsWith(".class")) {
                StringBuffer buffer = new StringBuffer();
                int len = name.length()- ".class".length();
                for (int i = 0; i < len; i++) {
                    char c = name.charAt(i);
                    if (c == '/' || c == '\\') {
                        buffer.append('.');
                    }
                    else {
                        buffer.append(c);
                    }
                }
                String className = buffer.toString();
                System.out.println("Found class "+className);
                try {
                	System.out.println("--> try to build class object... ");
                    Class<?> c = Class.forName(className);
                    System.out.println("--> class object "+(c==null?"null":(""+c.getName())));
                    if (Plugin.class.isAssignableFrom(c)) {
                        Plugin p = (Plugin)c .newInstance();
                        System.out.println("Plugin: "+className);
                        return addPlugin(p);
                    }
                }
                catch(Exception ignored) {
                    ;
                }
            }
        }
        System.out.println("No plugin found in " + jarName);
    	} catch(Exception zipOpenExceptionIgnored) {
            ;
        }
        return null;
    }
    
    /**
     * Registers a <code>Plugin</code> into the <code>Platform</code>.
     * Note that you are not supposed to use it.
     * 
     * @param p the Plugin to add.
     * @return the <code>Plugin</code> installed.
     */
    public Plugin addPlugin(Plugin p) {
        if (plugins.contains(p)) return p; // already there
        plugins.add(p);
        p.install(this);
        return p;
    }
    
    /**
     * Unregisters a <code>Plugin</code> from the <code>Platform</code>.
     * Note that you are not supposed to use it.
     * 
     * @param p the Plugin to remove.
     */
    public void removePlugin(Plugin p) {
        if (plugins.remove(p)) {
            p.desinstall(this);
        }
    }
    
    /**
     * @return an iterator over the currently loaded <code>Plugin</code>s.
     */
    public Iterator<Plugin> pluginIterator() {
        return plugins.iterator();
    }

	public long getStartTime() {
		return startTime;
	}
	
}