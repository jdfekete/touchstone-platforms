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

/**
 * <b>TernaryAxisExpr</b> is the base class for ternary axis expressions.
 * 
 */
public abstract class TernaryAxisExpr extends BinaryAxisExpr {
    protected AxisExpr e3;
    /**
     * Creates a TernaryAxisExpr.
     * @param e1 the first expression
     * @param e2 the second expression
     * @param e3 the thrird expression
     */
    public TernaryAxisExpr(AxisExpr e1, AxisExpr e2, AxisExpr e3) {
        super(e1, e2);
        this.e3 = e3;
    }
    
    /**
     * {@inheritDoc}
     */
    public Set<String> collectComponentDependancies(Set<String> set) {
        super.collectComponentDependancies(set);
        e3.collectComponentDependancies(set);
        return set;
    }
    
    /**
     * {@inheritDoc}
     */
    public void reset() {
        super.reset();
        e3.reset();
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (super.equals(obj)) {
            TernaryAxisExpr other = (TernaryAxisExpr)obj;
            return other.e3.equals(e3);
        }
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return super.hashCode() 
            + e3.hashCode();
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "("+e1.toString()+","+e2.toString()+","+e3.toString()+")";
    }
}
