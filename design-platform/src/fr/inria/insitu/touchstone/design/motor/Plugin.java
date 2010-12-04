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

import fr.inria.insitu.touchstone.design.graphic.widgets.Function;

import java.io.File;
import java.io.Serializable;
import java.util.Vector;


public class Plugin implements Serializable {

	private static final long serialVersionUID = 42L;
	
	String id = "";

	private boolean loadFactors = true;
	private boolean loadMeasures = true;
	private boolean loadIntertitles = true;
	private boolean loadCriteria = true;
	private boolean loadBlockClass = true;
	private Vector<Factor> predefinedFactors = new Vector<Factor>();
	private Vector<Measure> predefinedMeasures = new Vector<Measure>();
	private Vector<Function> predefinedIntertitles = new Vector<Function>();
	private Vector<Function> predefinedCriteria = new Vector<Function>();
	private Vector<Function> predefinedBlockClass = new Vector<Function>();
	private File xmlFile;
	private File jarFile;
	
	public void addFactor(Factor f){
		predefinedFactors.add(f);
	}
	public void addMeasure(Measure m){
		predefinedMeasures.add(m);
	}
	public void addIntertitle(Function f){
		predefinedIntertitles.add(f);
	}
	public void addCriterion(Function f){
		predefinedCriteria.add(f);
	}
	public void addBlockClass(Function f){
		predefinedBlockClass.add(f);
	}
	
	public String getId() {
		return id;
	}

	public Vector<Factor> getPredefinedFactors() {
		if (loadFactors)
			return predefinedFactors;
		else
			return new Vector<Factor>();
	}

	public Vector<Measure> getPredefinedMeasures() {
		if (loadMeasures)
			return predefinedMeasures;
		else 
			return new Vector<Measure>();
	}

	public Vector<Function> getPredefinedIntertitles() {
		if (loadIntertitles)
			return predefinedIntertitles;
		else
			return new Vector<Function>();
	}

	public Vector<Function> getPredefinedCriteria() {
		if (loadCriteria)
			return predefinedCriteria;
		else
			return new Vector<Function>();
	}

	public Vector<Function> getPredefinedBlockClass() {
		if (loadBlockClass)
			return predefinedBlockClass;
		else
			return new Vector<Function>();
	}	
	
	public boolean loadFactors() {
		return loadFactors;
	}
	public void setLoadFactors(boolean loadFactors) {
		this.loadFactors = loadFactors;
	}
	public boolean loadMeasures() {
		return loadMeasures;
	}
	public void setLoadMeasures(boolean loadMeasures) {
		this.loadMeasures = loadMeasures;
	}
	public boolean loadIntertitles() {
		return loadIntertitles;
	}
	public void setLoadIntertitles(boolean loadIntertitles) {
		this.loadIntertitles = loadIntertitles;
	}
	public boolean loadCriteria() {
		return loadCriteria;
	}
	public void setLoadCriteria(boolean loadCriteria) {
		this.loadCriteria = loadCriteria;
	}
	public boolean loadBlockClass() {
		return loadBlockClass;
	}
	public void setLoadBlockClass(boolean loadBlockClass) {
		this.loadBlockClass = loadBlockClass;
	}	
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String toString() {
		return id;
	}
	
	public void setXMLFile(File xmlFile) {
		this.xmlFile = xmlFile;
	}
	
	public File getXMLFile() {
		return xmlFile;
	}
	public File getJarFile() {
		return jarFile;
	}
	public void setJarFile(File jarFile) {
		this.jarFile = jarFile;
	}
	
}
