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
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTree;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import fr.inria.insitu.touchstone.design.motor.Block;
import fr.inria.insitu.touchstone.design.motor.BlockType;
import fr.inria.insitu.touchstone.design.motor.Blocking;
import fr.inria.insitu.touchstone.design.motor.Experiment;
import fr.inria.insitu.touchstone.design.motor.Factor;
import fr.inria.insitu.touchstone.design.motor.FactorSet;
import fr.inria.insitu.touchstone.design.motor.Value;


public class PanelBlocking extends StepPanel<Blocking> {

	private static final long serialVersionUID = 42L;
	
	private JPanel blockTypePanel = new JPanel();
	private PanelInfo informations = new PanelInfo();	
	private DefaultMutableTreeNode root = new DefaultMutableTreeNode("experiment"); 
	private JTree blockAsTree = new JTree(root);
	private Vector<JComboBox> expertMode = new Vector<JComboBox>();
	private JLabel blockTypeExpression = new JLabel("Selected Block Type : ");
	private Vector<JSpinner> blockReplications = new Vector<JSpinner>();

	private JLabel numberOfTrials;

	
	public PanelBlocking(DesignPlatform designPlatform, Experiment experiment, int depth) {
		super(designPlatform, experiment, depth);
		setLayout(new GridBagLayout());
		blockAsTree.setCellRenderer(new TreeRendererExperiment(root));
	}

	private Blocking getBlockingFromExpertModePanel(boolean structureChanged){
		if(structureChanged || experiment.getBlocking() == null) {
			FactorSet factorSet = experiment.getFactorSet();
			Vector<Factor> subjFactors = factorSet.getBetweenSubjectFactors();
			Vector<Vector<Factor>> blockedFactors_tmp = new Vector<Vector<Factor>>();
			Vector<Factor> freeFactors = new Vector<Factor>();
			for(int i =0; i< expertMode.size();i++){
				JComboBox cb = expertMode.get(i);
				if(cb.getSelectedIndex() == 0) {
					freeFactors.add(factorSet.getWithinSubjectFactors().get(i));
				} else {
					int index = cb.getSelectedIndex()-1;
					for(int cpt = blockedFactors_tmp.size(); cpt <= index; cpt++) {
						blockedFactors_tmp.add(new Vector<Factor>());
					}
					blockedFactors_tmp.get(index).add(factorSet.getWithinSubjectFactors().get(i));
				}
			}
			Vector<Vector<Factor>> blockedFactors = new Vector<Vector<Factor>>();
			for (Iterator<Vector<Factor>> iterator = blockedFactors_tmp.iterator(); iterator.hasNext();) {
				Vector<Factor> factors = iterator.next();
				if(factors.size() > 0)
					blockedFactors.add(factors);	
			}
			if(freeFactors.size() == 0) {
				Factor factice = new Factor();
				factice.addValue(new Value("", "", factice));
				freeFactors.add(factice);
			}
			
			BlockType selectedBlockType = new BlockType(subjFactors, freeFactors, blockedFactors);
			Block selectedBlock = selectedBlockType.generateBlock();
			experiment.setBlocking(new Blocking(selectedBlock, selectedBlockType));
		}

		Block structure = experiment.getBlocking().getSelectedBlockStructure();
		for (Iterator<Block> iterator = structure.iterator(); iterator.hasNext();) {
			Block selectedBlock = iterator.next();
			BlockType selectedBlockType = experiment.getBlocking().getSelectedBlockType();
			// 0 participants
			// for (int i = 1; i<= selectedBlockType.getNumberOfBlockLevel();i++) blocks
			// selectedBlockType.getNumberOfBlockLevel() + 1 trials
			selectedBlock.setBlockReplication(0,(Integer)blockReplications.get(0).getValue());
			for (int i = 1; i<= selectedBlockType.getNumberOfBlockLevel();i++) {
				if(i < (blockReplications.size() - 1))
					selectedBlock.setBlockReplication(i,(Integer)blockReplications.get(i).getValue());
			}
			selectedBlock.setBlockReplication(selectedBlockType.getNumberOfBlockLevel() + 1,(Integer)blockReplications.get(blockReplications.size()-1).getValue());
		}
		
		return experiment.getBlocking();
	}

