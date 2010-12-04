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

import java.awt.Point;

public class Item  {

	private static final long serialVersionUID = 42L;
	
	protected ItemPart[] item;
	private String[] itemParts;
	private boolean firstEditable;

	/**
	 * Construct an Item from an array of string, each string representing a part.
	 * There can't be two consecutive editable parts, or uneditable parts. 
	 * @param itemParts Array of string representing the parts
	 * @param firstEditable true if the first item is editable, false otherwise
	 */
	public Item(String[] itemParts, boolean firstEditable) {
		this.itemParts = itemParts;
		this.firstEditable = firstEditable;
		item = new ItemPart[itemParts.length];
		for (int i =0 ; i < itemParts.length; i++){
			if (i%2==0)
				item[i]=new ItemPart(itemParts[i],firstEditable);
			else
				item[i]=new ItemPart(itemParts[i],!firstEditable);
		}		
	}

	
	protected Object clone() {
		return new Item(itemParts.clone(),firstEditable);
	}

	
	public String toString() {
		String result = "";
		for (ItemPart ip : item)
			result += ip.name;
		return result;			
	}

	/**
	 * @return an HTML representation of this item
	 */
	public String toHTML() {
		String result = "<html>";
		for (ItemPart ip : item)
			if (ip.editable)
				result += "<em><font color=#777777>"+ip.name+"</font></em>";
			else
				result += "<b><font color=#000000>"+ip.name+"</font></b>";

		return result+"</html>";
	}

	/**
	 * 
	 * @return true if all the itemParts of this item are editable
	 */
	public boolean isEditable(){
		boolean editable = false;
		int i = 0;
		while((i<item.length)&&(!editable)){
			if (item[i].editable)
				editable = true;
			i++;
		}
		return editable;
	}

	/**
	 * 
	 * @param offs offset of the specified character
	 * @return a point containing in the X field the index of the ItemPart containing the specified offset
	 * and in the Y field, the offset index in the itemPart.
	 */
	private Point getItemPartAt(int offs){
		int charCount = item[0].length()-1;
		int itemPartCount =0;
		while ((charCount<offs)&&(itemPartCount<(item.length-1))){
			itemPartCount++;
			charCount+=item[itemPartCount].length();
		}		
		if (itemPartCount<=(item.length)){
			//Point : x = ItemPart index, 
			//        y = offs index in the itemPart
			return new Point(itemPartCount,item[itemPartCount].length()-(charCount-offs));
		}
		else
			return null;
	}

	/**
	 * remove the text between the two specified index
	 * @param begin exclusive
	 * @param end exclusive
	 */
	private void removeBetween(Point begin, Point end){
		String newName = "";
		if (begin.y-1>0)
			newName = item[begin.x].name.substring(0,begin.y-1);
		if (end.y<item[begin.x].name.length())
			newName += item[begin.x].name.substring(end.y);
		item[begin.x].setName(newName);
	}

	/**
	 * tests whether or not this item can be edited on the specified area
	 * @param offs begin offset
	 * @param len length
	 * @return true if it can be edited on the specified area
	 */
	public boolean remove(int offs, int len){
		Point begin = getItemPartAt(offs);
		Point end = getItemPartAt(offs+len-1);
		if (begin==null||end==null||begin.x!=end.x){
			//Bad Location
			return false;
		}
		else
			if(!item[begin.x].editable){
				//Not Editable
				return false;
			}
			else{
				removeBetween(begin,end);
				return true;
			}
	}

	/**
	 * insert string after the specified index
	 * @param offs the specified index
	 * @param str the string to insert
	 * @return true if it has been done
	 */
	public boolean insertString(int offs, String str) {
		Point itemPartDatas = getItemPartAt(offs);
		itemPartDatas.y --;
		ItemPart itemPart= item[itemPartDatas.x];
		if (itemPartDatas.x>0){
			if ((itemPartDatas.y==0)&&(!itemPart.editable)){
				itemPartDatas.x --;
				itemPart= item[itemPartDatas.x];
				itemPartDatas.y = itemPart.length();				
			}
		}

		if (itemPart.editable){
			String newName="";
			if (itemPartDatas.y>0)
				newName += itemPart.name.substring(0,itemPartDatas.y);
			newName+=str;
			if(itemPartDatas.y<itemPart.length())
				newName += itemPart.name.substring(itemPartDatas.y);
			itemPart.setName(newName);
			return true;
		}
		else
			return false;
	}
}