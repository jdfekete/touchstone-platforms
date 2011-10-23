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

import fr.inria.insitu.touchstone.design.graphic.widgets.MinusButton;
import fr.inria.insitu.touchstone.design.graphic.widgets.PlusButton;
import fr.inria.insitu.touchstone.design.motor.Experiment;
import fr.inria.insitu.touchstone.design.motor.Factor;
import fr.inria.insitu.touchstone.design.motor.FactorSet;
import fr.inria.insitu.touchstone.design.motor.MeasureType;
import fr.inria.insitu.touchstone.design.motor.Step;
import fr.inria.insitu.touchstone.design.motor.Value;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;


public class PanelFactors extends StepPanel<FactorSet> {

	private static final long serialVersionUID = 42L;
	
	private SubPanelFactors subPanel = new SubPanelFactors();
	private Vector<Factor> pluginsFactor = experiment.getPredefinedFactors();
	private JComboBox factorsFromPlugins = new JComboBox(pluginsFactor);
	private JScrollPane jsp = new JScrollPane(subPanel);
	private JLabel factorsFromPluginLabel = new JLabel("Load factors from plugins :");
	private JButton newFactor = new JButton("Create a new factor");
	
	private ExperimentListener experimentListener;
	
	public PanelFactors(DesignPlatform designPlatform, Experiment experiment, int depth) {
		super(designPlatform, experiment, depth);
		experimentListener = new ExperimentListener(this);
		needJSP = false;
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridy=0;
		gbc.gridx=0;
		gbc.weightx = 1;
		gbc.insets = new Insets(2,2,2,2);
		add(factorsFromPluginLabel,gbc);
		gbc.gridx++;
		gbc.fill = GridBagConstraints.BOTH;
		add(factorsFromPlugins,gbc);
		factorsFromPlugins.setMinimumSize(new Dimension(300,20));
		factorsFromPlugins.addItemListener(new ItemListener(){
			
			public void itemStateChanged(ItemEvent arg0) {
				if (arg0.getStateChange() == ItemEvent.SELECTED){
					subPanel.add((Factor)arg0.getItem());
					revalidate();
					jsp.getViewport().setViewPosition(new Point(0,jsp.getVerticalScrollBar().getMaximum()));
					updateExperimentPreview();
				}
			}
		});

		gbc.gridy=1;
		gbc.gridx=0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridwidth = 6;
		
		add(jsp,gbc);

		gbc.gridy++;
		gbc.gridwidth = 10;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weighty = 0;
		add(newFactor,gbc);				
		newFactor.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (e.getSource()==newFactor){	
					subPanel.add(subPanel.new PanelFactor());
					revalidate();
					jsp.getViewport().setViewPosition(new Point(0,jsp.getVerticalScrollBar().getMaximum()));
					updateExperimentPreview();
				}		
			}				
		});
		
	}

	public class SubPanelFactors extends JPanel {

		private static final long serialVersionUID = 1L;

		Vector<PanelFactor> panelsFactors = new Vector<PanelFactor>();
		GridBagConstraints gbc = new GridBagConstraints();

		public SubPanelFactors() {
			setLayout(new GridBagLayout());
			gbc.gridy = 0;
			revalidate();
		}

		/**
		 * 
		 * @param f the factor to display and add to the factor list.
		 */
		public void add(Factor f){
			add(new PanelFactor(f));
		}

		/**
		 * 
		 * @param pf displays and adds a PanelFactor to the panel factor's list
		 */
		public void add(final PanelFactor pf){
			panelsFactors.add(pf);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weighty = 0;
			gbc.weightx = 0;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.insets = new Insets(10,2,2,2);
			gbc.gridx = 1;
			add(pf.tagLabel,gbc);
			
			gbc.gridx = 2;
			add(pf.roleLabel,gbc);

			gbc.gridx = 3;
			gbc.gridwidth = 2;
			add(pf.nameLabel,gbc);

			gbc.gridx = 5;
			gbc.gridwidth = 1;
			add(pf.typeLabel,gbc);

			gbc.gridx = 6;
			add(pf.valueLabel,gbc);

			gbc.gridx = 0;
			gbc.gridwidth = 1;
			gbc.gridy++;
			gbc.insets = new Insets(0,2,2,2);
			add(pf.remove,gbc);
			pf.remove.addActionListener(new ActionListener(){
				
				public void actionPerformed(ActionEvent e) {
					remove(pf.remove);
					remove(pf.role);
					remove(pf.shortFactorName);
					remove(pf.fullFactorName);
					remove(pf.type);
					remove(pf.values.getParent().getParent());
					remove(pf.shortValueName);
					remove(pf.fullValueName);
					remove(pf.add);
					remove(pf.factorDependanceNameLabel);
					remove(pf.factorDependanceNameBox);
					remove(pf.tag);

					remove(pf.roleLabel);
					remove(pf.nameLabel);
					remove(pf.typeLabel);
					remove(pf.valueLabel);
					remove(pf.tagLabel);
					remove(pf.separator);

					panelsFactors.remove(pf);
					
					updateExperimentPreview();
					revalidate();
					repaint();
				}
			});

			gbc.gridx = 1;
			add(pf.tag,gbc);
			
			gbc.gridx = 2;
			add(pf.role,gbc);

			gbc.gridx = 3;
			gbc.weightx = 0.3;
			add(pf.shortFactorName,gbc);

			gbc.gridx = 4;
			gbc.weightx = 1;
			add(pf.fullFactorName,gbc);

			gbc.gridx = 5;
			gbc.weightx = 0;
			add(pf.type,gbc);

			gbc.gridx = 6;
			gbc.gridheight = 2;
			gbc.gridwidth = 3;
			gbc.insets = new Insets(4,2,2,2);
			JScrollPane jsp = new JScrollPane(pf.values);
			jsp.setPreferredSize(new Dimension(60, 50));
			add(jsp,gbc);
			pf.values.addKeyListener(new KeyAdapter(){
				public void keyPressed(KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE){
						int index = pf.values.getSelectedIndex();
						if (index >= 0){
							pf.thisFactor.getValues().remove(index);
							pf.valuesString.remove(index);
							pf.values.setListData(pf.valuesString);
							pf.ensureTypeCoherence();
						}
					}
				}
			});
			
			gbc.gridy += 2;
			gbc.gridheight = 1;
			gbc.gridwidth = 1;
			gbc.weightx = 0;
			gbc.insets = new Insets(2,2,10,2);
			add(pf.shortValueName,gbc);

			gbc.gridx = 7;
			gbc.weightx = 0.5;
			add(pf.fullValueName,gbc);

			gbc.gridx = 8;
			gbc.weightx = 0;
			add(pf.add,gbc);

			gbc.gridx = 9;
			gbc.gridy -= 2;

			gbc.gridx = 10;

			gbc.gridy+=4;
			gbc.gridx = 0;
			gbc.gridwidth = 10;
			add(pf.separator,gbc);

			gbc.gridy ++;
			gbc.gridx = 0;
		}

		/**
		 * @return the FactorSet constructed from the data of this step.
		 */
		public FactorSet getFactorSet() {
			Vector<Factor> factors = new Vector<Factor>();
			for (PanelFactor panelFactor : panelsFactors) {
				Factor f = panelFactor.getFactor();
				factors.add(f);
			}
			return new FactorSet(factors);
		}

		/**
		 * 
		 * graphical component set used to display a factor.
		 *
		 */
		public class PanelFactor {
			MinusButton remove = new MinusButton();
			JComboBox role = new JComboBox(fr.inria.insitu.touchstone.design.motor.FactorRole.values());
			JComboBox tag = new JComboBox(new String[]{"Within Subject","Between Subject"});
			JTextField shortFactorName = new JTextField("F"+(panelsFactors.size()+1));
			JTextField fullFactorName = new JTextField();
			JComboBox type = new JComboBox(fr.inria.insitu.touchstone.design.motor.MeasureType.values());
			Vector<String> valuesString = new Vector<String>();
			JList values = new JList(valuesString);
			JTextField shortValueName = new JTextField();
			JTextField fullValueName = new JTextField();
			PlusButton add = new PlusButton();
			JLabel factorDependanceNameLabel = new JLabel("Factor :");
			JComboBox factorDependanceNameBox = new JComboBox();
			JToolBar.Separator separator = new JToolBar.Separator();

			JLabel roleLabel = new JLabel("Factor Role :");
			JLabel nameLabel = new JLabel("Factor Name :");
			JLabel typeLabel = new JLabel("Factor Type :");
			JLabel valueLabel = new JLabel("Factor Values :");
			JLabel dependanceLabel = new JLabel("Inter-dependance :");
			JLabel tagLabel = new JLabel("Factor Tag :");
			
			Factor thisFactor = new Factor();

			public PanelFactor() {
				this(new Factor());
			}

			public PanelFactor(Factor factor){
				thisFactor = factor;
				
				shortFactorName.getDocument().removeDocumentListener(experimentListener);
				fullFactorName.getDocument().removeDocumentListener(experimentListener);
				shortValueName.getDocument().removeDocumentListener(experimentListener);
				fullValueName.getDocument().removeDocumentListener(experimentListener);
				type.removeActionListener(experimentListener);
				
				valuesString.removeAllElements();
				
				role.setSelectedItem(factor.getRole());
				shortFactorName.setText(factor.getShortName());
				fullFactorName.setText(factor.getFullName());
				type.setSelectedItem(factor.getType());
				tag.setSelectedItem(factor.getTag());
				
				for(Value v : factor.getValues())
					valuesString.add(v.getShortValue()+" "+v.getFullValue());
				if(factor.getValues().size() == 0) {
					valuesString.add("1");
					thisFactor.addValue("1", "");			
					valuesString.add("2");
					thisFactor.addValue("2", "");
				}

				add.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						thisFactor.addValue(shortValueName.getText(),fullValueName.getText());
						valuesString.add(shortValueName.getText()+" "+fullValueName.getText());
						values.setListData(valuesString);
						shortValueName.setText("");
						fullValueName.setText("");
						ensureTypeCoherence();
					}				
				});

				KeyListener kl = new KeyAdapter(){
					
					public void keyPressed(KeyEvent e) {
						if (e.getKeyCode() == KeyEvent.VK_ENTER){
							valuesString.add(shortValueName.getText()+" "+fullValueName.getText());
							thisFactor.addValue(shortValueName.getText(),fullValueName.getText());
							values.setListData(valuesString);
							shortValueName.setText("");
							fullValueName.setText("");
							ensureTypeCoherence();
						}

					}
				};
				shortValueName.addKeyListener(kl);
				fullValueName.addKeyListener(kl);
				
				shortFactorName.getDocument().addDocumentListener(experimentListener);
				fullFactorName.getDocument().addDocumentListener(experimentListener);
				shortValueName.getDocument().addDocumentListener(experimentListener);
				fullValueName.getDocument().addDocumentListener(experimentListener);
				type.addActionListener(experimentListener);
			}
			
			public void ensureTypeCoherence() {
				Vector<Value> vals = getFactor().getValues();
				boolean floatPossible = true;
				boolean integerPossible = true;
				for (Iterator<Value> iterator = vals.iterator(); iterator.hasNext();) {
					Value value = iterator.next();
					try {
						Double.parseDouble(value.getShortValue());
					} catch(NumberFormatException exc1) {
						floatPossible = false;
					}
					try {
						Integer.parseInt(value.getShortValue());
					} catch(NumberFormatException exc2) {
						integerPossible = false;
					}
				}
				int indexSelected = type.getSelectedIndex();
				if(!integerPossible) {
					if(!floatPossible) {
						MeasureType[] items = new MeasureType[1];
						items[0] = MeasureType.String;
						type.setModel(new DefaultComboBoxModel(items));
						type.setSelectedIndex(0);
						type.revalidate();
					} else {
						MeasureType[] items = new MeasureType[2];
						items[0] = MeasureType.String;
						items[1] = MeasureType.Float;
						type.setModel(new DefaultComboBoxModel(items));
						if(indexSelected < items.length)
							type.setSelectedIndex(indexSelected);
						else
							type.setSelectedIndex(0);
						type.revalidate();
					}
				} else {
					type.setModel(new DefaultComboBoxModel(MeasureType.values()));
					type.setSelectedIndex(indexSelected);
					type.revalidate();
				}
			}

			/***
			 * 
			 * @return a factor constructed from this set of component
			 */
			public Factor getFactor(){		
				thisFactor.setFullName(fullFactorName.getText());
				thisFactor.setShortName(shortFactorName.getText());
				thisFactor.setRole((fr.inria.insitu.touchstone.design.motor.FactorRole)role.getSelectedItem());
				thisFactor.setType((fr.inria.insitu.touchstone.design.motor.MeasureType)type.getSelectedItem());
				thisFactor.setTag((String)tag.getSelectedItem());
				return thisFactor;
			}

			/**
			 * @return null if this PanelFactor is totally filled, else the string that describes the problem.
			 */
			public String getStatus() {
				if (shortFactorName.getText().length()==0)
					return "You must fill the Factor's Name";
				if (shortFactorName.getText().indexOf(Value.separator)> -1)
					return "The factor "+shortFactorName.getText()+" can not contains the character "+Value.separator+" in its name.";
				
				
				int factorsWithThisName = 0;
				int index = 0;
				while ((factorsWithThisName<2)&&(index<panelsFactors.size())){
					if (panelsFactors.get(index).shortFactorName.getText().equals(shortFactorName.getText()))
						factorsWithThisName++;
					index++;
				}
				if (factorsWithThisName>=2)
					return "The factor name "+shortFactorName.getText()+" must be used only once.";
				return null;
			}
		}

		/**
		 * @return null if the FactorSet can be created, else the string that describes the problem.
		 */
		public String getStatus() {
			String result = null;
			int i = 0;
			while ((i<panelsFactors.size())&&(result==null)){
				result = panelsFactors.get(i).getStatus();
				i++;
			}			
			return result;
		}

		/**
		 * Display the StepPanel according to the previous steps
		 */
		public void display(Step step) {
			FactorSet fs = (FactorSet) step;
			removeAll();
			panelsFactors.removeAllElements();
			for (Factor f : fs.getFactors() )
				add(new PanelFactor(f));
			revalidate();
			repaint();
		}
	}

	
	public String getStatus() {
		return subPanel.getStatus();
	}

	public FactorSet getStep() {
		return subPanel.getFactorSet();
	}
	
	public void save() {
		save(true);
	}
	
	private void save(boolean warning) {
		FactorSet fs = getStep();
		if(fs.getFactors().size() < 1) return;
		for (Iterator<Factor> iterator = fs.getFactors().iterator(); iterator.hasNext();) {
			Factor factor = iterator.next();
			if(factor.getValues().size() < 2) {
				if(warning)
					JOptionPane.showMessageDialog(getDesignPlatform(),
					    factor.getShortName()+" must have at least two values.",
					    "Warning",
					    JOptionPane.WARNING_MESSAGE);
				return;
			}
		}
		experiment.setFactorSet(fs);
	}
	
	private void saveWithoutWarning() {
		save(false);
	}

	public void display() {
		experimentListener.setEnabled(false);
		
		Step step = experiment.getFactorSet();
		
		pluginsFactor.removeAllElements();
		pluginsFactor.addAll(experiment.getPredefinedFactors());
		if (pluginsFactor.isEmpty()){
			factorsFromPlugins.setVisible(false);
			factorsFromPluginLabel.setVisible(false);
		}
		else{
			factorsFromPlugins.setVisible(true);
			factorsFromPluginLabel.setVisible(true);
		}
		
		subPanel.display(step);
		hiliteExperimentPreview();
		experimentListener.setEnabled(true);
	}
	
	public void updateExperimentPreview() {
		saveWithoutWarning();
		getDesignPlatform().getExperimentPreview().updateFactorsData();
	}
	
	public void hiliteExperimentPreview() {
		getDesignPlatform().getExperimentPreview().hiliteFactorsData();
	}
	
}

