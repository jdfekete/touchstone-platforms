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
package fr.inria.insitu.touchstone.design.graphic.widgets;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.ComboBoxEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;


/**
 * 
 * a JComboBox style menu, that can be edited partially
 *
 */
public class EditableMenu extends JComboBox {

	private static final long serialVersionUID = 42L;
	
	/**
	 * the item contained in this menu
	 */
	private Vector<EditableItem> itemSet;
	private MyComboBoxEditor comboBoxEditor = new MyComboBoxEditor();
	public EditableMenu(Vector<EditableItem> set) {
		super(set);
		itemSet = set;
		
		setEditor(comboBoxEditor);
		comboBoxEditor.editor.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				actionEvent(e);
			}

			private void actionEvent(DocumentEvent e) {
				ActionListener[] listeners = EditableMenu.this.getListeners(ActionListener.class);
				try {
					for (int i = 0; i < listeners.length; i++) {
						listeners[i].actionPerformed(new ActionEvent(EditableMenu.this, 34, e.getDocument().getText(0, e.getDocument().getLength())));
					}
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
			}

			public void insertUpdate(DocumentEvent e) {
				actionEvent(e);
			}

			public void removeUpdate(DocumentEvent e) {
				actionEvent(e);
			}
			
		});
		
		setRenderer(new DefaultListCellRenderer(){
			private static final long serialVersionUID = 1L;

			public Component getListCellRendererComponent( JList list,Object value,int index,boolean isSelected,boolean cellHasFocus ){
				if (value instanceof Item)
					super.getListCellRendererComponent( list, ((Item)value).toHTML(), index, isSelected, cellHasFocus );
				else
					super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
				return this;
			}
		}
		);

		addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				setEditable(((EditableItem)getSelectedItem()).toEditableMenuItem().isEditable());
			}			
		});		
		
		if (((EditableItem)getSelectedItem())!=null)
			setEditable(((EditableItem)getSelectedItem()).toEditableMenuItem().isEditable());
		
	}
	
	/**
	 * change the item to be displayed
	 * @param itemSet the item to display in the menu
	 */
	public void setItemSet(Vector<EditableItem> itemSet) {
		this.itemSet = itemSet;
	}

	
	
	/**
	 * 
	 * @return a vector of editable item contained in the menu
	 */
	public Vector<EditableItem> getItemSet() {
		return itemSet;
	}

	/**
	 * 
	 * @return a string representation of the item being edited
	 */
	public String getText(){
		return ((JTextComponent)getEditor().getEditorComponent()).getText();
	}
	
	
	public Object clone() {
		Vector<EditableItem> clone = new Vector<EditableItem>();
		for (EditableItem item : itemSet)
			clone.add(item.clone());
		return new EditableMenu(clone);
	}
	
	private class MyComboBoxEditor implements ComboBoxEditor {

//		private JTextPane editor = new JTextPane();
		private JTextField editor = new JTextField();
		private Item item;	
		private SimpleAttributeSet editableAttributeSet = new SimpleAttributeSet();
		private SimpleAttributeSet unEditableAttributeSet = new SimpleAttributeSet();
		private EditableItem editableItem;
		
		MyComboBoxEditor(){
			editor.setDocument(new MyDefaultStyledDocument());
			editor.setBackground(Color.WHITE);
			StyleConstants.setItalic(editableAttributeSet,true);
			StyleConstants.setForeground(editableAttributeSet,Color.gray);
			StyleConstants.setBold(unEditableAttributeSet,true);
		}

		/**
		 * not implemented
		 */
		public void addActionListener(ActionListener l) {
		}

		public Component getEditorComponent() {
			return editor;
		}

		public Object getItem() {
			return editableItem;
		}

		/**
		 * not implemented
		 */
		public void removeActionListener(ActionListener l) { }


		/**
		 * not implemented
		 */
		public void selectAll() { }

		public void setItem(Object object) {
			if (object instanceof EditableItem) {				
				editableItem= (EditableItem) object;
				item = editableItem.getItemRepresentation();
				
				MyDefaultStyledDocument document = (MyDefaultStyledDocument)editor.getDocument();				
				try {
					document.remove(-1, -1);
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
				int offset = 0;
				for(ItemPart ip : item.item)
					try {					
						AttributeSet as;
						if (ip.editable)
							as = editableAttributeSet;
						else
							as = unEditableAttributeSet;
						document.insertString2(offset, ip.name, as);
						offset+=ip.name.length();
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
			}
		}

		private class MyDefaultStyledDocument extends DefaultStyledDocument {

			private static final long serialVersionUID = 1L;

			public void remove(int offs, int len) throws BadLocationException {
				if ((offs==-1)&&(len==-1)){
					//called by setItem
					try {
						super.remove(0, getLength());
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
				}
				else{
					//called by user
					if (item.remove(offs, len))
						try {
							super.remove(offs, len);
						} catch (BadLocationException e) {
							e.printStackTrace();
						}
				}							
			}

			public void insertString2(int offs, String str, AttributeSet a)throws BadLocationException {
				super.insertString(offs, str, a);
			}

			
			public void insertString(int offs, String str, AttributeSet a)throws BadLocationException {
				if (item.insertString(offs,str))
					super.insertString(offs, str, editableAttributeSet);
			}
		}
	}
}
