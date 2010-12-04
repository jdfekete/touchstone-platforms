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

import fr.inria.insitu.touchstone.run.Platform.EndCondition;
import fr.inria.insitu.touchstone.run.utils.BasicFactory;

/**
 * <b>EndConditionFactory</b> is a factory for generating EndConditions.
 * <p>
 * An end condition is a criterion (an instance of <b>EndCondition</b>). 
 * The touchstone platform contains a set of
 * predefined criterion. Use the tag &#64;touchstone.criterion
 * to add your own criteria (so that you can refer them by their id in an experiment
 * script and export them to the design platform).
 * </p>
 * <p>
 * For example, the <b>Dwell</b> condition used in the experiment 
 * <i>Compare multi-scale navigation techniques</i> described in the
 * touchstone CHI'07 article look like:
 * <pre>
 * &#47;*
 * * &#64;touchstone.criterion Dwell
 * **&#47;
 * public class Dwell extends AbstractGIEndCondition {
 * 	public Dwell(int duration) { ... }
 * }
 * </pre>  
 * Now, the experiment script can contain instructions that are
 * directly "interpretable" by the run platform such as:
 * <pre>
 * &lt;block ... class="FittsBlock" criterionTrial="Dwell(1000) | (TimeOut(60000)=>{Too Long})" &gt;
 * ...
 * &lt;/block&gt;
 * </pre>
 * This example illustrates how different criteria can be combined to
 * define a more complex criterion. First, each criterion can be a hit
 * criterion, i.e. when reached the trial is a <b>HIT</b>, or
 * a miss criterion. By default a criterion is a hit criterion. To
 * indicate that a criterion is a miss, it must be followed by its error
 * reason:
 * <pre>
 * &lt;criterion&gt;=>{&lt;error_reason&gt;}
 * </pre>
 * e.g.
 * <pre>
 * TimeOut(60000)=>{Too Long}
 * </pre>
 * Second, criteria can be combined using the operators {|, &, ^, !}. On this example,
 * a trial ends at least when one of the two criteria is checked: <code>Dwell(1000)</code>
 * or <code>TimeOut(60000)</code>. In the first case, it is a hit trial while, in the
 * second case, it is a miss trial.
 * @see fr.inria.insitu.touchstone.run.Platform.EndCondition
 * @see fr.inria.insitu.touchstone.run.endConditions.AbstractEndCondition
 * @see fr.inria.insitu.touchstone.run.endConditions.OrEndCondition
 * @see fr.inria.insitu.touchstone.run.endConditions.AndEndCondition
 * @see fr.inria.insitu.touchstone.run.endConditions.XorEndCondition
 * @see fr.inria.insitu.touchstone.run.endConditions.NotEndCondition
 * </p> 
 * @author Caroline Appert
 */
public class EndConditionFactory extends BasicFactory {
    private static final EndConditionFactory instance = new EndConditionFactory();
    
    /**
     * Creates a factory.
     */
    public EndConditionFactory() {
        super("criteria");
    }
    
    /**
     * @return the default instance of this factory
     */
    public static EndConditionFactory getInstance() {
        return instance;
    }
    
    /**
     * Creates an EndCondition using a specified constructor.
     * @param name the EndConditon name
     * @param args the constructor arguments
     * @return an EndCondition
     * @throws Exception 
     */
    public static EndCondition create(String name, Object[] args) throws Exception {
        return getInstance().createEndCondition(name, args);
    }
    
    /**
     * Creates an EndCondition using a specified constructor.
     * @param name the EndConditon name
     * @param args the constructor arguments
     * @return an EndCondition
     * @throws Exception 
     */
    public EndCondition createEndCondition(String name, Object[] args) throws Exception {
        return (EndCondition)createFor(name, args);
    }

}
