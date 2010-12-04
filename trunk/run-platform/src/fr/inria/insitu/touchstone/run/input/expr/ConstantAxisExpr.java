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

import fr.inria.insitu.touchstone.run.input.AxisExpr;
import fr.inria.insitu.touchstone.run.input.InputEnvironment;

/**
 * <b>ConstantAxisExpr</b> implements the constant expression.
 * 
 */
public class ConstantAxisExpr implements AxisExpr {
    double value;
    /** The value MIN_VALUE. */
    public static final ConstantAxisExpr MIN_VALUE = new ConstantAxisExpr(-Double.MAX_VALUE) {
        public String toString() {return "MIN_VALUE"; }
    };
    /** The value MAX_VALUE. */
    public static final ConstantAxisExpr MAX_VALUE = new ConstantAxisExpr(Double.MAX_VALUE) {
        public String toString() { return "MAX_VALUE"; }
    };
    /** The value ZERO. */
    public static final ConstantAxisExpr ZERO = new ConstantAxisExpr(0);
    /** The value ONE. */
    public static final ConstantAxisExpr ONE = new ConstantAxisExpr(1);
    
    /**
     * Creates a ConstantAxisExpr.
     * @param value the constant
     */
    public ConstantAxisExpr(double value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> collectComponentDependancies(Set<String> set) {
        return set;
    }

    /**
     * {@inheritDoc}
     */
    public double getValue(InputEnvironment env) {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    public void reset() {
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        if (value == -Double.MAX_VALUE) {
            return "MIN_VALUE";
        }
        if (value == Double.MAX_VALUE) {
            return "MAX_VALUE";
        }
        return Double.toString(value);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof ConstantAxisExpr) {
            ConstantAxisExpr cst = (ConstantAxisExpr) obj;
            return cst.value == value;
        }
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        long bits = Double.doubleToLongBits(value);
        return (int)(bits ^ (bits >>> 32));
    }
}
