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

import fr.inria.insitu.touchstone.design.graphic.widgets.AddYourOwn;
import fr.inria.insitu.touchstone.design.graphic.widgets.EditableItem;
import fr.inria.insitu.touchstone.design.graphic.widgets.EditableMenu;
import fr.inria.insitu.touchstone.design.graphic.widgets.Function;
import fr.inria.insitu.touchstone.design.graphic.widgets.MinusButton;
import fr.inria.insitu.touchstone.design.graphic.widgets.PlusButton;
import fr.inria.insitu.touchstone.design.motor.Block;
import fr.inria.insitu.touchstone.design.motor.BlockType;
import fr.inria.insitu.touchstone.design.motor.Blocking;
import fr.inria.insitu.touchstone.design.motor.Experiment;
import fr.inria.insitu.touchstone.design.motor.Factor;
import fr.inria.insitu.touchstone.design.motor.Intertitle;
import fr.inria.insitu.touchstone.design.motor.Plugin;
import fr.inria.insitu.touchstone.design.motor.Practice;
import fr.inria.insitu.touchstone.design.motor.PracticeBlock;
import fr.inria.insitu.touchstone.design.motor.Value;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.TreePath;


public class PanelPractice extends StepPanel<Practice> {

	private static final long serialVersionUID = 42L;
	
	private Vector<PracticeLine> lines = new Vector<PracticeLine>();
	private JPanel practicesPanel;
	
	private TreeRendererFullExperiment treeRenderer;

	private Vector<TreeNodeExperiment> experimentNodes = new Vector<TreeNodeExperiment>();
	private Vector<TreeNodeExperiment> subjectNodes = new Vector<TreeNodeExperiment>();
	private Vector<TreeNodeExperiment> setupNodes = new Vector<TreeNodeExperiment>();
	private Vector<TreeNodeExperiment> blockNodes = new Vector<TreeNodeExperiment>();
	private Vector<Vector<TreeNodeExperiment>> interblockNodes = new Vector<Vector<TreeNodeExperiment>>();
	private Vector<Vector<TreeNodeExperiment>> practiceBlockNodes = new Vector<Vector<TreeNodeExperiment>>();
	private Vector<Vector<TreeNodeExperiment>> practiceInterblockNodes = new Vector<Vector<TreeNodeExperiment>>();
	private Vector<TreeNodeExperiment> trialNodes = new Vector<TreeNodeExperiment>();
	private Vector<TreeNodeExperiment> intertrialNodes = new Vector<TreeNodeExperiment>();

	private JLabel blockTypeExpression = new JLabel("Selected Block Type : ");
	
	private PracticeListener practiceListener = new PracticeListener();
	
	class PracticeListener implements ChangeListener, ItemListener, ActionListener, DocumentListener {

		private boolean on = true;
		
		public PracticeListener() { }
		
		public void disable() {
			on = false;	
		}
		
		public void enable() {
			on = true;	
		}
		
		public void itemStateChanged(ItemEvent e) {
			if(!on) return;
			updateTree();
		}

		public void actionPerformed(ActionEvent e) {
			if(!on) return;
			updateTree();
		}

		public void changedUpdate(DocumentEvent e) {
			if(!on) return;
			updateTree();
		}

		public void insertUpdate(DocumentEvent e) {
			if(!on) return;
			updateTree();
		}

		public void removeUpdate(DocumentEvent e) {
			if(!on) return;
			updateTree();
		}

		public void stateChanged(ChangeEvent e) {
			if(!on) return;
			updateTree();
		}
		
	}
	
	class PracticeLine { //extends JPanel {
		
		private static final long serialVersionUID = 1L;

