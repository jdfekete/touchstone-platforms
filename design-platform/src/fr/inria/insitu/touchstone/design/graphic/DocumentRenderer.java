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

/*  Copyright 2002
Kei G. Gauthier
Suite 301
77 Winsor Street
Ludlow, MA  01056
*/
/* (from http://www.koders.com/java/fidDF2F42D24FFEFD982138F6E39FD706EF956C7A82.aspx) */

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.JEditorPane;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.swing.text.View;
import javax.swing.text.html.HTMLDocument;

public class DocumentRenderer implements Printable {
	
	private static final long serialVersionUID = 42L;
	protected int currentPage = -1; //Used to keep track of when
	//the page to print changes.

	protected JEditorPane jeditorPane;
	
	//Container to hold the
	//Document. This object will
	//be used to lay out the
	//Document for printing.

	protected double pageEndY = 0; //Location of the current page
	//end.

	protected double pageStartY = 0; //Location of the current page
	//start.

	protected boolean scaleWidthToFit = true; //boolean to allow control over


	protected PageFormat pFormat;
	protected PrinterJob pJob;

	public DocumentRenderer() {
		pFormat = new PageFormat();
		pJob = PrinterJob.getPrinterJob();
	}

	public Document getDocument() {
		if (jeditorPane != null)
			return jeditorPane.getDocument();
		else
			return null;
	}

	public boolean getScaleWidthToFit() {
		return scaleWidthToFit;
	}

	public void pageDialog() {
		pFormat = pJob.pageDialog(pFormat);
	}

	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
		double scale = 1.0;
		Graphics2D graphics2D;
		View rootView;
		// I
		graphics2D = (Graphics2D) graphics;
		// II
		jeditorPane.setSize((int) pageFormat.getImageableWidth(), Integer.MAX_VALUE);
		jeditorPane.validate();
		// III
		rootView = jeditorPane.getUI().getRootView(jeditorPane);
		// IV
		if ((scaleWidthToFit) && (jeditorPane.getMinimumSize().getWidth() >
		pageFormat.getImageableWidth())) {
			scale = pageFormat.getImageableWidth() /
			jeditorPane.getMinimumSize().getWidth();
			graphics2D.scale(scale, scale);
		}
		// V
		graphics2D.setClip((int) (pageFormat.getImageableX() / scale),
				(int) (pageFormat.getImageableY() / scale),
				(int) (pageFormat.getImageableWidth() / scale),
				(int) (pageFormat.getImageableHeight() / scale));
		// VI
		if (pageIndex > currentPage) {
			currentPage = pageIndex;
			pageStartY += pageEndY;
			pageEndY = graphics2D.getClipBounds().getHeight();
		}
		// VII
		graphics2D.translate(graphics2D.getClipBounds().getX(),
				graphics2D.getClipBounds().getY());
		// VIII
		Rectangle allocation = new Rectangle(0,
				(int) -pageStartY,
				(int) (jeditorPane.getMinimumSize().getWidth()),
				(int) (jeditorPane.getPreferredSize().getHeight()));
		// X
		if (printView(graphics2D, allocation, rootView)) {
			return Printable.PAGE_EXISTS;
		} else {
			pageStartY = 0;
			pageEndY = 0;
			currentPage = -1;
			return Printable.NO_SUCH_PAGE;
		}
	}

	/* print(HTMLDocument) is called to set an HTMLDocument for printing.
	 */
	public void print(HTMLDocument htmlDocument) {
		setDocument(htmlDocument);
		printDialog();
	}

	public void print(JEditorPane jedPane) {
		setDocument(jedPane);
		printDialog();
	}

	public void print(PlainDocument plainDocument) {
		setDocument(plainDocument);
		printDialog();
	}

	protected void printDialog() {
		if (pJob.printDialog()) {
			pJob.setPrintable(this, pFormat);
			try {
				pJob.print();
			} catch (PrinterException printerException) {
				pageStartY = 0;
				pageEndY = 0;
				currentPage = -1;
				System.out.println("Error Printing Document");
			}
		}
	}

	protected boolean printView(Graphics2D graphics2D, Shape allocation,
			View view) {
		boolean pageExists = false;
		Rectangle clipRectangle = graphics2D.getClipBounds();
		Shape childAllocation;
		View childView;

		if (view.getViewCount() > 0) {
			for (int i = 0; i < view.getViewCount(); i++) {
				childAllocation = view.getChildAllocation(i, allocation);
				if (childAllocation != null) {
					childView = view.getView(i);
					if (printView(graphics2D, childAllocation, childView)) {
						pageExists = true;
					}
				}
			}
		} else {
			// I
			if (allocation.getBounds().getMaxY() >= clipRectangle.getY()) {
				pageExists = true;
				// II
				if ((allocation.getBounds().getHeight() > clipRectangle.getHeight()) &&
						(allocation.intersects(clipRectangle))) {
					view.paint(graphics2D, allocation);
				} else {
					// III
					if (allocation.getBounds().getY() >= clipRectangle.getY()) {
						if (allocation.getBounds().getMaxY() <= clipRectangle.getMaxY()) {
							view.paint(graphics2D, allocation);
						} else {
							// IV
							if (allocation.getBounds().getY() < pageEndY) {
								pageEndY = allocation.getBounds().getY();
							}
						}
					}
				}
			}
		}
		return pageExists;
	}

	/* Method to set the content type the JEditorPane.
	 */
	protected void setContentType(String type) {
		jeditorPane.setContentType(type);
	}

	/* Method to set an HTMLDocument as the Document to print.
	 */
	public void setDocument(HTMLDocument htmlDocument) {
		jeditorPane = new JEditorPane();
		setDocument("text/html", htmlDocument);
	}

	public void setDocument(JEditorPane jedPane) {
		jeditorPane = new JEditorPane();
		setDocument(jedPane.getContentType(), jedPane.getDocument());
	}

	public void setDocument(PlainDocument plainDocument) {
		jeditorPane = new JEditorPane();
		setDocument("text/plain", plainDocument);
	}

	/* Method to set the content type and document of the JEditorPane.
	 */
	protected void setDocument(String type, Document document) {
		setContentType(type);
		jeditorPane.setDocument(document);
	}

	/* Method to set the current choice of the width scaling option.
	 */
	public void setScaleWidthToFit(boolean scaleWidth) {
		scaleWidthToFit = scaleWidth;
	}


}
