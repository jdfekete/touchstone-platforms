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
import fr.inria.insitu.touchstone.design.motor.Block;
import fr.inria.insitu.touchstone.design.motor.BlockType;
import fr.inria.insitu.touchstone.design.motor.Blocking;
import fr.inria.insitu.touchstone.design.motor.Experiment;
import fr.inria.insitu.touchstone.design.motor.Intertitle;
import fr.inria.insitu.touchstone.design.motor.Timing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.tree.TreePath;


public class PanelTiming extends StepPanel<Timing> {

	private static final long serialVersionUID = 42L;

	private	EditableMenu intertitlesMenu;
	private	EditableMenu functionsMenu;
	private	Vector<JSpinner> estimatedTimes = new Vector<JSpinner>();
	private	Vector<Integer> quantities = new Vector<Integer>();
	private	Vector<JComponent> componentQuantities = new Vector<JComponent>();
	
	private	JLabel estimatedTimeTotal = new JLabel();
	private	JLabel estimatedTimeSubject = new JLabel();
	private	Vector<JCheckBox> hideCBs = new Vector<JCheckBox>();
	private	Vector<JTextField> criteria = new Vector<JTextField>();
	private	Vector<EditableMenu> intertitles = new Vector<EditableMenu>();
	
	// practice data
	private	Vector<Integer> practiceQuantities = new Vector<Integer>();
	private	Vector<JSpinner> practiceEstimatedTimes = new Vector<JSpinner>();
	private	Vector<JTextField> practiceCriteria = new Vector<JTextField>();
	private	Vector<EditableMenu> practiceIntertitles = new Vector<EditableMenu>();
	private	Vector<JComponent> componentPracticeQuantities = new Vector<JComponent>();

	private JLabel blockTypeExpression = new JLabel("Selected Block Type : ");
	
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

	private InterTrialListener interTrialListener = new InterTrialListener();
	
	public static int DEFAULT_ESTIMATED_TIME = 0;

