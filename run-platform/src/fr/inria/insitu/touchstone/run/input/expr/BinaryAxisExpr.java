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
 * <b>BinaryAxisExpr</b> is the base class of 
 * binary axis expressions.
 * 
 */
public abstract class BinaryAxisExpr implements AxisExpr {
    AxisExpr e1;
    AxisExpr e2;

    /**
     * Creates a BinaryAxisExpr.
     * @param e1 first expr
     * @param e2 second expr
     */
    public BinaryAxisExpr(AxisExpr e1, AxisExpr e2) {
        this.e1 = e1;
        this.e2 = e2;
    }
    
    /**
     * {@inheritDoc}
     */
    public Set<String> collectComponentDependancies(Set<String> set) {
        e1.collectComponentDependancies(set);
        e2.collectComponentDependancies(set);
        return set;
    }

    /**
     * {@inheritDoc}
     */
    public void reset() {
        e1.reset();
        e2.reset();
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        // TODO Auto-generated method stub
        return "("+e1.toString()+","+e2.toString()+")";
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj.getClass() != getClass()) {
            return false;
        }
        BinaryAxisExpr e = (BinaryAxisExpr)obj;
        return e1.equals(e.e1) && e2.equals(e.e2);
    }
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return getClass().hashCode() + e1.hashCode() + e2.hashCode();
    }
}
