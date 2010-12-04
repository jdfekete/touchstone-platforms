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
package fr.inria.insitu.touchstone.run.input;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.ControllerEvent;
import net.java.games.input.ControllerListener;
import net.java.games.input.Event;
import net.java.games.input.Keyboard;
import net.java.games.input.Mouse;
import antlr.RecognitionException;
import antlr.TokenStreamException;
import fr.inria.insitu.touchstone.run.LaunchExperiment;
import fr.inria.insitu.touchstone.run.Platform;
import fr.inria.insitu.touchstone.run.input.expr.NamedAxisExpr;
import fr.inria.insitu.touchstone.run.input.parser.AxisExprLexer;
import fr.inria.insitu.touchstone.run.input.parser.AxisExprParser;
import fr.inria.insitu.touchstone.run.utils.KeyMapJInputAWT;
import fr.inria.insitu.touchstone.run.utils.StringUtils;

/**
 * <b>InputManager</b> is the central repository for device management.
 * 
 * <p>
 * InputManager manages physical devices and transform them into virtual
 * devices.
 * <p>
 * Physical devices are called "controllers". Each controller has one or several
 * components. For example, the Mouse controller has at least three components:
 * the two axis called "X" and "Y", and one button called "Left". Thus, the
 * physical or concrete axes are called "Mouse.X", "Mouse.Y" and "Mouse.Left".
 * 
 * <p>
 * Virtual devices are manages as a set of virtual axes with a simple name such
 * as "x" or "y". The value of each virtual axis is computed from the values of
 * one or several concrete axes using a simple expression. For example, to
 * create a virtual axis called "height" computed by integrating the values of
 * the mouse wheel when it turns, the expression is
 * <code>"int(Mouse.Wheel)"</code>.
 * 
 */
