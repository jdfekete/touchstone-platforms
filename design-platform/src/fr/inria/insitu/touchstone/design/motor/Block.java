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
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

public class Block extends Vector<Block> implements Serializable {

	private static final long serialVersionUID = 42L;
	
	protected int replications=1;
	protected Vector<Value> values = new Vector<Value>();
	protected Block parent =null;
	
	private static int cpt =0; 
	protected int ID;
	
	protected OrderingMode orderingMode = new Random();
	protected boolean serial = false;
	
	//index 0 : Subject
	//index 1 : Block1
	//lastElement  : FreeFactors
	public Block(Vector<Value> values ,Vector<Vector<Factor>> factors){
		ID=cpt;
		cpt++;
		this.values = values;
		if (factors.size()>0){
			//pour chaque combinaison de valeurs de facteur, creer un block
			Vector<Vector<Value>> combinations = new Vector<Vector<Value>>();
			Vector<Vector<Factor>> reste = new Vector<Vector<Factor>>();
			if (factors.size()>1)
				reste.addAll(new Vector<Vector<Factor>>(factors.subList(1, factors.size())));
			combinations = BlockType.getAllCombinations(factors.get(0));

			for (Vector<Value> combination : combinations)
				if (combination.size()>0)
					add(new Block(combination,reste));	
		}
	}

	public Block(){
		values = new Vector<Value>();
		ID=cpt;
		cpt++;	
	}	

	public Block(Vector<Block> children) {
		this();
		addAll(children);
	}

	public Vector<Value> getValues() {
		return values;
	}

	public void setValues(Vector<Value> values) {
		this.values = values;
	}

	
	public synchronized boolean add(Block e) {
		e.setParent(this);
		return super.add(e);
	}
	
	public void add(int index, Block element) {
		element.setParent(this);
		super.add(index, element);
	}

	
	public synchronized boolean addAll(Collection<? extends Block> c) {
		for (Block b : c)
			b.setParent(this);
		return super.addAll(c);
	}
	
	public synchronized boolean addAll(int index, Collection<? extends Block> c) {
		for (Block b : c)
			b.setParent(this);
		return super.addAll(index, c);
	}
	
	public synchronized void addElement(Block obj) {
		obj.setParent(this);
		super.addElement(obj);
	}



	public int getReplications() {
		return replications;
	}

	public void setReplications(int replications) {
		this.replications = replications;
	}

	public int getDepth(){
		if (size()==0)
			return 0;
		else return 1+get(0).getDepth();
	}

	
	public String toString() {
		String result = "";
		for (Value v : values)
			result += v +" ";
		return result;
	}

	public void setParent(Block parent) {
		this.parent = parent;
	}

	public Block getParent() {
		return parent;
	}

	/**
	 * All the blocks at the specified depth will be replicated the specified number of time.
	 * @param depth the specified depth (0 = this)
	 * @param replication 
	 */
	public void setBlockReplication(int depth, int replication){
		if (depth==0)
			replications = replication;
		else
			for(Block block : this)
				block.setBlockReplication(depth-1, replication);
	}
	
	public int getBlockReplications(int depth) {
		if(depth == 0) return replications;
		else
			return get(0).getBlockReplications(depth-1);
	}

	/**
	 * 
	 * @return the number of trials (leaves) of this block (tree)
	 */
	public int getNumberOfTrials(){
		if (size()==0)
			return replications;
		else {
			int result =0;
			for (Block b : this)
				result += b.getNumberOfTrials();
			return result*replications;
		}			
	}

	/**
	 * @param depth the specified depth
	 * @return the number of interblock at the specified depth 
	 */
	public int getNumberOfInterBlock(int depth){
		if (depth==1)
			return size()*get(0).getReplications()-1;
		else 
			return get(0).getNumberOfInterBlock(depth-1)*size();		
	}

