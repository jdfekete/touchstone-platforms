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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;

import fr.inria.insitu.touchstone.design.motor.Plugin;


public class NameClassMap {

	private JarFileLoader 							jarFileLoader;

	private Hashtable<String, Class<?>> 			intertitles = new Hashtable<String, Class<?>>();
	private Hashtable<String, Class<?>> 			blocks = new Hashtable<String, Class<?>>();
	private Hashtable<String, Class<?>> 			criteria = new Hashtable<String, Class<?>>();
	private Hashtable<Class<?>, ArrayList<String>> 	measures = new Hashtable<Class<?>, ArrayList<String>>();

	
	private ArrayList<String> 						factorsID = new ArrayList<String>();
	private ArrayList<Class<?>> 					factorsClasses = new ArrayList<Class<?>>();
	private ArrayList<Hashtable<String, Class<?>>> 	factorsValues = new ArrayList<Hashtable<String, Class<?>>>();
	
	public NameClassMap() {
		URL urls [] = {};
		jarFileLoader = new JarFileLoader (urls);
	}
	
	public void addLibraries(String[] libraries) {
		URL urls [] = {};
		jarFileLoader = new JarFileLoader (urls);
		try {
//			jarFileLoader.addFile("/Users/appert/Documents/workspace/exp-stationary-postures/lib/posturesRecognizers.jar");
			for (int i = 0; i < libraries.length; i++) {
				jarFileLoader.addFile(libraries[i]);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public void registerJars(Vector<Plugin> pluginObjects) {
		for (int i = 0; i < pluginObjects.size(); i++) {
			registerJar(pluginObjects.get(i).getJarFile());
		}
		for (int i = 0; i < pluginObjects.size(); i++) {
			registerValues(pluginObjects.get(i).getJarFile());
		}
	}

	public void registerJar(File file) {
		try {
			jarFileLoader.addFile(file.getAbsolutePath());
			JarFile jar = new JarFile(file, false, ZipFile.OPEN_READ);
			
			// intertitles
			JarEntry entry = jar.getJarEntry("resources/intertitles.properties");
			if (entry != null) {
				InputStream in = jar.getInputStream(entry);
				if (in != null) {
					Properties props = new Properties();
					props.load(in);
					for (int i = 0; i < 1000; i++) {
						String suffix = "." + i;
						String nameProperty = props.getProperty("name" + suffix);
						String classProperty = props.getProperty("class" + suffix);
						if(nameProperty == null || classProperty == null)
							break;
						intertitles.put(nameProperty, jarFileLoader.loadClass(classProperty));
					}
				}
			}
			
			// blocks
			entry = jar.getJarEntry("resources/blocks.properties");
			if (entry != null) {
				InputStream in = jar.getInputStream(entry);
				if (in != null) {
					Properties props = new Properties();
					props.load(in);
					for (int i = 0; i < 1000; i++) {
						String suffix = "." + i;
						String nameProperty = props.getProperty("name" + suffix);
						String classProperty = props.getProperty("class" + suffix);
						if(nameProperty == null || classProperty == null)
							break;
						blocks.put(nameProperty, jarFileLoader.loadClass(classProperty));
					}
				}
			}
			
			// criteria
			entry = jar.getJarEntry("resources/criteria.properties");
			if (entry != null) {
				InputStream in = jar.getInputStream(entry);
				if (in != null) {
					Properties props = new Properties();
					props.load(in);
					for (int i = 0; i < 1000; i++) {
						String suffix = "." + i;
						String nameProperty = props.getProperty("name" + suffix);
						String classProperty = props.getProperty("class" + suffix);
						if(nameProperty == null || classProperty == null)
							break;
						criteria.put(nameProperty, jarFileLoader.loadClass(classProperty));
					}
				}
			}
			
			// measures
			entry = jar.getJarEntry("resources/measuresExported.properties");
			if (entry != null) {
				InputStream in = jar.getInputStream(entry);
				if (in != null) {
					BufferedReader br = new BufferedReader(new InputStreamReader(in)); 
					String line = br.readLine();
					while(line != null) {
						String[] parts = line.split(":");
						if(parts.length == 2) {
							Class<?> exportingClass = jarFileLoader.loadClass(parts[0]);
							ArrayList<String> m = measures.get(exportingClass);
							if(m == null) m = new ArrayList<String>();
							m.add(parts[1]);
							measures.put(exportingClass, m);
						}
						line = br.readLine();
					}
				}
			}
			
			// factors
			entry = jar.getJarEntry("resources/factoriesForFactors.properties");
			if (entry != null) {
				InputStream in = jar.getInputStream(entry);
				if (in != null) {
					BufferedReader br = new BufferedReader(new InputStreamReader(in));
					String line = br.readLine();
					while(line != null) {
						String[] parts = line.split(":");
						if(parts.length != 2) {
							line = br.readLine();
							continue;
						}
						String factorID = parts[1];
//						System.out.println(parts[0]+":"+parts[1]);
						if(!factorsID.contains(factorID)) {
							factorsID.add(factorID);
//							System.out.println("--> class="+jarFileLoader.loadClass(parts[0]));
							factorsClasses.add(jarFileLoader.loadClass(parts[0]));
							factorsValues.add(new Hashtable<String, Class<?>>());
						}
						line = br.readLine();
					}
				}
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void registerValues(File file) {

		try {
			jarFileLoader.addFile(file.getAbsolutePath());
			JarFile jar = new JarFile(file, false, ZipFile.OPEN_READ);
			
			// values
			JarEntry entry = jar.getJarEntry("resources/values.properties");
			if (entry != null) {
				InputStream in = jar.getInputStream(entry);
				if (in != null) {
					Properties props = new Properties();
					props.load(in);
					for (int i = 0; i < 1000; i++) {
						String suffix = "." + i;
						String factorProperty = props.getProperty("factor" + suffix);
						String nameProperty = props.getProperty("name" + suffix);
						String classProperty = props.getProperty("class" + suffix);
						if(factorProperty == null || nameProperty == null || classProperty == null)
							break;
						int index = factorsID.indexOf(factorProperty);
						factorsValues.get(index).put(nameProperty, jarFileLoader.loadClass(classProperty));
					}
				}
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public Class<?> getClassForFactor(String idFactor) {
		int index = factorsID.indexOf(idFactor);
		if(index < 0) return null;
		return factorsClasses.get(index);
	}

	public Class<?> getClassForValue(String idFactor, String idValue) {
		int index = factorsID.indexOf(idFactor);
		if(index < 0) return null;
		return factorsValues.get(index).get(idValue);
	}

	public Class<?> getClassForBlock(String idBlock) {
		return blocks.get(idBlock);
	}
	
	public Class<?> getClassForIntertitle(String idIntertitle) {
		return intertitles.get(idIntertitle);
	}
	
	public Class<?> getClassForCriterion(String idCriterion) {
		return criteria.get(idCriterion);
	}
	
	public ArrayList<String> getMeasuresForClass(Class<?> cl) {
		ArrayList<String> res = new ArrayList<String>();
		for (Enumeration<Class<?>> enumeration = measures.keys(); enumeration.hasMoreElements(); ) {
			Class<?> next = enumeration.nextElement();
			if(next.isAssignableFrom(cl))
				res.addAll(measures.get(next));
		}
		return res;
	}

}
