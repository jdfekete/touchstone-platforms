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
package fr.inria.insitu.touchstone.design.motor;

import fr.inria.insitu.touchstone.design.graphic.widgets.AddYourOwn;
import fr.inria.insitu.touchstone.design.graphic.widgets.Function;
import fr.inria.insitu.touchstone.design.torun.CodeGeneration;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;



public class Intertitle implements Serializable {

	private static final long serialVersionUID = 42L;

	private String classe;
	private String functionName;
	private Vector<String> argsTypes;
	private Vector<String> argsValues;

	public Intertitle(Vector<String> argsType, Vector<String> argsValues, String classe, String functionName) {
		this.argsTypes = argsType;
		this.argsValues = argsValues;
		this.classe = classe;
		this.functionName = functionName;
	}

	public Intertitle(Function function) {
		if(function.equals(new AddYourOwn())) {
			if(function.getArgsValues().get(0).compareTo("Add your own") == 0) {
				this.argsTypes = function.getArgsType();
				this.argsValues = function.getArgsValues();
				this.classe = function.getClasse();
				this.functionName = function.getName();
			} else {
				String expression = function.getArgsValues().get(0);
				classe = CodeGeneration.getSimpleClassName(expression);
				functionName = classe;
				Class<?>[] classesTypes = CodeGeneration.getArgumentsTypes(expression);
				argsTypes = new Vector<String>();
				if(classesTypes != null)
					for (int i = 0; i < classesTypes.length; i++)
						argsTypes.add(classesTypes[i].getName());
				String[] vals = CodeGeneration.getArguments(expression);
				argsValues = new Vector<String>();
				if(vals != null)
					for (int i = 0; i < vals.length; i++)
						argsValues.add(vals[i]);
			}
		} else {
			this.argsTypes = function.getArgsType();
			this.argsValues = function.getArgsValues();
			this.classe = function.getClasse();
			this.functionName = function.getName();
		}
	}

	protected Object clone() throws CloneNotSupportedException {
		Vector<String> argsTypesCopy = new Vector<String>();
		Vector<String> argsValuesCopy = new Vector<String>();
		for (Iterator<String> iterator = argsTypes.iterator(); iterator.hasNext();)
			argsTypesCopy.add(iterator.next());
		for (Iterator<String> iterator = argsValues.iterator(); iterator.hasNext();)
			argsValuesCopy.add(iterator.next());
		return new Intertitle(argsTypesCopy, argsValuesCopy, classe, functionName);
	}

	/**
	 * 
	 * @return true if each value of argsValues is of the type indicated in the corresponding index of argsTypes. 
	 */
	private boolean isCorrectlyTyped(){
		if (argsTypes.size() != argsValues.size())
			return false;

		int i = 0;
		while((i<argsTypes.size())&&(valueHasCorrectType(argsValues.get(i), argsTypes.get(i))))
			i++;		

		return i == argsTypes.size();
	}

	/**
	 * Tests if a value has the correct type
	 * @param value the value
	 * @param type the type (types are : java.lang.string, int, float, char) 
	 * @return true if the value has the correct type
	 */
	private boolean valueHasCorrectType(String value, String type){
		if (type.equalsIgnoreCase("java.lang.string"))
			return true;
		else {
			value = removeSpaces(value);
			if (type.equalsIgnoreCase("int")){
				try {
					Integer.parseInt(value);
					return true;	
				} catch (Exception e) {
					return false;
				}
			}
			else if (type.equalsIgnoreCase("float")){
				try {
					Float.parseFloat(value);
					return true;	
				} catch (Exception e) {
					return false;
				}
			}
			else if (type.equalsIgnoreCase("char")){
				return value.length() == 1;
			}
			else if (type.equalsIgnoreCase("long")){
				try {
					Long.parseLong(value);
					return true;	
				} catch (Exception e) {
					return false;
				}
			}
			else if (type.equalsIgnoreCase("double")){
				try {
					Double.parseDouble(value);
					return true;	
				} catch (Exception e) {
					return false;
				}
			}
			return false;
		}
	}

	/**
	 * removes the spaces contained in the value
	 * @param value
	 * @return the value without spaces
	 */
	private String removeSpaces(String value){
		String result = "";
		for (int i = 0; i<value.length() ; i++)
			if (value.charAt(i)!=' ')
				result+=value.charAt(i);
		return result;
	}


	public String toString() {
		if (functionName.equals("")&&(classe.equals(""))){
			if (argsValues.size()>0 && argsTypes.size()>0)
				if(!argsValues.get(0).equals(argsTypes.get(0)))
					return argsValues.get(0);
			return "";
		}
		else
			if (isCorrectlyTyped()){
				String[] namePackage = classe.split("\\.");
				String result = namePackage[namePackage.length-1];
				if(argsValues.size() > 0) result+="(";
				for (int i = 0 ; i<argsValues.size() ; i++)
					result+= argsValues.get(i)+",";
				if(argsValues.size() > 0)
					result = result.substring(0, result.length()-1)+")";
				return result;
			}			
			else 
				return null;
	}

	public String simpleToString() {
		if (functionName.equals("")&&(classe.equals(""))){
			if (argsValues.size() > 0 && argsTypes.size() > 0 && !argsValues.get(0).equals(argsTypes.get(0)))
				return argsValues.get(0);
			else
				return "";
		}
		else
			if (isCorrectlyTyped()){
				String result = functionName;
				if(argsValues.size() > 0) result+="(";
				for (int i = 0 ; i<argsValues.size() ; i++)
					if (argsTypes.get(i).equalsIgnoreCase("java.lang.string")) {
						if(!(argsValues.get(i).charAt(0) == '{' && argsValues.get(i).charAt(argsValues.get(i).length()-1) == '}'))
							result+="{"+argsValues.get(i)+"}"+",";
						else
							result+=argsValues.get(i)+",";
					} else
						result+= argsValues.get(i)+",";
				if(argsValues.size() > 0)
					result = result.substring(0, result.length()-1)+")";
				return result;
			}			
			else 
				return null;
	}

	/**
	 * 
	 * @return the class of this intertitle
	 */
	public String getClasse() {
		return classe;
	}

	/**
	 * 
	 * @return the name of this intertitle.
	 */
	public String getFunctionName() {
		return functionName;
	}

	/** 
	 * @return a {@link Function} representation of this intertitle
	 */
	public Function toFunction(){
		if (functionName.equals("")&&(classe.equals(""))) {
			if(argsTypes.size() > 0 && argsValues.size() > 0) 
				return new AddYourOwn(argsTypes.get(0),argsValues.get(0));
			else
				return new AddYourOwn();
		}
		Vector<String> argsTypeVec = new Vector<String>();
		for (String argType : argsTypes) {
			argsTypeVec.add(argType);
		}
		Vector<String> argsValueVec = new Vector<String>();
		for (String argValue : argsValues) {
			argsValueVec.add(argValue);
		}
		return new Function(functionName,argsTypeVec,argsValueVec,classe);
	}

}
