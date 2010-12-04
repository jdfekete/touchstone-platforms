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


public class AddYourOwn extends Function {

	private static final long serialVersionUID = 42L;
	
	public AddYourOwn() {
		this("Add your own");		
	}
	
	public AddYourOwn(String text){
		argsType.add(text);
		itemRepresentation = toEditableMenuItem();
	}
	
	public AddYourOwn(String text, String textValue){
		this(text);
		itemRepresentation.item[0].name = textValue;
	}
	
	public Item toEditableMenuItem() {
		return new Item(new String[]{argsType.get(0)},true);
	}
	
	
	public EditableItem clone(){
		return new AddYourOwn(new String(argsType.get(0)));
	}

	
	public boolean equals(Object obj) {
		if ((obj != null)&&(obj.getClass() == AddYourOwn.class))
			return argsType.get(0).equals(((AddYourOwn)obj).argsType.get(0));
		else
			return false;
	}

	
	
}
