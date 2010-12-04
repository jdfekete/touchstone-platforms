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

import fr.inria.insitu.touchstone.run.exp.model.ExperimentPart;
import fr.inria.insitu.touchstone.run.utils.BasicFactory;

/**
 * <b>ExperimentPartFactory</b> is a factory for generating ExperimentParts.
 * 
 * <p>
 * An experiment part is a block (an instance of <b>Block</b>) or an intertitle
 * (an instance of <b>Intertitle</b>). The touchstone platform contains a set of
 * predefined blocks and intertitles. Use the tags &#64;touchstone.intertitle
 * and &#64;touchstone.block to add your own blocks and
 * intertitles (so that you can refer them by their id in an experiment
 * script and export them to the design platform).
 * </p>
 * <p>
 * For example, <b>FittsBlock</b> and the intertitle <b>Message</b> classes
 * look like:
 * <pre>
 * &#47;*
 * * &#64;touchstone.block FittsBlock
 * **&#47;
 * public class FittsBlock extends Block {
 * 	...
 * }
 * </pre>  
 * <pre>
 * &#47;*
 * * &#64;touchstone.intertitle Message
 * **&#47;
 * public class Message extends Intertitle {
 * 	public Message(String message) { ... }
 * }
 * </pre>
 * Now, the experiment script can contain instructions that are
 * directly "interpretable" by the run platform such as:
 * <pre>
 * &lt;intertrial ... class="Message({Bring the target,\ni.e. the red square,\nas fast as possible\non the middle black line.})" ... &gt;
 * &lt;block ... class="FittsBlock" ... &gt;
 * ...
 * &lt;/block&gt;
 * &lt;/intertrial&gt;
 * </pre>
 * </p>
 * @author Caroline Appert
 * @version $Revision: 1.10 $
 */
public class ExperimentPartFactory extends BasicFactory {
    private static final ExperimentPartFactory instance = new ExperimentPartFactory();
    
    /**
     * Creates an ExperimentPartFactory.
     */
    public ExperimentPartFactory() {
        super("intertitles");
        addDefaultCreators("blocks");
    }
    
    /**
     * @return the default instance of this factory
     */
    public static ExperimentPartFactory getInstance() {
        return instance;
    }
    
    /**
     * Creates a ExperimentPartFactory using a specified constructor.
     * @param name the EndConditon name
     * @param args the constructor arguments
     * @return an EndCondition
     * @throws Exception 
     */
    public static ExperimentPart create(String name, Object[] args) throws Exception {
        return getInstance().createExperimentPart(name, args);
    }
    
    /**
     * Creates an EndCondition using a specified constructor.
     * @param name the EndConditon name
     * @param args the constructor arguments
     * @return an EndCondition
     * @throws Exception 
     */
    public ExperimentPart createExperimentPart(String name, Object[] args) throws Exception {
        return (ExperimentPart)createFor(name, args);
    }

}
