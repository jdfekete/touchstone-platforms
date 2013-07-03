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
package fr.inria.insitu.touchstone.design.motor;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

public class Practice extends Step implements Serializable {

	private static final long serialVersionUID = 42L;
	
	private Vector<Vector<PracticeBlock>> practices = new Vector<Vector<PracticeBlock>>();
	private Vector<Intertitle> blockClasses = new Vector<Intertitle>();
	private Vector<Boolean> practicesEnabled = new Vector<Boolean>();
	private Vector<Boolean> repetitionsEnabled = new Vector<Boolean>();
	
	public Practice(Vector<Vector<PracticeBlock>> practices, Vector<Boolean> practicesEnabled, Vector<Boolean> repetitionsEnabled, Vector<Intertitle> blockClasses) {//, Vector<Intertitle> blockClasses) {
		super();
		this.practices = practices;
		this.practicesEnabled = practicesEnabled;
		this.repetitionsEnabled = repetitionsEnabled;
		this.blockClasses = blockClasses;
	}
	
	public Practice(Blocking blocking) {
		super();
		// practice at experiment level
		practices.add(new Vector<PracticeBlock>());
		practicesEnabled.add(true);
		repetitionsEnabled.add(false);
		blockClasses.add(null);
		// practice at block levels
		Block blockStructure = blocking.getSelectedBlockStructure();
		int depth = blockStructure.getDepth() - 1;
		for (int i = 1; i < depth; i++) {
			practices.add(new Vector<PracticeBlock>());
			practicesEnabled.add(true);
			repetitionsEnabled.add(false);
			blockClasses.add(null);
		}
	}
	
	public void enablePractice(boolean enabled, int blockingLevel) {
		practicesEnabled.set(blockingLevel, enabled);
	}

	public boolean isPracticeEnabled(int blockingLevel) {
		return practicesEnabled.get(blockingLevel);
	}
	
	public Vector<PracticeBlock> getPracticeBlocksFor(Block block, Vector<Factor> experimentFactors) {
		int depth = block.getDepth();
		int index = practices.size() - depth;
		if(depth <= 0 || !practicesEnabled.get(index)) return null;
		Vector<PracticeBlock> practiceBlocks = practices.get(index);
		Vector<PracticeBlock> res = new Vector<PracticeBlock>();
		for(int i = 0; i < practiceBlocks.size(); i++) {
			Block practiceBlock = practiceBlocks.get(i);
			Vector<Value> practiceBlockValues = practiceBlock.getValues();
			PracticeBlock newBlock = new PracticeBlock();
			Vector<Value> newValues = new Vector<Value>();
			Vector<Factor> sampledFactor = new Vector<Factor>();
			for(int j = 0; j < experimentFactors.size(); j++) {
				if(practiceBlockValues.get(j).isBlockValue()) {
					Vector<Value> valuesForThisBlock = new Vector<Value>();
					Block b = block;
					while(b != null) {
						valuesForThisBlock.addAll(b.getValues());
						b = b.getParent();
					}
					for (Iterator<Value> iterator = valuesForThisBlock.iterator(); iterator.hasNext();) {
						Value next = iterator.next();
						if(next.getFactor().equals(experimentFactors.get(j)))
							newValues.add(next);
					}
				} else if(practiceBlockValues.get(j).isSample()) {
					sampledFactor.add(experimentFactors.get(j));
				} else {
					newValues.add(practiceBlockValues.get(j));
				}
			}
			newBlock.setValues(newValues);
			Vector<Vector<Value>> combinations = new Vector<Vector<Value>>();
			getCombinations(combinations, new Vector<Value>(), sampledFactor, 0);
			
			Collections.shuffle(combinations);
			
			int nbTrials = practiceBlock.size();
			for(int j = 0; j < nbTrials; j++) {
				Block trial = new Block();
				trial.setValues(combinations.get((j%combinations.size())));
				newBlock.add(trial);
				trial.setParent(newBlock);
			}
			res.add(newBlock);
		}
		Random random = new Random();
		try {
			random.order(res);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	private void getCombinations(Vector<Vector<Value>> res, Vector<Value> inBuilt, Vector<Factor> factors, int index) {
		if(index >= factors.size()) {
			res.add(inBuilt);
		} else {
			Factor f = factors.get(index);
			Vector<Value> values = f.getValues();
			for (Iterator<Value> iterator = values.iterator(); iterator.hasNext();) {
				Value value = iterator.next();
				Vector<Value> inBuiltCopy = new Vector<Value>(inBuilt);
				inBuiltCopy.add(value);
				getCombinations(res, inBuiltCopy, factors, index+1);
			}
		}
	}
	
	public Vector<PracticeBlock> getPracticeAtExperimentLevel() {
		return practices.get(0);
	}
	
	public Vector<PracticeBlock> getPracticeAtBlockLevel(int i) {
		return practices.get(i);
	}	
	
	public Vector<Vector<PracticeBlock>> getPractices() {
		return practices;
	}

	public void setPractices(Vector<Vector<PracticeBlock>> practices) {
		this.practices = practices;
	}

	protected Object clone() throws CloneNotSupportedException {
		Vector<Vector<PracticeBlock>> practicesCopy = new Vector<Vector<PracticeBlock>>();
		Vector<Boolean> practicesEnabledCopy = new Vector<Boolean>();
		Vector<Boolean> repetitionsEnabledCopy = new Vector<Boolean>();
		Vector<Intertitle> blockClassesCopy = new Vector<Intertitle>();
		Iterator<Boolean> itPrEnabled = practicesEnabled.iterator();
		Iterator<Boolean> itRepEnabled = repetitionsEnabled.iterator();
		Iterator<Intertitle> itBlockClasses = blockClasses.iterator();
		for (Iterator<Vector<PracticeBlock>> iterator = practices.iterator(); iterator.hasNext();) {
			Vector<PracticeBlock> next = iterator.next();
			if(next == null) practicesCopy.add(null);
			else {
				Vector<PracticeBlock> copy = new Vector<PracticeBlock>();
				for (Iterator<PracticeBlock> iterator2 = next.iterator(); iterator2.hasNext();) {
					PracticeBlock block = iterator2.next();
					copy.add((PracticeBlock)block.clone());
				}
				practicesCopy.add(copy);
			}
			practicesEnabledCopy.add(itPrEnabled.next());
			repetitionsEnabledCopy.add(itRepEnabled.next());
			Intertitle blClass = itBlockClasses.next();
			if(blClass == null)
				blockClassesCopy.add(null);
			else
				blockClassesCopy.add((Intertitle)blClass.clone());
		}
		return new Practice(practicesCopy, practicesEnabledCopy, repetitionsEnabledCopy, blockClassesCopy);
	}

	public Vector<Boolean> getPracticesEnabled() {
		return practicesEnabled;
	}

	public Vector<Boolean> getReplicationsEnabled() {
		return repetitionsEnabled;
	}

	public Vector<Intertitle> getBlockClasses() {
		return blockClasses;
	}

	public void setBlockClasses(Vector<Intertitle> blockClasses) {
		this.blockClasses = blockClasses;
	}
	
	public Intertitle getBlockClassAtBlockLevel(int i) {
		if(i < blockClasses.size())
			return blockClasses.get(i);
		else
			return null;
	}	
	
	public Intertitle setBlockClassAtBlockLevel(int i, Intertitle blockClass) {
		return blockClasses.set(i, blockClass);
	}

}