		class ActionRemoveListener implements ActionListener {
			int index;
			public ActionRemoveListener(int index) {
				this.index = index;
			}			
			public void actionPerformed(ActionEvent e) {
				
				PanelPractice.this.experiment.getPractice().getPractices().get(PracticeLine.this.blockingLevel).remove(index);
				
				// save opened paths
				Enumeration<TreePath> expandedPaths = treeRenderer.getTree().getExpandedDescendants(new TreePath(treeRenderer.getRoot()));
				PanelPractice.this.display();
				
				// restore opened paths
				while(expandedPaths != null && expandedPaths.hasMoreElements()) {
					TreePath path = expandedPaths.nextElement();
					TreePath nPath = Utils.getTreePath(path, treeRenderer.getTree(), false);
					if(nPath != null) treeRenderer.getTree().expandPath(nPath);
				}
			}
			
		}
		
		Vector<Vector<JComboBox>> factorPolicy = new Vector<Vector<JComboBox>>(); 
		Vector<JSpinner> spinnerNbTrials = new Vector<JSpinner>();
		JCheckBox practiceOff = new JCheckBox("off");
		
		private Vector<EditableItem> blockClass = new Vector<EditableItem>();
		private EditableMenu selectBlockClassBox = new EditableMenu(blockClass);
		
		private JCheckBox replicationsOn = new JCheckBox("replications");
		
		private int blockingLevel;
		
