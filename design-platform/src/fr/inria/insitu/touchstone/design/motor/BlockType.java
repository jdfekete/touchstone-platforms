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
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;


public class BlockType implements Serializable {

	private static final long serialVersionUID = 42L;
	
	private Vector<Vector<Factor>> structure;
	
	private BlockType() { }

	/** 
	 * @param subjFactors
	 * @param freeFactors
	 * @param blockedFactors
	 */
	public BlockType(Vector<Factor> subjFactors, Vector<Factor> freeFactors, Vector<Vector<Factor>> blockedFactors){
		structure = new Vector<Vector<Factor>>();
		//subj factors
		structure.add(new Vector<Factor>());
		//free factors
		structure.add(new Vector<Factor>());
		setSubjFactors(subjFactors);
		setFreeFactors(freeFactors);
		setBlockedFactors(blockedFactors);

		if ((structure.get(1).size()==0)&&(structure.size()>2))
			structure.remove(1);

		// sort the structure
		for (Vector<Factor> vf : structure)
			Collections.sort(vf, new Comparator<Factor>(){
				public int compare(Factor o1, Factor o2) {
					return o1.toString().compareToIgnoreCase(o2.toString());
				}
			});
		
		if (getFreeFactors().size()==0){
			Vector<Factor> vf = structure.lastElement();
			structure.remove(vf);
			structure.get(1).addAll(vf);
		}	
	}
	
	protected Object clone() throws CloneNotSupportedException {
		Vector<Vector<Factor>> struct = new Vector<Vector<Factor>>();
		for (Iterator<Vector<Factor>> iterator = structure.iterator(); iterator.hasNext();) {
			Vector<Factor> next = iterator.next();
			Vector<Factor> copy = new Vector<Factor>();
			for (Iterator<Factor> iterator2 = next.iterator(); iterator2.hasNext();) {
				copy.add((Factor)iterator2.next().clone());
			}
			struct.add(copy);
		}
		BlockType cloned = new BlockType();
		cloned.structure = struct;
		return cloned; 
	}
	
	
	public String toString() {
		String result = "";

		Vector<Factor> subjFactors = getSubjFactors();
		Vector<Factor> freeFactors = getFreeFactors();
		
		
		
		Vector<Vector<Factor>> blockedFactors = getBlockedFactors();
		if (subjFactors.size()>0){
			result += "Subj[ ";
			for (int i =0 ; i<subjFactors.size()-1;i++)
				result += subjFactors.get(i).toString()+" x ";
			result += subjFactors.lastElement().toString()+" ] x ";
		}

		if (blockedFactors.size()>0){
			for (int i=0 ; i<(blockedFactors.size()-1);i++){
				result += "Block"+(i+1)+"[ ";
				for (int j =0 ; j<(blockedFactors.get(i).size()-1);j++)
					result+= blockedFactors.get(i).get(j)+" x ";
				result += blockedFactors.get(i).lastElement()+" ] x ";					
			}
			result += "Block"+blockedFactors.size()+"[ ";
			for (int i = 0 ; i< (blockedFactors.lastElement().size()-1);i++){
				result += blockedFactors.lastElement().get(i)+" x ";
			}
			result += blockedFactors.lastElement().lastElement()+" ] x ";
		}	
		
		// remove factice factor
		Vector<Factor> frFactors = new Vector<Factor>();
		for (Iterator<Factor> iterator = freeFactors.iterator(); iterator.hasNext();) {
			Factor factor = iterator.next();
			if(factor.getShortName().length() != 0)
				frFactors.add(factor);
		}
		

		if (frFactors.size()>0){
			for( int i = 0 ; i< frFactors.size()-1;i++) {
				result += frFactors.get(i)+" x ";
			}
			result += frFactors.lastElement();
		} else {
			if(result.length() > 3)
				result = result.substring(0, result.length()-3);
		}

		return result;
	}

	public Vector<Factor> getSubjFactors(){
		return structure.get(0);
	}
	public void setSubjFactors(Vector<Factor> vf ){
		structure.set(0, vf);
	}
	public void addSubjFactor(Factor f){
		getSubjFactors().add(f);
	}
	

	public Vector<Factor> getFreeFactors(){
		return structure.get(1);
	}
	
	public Vector<Factor> getFreeFactorsWithoutFactice(){
		Vector<Factor> freeFactors = new Vector<Factor>();
		for (Iterator<Factor> iterator = structure.get(1).iterator(); iterator.hasNext();) {
			Factor factor = iterator.next();
			if(factor.getShortName().length() > 0) freeFactors.add(factor);
		}
		return freeFactors;
	}
	
