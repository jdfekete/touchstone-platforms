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
package fr.inria.insitu.touchstone.design.XMLParser;

import fr.inria.insitu.touchstone.design.graphic.widgets.Function;
import fr.inria.insitu.touchstone.design.motor.Experiment;
import fr.inria.insitu.touchstone.design.motor.Factor;
import fr.inria.insitu.touchstone.design.motor.Measure;
import fr.inria.insitu.touchstone.design.motor.MeasureType;
import fr.inria.insitu.touchstone.design.motor.Plugin;

import java.util.Iterator;
import java.util.Vector;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class PluginHandler extends DefaultHandler {

	private static final long serialVersionUID = 42L;

	private Plugin plugin = new Plugin();
	private boolean creatingFactors = false;
	private boolean creatingIntertitles = false;
	private boolean creatingCriteria = false;
	private boolean creatingBlocks = false;

	private Function currentFunction = null;

	private Factor currentFactor;

	private Experiment experiment;

	public PluginHandler(Experiment experiment) {
		this.experiment = experiment;
	}

	public void startElement(String nameSpaceURI, String localName, String rawName, Attributes attributs) throws SAXException {

		if (localName.equals("plugin"))
			plugin.setId(attributs.getValue("id"));

		else if (localName.equals("factors"))
			creatingFactors = true;
		else if (localName.equals("intertitles"))
			creatingIntertitles = true;
		else if (localName.equals("criteria"))
			creatingCriteria = true;
		else if (localName.equals("blocks"))
			creatingBlocks = true;
		else if ((localName.equals("factor"))){
			String idFactor = attributs.getValue("id");
			Vector<Factor> predefinedFactors = experiment.getPredefinedFactors();
			currentFactor = null;
			for (Iterator<Factor> iterator = predefinedFactors.iterator(); iterator.hasNext();) {
				Factor factor = iterator.next();
				if(factor.getShortName().compareTo(idFactor) == 0) {
					currentFactor = factor;
					break;
				}
			}
			if(currentFactor == null) {
				currentFactor = new Factor();
				currentFactor.setShortName(idFactor);
			}
			if(attributs.getValue("name") != null)
				currentFactor.setFullName(attributs.getValue("name"));
			if(attributs.getValue("help") != null)
				currentFactor.setHelp(attributs.getValue("help"));
			String type = attributs.getValue("type");
			if(type != null) {
				if(type.equalsIgnoreCase("integer")) currentFactor.setType(MeasureType.Integer);
				else if(type.equalsIgnoreCase("float")) currentFactor.setType(MeasureType.Float);
				else if(type.equalsIgnoreCase("number")) currentFactor.setType(MeasureType.Float);
				else currentFactor.setType(MeasureType.String);
			}
			if(attributs.getValue("tag") != null)
				currentFactor.setTag(attributs.getValue("tag"));


			//			currentFactor = new Factor();
			//			currentFactor.setShortName(attributs.getValue("id"));
			//			currentFactor.setFullName(attributs.getValue("name"));
			//			currentFactor.setHelp(attributs.getValue("help"));
			//			String type = attributs.getValue("type");
			//			if(type != null) {
			//				if(type.compareTo("number") == 0) currentFactor.setType(FactorType.number);
			//				else currentFactor.setType(FactorType.character);
			//			}
			//			currentFactor.setTag(attributs.getValue("tag"));
			//			if (currentFactor.getTag() == null)
			//				currentFactor.setTag("Within Subject");


			plugin.addFactor(currentFactor);
		}
		else if ((localName.equals("value"))&&(creatingFactors))
			currentFactor.addValue(attributs.getValue("name"), "");

		else if (localName.equals("measure")){
			String id = attributs.getValue("id");
			String parent = "";
			int index = id.lastIndexOf(".");
			if(index != -1) {
				parent = id.substring(0, index);
				id = id.substring(index+1, id.length());
			}
			String type = attributs.getValue("type");
			MeasureType mType = MeasureType.String;
			if(type != null) {
				try{
					mType = MeasureType.valueOf(type);
				} catch(Exception unknownType) {
					if(type.equalsIgnoreCase("Integer"))
						mType = MeasureType.Integer;
					if(type.equalsIgnoreCase("float"))
						mType = MeasureType.Float;
				}
			}
			String name = attributs.getValue("name");
			if(name == null) name = "";
			Measure measure = new Measure(true,true,id,parent,mType,name);
			plugin.addMeasure(measure);
		}

		else if (localName.equals("intertitle")){
			currentFunction = new Function();
			currentFunction.setName(attributs.getValue("id"));
			currentFunction.setClasse(attributs.getValue("class"));
		}
		else if (localName.equals("criterion")){
			currentFunction = new Function();
			currentFunction.setName(attributs.getValue("id"));
			currentFunction.setClasse(attributs.getValue("class"));
		}
		else if (localName.equals("constructor")){
			String nameFunction = currentFunction.getName();
			String classFunction = currentFunction.getClasse();
			currentFunction = new Function();
			currentFunction.setName(nameFunction);
			currentFunction.setClasse(classFunction);
			if(creatingIntertitles) plugin.addIntertitle(currentFunction);
			if(creatingCriteria) plugin.addCriterion(currentFunction);
		}

		else if (localName.equals("arg")){
			String type = attributs.getValue("type");
			if (creatingIntertitles)
				plugin.getPredefinedIntertitles().lastElement().addArgType(type);
			if (creatingCriteria)
				plugin.getPredefinedCriteria().lastElement().addArgType(type);
		}
		else if (localName.equals("block")){
			if (creatingBlocks) {
				currentFunction = new Function(attributs.getValue("id"),new Vector<String>(),attributs.getValue("class"));
				plugin.getPredefinedBlockClass().add(currentFunction);
			}
		}

	}


	public void endElement(String nameSpaceURI, String localName, String rawName) throws SAXException {

		if (localName.equals("factors"))
			creatingFactors = false;
		else if(localName.equals("intertitles"))
			creatingIntertitles = false;
		else if(localName.equals("criteria"))
			creatingCriteria = false;
		else if (localName.equals("blocks"))
			creatingBlocks = false;
	}	

	/**
	 * @return the plugin constructed by this parser.
	 */
	public Plugin getPlugin() {
		return plugin;
	}
}
