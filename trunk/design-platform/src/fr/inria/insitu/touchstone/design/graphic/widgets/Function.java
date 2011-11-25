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

import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;

/**
 * 
 * class used to represent a function as an {@link EditableItem} 
 *
 */
public class Function extends EditableItem implements Serializable {

	private static final long serialVersionUID = 42L;
	
	private String name="";
	protected Vector<String> argsType = new Vector<String>();
	private String classe = ""; 

	/**
	 * Construct a function object.
	 * @param name the name of the function
	 * @param args the type of each argument of the function
	 */
	public Function(String name, Vector<String> args){
		this.name = name;
		this.argsType = args;
		itemRepresentation = toEditableMenuItem();
	}

	/**
	 * Construct a function object.
	 * @param name name the name of the function
	 * @param argsType the type of each argument of the function
	 * @param classe the class of the function (as in the XML file describing a plugin) 
	 */
	public Function(String name, Vector<String> argsType, String classe ){
		this.name = name;
		this.argsType = argsType;
		this.classe = classe;
		itemRepresentation = toEditableMenuItem();
	}
	
	/**
	 * Construct a function object.
	 * @param name name the name of the function
	 * @param argsType the type of each argument of the function
	 * @param argsValues the value of each argument of the function
	 * @param classe the class of the function (as in the XML file describing a plugin)
	 */
	public Function(String name, Vector<String> argsType, Vector<String> argsValues, String classe ){
//		System.out.println("new function...");
		this.name = name;
		this.argsType = argsType;
//		for (Iterator<String> iterator = argsType.iterator(); iterator.hasNext();) {
//			String string = iterator.next();
//			System.out.println("\ttype="+string);
//		}
		this.classe = classe;
		itemRepresentation = toEditableMenuItem();
		setArgsValues(argsValues);
	}
	
	
	public Function(){}

	public String getName() {
		return name;
	}
	public void setName(String functionName) {
		this.name = functionName;
		itemRepresentation = toEditableMenuItem();
	}
	public Vector<String> getArgsType() {
		return argsType;
	}
	public void addArgType(String arg){
		argsType.add(arg);
		itemRepresentation = toEditableMenuItem();
	}
	public Vector<String> getArgsValues(){
		Vector<String> result = new Vector<String>();
		ItemPart[] itemParts = itemRepresentation.item;
		for (ItemPart item : itemParts)
			if (item.editable)
				result.add(item.name);
		return result;
	}
	
	public void setArgsValues(String[] argsValues){
		int i = 0;
		for (ItemPart itemPart : itemRepresentation.item)
			if (itemPart.editable){
				itemPart.name = argsValues[i];
				i++;
			}
	}
	
	public void setArgsValues(Vector<String> argsValues){
		setArgsValues(argsValues.toArray(new String[1]));
	}
	
	public String getClasse() {
		return classe;
	}
	
	public void setClasse(String classe){
		this.classe = classe;
		itemRepresentation = toEditableMenuItem();
	}

	
	public boolean equals(Object obj) {
		if (obj.getClass() == Function.class){
			Function function = (Function) obj;
			return (this.name.equalsIgnoreCase(function.getName())
					&& this.argsType.equals(function.argsType));
		}
		else 
			return false;
	}
	
	
	public EditableItem clone() {
		Vector<String> argsClone = new Vector<String>();
		for (String arg : argsType)
			argsClone.add(new String(arg));
		return new Function(new String(name),argsClone, new String(classe));
	}

	
	public Item toEditableMenuItem(){
		Vector<String> itemParts = new Vector<String>();
		itemParts.add(name+"(");
		for (int i = 0 ; i< argsType.size()-1;i++){
			itemParts.add(" "+argsType.get(i));
			itemParts.add(",");
		}
		if (argsType.size()>0){
			itemParts.add(" "+argsType.lastElement()+" ");
			itemParts.add(")");
		}
		else
			itemParts.set(0,itemParts.get(0)+")");
		return new Item(itemParts.toArray(new String[itemParts.size()]),false);
	}	
}
