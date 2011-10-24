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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import fr.inria.insitu.touchstone.design.XMLParser.PluginHandler;
import fr.inria.insitu.touchstone.design.graphic.widgets.AddYourOwn;
import fr.inria.insitu.touchstone.design.graphic.widgets.ArrowButton;
import fr.inria.insitu.touchstone.design.graphic.widgets.EditableItem;
import fr.inria.insitu.touchstone.design.graphic.widgets.EditableMenu;
import fr.inria.insitu.touchstone.design.graphic.widgets.Function;
import fr.inria.insitu.touchstone.design.graphic.widgets.PlusButton;
import fr.inria.insitu.touchstone.design.motor.Experiment;
import fr.inria.insitu.touchstone.design.motor.Intertitle;
import fr.inria.insitu.touchstone.design.motor.Plugin;
import fr.inria.insitu.touchstone.design.motor.Step;


public class PanelExperiment extends StepPanel<Step> {

	private static final long serialVersionUID = 42L;

	private JLabel titleLabel = new JLabel("Title of experiment:");
	private JTextField titleField  = new JTextField();
	private JLabel codeLabel = new JLabel("Short code:");
	private JTextField codeField  = new JTextField();
	private JLabel authorLabel = new JLabel("Author:");
	private JTextField authorField  = new JTextField();
	private JLabel selectBlockClassLabel = new JLabel("Select a kind of experiment:");
	private Vector<EditableItem> blockClasses = new Vector<EditableItem>();
	private EditableMenu selectBlockClassBox = new EditableMenu(blockClasses);
	private JLabel descriptionLabel = new JLabel("Description:");
	private JTextArea descriptionField  = new JTextArea();
	private PluginPanel pluginsPanel = new PluginPanel();
	private ArrowButton mb = new ArrowButton();

	private ExperimentListener experimentListener;

	public PanelExperiment(DesignPlatform designPlatform, Experiment experiment, int depth){
		super(designPlatform, experiment, depth);	
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1;
		gbc.weighty = 0.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.gridy = 0;
		gbc.gridx = 0;
		gbc.insets = new Insets(2,2,0,2);
		add(titleLabel,gbc);
		gbc.weighty = 0;
		gbc.gridy = 1;
		add(titleField,gbc);
		gbc.gridy = 2;
		add(codeLabel,gbc);
		gbc.gridy = 3;
		add(codeField,gbc);
		gbc.gridy = 4;
		add(authorLabel,gbc);
		gbc.gridy = 5;
		add(authorField,gbc);
		gbc.gridwidth = 2;
		gbc.gridy = 6;
		add(descriptionLabel,gbc);
		JPanel jp = new JPanel();
		mb.addMouseListener(new MouseAdapter(){

			public void mousePressed(MouseEvent e) {
				if (!mb.isOpen())
					pluginsPanel.addAll();				
				else	
					pluginsPanel.removeAll();
				mb.setOpen(!mb.isOpen());
			}			
		});

		jp.setLayout(new BorderLayout());
		jp.add(mb,BorderLayout.WEST);
		jp.add(new JLabel("  Plugins:"));
		gbc.gridx = 0;
		gbc.gridy = 7;
		gbc.weighty = 0.7;
		add(new JScrollPane(descriptionField),gbc);
		gbc.gridy = 8;
		gbc.weighty = 0;
		add(jp,gbc);
		gbc.gridy = 9;
		gbc.weighty = 0;
		add(pluginsPanel,gbc);
		gbc.weighty = 0;
		gbc.gridy = 10;
		gbc.gridwidth = 1;
		add(selectBlockClassLabel,gbc);
		gbc.gridy = 11;
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		selectBlockClassBox.setPreferredSize(new Dimension(250, selectBlockClassBox.getPreferredSize().height));
		blockClasses.add(new AddYourOwn());
		add(selectBlockClassBox,gbc);
		selectBlockClassBox.setSelectedIndex(0);

		experimentListener = new ExperimentListener(this);
		codeField.getDocument().addDocumentListener(experimentListener);
		titleField.getDocument().addDocumentListener(experimentListener);
		authorField.getDocument().addDocumentListener(experimentListener);
		descriptionField.getDocument().addDocumentListener(experimentListener);
		selectBlockClassBox.addActionListener(experimentListener);
	}


