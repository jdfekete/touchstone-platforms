/*   TouchStone run platform is a software to run lab experiments. It is         *
 *   published under the terms of a BSD license (see details below)              *
 *   Author: Caroline Appert (appert@lri.fr)                                     *
 *   Copyright (c) 2010 Caroline Appert and INRIA, France.                       *
 *   TouchStone run platform reuses parts of an early version which were         *
 *   programmed by Jean-Daniel Fekete under the terms of a MIT (X11) Software    *
 *   License (Copyright (C) 2006 Jean-Daniel Fekete and INRIA, France)           *
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
package fr.inria.insitu.touchstone.run;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import fr.inria.insitu.touchstone.run.exp.model.Experiment;
import fr.inria.insitu.touchstone.run.exp.model.ExperimentRemote;
import fr.inria.insitu.touchstone.run.input.AxesEvent;
import fr.inria.insitu.touchstone.run.input.AxesListener;
import fr.inria.insitu.touchstone.run.input.InputManager;
import fr.inria.insitu.touchstone.run.utils.FileSystemModel;

/**
 * Launches a run of an experiment for a participant. 
 * If the script file contains an experiment whose 
 * <code>id</code> is <code>idExperiment</code> 
 * (i.e. <code>&lt;experiment id="idExperiment" name="..."&gt;</code>), 
 * all the information relative to this experiment 
 * will be stored in and retrieved from a directory named <code>idExperiment</code>.
 * <br>
 * All the information consists of:
 * <ul>
 * <li> A tabular file named <code>participants.txt</code> which contains two 
 * columns <i>ID</i> and <i>NAME</i> to indicate the mapping between
 * the id of the participant and its name (the logging files
 * containing only the participant id). For example,
 * <pre>
 * S1	John
 * S2	Smith
 * </pre>
 * This file is read once a
 * script file has been loaded and before running to indicate which
 * participants have already run the experiment.
 * <li> A directory named <code>logs</code> that contains itself two subdirectories :
 * 		<ul>
 * 		<li> A subdirectory named <code>cinematic</code> used to store the file
 * 			containing the cinematic data of this run. This file is named
 * 			<code>cinematic-&lt;year&gt;-&lt;month&gt;-&lt;day&gt;-&lt;hour&gt;-&lt;min&gt;-&lt;sec&gt;-&lt;idParticipant&gt;</code>.
 * 			Its heading is a comment looking like :
 * 			<pre>
 * 			# MSnav:Compare multiscale navigation techniques
 *			# scale:scale ratio
 *			# x:x-coordinate
 *			# y:y-coordinate
 *			# Date: 2006-12-05-15-10-01
 *			# Participant: S1
 * 			</pre>
 * 			and its content is made of one column indicating block number, 
 * 			one column indicating trial number and one column per cinematic
 * 			measure required by the script.  
 * 		<li> A subdirectory named <code>trial</code> used to store the file
 * 			containing the trial data of this run. This file is named
 * 			<code>trial-&lt;year&gt;-&lt;month&gt;-&lt;day&gt;-&lt;hour&gt;-&lt;min&gt;-&lt;sec&gt;-&lt;idParticipant&gt;</code>.
 * 			Its heading is a comment indicating the mapping between
 * 			the id of a factor/measure and its long name, it looks like :
 * 			<pre>
 * 			# MSnav:Compare multiscale navigation techniques
 *			# T:technique
 * 			# ID:ID
 * 			# W:width
 *			# MT:movement time
 * 			# HIT:end trial
 *			# D:distance
 *			# HIT:success
 *			# Date: 2006-12-05-15-10-01
 * 			</pre>
 * 			and its content is made of one column for experiment id,
 * 			one for participant id, one for block number, 
 * 			one for trial number, one per factor and one per trial
 * 			measure required by the script.
 * 		</ul>
 * </ul>
 * 
 * @author Caroline Appert
 *
 */
public class LaunchExperiment implements AxesListener {

	/**
	 * The string in keyboard and mouse JComboBoxes to
	 * indicate standard AWT input event system.
	 */
	public static final String DEFAULT_AWT = "standard AWT";

	private JComboBox comboParticipants = new JComboBox();
	private JTextField nameParticipantSelected = new JTextField();
	private Dialog dialog;
	private JTextField scriptTextField = new JTextField("Choose a script");
	private JButton run;
	private File confFile = new File("input.conf");
	private File experimentfile = null;
	private Experiment experiment = null;
	private JSpinner spinnerBlock, spinnerTrial;
	private String experimentName;

	private JComboBox miceAvailable;
	private JComboBox keyboardsAvailable;

	private JCheckBox oneLogFile;
	private JCheckBox showRemote;
	private JCheckBox installTouchstoneCursor;
	private JCheckBox logCheckbox;

	private JTextArea loggingArea;

	private Dialog logDialog;

	private JTable tableParticipants;
	private JTree treeLogs;

	private String keyboardCmdLine = null;

	/* OSC fields */
	//	private int nbClients = 0;
	private JCheckBox enableOSC = new JCheckBox("enable OSC");
	private JFormattedTextField portPlatformTextField = null;
	private JPanel clientsList = new JPanel();
	private JScrollPane jspClientsList = new JScrollPane();
	private Vector<JTextField> oscHostsClientsTF = new Vector<JTextField>();
	private Vector<JFormattedTextField> oscPortsClientsTF = new Vector<JFormattedTextField>();

	/* Favorites */
	JTextField tfFavorite;
	JComboBox cbFavorites;
	JButton addFavorite, removeFavorite;