	@SuppressWarnings("unchecked")
	public synchronized Object clone() {
		Vector<Block> bfs = this.BFS();
		Vector<Block> bfsCopy = new Vector<Block>();
		for(Block block : bfs){
			Block copy = new Block(new Vector<Block>());
			copy.replications = block.replications;
			copy.values = (Vector<Value>)block.values.clone();
			copy.orderingMode = block.orderingMode;
			copy.serial = block.serial;
			bfsCopy.add(copy);
		}

		for (int i = 0; i< bfs.size(); i++){
			Block block = bfs.get(i);
			if (block.getParent()!=null){
				int indexOfParent = bfs.indexOf(block.getParent());
				if (indexOfParent!=-1)
					bfsCopy.get(indexOfParent).add(bfsCopy.get(i));
			}
		}
		return bfsCopy.get(0);
	}

	
	/**
	 * 
	 * @return a vector containing all the blocks of this block in the BFS order
	 */
	public Vector<Block> BFS(){
		Vector<Block> parcourus = new Vector<Block>();
		LinkedList<Block> pile = new LinkedList<Block>();
		Vector<Block> result = new Vector<Block>();

		pile.add(this);
		parcourus.add(this);

		while(!pile.isEmpty()){
			Block current = pile.removeFirst();
			result.add(current);
			for (int i = 0; i<current.size();i++){
				Block child = current.get(i);
				if (!parcourus.contains(child)){
					parcourus.add(child);
					pile.add(child);
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * @return the leaves of this block
	 */
	public Vector<Block> getLeaves(){
		Vector<Block> leaves = new Vector<Block>();
		LinkedList<Block> pile = new LinkedList<Block>();
		pile.add(this);
		while(!pile.isEmpty()){
			Block block = pile.removeFirst();
			if (block.size()==0)
				leaves.add(block);
			for (Block b : block)
				pile.add(b);
		}
		return leaves;
	}

	/**
	 * 
	 * @param depth 0 is for Subject, 1 for Block1 and so on.
	 * @return a vector containing the blocks which have the specified depth
	 */
	public Vector<Block> getBlocksAtDepth(int depth){
		Vector<Block> result = new Vector<Block>();
		if (depth == 0)
			return this;
		else
			for(Block block : this)
				result.addAll(block.getBlocksAtDepth(depth-1));
		return result;
	}

	/**
	 * replicate all the blocks of this block according to it's associated replication parameter
	 */
	public void replicateSubBlocks(){
		Vector<Block> bfs = this.BFS();
		for (int i =  bfs.size()-1 ; i>=0;i--){
			Block block = bfs.get(i);
			if (block.getParent() != null){
				int numberOfReplication = block.getReplications();
				block.replications = 1;
				for (int j = 1 ; j< numberOfReplication ; j++){
					block.getParent().add(block.getParent().indexOf(block),(Block)block.clone());
				}
			}
		}
	}	

	/**
	 * replicate all the blocks at the specified depth of this block according to it's associated replication parameter
	 * @param depth the specified depth
	 */
	public void replicateBlockAtDepth(int depth){
		if(depth == 0) {
			replicateSubjects(); return;
		}
		Vector<Block> v = getBlocksAtDepth(depth);
		for (int i = 0; i<v.size();i++) {
			v.get(i).replicate();
		}
	}

	/**
	 * clone this block <i>replications</i> times and add them to its parent.
	 * if this block's parent is null, then nothing is done.
	 */
	public void replicate(){
		if (parent != null){
			if (replications>1){
				int numberOfReplication = replications;
//				replications = 1;
				for (int i = 1 ; i<numberOfReplication ;i++) {
					this.getParent().add(this.getParent().indexOf(this),(Block)this.clone());
				}
			}
		}
	}
	
	public void replicateSubjects() {
		Vector<Block> v = getBlocksAtDepth(0);
		if(v.size() == 0) return;
		Block parent = v.firstElement().parent;
		int replications = v.firstElement().replications;
		if (parent != null){
			if (replications>1){
				int numberOfReplication = replications;
//				v.firstElement().replications = 1;
				int nbChildren = parent.size();
				int nbFullReplications = numberOfReplication / nbChildren;
				int nbReplicationsLeft = numberOfReplication % nbChildren;
				int j = 0;

				Vector<Block> tmp = new Vector<Block>();
				tmp.addAll(v);
				
				for(Block b : tmp) {
					for (int i = 1 ; i<nbFullReplications ;i++) {
						parent.add(parent.indexOf(b),(Block)b.clone());
					}
					if (j < nbReplicationsLeft)
						parent.add(parent.indexOf(b),(Block)b.clone());
					j++;
					
				}
			}
		}
	}

	/**
	 * 
	 * @return a vector containing each line structured as required by the step 5
	 */
	public Vector<String> toDetailedString(){
		Vector<Block> leaves = getLeaves();
		Couple<Vector<String>, Vector<String>> lines = new Couple<Vector<String>, Vector<String>>(new Vector<String>(),new Vector<String>()); 

		for (Block block : leaves){
			lines.getFirst().add("");
			lines.getSecond().add("");
			while (block.getParent()!=null){
				String valuesAsString = "";
				for (Value v : block.values )
					if(v.toString().length() > 0) valuesAsString += "\""+v+"\",";

				Block parent =block.getParent();

				String currentIndex = lines.getFirst().lastElement();
				currentIndex = (parent.indexOf(block)+1)+","+currentIndex;
				lines.getFirst().remove(lines.getFirst().size()-1);
				lines.getFirst().add(currentIndex);

				String currentValues = lines.getSecond().lastElement();
				currentValues = valuesAsString+currentValues;
				lines.getSecond().remove(lines.getSecond().size()-1);
				lines.getSecond().add(currentValues);

				block = parent;
			}
			lines.getSecond().set(lines.getSecond().size()-1, lines.getSecond().lastElement().substring(0,lines.getSecond().lastElement().length()-1));
		}
		Vector<String> result = new Vector<String>();
		for (int i = 0; i<lines.getFirst().size();i++){			
			result.add(lines.getFirst().get(i).toString()
					+ lines.getSecond().get(i).toString()+"\n");			
		}	

		return result;
	}


	
	public synchronized boolean equals(Object o) {
		if ((o!=null)&&(o instanceof Block)){
			Block block = (Block) o;
			return (ID == block.ID);
		}
		else
			return false;
	}

	public OrderingMode getOrderingMode() {
		return orderingMode;
	}

	public void setOrderingMode(OrderingMode orderingMode) {
		this.orderingMode = orderingMode;
	}

	public boolean isSerial() {
		return serial;
	}
	
	public boolean isReplication() {
		if(parent == null) return false;
		for (Iterator<Block> iterator = parent.iterator(); iterator.hasNext();) {
			Block block = iterator.next();
			if(block.equals(this)) return false;
			Vector<Value> v = block.getValues();
			if(v.size() != getValues().size()) continue;
			boolean eq = true;
			Iterator<Value> itValues = getValues().iterator();
			for (Iterator<Value> iterator2 = v.iterator(); iterator2.hasNext();) {
				Value value1 = iterator2.next();
				Value value2 = itValues.next();
				eq = eq && value1.equals(value2);
			}
			if(eq) return true;
		}
		return false;
	}

	public void setSerial(boolean serialized) {
		this.serial = serialized;
	}

//	 String toXMLAux(String blockClass, String criterionTrial){
//		String result = "";		
//		if (size()==0){//it's a trial
//				result+="\t<trial values =\" ";
//				for (Value value : values)
//					result+=value.getFactor().getShortName()+"="+value.getShortValue()+",";
//				result = result.substring(0, result.length()-1);
//				result += "\" />\n";
//		}
//		else{ //it's a random block
//			result+="<block values =\"";
//			String valuesString = " ";
//			for (Value value : values)
//				valuesString+=value.getFactor().getShortName()+"="+value.getShortValue()+",";
//			result+= valuesString.substring(0, valuesString.length()-1)+"\"";
//			result+= " class = \""+blockClass+"\"";
//			if (getDepth()==1)
//				result+= " criterionTrial = \""+criterionTrial+"\"";
//			result+= ">\n";
//			for (int i = 0; i < size() ; i++)
//				result+= this.get(i).toXMLAux(blockClass, criterionTrial);
//			result+="</block>\n";
//		}	
//		return result;
//	}
	
	/** 
	 * @param intertitles
	 * @param criteria
	 * @param blockClass
	 * @return an XML representation of this block
	 */
//	public String toXML(Vector<Intertitle> intertitles, Vector<String> criteria, String blockClass){
//		String result = "";
//		if (parent == null){ // it's the root of the block structure
//			for (int subjectID = 0; subjectID < size() ; subjectID++){
//				result+="<run id=\"S"+subjectID+"\">\n";
//				result+="<setup class=\""+"\">\n";
//				result+="<intertrial class=\""+intertitles.firstElement()+"\" criterion =\""+criteria.firstElement()+"\">\n";
//				for (int i = 1; i< criteria.size()-1;i++)
//					result+="<interBlock class=\""+intertitles.get(i)+"\" criterion =\""+criteria.get(i)+"\">\n";
//	
//				result+=get(subjectID).toXMLAux(blockClass, criteria.lastElement());
//				
//				for (int i = 1; i< criteria.size()-1;i++)
//					result+="</interBlock>\n";
//				result+="</intertrial>\n";
//				result+="</setup>\n";
//				result+="</run>\n";
//			}			
//		}
//		return result;
//	}
}