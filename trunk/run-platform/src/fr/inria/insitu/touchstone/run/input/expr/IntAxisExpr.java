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

import fr.inria.insitu.touchstone.run.input.AxisExpr;
import fr.inria.insitu.touchstone.run.input.InputEnvironment;

/**
 * <b>IntAxisExpr</b> implements the integration axis expression.
 * 
 * <p>Integration computes the sum of its sub-expression.
 * 
 */
public class IntAxisExpr extends TernaryAxisExpr {
    double prev = 0;
    
    /**
     * Creates an IntAxisExpr.
     * @param e the sub-expression
     * @param min the minimum value to clamp to
     * @param max the maximum value to clamp to
     */
    public IntAxisExpr(AxisExpr e, AxisExpr min, AxisExpr max) {
        super(e, min, max);
    }
    
    /**
     * Creates an IntAxisExpr with no clamp values.
     * @param e the sub-expression
     */
    public IntAxisExpr(AxisExpr e) {
        this(e, ConstantAxisExpr.MIN_VALUE, ConstantAxisExpr.MAX_VALUE);
    }

    /**
     * {@inheritDoc}
     */
    public double getValue(InputEnvironment env) {
        prev += e1.getValue(env);
        prev = Math.max(e2.getValue(env), prev);
        prev = Math.min(e3.getValue(env), prev);
        return prev;
    }

    /**
     * {@inheritDoc}
     */
    public void reset() {
        super.reset();
        prev = 0;
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "int"+super.toString();
    }
}