	/**
	 * Used to display a block in a JTree.
	 * Create a node for each child of the given block and add them to the specified node. 
	 * @param node
	 * @param block
	 */
	private void addChild(DefaultMutableTreeNode node, Block block){
		for (Block b : block){
			DefaultMutableTreeNode n = new DefaultMutableTreeNode(b.toString());
			addChild(n,b);
			node.add(n);
		}
	}

	private void updateTree() {
		// save opened paths
		Enumeration<TreePath> expandedPaths = blockAsTree.getExpandedDescendants(new TreePath(root));
		
		Blocking blocking = experiment.getBlocking();
		root.removeAllChildren();
		Block subjectGroups = (Block)blocking.getSelectedBlockStructure().clone();
		for (int j = 0; j <= subjectGroups.getDepth(); j++) {
			subjectGroups.replicateBlockAtDepth(j);	
		}
		int i = 0;
		for (Block subject : subjectGroups){
			String subjLabel = "S"+i;
			if(subject.toString().length() > 0) subjLabel += " - "+subject.toString();
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(subjLabel);
			addChild(node, subject);
			root.add(node);
			i++;
		}
		((DefaultTreeModel)blockAsTree.getModel()).reload();
		
		// restore opened paths
		while(expandedPaths != null && expandedPaths.hasMoreElements()) {
			TreePath path = expandedPaths.nextElement();
			TreePath nPath = Utils.getTreePath(path, blockAsTree, true);
			if(nPath != null) blockAsTree.expandPath(nPath);
		}
	}
	