		public PracticeLine(boolean experiment, int blockingLevel, int nbBlocks, Practice currentPractice, int index, JPanel parent, GridBagConstraints gbc, GridBagLayout gbl) {
			super();
			this.blockingLevel = blockingLevel;
			
			gbc.gridx = 0;
			gbc.gridy++;
			int initGridy = gbc.gridy;
			
			JLabel titleRow = new JLabel();
			if(experiment) titleRow.setText("Experiment level");
			else titleRow.setText("Block"+blockingLevel+" level");
			titleRow.setPreferredSize(new Dimension(150, (int)titleRow.getPreferredSize().getHeight()));
			parent.add(titleRow, gbc);
			
			gbc.gridx++;
			parent.add(practiceOff, gbc);
			practiceOff.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					enableLine(e.getStateChange() == ItemEvent.DESELECTED);
				}
			});
			
			Vector<Factor> factorSet = PanelPractice.this.experiment.getFactorSet().getFactors();
			Vector<Factor> blockedAtThisLevel = new Vector<Factor>();
			if(!experiment) {
				int level = blockingLevel-1;
				while(level >= 0) {
					blockedAtThisLevel.addAll(PanelPractice.this.experiment.getBlocking().getSelectedBlockType().getBlockedFactors().get(level));
					level--;
				}
			}
			
			int initGridx = gbc.gridx;
			for(int i = 0; i < nbBlocks; i++) {
				gbc.gridx = initGridx;
				SpinnerNumberModel snmNbTrials = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
				JSpinner jspinner = new JSpinner(snmNbTrials);
				spinnerNbTrials.add(jspinner);
				if(i < currentPractice.getPractices().get(index).size()
						&& currentPractice.getPractices().get(index).get(i).size() > 0)
					jspinner.setValue(currentPractice.getPractices().get(index).get(i).size());
				practicesPanel.add(jspinner);
				gbc.gridx++;
				jspinner.setPreferredSize(new Dimension(50, (int)jspinner.getPreferredSize().getHeight()));
				parent.add(jspinner, gbc);
				
				Vector<JComboBox> comboBoxes = new Vector<JComboBox>();
				JComboBox comboBox;
				Factor factor;
				
				Vector<Value> values = new Vector<Value>();
				if(i < currentPractice.getPractices().get(index).size())
					values = currentPractice.getPractices().get(index).get(i).getValues();
				
				for (Iterator<Factor> iteratorFactors = factorSet.iterator(); iteratorFactors.hasNext();) {
					factor = iteratorFactors.next();
					comboBox = new JComboBox();
					if(blockedAtThisLevel.contains(factor)) {
						Value bValue = (Value)(Value.BLOCK_VALUE.clone());
						bValue.setFactor(factor);
						comboBox.addItem(bValue);
					}
					Value sValue = (Value)(Value.SAMPLE.clone());
					sValue.setFactor(factor);
					comboBox.addItem(sValue);
					
					for (Iterator<Value> iteratorValues = factor.getValues().iterator(); iteratorValues.hasNext();)
						comboBox.addItem(iteratorValues.next());
					for (Iterator<Value> iteratorValues2 = values.iterator(); iteratorValues2.hasNext();)
						comboBox.setSelectedItem(iteratorValues2.next());		
					
					comboBoxes.add(comboBox);
					gbc.gridx++;
					comboBox.setPreferredSize(new Dimension(150, (int)comboBox.getPreferredSize().getHeight()));
					parent.add(comboBox, gbc);
				}
				
				gbc.gridx++;
				if(i==0) parent.add(selectBlockClassBox,gbc);
				refreshBlockClass();
				
				if(i != 0) {
					MinusButton remove = new MinusButton();
					remove.addActionListener(new ActionRemoveListener(i));
					gbc.gridx++;
					parent.add(remove, gbc);
					remove.setBackground(Color.WHITE);
				} else {
					PlusButton add = new PlusButton();
					add.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							save();
							PanelPractice.this.experiment.getPractice().getPractices().get(PracticeLine.this.blockingLevel).add(new PracticeBlock());
							// save opened paths
							Enumeration<TreePath> expandedPaths = treeRenderer.getTree().getExpandedDescendants(new TreePath(treeRenderer.getRoot()));
							PanelPractice.this.display();
							// restore opened paths
							while(expandedPaths != null && expandedPaths.hasMoreElements()) {
								TreePath path = expandedPaths.nextElement();
								TreePath nPath = Utils.getTreePath(path, treeRenderer.getTree(), false);
								if(nPath != null) treeRenderer.getTree().expandPath(nPath);
							}
						}
					});
					gbc.gridx++;
					parent.add(add, gbc);
					add.setBackground(Color.WHITE);
				}
				factorPolicy.add(comboBoxes);
				boolean on = currentPractice.getPracticesEnabled().get(index);
				practiceOff.setSelected(!on);
				enableLine(on);
				gbc.gridy++;
			}
			int maxGridy = gbc.gridy;
			
			gbc.gridy = initGridy;
			gbc.gridx++;
			if(!experiment) {
				parent.add(replicationsOn, gbc);
				replicationsOn.setSelected(currentPractice.getReplicationsEnabled().get(index));
				replicationsOn.setBackground(Color.WHITE);
			} else {
				JPanel factice = new JPanel();
				factice.setPreferredSize(replicationsOn.getPreferredSize());
				parent.add(factice, gbc);
				factice.setBackground(Color.WHITE);
			}
			gbc.gridy = maxGridy;
		}
		
		private void enableLine(boolean on) {
			for (Iterator<Vector<JComboBox>> iterator = factorPolicy.iterator(); iterator.hasNext();) {
				Vector<JComboBox> line = iterator.next();
				for (Iterator<JComboBox> iterator2 = line.iterator(); iterator2.hasNext();) {
					JComboBox comboBox = iterator2.next();
					comboBox.setEnabled(on);
				}
			}
			for (Iterator<JSpinner> iterator = spinnerNbTrials.iterator(); iterator.hasNext();) {
				JSpinner next = iterator.next();
				next.setEnabled(on);
			}
			replicationsOn.setEnabled(on);
			selectBlockClassBox.setEnabled(on);
		}
		
		/**
		 * Put in the JComboBox containing the blockclasses all
		 * the blockclasses contained in the loaded plugins.  
		 */
		private void refreshBlockClass(){
			blockClass.removeAllElements();
			Vector<EditableItem> result = new Vector<EditableItem>();
			Practice practice = experiment.getPractice();
			
			Intertitle practiceClass = practice.getBlockClassAtBlockLevel(blockingLevel); 
			Function fnExp = experiment.getBlockClass().toFunction();
			
			for (Plugin plugin : experiment.getPlugins())
				for (Function function : plugin.getPredefinedBlockClass()) {
					if (!result.contains(function)) {
						if(practiceClass != null) {
							if(!function.equals(practiceClass.toFunction())) {
								result.add(function);
							}
						} else {
							result.add(function);
						}
					}
				}
			
			AddYourOwn addYourOwn = new AddYourOwn();
			result.add(addYourOwn);
			
			Function fnPractice = practiceClass == null ? addYourOwn : practiceClass.toFunction();
			
			if(!result.contains(fnExp)) 
				result.add(fnExp);
			if(!result.contains(fnPractice))
				result.add(fnPractice);
			
			int index = result.indexOf(fnPractice);
			if(index != -1) {
				EditableItem ei = result.remove(index);
				result.add(ei);
			} else {
				System.err.println("the practice function should be in the combo box...");
			}
			
			blockClass.addAll(result);
			selectBlockClassBox.setSelectedIndex(0);
			selectBlockClassBox.setSelectedIndex(result.size()-1);
			selectBlockClassBox.revalidate();
		}
		
		public Vector<PracticeBlock> getPracticeBlock() {
			Vector<PracticeBlock> res = new Vector<PracticeBlock>();
			int index = 0;
			for (Iterator<Vector<JComboBox>> iterator = factorPolicy.iterator(); iterator.hasNext();) {
				Vector<Value> values = new Vector<Value>();
				Vector<JComboBox> line = iterator.next();
				for (Iterator<JComboBox> iterator2 = line.iterator(); iterator2.hasNext();)
					values.add((Value)iterator2.next().getSelectedItem());
				Vector<Block> trials = new Vector<Block>();
				for(int i = 0; i < (Integer)spinnerNbTrials.get(index).getValue(); i++)
					trials.add(new Block());
				PracticeBlock block = new PracticeBlock(trials);
				block.setValues(values);
				res.add(block);
				index++;
			}
			
			return res;
		}

		public Intertitle getBlockClass() {
			Function function = (Function) selectBlockClassBox.getSelectedItem();
			return new Intertitle(function);
		}
		
	}
	
	public PanelPractice(DesignPlatform designPlatform, Experiment experiment,
			int depth) {
		super(designPlatform, experiment, depth);
		setLayout(new GridBagLayout());
		treeRenderer = new TreeRendererFullExperiment(
				false,
				experimentNodes,
				subjectNodes,
				setupNodes,
				blockNodes,
				interblockNodes,
				intertrialNodes,
				trialNodes,
				practiceInterblockNodes,
				practiceBlockNodes);
	}

	public void hiliteExperimentPreview() {
		getDesignPlatform().getExperimentPreview().hiliteExperimentTree();
	}

	public void save() {
		experiment.setPractice(getStep());
	}
	
	public Practice getStep() {
		Vector<Vector<PracticeBlock>> practices = new Vector<Vector<PracticeBlock>>();
		Vector<Boolean> practicesEnabled = new Vector<Boolean>();
		Vector<Boolean> replications = new Vector<Boolean>();
		Vector<Intertitle> blockClasses = new Vector<Intertitle>();
		for(int i = 0; i < lines.size(); i++) {
			Vector<PracticeBlock> pr = lines.get(i).getPracticeBlock(); 
			practices.add(pr);
			practicesEnabled.add(!lines.get(i).practiceOff.isSelected());
			replications.add(lines.get(i).replicationsOn.isSelected());
			blockClasses.add(lines.get(i).getBlockClass());
		}
		Practice p = new Practice(practices, practicesEnabled, replications, blockClasses);
		return p;
	}

	public void updateExperimentPreview() { }
	
	public void display() {
		practiceListener.disable();
		
		removeAll();
		lines.clear();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(3,3,3,3);
		
		Practice practice = experiment.getPractice();
		Vector<Vector<PracticeBlock>> practices = practice.getPractices();
		
		practicesPanel = new JPanel();
		practicesPanel.setBackground(Color.WHITE);
		GridBagLayout gbl = new GridBagLayout();
		practicesPanel.setLayout(gbl);

		gbc.gridx = 2;
		JLabel labelTrials = new JLabel("trials");
		practicesPanel.add(labelTrials, gbc);
		gbc.gridx++;
		for (int i = 0; i < experiment.getFactorSet().getFactors().size(); i++) {
			Factor factor = experiment.getFactorSet().getFactors().get(i);
			JLabel titleLabel = new JLabel("Factor "+factor.getShortName());
			practicesPanel.add(titleLabel, gbc);
			gbc.gridx++;
		}
		JLabel labelKind = new JLabel("practice kind");
		practicesPanel.add(labelKind, gbc);
		
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		for (int i = 0; i < practices.size(); i++) {
			PracticeLine practiceLine = new PracticeLine(i==0, i, Math.max(1, practices.get(i).size()), practice, i, practicesPanel, gbc, gbl);
			lines.add(practiceLine);
			
			practiceLine.practiceOff.addItemListener(practiceListener);
			for (Iterator<Vector<JComboBox>> iterator = practiceLine.factorPolicy.iterator(); iterator.hasNext();) {
				Vector<JComboBox> vector = iterator.next();
				for (Iterator<JComboBox> iterator2 = vector.iterator(); iterator2.hasNext();) {
					JComboBox comboBox = iterator2.next();
					comboBox.addActionListener(practiceListener);
				}
			}
			for (Iterator<JSpinner> iterator = practiceLine.spinnerNbTrials.iterator(); iterator.hasNext();) {
				JSpinner spinner = iterator.next();
				spinner.addChangeListener(practiceListener);
			}
			
			practiceLine.replicationsOn.addItemListener(practiceListener);
		}

		// build tree
		// experiment tree
		
		// save opened paths
		Enumeration<TreePath> expandedPaths = null;
		if(treeRenderer.getRoot() != null)
			expandedPaths = treeRenderer.getTree().getExpandedDescendants(new TreePath(treeRenderer.getRoot()));
		
		JTree experimentTree = treeRenderer.buildTree();
		JScrollPane jspTree = new JScrollPane(experimentTree);
		jspTree.setPreferredSize(new Dimension(380, getPreferredSize().height));
		JScrollPane jspPanel = new JScrollPane(practicesPanel);
		jspPanel.setPreferredSize(new Dimension(380, getPreferredSize().height));

		JSplitPane mainPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jspTree, jspPanel);
		GridBagConstraints gbcMain = new GridBagConstraints();
		gbcMain.fill = GridBagConstraints.BOTH;
		gbcMain.weightx = 1;
		gbcMain.gridy = 0;
		gbcMain.weighty = 0;
		gbcMain.insets = new Insets(5,0,5,0);
		Blocking blocking = experiment.getBlocking();
		BlockType selectedBlockType = blocking.getSelectedBlockType();
		blockTypeExpression.setText("Selected Block Type: "+selectedBlockType.toString());
		blockTypeExpression.setHorizontalAlignment(SwingConstants.CENTER);
		add(blockTypeExpression, gbcMain);
		gbcMain.gridy++;
		gbcMain.weighty = 1;
		add(mainPanel, gbcMain);
		
		save();
		treeRenderer.initTree(experiment.getBlockClass(), experiment.getFactorSet(), experiment.getOrdering(), experiment.getTiming(), experiment.getPractice());
	
		// restore opened paths
		while(expandedPaths != null && expandedPaths.hasMoreElements()) {
			TreePath path = expandedPaths.nextElement();
			TreePath nPath = Utils.getTreePath(path, treeRenderer.getTree(), true);
			if(nPath != null) treeRenderer.getTree().expandPath(nPath);
		}
		
		practiceListener.enable();
		revalidate();
		repaint();
	}
	
	private void updateTree() {
		// save opened paths
		Enumeration<TreePath> expandedPaths = treeRenderer.getTree().getExpandedDescendants(new TreePath(treeRenderer.getRoot()));
		
		PanelPractice.this.save();
		treeRenderer.initTree(experiment.getBlockClass(), experiment.getFactorSet(), experiment.getOrdering(), experiment.getTiming(), experiment.getPractice());
		
		// restore opened paths
		while(expandedPaths != null && expandedPaths.hasMoreElements()) {
			TreePath path = expandedPaths.nextElement();
			TreePath nPath = Utils.getTreePath(path, treeRenderer.getTree(), false);
			if(nPath != null) treeRenderer.getTree().expandPath(nPath);
		}
	}
}
