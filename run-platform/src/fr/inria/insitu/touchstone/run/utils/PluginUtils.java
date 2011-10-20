/*   TouchStone run platform is a software to run lab experiments. It is         *
 *   published under the terms of a BSD license (see details below)              *
 *   Author: Caroline Appert (appert@lri.fr)                                     *
 *   Copyright (c) 2010 Caroline Appert and INRIA, France.                       *
 *   TouchStone run platform reuses parts of an early version which were         *
 *   programmed by Jean-Daniel Fekete under the terms of a MIT (X11) Software    *
 *   License (Copyright (C) 2006 Jean-Daniel Fekete and INRIA, France)           *
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
package fr.inria.insitu.touchstone.run.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import fr.inria.insitu.touchstone.run.Platform;
import fr.inria.insitu.touchstone.run.Plugin;
import fr.inria.insitu.touchstone.run.input.Axes;

/**
 * <b>PluginUtils</b> implements utility function for declaring the plugin.
 * 
 */
public class PluginUtils {

    /** Logger. */
    private static final Logger   LOG                    = Logger.getLogger(PluginUtils.class.getName());
    
    
    /**
     * Export the plugin as an xml file.
     * @param p the plugin
     * @param filename the output file name
     * @return true if the plugin has been correctly exported
     */
    public static boolean exportAsXML(Plugin p, String filename, boolean mouseAndKeyboard) {
        return exportAsXML(p, new File(filename), mouseAndKeyboard);
    }
 
    /**
     * Export the plugin as an xml file.
     * @param p the plugin
     * @param filename the output file name
     * @return true if the plugin has been correctly exported
     */
    public static boolean exportAsXML(Plugin p, String filename) {
        return exportAsXML(p, new File(filename), false);
    }
    
    /**
     * Export the plugin as an xml file.
     * @param p the plugin
     * @param file the output file
     * @return true if the plugin has been correctly exported
     */
    public static boolean exportAsXML(Plugin p, File file, boolean mouseAndKeyboard) {
        try {
        	Element root = new Element("touchstone");
        	root.setAttribute("version", "1.0");
        	
            Element platform = new Element("platform");
            root.addContent(platform);
            
            DateFormat dateFormat = DateFormat.getDateTimeInstance();
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            platform.setAttribute("date", dateFormat.format(new Date())); //.toGMTString());
            Document doc = new Document(root);
            Element plugin = new Element("plugin");
            System.out.println("id: "+p.getProperties().getProperty(Plugin.PROPERTY_NAME, "unnamed"));
            plugin.setAttribute(
                    "id", 
                    p.getProperties().getProperty(Plugin.PROPERTY_NAME, "unnamed"));
            platform.addContent(plugin);
            plugin.addContent(exportFactorsAsXml());
    		plugin.addContent(exportMeasuresAsXml(mouseAndKeyboard));
    		
    		Element[] intertitlesAndBlocks = exportIntertitlesAndBlocksAsXml();
    		for(int i = 0; i < intertitlesAndBlocks.length; i++) {
    			plugin.addContent(intertitlesAndBlocks[i]);
    		}
    		plugin.addContent(exportCriteria());
    		
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            FileOutputStream fout = new FileOutputStream(file);
            BufferedOutputStream out = new BufferedOutputStream(fout);
            outputter.output(doc, out);
            out.close();
            fout.close();
            return true;
        }
        catch(Exception e) {
            LOG.log(Level.SEVERE, "Cannot create a DOM ", e);
            return false;
        }
    }
    
    
    
    /**
     * @return the union of the input axes used by this plugin
     * @see Plugin#getAxes()
     */
    public Axes getAxes() {
        Axes axes = new Axes();
        InputStream in = 
        	Platform.classloader.getResourceAsStream("resources/axes.properties");
        if (in != null)
            try {
                BufferedReader bin = new BufferedReader(new InputStreamReader(in));
                
                while(true) {
                    String line = bin.readLine();
                    if (line == null) break;
                    if (line.startsWith("#")) continue;
                    int index = line.indexOf(':');
                    if (index == -1) continue;
                    String[] s = line.substring(index+1).split(" ");
                    for (int i = 0; i < s.length; i++) {
                        String a = s[i];
                        axes.add(a.trim());
                    }
                }
        }
        catch(Exception e) {
            LOG.log(Level.WARNING, "Cannot load axes from resources/axes.properties", e);
        } 
        return axes;
    }
 
