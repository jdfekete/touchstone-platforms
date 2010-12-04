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
package fr.inria.insitu.touchstone.run.exp.defaults;

import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Window;

import javax.swing.JDialog;

import fr.inria.insitu.touchstone.run.Platform;
import fr.inria.insitu.touchstone.run.Platform.EndCondition;
import fr.inria.insitu.touchstone.run.exp.model.Intertitle;
import fr.lri.swingstates.canvas.CExtensionalTag;
import fr.lri.swingstates.canvas.CText;
import fr.lri.swingstates.canvas.Canvas;

/**
 * Message is a Canvas to display a message. 
 * The Canvas size is defined using the values of the "Window.width" and "Window.height" axes found in the input configuration (file input.conf in the experiment directory).
 * The text size for the message is adjusted using the Canvas size.
 * 
 * Bottom room is dedicated to display an 
 * additional message indicating which action
 * the user has to perform to validate this
 * message. 
 * 
 * @touchstone.intertitle Message
 * @author Caroline Appert
 */
public class Message extends Intertitle {

	private static Font font = new Font("verdana", Font.PLAIN, 16);
	
	static int HEIGHT_ACTION_MESSAGE = 60;
	
	private double currentY = 0;
	
	private Canvas canvas;
	private CExtensionalTag lineTag;
	private JDialog dialog;
	
	private String message;
	
	private Component registeredComponent = null;
	
	private Window fsWindow;
	
	/**
	 * Builds a Message. 
	 * Sets its size to the platform
	 * page dimensions.
	 * @param message The message of this screener. To display a message on several lines, use the sequence '\n' to break lines.
	 * 
	 * @see fr.inria.insitu.touchstone.run.Platform#getPageHeight()
	 * @see fr.inria.insitu.touchstone.run.Platform#getPageWidth()
	 */
	public Message(String message) {
		super();
		this.message = message;
		canvas = new Canvas(
				(int)getPlatform().getAxisValue("Window.width"),
				(int)getPlatform().getAxisValue("Window.height"));
	}
	
	/**
	 * Sets the scriptTextField to display in the bottom.
	 * @param text The scriptTextField.
	 */
	public void setActionText(String text) {
		CText l = canvas.newText(0, 0, text, font);
		l.setReferencePoint(0.5, 0.5).translateTo(
				getPlatform().getAxisValue("Window.width")/2,
				getPlatform().getAxisValue("Window.height") - HEIGHT_ACTION_MESSAGE/2);
	}

	/**
	 * {@inheritDoc}
	 */
	public void beginIntertitle() {
		lineTag = new CExtensionalTag() { };
		addText(message);
		init();
		setActionText("Action to go on: "+getEndCondition().getEndCondition());
		
		registeredComponent = Platform.getInstance().getRegisteredComponent();
		
		dialog = new JDialog(Platform.getInstance());
		dialog.getContentPane().add(canvas);
		dialog.setUndecorated(true);
		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice gs = ge.getDefaultScreenDevice();
	    fsWindow = gs.getFullScreenWindow();
		
		if(fsWindow != null) {
			try {
			    gs.setFullScreenWindow(dialog);
			} finally { }
		} else {
			dialog.pack();
		}
		
	    dialog.setVisible(true);
		dialog.setModal(true);
		Platform.getInstance().registerComponent(canvas);
		canvas.requestFocus();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void endIntertitle() {
		dialog.setVisible(false);
		canvas.removeAllShapes();
		if(registeredComponent != null) Platform.getInstance().registerComponent(registeredComponent);
		if(fsWindow == null) return;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice gs = ge.getDefaultScreenDevice();
		try {
		    gs.setFullScreenWindow(fsWindow);
		} finally { }
	}
	
	/**
	 * Adds a text to the message.
	 * 
	 * @param text The text to add.
	 * @return This screener.
	 */
	public Message addText(String text) {
		String[] lines = text.split("\n");
		for(int i = 0; i < lines.length; i++) {
			addLine(lines[i]);
		}
		return this;
	}
	
	private Message addLine(String line) {
		CText l = canvas.newText(0, currentY, line, font);
		l.addTag(lineTag);
		currentY+=(l.getHeight() + 25);
		return this;
	}
	
	private void init() {
		double availableViewWidth = getPlatform().getAxisValue("Window.width") - 40;
		double availableViewHeight = getPlatform().getAxisValue("Window.height") - HEIGHT_ACTION_MESSAGE;
		double ratioX = availableViewWidth / lineTag.getBoundingBox().getWidth();
		double ratioY = availableViewHeight / lineTag.getBoundingBox().getHeight();
		if(ratioX > 2.5) ratioX = 2.5;
		if(ratioY > 2.5) ratioY = 2.5;
		double ratio = Math.min(ratioX, ratioY);
		lineTag.scaleBy(ratio);
		lineTag.translateBy(
				getPlatform().getAxisValue("Window.width")/2 - lineTag.getCenterX(),
				(getPlatform().getAxisValue("Window.height") - HEIGHT_ACTION_MESSAGE)/2 - lineTag.getCenterY());
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setEndCondition(EndCondition ec) {
		super.setEndCondition(ec);
	}
}
