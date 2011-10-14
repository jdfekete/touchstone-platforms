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

import fr.inria.insitu.touchstone.design.graphic.widgets.MyButton;
import fr.inria.insitu.touchstone.design.motor.Experiment;
import fr.inria.insitu.touchstone.design.motor.Plugin;
import fr.inria.insitu.touchstone.design.motor.Step;
import fr.inria.insitu.touchstone.design.torun.CodeGeneration;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;


import com.sun.org.apache.xerces.internal.dom.DocumentImpl;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class PanelSummary extends StepPanel<Step> {

	private static final long serialVersionUID = 42L;

	public JTextPane summary = new JTextPane();

	public JButton loadXML = new JButton("Open another XML experiment file...");
	public JButton save = new JButton("Save");
	public JTextField xmlFileName = new JTextField("");
	public JButton javaCode = new JButton("Generate Java code");
	public JButton csvFile = new JButton("Get CSV description");

	private DocumentImpl xmlDoc;
	
	public static String LAST_DIRECTORY = ".";
	
	public PanelSummary(DesignPlatform designPlatform, final Experiment experiment, int depth) {
		super(designPlatform, experiment, depth);
		needJSP = false;

		loadXML.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser(DesignPlatform.LAST_DIRECTORY) {
					protected JDialog createDialog(Component parent)
					throws HeadlessException {
						JDialog dlg = super.createDialog(parent);
						dlg.setLocation(getDesignPlatform().getLocationOnScreen().x + 50, getDesignPlatform().getLocationOnScreen().y + 50);
						return dlg;
					}
				};
				int returnVal = fc.showDialog(loadXML,"Open");
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					String file = fc.getSelectedFile().getAbsolutePath();
					display(fc.getSelectedFile());
					xmlFileName.setText(file);
					DesignPlatform.LAST_DIRECTORY = fc.getSelectedFile().getParent();
					DesignPlatform.saveCurrentDirectory();
				}	
			}

		});

		xmlFileName.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) { textChanged(e); }
			public void insertUpdate(DocumentEvent e) { textChanged(e); }
			public void removeUpdate(DocumentEvent e) { textChanged(e); }
			void textChanged(DocumentEvent e) {
				javaCode.setEnabled(xmlFileName.getText().length() != 0);
			}
		});

		save.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser(DesignPlatform.LAST_DIRECTORY) {
					protected JDialog createDialog(Component parent)
					throws HeadlessException {
						JDialog dlg = super.createDialog(parent);
						dlg.setLocation(getDesignPlatform().getLocationOnScreen().x + 50, getDesignPlatform().getLocationOnScreen().y + 50);
						return dlg;
					}
				};
				int returnVal = fc.showDialog(save,"Save");
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					String file = fc.getSelectedFile().getAbsolutePath();
					save(file);
					xmlFileName.setText(file);
					DesignPlatform.LAST_DIRECTORY = fc.getSelectedFile().getParent();
					DesignPlatform.saveCurrentDirectory();
				}				
			}
		});

		javaCode.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				File experimentfile;
				if(xmlFileName.getText().length() != 0) experimentfile = new File(xmlFileName.getText());
				else {
					experimentfile = new File("._tmp.xml");
					save("._tmp.xml");
				}
				if (experimentfile.exists()) {
					JDialog framePlugins = new JDialog(getDesignPlatform());
					framePlugins.setLocation(getDesignPlatform().getLocationOnScreen().x + 50, getDesignPlatform().getLocationOnScreen().y + 50);
					CodeGenerationPanel panelPlugins = new CodeGenerationPanel(framePlugins, getExperiment(), experimentfile);
					framePlugins.getContentPane().add(panelPlugins);
					framePlugins.pack();
					framePlugins.setVisible(true);
				}
			}
		});

		csvFile.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser(LAST_DIRECTORY) {
					protected JDialog createDialog(Component parent)
					throws HeadlessException {
						JDialog dlg = super.createDialog(parent);
						dlg.setLocation(getDesignPlatform().getLocationOnScreen().x + 50, getDesignPlatform().getLocationOnScreen().y + 50);
						return dlg;
					}
				};
				int returnVal = fc.showDialog(getDesignPlatform(),"Save as csv");
				File file = new File("experiment.csv");
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					file = fc.getSelectedFile();
					LAST_DIRECTORY = fc.getSelectedFile().getParent();
					saveCurrentDirectory();
					
					PrintWriter pw;
					try {
						pw = new PrintWriter(file);
						getDesignPlatform().getExperiment().toCSV(pw);
						pw.close();
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 0;
		gbc.weightx = 0;
		gbc.gridy = 0;
		gbc.gridx = 0;
		add(loadXML, gbc);

		gbc.weighty = 1;
		gbc.weightx = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.gridy++;
		summary.setFont(ExperimentPreview.FONT);
		add(new JScrollPane(summary),gbc);

		gbc.weighty = 0;
		gbc.weightx = 0;
		gbc.gridy++;
		add(bottomPanel(), gbc);
	}
	
	private JPanel bottomPanel() {
		JPanel result = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		result.add(save,gbc);
		gbc.gridx++;
		gbc.weightx = 0.7;
		result.add(xmlFileName,gbc);
		gbc.gridx++;
		gbc.weightx = 0.1;
		result.add(javaCode);
		gbc.gridx++;
		gbc.weightx = 0.1;
		result.add(csvFile);
		return result;
	}

	private void display(File selectedFile) {
		try {
			FileReader in = new FileReader(selectedFile);
			summary.read(in, selectedFile.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * saves the displayed text at the specified location 
	 * @param path the specified location
	 */
	private void save(String path) {
		try {
			File file = new File(path);
			file.createNewFile();
			FileWriter fw = new FileWriter(file);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			verboseXML(xmlDoc, baos);
			fw.write(baos.toString());
			fw.flush();
			fw.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	static void verboseXML(DocumentImpl xmlDoc, OutputStream os) {
		try {
			OutputFormat of = new OutputFormat("XML","ISO-8859-1",true);
			of.setIndent(1);
			of.setIndenting(true);
			XMLSerializer serializer = new XMLSerializer(os,of);
			serializer.asDOMSerializer();
			serializer.serialize(xmlDoc.getDocumentElement());
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static void saveCurrentDirectory() {
		File lastDir = new File("_last_csv_dir.tmp");
		try {
			(new DataOutputStream(new FileOutputStream(lastDir))).writeUTF(LAST_DIRECTORY);
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public void display() {
		String text = "";
		try {
			xmlDoc = experiment.toXML();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			verboseXML(xmlDoc, baos);
			text = baos.toString();
			
			StyledDocument styledDocument = new DefaultStyledDocument() {
				private static final long serialVersionUID = 1L;

				public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
					super.insertString(offs, str, a);
				}
			};
			summary.setStyledDocument(styledDocument);

			summary.setText(text);
		} catch (Exception e){
		}
		hiliteExperimentPreview();
	}

	public void save() { }

	public void updateExperimentPreview() { }

	public void hiliteExperimentPreview() {
		getDesignPlatform().getExperimentPreview().hiliteAll();
	}

	class JarFilter extends FileFilter {

		//Accept all directories and all gif, jpg, tiff, or png files.
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().endsWith(".jar");
		}

		//The description of this filter
		public String getDescription() {
			return "Just jar files";
		}
	}

	class CodeGenerationPanel extends JPanel {

		private static final long serialVersionUID = 1L;
		private JList list;
		private JButton setJarFile;
		private JButton browseFolder;
		private JButton go;
		private JTextField folderSelected;
		private File experimentFile;
		private JDialog dialog;
		private JCheckBox generateClassesForCharacterValues;
		
		private JTextField additionalLibrariesTF;

		public CodeGenerationPanel(JDialog framePlugins, Experiment exp, File expFile) {
			super();
			this.experimentFile = expFile;
			this.dialog = framePlugins;

			go = new JButton("Go");
			go.setEnabled(false);

			GridBagLayout gbl = new GridBagLayout();
			GridBagConstraints gbc = new GridBagConstraints();
			setLayout(gbl);

			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1;
			gbc.gridy = 0;
			gbc.gridx = 0;
			gbc.gridwidth = 2;
			JLabel labelDirectory = new JLabel("Folder for code generation:");
			add(labelDirectory, gbc);
			gbc.gridy++;
			gbc.gridwidth = 1;
			gbc.weightx = 1;
			folderSelected = new JTextField("");
			folderSelected.setMinimumSize(new Dimension(200, folderSelected.getMinimumSize().height));
			folderSelected.setPreferredSize(new Dimension(200, folderSelected.getMinimumSize().height));
			folderSelected.getDocument().addDocumentListener(new DocumentListener() {
				public void changedUpdate(DocumentEvent e) {
					go.setEnabled(e.getDocument().getLength() != 0);
				}
				public void insertUpdate(DocumentEvent e) {
					go.setEnabled(e.getDocument().getLength() != 0);
				}
				public void removeUpdate(DocumentEvent e) {
					go.setEnabled(e.getDocument().getLength() != 0);
				}
			});
			add(folderSelected, gbc);
			gbc.gridx++;
			gbc.fill = GridBagConstraints.NONE;
			browseFolder = new JButton("Browse");
			browseFolder.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFileChooser fc = new JFileChooser(DesignPlatform.LAST_DIRECTORY) {
						protected JDialog createDialog(Component parent)
						throws HeadlessException {
							JDialog dlg = super.createDialog(parent);
							dlg.setLocation(getDesignPlatform().getLocationOnScreen().x + 50, getDesignPlatform().getLocationOnScreen().y + 50);
							return dlg;
						}
					};
					fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					fc.setDialogTitle("Set folder for code generation");
					int returnVal = fc.showDialog(PanelSummary.this.getDesignPlatform(),"Folder for code generation");
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						folderSelected.setText(fc.getSelectedFile().getAbsolutePath());
					}
				}
			});
			gbc.weightx = 0;
			add(browseFolder, gbc);

			if(exp.getPlugins().size() > 1) {
				gbc.gridy++;
				gbc.gridx=0;
				gbc.gridwidth = 2;
				gbc.fill = GridBagConstraints.HORIZONTAL;
				add(new JLabel("Jar files for plugins:"), gbc);
				gbc.gridy++;
				gbc.gridwidth = 1;
				gbc.fill = GridBagConstraints.BOTH;
				gbc.weightx = 1;
				final Vector<Plugin> plugins = new Vector<Plugin>();
				for (Iterator<Plugin> iterator = exp.getPlugins().iterator(); iterator.hasNext();) {
					Plugin plugin = iterator.next();
					if(plugin.getId().compareTo("Core")==0) continue;
					plugins.add(plugin);
				}
				list = new JList(plugins);
				list.setCellRenderer(new CellRenderer());
				list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				list.addListSelectionListener(new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						setJarFile.setEnabled(e.getFirstIndex() >= 0);
					}
				});
				add(list, gbc);






				gbc.gridx++;
				gbc.anchor = GridBagConstraints.EAST;
				gbc.weightx = 0;
				final JList setJarFileList = new JList(plugins);
				setJarFileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				setJarFileList.setCellRenderer(new CellButtonRenderer());
				setJarFileList.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						int index = setJarFileList.getSelectedIndex();
						JFileChooser fcPlugins = new JFileChooser(DesignPlatform.LAST_DIRECTORY) {
							protected JDialog createDialog(Component parent)
							throws HeadlessException {
								JDialog dlg = super.createDialog(parent);
								dlg.setLocation(getDesignPlatform().getLocationOnScreen().x + 50, getDesignPlatform().getLocationOnScreen().y + 50);
								return dlg;
							}
						};
						Plugin plugin = plugins.get(index);
						fcPlugins.setDialogTitle("Set jar file for plugin "+plugin.getId());
						fcPlugins.setFileFilter(new JarFilter());
						fcPlugins.setFileSelectionMode(JFileChooser.FILES_ONLY);
						int rVal = fcPlugins.showDialog(PanelSummary.this.getDesignPlatform(),"Set jar file");
						if (rVal == JFileChooser.APPROVE_OPTION) {
							plugin.setJarFile(fcPlugins.getSelectedFile());
							DesignPlatform.LAST_DIRECTORY = plugin.getJarFile().getParent();
							DesignPlatform.saveCurrentDirectory();
						}
						list.repaint();
					}
				});
				add(setJarFileList, gbc);
			}
			
			gbc.gridx=0;
			gbc.gridy++;
			gbc.gridwidth = 2;
			gbc.weightx = 1;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.BOTH;
			add(new JLabel("Required additional libraries:"), gbc);
			gbc.gridy++;
			gbc.gridwidth = 1;
			gbc.weightx = 1;
			additionalLibrariesTF = new JTextField("");
			additionalLibrariesTF.setMinimumSize(new Dimension(200, additionalLibrariesTF.getMinimumSize().height));
			additionalLibrariesTF.setPreferredSize(folderSelected.getPreferredSize());
			add(additionalLibrariesTF, gbc);
			gbc.gridx++;
			gbc.fill = GridBagConstraints.NONE;
			JButton browseLibraries = new JButton("Browse");
			browseLibraries.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFileChooser fc = new JFileChooser(DesignPlatform.LAST_DIRECTORY) {
						protected JDialog createDialog(Component parent)
						throws HeadlessException {
							JDialog dlg = super.createDialog(parent);
							dlg.setLocation(getDesignPlatform().getLocationOnScreen().x + 50, getDesignPlatform().getLocationOnScreen().y + 50);
							return dlg;
						}
					};
					fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
					fc.setMultiSelectionEnabled(true);
					fc.setDialogTitle("Select jar files for additional required libraries");
					fc.addChoosableFileFilter(new FileFilter() {
						
						public String getExtension(File f) {
					        String ext = null;
					        String s = f.getName();
					        int i = s.lastIndexOf('.');
					        if (i > 0 &&  i < s.length() - 1) {
					            ext = s.substring(i+1).toLowerCase();
					        }
					        return ext;
					    }

						public boolean accept(File f) {
					        if (f.isDirectory()) {
					            return true;
					        }
					        String extension = getExtension(f);
					        if (extension != null) {
					            return extension.equals("jar");
					        }
					        return false;
					    }

					    public String getDescription() {
					        return "Just Jar Files";
					    }

					});
		            fc.setAcceptAllFileFilterUsed(false);
					int returnVal = fc.showDialog(PanelSummary.this.getDesignPlatform(),"Ok");
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File[] files = fc.getSelectedFiles();
						String text = additionalLibrariesTF.getText();
						for (int i = 0; i < files.length; i++) {
							if(text.length() != 0)
								text += ";";
							text += files[i].getAbsolutePath();
						}
						additionalLibrariesTF.setText(text);
					}
				}
			});
			gbc.weightx = 0;
			add(browseLibraries, gbc);
			
			
			
			

			gbc.gridx=0;
			gbc.gridy++;
			gbc.gridwidth = 2;
			gbc.weightx = 1;
			gbc.anchor = GridBagConstraints.CENTER;
			generateClassesForCharacterValues = new JCheckBox("Classes for values of factors of type character");
			generateClassesForCharacterValues.setSelected(false);
			add(generateClassesForCharacterValues, gbc);

			gbc.gridy++;
			gbc.weightx = 0;
			go.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					File folder = new File(folderSelected.getText());
					String[] additionalLibraries = null;
					if(additionalLibrariesTF.getText().trim().length() != 0) {
						additionalLibraries = additionalLibrariesTF.getText().split(";");
						for (int i = 0; i < additionalLibraries.length; i++) {
							additionalLibraries[i] = additionalLibraries[i].trim();
//							System.out.println("Additional library "+i+" = "+additionalLibraries[i]);
						}
					}
					new CodeGeneration(folder, experimentFile, experiment.getPlugins(), javaCode, generateClassesForCharacterValues.isSelected(), additionalLibraries);
					DesignPlatform.LAST_DIRECTORY = folder.getParent();
					DesignPlatform.saveCurrentDirectory();
					dialog.setVisible(false);
					if(experimentFile.getName().compareTo("._tmp.xml") == 0)
						experimentFile.delete();
				}
			});
			add(go, gbc);
		}

		class CellRenderer implements ListCellRenderer {

			private MyButton buttonRefForSize = new MyButton("Set Jar File");

			public CellRenderer() { }

			class ListElement extends JPanel  {

				private static final long serialVersionUID = 1L;
				private Plugin plugin;
				private JLabel labelID;
				private JLabel labelPath;

				public ListElement(Plugin pl) {
					super();
					this.plugin = pl;
					labelID = new JLabel(plugin.getId()+":");
					labelID.setBackground(Color.WHITE);
					labelID.setOpaque(true);
					labelPath = plugin.getJarFile() != null ? new JLabel(plugin.getJarFile().getAbsolutePath()) : new JLabel("null");
					labelPath.setOpaque(true);
					if(plugin.getJarFile() != null && plugin.getJarFile().exists()) labelPath.setForeground(Color.GREEN);
					else labelPath.setForeground(Color.RED);

					labelID.setBackground(Color.WHITE);	
					labelPath.setBackground(Color.WHITE);

					setLayout(new BorderLayout());
					add(labelID, BorderLayout.WEST);
					add(labelPath, BorderLayout.CENTER);

					setPreferredSize(new Dimension(getPreferredSize().width, buttonRefForSize.getPreferredSize().height));

					setBackground(Color.WHITE);
				}

				public void setFont(Font f) {
					if(labelID != null && labelPath != null) {
						labelID.setFont(f);
						labelPath.setFont(f);
					} else {
						super.setFont(f);
					}

				}
			}

			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				ListElement listElement = new ListElement((Plugin)value);
				listElement.setFont(list.getFont());
				return listElement;
			}

		}


		class CellButtonRenderer implements ListCellRenderer {

			public CellButtonRenderer() { }

			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				return new MyButton("set Jar File...");
			}

		}

	}

}
