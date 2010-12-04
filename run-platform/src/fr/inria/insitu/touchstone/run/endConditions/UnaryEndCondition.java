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
package fr.inria.insitu.touchstone.run.endConditions;

import java.awt.event.InputEvent;

import javax.swing.Timer;

import com.illposed.osc.OSCMessage;

import fr.inria.insitu.touchstone.run.Platform.EndCondition;
import fr.inria.insitu.touchstone.run.input.AxesEvent;

/**
 * <b>UnaryEndCondition</b> is the abstract base class for 
 * unary end conditions. 
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.9 $
 */
public abstract class UnaryEndCondition implements EndCondition {
    protected EndCondition endCondition = null;
    protected long endTime = -1;

    /**
     * Creates an UnaryEndCondition.
     * @param ec the dependant condition
     */
    public UnaryEndCondition(EndCondition ec) {
        endCondition = ec;
    }

    /**
     * Evaluates the end condition given the value of the
     * child end condition.
     * @param v the value
     * @return the tagged of the operator
     */
    public boolean eval(boolean v) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isReached(AxesEvent e) {
    	if (eval(endCondition.isReached(e))) {
          return true;
      }
      return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isReached(InputEvent e) {
    	if (eval(endCondition.isReached(e))) {
          return true;
      }
      return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isReached(Timer timer, long when) {
        if (eval(endCondition.isReached(timer, when))) {
            endTime = when;
            return true;
        }
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isReached(OSCMessage message, long when) {
        if (eval(endCondition.isReached(message, when))) {
            endTime = when;
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void start() {
        endCondition.start();
    }
    
    /**
     * {@inheritDoc}
     */
    public void stop() {
        endCondition.stop();
    }
}
