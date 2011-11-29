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
package fr.inria.insitu.touchstone.design.torun;

import java.awt.Component;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import fr.inria.insitu.touchstone.design.motor.Plugin;
import fr.inria.insitu.touchstone.run.Platform;
import fr.inria.insitu.touchstone.run.endConditions.AbstractEndCondition;
import fr.inria.insitu.touchstone.run.exp.model.Block;
import fr.inria.insitu.touchstone.run.exp.model.Intertitle;
import fr.inria.insitu.touchstone.run.exp.parse.DecomposeExpressionLexer;
import fr.inria.insitu.touchstone.run.exp.parse.DecomposeExpressionParser;

/**
 * This code generation works independently from the design platform, i.e. it only uses the XML script to generate what is necessary.
 * 
 * @author appert
 *
 */
public class CodeGeneration {

	private LinkedList<HashMap<String, String>> measures;
	private String experimentID;
	private String experimentAuthor;
	private String experimentDescription;
	private Component parent;
	private LinkedList<String> fileAlreadyGenerated;
	private String currentCharacterFactor;
	private Vector<String> factors;
	private File rootDirectory;
	private Vector<Plugin> pluginObjects;

	private NameClassMap nameClassMap = new NameClassMap();


	public CodeGeneration(File rootDirectory, File script, Vector<Plugin> pluginObjects, Component parent, boolean generateClassesForCharacterValues, String[] libraries) {
		this.rootDirectory = rootDirectory;
		this.pluginObjects = pluginObjects;
		this.factors = new Vector<String>();

		File dirRoot = new File(rootDirectory.getAbsolutePath());
		if(!dirRoot.exists()) dirRoot.mkdirs();

		this.parent = parent;
		this.currentCharacterFactor = null;
		this.fileAlreadyGenerated = new LinkedList<String>();

		if(libraries != null) {
			copyJarFiles(libraries);
			nameClassMap.addLibraries(libraries);
		}
		copyJarFiles(pluginObjects);
		nameClassMap.registerJars(pluginObjects);

		measures = new LinkedList<HashMap<String, String>>();

		XMLReader xr;
		HandlerXML handler = new HandlerXML(this, generateClassesForCharacterValues);
		try {
			xr = XMLReaderFactory.createXMLReader();
			xr.setContentHandler(handler);
			xr.setErrorHandler(handler);
			FileReader r = new FileReader(script);
			InputSource is = new InputSource(r);
			is.setSystemId(script.getName());
			xr.parse(is);
		} catch (Exception e) {
			e.printStackTrace();
		}

		copyBuildFiles();
		generateXMLBuild();
		generateInputConfFile();
	}

