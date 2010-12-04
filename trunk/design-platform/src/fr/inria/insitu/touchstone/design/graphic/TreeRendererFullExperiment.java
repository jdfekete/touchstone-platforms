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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JTree;
//import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import fr.inria.insitu.touchstone.design.motor.Block;
import fr.inria.insitu.touchstone.design.motor.Experiment;
import fr.inria.insitu.touchstone.design.motor.FactorSet;
import fr.inria.insitu.touchstone.design.motor.Intertitle;
import fr.inria.insitu.touchstone.design.motor.Ordering;
import fr.inria.insitu.touchstone.design.motor.Practice;
import fr.inria.insitu.touchstone.design.motor.PracticeBlock;
import fr.inria.insitu.touchstone.design.motor.Timing;


public class TreeRendererFullExperiment extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;

	private Vector<TreeNodeExperiment> experimentNodes;
	private Vector<TreeNodeExperiment> subjectNodes;
	private Vector<TreeNodeExperiment> setupNodes;
	private Vector<TreeNodeExperiment> blockNodes;
	private Vector<Vector<TreeNodeExperiment>> interblockNodes;
	private Vector<TreeNodeExperiment> intertrialNodes;
	private Vector<TreeNodeExperiment> trialNodes;
	private Vector<Vector<TreeNodeExperiment>> practiceBlockNodes;
	private Vector<Vector<TreeNodeExperiment>> practiceInterblockNodes;
	
	private boolean intertitles;
	
	private JTree tree;
	private TreeNodeExperiment root;
	
	public static Color SETUP_COLOR = new Color(133, 127, 123);
	public static Color PRACTICE_COLOR = new Color(64, 129, 157);
	public static Color TRIAL_COLOR = new Color(137, 113, 172);
	public static Color BLOCK_COLOR = new Color(60, 83, 156);

	private static Font SECONDARY_FONT = new Font("Times", Font.ITALIC, 13);
	private static Font PRIMARY_FONT = new Font("Arial", Font.BOLD, 14);
	
	private Vector<Integer> disabledInterblocks = new Vector<Integer>();
	private Vector<Integer> disabledPracticeInterblocks = new Vector<Integer>(); 
	private boolean disabledIntertrial = false;
	
	public TreeRendererFullExperiment(
			boolean intertitles,
			Vector<TreeNodeExperiment> experimentNodes, 
			Vector<TreeNodeExperiment> subjectNodes, 
			Vector<TreeNodeExperiment> setupNodes,
			Vector<TreeNodeExperiment> blockNodes, 
			Vector<Vector<TreeNodeExperiment>> interblockNodes, 
			Vector<TreeNodeExperiment> intertrialNodes,
			Vector<TreeNodeExperiment> trialNodes,
			Vector<Vector<TreeNodeExperiment>> practiceBlockNodes,
			Vector<Vector<TreeNodeExperiment>> practiceInterblockNodes) {
		super();
		this.intertitles = intertitles;
		this.blockNodes = blockNodes;
		this.experimentNodes = experimentNodes;
		this.interblockNodes = interblockNodes;
		this.intertrialNodes = intertrialNodes;
		this.setupNodes = setupNodes;
		this.subjectNodes = subjectNodes;
		this.trialNodes = trialNodes;
		this.practiceBlockNodes = practiceBlockNodes;
		this.practiceInterblockNodes = practiceInterblockNodes;
	}

	public JTree buildTree() {
		root = new TreeNodeExperiment("experiment", "experiment", TreeNodeExperiment.EXPERIMENT_TYPE);
		tree = new JTree(root);
		tree.setCellRenderer(this);
		return tree;
	}
	
	public void initTree(Intertitle blockClass, FactorSet factorSet, Ordering ordering, Timing t, Practice practice) {
		experimentNodes.clear();
		subjectNodes.clear();
		setupNodes.clear();
		blockNodes.clear();
		interblockNodes.clear();
		trialNodes.clear();
		intertrialNodes.clear();
		practiceBlockNodes.clear();
		practiceInterblockNodes.clear();

		root.removeAllChildren();
		experimentNodes.add(root);
		for (int subjectID = 0; subjectID < ordering.getOrderedBlock().size() ; subjectID++){
			String subjLabel = "S"+subjectID;
			Block subject = ordering.getOrderedBlock().get(subjectID);
			if(subject.toString().length() > 0) subjLabel += " - "+subject.toString();
			TreeNodeExperiment nodeSubject = new TreeNodeExperiment(subjLabel, subject, TreeNodeExperiment.PARTICIPANT_TYPE);
			subjectNodes.add(nodeSubject);
			root.add(nodeSubject);
			TreeNodeExperiment nodeSetup = new TreeNodeExperiment("set up ("+t.getSetupClass()+", "+t.getSetupCriterion()+")", "experiment", TreeNodeExperiment.SETUP_TYPE);
			setupNodes.add(nodeSetup);
			nodeSubject.add(nodeSetup);
			if(intertitles) {
				if(practiceInterblockNodes.size() == 0)
					practiceInterblockNodes.add(new Vector<TreeNodeExperiment>());
			}
			if(practiceBlockNodes.size() == 0)
				practiceBlockNodes.add(new Vector<TreeNodeExperiment>());
			if(practice != null && practice.getPracticesEnabled().get(0)) {
				Vector<PracticeBlock> practiceBlocks = practice.getPracticeBlocksFor(ordering.getOrderedBlock().get(subjectID), factorSet.getFactors());
				for(int i = 0; i < practiceBlocks.size(); i++) {
					if(intertitles) {
						TreeNodeExperiment nodeInterblockPractice = new TreeNodeExperiment("interpractice ("+t.getPracticeIntertitles().get(0)+", "+t.getPracticeCriteria().get(0)+")", practiceBlocks.get(i), TreeNodeExperiment.INTERPRACTICE_TYPE);
						practiceInterblockNodes.get(0).add(nodeInterblockPractice);
						if(!disabledPracticeInterblocks.contains(0)) // ++
							nodeSubject.add(nodeInterblockPractice);
					}
//					TreeNodeExperiment nodeBlockPractice = new TreeNodeExperiment(practice.getPractices().get(0).get(i)+"("+practice.getBlockClasses().get(0)+", _)");
					TreeNodeExperiment nodeBlockPractice = new TreeNodeExperiment(practice.getPractices().get(0).get(i)+"("+blockClass+", _)", practiceBlocks.get(i), TreeNodeExperiment.PRACTICE_TYPE);
					practiceBlockNodes.get(0).add(nodeBlockPractice);
					nodeSubject.add(nodeBlockPractice);
					for(int j = 0; j < practiceBlocks.get(i).size(); j++) {
						if(intertitles) {
							TreeNodeExperiment intertrial = new TreeNodeExperiment("intertrial ("+t.getIntertrialClass().simpleToString()+", "+t.getIntertrialCriterion()+")", practiceBlocks.get(i), TreeNodeExperiment.INTERTRIAL_TYPE);
							if(!disabledIntertrial) // ++
								nodeBlockPractice.add(intertrial);
							intertrialNodes.add(intertrial);
						}
						TreeNodeExperiment trial = new TreeNodeExperiment(practiceBlocks.get(i).get(j).toString() +" (_, "+t.getCriterionTrial()+")", practiceBlocks.get(i), TreeNodeExperiment.TRIAL_TYPE);
						nodeBlockPractice.add(trial);
						trialNodes.add(trial);
					}
				}
				
			}
			for(int i = 0; i < ordering.getOrderedBlock().get(subjectID).size(); i++) {
				blockToTree(blockClass, factorSet, t, practice, nodeSubject, ordering.getOrderedBlock().get(subjectID).get(i));
			}
		}
		((DefaultTreeModel)tree.getModel()).reload();
	}
	
	void blockToTree(Intertitle blockClass, FactorSet factorSet, Timing t, Practice practice, TreeNodeExperiment node, Block block) {
		Timing timing = t;
		if (block.size()==0) { // it's a trial
			if(intertitles) {
				TreeNodeExperiment intertrial = new TreeNodeExperiment("intertrial ("+timing.getIntertrialClass().simpleToString()+", "+timing.getIntertrialCriterion()+")", block, TreeNodeExperiment.INTERTRIAL_TYPE);
				if(!disabledIntertrial) // ++
					node.add(intertrial);
				intertrialNodes.add(intertrial);
			}
			TreeNodeExperiment trial = new TreeNodeExperiment(block.toString() +" (_, "+timing.getCriterionTrial()+")", block, TreeNodeExperiment.TRIAL_TYPE);
			trialNodes.add(trial);
			node.add(trial);
		} else {
			int index = practice.getPractices().size() - block.getDepth();
			if(intertitles) {
				if(index >= practiceInterblockNodes.size())
					for(int i = practiceInterblockNodes.size(); i <= index; i++)
						practiceInterblockNodes.add(new Vector<TreeNodeExperiment>());
			}
			if(index >= practiceBlockNodes.size())
				for(int i = practiceBlockNodes.size(); i <= index; i++)
					practiceBlockNodes.add(new Vector<TreeNodeExperiment>());
			
			if(practice != null && practice.getPracticesEnabled().get(index)
					&& !(block.isReplication() && !practice.getReplicationsEnabled().get(index))) {
				Vector<PracticeBlock> practiceBlocks = practice.getPracticeBlocksFor(block, factorSet.getFactors());
				
				for(int i = 0; i < practiceBlocks.size(); i++) {
					if(intertitles) {
						TreeNodeExperiment nodeInterblockPractice = new TreeNodeExperiment("interpractice"+index+" ("+t.getPracticeIntertitles().get(index).simpleToString()+", "+t.getPracticeCriteria().get(index)+")", block, TreeNodeExperiment.INTERPRACTICE_TYPE);
						practiceInterblockNodes.get(index).add(nodeInterblockPractice);
						if(!disabledPracticeInterblocks.contains(index)) // ++
							node.add(nodeInterblockPractice);
					}
//					TreeNodeExperiment nodeBlockPractice = new TreeNodeExperiment(practiceBlocks.get(i)+"("+practice.getBlockClasses().get(index)+", _)");
					TreeNodeExperiment nodeBlockPractice = new TreeNodeExperiment(practiceBlocks.get(i)+"("+blockClass.simpleToString()+", _)", practiceBlocks.get(i), TreeNodeExperiment.PRACTICE_TYPE);
					practiceBlockNodes.get(index).add(nodeBlockPractice);
					node.add(nodeBlockPractice);
					for(int j = 0; j < practiceBlocks.get(i).size(); j++) {
						if(intertitles) {
							TreeNodeExperiment intertrial = new TreeNodeExperiment("intertrial ("+t.getIntertrialClass().simpleToString()+", "+t.getIntertrialCriterion()+")", practiceBlocks.get(i).get(j), TreeNodeExperiment.INTERTRIAL_TYPE);
							nodeBlockPractice.add(intertrial);
							if(!disabledIntertrial) // ++
								intertrialNodes.add(intertrial);
						}
						TreeNodeExperiment trial = new TreeNodeExperiment(practiceBlocks.get(i).get(j).toString() +" (_, "+t.getCriterionTrial()+")", practiceBlocks.get(i).get(j), TreeNodeExperiment.TRIAL_TYPE);
						nodeBlockPractice.add(trial);
						trialNodes.add(trial);
					}
				}
				
			}
			
			if(intertitles) {
				int totalDepth = timing.getIntertitles().size() - Timing.INDEX_BEGIN_BLOCKS;
				if(block.getDepth() == 1) {
					Block b = block;
					while(b.getParent() != null && b.getParent().getDepth() <= totalDepth && b.getParent().indexOf(b) == 0)
						b = b.getParent();
					index = timing.getIntertitles().size() - b.getDepth();
					int interblockNumber = totalDepth - b.getDepth();
					TreeNodeExperiment interblock = new TreeNodeExperiment("interblock"+(interblockNumber+1)+" ("+
							timing.getIntertitles().get(index).simpleToString()+", "+
							timing.getCriteria().get(index)+")", b, TreeNodeExperiment.INTERBLOCK_TYPE);
					Vector<TreeNodeExperiment> interblocksAtDepth = null;
					if(interblockNumber >= interblockNodes.size()) {
						for(int i = interblockNodes.size(); i <= (interblockNumber+1); i++) {
							interblockNodes.add(new Vector<TreeNodeExperiment>());
						}
					}
					interblocksAtDepth = interblockNodes.get(interblockNumber);
					if(!disabledInterblocks.contains(interblockNumber)) // ++
						interblocksAtDepth.add(interblock);
					node.add(interblock);
				}
			}
			

			TreeNodeExperiment bl = new TreeNodeExperiment(block.toString() + "("+blockClass.simpleToString()+", _)", block, TreeNodeExperiment.BLOCK_TYPE);
			blockNodes.add(bl);
			node.add(bl);
			for(int i = 0; i < block.size(); i++) 
				blockToTree(blockClass, factorSet, t, practice, bl, block.get(i));
		}
	}
	
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		if (experimentNodes.contains(value)) {
			setIcon(Utils.getIconExpe());
			c.setFont(PRIMARY_FONT);
		} else if(subjectNodes.contains(value)) {
			setIcon(Utils.getIconSubject());
			c.setFont(PRIMARY_FONT);
		} else if(setupNodes.contains(value)) {
			c.setForeground(SETUP_COLOR);
			setIcon(Utils.getIconSetup());
			c.setFont(PRIMARY_FONT);
		} else if(blockNodes.contains(value)) {
			setIcon(Utils.getIconBlock());
			c.setForeground(BLOCK_COLOR);
			c.setFont(PRIMARY_FONT);
		} else if(intertrialNodes.contains(value)) {
			setIcon(Utils.getIconInterTrial());
			c.setForeground(TRIAL_COLOR);
			c.setFont(SECONDARY_FONT);
		} else {
			boolean isInterblock = false;
			for (Iterator<Vector<TreeNodeExperiment>> iterator = interblockNodes.iterator(); iterator.hasNext();) {
				Vector<TreeNodeExperiment> next = iterator.next();
				if(next.contains(value)) {
					isInterblock = true; break;
				}

			} if(isInterblock) {
				setIcon(Utils.getIconInterBlock());
				c.setForeground(BLOCK_COLOR);
				c.setFont(SECONDARY_FONT);
			} else {
				boolean isPracticeInterblock = false;
				boolean first = true;
				for (Iterator<Vector<TreeNodeExperiment>> iterator = practiceInterblockNodes.iterator(); iterator.hasNext();) {
					Vector<TreeNodeExperiment> next = iterator.next();
					if(next.contains(value)) {
						isPracticeInterblock = true; break;
					}
					first = false;
				} 
				if(isPracticeInterblock) {
					if(!first) {
						setIcon(Utils.getIconInterblockPractice());
						c.setForeground(PRACTICE_COLOR);
					} else {
						setIcon(Utils.getIconInterblockPracticeExp());
						c.setForeground(SETUP_COLOR);
					}
					c.setFont(SECONDARY_FONT);
				} else {
					boolean isPracticeBlock = false;
					first = true;
					for (Iterator<Vector<TreeNodeExperiment>> iterator = practiceBlockNodes.iterator(); iterator.hasNext();) {
						Vector<TreeNodeExperiment> next = iterator.next();
						if(next.contains(value)) {
							isPracticeBlock = true; break;
						}
						first = false;
					} 
					if(isPracticeBlock) {
						if(!first) {
							setIcon(Utils.getIconPractice());
							c.setForeground(PRACTICE_COLOR);
						} else {
							setIcon(Utils.getIconPracticeExp());
							c.setForeground(SETUP_COLOR);
						}
						c.setFont(PRIMARY_FONT);
					} else {
						setIcon(Utils.getIconTrial());
						c.setForeground(TRIAL_COLOR);
						c.setFont(PRIMARY_FONT);
					}
				}
			}
		}
		return c;
	}
	
	void updateSetup(Timing t) {
		for (Iterator<TreeNodeExperiment> iterator = setupNodes.iterator(); iterator.hasNext();) {
			TreeNodeExperiment next = iterator.next();
			next.setUserObject("set up ("+t.getSetupClass()+", "+t.getSetupCriterion()+")");
		}
		((DefaultTreeModel)tree.getModel()).reload();
	}
	
	void updateTrial(Timing t) {
		for (Iterator<TreeNodeExperiment> iterator = trialNodes.iterator(); iterator.hasNext();) {
			TreeNodeExperiment next = iterator.next();
			String trialValues = (String)next.getUserObject();
			trialValues = trialValues.substring(0, trialValues.indexOf('(')-1);
			next.setUserObject(trialValues +" ("+t.getCriterionTrial()+")");
		}
		((DefaultTreeModel)tree.getModel()).reload();
	}
	
	void updateIntertrial(Timing t, boolean disabled, Experiment experiment) {
		for (Iterator<TreeNodeExperiment> iterator = intertrialNodes.iterator(); iterator.hasNext();) {
			TreeNodeExperiment next = iterator.next();
			next.setUserObject("intertrial ("+t.getIntertrialClass().simpleToString()+", "+t.getIntertrialCriterion()+")");
			if(this.disabledIntertrial != disabled && disabled)
				((TreeNodeExperiment)next.getParent()).remove(next);
		}
		
		if(this.disabledIntertrial != disabled) {
			this.disabledIntertrial = disabled;
			if(!this.disabledIntertrial)
				initTree(experiment.getBlockClass(), experiment.getFactorSet(), experiment.getOrdering(), experiment.getTiming(), experiment.getPractice());
		}
		((DefaultTreeModel)tree.getModel()).reload();
	}
	
	void updateInterblock(Timing t, int interblockNumber, int indexIntertitle, int indexCriteria, boolean practice, boolean disabled, Experiment experiment) {
		if(interblockNumber-1 >= interblockNodes.size()) return;
		Vector<Vector<TreeNodeExperiment>> nodes = practice ? practiceInterblockNodes : interblockNodes;
		int index = practice ? interblockNumber : interblockNumber-1;
		if(index >= nodes.size() || nodes.get(index) == null) return;
		for (Iterator<TreeNodeExperiment> iterator = nodes.get(index).iterator(); iterator.hasNext();) {
			TreeNodeExperiment next = iterator.next();
			if(practice) {
				next.setUserObject("interpractice"+interblockNumber+" ("+
						t.getPracticeIntertitles().get(indexIntertitle).simpleToString()+", "+
						t.getPracticeCriteria().get(indexCriteria)+")");
				if(disabled && ((TreeNodeExperiment)next.getParent()) != null) {
					((TreeNodeExperiment)next.getParent()).remove(next);
				}
			} else {
				next.setUserObject("interblock"+interblockNumber+" ("+
						t.getIntertitles().get(indexIntertitle).simpleToString()+", "+
						t.getCriteria().get(indexCriteria)+")");
				if(disabled && ((TreeNodeExperiment)next.getParent()) != null) {
					((TreeNodeExperiment)next.getParent()).remove(next);
				}
			}
		}
		
		if(practice) {
			if(this.disabledPracticeInterblocks.contains(interblockNumber) != disabled) {
				if(this.disabledPracticeInterblocks.contains(interblockNumber))
					this.disabledPracticeInterblocks.remove(new Integer(interblockNumber));
				else
					this.disabledPracticeInterblocks.add(new Integer(interblockNumber));
				if(!this.disabledPracticeInterblocks.contains(interblockNumber))
					initTree(experiment.getBlockClass(), experiment.getFactorSet(), experiment.getOrdering(), experiment.getTiming(), experiment.getPractice());
			}
		} else {
			if(this.disabledInterblocks.contains(interblockNumber) != disabled) {
				if(this.disabledInterblocks.contains(interblockNumber))
					this.disabledInterblocks.remove(new Integer(interblockNumber));
				else
					this.disabledInterblocks.add(new Integer(interblockNumber));
				if(!this.disabledInterblocks.contains(interblockNumber))
					initTree(experiment.getBlockClass(), experiment.getFactorSet(), experiment.getOrdering(), experiment.getTiming(), experiment.getPractice());
			}
		}

		((DefaultTreeModel)tree.getModel()).reload();
	}
	
	public JTree getTree() {
		return tree;
	}

	public TreeNodeExperiment getRoot() {
		return root;
	}
	
}
