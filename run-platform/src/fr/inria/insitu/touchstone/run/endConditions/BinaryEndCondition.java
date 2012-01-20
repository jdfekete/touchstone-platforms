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
import java.util.EventObject;

import javax.swing.Timer;
import javax.swing.event.DocumentEvent;

import com.illposed.osc.OSCMessage;

import fr.inria.insitu.touchstone.run.Platform.EndCondition;
import fr.inria.insitu.touchstone.run.input.AxesEvent;

/**
 * <b>BinaryEndCondition</b> is an abstract base class for binary
 * end conditions.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.10 $
 */
public abstract class BinaryEndCondition implements EndCondition {
        protected EndCondition endCondition1 = null;
        protected EndCondition endCondition2 = null;
        
        /**
         * Builds a AndEndCondition.
         * @param ec1 The first condition
         * @param ec2 The second condition
         */
        public BinaryEndCondition(EndCondition ec1, EndCondition ec2) {
            endCondition1 = ec1;
            endCondition2 = ec2;
        }

        /**
         * Evaluates the end condition of the two values.
         * @param v1 the first value
         * @param v2 the second value
         * @return the tagged of the evaluation
         */
        public abstract boolean eval(boolean v1, boolean v2);

        /**
         * {@inheritDoc}
         */
        public boolean isReached(AxesEvent e) {
        	return eval(endCondition1.isReached(e), endCondition2.isReached(e));
        }
        
        /**
         * {@inheritDoc}
         */
        public boolean isReached(InputEvent e) {
        	return eval(endCondition1.isReached(e), endCondition2.isReached(e));
        }

        /**
         * {@inheritDoc}
         */
        public boolean isReached(Timer timer, long when) {
            return eval(endCondition1.isReached(timer, when), endCondition2.isReached(timer, when));
        }
        
        /**
         * {@inheritDoc}
         */
        public boolean isReached(OSCMessage message, long when) {
            return eval(endCondition1.isReached(message, when), endCondition2.isReached(message, when));
        }
        
        public boolean isReached(EventObject e) {
        	return eval(endCondition1.isReached(e), endCondition2.isReached(e));
    	}
        
        public boolean isReached(DocumentEvent e) {
        	return eval(endCondition1.isReached(e), endCondition2.isReached(e));
    	}

        /**
         * {@inheritDoc}
         */
        public void start() {
            endCondition1.start();
            endCondition2.start();
        }
        
        /**
         * {@inheritDoc}
         */
        public void stop() {
            endCondition1.stop();
            endCondition2.stop();
        }
        
}