	private void saveLaunchConfiguration(File file) {
		Properties configProperties = new Properties();
		configProperties.setProperty("ExperimentScript", experimentfile.getAbsolutePath());
		configProperties.setProperty("ParticipantID", comboParticipants.getSelectedItem().toString());
		configProperties.setProperty("ParticipantName", nameParticipantSelected.getText());
		configProperties.setProperty("StartingBlock", spinnerBlock.getValue().toString());
		configProperties.setProperty("StartingTrial", spinnerTrial.getValue().toString());
		configProperties.setProperty("ShowRemote", ""+showRemote.isSelected());
		configProperties.setProperty("MasterLogFile", ""+oneLogFile.isSelected());

		configProperties.setProperty("OSCEnabled", ""+enableOSC.isSelected());
		configProperties.setProperty("PlatformPort", portPlatformTextField.getText());
		String hosts = "";
		for (Iterator<JTextField> iterator = oscHostsClientsTF.iterator(); iterator.hasNext();) {
			String h = iterator.next().getText();
			hosts += hosts.length() == 0 ? h : (","+h);
		}
		if(hosts.trim().length() > 0) configProperties.setProperty("ClientHosts", hosts);
		String ports = "";
		for (Iterator<JFormattedTextField> iterator = oscPortsClientsTF.iterator(); iterator.hasNext();) {
			Integer i = Integer.parseInt(iterator.next().getText());
			ports += ports.length() == 0 ? ""+i : (","+i);
		}
		if(ports.trim().length() > 0) configProperties.setProperty("ClientPorts", ports);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			configProperties.storeToXML(fos, "myComment");
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void saveLaunchConfiguration() {
		saveLaunchConfiguration(new File(".config-last.xml"));
	}

	private void loadLaunchConfiguration(File configFile) {
		if(!configFile.exists()) return;
		Properties configProperties = new Properties();	
		try {
			configProperties.loadFromXML(new FileInputStream(configFile));
			experimentfile = new File(configProperties.getProperty("ExperimentScript"));
			if(experimentfile.exists()) {
				loadExperimentFile(experimentfile);
				comboParticipants.setSelectedItem(configProperties.getProperty("ParticipantID"));
				nameParticipantSelected.setText(configProperties.getProperty("ParticipantName"));
				spinnerBlock.setValue(Integer.parseInt(configProperties.getProperty("StartingBlock")));
				spinnerTrial.setValue(Integer.parseInt(configProperties.getProperty("StartingTrial")));
			}
			showRemote.setSelected(configProperties.getProperty("ShowRemote").equals("true"));
			oneLogFile.setSelected(configProperties.getProperty("MasterLogFile").equals("true"));

			enableOSC.setSelected(configProperties.getProperty("OSCEnabled").equals("true"));
			portPlatformTextField.setText(configProperties.getProperty("PlatformPort"));
			String hosts = configProperties.getProperty("ClientHosts");
			if(hosts == null) return;
			String ports = configProperties.getProperty("ClientPorts");
			String[] clientHosts = hosts.split(",");
			String[] clientPorts = ports.split(",");
			clientsList.removeAll();
			oscHostsClientsTF.clear();
			oscPortsClientsTF.clear();
			for (int i = 0; i < clientHosts.length; i++) {
				addClientLine(clientHosts[i], Integer.parseInt(clientPorts[i]));
			}
			updateClientsPanel();

		} catch (InvalidPropertiesFormatException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	class BrowseListener implements ActionListener{

		BrowseListener() { }
		/**
		 * {@inheritDoc}
		 */
		public void actionPerformed(ActionEvent arg0) {
			JFileChooser jfc = new JFileChooser(System.getProperty("user.dir"));
			int returnVal = jfc.showOpenDialog(dialog);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				experimentfile = jfc.getSelectedFile();
				scriptTextField.setText(experimentfile.getAbsolutePath());
				jfc.setVisible(false);
				experiment = new Experiment(experimentfile);
				// TODO Show a label to let experimenter know that classpath must include the loaded jars required by the tag pluginRequired
				run.setEnabled(true);
				comboParticipants.removeAllItems();
				Enumeration<String> participantsIDs = experiment.getParticipantsIDs();
				while(participantsIDs.hasMoreElements()) {
					String next = participantsIDs.nextElement();
					int i = 0;
					// sort list of participants ID
					while(i < comboParticipants.getItemCount() && (comboParticipants.getItemAt(i).toString().compareTo(next) < 0 || comboParticipants.getItemAt(i).toString().length() < next.length())) {
						i++;
					}
					comboParticipants.insertItemAt(next, i);
				}
				experimentName = Platform.getInstance().getMeasure(Experiment.MEASURE_EXPERIMENT_NAME).getStringValue();
				File rootDir = new File(experimentName);
				if(!rootDir.exists()) {
					rootDir.mkdirs();
				}
				treeLogs.setModel(new FileSystemModel(rootDir));
				experimentInformation();
				if(comboParticipants.getItemCount() > 0) comboParticipants.setSelectedIndex(0);

				fillPanelOSC(); 
			}
			jfc.setVisible(true);
		}

	}

	class RunListener implements ActionListener {
		RunListener() { }

		/**
		 * {@inheritDoc}
		 */
		public void actionPerformed(ActionEvent arg0) {
			if(nameParticipantSelected.getText().length() != 0) {
				start();
			} else {
				Object[] options = { "OK", "CANCEL" };
				JOptionPane optionPane = new JOptionPane(
						"You have not entered a participant name\n"
						+ "Ignore and go on?",
						JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
						null, options, options[1]);
				JDialog warningDialog = optionPane.createDialog(dialog, "Warning: Participant name empty");
				warningDialog.setVisible(true);
				Object selectedValue = optionPane.getValue();
				if(selectedValue == null)
					return;
				for(int counter = 0, maxCounter = options.length;
				counter < maxCounter; counter++) {
					if(options[counter].equals(selectedValue))
						if(counter == 0) start(); else return;
				}
			}
		}

	}

	// Start: allow to start an experiment directly
	public void loadExperimentFile(String file) {
		loadExperimentFile(new File(file));
	}

	public void loadExperimentFile(File file) {
		experimentfile = file;
		scriptTextField.setText(experimentfile.getAbsolutePath());
		experiment = new Experiment(experimentfile);
		// TODO Show a label to let experimenter know that classpath must include the loaded jars required by the tag pluginRequired
		run.setEnabled(true);
		comboParticipants.removeAllItems();
		Enumeration<String> participantsIDs = experiment.getParticipantsIDs();
		while(participantsIDs.hasMoreElements()) {
			String next = participantsIDs.nextElement();
			int i = 0;
			// sort the list of participants ID
			while(i < comboParticipants.getItemCount() && (comboParticipants.getItemAt(i).toString().compareTo(next) < 0 || comboParticipants.getItemAt(i).toString().length() < next.length())) {
				i++;
			}
			comboParticipants.insertItemAt(next, i);
		}
		experimentName = Platform.getInstance().getMeasure(Experiment.MEASURE_EXPERIMENT_NAME).getStringValue();
		File rootDir = new File(experimentName);
		if(!rootDir.exists()) {
			rootDir.mkdirs();
		}
		treeLogs.setModel(new FileSystemModel(rootDir));
		experimentInformation();
		if(comboParticipants.getItemCount() > 0) comboParticipants.setSelectedIndex(0);
	}

	public void setParticipant(String name) {
		if (experiment == null) return;
		if(name == null) return;
		// the following line has not been tested yet
		if (!comboParticipants.getSelectedItem().toString().equals(name))
			comboParticipants.setSelectedItem(name);
		spinnerBlock.setValue(new Integer(1));
		spinnerBlock.setModel(new SpinnerNumberModel(1, 1, experiment.getNbBlocks(name), 1));
		spinnerTrial.setValue(new Integer(1));
		spinnerTrial.setModel(new SpinnerNumberModel(1, 1, experiment.getNbTrials(name, 1), 1));
	}

	public void setBlock(int block) {
		if (((Integer)spinnerBlock.getValue()).intValue() != block)
			spinnerBlock.setValue(new Integer(block));
		String participant = comboParticipants.getSelectedItem().toString();
		spinnerTrial.setModel(new SpinnerNumberModel(1, 1, experiment.getNbTrials(participant, block), 1));
		spinnerTrial.setValue(new Integer(1));
	}

	public void setTrial(int trial) {
		spinnerTrial.setValue(new Integer(trial));
	}

	public void setLanguage(int language) {
		// back compatibility from the void
		//this.languageList.setSelectedIndex(language);
	}

	public int getLanguage() {
		// back compatibility from the void
		return 1;
		//return this.languageList.getSelectedIndex();
	}

	public void setKeyboard(String k) {
		keyboardCmdLine = k;
	}
	// End: allow to start an experiment directly

	private void startInput() {
		InputManager.getInstance().removeAxesListener(this);
		dialog.setVisible(false);
		Platform.getInstance().installGeneralizedInput(confFile, 
				miceAvailable.getSelectedItem().toString(),
				(keyboardCmdLine== null)?
						keyboardsAvailable.getSelectedItem().toString() :
							keyboardCmdLine);
		//		TODO ?
		//		if(installTouchstoneCursor.isSelected()) Platform.getInstance().installCursor();
		registerNewParticipant(comboParticipants.getSelectedItem().toString(), nameParticipantSelected.getText());
		Platform platform = Platform.getInstance();
		platform.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	public void start() {
		startInput();
		Platform.getInstance().validate();
		int trial = ((Integer)(spinnerTrial.getValue())).intValue();
		int block = ((Integer)(spinnerBlock.getValue())).intValue();

		experiment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(arg0.getActionCommand().compareTo(Experiment.EXPERIMENT_FINISHED) == 0) {
					Platform.getInstance().setVisible(false);
					if(oneLogFile.isSelected()) {
						generateOneLogFile();
					} else {
						GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
						GraphicsDevice dev = genv.getDefaultScreenDevice();
						dev.setFullScreenWindow(null);
						JOptionPane.showMessageDialog(dialog, "Experiment finished. Thank you very much for your participation.");
						System.exit(0);
					}
				}
			}
		});

		Platform.getInstance().disableOSC();
		if(enableOSC.isSelected()) {
			int portPlatform = Integer.parseInt(""+portPlatformTextField.getValue());//(Integer)portPlatformTextField.getValue();
			Platform.getInstance().enableOSC(portPlatform);
			for (int i = 0; i < oscHostsClientsTF.size(); i++) {
				String host = oscHostsClientsTF.get(i).getText();
				int port = Integer.parseInt(""+oscPortsClientsTF.get(i).getValue());
				Platform.getInstance().addOSCClient(host, port);
			}
		}

		saveLaunchConfiguration();

		experiment.start((String)comboParticipants.getSelectedItem(), block, trial);
		if(showRemote.isSelected())
			new ExperimentRemote(experiment);


	}

