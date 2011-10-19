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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import fr.inria.insitu.touchstone.design.motor.ContinuousMeasureValue;
import fr.inria.insitu.touchstone.design.motor.DiscreteMeasureValue;
import fr.inria.insitu.touchstone.design.motor.Experiment;
import fr.inria.insitu.touchstone.design.motor.Factor;
import fr.inria.insitu.touchstone.design.motor.Measure;
import fr.inria.insitu.touchstone.design.motor.MeasureSet;
import fr.inria.insitu.touchstone.design.motor.MeasureType;
import fr.inria.insitu.touchstone.design.motor.MeasureValue;


public class PanelMeasures extends StepPanel<MeasureSet> {

	private static final long serialVersionUID = 42L;

	private DefaultMutableTreeNode rootAvailable = new DefaultMutableTreeNode();
	private DefaultMutableTreeNode rootSelected = new DefaultMutableTreeNode();
	private JTree availableMeasures = new JTree(rootAvailable);
	private JTree selectedMeasures  = new JTree(rootSelected);
	private PanelMeasure measureEdition = new PanelMeasure();

	private boolean listenersEnabled = true;

	public PanelMeasures(DesignPlatform designPlatform, Experiment experiment, int depth) {
		super(designPlatform, experiment, depth);

		needJSP = false;

		availableMeasures.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if(!listenersEnabled) return;
				int selRow = availableMeasures.getRowForLocation(e.getX(), e.getY());
				TreePath selPath = availableMeasures.getPathForLocation(e.getX(), e.getY());
				if(selRow != -1) {
					if(e.getClickCount() == 2) {
						DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)selPath.getLastPathComponent();
						Object selectedItem = selectedNode.getUserObject();	
						if ((selectedItem!=null)&&(selectedItem.getClass() == Measure.class)){
							measureEdition.setEditedMeasure(null);
							addMeasureTo(rootSelected, (Measure)((Measure)selectedItem).clone());
							updateSelectedMeasures();
						}							
					}
				}
			}
		});

		selectedMeasures.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if(!listenersEnabled) return;
				int selRow = selectedMeasures.getRowForLocation(e.getX(), e.getY());
				TreePath selPath = selectedMeasures.getPathForLocation(e.getX(), e.getY());
				if(selRow != -1) {
					if(e.getClickCount() == 2) {
						DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)selPath.getLastPathComponent();
						Object selectedItem = selectedNode.getUserObject();						
						if ((selectedItem!=null)&&(selectedItem.getClass() == Measure.class)){
							measureEdition.setEditedMeasure(null);
							removeMeasureFrom(rootSelected, selectedNode);
							measureEdition.clearAll();
							updateSelectedMeasures();
						}							
					}
				}  else {
					measureEdition.clearAll();
					selectedMeasures.clearSelection();
				}
			}
		});
		selectedMeasures.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)selectedMeasures.getLastSelectedPathComponent();
				Object selectedItem = null;
				if(node != null) selectedItem = node.getUserObject();
				if ((selectedItem!=null)&&(selectedItem.getClass() == Measure.class)){
					Measure selectedMeasure = (Measure)selectedItem;
					measureEdition.setEditedMeasure(selectedMeasure);
					measureEdition.displayMeasure(selectedMeasure);
				} else {
					measureEdition.clearAll();
				}
			}
		});
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth =1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5,5,5,5);
		gbc.gridwidth = 2;
		JButton getLogButton = new JButton("Get a log sample");
		add(getLogButton,gbc);
		getLogButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showSampleLogCreationWindow();	
			}
		});
		gbc.gridy++;
		gbc.gridwidth = 1;
		add(new JLabel("Available measures:"),gbc);
		gbc.gridx++;
		add(new JLabel("Selected measures:"),gbc);
		gbc.gridy++;
		gbc.gridx =0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		add(new JScrollPane(availableMeasures),gbc);
		gbc.gridx++;
		add(new JScrollPane(selectedMeasures),gbc);
		gbc.gridx=0;
		gbc.gridy++;
		gbc.gridwidth =2;
		gbc.weighty = 0;
		add(measureEdition,gbc);
	}

	private JFrame sampleLogCreationWindow = null;
	private JPanel sampleLogCreationPanel = null;
	private Vector<JTextField> expressions = null;
	private Vector<Measure> measureNames = null;
	private boolean syntaxCorrect = false; 

	private class ExpressionListener implements DocumentListener {

		private int cpt;

		public ExpressionListener(int cpt) {
			this.cpt = cpt;
		}

		private void checkSyntax() {
			try {
				new ContinuousMeasureValue(
						expressions.get(cpt).getText(), 
						measureNames.get(cpt).getType().equals(MeasureType.Integer));
				expressions.get(cpt).setBackground(Color.WHITE);
				syntaxCorrect = true; 
				return;
			} catch(Exception exc) {
				expressions.get(cpt).setBackground(new Color(255,55,55));
				syntaxCorrect = false; 
			}
			try {
				new DiscreteMeasureValue(
						expressions.get(cpt).getText(),
						measureNames.get(cpt).getType());
				expressions.get(cpt).setBackground(Color.WHITE);
				syntaxCorrect = true; 
				return;
			} catch(Exception exc) {
				expressions.get(cpt).setBackground(new Color(255,55,55));
				syntaxCorrect = false; 
			}
		}

		public void changedUpdate(DocumentEvent e) {
			checkSyntax();
		}

		public void insertUpdate(DocumentEvent e) {
			checkSyntax();
		}

		public void removeUpdate(DocumentEvent e) {
			checkSyntax();
		}

	}

	private void showSampleLogCreationWindow() {
		if(sampleLogCreationWindow == null) {
			sampleLogCreationWindow = new JFrame("");
			sampleLogCreationPanel = new JPanel();
			sampleLogCreationWindow.getContentPane().add(sampleLogCreationPanel);
		}
		sampleLogCreationWindow.setLocation(getDesignPlatform().getLocationOnScreen().x + 50, getDesignPlatform().getLocationOnScreen().y + 50);
		sampleLogCreationPanel.removeAll();
		Vector<Measure> measures = getExperiment().getMeasureSet().getMeasures();
		// possible types: Integer, Float, String
		sampleLogCreationPanel.setLayout(new BorderLayout());
		JPanel panelListMeasures = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.5;
		expressions = new Vector<JTextField>();
		measureNames = new Vector<Measure>();
		int cpt = 0;
		for (Iterator<Measure> iterator = measures.iterator(); iterator.hasNext();) {
			Measure measure = iterator.next();

			boolean isFactor = false;
			Vector<Factor> factors = getExperiment().getFactorSet().getFactors();
			for (Iterator<Factor> iterator2 = factors.iterator(); iterator2.hasNext();) {
				Factor factor = iterator2.next();
				if(factor.getShortName().compareTo(measure.getId()) == 0) {
					isFactor = true; break;
				}
			}
			if(
					isFactor
					|| measure.getId().compareTo("inPractice") == 0
					|| measure.getId().compareTo("nbBlocks") == 0
					|| measure.getId().compareTo("nbTrials") == 0
			) 
				continue;

			String mName = measure.getParent().length() != 0 ? 
					measure.getParent()+"."+measure.getId() 
					: measure.getId();
					JLabel measureName = new JLabel(mName+" ("+measure.getType()+")");
					if(measure.getPossibleValue() != null)
						expressions.add(new JTextField(measure.getPossibleValue().toString()));
					else {
						JTextField tf = new JTextField();
						tf.setMinimumSize(new Dimension(100,tf.getPreferredSize().height));
						tf.setPreferredSize(new Dimension(100,tf.getPreferredSize().height));
						expressions.add(tf);
						//				expressions.add(new JTextField("            "));
					}
					measureNames.add(measure);
					expressions.get(expressions.size()-1).getDocument().addDocumentListener(new ExpressionListener(cpt));
					panelListMeasures.add(measureName, gbc);
					gbc.gridx = 1;
					panelListMeasures.add(expressions.get(expressions.size()-1), gbc);
					gbc.gridy ++;
					gbc.gridx = 0;
					cpt++;
		}
		JButton getLogSample = new JButton("get log sample");
		getLogSample.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(syntaxCorrect) {
					for (int i = 0; i < expressions.size(); i++) {
						Measure measure = measureNames.get(i);
						MeasureValue mVal = null;
						try {
							mVal = new ContinuousMeasureValue(
									expressions.get(i).getText(), 
									measureNames.get(i).getType().equals(MeasureType.Integer));
							measure.setPossibleValue(mVal);
						} catch(Exception e1) {
							try {
								mVal = new DiscreteMeasureValue(
										expressions.get(i).getText(),
										measureNames.get(i).getType());
								measure.setPossibleValue(mVal);
							} catch(Exception e2) {
								System.err.println("should not happen... "+
										expressions.get(i).getText()+
										" ["+measureNames.get(i).getId()+" is of type "+getExperiment().getMeasureSet().getMeasures().get(i).getType()+"]");
							}
						}
					}
					JFileChooser fc = new JFileChooser(DesignPlatform.LAST_DIRECTORY) {
						protected JDialog createDialog(Component parent)
						throws HeadlessException {
							JDialog dlg = super.createDialog(parent);
							dlg.setLocation(getDesignPlatform().getLocationOnScreen().x + 50, getDesignPlatform().getLocationOnScreen().y + 50);
							return dlg;
						}
					};
					int returnVal = fc.showDialog(getDesignPlatform(),"Save");
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						writeLogSample(fc.getSelectedFile());
						DesignPlatform.LAST_DIRECTORY = fc.getSelectedFile().getParent();
						DesignPlatform.saveCurrentDirectory();
						sampleLogCreationWindow.setVisible(false);
					}
				} else {
					String[] initString =
					{ 
							"The sample log will be created by picking one possible value for each measure.\n" +
							"To specify the possible values for a given measure, you can either\n" +
							"define a set of discrete values and the possible value will be randomly picked in this set:\n",
							"{value_1, value_2, ..., value_n}\n",
							"or\n" +
							"define a continuous range and the value will be randomly picked in this range:\n",
							"[value_min, value_max]"
					};
					String[] initStyles =
					{ "regular", "italic", "regular", "italic" };
					JTextPane textPane = new JTextPane();
					StyledDocument doc = textPane.getStyledDocument();
					addStylesToDocument(doc);

					try {
						for (int i=0; i < initString.length; i++) {
							doc.insertString(doc.getLength(), initString[i],
									doc.getStyle(initStyles[i]));
						}
					} catch (BadLocationException ble) {
						System.err.println("Couldn't insert initial text into text pane.");
					}

					/* 
					 * The sample log will be created by picking one possible value for each measure.
					 * To specify the possible values for a given measure, you can either
					 * define a set of discrete values and the possible value will be randomly picked in this set:
					 * {value_1, value_2, ..., value_n}
					 * or 
					 * define a continuous range and the value will be randomly picked in this range:
					 * [value_min, value_max]
					 */
					final JDialog errorMessage = new JDialog(getDesignPlatform());
					errorMessage.setLocationRelativeTo(getDesignPlatform());
					errorMessage.setLocation(new Point(50, 50));
					errorMessage.getContentPane().setLayout(new BorderLayout());
					errorMessage.getContentPane().add(textPane, BorderLayout.NORTH);
					JButton ok = new JButton("ok");
					ok.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							errorMessage.setVisible(false);
						}
					});
					ok.setMaximumSize(ok.getPreferredSize());
					errorMessage.getContentPane().add(ok, BorderLayout.SOUTH);
					errorMessage.pack();
					errorMessage.setVisible(true);
				}
			}
		});
		sampleLogCreationPanel.add(panelListMeasures, BorderLayout.CENTER);
		sampleLogCreationPanel.add(getLogSample, BorderLayout.SOUTH);
		sampleLogCreationWindow.pack();
		sampleLogCreationWindow.setVisible(true);
	}

	protected void addStylesToDocument(StyledDocument doc) {
		//Initialize some styles.
		Style def = StyleContext.getDefaultStyleContext().
		getStyle(StyleContext.DEFAULT_STYLE);

		Style regular = doc.addStyle("regular", def);
		StyleConstants.setFontFamily(def, "SansSerif");

		Style s = doc.addStyle("italic", regular);
		StyleConstants.setItalic(s, true);

		s = doc.addStyle("bold", regular);
		StyleConstants.setBold(s, true);
	}


	private void writeLogSample(File file) {
		try {
			PrintWriter pw = new PrintWriter(file);
			getExperiment().toFacticeLogs(pw);
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * takes two node and tests if the second one is a child of the first one.
	 * @param parent the supposed parent node
	 * @param child the supposed child node
	 * @return the child index if <code>child</code> is a child of node, -1 otherwise.
	 */
	private int indexChildOf(TreeNode parent, DefaultMutableTreeNode child){
		for (int i = 0; i < parent.getChildCount(); i++) {
			DefaultMutableTreeNode c = (DefaultMutableTreeNode)parent.getChildAt(i);
			if (c.getUserObject().equals(child.getUserObject())) return i;
		}
		return -1;
	}	

	/**
	 * 
	 * @param node a Node
	 * @param m a Measure
	 * @return the node which contains the measure and is child of node.
	 */
	private DefaultMutableTreeNode getParentNode(TreeNode node, Measure m) {
		int i = 0;
		while( (i < node.getChildCount())
				&& (indexChildOf(node.getChildAt(i), new DefaultMutableTreeNode(m)) < 0))
			i++;
		return (DefaultMutableTreeNode)node.getChildAt(i);		
	}

	static MeasureType[] types = {
		MeasureType.Integer,
		MeasureType.Float,
		MeasureType.String
	};

	/**
	 * 
	 * Panel used to edit a specified Measure or to create a new one.
	 *
	 */
	private class PanelMeasure extends JPanel{

		private static final long serialVersionUID = 1L;

		JCheckBox log = new JCheckBox();
		JCheckBox cinematic = new JCheckBox();
		JTextField id= new JTextField();
		JTextField name= new JTextField();
		JTextField parent= new JTextField();
		JComboBox comboType= new JComboBox(types);
		JButton addButton = new JButton("Add");
		JButton clearAll = new JButton("New");

		Measure editedMeasure;

		public PanelMeasure() {
			setBorder(BorderFactory.createTitledBorder("Measure creation"));

			setLayout(new GridBagLayout());
			GridBagConstraints gbcEdition = new GridBagConstraints();
			gbcEdition.gridx = 0;
			gbcEdition.gridy = 0;
			gbcEdition.gridwidth =1;
			gbcEdition.gridheight = 1;
			gbcEdition.fill = GridBagConstraints.HORIZONTAL;
			gbcEdition.insets = new Insets(2,2,2,2);
			add(new JLabel("Trial"),gbcEdition);
			gbcEdition.gridx++;
			add(new JLabel("Event"),gbcEdition);
			gbcEdition.gridx++;
			gbcEdition.weightx = 1;
			add(new JLabel(" Type"),gbcEdition);
			gbcEdition.gridx++;
			add(new JLabel(" Parent"),gbcEdition);
			gbcEdition.gridx++;
			add(new JLabel(" ID"),gbcEdition);
			gbcEdition.gridx++;
			add(new JLabel(" Name"),gbcEdition);
			gbcEdition.gridx=0;
			gbcEdition.gridy++;
			gbcEdition.weightx = 0;
			add(log,gbcEdition);
			gbcEdition.gridx++;
			add(cinematic,gbcEdition);
			gbcEdition.gridx++;
			gbcEdition.weightx = 1;
			add(comboType,gbcEdition);
			gbcEdition.gridx++;
			add(parent,gbcEdition);
			gbcEdition.gridx++;
			add(id,gbcEdition);
			gbcEdition.gridx++;
			add(name,gbcEdition);
			gbcEdition.gridx++;
			gbcEdition.weightx =0;
			gbcEdition.gridy--;
			add(clearAll,gbcEdition);
			gbcEdition.gridy++;
			add(addButton,gbcEdition);

			log.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e) {
					if(!listenersEnabled) return;
					if (editedMeasure !=null){
						editedMeasure.setLog(log.isSelected());
						((DefaultTreeModel)selectedMeasures.getModel()).reload(getParentNode(rootSelected, editedMeasure));
						updateSelectedMeasures();
					}
				}
			});
			cinematic.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e) {
					if(!listenersEnabled) return;
					if (editedMeasure !=null){
						editedMeasure.setCinematic(cinematic.isSelected());
						updateSelectedMeasures();
						updateExperimentPreview();
					}
				}
			});
			id.addKeyListener(new KeyAdapter(){

				public void keyReleased(KeyEvent e) {
					if(!listenersEnabled) return;
					if (editedMeasure !=null){
						editedMeasure.setId(id.getText());
						//						updateSelectedMeasures();
						//						updateExperimentPreview();
					}
				}
			});
			id.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent e) {
					updateSelectedMeasures();
					updateExperimentPreview();
				}
			});
			name.addKeyListener(new KeyAdapter(){

				public void keyReleased(KeyEvent e) {
					if(!listenersEnabled) return;
					if (editedMeasure !=null){
						editedMeasure.setName(name.getText());
						//						updateSelectedMeasures();
						//						updateExperimentPreview();
					}
				}
			});
			name.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent e) {
					updateSelectedMeasures();
					updateExperimentPreview();
				}
			});
			parent.addKeyListener(new KeyAdapter(){

				public void keyReleased(KeyEvent e) {
					if(!listenersEnabled) return;
					if (editedMeasure !=null){
						DefaultMutableTreeNode parentMeasure = getParentNode(rootSelected, editedMeasure);
						DefaultMutableTreeNode measureNode = null;
						int i = indexChildOf(parentMeasure, new DefaultMutableTreeNode(editedMeasure));
						if(i >= 0) measureNode = (DefaultMutableTreeNode)parentMeasure.getChildAt(i);
						removeMeasureFrom(rootSelected, measureNode);
						editedMeasure.setParent(parent.getText());
						addMeasureTo(rootSelected, editedMeasure);
						//						updateSelectedMeasures();
						//						updateExperimentPreview();
					}
				}
			});
			parent.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent e) {
					updateSelectedMeasures();
					updateExperimentPreview();
				}
			});
			comboType.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					if(!listenersEnabled) return;
					if (editedMeasure !=null){
						MeasureType mType = MeasureType.String;
						try {
							mType = MeasureType.valueOf(comboType.getSelectedItem().toString());
						} catch(Exception unknownType) { }
						editedMeasure.setType(mType);
						updateExperimentPreview();
					}
				}
			});

			addButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					if(!listenersEnabled) return;
					Measure measure = getMeasure();
					addMeasureTo(rootSelected,measure);
					updateSelectedMeasures();
					updateExperimentPreview();
					clearAll();
				}
			});

			clearAll.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e) {
					if(!listenersEnabled) return;
					clearAll();
				}
			});


		}

		/**
		 * removes all the text from the JTextComponents
		 */
		public void clearAll(){
			setEditedMeasure(null);
			log.setSelected(false);
			cinematic.setSelected(false);
			id.setText("");
			name.setText("");
			parent.setText("");
			comboType.setSelectedIndex(0);
		}

		/**
		 * Displays the measure
		 * @param measure
		 */
		public void displayMeasure(Measure measure){
			log.setSelected(measure.isLog());
			cinematic.setSelected(measure.isCinematic());
			name.setText(measure.getName());
			parent.setText(measure.getParent());
			int index = -1;
			for (int i = 0; i < types.length; i++) {
				if(types[i].equals(measure.getType()))
					index = i;
			}
			if(index == -1) index = 0;
			comboType.setSelectedIndex(index);
			id.setText(measure.getId());
		}

		/**
		 * 
		 * @return the constructed measure from this panel
		 */
		public Measure getMeasure(){
			return new Measure(cinematic.isSelected(),log.isSelected(),id.getText(),parent.getText(),(MeasureType)comboType.getSelectedItem(),name.getText()); //,values.getText());
		}	

		/**
		 * set the measure to edit; 
		 * @param m the measure to edit; null if you're creating a measure.
		 */
		public void setEditedMeasure(Measure m){
			editedMeasure = m;
			addButton.setEnabled(m==null);
			if (addButton.isEnabled())
				setBorder(BorderFactory.createTitledBorder("Measure creation"));
			else
				setBorder(BorderFactory.createTitledBorder("Measure edition"));
		}
	}

	/**
	 * add a measure to the specified node
	 * @param root the node to which the measure will be added
	 * @param measure the measure to add
	 */
	@SuppressWarnings("unchecked")
	public void addMeasureTo(DefaultMutableTreeNode root,Measure measure){
		Enumeration<DefaultMutableTreeNode> children = root.children();
		boolean done = false;
		DefaultMutableTreeNode measureNode = new DefaultMutableTreeNode(measure);
		while ((children.hasMoreElements())&&(!done)){
			DefaultMutableTreeNode child = children.nextElement();
			if (child.toString().equals(measure.getParent())){
				if (indexChildOf(child, measureNode) >= 0)
					done = true;
				else{
					child.add(measureNode);
					done = true;
				}				
			}
		}		
		if (!done){
			DefaultMutableTreeNode parent;
			parent = new DefaultMutableTreeNode(measure.getParent());
			parent.add(measureNode);
			root.add(parent);
		}
		if(listenersEnabled) updateExperimentPreview();
	}

	/**
	 * removes a measure from a Node
	 * @param root the node from which the measure will be removed
	 * @param measure the node containing the measure to remove 
	 */
	@SuppressWarnings("unchecked")
	public void removeMeasureFrom(DefaultMutableTreeNode root,DefaultMutableTreeNode measure){
		Enumeration<DefaultMutableTreeNode> children = root.children();
		DefaultMutableTreeNode emptyParent = null;
		while (children.hasMoreElements()){
			DefaultMutableTreeNode child = children.nextElement();
			if (child.equals(measure.getParent())){
				child.remove(measure);
				if(child.getChildCount() == 0) emptyParent = child;
				break;
			}
		}
		if(emptyParent != null) root.remove(emptyParent);
		if(listenersEnabled) updateExperimentPreview();
		return;
	}


	@SuppressWarnings("unchecked")
	public MeasureSet getStep() {
		MeasureSet measureSet = new MeasureSet();
		Enumeration<DefaultMutableTreeNode> nodes = rootSelected.depthFirstEnumeration();
		while(nodes.hasMoreElements()){
			DefaultMutableTreeNode node = nodes.nextElement();
			if (node.getUserObject() instanceof Measure){
				Measure measure = (Measure)node.getUserObject();
				measureSet.addMeasure(measure);
			}
		}
		return measureSet;
	}


	public void display() {
		
		listenersEnabled = false;
		rootAvailable.removeAllChildren();
		rootSelected.removeAllChildren();
		for (Measure measure : experiment.getPredefinedMeasures()) {
			boolean toAdd = true;
			for (int i = 0; i < fr.inria.insitu.touchstone.run.exp.model.Experiment.MEASURES_ALWAYS_DEFINED.length; i++) {
				if(fr.inria.insitu.touchstone.run.exp.model.Experiment.MEASURES_ALWAYS_DEFINED[i].compareTo(measure.getId()) == 0) toAdd = false;
			}
			if(toAdd) addMeasureTo(rootAvailable,measure);
		}
		for (Factor factor : experiment.getFactorSet().getFactors()) {
			Measure measure = new Measure(true, false, factor.getShortName(), "", factor.getType(), factor.getFullName());
			addMeasureTo(rootAvailable,measure);
		}


		for (int i = 0; i<rootAvailable.getChildCount();i++)
			availableMeasures.expandRow(i);
		MeasureSet measureSet = experiment.getMeasureSet();
		if(measureSet != null) {
			for (Measure measure : measureSet.getMeasures()) {
				addMeasureTo(rootSelected,measure);
			}
		}
		selectedMeasures.expandRow(0);
		hiliteExperimentPreview();
		listenersEnabled = true;
		updateSelectedMeasures();
		
		measureEdition.clearAll();
	}

	public void save() {
		experiment.setMeasureSet(getStep());
	}

	public void updateExperimentPreview() {
		save();
		getDesignPlatform().getExperimentPreview().updateMeasuresData();
	}

	public void hiliteExperimentPreview() {
		getDesignPlatform().getExperimentPreview().hiliteMeasuresData();
	}

	private void updateSelectedMeasures() {
		TreePath[] selectedPaths = selectedMeasures.getSelectionPaths();
		
		// save opened paths
		Enumeration<TreePath> expandedPaths = selectedMeasures.getExpandedDescendants(new TreePath(rootSelected));
		((DefaultTreeModel)selectedMeasures.getModel()).reload();

		// restore opened paths
		while(expandedPaths != null && expandedPaths.hasMoreElements()) {
			TreePath path = expandedPaths.nextElement();
			TreePath nPath = Utils.getTreePath(path, selectedMeasures, true);
			if(nPath != null) selectedMeasures.expandPath(nPath);
		}
		
		selectedMeasures.setSelectionPaths(selectedPaths);
	}

}
