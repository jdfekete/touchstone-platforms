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
import java.awt.Dimension;
import java.awt.Font;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import fr.inria.insitu.touchstone.design.motor.Step;
import fr.inria.insitu.touchstone.design.web.LinkListener;
import fr.inria.insitu.touchstone.design.web.PrintableTextArea;



public class Tab<S extends Step> extends JPanel {

	private static final long serialVersionUID = 42L;
	private PrintableTextArea help = new PrintableTextArea("Help", new Dimension(500,200));
	private StepPanel<S> content;

	private JScrollPane jspExperimentPreview;
	
	public Tab(URL helpURL, StepPanel<S> content, String title){
		this(content, title);
		setHelp(helpURL);
	}

	/**
	 * Construct a new tab with the specified title and displaying the specified StepPanel
	 * @param content
	 * @param title
	 */
	public Tab(StepPanel<S> content, String title){
		JLabel titleLabel = new JLabel(title);
		titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		
		titleLabel.setFont(new Font("monospace", Font.PLAIN, 22));
		titleLabel.setForeground(Color.DARK_GRAY);
		titleLabel.setBorder(BorderFactory.createEtchedBorder());
		titleLabel.setBackground(Color.WHITE);
		titleLabel.setMaximumSize(titleLabel.getPreferredSize());
		this.content = content;

		jspExperimentPreview = new JScrollPane();
		jspExperimentPreview.setPreferredSize(new Dimension(300, 100));
		jspExperimentPreview.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
		
		setLayout(new BorderLayout());
		((BorderLayout)getLayout()).setHgap(5);
		((BorderLayout)getLayout()).setVgap(5);
		
		JPanel topPanel = new JPanel(new BorderLayout());	
		topPanel.add(titleLabel,BorderLayout.NORTH);
		JSplitPane helpPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, help.getJsp(), jspExperimentPreview);
		topPanel.add(helpPanel,BorderLayout.CENTER);
		add(topPanel,BorderLayout.NORTH);
		if (content.needScrollPane()) {
			JScrollPane jsp = new JScrollPane(content);
			jsp.setPreferredSize(new Dimension(500, 300));
		} else {
			content.setPreferredSize(new Dimension(500, 300));
		}
		add(content,BorderLayout.CENTER);
	}
	
	public void printHelp() {
		help.print();
	}
	
	public void setHelp(URL helpURL) {
		help.setPage(helpURL);
	}

	/**
	 * Create a step from the content StepPanel and add it to its parent.
	 * @throws Exception if the getStatus() method from the StepPanel content returns an error.
	 */
	public void save() throws Exception {
		String status = getStatus();
		if (status==null)
			content.save();
		else
			throw new Exception(status);
	}

	/**
	 * Displays this tab.
	 */
	public void display(){
		content.display();
		ExperimentPreview experimentPreview = content.getDesignPlatform().getExperimentPreview();
		jspExperimentPreview.setAlignmentX(LEFT_ALIGNMENT);
		jspExperimentPreview.setViewportView(experimentPreview);
		revalidate();
	}

	/**
	 * @return null if this tab has been correctly filled, else a string explaining the error.
	 */
	public String getStatus(){
		return content.getStatus();
	}
	
	public StepPanel<S> getContent() {
		return content;
	}
	
	public void setHyperLinkListener(LinkListener listener) {
		help.setHyperLinkListener(listener);
	}

	public PrintableTextArea getHelp() {
		return help;
	}

}
