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

import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;

import fr.inria.insitu.touchstone.run.utils.Loader;

/**
 * <b>MouseDisabler</b> is a singleton object that disables and enables the
 * standard mouse feedback.
 * 
 */
public class MouseDisabler {
    private static final Cursor        NO_CURSOR        = Toolkit.getDefaultToolkit().createCustomCursor(
                                                                Loader.loadImage("noCursor.gif"),
                                                                new Point(
                                                                        7,
                                                                        7),
                                                                "No cursor");
    private static final MouseDisabler INSTANCE         = new MouseDisabler();
    private static final Robot         ROBOT            = createRobot();

    private Point                      recenteringPoint = null;

    private Cursor                     oldCursor;
    private boolean                    installed        = false;
    private Frame                      frame            = null;

    protected MouseDisabler() {
    }

    /**
     * @return Returns the instance of this class
     */
    public static MouseDisabler getInstance() {
        return INSTANCE;
    }

    /**
     * Install the mouse disabler on the specified frame.
     * 
     * @param frame
     *            the frame
     * @return true if the installation has succeeded
     */
    public boolean install(Frame frame) {
        if (frame == null || installed) {
            return false;
        }
        // TODO mess up with event queue
        // MyEventQueue.disableMouse();
        oldCursor = frame.getCursor();
        frame.setCursor(NO_CURSOR);
        this.frame = frame;
        installed = true;
        return true;
    }

    /**
     * Uninstall the mouse disabler on the frame.
     * 
     * @return true if the uninstallation has succeeded
     */
    public boolean uninstall() {
        if (frame == null || !installed) {
            return false;
        }
        frame.setCursor(oldCursor);
        // TODO reinstall event queue
        // MyEventQueue.enableMouse()
        installed = false;
        Rectangle wr = frame.getBounds();
        int xcenter = wr.x + wr.width/2;
        int ycenter = wr.y + wr.height/2;
        recenteringPoint = new Point(xcenter, ycenter);
        this.frame = null;
        return true;
    }

    private static Robot createRobot() {
        try {
            return new Robot();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Make sure the standard mouse is trapped.
     */
    public void doDisable() {
        if (recenteringPoint != null) {
              // -- Recenter the system cursor inside the window
              ROBOT.mouseMove(recenteringPoint.x, recenteringPoint.y);
              // -- Flush direct mouse reader's event queue (moving system cursor generates an event !!)
//              if (mouse_reader != null)
//                    mouse_reader.getState();
        }
  }
}