	public PanelTiming(DesignPlatform designPlatform, Experiment experiment, int depth) {
		super(designPlatform, experiment, depth);
		setLayout(new GridBagLayout());
		needJSP = false;
		treeRenderer = new TreeRendererFullExperiment(
				true,
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

	/**
	 * 
	 * class used to specify a criterion
	 *
	 */
	private class SpecifyCriterion {

		JPanel specifyCriterionPane = new JPanel();
		private JButton specifyCriterionButton = new JButton("Specify Criterion");
		MachineCriterion machine;

		public SpecifyCriterion(){
			specifyCriterionPane.setLayout(new GridBagLayout());
			specifyCriterionPane.setBackground(Color.WHITE);
			specifyCriterionButton.setBackground(Color.WHITE);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1;
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.gridwidth = 1;
			specifyCriterionPane.add(specifyCriterionButton,gbc);
			machine = new MachineCriterion();
		}

		public JTextField getCriterionJTF(){
			return machine.criterion;
		}

		/**
		 * Set of widgets used to create a criterion
		 */
		private class MachineCriterion extends JPanel{
			
			private static final long serialVersionUID = 1L;
			
			JTextField criterion = new JTextField(10);
			JButton[] toolBarButtons = {
					new JButton("("),
					new JButton(")"),
					new JButton("{"),
					new JButton("}"),
					new JButton("!"),
					new JButton("&"),
					new JButton("|"),
					new JButton("^"),
					new JButton("=>") };

			EditableMenu fonctions = (EditableMenu)functionsMenu.clone();
			JButton add = new JButton("Add");
			JButton hide = new JButton("Hide");
			JButton show = new JButton("Show");
			JToolBar toolBar;

			private class MyActionListener implements ActionListener {
				String textToInsert = "";
				public MyActionListener(String textToInsert) {
					this.textToInsert = textToInsert;
				}

				public void actionPerformed(ActionEvent e) {
					int caretPosition = criterion.getCaretPosition(); 
					try {
						criterion.getDocument().insertString(caretPosition, textToInsert, null);
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}
				}
			}

			private MachineCriterion() {
				setLayout(new GridBagLayout());
				setBackground(Color.WHITE);
				
				toolBar = new JToolBar();
				toolBar.setBackground(Color.WHITE);;
				toolBar.setFloatable(false);

				for (int i = 0; i < toolBarButtons.length; i++) {
					toolBar.add(toolBarButtons[i]);
					toolBarButtons[i].addActionListener(new MyActionListener(toolBarButtons[i].getActionCommand()));
					toolBarButtons[i].setPreferredSize(toolBarButtons[toolBarButtons.length - 1].getPreferredSize());
				}

				add.addActionListener(new ActionListener(){

					public void actionPerformed(ActionEvent e) {
						int caretPosition = criterion.getCaretPosition();
						try {
							criterion.getDocument().insertString(caretPosition,fonctions.getText(), null);
						} catch (BadLocationException e1) {
							e1.printStackTrace();
						}
					}
				});

				specifyCriterionButton.addActionListener(new ActionListener(){

					public void actionPerformed(ActionEvent e) {
						showSpecifyCriterion();
					}
				});

				hide.addActionListener(new ActionListener(){

					public void actionPerformed(ActionEvent e) {
						remove(toolBar);
						remove(fonctions);
						remove(add);
						specifyCriterionPane.remove(hide);
						GridBagConstraints gbc = new GridBagConstraints();
						gbc.weightx = 0;
						gbc.weighty = 0;
						gbc.gridx = 1;
						gbc.gridy = 0;
						specifyCriterionPane.add(show);
						revalidate();
					}						
				});
				show.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						showSpecifyCriterion();
					}
				});
			}

			private void showSpecifyCriterion() {
				specifyCriterionPane.remove(specifyCriterionButton);
				specifyCriterionPane.remove(show);
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.weightx = 1;
				gbc.weighty = 0;
				gbc.gridx = 0;
				gbc.gridy = 1;
				gbc.fill = GridBagConstraints.HORIZONTAL;
				gbc.gridwidth = 2;
				toolBar.setPreferredSize(new Dimension(specifyCriterionButton.getPreferredSize().width, toolBar.getPreferredSize().height));
				add(toolBar, gbc);
				gbc.gridy++;
				gbc.gridwidth = 1;
				gbc.weightx = 1;
				add(fonctions,gbc);
				gbc.gridx = 1;
				gbc.weightx = 0;
				add(add,gbc);

				gbc.gridx = 0;
				gbc.gridy = 0;
				gbc.weightx = 1;
				gbc.fill = GridBagConstraints.HORIZONTAL;
				specifyCriterionPane.add(criterion,gbc);
				gbc.gridx++;
				gbc.weightx = 0;
				specifyCriterionPane.add(hide,gbc);	
				specifyCriterionPane.revalidate();
				revalidate();
			}	
		}
	}

	private void updateSetUp() {
		// save opened paths
		Enumeration<TreePath> expandedPaths = treeRenderer.getTree().getExpandedDescendants(new TreePath(treeRenderer.getRoot()));

		treeRenderer.updateSetup(getStep());

		// restore opened paths
		while(expandedPaths != null && expandedPaths.hasMoreElements()) {
			TreePath path = expandedPaths.nextElement();
			TreePath nPath = Utils.getTreePath(path, treeRenderer.getTree(), false);
			if(nPath != null) treeRenderer.getTree().expandPath(nPath);
		}
	}
	
	private void updateTrial() {
		// save opened paths
		Enumeration<TreePath> expandedPaths = treeRenderer.getTree().getExpandedDescendants(new TreePath(treeRenderer.getRoot()));

		treeRenderer.updateTrial(getStep());

		// restore opened paths
		while(expandedPaths != null && expandedPaths.hasMoreElements()) {
			TreePath path = expandedPaths.nextElement();
			TreePath nPath = Utils.getTreePath(path, treeRenderer.getTree(), false);
			if(nPath != null) treeRenderer.getTree().expandPath(nPath);
		}
	}
	
	private void updateInterTrial(boolean off) {
		// save opened paths
		Enumeration<TreePath> expandedPaths = treeRenderer.getTree().getExpandedDescendants(new TreePath(treeRenderer.getRoot()));

//		treeRenderer.updateIntertrial(getStep());
		treeRenderer.updateIntertrial(getStep(), off, experiment);

		// restore opened paths
		while(expandedPaths != null && expandedPaths.hasMoreElements()) {
			TreePath path = expandedPaths.nextElement();
			TreePath nPath = Utils.getTreePath(path, treeRenderer.getTree(), false);
			if(nPath != null) treeRenderer.getTree().expandPath(nPath);
		}
	}
	
	
	public void display() {
		removeAll();
		
		estimatedTimes = new Vector<JSpinner>();
		quantities = new Vector<Integer> ();
		componentQuantities = new Vector<JComponent>();
		criteria = new Vector<JTextField>();
		intertitles = new Vector<EditableMenu>();
		practiceEstimatedTimes = new Vector<JSpinner>();
		practiceQuantities = new Vector<Integer> ();
		componentPracticeQuantities = new Vector<JComponent>();
		practiceCriteria = new Vector<JTextField>();
		practiceIntertitles = new Vector<EditableMenu>();
		
		Block selectedBlock = experiment.getBlocking().getSelectedBlockStructure();

		//intertitlesMenu
		Vector<EditableItem> intertitlesSet = new Vector<EditableItem>();
		for (Function f : experiment.getPredefinedIntertitles())
			intertitlesSet.add(f);
		intertitlesSet.add(new AddYourOwn());
		intertitlesMenu = new EditableMenu(intertitlesSet);

		//CriteriaFunctionsMenu
		Vector<EditableItem> criteriaSet = new Vector<EditableItem>();
		for (Function f : experiment.getPredefinedCriteria())
			criteriaSet.add(f);
		criteriaSet.add(new AddYourOwn());
		functionsMenu = new EditableMenu(criteriaSet);	

		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(Color.WHITE);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(3,3,3,3);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx++;
		gbc.gridx++;
		panel.add(new JLabel("Quantity",javax.swing.SwingConstants.CENTER),gbc);
		gbc.gridx++;
		panel.add(new JLabel("Estimated time",javax.swing.SwingConstants.CENTER),gbc);
		gbc.gridx++;
		panel.add(new JLabel("Java class",javax.swing.SwingConstants.CENTER),gbc);
		gbc.gridx++;
		panel.add(new JLabel("Criterion",javax.swing.SwingConstants.CENTER),gbc);

		gbc.gridy+=2;
		addLine(Utils.getIconSetup(), panel, "set up:",1,gbc, true, true, TreeRendererFullExperiment.SETUP_COLOR, false, false);
		intertitles.get(intertitles.size()-1).addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateSetUp();
			}
		});
		criteria.get(criteria.size()-1).getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				updateSetUp();
			}

			public void insertUpdate(DocumentEvent e) {
				updateSetUp();
			}

			public void removeUpdate(DocumentEvent e) {
				updateSetUp();
			}
		});
		
		gbc.gridy+=2;
		addLine(Utils.getIconTrial(), panel, "Trial:",selectedBlock.getNumberOfTrials(),gbc, true, false, TreeRendererFullExperiment.TRIAL_COLOR, false, false);
		criteria.get(criteria.size()-1).getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				updateTrial();
			}

			public void insertUpdate(DocumentEvent e) {
				updateTrial();
			}

			public void removeUpdate(DocumentEvent e) {
				updateTrial();
			}
		});
		
		gbc.gridy+=2;
		addLine(Utils.getIconInterTrial(), panel, "intertrial:",selectedBlock.getNumberOfInterBlock(selectedBlock.getDepth()),gbc, true, true, TreeRendererFullExperiment.TRIAL_COLOR, false, true);
		
		hideCBs.get(hideCBs.size()-1).addItemListener(interTrialListener);
		intertitles.get(intertitles.size()-1).addActionListener(interTrialListener);
		criteria.get(criteria.size()-1).getDocument().addDocumentListener(interTrialListener);
		
		if(experiment.getPractice() != null) {
			if(experiment.getPractice().isPracticeEnabled(0)) {
				gbc.gridy+=2;
				addLine(Utils.getIconInterblockPracticeExp(), panel, "interpractice0:",
						experiment.getPractice().getPractices().get(0).size(),gbc, true, true, TreeRendererFullExperiment.SETUP_COLOR, true, true);
				InterBlockListener practiceInterblockListener = new InterBlockListener(0, practiceIntertitles.size()-1, practiceCriteria.size()-1, true);
				hideCBs.get(hideCBs.size()-1).addItemListener(practiceInterblockListener);
				practiceIntertitles.get(practiceIntertitles.size()-1).addActionListener(practiceInterblockListener);
				practiceCriteria.get(practiceCriteria.size()-1).getDocument().addDocumentListener(practiceInterblockListener);
			} else {
				practiceIntertitles.add(null);
				practiceCriteria.add(null);
				practiceEstimatedTimes.add(null);
				practiceQuantities.add(null);
				componentPracticeQuantities.add(null);
			}
		}
		
		gbc.gridy+=2;
		for (int i = 2 ; i < selectedBlock.getDepth();i++){
			addLine(Utils.getIconInterBlock(), panel, "interblock"+(i-1)+":",selectedBlock.getNumberOfInterBlock(i),gbc, true, true, TreeRendererFullExperiment.BLOCK_COLOR, false, true);
			InterBlockListener interblockListener = new InterBlockListener(i-1, intertitles.size()-1, criteria.size()-1, false);
			hideCBs.get(hideCBs.size()-1).addItemListener(interblockListener);
			intertitles.get(intertitles.size()-1).addActionListener(interblockListener);
			criteria.get(criteria.size()-1).getDocument().addDocumentListener(interblockListener);
			gbc.gridy+=2;
			if(experiment.getPractice() != null && experiment.getPractice().isPracticeEnabled(i-1)) {
				addLine(Utils.getIconInterblockPractice(), panel, "interpractice"+(i-1)+":",
						experiment.getPractice().getPractices().get(i-1).size(),
						gbc, true, true, TreeRendererFullExperiment.PRACTICE_COLOR, true, true);
				InterBlockListener practiceInterblockListener = new InterBlockListener(i-1, practiceIntertitles.size()-1, practiceCriteria.size()-1, true);
				hideCBs.get(hideCBs.size()-1).addItemListener(practiceInterblockListener);
				practiceIntertitles.get(practiceIntertitles.size()-1).addActionListener(practiceInterblockListener);
				practiceCriteria.get(practiceCriteria.size()-1).getDocument().addDocumentListener(practiceInterblockListener);
				gbc.gridy+=2;
			} else {
				practiceIntertitles.add(null);
				practiceCriteria.add(null);
				practiceEstimatedTimes.add(null);
				practiceQuantities.add(null);
				componentPracticeQuantities.add(null);
			}
		}
		
		gbc.weightx=0;
		gbc.gridx = 1;
		gbc.gridwidth = 5;
		panel.add(new JToolBar.Separator(),gbc);
		gbc.gridy++;
		gbc.weightx=0;
		gbc.gridwidth = 1;
		gbc.gridx = 1;
		panel.add(new JLabel("Total Estimated Time :",javax.swing.SwingConstants.RIGHT),gbc);
		gbc.gridwidth = 4;
		gbc.gridx+=2;
		panel.add(estimatedTimeTotal,gbc);

		gbc.gridy++;
		gbc.gridx=0;
		gbc.gridwidth = 1;
		gbc.gridx++;
		panel.add(new JLabel("Estimated time per Subject :",javax.swing.SwingConstants.RIGHT),gbc);
		gbc.gridwidth = 4;
		gbc.gridx+=2;
		panel.add(estimatedTimeSubject,gbc);
		updateTime();		

		Timing timing = experiment.getTiming();

		if(timing == null) return;

		// build tree
		// experiment tree
		
		// save opened paths
		Enumeration<TreePath> expandedPaths = null;
		if(treeRenderer.getRoot() != null)
			expandedPaths = treeRenderer.getTree().getExpandedDescendants(new TreePath(treeRenderer.getRoot()));
		
		JScrollPane jspTree = new JScrollPane(treeRenderer.buildTree());
		jspTree.setPreferredSize(new Dimension(380, getPreferredSize().height));
		JScrollPane jspPanel = new JScrollPane(panel);
		jspPanel.setPreferredSize(new Dimension(380, getPreferredSize().height));

		JSplitPane mainPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jspTree, jspPanel);
		
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5,0,5,0);
		Blocking blocking = experiment.getBlocking();
		BlockType selectedBlockType = blocking.getSelectedBlockType();
		blockTypeExpression.setText("Selected Block Type: "+selectedBlockType.toString());
		blockTypeExpression.setHorizontalAlignment(SwingConstants.CENTER);
		add(blockTypeExpression, gbc);
		
		gbc.weighty = 1;
		gbc.gridy++;
		add(mainPanel, gbc);
		
		// restore saved values
		
		Function addYourOwn = new AddYourOwn();
		for(int i = 0; i< criteria.size();i++) {
			if(criteria.get(i) == null)
				continue;
			if(i < timing.getCriteria().size())
				criteria.get(i).setText(timing.getCriteria().get(i));
			else 
				criteria.get(i).setText("");
		}
		for(int i = 0; i< intertitles.size();i++){
			if(intertitles.get(i) == null)
				continue;
			Function savedIntertitle;
			if(i < timing.getIntertitles().size() && timing.getIntertitles().get(i) != null) savedIntertitle = timing.getIntertitles().get(i).toFunction();
			else savedIntertitle = addYourOwn;
			Vector<EditableItem> itemSet = intertitles.get(i).getItemSet();
			
			int index = itemSet.indexOf(savedIntertitle);
			if(index == -1) {
				itemSet.add(savedIntertitle);
				index = itemSet.indexOf(savedIntertitle);
			}
			
			itemSet.setElementAt(savedIntertitle, index);
			 

			//TODO [Matthis] this is terribly ugly but that's the only way i found to refresh the selectedItem
			//(repaint and (re)validate don't work.)
			Vector<EditableItem> itemSetForRefresh = new Vector<EditableItem>();
			itemSetForRefresh.add(new AddYourOwn("123.56.^@#"));
			intertitles.get(i).setSelectedItem(itemSetForRefresh.firstElement());

			intertitles.get(i).setItemSet(itemSet);
			intertitles.get(i).setSelectedItem(savedIntertitle);			
		}
		for(int i = 0; i< estimatedTimes.size();i++) {
			if(estimatedTimes.get(i) == null)
				continue;
			if(i < timing.getIntertitles().size()) 
				estimatedTimes.get(i).setValue(timing.getEstimatedTime().get(i));
			else
				estimatedTimes.get(i).setValue(PanelTiming.DEFAULT_ESTIMATED_TIME);
		}
		for(int i = 0; i< practiceCriteria.size();i++) {
			if(practiceCriteria.get(i) == null)
				continue;
			if(i < timing.getPracticeCriteria().size() && timing.getPracticeCriteria().get(i) != null)
				practiceCriteria.get(i).setText(timing.getPracticeCriteria().get(i));
			else 
				practiceCriteria.get(i).setText("");
		}
		for(int i = 0; i< practiceIntertitles.size();i++){
			if(practiceIntertitles.get(i) == null)
				continue;
			Function savedIntertitle;
			if(i < timing.getPracticeIntertitles().size() && timing.getPracticeIntertitles().get(i) != null) savedIntertitle = timing.getPracticeIntertitles().get(i).toFunction();
			else savedIntertitle = addYourOwn;
			Vector<EditableItem> itemSet = practiceIntertitles.get(i).getItemSet();
			
			int index = itemSet.indexOf(savedIntertitle);
			if(index == -1) {
				itemSet.add(savedIntertitle);
				index = itemSet.indexOf(savedIntertitle);
			}
			
			itemSet.setElementAt(savedIntertitle, index);

			//TODO [Matthis] this is terribly ugly but that's the only way i found to refresh the selectedItem
			//(repaint and (re)validate don't work.)
			Vector<EditableItem> itemSetForRefresh = new Vector<EditableItem>();
			itemSetForRefresh.add(new AddYourOwn("123.56.^@#"));
			practiceIntertitles.get(i).setSelectedItem(itemSetForRefresh.firstElement());

			practiceIntertitles.get(i).setItemSet(itemSet);
			practiceIntertitles.get(i).setSelectedItem(savedIntertitle);			
		}
		for(int i = 0; i< practiceEstimatedTimes.size();i++) {
			if(practiceEstimatedTimes.get(i) == null)
				continue;
			if(i < timing.getPracticeEstimatedTime().size() && timing.getPracticeEstimatedTime().get(i) != null) 
				practiceEstimatedTimes.get(i).setValue(timing.getPracticeEstimatedTime().get(i));
			else
				practiceEstimatedTimes.get(i).setValue(PanelTiming.DEFAULT_ESTIMATED_TIME);
		}

		hiliteExperimentPreview();

		save();
		treeRenderer.initTree(experiment.getBlockClass(), experiment.getFactorSet(), experiment.getOrdering(), experiment.getTiming(), experiment.getPractice());
		updateQuantities();
		
		// restore opened paths
		while(expandedPaths != null && expandedPaths.hasMoreElements()) {
			TreePath path = expandedPaths.nextElement();
			TreePath nPath = Utils.getTreePath(path, treeRenderer.getTree(), true);
			if(nPath != null) treeRenderer.getTree().expandPath(nPath);
		}
		
		updateTime();
	}

	private void updateQuantities() {
		
		quantities.set(2, intertrialNodes.size());
		if(componentQuantities.get(2) != null && componentQuantities.get(2) instanceof JLabel) {
			((JLabel)componentQuantities.get(2)).setText(""+intertrialNodes.size());
			((JLabel)componentQuantities.get(2)).repaint();
		}
		
		quantities.set(1, trialNodes.size());
		if(componentQuantities.get(1) != null && componentQuantities.get(1) instanceof JLabel) {
			((JLabel)componentQuantities.get(1)).setText(""+intertrialNodes.size());
			((JLabel)componentQuantities.get(1)).repaint();
		}
		
		for(int i = 3; i < quantities.size(); i++) {
			quantities.set(i, interblockNodes.get(i-3).size());
			if(componentQuantities.get(i) != null && componentQuantities.get(i) instanceof JLabel) {
				((JLabel)componentQuantities.get(i)).setText(""+interblockNodes.get(i-3).size());
				((JLabel)componentQuantities.get(i)).repaint();
			}
		}
		
		for(int i = 0; i < practiceQuantities.size(); i++) {
			if(practiceInterblockNodes.get(i).size() == 0) continue;
			practiceQuantities.set(i, practiceInterblockNodes.get(i).size());
			if(componentPracticeQuantities.get(i) != null && componentPracticeQuantities.get(i) instanceof JLabel) {
				((JLabel)componentPracticeQuantities.get(i)).setText(""+practiceInterblockNodes.get(i).size());
				((JLabel)componentPracticeQuantities.get(i)).repaint();
			}
		}
		
		repaint();
	}

	private class InterTrialListener implements ActionListener, ItemListener, DocumentListener {
		
		private boolean off = false;

		public InterTrialListener() { }

		public void itemStateChanged(ItemEvent e) {
			off = e.getStateChange() == ItemEvent.SELECTED;
			updateInterTrial(off);
		}
		public void actionPerformed(ActionEvent e) {
			updateInterTrial(off);
		}
		public void changedUpdate(DocumentEvent e) {
			updateInterTrial(off);
		}
		public void insertUpdate(DocumentEvent e) {
			updateInterTrial(off);
		}
		public void removeUpdate(DocumentEvent e) {
			updateInterTrial(off);
		}
	}
	
	private class InterBlockListener implements ActionListener, ItemListener, DocumentListener {
		private int interblockNumber;
		private int indexIntertitle;
		private int indexCriteria;
		private boolean practice = false;
		
		private boolean off = false;
		
		public InterBlockListener(int interblockNumber, int indexIntertitle, int indexCriteria, boolean practice) {
			this.interblockNumber = interblockNumber;
			this.indexIntertitle = indexIntertitle;
			this.indexCriteria = indexCriteria;
			this.practice = practice;
		}

		void updateInterblock() {
			// save opened paths
			Enumeration<TreePath> expandedPaths = treeRenderer.getTree().getExpandedDescendants(new TreePath(treeRenderer.getRoot()));
			
			save();
			treeRenderer.updateInterblock(getStep(), interblockNumber, indexIntertitle, indexCriteria, practice, off, experiment);
			
			// restore opened paths
			while(expandedPaths != null && expandedPaths.hasMoreElements()) {
				TreePath path = expandedPaths.nextElement();
				TreePath nPath = Utils.getTreePath(path, treeRenderer.getTree(), false);
				if(nPath != null) treeRenderer.getTree().expandPath(nPath);
			}
		}

		public void itemStateChanged(ItemEvent e) {
			off = e.getStateChange() == ItemEvent.SELECTED;
			updateInterblock();
		}
		
		public void actionPerformed(ActionEvent e) {
			updateInterblock();
		}

		public void changedUpdate(DocumentEvent e) {
			updateInterblock();
		}

		public void insertUpdate(DocumentEvent e) {
			updateInterblock();
		}

		public void removeUpdate(DocumentEvent e) {
			updateInterblock();
		}
	}

	/**
	 * Add a new line that will be used to specify the criterion and intertitle for each blocking level to this panel.
	 * @param label the block level
	 * @param quantity the quantity of block at this level
	 * @param gbc the grid bag constraint specifying where the line should be displayed.
	 */
	private void addLine(ImageIcon imageIcon, JPanel container, String label, int quantity, GridBagConstraints gbc, boolean criterion, boolean intertitle, Color foreground, boolean practice, boolean disableCheckbox){
		addLine(imageIcon, container, label, new JLabel(quantity+"",javax.swing.SwingConstants.CENTER), gbc, criterion, intertitle, foreground, practice, disableCheckbox);
	}	

	/**
	 * Add a new line that will be used to specify the criterion and intertitle for each blocking level to this panel.
	 * @param label the block level
	 * @param quantity the quantity of block at this level
	 * @param gbc the grid bag constraint specifying where the line should be displayed.
	 */
	private void addLine(ImageIcon imageIcon, JPanel container, String label, JComponent quantity, GridBagConstraints gbc, boolean criterion, boolean intertitle, Color foreground, boolean practice, boolean disableCheckbox){
		gbc.gridx=0;
		gbc.weightx = 0;
		
		if(disableCheckbox) {
			JCheckBox off = new JCheckBox("hide");
			container.add(off,gbc);
			hideCBs.add(off);
		}
		gbc.gridx++;
		
		JLabel lab = new JLabel(label,javax.swing.SwingConstants.RIGHT);
		lab.setForeground(foreground);
		lab.setIcon(imageIcon);
		container.add(lab,gbc);
		gbc.gridx++;
		container.add(quantity,gbc);
		gbc.gridx++;
		JPanel jp = new JPanel();

		JSpinner timeJTF = new JSpinner(new SpinnerNumberModel(DEFAULT_ESTIMATED_TIME, 0, 1000, 1));
		if(practice) {
			practiceEstimatedTimes.add(timeJTF);
		}
		else estimatedTimes.add(timeJTF);
		timeJTF.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				updateTime();
			}
		});
		jp.setLayout(new FlowLayout());
		jp.setBackground(Color.WHITE);
		jp.add(timeJTF);
		jp.add(new JLabel("sec."));
		container.add(jp,gbc);
		gbc.weightx = 1;
		gbc.gridx++;
		EditableMenu em = null;
		if(intertitle) {
			em = (EditableMenu)intertitlesMenu.clone();
			container.add(em,gbc);
		}

		if(practice) practiceIntertitles.add(em);
		else intertitles.add(em);
		gbc.gridx++;
		SpecifyCriterion sc = null;
		if(criterion) {
			sc = new SpecifyCriterion();
			container.add(sc.specifyCriterionPane,gbc);
			gbc.gridy++;
			container.add(sc.machine,gbc);
			gbc.gridy--;
		}
		if(sc != null) {
			if(practice) practiceCriteria.add(sc.getCriterionJTF());
			else criteria.add(sc.getCriterionJTF());
		}
		else {
			if(practice) practiceCriteria.add(null);
			else criteria.add(null);
		}

		if (quantity.getClass()== JLabel.class) {
			if(practice) {
				practiceQuantities.add(Integer.parseInt(((JLabel)quantity).getText()));
				componentPracticeQuantities.add(quantity);
			} else { 
				quantities.add(Integer.parseInt(((JLabel)quantity).getText()));
				componentQuantities.add(quantity);
			}
		}
	}


	/**
	 * refresh the label displaying the time need to perform the experiment
	 */
	public void updateTime(){
		Block selectedBlock = experiment.getBlocking().getSelectedBlockStructure();
		estimatedTimeTotal.setText(secondsToString(estimateTime()));
		estimatedTimeSubject.setText(secondsToString((long)(estimateTime()/selectedBlock.get(0).getReplications())));
	}

	/**
	 * 
	 * @return the time estimated to perform the experiment
	 */
	public long estimateTime(){
		long estimatedTime = 0;
		for (int i = 0 ; i< quantities.size(); i++)
			estimatedTime += quantities.get(i).intValue()*(Integer)estimatedTimes.get(i).getValue();
		for (int i = 0 ; i< practiceQuantities.size(); i++) {
			if(practiceQuantities.get(i) != null) {
				estimatedTime += practiceQuantities.get(i).intValue()*(Integer)practiceEstimatedTimes.get(i).getValue();
			}
		}
		return estimatedTime;
	}

	/**
	 * @param time a duration in milliseconds
	 * @return a string representing the duration a the format d:h:m:s
	 */
	public static String secondsToString(long time){
		int seconds = (int)(time % 60);
		int minutes = (int)((time/60) % 60);
		int hours = (int)((time/3600) % 24);
		int days = (int)(time/(3600*24));
		String result = "";
		if (days >0 ){
			result+= days+"d ";
			result+=(hours<10  ? "0" : "")+ hours+"h ";
			result+=(minutes<10? "0" : "")+ minutes+"mn ";
			result+=(seconds<10? "0" : "")+ seconds+"s ";
		}		    
		else {
			if (hours>0){
				result+=(hours<10  ? "0" : "")+ hours+"h ";
				result+=(minutes<10? "0" : "")+ minutes+"mn ";
				result+=(seconds<10? "0" : "")+ seconds+"s ";	    		
			}
			else{
				if (minutes>0){
					result+=(minutes<10? "0" : "")+ minutes+"mn ";
					result+=(seconds<10? "0" : "")+ seconds+"s ";
				}
				else{
					if (seconds>0)
						result+=(seconds<10? "0" : "")+ seconds+"s";
				}		    			
			}		    		
		}
		return result;
	}



	public Timing getStep() {
		Vector<String> criteriaString = new Vector<String>();
		Vector<Intertitle> intertitlesObjects = new Vector<Intertitle>();
		Vector<Integer> estimatedTimeValues = new Vector<Integer>();
		for (JTextField criterionJTF : criteria) {
			if(criterionJTF != null) criteriaString.add(criterionJTF.getText());
			else criteriaString.add(null);
		}
		
		for (EditableMenu edm : intertitles) {
			if(edm != null)  {
				Function function = (Function) edm.getSelectedItem();
				Intertitle intertitle = new Intertitle(function);
				intertitlesObjects.add(intertitle);
//				Vector<String> argsType = function.getArgsType();
//				Vector<String> argsValue = function.getArgsValues();
//				Intertitle intertitle = new Intertitle(argsType,argsValue,function.getClasse(),function.getName());
//				intertitlesObjects.add(intertitle);
			} else {
				intertitlesObjects.add(null);
			}
		}
		for (JSpinner textField : estimatedTimes) {
			if(textField != null) 
				estimatedTimeValues.add((Integer)textField.getValue());
			else
				estimatedTimeValues.add(null);
		}
		
		Vector<String> practiceCriteriaString = new Vector<String>();
		Vector<Intertitle> practiceIntertitlesObjects = new Vector<Intertitle>();
		Vector<Integer> practiceEstimatedTimeValues = new Vector<Integer>();
		for (JTextField criterionJTF : practiceCriteria) {
			if(criterionJTF != null) practiceCriteriaString.add(criterionJTF.getText());
			else practiceCriteriaString.add(null);
		}
		for (EditableMenu edm : practiceIntertitles) {
			if(edm != null)  {
				Function function = (Function) edm.getSelectedItem();
				Intertitle intertitle = new Intertitle(function);
				practiceIntertitlesObjects.add(intertitle);
			} else {
				practiceIntertitlesObjects.add(null);
			}
		}
		for (JSpinner textField : practiceEstimatedTimes) {
			if(textField != null) 
				practiceEstimatedTimeValues.add((Integer)textField.getValue());
			else
				practiceEstimatedTimeValues.add(null);
		}

		return new Timing(criteriaString,intertitlesObjects,estimatedTimeValues, practiceCriteriaString, practiceIntertitlesObjects, practiceEstimatedTimeValues);
	}


	public String getStatus() {
		return super.getStatus();
	}

	public void save() {
		experiment.setTiming(getStep());
	}

	public void updateExperimentPreview() {
	}

	public void hiliteExperimentPreview() {
		getDesignPlatform().getExperimentPreview().hiliteExperimentTree();
	}

}
