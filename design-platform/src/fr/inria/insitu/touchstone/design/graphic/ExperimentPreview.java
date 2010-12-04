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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.io.Serializable;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;

public class ExperimentPreview extends JPanel implements Serializable {
	private static final long serialVersionUID = 42L;
	
	protected JTextArea topExperimentTextArea;
	protected JTextArea bottomExperimentTextArea;
	protected JTextArea factorsTextArea;
	protected JTextArea measuresTextArea;
	protected JTree experimentTree;
	
	protected DesignPlatform designPlatform;
	
	private static Border HILITE_BORDER = BorderFactory.createLineBorder(Color.RED);
	private static Border UNHILITE_BORDER = BorderFactory.createEmptyBorder();
	
	public static Font FONT = new Font("monospace", Font.PLAIN, 9);
	
	public ExperimentPreview(DesignPlatform designPlatform) {
		super();
		setBackground(Color.WHITE);
		this.designPlatform = designPlatform;
		topExperimentTextArea = new JTextArea("");
		topExperimentTextArea.setEditable(false);
		topExperimentTextArea.setFont(FONT);
		factorsTextArea = new JTextArea("");
		factorsTextArea.setEditable(false);
		factorsTextArea.setFont(FONT);
		measuresTextArea = new JTextArea("");
		measuresTextArea.setEditable(false);
		measuresTextArea.setFont(FONT);
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("experiment");
		experimentTree = new JTree(root);
		bottomExperimentTextArea = new JTextArea("</experiment>");
		bottomExperimentTextArea.setEditable(false);
		bottomExperimentTextArea.setFont(FONT);
		
		dolayout();
		
		updateExperimentData();
		updateFactorsData();
		updateMeasuresData();
		
		experimentTree.setCellRenderer(new TreeRendererExperiment(root));
	}
	
	private void undolayout() {
		removeAll();
	}
	
	private void dolayout() {
		
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(new GridBagLayout());
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridy = 0;
		gbc.gridx = 0;
		add(topExperimentTextArea, gbc);
		gbc.gridy++;
		add(factorsTextArea, gbc);
		gbc.gridy++;
		add(measuresTextArea, gbc);
		gbc.gridy++;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 35, 0, 35);
		add(experimentTree, gbc);
		gbc.gridy++;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		add(bottomExperimentTextArea, gbc);
		revalidate();
	}
	
	public void updateExperimentData() {
		String contentString = "<experiment author=\""
			+ designPlatform.getExperiment().getAuthor()
			+ "\" description=\""
			+ designPlatform.getExperiment().getDescription()
			+ "\" id=\""
			+ designPlatform.getExperiment().getShortCode()
			+ "\" name=\""
			+ designPlatform.getExperiment().getTitle()
			+ "\">";
		topExperimentTextArea.setText(contentString);
		undolayout();
		dolayout();
	}
	
	public void updateFactorsData() {
		String contentString = designPlatform.getExperiment().getFactorSet().toXML();
		if(contentString.length() > 1) contentString = contentString.substring(0, contentString.length()-1);
		factorsTextArea.setText(contentString);
		undolayout();
		dolayout();
	}
	
	public void updateMeasuresData() {
		String contentString = designPlatform.getExperiment().getMeasureSet().toXML();
		if(contentString.length() > 1) contentString = contentString.substring(0, contentString.length()-1);
		measuresTextArea.setText(contentString);
		undolayout();
		dolayout();
	}
	
	public void hiliteExperimentData() {
		if(getParent() != null && getParent() instanceof JViewport) {
			((JViewport)getParent()).setViewPosition(new Point(0, 0));
		}
		topExperimentTextArea.setBorder(HILITE_BORDER);
		factorsTextArea.setBorder(UNHILITE_BORDER);
		measuresTextArea.setBorder(UNHILITE_BORDER);
		experimentTree.setBorder(UNHILITE_BORDER);
		bottomExperimentTextArea.setBorder(HILITE_BORDER);
	}
	
	public void hiliteFactorsData() {
		if(getParent() != null && getParent() instanceof JViewport) {
			((JViewport)getParent()).setViewPosition(new Point(0, topExperimentTextArea.getSize().height));
		}
		topExperimentTextArea.setBorder(UNHILITE_BORDER);
		factorsTextArea.setBorder(HILITE_BORDER);
		measuresTextArea.setBorder(UNHILITE_BORDER);
		experimentTree.setBorder(UNHILITE_BORDER);
		bottomExperimentTextArea.setBorder(UNHILITE_BORDER);
	}
	
	public void hiliteMeasuresData() {
		if(getParent() != null && getParent() instanceof JViewport) {
			((JViewport)getParent()).setViewPosition(new Point(0, topExperimentTextArea.getSize().height + factorsTextArea.getSize().height));
		}
		topExperimentTextArea.setBorder(UNHILITE_BORDER);
		factorsTextArea.setBorder(UNHILITE_BORDER);
		measuresTextArea.setBorder(HILITE_BORDER);
		experimentTree.setBorder(UNHILITE_BORDER);
		bottomExperimentTextArea.setBorder(UNHILITE_BORDER);
	}
	
	public void hiliteExperimentTree() {
		if(getParent() != null && getParent() instanceof JViewport) {
			((JViewport)getParent()).setViewPosition(new Point(0, topExperimentTextArea.getSize().height + factorsTextArea.getSize().height + measuresTextArea.getSize().height));
		}
		topExperimentTextArea.setBorder(UNHILITE_BORDER);
		factorsTextArea.setBorder(UNHILITE_BORDER);
		measuresTextArea.setBorder(UNHILITE_BORDER);
		experimentTree.setBorder(HILITE_BORDER);
		bottomExperimentTextArea.setBorder(UNHILITE_BORDER);
	}
	
	public void hiliteAll() {
		if(getParent() != null && getParent() instanceof JViewport) {
			((JViewport)getParent()).setViewPosition(new Point(0, topExperimentTextArea.getSize().height + factorsTextArea.getSize().height + measuresTextArea.getSize().height));
		}
		topExperimentTextArea.setBorder(UNHILITE_BORDER);
		factorsTextArea.setBorder(UNHILITE_BORDER);
		measuresTextArea.setBorder(UNHILITE_BORDER);
		experimentTree.setBorder(UNHILITE_BORDER);
		bottomExperimentTextArea.setBorder(UNHILITE_BORDER);
		setBorder(HILITE_BORDER);
	}
	
}