	public Step getStep() {
		experiment.setDescription(descriptionField.getText());
		experiment.setPlugins(pluginsPanel.getPlugins());
		experiment.setAuthor(authorField.getText());
		experiment.setTitle(titleField.getText()); 
		experiment.setShortCode(codeField.getText());
		Function function = (Function) selectBlockClassBox.getSelectedItem();
		Intertitle intertitle = new Intertitle(function);
		experiment.setBlockClass(intertitle);
		return experiment;
	}

	/**
	 * The panel to load a plugin.
	 */
	private class PluginPanel extends JPanel {

		private static final long serialVersionUID = 1L;

		JLabel pluginsSelectedLabel = new JLabel("Selected plugins:");
		JList pluginsSelectedList = new JList(new DefaultListModel());		
		JPanel panelSelectedPlugin;

		JLabel locationLabel = new JLabel("Specify location:");
		JTextField locationField = new JTextField();
		JButton browse = new JButton("Browse...");
		PlusButton add = new PlusButton();

		PluginDescriptionPanel pluginDescriptionPanel = null;

		public PluginPanel() {
			setLayout(new GridBagLayout());
			setBorder(BorderFactory.createTitledBorder(""));
			panelSelectedPlugin = new JPanel(new GridBagLayout());
			GridBagConstraints gbc2 = new GridBagConstraints();
			gbc2.fill = GridBagConstraints.BOTH;
			gbc2.anchor = GridBagConstraints.NORTHWEST;
			gbc2.weightx = 0.75;
			gbc2.weighty = 1.00;
			JScrollPane jsp = new JScrollPane(pluginsSelectedList);
			panelSelectedPlugin.add(jsp, gbc2);
			pluginDescriptionPanel = new PluginDescriptionPanel((Plugin)pluginsSelectedList.getSelectedValue());
			gbc2.weightx = 0.25;
			gbc2.gridx = 1;
			JScrollPane jspDesc = new JScrollPane(pluginDescriptionPanel);
			jspDesc.setPreferredSize(new Dimension(200, 60));
			panelSelectedPlugin.add(jspDesc, gbc2);

			refreshBlockClass();

			browse.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e) {
					JFileChooser fc = new JFileChooser(DesignPlatform.LAST_DIRECTORY) {
						protected JDialog createDialog(Component parent)
						throws HeadlessException {
							JDialog dlg = super.createDialog(parent);
							dlg.setLocation(getDesignPlatform().getLocationOnScreen().x + 50, getDesignPlatform().getLocationOnScreen().y + 50);
							return dlg;
						}
					};
					int returnVal = fc.showDialog(browse,"Choose Plugin");
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						String file = fc.getSelectedFile().getAbsolutePath();
						locationField.setText(file);
						DesignPlatform.LAST_DIRECTORY = fc.getSelectedFile().getParent();
						DesignPlatform.saveCurrentDirectory();
					}
				}			
			});

			add.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					File f = new File(locationField.getText());
					if (f.exists())	
						try {
							XMLReader saxReader = XMLReaderFactory.createXMLReader();
							PluginHandler ph = new PluginHandler(getExperiment());
							saxReader.setContentHandler(ph);
							saxReader.parse(f.toURI().toURL().toString());
							((DefaultListModel)pluginsSelectedList.getModel()).addElement(ph.getPlugin());
							ph.getPlugin().setXMLFile(f);
							pluginsSelectedList.setSelectedValue(ph.getPlugin(), true);
							refreshBlockClass();
						} catch (Exception ex) {
							ex.printStackTrace();
						}								
				}			
			});


			pluginsSelectedList.addMouseListener(new MouseListener(){

				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount()==2){
						((DefaultListModel)pluginsSelectedList.getModel()).remove(pluginsSelectedList.getSelectedIndex());
						refreshBlockClass();
					}
				}
				public void mouseEntered(MouseEvent e) {}
				public void mouseExited(MouseEvent e) {}
				public void mousePressed(MouseEvent e) {}
				public void mouseReleased(MouseEvent e) {}			
			});

			pluginsSelectedList.addListSelectionListener(new ListSelectionListener(){

				public void valueChanged(ListSelectionEvent e) {
					pluginDescriptionPanel.setPlugin((Plugin)pluginsSelectedList.getSelectedValue());
				}
			});			
		}

		/**
		 * Put in the JComboBox containing the blockclasses all
		 * the blockclasses contained in the loaded plugins.  
		 */
		public void refreshBlockClass(){
			selectBlockClassBox.removeActionListener(experimentListener);
			blockClasses.removeAllElements();
			Vector<EditableItem> result = new Vector<EditableItem>();
			for (Plugin plugin : getPlugins())
				for (Function function : plugin.getPredefinedBlockClass()) {
					if (!result.contains(function)) {
						if(experiment.getBlockClass() != null) {
							if(!function.equals(experiment.getBlockClass().toFunction())) {
								result.add(function);
							}
						} else {
							result.add(function);
						}
					}
				}
			AddYourOwn addYourOwn = new AddYourOwn();
			if(experiment.getBlockClass() != null
					&& !result.contains(experiment.getBlockClass().toFunction())) {
				if(!experiment.getBlockClass().toFunction().equals(addYourOwn))
					result.add(addYourOwn);
				result.add(experiment.getBlockClass().toFunction());
			} else {
				result.add(addYourOwn);
			}
			blockClasses.addAll(result);
			selectBlockClassBox.setSelectedIndex(0);
			selectBlockClassBox.setSelectedIndex(result.size()-1);
			selectBlockClassBox.revalidate();
			selectBlockClassBox.addActionListener(experimentListener);
		}

		/**
		 * remove all elements from the panel
		 */
		public void removeAll(){
			remove(pluginsSelectedLabel);
			remove(locationLabel);
			remove(locationField);
			remove(browse);
			remove(add);
			remove(panelSelectedPlugin);

			revalidate();
		}

		/**
		 * add all elements to the panel
		 */
		public void addAll(){
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.BOTH;
			gbc.anchor = GridBagConstraints.NORTHWEST;
			gbc.weightx = 1;
			gbc.weighty = 0;
			gbc.gridheight = 1;
			gbc.insets = new Insets(2,2,0,2);
			gbc.gridy = 0;
			gbc.gridx = 0;
			gbc.gridwidth = 5;
			add(pluginsSelectedLabel,gbc);
			gbc.weighty = 1;
			gbc.gridy = 1;
			gbc.gridheight = 5;
			panelSelectedPlugin.setMinimumSize(new Dimension(402, 80));
			add(panelSelectedPlugin,gbc);


			gbc.gridy += gbc.gridheight;
			gbc.weighty = 0;
			gbc.gridheight = 1;
			add(locationLabel,gbc);
			gbc.gridwidth = 3;
			gbc.gridy ++;
			add(locationField,gbc);
			gbc.gridx = 3;
			gbc.gridwidth = 1;
			gbc.weightx = 0;
			add(browse,gbc);
			gbc.gridx = 4;
			add(add,gbc);

			revalidate();
			PanelExperiment.this.revalidate();
		}

		/**
		 * 
		 * @return a Vector containing the plugins loaded by the users.
		 */
		public Vector<Plugin> getPlugins(){
			Vector<Plugin> plugins = new Vector<Plugin>();
			ListModel lm = pluginsSelectedList.getModel();
			for(int i = 0; i<lm.getSize();i++)
				plugins.add((Plugin)lm.getElementAt(i));			
			return plugins;
		}

		/***
		 * 
		 * Panel used to permit to the user to specify which 
		 * part ( criterion, intertitles, blocks,etc.) of the plugin should be loaded.
		 *
		 */
		private class PluginDescriptionPanel extends JPanel {

			private static final long serialVersionUID = 1L;

			JLabel pluginName;
			JCheckBox loadFactors;
			JCheckBox loadMeasures;
			JCheckBox loadCriteria;
			JCheckBox loadIntertitles;
			JCheckBox loadBlockClass;
			Plugin plugin;

			public PluginDescriptionPanel (Plugin p){

				loadFactors = new JCheckBox("Load Factors");
				loadMeasures = new JCheckBox("Load Measures");
				loadCriteria = new JCheckBox("Load Criteria");
				loadIntertitles = new JCheckBox("Load Intertitles");
				loadBlockClass = new JCheckBox("Load Block Class");
				pluginName = new JLabel("Plugin: ");

				loadFactors.addActionListener(new ActionListener(){

					public void actionPerformed(ActionEvent e) {
						plugin.setLoadFactors(!plugin.loadFactors());
					}
				});

				loadMeasures.addActionListener(new ActionListener(){

					public void actionPerformed(ActionEvent e) {
						plugin.setLoadMeasures(!plugin.loadMeasures());
					}
				});

				loadCriteria.addActionListener(new ActionListener(){

					public void actionPerformed(ActionEvent e) {
						plugin.setLoadCriteria(!plugin.loadCriteria());
					}
				});

				loadIntertitles.addActionListener(new ActionListener(){

					public void actionPerformed(ActionEvent e) {
						plugin.setLoadIntertitles(!plugin.loadIntertitles());
					}
				});				

				loadBlockClass.addActionListener(new ActionListener(){

					public void actionPerformed(ActionEvent e) {
						plugin.setLoadBlockClass(!plugin.loadBlockClass());
					}
				});	

				setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
				add(pluginName);
				add(loadFactors);
				add(loadMeasures);
				add(loadCriteria);
				add(loadIntertitles);
				add(loadBlockClass);

				setPlugin(p);
			}

			public void setPlugin(Plugin plugin) {
				loadFactors.setEnabled(plugin != null);
				loadMeasures.setEnabled(plugin != null);
				loadCriteria.setEnabled(plugin != null);
				loadIntertitles.setEnabled(plugin != null);
				loadBlockClass.setEnabled(plugin != null);
				boolean lf = plugin != null ? plugin.loadFactors() : true;
				loadFactors.setSelected(lf);
				boolean lm = plugin != null ? plugin.loadMeasures() : true;
				loadMeasures.setSelected(lm);
				boolean lc = plugin != null ? plugin.loadCriteria() : true;
				loadCriteria.setSelected(lc);
				boolean li = plugin != null ? plugin.loadIntertitles() : true;
				loadIntertitles.setSelected(li);
				boolean lcls = plugin != null ? plugin.loadBlockClass() : true;
				loadBlockClass.setSelected(lcls);
				if(plugin != null) pluginName.setText("Plugin: "+plugin.getId());
				else pluginName.setText("Plugin: ");
				this.plugin = plugin;
			}

		}			
	}

	public void display() {
		experimentListener.setEnabled(false);

		descriptionField.setText(experiment.getDescription() != null ? experiment.getDescription() : "");
		authorField.setText(experiment.getAuthor() != null ? experiment.getAuthor() : "");
		titleField.setText(experiment.getTitle() != null ? experiment.getTitle() : "");
		codeField.setText(experiment.getShortCode() != null ? experiment.getShortCode() : "");

		((DefaultListModel)pluginsPanel.pluginsSelectedList.getModel()).removeAllElements();
		for (Iterator<Plugin> iterator = experiment.getPlugins().iterator(); iterator.hasNext();) {
			Plugin plugin = iterator.next();
			boolean in = false;
			for(int i = 0; i < pluginsPanel.pluginsSelectedList.getModel().getSize(); i++)
				in = in || (((Plugin)pluginsPanel.pluginsSelectedList.getModel().getElementAt(i)).getId().compareTo(plugin.getId()) == 0);
			if(!in)
				((DefaultListModel)pluginsPanel.pluginsSelectedList.getModel()).addElement(plugin);
		}

		pluginsPanel.refreshBlockClass();

		hiliteExperimentPreview();
		experimentListener.setEnabled(true);
	}


	public void save() {
		getStep();
	}

	public void updateExperimentPreview() {
		getStep();
		getDesignPlatform().getExperimentPreview().updateExperimentData();
	}

	public void hiliteExperimentPreview() {
		getDesignPlatform().getExperimentPreview().hiliteExperimentData();
	}

}