	public void setFreeFactors(Vector<Factor> vf ){
		structure.set(1, vf);
	}
	public void addFreeFactor(Factor f){
		getFreeFactors().add(f);
	}	
		
	public Vector<Vector<Factor>> getBlockedFactors(){
		Vector<Vector<Factor>> result = new Vector<Vector<Factor>>();
		if (structure.size()>2)
			result = new Vector<Vector<Factor>>(structure.subList(2, structure.size()));
		return result;
	}
	
	public int getBlockingLevel(String factorID) {
		for(int i = 1; i < structure.size(); i++) {
			Vector<Factor> factorsAtLevel_i = structure.get(i);
			for (Iterator<Factor> iterator = factorsAtLevel_i.iterator(); iterator.hasNext();) {
				Factor next = iterator.next();
				if(factorID.compareTo(next.getShortName()) == 0)
					return i;
			}
		}
		return 0;
	}
	
	public void setBlockedFactors(Vector<Vector<Factor>> vvf ){
		//remove blocked Factors
		while (structure.size()>2)
			structure.remove(2);
		//add Subj&free factors if they doesnt exist yet
		while (structure.size()<2)
			structure.add(new Vector<Factor>());
		//set the new blocked Factors
		structure.addAll(vvf);
	}
	

	/**
	 * 
	 * @return the number of blocking level of this blocktype
	 */
	public int getNumberOfBlockLevel(){
		return getBlockedFactors().size();
	}

	
	
	
	/**
	 * 
	 * @return the block generated by this BlockType
	 */
	public Block generateBlock(){
		Vector<Block> result = new Vector<Block>();
		Vector<Vector<Value>> combinations = getAllCombinations(getSubjFactors());
		Vector<Vector<Factor>> factors = new Vector<Vector<Factor>>();
		factors.addAll(getBlockedFactors());		
		factors.add(getFreeFactors());
		
		if(combinations.size()==0) {
			result.add(new Block(new Vector<Value>(),factors));
		} else {
			for (Vector<Value> combination : combinations) {
				result.add(new Block(combination,factors));
			}
		}
		return new Block(result);
	}

	/**
	 * 
	 * @param i the blocking level
	 * @return the number of block of this blocking level
	 */
	public int getNumberOfBlock(int i ){
		int numberOfBlock = 0;
		if (structure.size()>i){
			numberOfBlock = 1;
			for(Factor f : structure.get(i))
				numberOfBlock = f.getValues().size()*numberOfBlock;
		}
		return numberOfBlock;					
	}
	
	/** 
	 * @return the minimum number of subjects required to do the experiment  
	 */
	public int getNumberOfSubjects(){
		return getNumberOfBlock(0);
	}
	/**
	 * 
	 * @return the number of trial 
	 */
	public int getNumberOfTrials(){
		int nbTrials = 1;
		for (Vector<Factor> vf : structure)
			for(Factor f : vf)
				nbTrials*=f.getValues().size();
		return nbTrials;
	}

	/**
	 * Let be two factors A and B which have two values 1 and 2. 
	 * getAllCombinations( [A;B] ) will return :
	 * [[A1;B1];[A1;B2];[A2;B1];[A2;B2]]
	 *  
	 * @param factors
	 * @return all the combination of value of the given factors.
	 */
	public static Vector<Vector<Value>> getAllCombinations(Vector<Factor> factors){
		Vector<Vector<Value>> result = new Vector<Vector<Value>>();
		result.add(new Vector<Value>());
		for (Factor f: factors)
			result = getCombinations(result,f);

		return result;
	}

	@SuppressWarnings("unchecked")
	private static Vector<Vector<Value>> getCombinations(Vector<Vector<Value>> combinations, Factor factor ){
		Vector<Vector<Value>> result = new Vector<Vector<Value>>();
		for (Vector<Value> combination : combinations)
			for(Value v : factor.getValues()){
				Vector<Value> copy = (Vector<Value>) combination.clone();
				copy.add(v);
				result.add(copy);
			}
		return result;
	}		
	
	
	public boolean equals(Object obj) {
		if (obj.getClass() == BlockType.class){
			BlockType blockType = (BlockType) obj;
			if (blockType.structure.equals(structure))
				return true;
			else
				return false;
		}
		else
			return false;
	}
}