	/**
	 * Update this panel's display to take into account the new blocktype specified by the user.
	 */
	private void blockTypeChanged(){
		Blocking blocking = experiment.getBlocking();
		BlockType selectedBlockType = blocking.getSelectedBlockType();
		Block selectedBlock = experiment.getBlocking().getSelectedBlockStructure();
		
		ChangeListener replicationsListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				experiment.setBlocking(getBlockingFromExpertModePanel(false));
				numberOfTrials.setText(""+experiment.getBlocking().getSelectedBlockStructure().getNumberOfTrials());
				updateTree();
			}
		};
		
		//set the information panel display
		informations.removeAll();
		blockReplications.removeAllElements();
		informations.add(new JLabel("Number of trials: "));
		numberOfTrials = new JLabel(""+selectedBlock.getNumberOfTrials());
		informations.add(numberOfTrials);

		informations.add(new JLabel("Number of participants: "));
		int nbSubjects = experiment.getOrdering().getOrderedBlock().size();
		final SpinnerNumberModel spinnerSubjectReplicationsModel = new SpinnerNumberModel(nbSubjects, blocking.getSelectedBlockStructure().size(), Integer.MAX_VALUE, 1);
		JSpinner spinnerSubjectReplications = new JSpinner(spinnerSubjectReplicationsModel);
		blockReplications.add(spinnerSubjectReplications);
		spinnerSubjectReplicationsModel.addChangeListener(replicationsListener);
		informations.add(blockReplications.lastElement());

		for (int i = 1; i<= selectedBlockType.getNumberOfBlockLevel();i++){
			informations.add(new JLabel("Block"+(i)+" replications: "));
			SpinnerNumberModel spinnerBlockReplicationsModel = new SpinnerNumberModel(selectedBlock.firstElement().getBlockReplications(i), 1, Integer.MAX_VALUE, 1);
			JSpinner spinnerBlockReplications = new JSpinner(spinnerBlockReplicationsModel);
			blockReplications.add(spinnerBlockReplications);
			spinnerBlockReplications.addChangeListener(replicationsListener);
			informations.add(blockReplications.lastElement());
		}

		SpinnerNumberModel spinnerTrialReplicationsModel = new SpinnerNumberModel(selectedBlock.firstElement().getBlockReplications(selectedBlockType.getNumberOfBlockLevel()+1), 1, Integer.MAX_VALUE, 1);
		informations.add(new JLabel("Trial replications : "));
		JSpinner spinnerTrialReplications = new JSpinner(spinnerTrialReplicationsModel);
		blockReplications.add(spinnerTrialReplications);
		spinnerTrialReplications.addChangeListener(replicationsListener);
		informations.add(blockReplications.lastElement());

		//blockTypeExpression
		blockTypeExpression.setText("Selected Block Type: "+selectedBlockType.toString());

		Block block = blocking.getSelectedBlockStructure().firstElement();

		for (int i = 1; i<blockReplications.size();i++)
			blockReplications.get(i).setValue(block.getBlockReplications(i));
		
		updateTree();
		repaint();
		revalidate();
	}

	public void display() {
		removeAll();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.insets = new Insets(3,3,3,3);
		gbc.fill = GridBagConstraints.BOTH;

		FactorSet factorSet = experiment.getFactorSet();
		BlockType blockType = experiment.getBlocking().getSelectedBlockType();

		blockTypePanel.removeAll();
		expertMode.removeAllElements();
		blockTypePanel.setLayout(new FlowLayout());		

		ItemListener blockItemListener = new ItemListener (){
			public void itemStateChanged(ItemEvent e) {
				experiment.setBlocking(getBlockingFromExpertModePanel(true));
				blockTypeChanged();
			}
		};
		
		for (Factor f: factorSet.getWithinSubjectFactors()){
			blockTypePanel.add(new JLabel(f.toString()));
			JComboBox cb = new JComboBox();
			cb.addItem("Free");
			for (int i = 1; i<=factorSet.getNumberOfWithinSubjFactors();i++)
				cb.addItem("Block"+i);
			blockTypePanel.add(cb);
			expertMode.add(cb);
			int level = blockType.getBlockingLevel(f.getShortName());
			cb.setSelectedIndex(level-1);
			cb.addItemListener(blockItemListener);
		}

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		add(blockTypePanel,gbc);
		gbc.gridy++;
		blockTypeExpression.setHorizontalAlignment(SwingConstants.CENTER);
		add(blockTypeExpression,gbc);

		gbc.gridwidth = 1;
		gbc.weighty = 1;
		gbc.weightx = 0.5;
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.insets = new Insets(5,5,5,5);
		add(new JScrollPane(blockAsTree),gbc);
		blockAsTree.setRootVisible(true);

		gbc.gridx = 1;
		informations.setBackground(Color.WHITE);
		add(new JScrollPane(informations),gbc);
		
		
		blockTypeChanged();
		hiliteExperimentPreview();
	}



	public Blocking getStep(){
		return experiment.getBlocking();
	}


	public String getStatus() {
		return null;
	}

	/**
	 * 
	 * @author Matthis and Caroline
	 *
	 * class used to display the JTextFields used to set the blocks replications of the chosen block. 
	 */
	private class PanelInfo extends JPanel {

		private static final long serialVersionUID = 1L;
		
		GridBagConstraints gbc;
		public PanelInfo() {
			setLayout(new GridBagLayout());
			gbc = new GridBagConstraints();
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.insets = new Insets(3,3,3,3);
			gbc.fill = GridBagConstraints.HORIZONTAL;		
		}
		public Component add(Component comp) {
			add(comp,gbc);
			if (gbc.gridx==1){
				gbc.gridx=0;
				gbc.gridy++;
			}
			else
				gbc.gridx++;			
			return comp;			
		}	
	}

	public void save() {
		experiment.updateOrdering();
	}		
	
	public void updateExperimentPreview() {
	}
	
	public void hiliteExperimentPreview() {
		getDesignPlatform().getExperimentPreview().hiliteExperimentTree();
	}
	
}
