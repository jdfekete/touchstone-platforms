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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import fr.inria.insitu.touchstone.design.motor.AllPairs;
import fr.inria.insitu.touchstone.design.motor.Block;
import fr.inria.insitu.touchstone.design.motor.BlockType;
import fr.inria.insitu.touchstone.design.motor.Blocking;
import fr.inria.insitu.touchstone.design.motor.Experiment;
import fr.inria.insitu.touchstone.design.motor.Factor;
import fr.inria.insitu.touchstone.design.motor.FixedOrder;
import fr.inria.insitu.touchstone.design.motor.LatinSquare;
import fr.inria.insitu.touchstone.design.motor.Ordering;
import fr.inria.insitu.touchstone.design.motor.OrderingMode;
import fr.inria.insitu.touchstone.design.motor.Random;


public class PanelOrdering extends StepPanel<Ordering> {

	private static final long serialVersionUID = 42L;
	
	private Vector<OrderingMode> orderingModes = new Vector<OrderingMode>();
	private JPanel orderingPane = new JPanel();
	private JTextArea output = new JTextArea();
	private Vector<JComboBox> selectedOrderingModes = new Vector<JComboBox>();
	private Vector<JCheckBox> selectedSerial = new Vector<JCheckBox>();	
	private JTextPane errorMessages = new JTextPane();
	
	private JLabel blockTypeExpression = new JLabel("Selected Block Type : ");
	
	
	private DefaultMutableTreeNode root = new DefaultMutableTreeNode("experiment"); 
	private JTree blockAsTree = new JTree(root);
	
	private static OrderingMode ORDER_RANDOM = new Random();
	private static OrderingMode ORDER_LATIN_SQUARE = new LatinSquare();
	private static OrderingMode ORDER_ALL_PAIRS = new AllPairs();
	private static OrderingMode ORDER_FIXED = new FixedOrder();

