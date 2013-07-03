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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import fr.inria.insitu.touchstone.design.motor.Blocking;
import fr.inria.insitu.touchstone.design.motor.Experiment;
import fr.inria.insitu.touchstone.design.motor.FactorSet;
import fr.inria.insitu.touchstone.design.motor.MeasureSet;
import fr.inria.insitu.touchstone.design.motor.Ordering;
import fr.inria.insitu.touchstone.design.motor.Practice;
import fr.inria.insitu.touchstone.design.motor.Step;
import fr.inria.insitu.touchstone.design.motor.Timing;
import fr.inria.insitu.touchstone.design.web.LinkListener;


@SuppressWarnings("unchecked")
public class DesignPlatform extends JFrame {
	
	private static final long serialVersionUID = 42L;
	
	private JTabbedPane tabs = new JTabbedPane();
	private Experiment experiment = null;
	private int previousIndex = 0;
	private Tab currentTab;
	private DefaultComboBoxModel snapshots;
	private JButton addSnapshot;
	private JButton deleteSnapshot;
	private ExperimentPreview experimentPreview;
	
	private ArrayList<StepPanel<? extends Step>> steps = new ArrayList<StepPanel<? extends Step>>(); 
	
	public static String LAST_DIRECTORY = ".";
	
	public DesignPlatform() {
		super("TouchStone - Design platform");
		
		File lastDir = new File("_last_dir.tmp");
		if(lastDir.exists()) {
			try {
				LAST_DIRECTORY = (new DataInputStream(new FileInputStream(lastDir))).readUTF();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		experiment = new Experiment(this);
		experiment.init();
		experimentPreview = new ExperimentPreview(this);
		
		LinkListener listener = new LinkListener();
		
		try{
			PanelExperiment pe = new PanelExperiment(this, experiment,tabs.getTabCount());
			steps.add(pe);
			currentTab = new Tab<Step>(pe,"Step 1: Experiment set up");
			currentTab.setHelp((new File("help"+File.separator+"setup.html")).toURI().toURL());
			currentTab.setHyperLinkListener(listener);
			tabs.addTab("1. Setup", currentTab);
			
			PanelFactors pf = new PanelFactors(this, experiment,tabs.getTabCount());
			steps.add(pf);
			Tab<FactorSet> tabFactors = new Tab<FactorSet>(pf,"Step 2: Choose factors and values");
			tabFactors.setHelp((new File("help"+File.separator+"factors.html")).toURI().toURL());
			tabs.addTab("2. Factors", tabFactors);
			tabFactors.setHyperLinkListener(listener);
			
			PanelBlocking pb = new PanelBlocking(this, experiment,tabs.getTabCount());
			steps.add(pb);
			Tab<Blocking> tabBlocking = new Tab<Blocking>(pb,"Step 3: Choose blocks of trials and subjects");
			tabBlocking.setHelp((new File("help"+File.separator+"blocking.html")).toURI().toURL());
			tabs.addTab("3. Blocking", tabBlocking);
			tabBlocking.setHyperLinkListener(listener);

			PanelOrdering po = new PanelOrdering(this, experiment,tabs.getTabCount());
			steps.add(po);
			Tab<Ordering> tabOrdering = new Tab<Ordering>(po,"Step 4: Choose counter-balancing order");
			tabOrdering.setHelp((new File("help"+File.separator+"ordering.html")).toURI().toURL());
			tabs.addTab("4. Ordering", tabOrdering);
			tabOrdering.setHyperLinkListener(listener);
			
			PanelPractice pp = new PanelPractice(this, experiment,tabs.getTabCount());
			steps.add(pp);
			Tab<Practice> tabPractice = new Tab<Practice>(pp,"Step 5: Choose practice blocks");
			tabPractice.setHelp((new File("help"+File.separator+"practice.html")).toURI().toURL());
			tabs.addTab("5. Practice", tabPractice);
			tabPractice.setHyperLinkListener(listener);
			
			PanelTiming pt = new PanelTiming(this, experiment,tabs.getTabCount());
			steps.add(pt);
			Tab<Timing> tabTiming = new Tab<Timing>(pt,"Step 6: Choose experiment events and timing ");
			tabTiming.setHelp((new File("help"+File.separator+"timing.html")).toURI().toURL());
			tabs.addTab("6. Timing", tabTiming);
			tabTiming.setHyperLinkListener(listener);
			
			PanelMeasures pm = new PanelMeasures(this, experiment,tabs.getTabCount());
			steps.add(pm);
			Tab<MeasureSet> tabMeasures = new Tab<MeasureSet>(pm,"Step 7: Choose measures and log type");
			tabMeasures.setHelp((new File("help"+File.separator+"measures.html")).toURI().toURL());
			tabs.addTab("7. Measures", tabMeasures);
			tabMeasures.setHyperLinkListener(listener);
			
			PanelSummary ps = new PanelSummary(this, experiment,tabs.getTabCount());
			steps.add(ps);
			Tab<Step> tabSummary = new Tab<Step>(ps,"Step 8: Generate script, summary and code");
			tabSummary.setHelp((new File("help"+File.separator+"summary.html")).toURI().toURL());
			tabs.addTab("8. Summary", tabSummary);
			tabSummary.setHyperLinkListener(listener);
			
			((Tab)tabs.getComponentAt(0)).display();
		}catch (Exception e){
			e.printStackTrace();
		};
		
		// navigation using tabs / ensure saving of current step
		tabs.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e) {
				int selectedIndex = tabs.indexAtLocation(e.getX(), e.getY());
				navigateToTab(previousIndex, selectedIndex);
				previousIndex = selectedIndex;
				if (selectedIndex!=-1) currentTab = (Tab)tabs.getComponentAt(selectedIndex);
			}
		});
		