public class InputManager 
implements InputEnvironment, ControllerListener, Runnable {
	private static final Logger     LOG                  = Logger.getLogger(InputManager.class.getName());
	private static InputManager     instance;
	private boolean                 started              = false;
	private Thread                  pollThread;
	private long                    pollDelay            = 10;
	private long                    lastPollTime;

	// Management of listeners
	private ArrayList<AxesListener> axesListenerList     = new ArrayList<AxesListener>();
	private ArrayList<Axes>         axesListenerNameList = new ArrayList<Axes>();

	// To find controller by name
	private Map<String, Controller> nameControllerMap    = new HashMap<String, Controller>();
	private Map<String, AxisDesc>   nameAxisMap          = new HashMap<String, AxisDesc>();
	private Map<Component, String>  componentNameMap     = new HashMap<Component, String>();

	// Set of valid components
	private Set<Controller>         polledMap;
	private Axes                    axisToRecompute      = new Axes();
	private Axes                    axisModified;
	private boolean                 fireInvoked          = false;
	/**
	 * True if the <code>InputManager</code> is in debugging mode.
	 */
	public static boolean           debugging            = 	false;

	static final char[]             SPECIAL              = {
		'\t',
		' ', '!', '"', '#', '$', '%', '&', '\'', 
		'(', ')', '*', '+', ',', '-', '.', '/',
		':', ';', '<', '=', '>', '?', '@', '[', 
		'\\', ']', '^', '_', '`', '{', '|', '}', 
		'~'
	};

	static final String[]           SPECIAL_NAME         = {
		"Tab",
		"Space", "Exclam", "Quote", "Sharp", "Number", "Dollar", "Percent", "Amp", "Apos",
		"LParen", "RParen", "Ast", "Plus", "Comma", "Minus", "Dot", "Slash",
		"Colon", "Semi", "Less", "Equal", "Greater", "Quest", "At", "LBrack",
		"Backslash", "RBrack", "Circ",  "Underscore", "Backquote", "LBrack", "Bar", "RBrack",
		"Tilde"
	};


	protected InputManager() {
		registerControllers();
	}

	/**
	 * @return the singleton instance of this class
	 */
	public static InputManager getInstance() {
		if (instance == null) {
			instance = new InputManager(); 
			instance.init();
		}
		return instance;
	}

	private void init() {
		try {
			GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice dev = genv.getDefaultScreenDevice();
			Rectangle r = dev.getDefaultConfiguration().getBounds();
			defineVirtualAxis("Window.width", ""+r.getWidth());
			defineVirtualAxis("Window.height", ""+r.getHeight());
			defineVirtualAxis("Mouse.x", ""+0);
			defineVirtualAxis("Mouse.y", ""+0);
			defineVirtualAxis("Mouse.Left", ""+0);
			defineVirtualAxis("Mouse.Middle", ""+0);
			defineVirtualAxis("Mouse.Right", ""+0);
			defineVirtualAxis("Mouse.Wheel", ""+0);
			for (Iterator<String> iterator = KeyMapJInputAWT.mappings.values().iterator(); iterator.hasNext();) {
				String kbAxis = iterator.next();
				defineVirtualAxis("Keyboard."+kbAxis, ""+0);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets the controller with the specified name.
	 * 
	 * @param name
	 *            the name
	 * @param c
	 *            the Controller
	 */
	private void setController(String name, Controller c) {
		name = StringUtils.clean(name);
		Controller old = nameControllerMap.get(name);
		if (old == c)
			return;
		if (old != null) {
			removeController(name);
		}
		nameControllerMap.put(name, c);
		Component[] comps = c.getComponents();
		for (int i = 0; i < comps.length; i++) {
			Component comp = comps[i];

			String prevComp = componentNameMap.get(comp);
			String axis;
			AxisDesc d;
			if (prevComp == null) {
				d = new AxisDesc(comp, c, name);
				axis = d.name;
				componentNameMap.put(comp, axis);
				if (debugging) {
					System.out.println("Available axis: "+axis);
				}
			}
			else {
				axis = computeAxisName(comp, name);
				d = new AxisDesc(axis, new NamedAxisExpr(prevComp));
				d.comp = comp;
				polledMap = null; // will recompute next time                
				if (debugging) {
					System.out.println("Aliasing axis "+axis+" to "+prevComp);
				}
			}
			nameAxisMap.put(axis, d);
		}
	}

	/**
	 * Removes the controller with the specified name.
	 * 
	 * @param name
	 *            the name
	 */
	private void removeController(String name) {
		name = StringUtils.clean(name);
		Controller c = nameControllerMap.get(name);
		if (c == null)
			return;
		nameControllerMap.remove(name);
		Component[] comps = c.getComponents();
		for (int i = 0; i < comps.length; i++) {
			Component comp = comps[i];
			String axis = componentNameMap.get(comp);
			componentNameMap.remove(comp);
			nameAxisMap.remove(axis);
		}
	}

	/**
	 * Returns the name of a specified component.
	 * @param comp the component
	 * @return the name of the specified component
	 */
	private String getComponentName(Component comp) {
		return componentNameMap.get(comp);
	}

	private AxisDesc getAxisDesc(String axis) {
		return nameAxisMap.get(axis);
	}

	/**
	 * Returns the device name of the axis.
	 * @param axis the axis
	 * @return the device name
	 */
	public static String getAxisDevice(String axis) {
		int i = axis.lastIndexOf('.');
		return axis.substring(0, i);
	}

	/**
	 * Returns the component name of the axis.
	 * @param axis the axis
	 * @return the component name
	 */
	public static String getAxisComponent(String axis) {
		int i = axis.lastIndexOf('.');
		return axis.substring(i+1);
	}

	/**
	 * Returns the AxisExpr associated with a specified axis.
	 * @param axis the axis
	 * @return the AxisExpr or null if the axis is a physical
	 * axis
	 */
	public AxisExpr getAxisExpr(String axis) {
		AxisDesc cd = getAxisDesc(axis);
		if (cd != null) {
			if (cd.comp != null) return null; // don't show aliases
			return cd.expr;
		}
		String msg = "Unknown axis " + axis;
		LOG.log(Level.SEVERE, msg);
		if(Platform.JINPUT_ON) throw new UnknownComponentException(msg);
		else return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getAxisValue(String axis) {
		AxisDesc cd = getAxisDesc(axis);
		if (cd != null) {
			return cd.value;
		}
		String msg = "Unknown axis " + axis;
		LOG.log(Level.SEVERE, msg);
		if(Platform.JINPUT_ON) throw new UnknownComponentException(msg);
		else return -1;
	}

	/**
	 * Defines a constant value.
	 * @param axis the name
	 * @param value the value
	 * @param manual true is this value is set by the developer (false if it is set by the polling thread)
	 */
	public void setAxisValue(String axis, double value, boolean manual) {
		AxisDesc c = getAxisDesc(axis);
		if (c == null) {
			c = new AxisDesc(axis);
			nameAxisMap.put(axis, c);
		}
		c.setValue(value, lastPollTime);
		if(!manual) c.eval(lastPollTime);
		notifyIfNeeded();
	}

	/**
	 * Defines a constant value.
	 * @param axis the name
	 * @param value the value
	 */
	public void setAxisValue(String axis, double value) {
		setAxisValue(axis, value, false);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isAxisDefined(String axis) {
		return getAxisDesc(axis) != null;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getAxisTime(String axis) {
		AxisDesc cd = getAxisDesc(axis);
		if (cd != null) {
			return cd.time;
		}
		String msg = "Unknown axis " + axis;
		LOG.log(Level.SEVERE, msg);
		if(Platform.JINPUT_ON) throw new UnknownComponentException(msg);
		else return -1;
	}

	/**
	 * {@inheritDoc}
	 */
	public Iterator<String> axisIterator() {
		return nameAxisMap.keySet().iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	public void resetAxes() {
		for (Iterator<AxisDesc> iter = nameAxisMap.values().iterator(); 
		iter.hasNext(); ) {
			AxisDesc d = iter.next();
			if (d.expr != null) {
				d.expr.reset();
			}
		}
	}

	private synchronized void addAxisToRecompute(Axes set) {
		if (axisToRecompute == null) {
			axisToRecompute = new Axes();
		}
		axisToRecompute.addAll(set);
	}

	private synchronized void addAxisModified(String axis) {
		if (axisModified == null) {
			axisModified = new Axes();
		}
		axisModified.add(axis);
	}


	/**
	 * Starts polling devices.
	 */
	public void start() {
		if (started)
			return;
		if (pollThread == null) {
			pollThread = new Thread() {
				public void run() {

					while (true) {
						if (!started)
							break;
						poll();
						try {
							Thread.sleep(pollDelay);
						} catch (InterruptedException e) {
							; // ignore
						}
					}
				}
			};
		}
		started = true;
		pollThread.setName("JInput");
		pollThread.start();
	}

	/**
	 * Resets input manager to initial configuration (=> delete all defined virtual axes).
	 */
	public void setToDefaultConfiguration() {
		boolean running = started;
		stop();
		registerControllers();
		if(running) start();
	}

	private void registerControllers() {
		if(nameControllerMap != null) nameControllerMap.clear();
		if(componentNameMap != null) componentNameMap.clear();
		if(nameAxisMap != null) nameAxisMap.clear();
		if(polledMap != null) polledMap.clear();

		init();

		if(!Platform.JINPUT_ON) return;

		try {

			ControllerEnvironment env = ControllerEnvironment.getDefaultEnvironment();
			env.addControllerListener(this);
			Controller[] cont = env.getControllers();
			for (int i = 0; i < cont.length; i++) {
				Controller c = cont[i];
				setController(c.getName(), c);
			}
		} catch(Exception e) {
			// We do not want to stop all if jinput does not work
			LOG.log(Level.INFO, "Failed to collect controllers through jInput");
			throw new UnknownComponentException("Failed to collect controllers through jInput");
		} 
	}

	/**
	 * @return A list of the names of the currently plugged mice.
	 */
	public ArrayList<String> getMice() {
		ArrayList<String> result = new ArrayList<String>();
		for(Iterator<String> i = nameControllerMap.keySet().iterator(); i.hasNext(); ) {
			String nameController = i.next();
			Controller controller = nameControllerMap.get(nameController);
			if (controller instanceof Mouse) {
				nameController = StringUtils.clean(nameController);
				result.add(nameController);
			}
		}
		return result;
	}

	/**
	 * Sets which mouse is used as the primary mouse.
	 * @param nameMouse The name of the mouse.
	 */
	public void setDefaultMouse(String nameMouse) {
		if(nameMouse.compareTo(LaunchExperiment.DEFAULT_AWT)==0) {
			Platform.getInstance().enableAWTMouse();
			return;
		}
		nameMouse = StringUtils.clean(nameMouse);
		Platform.getInstance().disableAWTMouse();
		Controller mouseController = nameControllerMap.get(nameMouse);
		if(mouseController == null) {
			String msg = "cannot find mouse controller "+nameMouse;
			LOG.log(Level.SEVERE, msg);
			if(Platform.JINPUT_ON) throw new UnknownComponentException(msg);
		}
		try {
			if(isAxisDefined(nameMouse+".x")) defineVirtualAxis("Mouse.x", "int("+nameMouse+".x,0.0,Window.width)");
			if(isAxisDefined(nameMouse+".y")) defineVirtualAxis("Mouse.y", "int("+nameMouse+".y,0.0,Window.height)");
			if(isAxisDefined(nameMouse+".Left")) defineVirtualAxis("Mouse.Left", nameMouse+".Left");
			if(isAxisDefined(nameMouse+".Middle")) defineVirtualAxis("Mouse.Middle", nameMouse+".Middle");
			if(isAxisDefined(nameMouse+".Right")) defineVirtualAxis("Mouse.Right", nameMouse+".Right");
			if(isAxisDefined(nameMouse+".z")) defineVirtualAxis("Mouse.Wheel", nameMouse+".z");
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return A list of the names of the currently plugged keyboards.
	 */
	public ArrayList<String> getKeyboards() {
		ArrayList<String> result = new ArrayList<String>();
		for(Iterator<String> i = nameControllerMap.keySet().iterator(); i.hasNext(); ) {
			String nameController = i.next();
			Controller controller = nameControllerMap.get(nameController);
			if (controller instanceof Keyboard) {
				result.add(nameController);
			}
		}
		return result;
	}

	/**
	 * @param nameKeyboard the name of keyboard controller to use as default keyboard.
	 * Sets the default keyboard.
	 */
	public void setDefaultKeyboard(String nameKeyboard) {
		if(nameKeyboard.compareTo(LaunchExperiment.DEFAULT_AWT)==0) {
			Platform.getInstance().enableAWTKeyboard();
			return;
		}
		nameKeyboard = StringUtils.clean(nameKeyboard);
		Platform.getInstance().disableAWTKeyboard();
		Controller kbController = nameControllerMap.get(nameKeyboard);
		if(kbController == null) {
			String msg = "cannot find keyboard controller "+nameKeyboard;
			LOG.log(Level.SEVERE, msg);
			if(Platform.JINPUT_ON) throw new UnknownComponentException(msg);
		}
		try {
			for (Iterator<String> iterator = KeyMapJInputAWT.mappings.values().iterator(); iterator.hasNext();) {
				String kbAxis = iterator.next();
				if(isAxisDefined(nameKeyboard+"."+kbAxis)) defineVirtualAxis("Keyboard."+kbAxis, nameKeyboard+"."+kbAxis);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}


	/**
	 * @return A list a the available physical axes in the current input configuration.
	 */
	public ArrayList<String> getAvailablePhysicalAxes() {
		ArrayList<String> res = new ArrayList<String>();
		for(Iterator<String> i = nameControllerMap.keySet().iterator(); i.hasNext(); ) {
			String nameController = i.next();
			Controller c = nameControllerMap.get(nameController);
			Component[] comps = c.getComponents();
			AxisDesc d;
			for (int cpt = 0; cpt < comps.length; cpt++) {
				Component comp = comps[cpt];
				d = new AxisDesc(comp, c, nameController);
				res.add(d.name);
			}
		}
		return res;
	}

	/**
	 * @return A list a the available axes (physical and virtual) in the current input configuration.
	 */
	public ArrayList<String> getAvailableAxes() {
		ArrayList<String> res = new ArrayList<String>();
		for(Iterator<String> i = nameAxisMap.keySet().iterator(); i.hasNext(); ) {
			String axis = i.next();
			res.add(axis);
		}
		return res;
	}

	/**
	 * {@inheritDoc}
	 */
	public void run() {
		fireEvents();
	}

	private synchronized void poll() {
		try {
			Set<Controller> controllers = getPolledMap();
			// First poll all the controllers
			// Then mark all the global axis dependent on components as modified
			// Then recompute all the global axis
			for (Iterator<Controller> iter = controllers.iterator(); iter.hasNext();) {
				Controller c = iter.next();
				if (!c.poll()) {
					removeController(c.getName());
				}
			}

			boolean fixCursor = false;
			Event e = new Event();
			for (Iterator<Controller> iter = controllers.iterator(); iter.hasNext();) {
				Controller c = iter.next();
				while (c.getEventQueue().getNextEvent(e)) {
					long time = e.getNanos()/1000000;
					Component comp = e.getComponent();
					if (comp instanceof Mouse) {
						fixCursor = true;
					}
					String axis = getComponentName(comp);
					axis = StringUtils.clean(axis);
					//					System.out.println("axis: "+axis);
					AxisDesc cd = getAxisDesc(axis);
					cd.setValue(e.getValue(), time);
					lastPollTime = Math.max(lastPollTime, time); // time in millis
				}
			}
			if (fixCursor) {
				MouseDisabler md = MouseDisabler.getInstance();
				md.doDisable();
			}
			while(! axisToRecompute.isEmpty()) {
				Axes set = new Axes(axisToRecompute);
				axisToRecompute.clear();
				for(Iterator<String> iter = set.iterator(); iter.hasNext(); ) {
					String axis = iter.next();
					AxisDesc d = getAxisDesc(axis);
					if (d.expr == null) {
						LOG.log(Level.SEVERE, "Axis to recompute is not an expression "+axis);
					}
					else {
						d.eval(lastPollTime);
					}
				}
			}
			notifyIfNeeded();
		} catch(Exception e) {
			//			e.printStackTrace();
		}
	}


	protected void notifyIfNeeded() {
		if (axisModified != null && !fireInvoked) {
			fireInvoked = true;
			SwingUtilities.invokeLater(this);
		}
	}

	/**
	 * Notifies all the listeners of the new values.
	 */
	private synchronized void fireEvents() {
		fireInvoked = false;
		fireAxesListeners(axisModified);
		axisModified = null;
	}

	/**
	 * Stop polling devices.
	 */
	public synchronized void stop() {
		if (!started)
			return;
		MouseDisabler md = MouseDisabler.getInstance();
		md.uninstall();
		started = false;
		pollThread = null;
		ControllerEnvironment env = ControllerEnvironment
		.getDefaultEnvironment();
		env.removeControllerListener(this);
	}

	/**
	 * @return the started
	 */
	public boolean isStarted() {
		return started;
	}

	/**
	 * Defines an axis to have a specified name and to be computed according the
	 * the specified expression.
	 * 
	 * <p>
	 * If the axis already existed, its definition is overridden. If the
	 * expression is null, the axis is undefined.
	 * 
	 * <p>
	 * For example, to specify the the "x" axis is the mouse X axis, the code
	 * is:
	 * 
	 * <pre>
	 * defineVirtualAxis(&quot;x&quot;, parse(&quot;Mouse.x&quot;));
	 * </pre>
	 * 
	 * or
	 * 
	 * <pre>
	 *       defineVirtualAxis(&quot;height&quot;, parse(&quot;min(100,max(0,integrate(Mouse.wheel)))&quot;);
	 * </pre>
	 * 
	 * @param name
	 *            the axis name
	 * @param expr
	 *            the AxisExpr
	 */
	public synchronized void defineVirtualAxis(String name, AxisExpr expr) {
		assert(name.indexOf('.')==-1);
		if (expr == null) {
			nameAxisMap.remove(name);
		}
		else {
			AxisDesc d = new AxisDesc(name, expr);
			nameAxisMap.put(name, d);
			d.eval(lastPollTime);
		}
		polledMap = null; // will recompute next time
	}

	/**
	 * Defines an axis to have a specified name and to be computed according
	 * the specified expression.
	 * @param name the axis name
	 * @param expr the expression using the standard syntax
	 * @throws ParseException if the expression cannot be parsed
	 */
	public void defineVirtualAxis(String name, String expr) 
	throws ParseException {
		defineVirtualAxis(name, parse(expr));
	}

	/**
	 * @return the polledMap
	 */
	private synchronized Set<Controller> getPolledMap() {
		if(!Platform.JINPUT_ON) {
			if (polledMap == null)
				polledMap = new HashSet<Controller>();
			return polledMap;
		}
		if (polledMap == null) {
			polledMap = new HashSet<Controller>();
			ControllerEnvironment env = 
				ControllerEnvironment.getDefaultEnvironment();
			Controller[] cont = env.getControllers();
			for (int i = 0; i < cont.length; i++) {
				Controller c = cont[i];
				polledMap.add(c);
			}

			// First remove the propagation lists to recompute them
			for (Iterator<Entry<String, AxisDesc>> iter = 
				nameAxisMap.entrySet().iterator(); 
			iter.hasNext();) {
				Entry<String, AxisDesc> e = iter.next();
				AxisDesc d = e.getValue();
				d.propagateList = null;
			}

			// Recompute the propagation lists
			Axes compSet = new Axes();
			Map<String,AxisDesc> addedMap = new HashMap<String, AxisDesc>();
			for (Iterator<Entry<String, AxisDesc>> iter = 
				nameAxisMap.entrySet().iterator(); 
			iter.hasNext();) {
				Entry<String, AxisDesc> e = iter.next();
				AxisDesc d = e.getValue();
				if (d.expr == null) continue; // no propagation to compute
				compSet.clear();
				d.expr.collectComponentDependancies(compSet);
				for (Iterator<String> citer = compSet.iterator(); 
				citer.hasNext();) {
					String compName = citer.next();
					AxisDesc dep = getAxisDesc(compName);
					if (dep == null) {
						dep = addedMap.get(compName);
						if (dep == null) {
							LOG.warning("Creating axis "+compName
									+" referenced by "+e.getKey()
									+" in expression '"+d.expr+"'");
							dep = new AxisDesc(compName);
							addedMap.put(compName, dep);
						}
					}
					if (dep.propagateList == null) {
						dep.propagateList = new Axes();
					}
					dep.propagateList.add(e.getKey());
				}
			}
			nameAxisMap.putAll(addedMap); // add the newly added axis
		}
		return polledMap;
	}

	/**
	 * Parses an expression specified in a string and returns the parsed
	 * expression.
	 * 
	 * @param expr
	 *            the textual representation of the expression
	 * @return an AxisExpr
	 * @exception ParseException
	 *                if a parsing error occured
	 */
	public AxisExpr parse(String expr) throws ParseException {
		AxisExprLexer lexer = new AxisExprLexer(new StringReader(expr));
		AxisExprParser parser = new AxisExprParser(lexer);

		try {
			return parser.expr();
		} catch (RecognitionException e) {
			throw new ParseException(e.getMessage(), e.getColumn());
		} catch (TokenStreamException e) {
			throw new ParseException(e.getMessage(), 0);
		}
	}

	/**
	 * Register an AxesListener for a specified set of axis.
	 * 
	 * @param axes
	 *            the set of axis names
	 * @param listener
	 *            the listener
	 */
	public void addAxesListener(Axes axes, AxesListener listener) {
		// to initialize axes with the current values
		if(axes != null) {
			axisToRecompute.addAll(axes);
			poll();
		}

		axesListenerList.add(listener);
		axesListenerNameList.add(axes);
	}

	/**
	 * Register an AxesListener for all the registered axes.
	 * 
	 * @param listener
	 *            the listener
	 */
	public void addAxesListener(AxesListener listener) {
		addAxesListener(null, listener);
	}

	/**
	 * Computes the set of modified axes that match (intersect) the set of
	 * axis wanted. 
	 * @param wanted the wanted axes
	 * @param modified the modified axes
	 * @return the set of axes that match or null if nothing matches
	 */
	public static Axes matchAxis(Axes wanted, Axes modified) {
		if (modified == null) return null;
		if (wanted == null
				|| wanted.containsAll(modified)) {
			return modified;
		}
		if (modified.containsAll(wanted)) {
			return wanted;
		}
		Axes match = null;
		for(Iterator<String> iter = wanted.iterator(); iter.hasNext(); ) {
			String wantedAxis = iter.next();
			if (modified.contains(wantedAxis)) {
				if (match == null) {
					match = new Axes();
				}
				match.add(wantedAxis);
			}
		}
		return match;
	}

	/**
	 * Fires all the axis listeners associated with a specified axis
	 * using a specified value and time.
	 * @param modified the set of modified axes
	 */
	private void fireAxesListeners(Axes modified) {
		if (debugging) {
			System.out.print("Modified axis: [ ");
			String modifiedAxis;
			for(Iterator<String> i = modified.iterator(); i.hasNext(); ) {
				modifiedAxis = i.next();
				System.out.print(modifiedAxis+":"+getAxisValue(modifiedAxis)+" ");
			}
			System.out.println(" ]");
		}
		
		AxesEvent event = new AxesEvent(modified, this);
		Platform.getInstance().evalEndCondition(event);
		
		for (int i =0; i < axesListenerList.size(); i++) {
			AxesListener l = axesListenerList.get(i);
			Axes wanted = axesListenerNameList.get(i);
			Axes match = matchAxis(wanted, modified);
			//			if (debugging) {
			//			System.out.println("Matched"+match+"="+modified+"/"+wanted);
			//			}
			if (match != null) {
				event = new AxesEvent(match, this);
				l.axesChanged(event);
			}
		}
	}

	/**
	 * Compares two sets of axes.
	 * @param a1 the first set
	 * @param a2 the second set
	 * @return true if they are equal, false otherwise
	 */
	public static boolean axesEquals(Axes a1, Axes a2) {
		if (a1 == a2) return true;
		if (a1 == null || a2 == null) return false;
		return a1.equals(a2);
	}

	/**
	 * Removes an AxesListener for a specified set of axes.
	 * 
	 * @param axes
	 *            the axes
	 * @param listener
	 *            the listener
	 */
	public void removeAxesListener(Axes axes, AxesListener listener) {
		for (int i = 0; i < axesListenerList.size(); i++) {
			if (axesListenerList.get(i) == listener
					&& axesEquals(axes, axesListenerNameList.get(i))) {
				axesListenerNameList.remove(i);
				axesListenerList.remove(i);
				return;
			}
		}
	}

	/**
	 * Removes an AxesListener for all the axes.
	 * 
	 * @param listener
	 *            the listener
	 */
	public void removeAxesListener(AxesListener listener) {
		removeAxesListener(null, listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void controllerAdded(ControllerEvent ev) {
		Controller c = ev.getController();
		LOG.info("Controller "+c+" added");
		setController(c.getName(), c);
	}

	/**
	 * {@inheritDoc}
	 */
	public void controllerRemoved(ControllerEvent ev) {
		Controller c = ev.getController();
		LOG.info("Controller "+c+" removed");
		setController(c.getName(), null);
	}

	/**
	 * @return the pollDelay
	 */
	public long getPollDelay() {
		return pollDelay;
	}


	/**
	 * @param pollDelay the pollDelay to set
	 */
	public void setPollDelay(long pollDelay) {
		this.pollDelay = pollDelay;
	}

	/**
	 * @return the debugging
	 */
	public boolean isDebugging() {
		return debugging;
	}

	/**
	 * Sets the InputManager in debug mode.
	 * @param debug the debugging to set
	 */
	public static void setDebugging(boolean debug) {
		debugging = debug;
	}

	private static String computeAxisName(Component comp, String contName) {
		String cname = comp.getIdentifier().getName();
		if (cname.equals("Unknown")) {
			cname = "_"+comp.getName();
		}
		StringBuffer tname = new StringBuffer();
		tname.append(contName);
		tname.append('.');
		for (int i = 0; i < cname.length(); i++) {
			char c = cname.charAt(i);
			int index = Arrays.binarySearch(SPECIAL, c);
			if (index >= 0) {
				tname.append(SPECIAL_NAME[index]);
				//				tname.append('_'); 
			}
			else if (Character.isJavaIdentifierPart(c)) {
				tname.append(c);
			}
			// ignore everything else
		}
		return tname.toString();

	}

	private class AxisDesc {
		String     name;
		double     value;
		long       time;
		Component  comp;
		Controller cont;
		AxisExpr   expr;
		Axes       propagateList;

		AxisDesc(String name) {
			this.name = name;
			this.time = -1;            
		}

		AxisDesc(Component comp, Controller cont, String contName) {
			this(computeAxisName(comp, StringUtils.clean(contName)));
			this.comp = comp;
			this.cont = cont;
		}

		AxisDesc(String name, AxisExpr expr) {
			this(name);
			this.expr = expr;
		}

		boolean isVirtual() {
			return comp == null;
		}

		void setValue(double value, long time) {
			if (this.time == time && this.value == value) {
				if (debugging) {
					System.out.println("Skipping propagation of "+name+" at time "+time+" for value "+value);
				}
				return;
			}
			this.value = value;
			this.time = time;
			if (propagateList != null)
				addAxisToRecompute(propagateList);
			addAxisModified(name);
		}

		void eval(long time) {
			setValue(expr.getValue(InputManager.this), time);
		}
	}

	/**
	 * Export the input axes as a set of measures.
	 * @param f the output file to generate
	 */
	public void exportAsXML(File f) {
		if (f == null) return;
		try {
			FileOutputStream fout = new FileOutputStream(f);
			OutputStreamWriter oout = new OutputStreamWriter(
					fout, Charset.forName("UTF-8"));
			PrintWriter out = new PrintWriter(oout);
			out.print("<?xml version='1.0' encoding='UTF-8'?>\n"
					+"<touchstone version='1.0'>\n");


			DateFormat dateFormat = DateFormat.getDateTimeInstance();
			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			String date = dateFormat.format(new Date());
			out.print(" <platform date='"+date+"'>\n"
					+"    <plugin id='Input'>\n"
					+"    <factors/>\n"
					+"    <measures>\n");
			ArrayList<String> listOfAxes = getAvailablePhysicalAxes();
			for(Iterator<String> i = listOfAxes.iterator(); i.hasNext(); ) {
				String axis = i.next();
				out.println("      <measure id=\""+axis+"\" name=\"Input Axis\" type=\"ratio\"/>");
			}

			out.println("    </measures></plugin></platform></touchstone>");
			out.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] arguments) throws Exception {
		final InputManager mgr = InputManager.getInstance();
		String name = "input.xml";
		if (arguments.length != 0) {
			name = arguments[0];
		}
		mgr.exportAsXML(new File(name));
	}
}
