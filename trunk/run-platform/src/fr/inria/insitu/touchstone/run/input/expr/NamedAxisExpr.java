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
package fr.inria.insitu.touchstone.run.input.expr;

import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

import fr.inria.insitu.touchstone.run.input.AxisExpr;
import fr.inria.insitu.touchstone.run.input.InputEnvironment;
import fr.inria.insitu.touchstone.run.input.InputManager;

/**
 * <b>NamedAxisExpr</b> implements an axis expr referencing a
 * device axis.
 * 
 */
public class NamedAxisExpr implements AxisExpr {
    String axis;
    private static Pattern idPattern;
    
    /**
     * Creates a NamedAxisExpr with an axis name.
     * @param axis the axis name
     */
    public NamedAxisExpr(String axis) {
        this.axis = axis;
    }
    /**
     * {@inheritDoc}
     */
    public Set<String> collectComponentDependancies(Set<String> set) {
        set.add(axis);
        return set;
    }

    /**
     * {@inheritDoc}
     */
    public double getValue(InputEnvironment env) {
        return env.getAxisValue(axis);
    }

    /**
     * {@inheritDoc}
     */
    public void reset() {
    }
    
    /**
     * @return Returns a pattern matching the syntax for ID.
     */
    public static final Pattern getIdPattern() {
        if (idPattern == null) {
            idPattern = Pattern.compile("[a-zA-Z][a-zA-Z_0-9]*");
        }
        return idPattern;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        String device = InputManager.getAxisDevice(axis);
        String comp = InputManager.getAxisComponent(axis);
        if (! getIdPattern().matcher(device).matches()) {
            device = "\""+StringEscapeUtils.escapeJava(device)+"\"";
        }
        if (! getIdPattern().matcher(comp).matches()) {
            comp = "\""+StringEscapeUtils.escapeJava(comp)+"\"";
        }
        return device + "." + comp;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof NamedAxisExpr) {
            NamedAxisExpr other = (NamedAxisExpr) obj;
            return other.axis.equals(axis);
        }
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return axis.hashCode();
    }
}