		JPanel snapshotsPanel = getSnapshotsPanel();
		menuBar();
		
		setLayout(new BorderLayout());
		add(snapshotsPanel, BorderLayout.NORTH);
		add(tabs, BorderLayout.CENTER);
		setSize(new Dimension(1200,800));
		
		pack();
		setVisible(true);
	}
	
	private void navigateToTab(int tabFrom, int tabTo) {
		if(!((tabFrom >= 0 && tabFrom <= 7) && (tabTo >= 0 && tabTo <= 7)))
			return;
		try {
			if(tabFrom < tabTo) {
				for (int i = tabFrom; i < tabTo; i++) {
					((Tab)tabs.getComponentAt(i)).save();
					((Tab)tabs.getComponentAt(i+1)).display();
				}
			} else {
				((Tab)tabs.getComponentAt(tabFrom)).save();
				((Tab)tabs.getComponentAt(tabTo)).display();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected JComboBox comboSnapshots;
	protected ActionListener comboSnapShotsListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().compareTo("comboBoxChanged") == 0
					&& snapshots.getSelectedItem() != null) {
				if(snapshots.getSelectedItem() instanceof Experiment) {
					try {
						setExperiment(((Experiment)snapshots.getSelectedItem()).snapshot());
					} catch (CloneNotSupportedException e1) {
						e1.printStackTrace();
					}
					((Tab)tabs.getSelectedComponent()).display();
				}
				comboEdited(snapshots.getSelectedItem().toString());
			}
		}
	};
	
	private JPanel getSnapshotsPanel() {
		JPanel snapshotsPanel = new JPanel();
		snapshotsPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 98;
		snapshots = new DefaultComboBoxModel();
		snapshots.addElement(experiment);
		comboSnapshots = new JComboBox(snapshots);
		comboSnapshots.addActionListener(comboSnapShotsListener);
		comboSnapshots.setEditable(true);
		Component c = comboSnapshots.getEditor().getEditorComponent();
		((JTextField)c).getDocument().addDocumentListener(
				new DocumentListener() {
					public void changedUpdate(DocumentEvent e) { comboEdited(e); }
					public void insertUpdate(DocumentEvent e) { comboEdited(e); }
					public void removeUpdate(DocumentEvent e) { comboEdited(e); }
				});
		snapshotsPanel.add(comboSnapshots, gbc);
		
		addSnapshot = new JButton("new snapshot    ");
		File cameraImage = new File("icons"+File.separator+"camera-small.png");
		Icon cameraIcon = null;
		try {
			cameraIcon = new ImageIcon(cameraImage.toURI().toURL());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		addSnapshot.setIcon(cameraIcon);
		addSnapshot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					((Tab)tabs.getSelectedComponent()).save();
					String newSnapshot = snapshots.getSelectedItem().toString();
					// remove potential existing snapshot having the same id
					int indexToRemove = getIndexOfExperiment(newSnapshot);
					Experiment snapShot = experiment.snapshot();
					snapShot.setID(newSnapshot);
					comboSnapshots.removeActionListener(comboSnapShotsListener);
					if(indexToRemove >= 0) snapshots.removeElementAt(indexToRemove);
					snapshots.insertElementAt(snapShot,0);
					snapshots.setSelectedItem(snapShot);
					comboSnapshots.addActionListener(comboSnapShotsListener);
				} catch (CloneNotSupportedException e2) {
					e2.printStackTrace();
				} catch (Exception e3) {
					e3.printStackTrace();
				}
			}
		});
		gbc.gridx = 1;
		gbc.weightx = 1;
		snapshotsPanel.add(addSnapshot, gbc);
		
		deleteSnapshot = new JButton("delete snapshot");
		File cameraStrikedImage = new File("icons"+File.separator+"camera-striked-small.png");
		Icon cameraStrikedIcon = null;
		try {
			cameraStrikedIcon = new ImageIcon(cameraStrikedImage.toURI().toURL());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		deleteSnapshot.setIcon(cameraStrikedIcon);
		deleteSnapshot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(snapshots.getSelectedItem() != null)
					snapshots.removeElement(snapshots.getSelectedItem());
			}
		});
		gbc.gridx = 2;
		snapshotsPanel.add(deleteSnapshot, gbc);
		
		return snapshotsPanel;
	}
	
	public int getIndexOfExperiment(String expID) {
		for(int i = 0; i < snapshots.getSize(); i++)
			if(expID.compareTo(snapshots.getElementAt(i).toString()) == 0) return i;
		return -1;
	}

	private void menuBar() {
		JMenuBar jmb = new JMenuBar();
		setJMenuBar(jmb);
		JMenu menuDesign = new JMenu("Design");
		JMenu menuPrint = new JMenu("Print");
		jmb.add(menuDesign);
		jmb.add(menuPrint);
		JMenuItem openDesign = new JMenuItem("Open a design");
		JMenuItem saveDesign = new JMenuItem("Save this design");
		JMenuItem printTabHelp = new JMenuItem("Print this tab help");
		menuDesign.add(openDesign);
		menuDesign.add(saveDesign);
		menuPrint.add(printTabHelp);
		printTabHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((Tab)tabs.getSelectedComponent()).printHelp();
			}
		});
		openDesign.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser(LAST_DIRECTORY);
				int returnVal = fc.showDialog(DesignPlatform.this,"Open");
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					load(fc.getSelectedFile());
					LAST_DIRECTORY = fc.getSelectedFile().getParent();
					saveCurrentDirectory();
				}
			}
		});
		saveDesign.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser(LAST_DIRECTORY);
				int returnVal = fc.showDialog(DesignPlatform.this,"Save");
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					save(fc.getSelectedFile());
					LAST_DIRECTORY = fc.getSelectedFile().getParent();
					saveCurrentDirectory();
				}
			}
		});
	}

	public static void saveCurrentDirectory() {
		File lastDir = new File("_last_dir.tmp");
		try {
			(new DataOutputStream(new FileOutputStream(lastDir))).writeUTF(LAST_DIRECTORY);
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	void comboEdited(String text) {
		if(text.length() == 0) {
			addSnapshot.setEnabled(false);
			deleteSnapshot.setEnabled(false);
		} else {
			addSnapshot.setEnabled(true);
			deleteSnapshot.setEnabled(true);
		}
		int index = getIndexOfExperiment(text);
		if(index >= 0) {
			addSnapshot.setText("replace snapshot");
			addSnapshot.setForeground(Color.RED);
		} else {
			addSnapshot.setText("new snapshot    ");
			addSnapshot.setForeground(Color.BLACK);
		}
	}
	
	void comboEdited(DocumentEvent e) {
		try {
			comboEdited(e.getDocument().getText(0, e.getDocument().getLength()));
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
	}

	private void save(File fileName) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
//			addSnapshot.doClick();
			oos.writeInt(snapshots.getSize());
			for(int i = 0; i < snapshots.getSize(); i++) {
				oos.writeObject(snapshots.getElementAt(i));
			}
			
			oos.writeObject(getExperiment());
			
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void load(File fileName) {
		snapshots.removeAllElements();
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
			int nbSnapshots = ois.readInt();
			Experiment exp = null;
			for(int i = 0; i < nbSnapshots; i++) {
				exp = (Experiment)ois.readObject();
				exp.setDesignPlatform(DesignPlatform.this);
				snapshots.addElement(exp);
			}
			try {
				exp = (Experiment)ois.readObject();
				exp.setDesignPlatform(DesignPlatform.this);
				setExperiment(exp);
			} catch(Exception e) {
				setExperiment((Experiment)snapshots.getElementAt(0));
			}
			ois.close();
			((Tab)tabs.getComponentAt(0)).display();
			navigateToTab(0, previousIndex);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public PanelExperiment getSetupTab() {
		return (PanelExperiment)tabs.getComponentAt(0);
	}

	public PanelFactors getFactorsTab() {
		return (PanelFactors) ((Tab<FactorSet>)tabs.getComponentAt(1)).getContent();
	}

	public PanelBlocking getBlockingTab() {
		return (PanelBlocking)((Tab<Blocking>)tabs.getComponentAt(2)).getContent();
	}

	public PanelOrdering getOrderingTab() {
		return (PanelOrdering)((Tab<Ordering>)tabs.getComponentAt(3)).getContent();
	}

	public PanelOrdering getPracticeTab() {
		return (PanelOrdering)((Tab<Ordering>)tabs.getComponentAt(4)).getContent();
	}
	
	public PanelTiming getTimingTab() {
		return (PanelTiming)((Tab<Timing>)tabs.getComponentAt(5)).getContent();
	}
	
	public PanelMeasures getMeasuresTab() {
		return (PanelMeasures)((Tab<MeasureSet>)tabs.getComponentAt(6)).getContent();
	}

	public PanelSummary getSummaryTab() {
		return (PanelSummary)((Tab<Step>)tabs.getComponentAt(7)).getContent();
	}

	public Experiment getExperiment() {
		return experiment;
	}
	
	public void setExperiment(Experiment exp) {
		this.experiment = exp;
		for (int i = 0; i < tabs.getComponentCount(); i++) {
			((Tab)tabs.getComponent(i)).getContent().setExperiment(exp);
		}
		this.experimentPreview = new ExperimentPreview(this);
	}
	
	
	public ExperimentPreview getExperimentPreview() {
		return experimentPreview;
	}
	
	public static void setUIFont (javax.swing.plaf.FontUIResource f){
		//
		// sets the default font for all Swing components.
		// ex. 
		//  setUIFont (new javax.swing.plaf.FontUIResource
		//   ("Serif",Font.ITALIC,12));
		//
		java.util.Enumeration keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get (key);
			//			System.out.println(key+"="+value);
			if (value instanceof javax.swing.plaf.FontUIResource)
				UIManager.put (key, f);
		}
	}


	public static void main(String[] args) {

		//Set the OS' look&feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			setUIFont (new javax.swing.plaf.FontUIResource
					("monospace",Font.PLAIN,13));
		} catch(Exception e) {};

		DesignPlatform main = new DesignPlatform();		
		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