    private static Element exportFactorsAsXml() {
    	Element factors = new Element("factors");
    	try {
			BufferedReader br = new BufferedReader(new FileReader("src/resources/factors.description"));
			String line = br.readLine();
			String[] parts;
			while(line != null) {
				Element factorElement = new Element("factor");
				for(int i = 0; i < 4; i++) {
					parts = line.split(":");
					factorElement.setAttribute(parts[0], parts[1]);
					if(i==0) {
						factors.addContent(factorElement);
					}
					line = br.readLine();
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			LOG.log(Level.SEVERE, "You must run build.xml to generate the file *.properties and *.description");
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			BufferedReader br = new BufferedReader(new FileReader("src/resources/values.description"));
			String line = br.readLine();
			// skip comment
			line = br.readLine();
			String[] parts;
			while(line != null) {
				Element valueElement = new Element("value");
				for(int i = 0; i < 4; i++) {
					parts = line.split(":");
					if(i==0) {
						Element parentFactor = null;
						for(Iterator it = factors.getChildren().iterator(); it.hasNext(); ) {
							Element next = (Element) it.next();
							if(next.getAttributeValue("id").compareTo(parts[1]) == 0) {
								parentFactor = next;
								break;
							}
						}
						if(parentFactor == null) {
							parentFactor = new Element("factor");
							parentFactor.setAttribute("id", parts[1]);
							factors.addContent(parentFactor);
						}
						parentFactor.addContent(valueElement);
					} else {
						valueElement.setAttribute(parts[0], parts[1]);
					}
					line = br.readLine();
				}
				// skip blank line
				line = br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			LOG.log(Level.SEVERE, "You must run build.xml with target 'factories' to generate the file *.properties and *.description");
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return factors;
    }
    
    private static Element exportMeasuresAsXml(boolean mouseAndKeyboard) {
    	Element measures = new Element("measures");
		try {
			BufferedReader br = new BufferedReader(new FileReader("src/resources/measures.description"));
			String line = br.readLine();
			// skip first line
			line = br.readLine();
			String[] parts;
			while(line != null) {
				Element measureElement = new Element("measure");
				for(int i = 0; i < 4; i++) {
					parts = line.split(":");
					measureElement.setAttribute(parts[0], parts[1]);
					line = br.readLine();
				}
				measures.addContent(measureElement);
				// read next line
				line = br.readLine();
			}
			br.close();
			if(mouseAndKeyboard) {
				for (int i = 0; i < Platform.measuresMouse.length; i++) {
					Element measureElement = new Element("measure");
					measureElement.setAttribute("id", Platform.measuresMouse[i]);
					measureElement.setAttribute("type", "integer");
					measures.addContent(measureElement);
				}
				Collection<String> keyboardMappings = KeyMapJInputAWT.mappings.values();
				for (Iterator<String> iterator = keyboardMappings.iterator(); iterator
						.hasNext();) {
					Element measureElement = new Element("measure");
					measureElement.setAttribute("id", "Keyboard."+iterator.next());
					measureElement.setAttribute("type", "integer");
					measures.addContent(measureElement);
				}
			}
		} catch (FileNotFoundException e) {
			LOG.log(Level.SEVERE, "You must run build.xml to generate the file *.properties and *.description");
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return measures;
    }
    
    private static Element[] exportIntertitlesAndBlocksAsXml() {
    	BufferedReader br;
    	String[] internElementNames = {"intertitle", "block"};
    	Element[] res = new Element[2];
    	try {
    		for(int i = 0; i < 2; i++) {
    			Element elementParent = new Element(internElementNames[i]+"s");
    			br = new BufferedReader(new FileReader("src/resources/"+internElementNames[i]+"s.properties"));
    			String line = br.readLine();
    			String[] parts;
    			while(line != null) {
    				Element elementChild = new Element(internElementNames[i]);
    				parts = line.split(":");
    				elementChild.setAttribute("id", parts[1]);
    				line = br.readLine();
    				parts = line.split(":");
    				elementChild.setAttribute("class", parts[1]);
    				Class c = Class.forName(parts[1]);
    				Constructor[] constructors = c.getConstructors();
    				for(int j = 0; j < constructors.length; j++) {
    					Class[] argsType = constructors[j].getParameterTypes();
    					Element constructor = new Element("constructor");
    					boolean onlyPrimitiveTypes = true;
    					if(argsType.length > 0) {
    						for(int k = 0; k < argsType.length; k++) {
    							Element argType = new Element("arg");
    							String typeName = argsType[k].getCanonicalName();
    							boolean primitiveArg =
    								typeName.compareTo("java.lang.String") == 0 
    								|| typeName.compareTo("byte") == 0
    								|| typeName.compareTo("short") == 0
    								|| typeName.compareTo("int") == 0
    								|| typeName.compareTo("long") == 0
    								|| typeName.compareTo("float") == 0
    								|| typeName.compareTo("double") == 0;
    							onlyPrimitiveTypes = onlyPrimitiveTypes && primitiveArg;
    							if(!onlyPrimitiveTypes) break;
    							argType.setAttribute("type", typeName);
    							constructor.addContent(argType);
    						}
    					}
    					if(onlyPrimitiveTypes)
							elementChild.addContent(constructor);
    				}
    				elementParent.addContent(elementChild);
    				line = br.readLine();
    				line = br.readLine();
    			}
    			br.close();
    			res[i] = elementParent;
    		}
    	} catch (FileNotFoundException e) {
    		LOG.log(Level.SEVERE, "You must run build.xml to generate the file *.properties and *.description");
    	} catch (IOException e) {
    		e.printStackTrace();
    	} catch (ClassNotFoundException e) {
    		e.printStackTrace();
    	}
    	return res;
    }

    private static Element exportCriteria() {
    	BufferedReader br;
    	Element criteria = new Element("criteria");
    	try {
    		br = new BufferedReader(new FileReader("src/resources/criteria.properties"));
    		String line = br.readLine();
    		String[] parts;
    		while(line != null) {
    			Element elementChild = new Element("criterion");
    			parts = line.split(":");
    			elementChild.setAttribute("id", parts[1]);
    			line = br.readLine();
    			parts = line.split(":");
    			elementChild.setAttribute("class", parts[1]);
    			Class c = Class.forName(parts[1]);
    			Constructor[] constructors = c.getConstructors();
    			for(int j = 0; j < constructors.length; j++) {
    				Class[] argsType = constructors[j].getParameterTypes();
					Element constructor = new Element("constructor");
					boolean onlyPrimitiveTypes = true;
					if(argsType.length > 0) {
						for(int k = 0; k < argsType.length; k++) {
							Element argType = new Element("arg");
							String typeName = argsType[k].getCanonicalName();
							boolean primitiveArg =
								typeName.compareTo("java.lang.String") == 0 
								|| typeName.compareTo("byte") == 0
								|| typeName.compareTo("short") == 0
								|| typeName.compareTo("int") == 0
								|| typeName.compareTo("long") == 0
								|| typeName.compareTo("float") == 0
								|| typeName.compareTo("double") == 0;
							onlyPrimitiveTypes = onlyPrimitiveTypes && primitiveArg;
							if(!onlyPrimitiveTypes) break;
							argType.setAttribute("type", typeName);
							constructor.addContent(argType);
						}
					}
					if(onlyPrimitiveTypes)
						elementChild.addContent(constructor);
    			}
    			criteria.addContent(elementChild);
    			line = br.readLine();
    			line = br.readLine();
    		}
    		br.close();
    	} catch (FileNotFoundException e) {
    		LOG.log(Level.SEVERE, "You must run build.xml factories to generate the file *.properties and *.description");
    	} catch (IOException e) {
    		e.printStackTrace();
    	} catch (ClassNotFoundException e) {
    		e.printStackTrace();
    	}
    	return criteria;
    }
    
    private static void exportAsXML(File f, boolean mouseAndKeyboard) {
    	try {
    		Element root = new Element("touchstone");
        	root.setAttribute("version", "1.0");
        	
    		Element platform = new Element("platform");
    		root.addContent(platform);
    		
    		DateFormat dateFormat = DateFormat.getDateTimeInstance();
    		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    		platform.setAttribute("date", dateFormat.format(new Date())); //.toGMTString());
    		Document doc = new Document(root);
    		Element plugin = new Element("plugin");
    		plugin.setAttribute("id", "Core");
    		platform.addContent(plugin);
    		plugin.addContent(exportFactorsAsXml());
    		plugin.addContent(exportMeasuresAsXml(mouseAndKeyboard));
    		Element[] intertitlesAndBlocks = exportIntertitlesAndBlocksAsXml();
    		for(int i = 0; i < intertitlesAndBlocks.length; i++) {
    			plugin.addContent(intertitlesAndBlocks[i]);
    		}
    		plugin.addContent(exportCriteria());
    		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
    		FileOutputStream fout = new FileOutputStream(f);
    		BufferedOutputStream out = new BufferedOutputStream(fout);
    		outputter.output(doc, out);
    		out.close();
    		fout.close();
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public static boolean isBlockDefined(String blockID) {
    	// TODO
    	return false;
    }
    
    public static boolean isFactorDefined(String factorID) {
    	// TODO
    	return false;
    }
    
    public static boolean isValueDefined(String factorID, String valueID) {
    	// TODO
    	return false;
    }
    
    public static boolean isMeasureDefined(String measureID) {
    	// TODO
    	return false;
    }
    
    public static boolean isIntertitleDefined(String intertitle) {
    	// TODO
    	return false;
    }
    
    /**
     * To export this project as a description xml file.
     * @param pluginJars the arguments of the main program
     */
    public static void main(String[] pluginJars) {
        if (pluginJars.length != 0) {
            Plugin p = null;
            for (int i = 0; i < pluginJars.length; i++) {
                String[] jars = pluginJars[i].split(File.pathSeparator);
                System.out.println("Found "+jars.length+" items is path");
                for (int j = 0; j < jars.length; j++)
                    try {
                        p = Platform.getInstance().addPluginJar(jars[i]);
                        if (p != null) {
                            System.out.println("Found plugin");
                            break;
                        }
                    }
                    catch(Exception e) {
                        System.err.println("Plugin class "+pluginJars[i]+" cannot be registered in Platform");
                        e.printStackTrace();
                    }
            }
        }
        
        
        Plugin p = findPluginDescription();
        System.out.println("Exporting plugin");
        PluginUtils.exportAsXML(p, "plugin.xml");
        
	}

	private static Plugin findPluginDescription() {
		File f = new File("build");
		File[] files = f.listFiles();
		for (int i = 0; i < files.length; i++) {
			try {
				String nameFile = files[i].getCanonicalPath();
				if(!files[i].isDirectory() && nameFile.endsWith(".jar")) {
					return Platform.getInstance().addPluginJar(nameFile);
	            }
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
   
}
