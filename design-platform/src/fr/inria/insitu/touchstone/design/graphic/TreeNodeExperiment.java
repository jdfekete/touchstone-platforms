/*   TouchStone design platform is a software to design protocols for lab        *
 *   experiments. It is published under the terms of a BSD license               *
 *   (see details below)                                                         *
 *   Author: Caroline Appert (appert@lri.fr)                                     *
 *   Copyright (c) 2010 Caroline Appert and INRIA, France.                       *
 *   TouchStone design platform reuses parts of an early version which were      *
 *   programmed by Matthis Gilbert.                                              *
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
package fr.inria.insitu.touchstone.design.graphic;

import javax.swing.tree.DefaultMutableTreeNode;

public class TreeNodeExperiment extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 1L;

	private Object value;
	private int type;

	public static int EXPERIMENT_TYPE = 1;
	public static int SETUP_TYPE = 2;
	public static int PRACTICE_TYPE = 3;
	public static int BLOCK_TYPE = 4;
	public static int INTERBLOCK_TYPE = 5;
	public static int TRIAL_TYPE = 6;
	public static int INTERTRIAL_TYPE = 7;
	public static int PARTICIPANT_TYPE = 8;
	public static int INTERPRACTICE_TYPE = 9;

	public TreeNodeExperiment(String label, Object value, int type) {
		super(label);
		this.value = value;
		this.type = type;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public boolean equals(Object obj) {
		boolean sameLabel = ((String)this.getUserObject()).compareTo((String)(((TreeNodeExperiment)obj).getUserObject())) == 0; 
		return 
		(sameLabel 
		&& (this.value == ((TreeNodeExperiment)obj).value && this.type == ((TreeNodeExperiment)obj).type))
		||
		// because practice blocks are generated at each change
		(sameLabel 
		&& (this.type == PRACTICE_TYPE && ((TreeNodeExperiment)obj).type == PRACTICE_TYPE));
	}

}