	public PanelOrdering(DesignPlatform designPlatform, Experiment experiment, int depth) {
		super(designPlatform, experiment, depth);
		needJSP = false;
		orderingModes.add(ORDER_RANDOM);
		orderingModes.add(ORDER_LATIN_SQUARE);
		orderingModes.add(ORDER_ALL_PAIRS);
		orderingModes.add(ORDER_FIXED);
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.weightx = 1;
		gbc.weighty = 0.12;
		gbc.insets = new Insets(5,0,0,0);
		JScrollPane jsp = new JScrollPane(orderingPane, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(jsp,gbc);
		
		gbc.gridy++;
		gbc.weighty = 0;
		gbc.insets = new Insets(5,0,5,0);
		Blocking blocking = experiment.getBlocking();
		BlockType selectedBlockType = blocking.getSelectedBlockType();
		blockTypeExpression.setText("Selected Block Type: "+selectedBlockType.toString());
		blockTypeExpression.setHorizontalAlignment(SwingConstants.CENTER);
		add(blockTypeExpression, gbc);
		
		
		gbc.gridwidth = 1;
		gbc.weightx = 0.5;
		gbc.gridy++;
		gbc.weighty = 1;
		gbc.insets = new Insets(0,5,0,5);
		add(new JScrollPane(blockAsTree), gbc);	
		blockAsTree.setRootVisible(true);
		gbc.gridx++;
		add(new JScrollPane(output),gbc);
		
		gbc.gridy++;
		gbc.weighty = 0;
		add(errorMessages,gbc);
		errorMessages.setForeground(Color.RED);
		errorMessages.setEditable(false);
		errorMessages.setBackground(orderingPane.getBackground());
		
		blockAsTree.setCellRenderer(new TreeRendererExperiment(root));
	}

	/**
	 * add a string to the string list representing the errors that happened while reordering.
	 * @param s the string explaining the error
	 */
	private void addErrorMessage(String s){
		if (errorMessages.getText().length() >0)
			errorMessages.setText(errorMessages.getText()+"\n"+s);
		else
			errorMessages.setText(s);
	}

	
	public void display() {
		experiment.updateOrdering();
		
		orderingPane.removeAll();
		selectedOrderingModes.removeAllElements();
		selectedSerial.removeAllElements();
		Block blockToOrder = experiment.getBlocking().getSelectedBlockStructure();
		for (int i = 1; i < blockToOrder.getDepth(); i++){
			JComboBox cb = new JComboBox(orderingModes);
			selectedOrderingModes.add(cb);
			OrderingMode orderMode = experiment.getOrdering().getOrderedBlock().getBlocksAtDepth(i-1).get(0).getOrderingMode();
			boolean s = experiment.getOrdering().getOrderedBlock().getBlocksAtDepth(i-1).get(0).isSerial();
			if(orderMode instanceof LatinSquare)
				cb.setSelectedItem(ORDER_LATIN_SQUARE);
			else
				if(orderMode instanceof AllPairs)
					cb.setSelectedItem(ORDER_ALL_PAIRS);
				else
					if(orderMode instanceof Random)
						cb.setSelectedItem(ORDER_RANDOM);
					else
						cb.setSelectedItem(ORDER_FIXED);

			cb.addItemListener(new ItemListener(){
				
				public void itemStateChanged(ItemEvent itemEvent) {
					if (itemEvent.getStateChange() == ItemEvent.SELECTED){
						orderedBlockChanged();
					}
				}
			});
			
			JCheckBox serial = new JCheckBox("Serial: ");
			serial.setSelected(s);
			selectedSerial.add(serial);
			serial.setSelected(experiment.getOrdering().getOrderedBlock().getBlocksAtDepth(i-1).get(0).isSerial());
			serial.addActionListener(new ActionListener(){
				
				public void actionPerformed(ActionEvent e){
					orderedBlockChanged();
				}
			});
			serial.setHorizontalTextPosition(SwingConstants.LEFT);
			String label = "";
			if (i==blockToOrder.getDepth()-1)
				label = "Free factors :";
			else label = "Block"+i+" :";
			JPanel jp = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0; 
			gbc.gridy = 0;
			gbc.gridwidth = 1;
			jp.add(new JLabel(label),gbc);
			gbc.gridx++;
			jp.add(cb,gbc);
			gbc.gridy++;
			gbc.gridx--;
			gbc.gridwidth = 2;
			gbc.anchor = GridBagConstraints.LINE_START;
			jp.add(serial,gbc);
			orderingPane.add(jp);
		}
		
		Blocking blocking = experiment.getBlocking();
		BlockType selectedBlockType = blocking.getSelectedBlockType();
		blockTypeExpression.setText("Selected Block Type: "+selectedBlockType.toString());
		
		Ordering ordering = experiment.getOrdering();
		if(ordering == null) return;
		orderedBlockChanged();
		hiliteExperimentPreview();
	}

	
	/**
	 * Displays the ordered block.
	 */
	public void orderedBlockChanged(){
		output.setText("");
		Block orderedBlock = (Block)experiment.getBlocking().getSelectedBlockStructure().clone();
		experiment.getOrdering().setOrderedBlock(orderedBlock);
		orderedBlock.replicateBlockAtDepth(0);
		errorMessages.setText("");
		for (int i = 0; i<selectedOrderingModes.size() ; i++){
			OrderingMode mode = (OrderingMode) selectedOrderingModes.get(i).getSelectedItem();
			if (selectedSerial.get(i).isSelected()){
				try {
					Vector<Block> toOrder = orderedBlock.getBlocksAtDepth(i);
					mode.order(toOrder, selectedSerial.get(i).isSelected());
				} catch (Exception e){
					JLabel label = (JLabel)((JPanel)orderingPane.getComponent(i)).getComponent(0);
					addErrorMessage(label.getText()+" "+e.getMessage());
				}
				for (Block block : orderedBlock.getBlocksAtDepth(i+1)){
					block.replicate();
				}
			}					
			else {
				for (Block block : orderedBlock.getBlocksAtDepth(i+1)){
					block.replicate();
				}
				try{
					mode.order(orderedBlock.getBlocksAtDepth(i), selectedSerial.get(i).isSelected());
				} catch (Exception e){
					JLabel label = (JLabel)((JPanel)orderingPane.getComponent(i)).getComponent(0);
					addErrorMessage(label.getText()+" "+e.getMessage());
					e.printStackTrace();
				}
			}
		}

		BlockType selectedBlocktype = experiment.getBlocking().getSelectedBlockType();
		
		Vector<Factor> freeFactors = selectedBlocktype.getFreeFactorsWithoutFactice();
		output.append("Subject");
		for (int i = 0; i < selectedBlocktype.getBlockedFactors().size();i++)
			output.append(", Block"+(i+1));
		output.append(", Trial");		
		for (Factor subjFactor : selectedBlocktype.getSubjFactors())
			output.append(", \""+subjFactor.getShortName()+"\"");
		for (Vector<Factor> blockedFactors : selectedBlocktype.getBlockedFactors())
			for (Factor factor : blockedFactors)
				output.append(", \""+factor.getShortName()+"\"");
		for (Factor freeFactor : freeFactors)
			output.append(", \""+freeFactor.getShortName()+"\"");
		output.append("\n");

		Vector<String> detailedString = orderedBlock.toDetailedString();
		boolean stop = false;
		for (int i = 0; i<detailedString.size()&&!stop;i++){
			try{
				output.append(detailedString.get(i));
			} catch (OutOfMemoryError error) {
				addErrorMessage("Error : Could not display the whole text. ("+(error.getMessage())+")");
				stop = true;
			}
		}
		updateTree();
	}
	
	private void updateTree() {
		// save opened paths
		Enumeration<TreePath> expandedPaths = blockAsTree.getExpandedDescendants(new TreePath(root));
		
		Block subjects = experiment.getOrdering().getOrderedBlock();
		root.removeAllChildren();
		int i = 0;
		for (Block subject : subjects){
			String subjLabel = "S"+i;
			if(subject.toString().length() > 0) subjLabel += " - "+subject.toString();
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(subjLabel);
			Utils.addChild(node, subject);
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

	public Ordering getStep() {
		return new Ordering(experiment.getOrdering().getOrderedBlock());
	}

	public void save() {
		experiment.setOrdering(getStep());
	}
	
	public void updateExperimentPreview() {
	}
	
	public void hiliteExperimentPreview() {
		getDesignPlatform().getExperimentPreview().hiliteExperimentTree();
	}
	
}