	void registerNewParticipant(String id, String name) {
		BufferedReader br = null;
		PrintWriter pw = null;
		String line;
		File rootDir = new File(experimentName);
		if(!rootDir.exists()) rootDir.mkdirs();
		File inFile = new File(rootDir+File.separator+"participants.txt");
		File outFile = new File(rootDir+File.separator+"participants.tmp");
		try {
			pw = new PrintWriter(outFile);
			pw.write(id+":"+name+"\n");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			if(inFile.exists()) {
				br = new BufferedReader(new FileReader(inFile));
				line = br.readLine();
				while(line != null) {
					pw.write(line+"\n");
					line = br.readLine();
				}
				br.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		pw.close();
		outFile.renameTo(inFile);
	}

	private void generateOneLogFile() {
		File rootDir = new File(experimentName+File.separator+"logs"+File.separator+"trial");
		concat(rootDir, "log");
		rootDir = new File(experimentName+File.separator+"logs"+File.separator+"cinematic");
		concat(rootDir, "cinematic");
	}

	private static void concat(File rootDir, String prefix) {
		BufferedReader br = null;
		PrintWriter pw = null;
		String line;
		if(!rootDir.exists()) return;
		File outFile = new File(rootDir.getAbsolutePath()+File.separator+"allLogs.log");
		boolean first = true;
		try {
			pw = new PrintWriter(outFile);
			File[] listOfLogFiles = rootDir.listFiles();
			for(int i = 0; i < listOfLogFiles.length; i++) {
				if(listOfLogFiles[i].getName().trim().startsWith(prefix+"-")) {
					br = new BufferedReader(new FileReader(listOfLogFiles[i]));
					line = br.readLine();
					int lineNumber = 1;
					while(line != null) {
						if(line.trim().length() >= 1 && line.trim().charAt(0) != '#') {
							if((!first && lineNumber > 2) || (first && lineNumber != 2))
								pw.write(line+"\n");
							lineNumber++;
						}
						line = br.readLine();
					}
					br.close();
					first = false;
				}
			}
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void experimentInformation() {
		String[] splitLine;

		Properties props = new Properties();
		InputStream in;
		try {
			in = new FileInputStream(experimentName+File.separator+"participants.txt");
			if (in != null) {
				props.load(in);
			}
		} catch (FileNotFoundException e1) {
			;
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(props.isEmpty()) return;
		int cpt = 0;
		Object[] columnNames = {"ID","name"};
		tableParticipants.setAutoCreateColumnsFromModel(true);
		try {
			BufferedReader br = new BufferedReader(new FileReader(experimentName+File.separator+"participants.txt"));
			int nbLines = 0;
			String line = br.readLine();
			while(line != null) {
				if(line.split(":").length == 2) nbLines++;
				line = br.readLine();
			}
			br.close();
			tableParticipants.setShowGrid(true);
			tableParticipants.setModel(new DefaultTableModel(columnNames, nbLines));
			br = new BufferedReader(new FileReader(experimentName+File.separator+"participants.txt"));
			line = br.readLine();
			while(line != null) {
				splitLine = line.split(":");
				if(splitLine.length == 2) {
					tableParticipants.getModel().setValueAt(splitLine[0], cpt, 0);
					tableParticipants.getModel().setValueAt(splitLine[1], cpt, 1);
					cpt++;
				}
				line = br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Builds an application to run an experiment.
	 * @param pluginJars an array containing the names of the plugin classes
	 */
	public LaunchExperiment(String[] pluginJars) {
		if (pluginJars.length != 0) {
			for (int i = 0; i < pluginJars.length; i++) {
				String[] jars = pluginJars[i].split(File.pathSeparator);
				//				System.out.println("Found "+jars.length+" items is path");
				for (int j = 0; j < jars.length; j++)
					try {
						Platform.getInstance().addPluginJar(jars[j]);
					}
				catch(Exception e) {
					System.err.println("Plugin class "+jars[i]+" cannot be registered in Platform");
					e.printStackTrace();
				}
			}
		}
		WindowAdapter confirm = new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (JOptionPane.showConfirmDialog(Platform.getInstance(), "Really Exit?")==JOptionPane.OK_OPTION) {
					System.exit(0);
				}
			}
		};
		Platform.getInstance();
		dialog = new Dialog(Platform.getInstance(), "Run an experiment");
		dialog.addWindowListener(confirm);
		dialog.setBackground(Color.WHITE);

		JTabbedPane tabbedPane = new JTabbedPane();

		JPanel panelRun = new JPanel();
		panelRun.setLayout(new BoxLayout(panelRun, BoxLayout.Y_AXIS));
		tabbedPane.addTab("Run", null, panelRun,
		"Run an experiment");
		JPanel panelSummary = new JPanel();
		panelSummary.setLayout(new GridLayout(1, 2));
		tabbedPane.addTab("Summary", null, panelSummary,
		"Summary of the experiment");
		JPanel panelInput = new JPanel();
		panelInput.setLayout(new GridLayout(1, 2));
		tabbedPane.addTab("Input", null, panelInput,
		"Input configuration");
		JPanel panelOSC = new JPanel();
		panelOSC.setLayout(new BorderLayout());
		tabbedPane.addTab("OSC", null, panelOSC,
		"OSC parameters");


		layoutPanelRun(panelRun);
		layoutPanelSummary(panelSummary);
		layoutPanelInput(panelInput);
		layoutPanelOSC(panelOSC);

		Platform.getInstance().installGeneralizedInput(confFile);

		dialog.add(tabbedPane);

		dialog.pack();
		dialog.setVisible(true);

		File here = new File(".");
		File[] files = here.listFiles();
		for (int i = 0; i < files.length; i++) {
			if(files[i].getName().startsWith(".config-")) {
				if(!files[i].getName().equals(".config-last.xml"))
					cbFavorites.addItem(files[i].getName().substring(8));
			}
		}
		cbFavorites.insertItemAt("last run", 0);
		cbFavorites.setSelectedIndex(0);

		loadLaunchConfiguration(new File(".config-last.xml"));

	}

	/**
	 * Main program.
	 * @param args program arguments
	 */
	public static void main(String[] args) {
		// TO REMOVE
//		System.setProperty("apple.laf.useScreenMenuBar", "true");
//		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Undo Experiment");
//		try {
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		System.setProperty("apple.awt.fakefullscreen", "true");
		// TO REMOVE
		
		new LaunchExperiment(args);
	}

	private void layoutPanelRun(JPanel main) {
		JPanel scriptPanel = new JPanel();
		scriptPanel.setBorder(BorderFactory.createTitledBorder("Experiment script"));
		scriptPanel.setLayout(new BoxLayout(scriptPanel, BoxLayout.X_AXIS));
		scriptTextField.setEditable(false);
		scriptPanel.add(scriptTextField);
		JButton browseButton = new JButton("Browse...");
		browseButton.addActionListener(new BrowseListener());
		scriptPanel.add(browseButton);

		JPanel participantPanel = new JPanel();
		participantPanel.setBorder(BorderFactory.createTitledBorder("Participant"));
		GridLayout gLayout = new GridLayout(2, 2, 3, 3);
		participantPanel.setLayout(gLayout);
		participantPanel.add(new JLabel("ID"));
		participantPanel.add(new JLabel("Name"));
		comboParticipants = new JComboBox();
		participantPanel.add(comboParticipants);
		nameParticipantSelected = new JTextField();
		participantPanel.add(nameParticipantSelected);

		comboParticipants.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(experiment == null) return;
				String participantSelected =  e.getItem().toString();
				if(participantSelected == null) return;
				spinnerBlock.setValue(new Integer(1));
				spinnerBlock.setModel(new SpinnerNumberModel(1, 1, experiment.getNbBlocks(participantSelected), 1));
				spinnerTrial.setValue(new Integer(1));
				spinnerTrial.setModel(new SpinnerNumberModel(1, 1, experiment.getNbTrials(participantSelected, 1), 1));
			}
		});

		JPanel startPointPanel = new JPanel();
		startPointPanel.setBorder(BorderFactory.createTitledBorder("Starting point"));
		GridLayout glStartPointPanel = new GridLayout(2, 2, 3, 3);
		startPointPanel.setLayout(glStartPointPanel);
		startPointPanel.add(new JLabel("Block"));
		startPointPanel.add(new JLabel("Trial"));
		spinnerBlock = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
		spinnerBlock.setEditor(new JSpinner.NumberEditor(spinnerBlock));
		startPointPanel.add(spinnerBlock);
		spinnerTrial = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
		spinnerTrial.setEditor(new JSpinner.NumberEditor(spinnerTrial));
		startPointPanel.add(spinnerTrial);

		spinnerBlock.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(comboParticipants.getSelectedItem() == null) return;
				String participant = comboParticipants.getSelectedItem().toString();
				int blockSelected = ((Integer)spinnerBlock.getValue()).intValue();
				spinnerTrial.setModel(new SpinnerNumberModel(1, 1, experiment.getNbTrials(participant, blockSelected), 1));
				spinnerTrial.setValue(new Integer(1));
			}
		});

		JPanel startPanel = new JPanel(new GridLayout(1, 2, 3, 3));
		startPanel.add(participantPanel);
		startPanel.add(startPointPanel);

		JPanel runPanel = new JPanel();
		runPanel.setBorder(BorderFactory.createTitledBorder("Run"));
		runPanel.setLayout(new BoxLayout(runPanel, BoxLayout.Y_AXIS));
		oneLogFile = new JCheckBox("Generate a master log file");
		oneLogFile.setAlignmentX(Component.CENTER_ALIGNMENT);
		runPanel.add(oneLogFile);

		showRemote = new JCheckBox("Show remote");
		showRemote.setAlignmentX(Component.CENTER_ALIGNMENT);
		runPanel.add(showRemote);

		run = new JButton("RUN!");
		run.setEnabled(false);
		run.setAlignmentX(Component.CENTER_ALIGNMENT);
		runPanel.add(run);

		run.addActionListener(new RunListener());

		JPanel favoritesPanel = new JPanel();
		favoritesPanel.setBorder(BorderFactory.createTitledBorder("Favorites"));
		GridBagLayout gblFavorites = new GridBagLayout();
		GridBagConstraints gbcFavorites = new GridBagConstraints();
		gbcFavorites.gridx = 0;
		gbcFavorites.gridy = 0;
		gbcFavorites.weightx = 0.5;
		gbcFavorites.fill = GridBagConstraints.BOTH;
		favoritesPanel.setLayout(gblFavorites);
		cbFavorites = new JComboBox();
		cbFavorites.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getItem().toString().equals("last run")) {
					loadLaunchConfiguration(new File(".config-last.xml"));
					tfFavorite.setText("");
					removeFavorite.setEnabled(false);
					return;
				}
				File launchConfigToLoad = new File(".config-"+e.getItem().toString());
				if(!launchConfigToLoad.exists()) return;
				tfFavorite.setText(e.getItem().toString());
				removeFavorite.setEnabled(true);
				loadLaunchConfiguration(launchConfigToLoad);
			}
		});
		favoritesPanel.add(cbFavorites, gbcFavorites);
		gbcFavorites.gridx++;
		tfFavorite = new JTextField();
		tfFavorite.getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent e) {
				addFavorite.setEnabled(tfFavorite.getText().trim().length() > 0);
			}
			public void insertUpdate(DocumentEvent e) {
				addFavorite.setEnabled(tfFavorite.getText().trim().length() > 0);
			}
			public void changedUpdate(DocumentEvent e) {
				addFavorite.setEnabled(tfFavorite.getText().trim().length() > 0);
			}
		});
		favoritesPanel.add(tfFavorite, gbcFavorites);
		gbcFavorites.gridx++;
		gbcFavorites.weightx = 0;
		addFavorite = new JButton("+");
		addFavorite.setEnabled(false);
		addFavorite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveLaunchConfiguration(new File(".config-"+tfFavorite.getText()));
				cbFavorites.insertItemAt(tfFavorite.getText(), 0);
			}
		});
		favoritesPanel.add(addFavorite, gbcFavorites);
		gbcFavorites.gridx++;
		removeFavorite = new JButton("-");
		removeFavorite.setEnabled(false);
		removeFavorite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File fileToDelete = new File(".config-"+tfFavorite.getText());
				if(fileToDelete.delete()) {
					cbFavorites.removeItem(tfFavorite.getText());
				}
			}
		});
		favoritesPanel.add(removeFavorite, gbcFavorites);
		
		GridBagLayout gblMain = new GridBagLayout();
		GridBagConstraints gbcMain = new GridBagConstraints();
		gbcMain.gridx = 0;
		gbcMain.gridy = 0;
		gbcMain.weighty = 0;
		gbcMain.weightx = 1;
		gbcMain.fill = GridBagConstraints.BOTH;
		main.setLayout (gblMain);
		main.add(scriptPanel, gbcMain);
		gbcMain.gridy++;
		main.add(startPanel, gbcMain);
		gbcMain.gridy++;
		gbcMain.weighty = 1;
		main.add(runPanel, gbcMain);
		gbcMain.gridy++;
		gbcMain.weighty = 0;
		main.add(favoritesPanel, gbcMain);
	}

	private void layoutPanelSummary(JPanel main) {
		tableParticipants = new JTable();
		JPanel tablePanel = new JPanel();
		tablePanel.setBorder(BorderFactory.createTitledBorder("Runs already completed"));
		JScrollPane tableParticipantsSP = new JScrollPane(tableParticipants);
		tableParticipants.setPreferredSize(new Dimension(300, 300));
		tablePanel.add(tableParticipantsSP);

		treeLogs = new JTree();
		treeLogs.setModel(null);
		JPanel treePanel = new JPanel(new BorderLayout());
		treePanel.setBorder(BorderFactory.createTitledBorder("Log files already recorded"));
		JScrollPane treeSP = new JScrollPane(treeLogs);
		treeLogs.setPreferredSize(new Dimension(300, 300));
		treePanel.add(treeSP, BorderLayout.CENTER);
		JButton generateMasterLogFile = new JButton("Master log file for analysis");
		generateMasterLogFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				generateOneLogFile();
			}
		});
		treePanel.add(generateMasterLogFile, BorderLayout.SOUTH);
		main.setLayout(new GridLayout(1, 2, 3, 3));
		main.add(tablePanel);
		main.add(treePanel);
	}

	void layoutPanelInput(JPanel main) {

		JPanel physicalAxes = new JPanel();
		physicalAxes.setLayout(new BoxLayout(physicalAxes, BoxLayout.Y_AXIS));
		physicalAxes.setBorder(BorderFactory.createTitledBorder("Physical axes"));

		JTextArea physicalAxesTA = new JTextArea();
		ArrayList<String> listOfAxes = InputManager.getInstance().getAvailablePhysicalAxes();
		for(Iterator<String> i = listOfAxes.iterator(); i.hasNext(); ) {
			physicalAxesTA.append(i.next());
			if(i.hasNext()) physicalAxesTA.append("\n");
		}
		InputManager inputManager = InputManager.getInstance();
		ArrayList<String> mice = inputManager.getMice();
		miceAvailable = new JComboBox();
		miceAvailable.addItem(LaunchExperiment.DEFAULT_AWT);
		for(Iterator<String> i = mice.iterator(); i.hasNext(); )
			miceAvailable.addItem(i.next());

		ArrayList<String> keyboards = inputManager.getKeyboards();
		keyboardsAvailable = new JComboBox();
		keyboardsAvailable.addItem(LaunchExperiment.DEFAULT_AWT);
		for(Iterator<String> i = keyboards.iterator(); i.hasNext(); )
			keyboardsAvailable.addItem(i.next());


		// mouse and keyboard panel
		JPanel mouseAndKeyboard = new JPanel(new GridLayout(4, 2, 3, 3));
		mouseAndKeyboard.add(new JLabel("Default mouse: "));
		mouseAndKeyboard.add(miceAvailable);
		mouseAndKeyboard.add(new JLabel("Default keyboard: "));
		mouseAndKeyboard.add(keyboardsAvailable);
		JCheckBox enableJInput = new JCheckBox("enable jInput");
		enableJInput.setSelected(Platform.JINPUT_ON);
		enableJInput.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) Platform.getInstance().enableJInput();
				else Platform.getInstance().disableJInput();

				ArrayList<String> mice = InputManager.getInstance().getMice();
				miceAvailable.removeAllItems();
				miceAvailable.addItem(LaunchExperiment.DEFAULT_AWT);
				for(Iterator<String> i = mice.iterator(); i.hasNext(); )
					miceAvailable.addItem(i.next());

				ArrayList<String> keyboards = InputManager.getInstance().getKeyboards();
				keyboardsAvailable.removeAllItems();
				keyboardsAvailable.addItem(LaunchExperiment.DEFAULT_AWT);
				for(Iterator<String> i = keyboards.iterator(); i.hasNext(); )
					keyboardsAvailable.addItem(i.next());
			}
		});
		mouseAndKeyboard.add(enableJInput);
		installTouchstoneCursor = new JCheckBox("Touchstone cursor");
		installTouchstoneCursor.setSelected(false);
		mouseAndKeyboard.add(installTouchstoneCursor);

		miceAvailable.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				String m = e.getItem().toString();
				if(m == null) return;
				if(m.compareTo(DEFAULT_AWT) != 0) {
					InputManager.getInstance().setDefaultMouse(m);
				}
				reinitInputConfiguration(confFile);
			}
		});
		keyboardsAvailable.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				String m = e.getItem().toString();
				if(m == null) return;
				if(m.compareTo(DEFAULT_AWT) != 0) {
					InputManager.getInstance().setDefaultKeyboard(m);
				}
				reinitInputConfiguration(confFile);
			}
		});

		JScrollPane physicalAxesSP = new JScrollPane(physicalAxesTA);
		physicalAxes.setPreferredSize(new Dimension(300, 300));
		physicalAxes.add(physicalAxesSP);
		physicalAxes.setAlignmentX(Component.LEFT_ALIGNMENT);
		physicalAxes.add(mouseAndKeyboard);
		physicalAxes.setAlignmentX(Component.LEFT_ALIGNMENT);

		JPanel virtualAxes = new JPanel();
		virtualAxes.setLayout(new BorderLayout());
		virtualAxes.setBorder(BorderFactory.createTitledBorder("Virtual axes"));

		logDialog = new Dialog(dialog);
		logCheckbox = new JCheckBox("show axes and their values");
		logCheckbox.setSelected(false);
		logCheckbox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				logDialog.setVisible(e.getStateChange() == ItemEvent.SELECTED);
				if(e.getStateChange() == ItemEvent.SELECTED)
					Platform.getInstance().registerComponent(loggingArea);
				reinitInputConfiguration(confFile);
			}
		});

		loggingArea = new JTextArea();
		loggingArea.setEditable(false);
		JScrollPane scrollLoggingArea = new JScrollPane(loggingArea);
		scrollLoggingArea.setPreferredSize(new Dimension(300, 300));
		loggingArea.setLayout(new BorderLayout());
		loggingArea.setBorder(BorderFactory.createTitledBorder("Log axes"));
		logDialog.add(scrollLoggingArea);
		logDialog.pack();

		confFile = new File("input.conf");
		final JTextArea virtualAxesTA = new JTextArea();
		loadInputConfigurationFile(virtualAxesTA);

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 3));
		JButton open = new JButton("Open...");
		buttons.add(open);
		JButton save = new JButton("Save");
		buttons.add(save);
		JButton saveas = new JButton("Save as...");
		buttons.add(saveas);

		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser(System.getProperty("user.dir"));
				int returnVal = jfc.showOpenDialog(dialog);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					confFile = jfc.getSelectedFile();
					loadInputConfigurationFile(virtualAxesTA);
					jfc.setVisible(false);
				} else {
					jfc.setVisible(false);
				}
			}
		});
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveInputConfigurationFile(confFile, virtualAxesTA);
				reinitInputConfiguration(confFile);
			}
		});
		saveas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser(System.getProperty("user.dir"));
				int returnVal = jfc.showSaveDialog(dialog);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					saveInputConfigurationFile(jfc.getSelectedFile(), virtualAxesTA);
					jfc.setVisible(false);
					reinitInputConfiguration(confFile);
				} else {
					jfc.setVisible(false);
				}
			}
		});
		buttons.add(open);
		buttons.add(save);
		buttons.add(saveas);

		JScrollPane virtualAxesSP = new JScrollPane(virtualAxesTA);
		virtualAxes.setPreferredSize(new Dimension(300, 300));
		virtualAxes.add(virtualAxesSP, BorderLayout.CENTER);
		virtualAxes.setAlignmentX(Component.CENTER_ALIGNMENT);
		virtualAxes.add(buttons, BorderLayout.SOUTH);

		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		main.setLayout(gbl);
		buildConstraints(constraints, 0, 0, 1, 1, 50, 99, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
		main.add(physicalAxes, constraints);
		buildConstraints(constraints, 1, 0, 1, 1, 50, 99, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
		main.add(virtualAxes, constraints);
		buildConstraints(constraints, 0, 1, 2, 1, 100, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
		main.add(logCheckbox, constraints);

	}

	void fillPanelOSC() {
		if(experiment == null) return;
		enableOSC.setSelected(experiment.isOSCEnabled());
		portPlatformTextField.setValue(experiment.getOSCPortPlatform());
		Vector<String> oscHostsClients = experiment.getOSCHostsClients();
		Vector<Integer> oscPortsClients = experiment.getOSCPortsClients();
		clientsList.removeAll();
		oscHostsClientsTF.clear();
		oscPortsClientsTF.clear();
		for (int i = 0; i < oscHostsClients.size(); i++) {
			addClientLine(oscHostsClients.get(i), oscPortsClients.get(i));
		}
	}

	void layoutPanelOSC(JPanel main) {
		main.add(enableOSC, BorderLayout.NORTH);

		GridBagConstraints gbc = new GridBagConstraints();
		JPanel hostsAndPorts = new JPanel();
		hostsAndPorts.setLayout(new GridBagLayout());
		main.add(hostsAndPorts, BorderLayout.CENTER);

		// platform panel
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.weighty = 0;
		JPanel platformPanel = new JPanel();
		TitledBorder border = BorderFactory.createTitledBorder("Platform");
		platformPanel.setBorder(border);
		hostsAndPorts.add(platformPanel, gbc);
		platformPanel.setLayout(new GridLayout(1, 2));
		JLabel portLabel = new JLabel("Port");
		NumberFormat nf = NumberFormat.getIntegerInstance();
		nf.setGroupingUsed(false);
		if(portPlatformTextField == null) portPlatformTextField = new JFormattedTextField(nf);
		portPlatformTextField.setValue(Platform.DEFAULT_OSC_PORT);
		platformPanel.add(portLabel);
		platformPanel.add(portPlatformTextField);

		// client apps panel
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 1;
		JPanel clientsPanel = new JPanel();
		border = BorderFactory.createTitledBorder("Clients");
		clientsPanel.setBorder(border);
		hostsAndPorts.add(clientsPanel, gbc);

		GridBagConstraints gbcClients = new GridBagConstraints();
		clientsPanel.setLayout(new GridBagLayout());
		gbcClients.gridx = 0;
		gbcClients.gridy = 0;
		gbcClients.fill = GridBagConstraints.BOTH;
		gbcClients.weightx = 1;
		gbcClients.weighty = 1;
		jspClientsList.setViewportView(clientsList);
		clientsPanel.add(jspClientsList, gbcClients);
		gbcClients.gridx = 0;
		gbcClients.gridy = 1;
		gbcClients.fill = GridBagConstraints.HORIZONTAL;
		gbcClients.weightx = 1;
		gbcClients.weighty = 0;
		JButton addClient = new JButton("Add client");
		addClient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addClientLine("", -1);
			}
		});
		clientsPanel.add(addClient, gbcClients);

		clientsList.setLayout(new GridBagLayout());
		addClientLine("", -1);
	}

	private void addClientLine(String hostValue, int portValue) {
		oscHostsClientsTF.add(new JTextField(hostValue));
		oscPortsClientsTF.add(new JFormattedTextField(""+portValue));
		updateClientsPanel();
	}

	private void updateClientsPanel() {
		clientsList.removeAll();
		clientsList = new JPanel();
		clientsList.setBackground(Color.WHITE);
		clientsList.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		for (int i = 0; i < oscHostsClientsTF.size(); i++) {
			final JTextField tfHost = oscHostsClientsTF.get(i);
			final JFormattedTextField tfPort = oscPortsClientsTF.get(i);

			final JPanel res = new JPanel();
			TitledBorder border = BorderFactory.createTitledBorder("Client");
			res.setBorder(border);
			res.setLayout(new GridLayout(2, 2));

			JLabel hostLabel = new JLabel("Host");
			res.add(hostLabel);
			res.add(tfHost);

			JLabel portLabel = new JLabel("Port");
			NumberFormat nf = NumberFormat.getIntegerInstance();
			nf.setGroupingUsed(false);
			res.add(portLabel);
			res.add(tfPort);

			c.weighty = 0.0;
			c.weightx = 1.0;
			c.gridx = 1;
			c.gridy = i;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.anchor = GridBagConstraints.NORTH;
			clientsList.add(res, c);

			JButton removeClient = new JButton(" - ");
			removeClient.setBorder(BorderFactory.createRaisedBevelBorder());
			removeClient.setBackground(new Color(230, 230, 230));
			removeClient.setOpaque(true);
			removeClient.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					oscHostsClientsTF.remove(tfHost);
					oscPortsClientsTF.remove(tfPort);
					updateClientsPanel();
				}
			});

			c.weightx = 0.0;
			c.gridx = 0;
			c.anchor = GridBagConstraints.CENTER;
			c.fill = GridBagConstraints.NONE;
			c.insets = new Insets(20, 0, 0, 0);
			clientsList.add(removeClient, c);
		}

		jspClientsList.setViewportView(clientsList);
		clientsList.revalidate();
		jspClientsList.isValidateRoot();
	}


	/**
	 * {@inheritDoc}
	 */
	public void axesChanged(AxesEvent e) {
		for(Iterator<String> i = e.axesModifiedIterator(); i.hasNext(); ) {
			String axisModified = i.next();
			loggingArea.setText(loggingArea.getText()+"\nAxis modified: "+axisModified+"\n\tvalue: "+e.getAxisValue(axisModified));
		}
	}

	static void buildConstraints(GridBagConstraints constraints, int x, int y, int w, int h, double columnExpand, double rowExpand, int componentFill, int componentAnchor) {
		constraints.gridx = x;
		constraints.gridy = y;
		constraints.gridwidth = w;
		constraints.gridheight = h;
		constraints.weightx = columnExpand;
		constraints.weighty = rowExpand;
		constraints.fill = componentFill;
		constraints.anchor = componentAnchor;
	}

	void loadInputConfigurationFile(JTextArea virtualAxes) {
		virtualAxes.setText("");
		try {
			BufferedReader br = new BufferedReader(new FileReader(confFile));
			String line = br.readLine();
			while(line != null) {
				virtualAxes.append(line);
				line = br.readLine();
				if(line != null) virtualAxes.append("\n");
			}
			br.close();
			reinitInputConfiguration(confFile);
		} catch (FileNotFoundException e1) {
			System.err.println("No configuration file yet");
			//e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(Exception ignoreInputException) {
			;
		}
	}

	void reinitInputConfiguration(File file) {
		if(miceAvailable.getSelectedItem() == null
				|| keyboardsAvailable.getSelectedItem() == null) return;
		Platform.getInstance().removeAxesListener(LaunchExperiment.this);
		InputManager.getInstance().stop();
		Platform.getInstance().installGeneralizedInput(file, 
				miceAvailable.getSelectedItem().toString(),
				keyboardsAvailable.getSelectedItem().toString());
		if(logCheckbox.isSelected())
			Platform.getInstance().addAxesListener(LaunchExperiment.this);
		InputManager.getInstance().start();
	}

	void saveInputConfigurationFile(File saveFile, JTextArea virtualAxes) {
		try {
			BufferedWriter br = new BufferedWriter(new FileWriter(saveFile));
			br.write(virtualAxes.getText());
			br.close();
			reinitInputConfiguration(saveFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