	private void generateInputConfFile() {
		File dest = new File(rootDirectory.getAbsolutePath()+File.separator+"input.conf");
		if(dest.exists()) {
			Object[] options = {"Yes", "No"};
			int n = JOptionPane.showOptionDialog(parent,
					"Overwrite existing file input.conf?",
					"Warning", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					options[1]);
			if(n != 0) return;
			else dest.delete();
		}
		try {
			dest.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getSimpleClassName(String expression) {
		int[] parentheses = getParenthesesIndices(expression);
		if(parentheses == null) return expression;
		else return expression.substring(0, parentheses[0]);
	}

	public static Class<?>[] getArgumentsTypes(String expression) {
		String[] args = getArguments(expression);
		if(args == null) return null;
		Class<?>[] argsClasses = new Class[args.length];
		for(int i = 0; i < args.length; i++) {
			argsClasses[i] = getType(args[i]);
		}
		return argsClasses;
	}

	public static String[] getArguments(String expression) {
		char c;
		boolean inString = false;
		String currentArg = "";
		Vector<String> res = new Vector<String>();
		int[] parentheses = getParenthesesIndices(expression);
		if(parentheses == null) return null;
		String arguments = expression.substring(parentheses[0]+1, parentheses[1]);
//		System.out.println(expression+" --> arguments="+arguments);
		if(arguments.length() == 0) return null;
		for(int i = 0; i < arguments.length(); i++) {
			c = arguments.charAt(i);
			if(c == '{') {
				inString = true;
			} else if(c == '}') {
				inString = false;
			}
			if(c == ',' && !inString) {
				res.add(currentArg);
				currentArg = "";
			} else {
				currentArg+=c;
			}
		}
		res.add(currentArg);

		String[] args = new String[res.size()];
		int i = 0;
		for (Iterator<String> iterator = res.iterator(); iterator.hasNext();) {
			String str = iterator.next();
			args[i] = str;
			i++;
		}
		return args;
	}

	private static Class<?> getType(String expression) {
		try {
			Integer.parseInt(expression);
			return int.class;
		} catch(NumberFormatException nfe0) {
			try {
				Long.parseLong(expression);
				return long.class;
			} catch(NumberFormatException nfe1) {
				try {
					Double.parseDouble(expression);
					return double.class;
				} catch(NumberFormatException nfe2) {
					if(expression.length() == 1) return char.class;
					// Strings always have a length of at least 2 since they are surrounded by '{' and '}'
					else return String.class;
				}
			}
		}
	}

	static int[] getParenthesesIndices(String expression) {
		int[] res = new int[2];
		int firstParenthesis = -1;
		int lastParenthesis = -1;
		for(int i = 0; i < expression.length(); i++) {
			char c = expression.charAt(i);
			if(c == '(') firstParenthesis = i;
		}
		if(firstParenthesis == -1) return null;
		for(int i = expression.length() - 1; i >= 0; i--) {
			char c = expression.charAt(i);
			if(c == ')') lastParenthesis = i;
		}
		if(lastParenthesis == -1) return null;
		res[0] = firstParenthesis;
		res[1] = lastParenthesis;
		return res;
	}

	class HandlerXML extends DefaultHandler {

		private CodeGeneration codeGeneration;
		private boolean generateClassesForCharacterValues;

		public HandlerXML(CodeGeneration codeGeneration, boolean generateClassesForCharacterValues) {
			this.codeGeneration = codeGeneration;
			this.generateClassesForCharacterValues = generateClassesForCharacterValues;
		}

		private HashMap<String, String> copyAttributes(Attributes atts) {
			HashMap<String, String> copy = new HashMap<String, String>();
			for(int i = 0; i < atts.getLength(); i++) {
				copy.put(atts.getQName(i), atts.getValue(i));
			}
			return copy;
		}

		/**
		 * {@inheritDoc}
		 */
		public void startElement (String uri, String name, String qName, Attributes atts) {
			if(name.compareTo("experiment") == 0) {
				experimentID = atts.getValue("id").toLowerCase();
				experimentAuthor = atts.getValue("author").toLowerCase();
				experimentDescription = atts.getValue("description").toLowerCase();
				experimentDescription = experimentDescription.replaceAll("\n", " ");
				experimentDescription = experimentDescription.replaceAll("\"", "\\\\\"");
				generatePluginDescription();
				return;
			}
			if(name.compareTo("measure") == 0) {
				measures.add(copyAttributes(atts));
				return;
			}
			if(name.compareTo("factor") == 0) {
				factors.add(atts.getValue("id"));
				String type = atts.getValue("type").toLowerCase();
				if(type.equalsIgnoreCase("Integer") || type.equalsIgnoreCase("Float")) {
					String factorID = atts.getValue("id");
					String factorName = atts.getValue("name");
					// First, check if this factor exists
					Class<?> cl = nameClassMap.getClassForFactor(factorID);
					// If not, generate it
					if(cl == null) generateNumberFactor(factorID, factorName);
				}
				else {
					currentCharacterFactor = atts.getValue("id");
					// First, check if this factor exists
					Class<?> cl = nameClassMap.getClassForFactor(currentCharacterFactor);
					// If not, generate it
					if(cl == null) generateCharacterFactor(atts.getValue("name"));
				}
				return;
			}
			if(name.compareTo("value") == 0) {
				if(currentCharacterFactor == null) return;
				// First, check if this character value is already defined as an object of a more complex type than a string
				String idValue = atts.getValue("id");
				Class<?> cl = nameClassMap.getClassForValue(currentCharacterFactor, idValue);
				// If not, generate it
				if(cl == null && generateClassesForCharacterValues) generateCharacterValue(idValue);
				return;
			}
			if(name.compareTo("block") == 0 || name.compareTo("practice") == 0) {
				String classExpression = atts.getValue("class");
				String className = getSimpleClassName(classExpression);

				Class<?> cl = nameClassMap.getClassForBlock(className);
				Class<?>[] args = getArgumentsTypes(classExpression);
				if(cl != null) {
					Constructor<?> constructor = null;
					try {
						constructor = cl.getConstructor(args);
					} catch (SecurityException e) { e.printStackTrace();
					} catch (NoSuchMethodException e) { }
					// the right class exists but not with the right constructor
					// --> generate a subclass of the existing class
					if(constructor == null) {
						generateBlock(className, cl, args);
					} else {
						// the right class with the right constructor exists --> nothing to do
					}
				} else { 
					generateBlock(className, Block.class, args);
				}

				String criterionExpression = atts.getValue("criterionTrial");
				if(criterionExpression != null) generateCriteriaList(criterionExpression);
				return;
			}

			if(name.compareTo("setup") == 0 || name.compareTo("interblock") == 0 || name.compareTo("intertrial") == 0) {
				String intertitleExpression = atts.getValue("class");
				String className = getSimpleClassName(intertitleExpression);

				Class<?> cl = nameClassMap.getClassForIntertitle(className);
				Class<?>[] args = getArgumentsTypes(intertitleExpression);
				if(cl != null) {
					Constructor<?> constructor = null;
					try {
						constructor = cl.getConstructor(args);
					} catch (SecurityException e) { e.printStackTrace();
					} catch (NoSuchMethodException e) { }
					// the right class exists but not with the right constructor
					// --> generate a subclass of the existing class
					if(constructor == null) {
						if(name.compareTo("setup") == 0) {
							removeMeasuresAlreadyExported(cl);
							generateSetUp(className, cl, args);
						} else 
							generateIntertitle(className, cl, args, false);
					} else {
						// the right class with the right constructor exists --> nothing to do for an interblock or intertrial
						// if some measures remain, generate new set up
						if(name.compareTo("setup") == 0) {
							removeMeasuresAlreadyExported(cl);
							if(measures.size() != 0) generateSetUp(className, cl, args);	
						}
					}
				} else { 
					if(name.compareTo("setup") == 0) {
						removeMeasuresAlreadyExported(Intertitle.class);
						generateSetUp(className, Intertitle.class, args);
					} else 
						generateIntertitle(className, Intertitle.class, args, false);
				}

				String criterionExpression = atts.getValue("criterion");
				if(criterionExpression != null && criterionExpression.length() > 0) generateCriteriaList(criterionExpression);
				return;
			}
		}

		public void endElement(String uri, String localName, String name)
		throws SAXException {
			if(name.compareTo("factor") == 0)
				currentCharacterFactor = null;
		}

		public void endDocument() throws SAXException {
			try {
				codeGeneration.copyIntoSRC();
			} catch (IOException e) {
				System.err.println("Exception while copying generated files from src-generated/ to src/");
				e.printStackTrace();
			}
		}

	}

	private void copyBuildFiles() {
		try {
			File directoryOfFilesToCopy = new File("filesToCopyForGeneration");
			File[] filesToCopy = directoryOfFilesToCopy.listFiles();
			for (int i = 0; i < filesToCopy.length; i++) {
				if(!filesToCopy[i].getName().startsWith("build")) continue;
				File dest = new File(rootDirectory.getAbsolutePath()+File.separator+filesToCopy[i].getName());
				fileCopy(filesToCopy[i], dest);
				dest.setExecutable(true);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void copyJarFiles(Vector<Plugin> pluginObjects) {
		try {
			File libDirectory = new File(rootDirectory.getAbsolutePath()+File.separator+"lib");
			libDirectory.mkdirs();
			for (int j = 0; j < pluginObjects.size(); j++) {
				if(pluginObjects.get(j).getJarFile() != null && pluginObjects.get(j).getId().compareTo("Core") != 0) {
					File copiedFile = new File(libDirectory.getAbsolutePath()+File.separator+pluginObjects.get(j).getJarFile().getName());
					fileCopy(pluginObjects.get(j).getJarFile(), copiedFile);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void copyJarFiles(String[] jarFiles) {
		try {
			File libDirectory = new File(rootDirectory.getAbsolutePath()+File.separator+"lib");
			libDirectory.mkdirs();
			for (int j = 0; j < jarFiles.length; j++) {
				File copiedFile = new File(libDirectory.getAbsolutePath()+File.separator+(new File(jarFiles[j])).getName());
				fileCopy(new File(jarFiles[j]), copiedFile);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void generateXMLBuild() {
		try {
			File xmlBuild = new File(rootDirectory.getAbsolutePath()+File.separator+"build.xml");
			PrintWriter pw = new PrintWriter(xmlBuild);
			String s = 
				"<?xml version=\"1.0\"?>\n" +
				"<project name=\""+experimentID+"\" default=\"default\">\n" +
				"\t<description>\n" +
				"\t"+experimentDescription+"\n" +
				"\t</description>\n";
			pw.write(s);

			s = "\n" +
			"\t<import file=\"../run-platform/touchstone-plugin.xml\"/>\n" +
			"\n";
			pw.write(s);

			s = 
				"\t<property name=\"Name\" value=\""+experimentID+"\"/>\n" +
				"\t<property name=\"name\" value=\""+experimentID+"\"/>\n" +
				"\t<property name=\"year\" value=\""+new GregorianCalendar().get(Calendar.YEAR)+"\"/>\n" +
				"\t<property name=\"Vendor\" value=\""+experimentAuthor+"\"/>\n" +
				"\t<property name=\"URL\" value=\"http://touchstone.lri.fr/\"/>\n\n";
			pw.write(s);

			s =
				"\t<!-- dependencies to other plugins -->\n\n" +
				"\t<property name=\"dependencies\" value=\"";
			for(int i = 0; i < this.pluginObjects.size(); i++) {
				s += this.pluginObjects.get(i).getId()+" ";
			}
			s += "\"/>\n";
			s += "\t<path id=\"deps-jars\">\n";
			for(int i = 0; i < pluginObjects.size(); i++) {
				if(pluginObjects.get(i).getJarFile() != null && pluginObjects.get(i).getId().compareTo("Core") != 0)
					s += "\t\t<pathelement location=\"lib/"+pluginObjects.get(i).getJarFile().getName()+"\"/>\n";
			}
			s += "\t</path>\n\n";
			pw.write(s);

			s =
				"\t<!-- =================================\n" +
				"\t\ttarget: init\n" +
				"\t================================= -->\n\n" +
				"\t<target name=\"init\" depends=\"touchstone-plugin.init\" description=\"--> Local initializations\">\n";
			s += "\t</target>\n\n";
			pw.write(s);

			s =     
				"\t<!-- =================================\n" + 
				"\t\ttarget: default\n" +          
				"\t================================= -->\n\n" +
				"\t<target name=\"default\" depends=\"jar\" description=\"--> description\">\n" +
				"\t</target>\n\n";
			pw.write(s);

			s = "</project>\n";
			pw.write(s);	

			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void generateCriterion(String className, Class<?> classToExtend, Class<?>[] args) {

		File dirPackage = new File(rootDirectory.getAbsolutePath()+File.separator+"src-generated"+File.separator+experimentID);
		dirPackage.mkdirs();
		String idCriterionForCode = getValidIDFor(className);
		File fileCriterion = new File(rootDirectory.getAbsolutePath()+File.separator+"src-generated"+File.separator+experimentID+File.separator+idCriterionForCode+".java");
		if(fileAlreadyGenerated.contains(fileCriterion.getAbsolutePath()))
			return;
		try {
			PrintWriter pw = new PrintWriter(fileCriterion);
			pw.write("package "+experimentID+";\n\n");
			pw.write("import fr.inria.insitu.touchstone.run.Platform;\n");
			//			pw.write("import "+classToExtend.getPackage().getName()+".*;\n");
			pw.write("import java.awt.event.InputEvent;\n");
			pw.write("import javax.swing.Timer;\n");
			pw.write("import fr.inria.insitu.touchstone.run.input.AxesEvent;\n");
			pw.write("import com.illposed.osc.OSCMessage;\n");

			pw.write("/**\n");
			pw.write(" *\n");
			pw.write(" * @touchstone.criterion "+className+"\n");
			pw.write(" */\n");

			pw.write("public class "+idCriterionForCode+" extends "+classToExtend.getName()+" {\n");

			// constructor
			pw.write("\tpublic "+idCriterionForCode+"(");
			if(args != null) {
				for(int i = 0; i < args.length; i++) {
					if(i!=0) pw.write(", ");
					pw.write(args[i].getSimpleName()+" arg"+i);
				}
			}
			pw.write(") {\n");
			pw.write("\t\tsuper();\n");
			pw.write("\t}\n");

			pw.write("\tpublic String getEndCondition() {\n");
			pw.write("\t\t// TODO define here the message that you want to be logged when this condition is reached\n");
			pw.write("\t\treturn null;\n");
			pw.write("\t}\n");

			pw.write("\tpublic boolean isReached(Timer timer, long when) {\n");
			pw.write("\t\t// This method is called each time a timer which is registered in the Platform expires\n");
			pw.write("\t\treturn false;\n");
			pw.write("\t}\n");

			pw.write("\tpublic boolean isReached(InputEvent e) {\n");
			pw.write("\t\t// This method is called each time an event occurs on a graphical component that is registered in the Platform\n");
			pw.write("\t\treturn false;\n");
			pw.write("\t}\n");

			pw.write("\tpublic boolean isReached(AxesEvent e) {\n");
			pw.write("\t\t// This method is called each time an axis which is listened by the Platform changes\n");
			pw.write("\t\treturn false;\n");
			pw.write("\t}\n");

			pw.write("\tpublic boolean isReached(OSCMessage message, long when) {\n");
			pw.write("\t\t// This method is called each time the Platform receives an OSC message\n");
			pw.write("\t\treturn false;\n");
			pw.write("\t}\n");

			pw.write("\tpublic void start() {\n");
			pw.write("\t\t// This method is called when this criterion is attached to an experiment component\n");
			pw.write("\t}\n");

			pw.write("\tpublic void stop() {\n");
			pw.write("\t\t// This method is called when this criterion is detached to an experiment component\n");
			pw.write("\t}\n");

			pw.write("}\n");
			pw.close();

			fileAlreadyGenerated.add(fileCriterion.getAbsolutePath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void writeHeaderForMeasures(PrintWriter pw) {
		for (Iterator<HashMap<String,String>> iterator = measures.iterator(); iterator.hasNext();) {
			HashMap<String,String> measure = iterator.next();
			String idMeasure = measure.get("id");
			if(factors.contains(idMeasure)) continue;
			pw.write(" * @touchstone.measure "+measure.get("id")+"\n");
			pw.write(" * \tid: "+idMeasure+"\n");
			pw.write(" * \tname: "+measure.get("name")+"\n");
			pw.write(" * \thelp: "+measure.get("help")+"\n");
			pw.write(" * \ttype: "+measure.get("type")+"\n");
			pw.write("\n");
		}
	}

	private void writeCodeForMeasures(PrintWriter pw) {
		// register measures
		for (Iterator<HashMap<String,String>> iterator = measures.iterator(); iterator.hasNext();) {
			HashMap<String,String> measure = iterator.next();
			String type = measure.get("type");
			String id = measure.get("id");
			if(factors.contains(id)) continue;
			if(type.compareToIgnoreCase("integer") == 0) {
				pw.write("\t\tPlatform.getInstance().addIntegerMeasure(\""+id+"\");\n");
			} else {
				if(type.compareToIgnoreCase("float") == 0) {
					pw.write("\t\tPlatform.getInstance().addDoubleMeasure(\""+id+"\");\n");
				} else {
					pw.write("\t\tPlatform.getInstance().addMeasure(new Measure(\""+id+"\") {\n");
					pw.write("\t\t\tpublic Object getValue() {\n");
					pw.write("\t\t\t\t// TODO automatically generated\n");
					pw.write("\t\t\t\treturn null;\n");
					pw.write("\t\t\t}\n");
					pw.write("\t\t});\n");
				}
			}
		}
		pw.write("\t\t// TODO do not forget to register the graphical component that must be tracked \n" +
		"\t\t// with Platform.getInstance().registerComponent(componentToTrack).\n" +
		"\t\t// This makes the platform able to update the values of Mouse.* and Keyboard.* measures\n" +
		"\t\t// and evaluates the current criterion each time an input event occurs on this component.\n");
	}


	private void generateBlock(String className, Class<?> classToExtend, Class<?>[] args) {
		File dirPackage = new File(rootDirectory.getAbsolutePath()+File.separator+"src-generated"+File.separator+experimentID);
		dirPackage.mkdirs();
		String idBlockForCode = getValidIDFor(className);
		File fileBlock = new File(rootDirectory.getAbsolutePath()+File.separator+"src-generated"+File.separator+experimentID+File.separator+idBlockForCode+".java");
		if(fileAlreadyGenerated.contains(fileBlock.getAbsolutePath())) 
			return;
		try {
			PrintWriter pw = new PrintWriter(fileBlock);
			pw.write("package "+experimentID+";\n\n");
			pw.write("import fr.inria.insitu.touchstone.run.*;\n");
			//			pw.write("import "+classToExtend.getPackage().getName()+".*;\n");
			pw.write("import fr.inria.insitu.touchstone.run.Platform.EndCondition;\n");
			pw.write("/**\n");

			pw.write(" *\n");
			pw.write(" * @touchstone.block "+className+"\n");
			pw.write(" */\n");

			pw.write("public class "+idBlockForCode+" extends "+classToExtend.getName()+" {\n");

			pw.write("\tpublic "+idBlockForCode+"(");

			if(args != null) {
				for(int i = 0; i < args.length; i++) {
					if(i!=0) pw.write(", ");
					pw.write(args[i].getSimpleName()+" arg1");
				}
			}
			pw.write(") {\n");


			pw.write("\t\tsuper();\n");
			if(args != null && args.length != 0 && classToExtend.equals(Block.class)) {
				pw.write("\t\t// TODO add arguments to 'super' call if required\n");
			}

			pw.write("\t}\n");

			pw.write("\tpublic void beginBlock() {\n");
			pw.write("\t}\n");

			pw.write("\tpublic void beginTrial() { }\n");

			pw.write("\tpublic void endTrial(EndCondition ec) { }\n");

			pw.write("\tpublic void endBlock() { }\n");

			pw.write("}\n");
			pw.close();

			fileAlreadyGenerated.add(fileBlock.getAbsolutePath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void generateIntertitle(String className, Class<?> classToExtend, Class<?>[] args, boolean codeForMeasures) {

		File dirPackage = new File(rootDirectory.getAbsolutePath()+File.separator+"src-generated"+File.separator+experimentID);
		dirPackage.mkdirs();
		String idIntertitleForCode = getValidIDFor(className);
		File fileIntertitle = new File(rootDirectory.getAbsolutePath()+File.separator+"src-generated"+File.separator+experimentID+File.separator+idIntertitleForCode+".java");
		if(fileAlreadyGenerated.contains(fileIntertitle.getAbsolutePath())) 
			return;
		try {
			PrintWriter pw = new PrintWriter(fileIntertitle);
			pw.write("package "+experimentID+";\n\n");
			pw.write("import fr.inria.insitu.touchstone.run.*;\n");
			//			pw.write("import "+classToExtend.getPackage().getName()+".*;\n");
			pw.write("/**\n");

			if(codeForMeasures) {
				//				removeMeasuresAlreadyExported(classToExtend);
				writeHeaderForMeasures(pw);
			}

			pw.write(" *\n");
			pw.write(" * @touchstone.intertitle "+className+"\n");
			pw.write(" */\n");

			pw.write("public class "+idIntertitleForCode+" extends "+classToExtend.getName()+" {\n");

			// constructor
			pw.write("\tpublic "+idIntertitleForCode+"(");
			if(args != null) {
				for(int i = 0; i < args.length; i++) {
					if(i!=0) pw.write(", ");
					pw.write(args[i].getSimpleName()+" arg1");
				}
			}
			pw.write(") {\n");
			pw.write("\t\tsuper();\n");

			if(codeForMeasures)
				writeCodeForMeasures(pw);

			pw.write("\t}\n");

			pw.write("\tpublic void beginIntertitle() { }\n");

			pw.write("\tpublic void endIntertitle() { }\n");

			pw.write("}\n");
			pw.close();


			fileAlreadyGenerated.add(fileIntertitle.getAbsolutePath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void generateSetUp(String className, Class<?> classToExtend, Class<?>[] args) {
		generateIntertitle(className, classToExtend, args, true);
	}


	private void generateCharacterFactor(String factorName) {
		File dirPackage = new File(rootDirectory.getAbsolutePath()+File.separator+"src-generated"+File.separator+experimentID);
		dirPackage.mkdirs();
		String idFactorForCode = getValidIDFor(currentCharacterFactor);
		File fileFactor = new File(rootDirectory.getAbsolutePath()+File.separator+"src-generated"+File.separator+experimentID+File.separator+"Factor_"+idFactorForCode+".java");
		if(fileAlreadyGenerated.contains(fileFactor.getAbsolutePath())) 
			return;
		try {
			PrintWriter pw = new PrintWriter(fileFactor);
			pw.write("package "+experimentID+";\n\n");
			pw.write("import fr.inria.insitu.touchstone.run.CharacterFactor;\n");
			pw.write("/**\n");
			pw.write(" *\n");
			pw.write(" * @touchstone.factor "+currentCharacterFactor+"\n");
			if(factorName.trim().length() > 0) pw.write(" *  name: "+factorName+"\n");
			pw.write(" */\n");

			pw.write("public class Factor_"+idFactorForCode+" extends CharacterFactor {\n");

			// constructor
			pw.write("\tpublic Factor_"+idFactorForCode+"() {\n");
			pw.write("\t\tsuper(\""+currentCharacterFactor+"\");\n");
			pw.write("\t}\n");

			pw.write("}\n");
			pw.close();


			fileAlreadyGenerated.add(fileFactor.getAbsolutePath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void generateCharacterValue(String idValue) {
		File dirPackage = new File(rootDirectory.getAbsolutePath()+File.separator+"src-generated"+File.separator+experimentID);
		dirPackage.mkdirs();
		String idValueForCode = getValidIDFor(idValue);
		File fileValue = new File(rootDirectory.getAbsolutePath()+File.separator+"src-generated"+File.separator+experimentID+File.separator+"Value_"+idValueForCode+".java");
		if(fileAlreadyGenerated.contains(fileValue.getAbsolutePath())) {
			return;
		}
		try {
			PrintWriter pw = new PrintWriter(fileValue);
			pw.write("package "+experimentID+";\n\n");
			pw.write("/**\n");
			pw.write(" *\n");
			pw.write(" * @touchstone.value "+idValue+"\n");
			pw.write(" * \tfactor: "+currentCharacterFactor+"\n");
			pw.write(" */\n");

			pw.write("public class Value_"+idValueForCode+" {\n");

			// constructor
			pw.write("\tpublic Value_"+idValueForCode+"() { }\n");

			pw.write("\tpublic String toString() {\n");
			pw.write("\t\treturn \""+idValue+"\";\n");
			pw.write("\t }\n");

			pw.write("}\n");
			pw.close();


			fileAlreadyGenerated.add(fileValue.getAbsolutePath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static String getValidIDFor(String id) {
		String res = "";
		for (int i = 0; i < id.length(); i++) {
			if((id.charAt(i) >= 'a' && id.charAt(i) <= 'z') ||
					(id.charAt(i) >= 'A' && id.charAt(i) <= 'Z') ||
					(id.charAt(i) >= '0' && id.charAt(i) <= '9') ||
					(id.charAt(i) == '_') ) {
				res += (""+id.charAt(i));				
			} else {
				res += "_";
			}
		}
		return res;
	}

	private void generateNumberFactor(String factorID, String factorName) {
		File dirPackage = new File(rootDirectory.getAbsolutePath()+File.separator+"src-generated"+File.separator+experimentID);
		dirPackage.mkdirs();
		String idFactorForCode = getValidIDFor(factorID);
		File fileFactor = new File(rootDirectory.getAbsolutePath()+File.separator+"src-generated"+File.separator+experimentID+File.separator+"Factor_"+idFactorForCode+".java");
		if(fileAlreadyGenerated.contains(fileFactor.getAbsolutePath())) 
			return;
		try {
			PrintWriter pw = new PrintWriter(fileFactor);
			pw.write("package "+experimentID+";\n\n");
			pw.write("import fr.inria.insitu.touchstone.run.NumericalFactor;\n");
			pw.write("/**\n");
			pw.write(" *\n");
			pw.write(" * @touchstone.factor "+factorID+"\n");
			if(factorName.trim().length() > 0) pw.write(" *  name: "+factorName+"\n");
			pw.write(" */\n");

			pw.write("public class Factor_"+idFactorForCode+" extends NumericalFactor {\n");

			// constructor
			pw.write("\tpublic Factor_"+idFactorForCode+"() {\n");
			pw.write("\t\tsuper(\""+factorID+"\");\n");
			pw.write("\t}\n");

			pw.write("}\n");
			pw.close();


			fileAlreadyGenerated.add(fileFactor.getAbsolutePath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static void fileCopy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	private void copyIntoSRC() throws IOException {
		if(!showOverwriteDialog()) return;
		File dirSrcGenerated = new File(rootDirectory.getAbsolutePath()+File.separator+"src-generated"+File.separator+experimentID);
		File dirSrc = new File(rootDirectory.getAbsolutePath()+File.separator+"src"+File.separator+experimentID);
		if(!dirSrc.exists()) dirSrc.mkdirs();
		File[] generatedFiles = dirSrcGenerated.listFiles();
		for (int i = 0; i < generatedFiles.length; i++) {
			File copy = new File(dirSrc, generatedFiles[i].getName());
			fileCopy(generatedFiles[i], copy);
		}
		dirSrcGenerated.delete();
	}

	private boolean showOverwriteDialog() {
		Object[] options = { "YES", "NO" };
		JOptionPane optionPane = new JOptionPane(
				"Do you want to output generated files into folder src?\nIf some files already exist they will be overwritten.",
				JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
				null, options, options[1]);
		JDialog warningDialog = optionPane.createDialog(parent, "Generation confirmation");
		warningDialog.setVisible(true);
		Object selectedValue = optionPane.getValue();
		if(selectedValue == null)
			return false;
		for(int counter = 0, maxCounter = options.length;
		counter < maxCounter; counter++) {
			if(options[counter].equals(selectedValue))
				if(counter == 0) return true;
		}
		return false;
	}

	private boolean isMouseMeasure(String measureName) {
		String[] parts = measureName.split(".");
		return parts.length > 0 && parts[0].compareTo("Mouse") == 0;
		//		for(int i = 0; i < Platform.measuresMouse.length; i++)
		//			if(measureName.compareTo(Platform.measuresMouse[i]) == 0) return true;
		//		return false;
	}

	private boolean isKeyboardMeasure(String measureName) {
		String[] parts = measureName.split(".");
		return parts.length > 0 && parts[0].compareTo("Keyboard") == 0;
	}

	private void removeMeasuresAlreadyExported(Class<?> classToExtend) {
		for(int i = 0; i < measures.size(); i++) {
			HashMap<String, String> m = (HashMap<String, String>)measures.get(i);
			String measureName = m.get("id");
			if(Platform.getInstance().isAxisDefined(measureName)
					|| Platform.getInstance().isMeasureDefined(measureName)
					|| isMouseMeasure(measureName)
					|| isKeyboardMeasure(measureName)
					|| factors.contains(measureName)) {
				measures.remove(m);
				i--;
			}
		}
		ArrayList<String> m = nameClassMap.getMeasuresForClass(classToExtend);
		for (Iterator<String> iterator = m.iterator(); iterator.hasNext();) {
			removeMeasure(iterator.next());
		}

		m = nameClassMap.getMeasuresForClass(Block.class);
		for (Iterator<String> iterator = m.iterator(); iterator.hasNext();) {
			removeMeasure(iterator.next());
		}

		for (int i = 0; i < fr.inria.insitu.touchstone.run.exp.model.Experiment.MEASURES_ALWAYS_AVAILABLE.length; i++) {
			removeMeasure(fr.inria.insitu.touchstone.run.exp.model.Experiment.MEASURES_ALWAYS_AVAILABLE[i]);
		}
	}

	private void removeMeasure(String idMeasure) {
		HashMap<String, String> next = null;
		for (Iterator<HashMap<String, String>> iterator = measures.iterator(); iterator.hasNext();) {
			next = iterator.next();
			if(next.get("id").compareTo(idMeasure) == 0)
				break;
			next = null;
		}
		if(next != null) measures.remove(next);
	}

	private void generateCriteriaList(String criterionExpression) {
		// parse before to identify all simple criteria in the expression
		DecomposeExpressionLexer lexerTmp = new DecomposeExpressionLexer(new StringReader(criterionExpression));
		DecomposeExpressionParser parserTmp = new DecomposeExpressionParser(lexerTmp);
		Vector<String> listCriteria = null;
		try {
			listCriteria = parserTmp.expr();
		} catch (RecognitionException e1) {
			e1.printStackTrace();
		} catch (TokenStreamException e1) {
			e1.printStackTrace();
		}
		for (Iterator<String> iterator = listCriteria.iterator(); iterator.hasNext();) {
			String simpleCriterion = iterator.next();
			String simpleCriterionClassName = getSimpleClassName(simpleCriterion);
			if(simpleCriterion != null) {
				Class<?> cl = nameClassMap.getClassForCriterion(simpleCriterionClassName);
				Class<?>[] args = getArgumentsTypes(simpleCriterion);
				if(cl != null) {
					Constructor<?> constructor = null;
					try {
						constructor = cl.getConstructor(args);
					} catch (SecurityException e) { e.printStackTrace();
					} catch (NoSuchMethodException e) { }
					// the right class exists but not with the right constructor
					// --> generate a subclass of the existing class
					if(constructor == null) {
						generateCriterion(simpleCriterionClassName, cl, args);
					} else {
						// the right class with the right constructor exists --> nothing to do
					}
				} else { 
					generateCriterion(simpleCriterionClassName, AbstractEndCondition.class, args);
				}

			}
		}
	}

	private void generatePluginDescription() {

		File dirPackage = new File(rootDirectory.getAbsolutePath()+File.separator+"src-generated"+File.separator+experimentID);
		dirPackage.mkdirs();
		File filePluginDescription = new File(rootDirectory.getAbsolutePath()+File.separator+"src-generated"+File.separator+experimentID+File.separator+"PluginDescription.java");
		if(fileAlreadyGenerated.contains(filePluginDescription.getAbsolutePath()))
			return;
		try {
			PrintWriter pw = new PrintWriter(filePluginDescription);
			pw.write("package "+experimentID+";\n\n");
			pw.write("import java.util.Properties;\n");
			pw.write("import fr.inria.insitu.touchstone.run.*;\n");
			pw.write("import fr.inria.insitu.touchstone.run.input.Axes;\n");

			pw.write("public class PluginDescription implements Plugin {\n");

			pw.write("\tprivate static final Properties PROPERTIES = new Properties();\n");
			pw.write("\tstatic {\n"+
					"\t\tPROPERTIES.setProperty(PROPERTY_NAME, \""+experimentID+"\");\n" +
					"\t\tPROPERTIES.setProperty(PROPERTY_AUTHOR, \""+experimentAuthor+"\");\n" +
					"\t\tPROPERTIES.setProperty(PROPERTY_URL, \"XXXExperiment urlXXX\");\n" +
					"\t\tPROPERTIES.setProperty(PROPERTY_DESCRIPTION, \""+experimentDescription+"\");\n" +
			"\t}\n\n");

			pw.write("\tpublic static void main(String[] args) {\n"+
					"\t\tLaunchExperiment.main(args);\n" +
			"\t}\n\n");


			pw.write("\tpublic String getName() {\n"+
					"\t\treturn (String)PROPERTIES.get(PROPERTY_NAME);\n" +
			"\t}\n\n");

			pw.write("\tpublic Properties getProperties() {\n"+
					"\t\treturn PROPERTIES;\n" +
			"\t}\n\n");

			pw.write("\tpublic Axes getAxes() {\n"+
					"\t\treturn null;\n" +
			"\t}\n\n");

			pw.write("\tpublic void install(Platform platform) { }\n\n");

			pw.write("\tpublic void desinstall(Platform platform) { }\n\n");

			pw.write("}\n");
			pw.close();

			fileAlreadyGenerated.add(filePluginDescription.getAbsolutePath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}

