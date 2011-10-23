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

import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.TimeZone;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipFile;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import fr.inria.insitu.touchstone.run.Platform;

/**
 * <b>BasicFactory</b> is the base class for all the factories
 * used in TouchStone.
 * 
 */
public class BasicFactory {
    private static final Logger log = Logger.getLogger(BasicFactory.class.getName());
    private HashMap<String,Object> entry;
    private String name;
    protected static HashMap<String,BasicFactory> factories = new HashMap<String,BasicFactory>();
    
    /**
     * Creates a BasicFactory with a specified name and loads
     * the creators (use only with a simple subclass creator).
     * 
     * @param name the Factory name
     */
    protected BasicFactory(String name) {
        entry = new HashMap<String,Object>();
        this.name = name;
        factories.put(name, this);
        addDefaultCreators(name);
    }
    
    protected BasicFactory(String name, boolean add) {
        entry = new HashMap<String,Object>();
        this.name = name;
        factories.put(name, this);
    }

    //Entry management for factories that need it.
    /**
     * Returns the entry associated with a specified name
     * in the Factory or null.
     * @param name the name
     * @return the entry of null
     */
    public Object getEntry(String name) {
        return entry.get(name);
    }
    
    /**
     * Associates a specified entry to a specified name
     * in the Factory using a priority.
     * @param name the name
     * @param o the entry
     * @return <code>true</code> if the entry has been
     * added
     */
    public boolean putEntry(String name, Object o) {
        return entry.put(name, o) != null;
    }
    
    /**
     * @return an Iterator over the names of the entries
     */
    public Iterator<String> iterator() {
        return entry.keySet().iterator();
    }
    
    /**
     * @return an Iterator over the names of the factories
     */
    public static Iterator factoriesIterator() {
        return factories.keySet().iterator();
    }
    
    /**
     * Return a factory given its name.
     * @param name the name
     * @return a factory or null
     */
    public static BasicFactory getFactory(String name) {
        return (BasicFactory)factories.get(name);
    }
    
    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
    
    public void addClass(String name, Class theClass) {
        putEntry(name, theClass);
    }
    
    /**
     * Returns the class object given its name in the factory.
     * @param name The name
     * @return The class
     */
    public Class<?> getClassFor(String name) {
        return (Class<?>)getEntry(name);
    }
    
    protected Object createFor(String name) throws Exception {
    	Class<?> c = getClassFor(name);
    	if (c == null) return null;
    	Constructor cons = c.getConstructor((Class[])null);
    	return cons.newInstance((Object[])null);
    }
    
    /**
     * Creates an object for the specified class named and the
     * list or arguments.
     * @param name the name
     * @param args the arguments
     * @return the created object or null
     */
    public Object createFor(String name, Object[] args) throws Exception {
        if (args == null)
            return createFor(name);
        Class<?> c = getClassFor(name);
        if (c == null) return null;
        Class<?>[] types = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
        	types[i] = args[i].getClass();
        	if (types[i] == Integer.class) {
        		types[i] = int.class;
        	}
        	else if (types[i]==Character.class) {
        		types[i] = char.class;
        	}
        	else if (types[i]==Double.class) {
        		types[i] = double.class;
        	}
        }
        Constructor cons = c.getConstructor(types);
        return cons.newInstance(args);
    }
    
    protected void addDefaultCreators(Properties p) {
        for (int i = 0; i < 1000; i++) {
            String suffix = "." + i;
            String nameProperty = p.getProperty("name" + suffix);
            String classProperty = p.getProperty("class" + suffix);
            if(nameProperty == null || classProperty == null) {
            	break;
            }
            try {
                addClass(nameProperty, Class.forName(classProperty));
            } catch (ClassNotFoundException e) {
                log.log(Level.SEVERE, "Cannot create class for name "+classProperty, e.getMessage());
            }
        }
    }
    
    protected void addDefaultCreators(String factoryName) {
    	Properties props1 = loadProperties(factoryName);
    	addDefaultCreators(props1);
        loadAllProperties(factoryName);
    }
    
    /**
     * Loads a properties from a resource name.
     * @param factoryName the factory name
     * @return the properties
     */
    public static Properties loadProperties(String factoryName) {
    	String resourceName = "resources/"+factoryName+".properties";
        InputStream in = 
        	Platform.classloader.getResourceAsStream(resourceName);
        Properties props = new Properties();
        if (in != null)
            try {
            props.load(in);
            return props;
        }
        catch(Exception e) {
            log.log(Level.WARNING, "Cannot load properties from "+resourceName, e);
        } 
        return props;
    }
    
    /**
     * Loads all the properties from the class path for the specified factory.
     * @param factoryName the factory name
     */
    public void loadAllProperties(String factoryName) {
        factoryName = factoryName.toLowerCase();
        String resourceName = "resources/"+factoryName+".properties";
        String classpath = System.getProperty("java.class.path");
        String sep = System.getProperty("path.separator");
        String[] cp = classpath.split(sep);
        for (int i = 0; i < cp.length; i++) {
            String path = cp[i];
            File f = new File(path);
            if (f.isDirectory()) {
                File res = new File(f, resourceName);
                if (res.canRead()) try {
                    FileInputStream in = new FileInputStream(res);
                    Properties props = new Properties();
                    props.load(in);
                    addDefaultCreators(props);
                }
                catch(Exception e) {
                    ; // ignore errors
                }
            }
            else if (path.endsWith(".jar")) try {
                JarFile jar = new JarFile(new File(path), false, ZipFile.OPEN_READ);
                JarEntry entry = jar.getJarEntry(resourceName);
                if (entry != null) {
                    InputStream in = jar.getInputStream(entry);
                    if (in != null) {
                        Properties props = new Properties();
                        props.load(in);
                        addDefaultCreators(props);
                    }
                }
            }
            catch(IOException e) {
                log.log(Level.SEVERE, "While trying to open jar file at "+path, e);
            }
        }
    }
    
    
    /**
     * Exports the contents of this factory as an XML file.
     * @param filename the filenamem to save
     * @return true if no error occured
     */
    public boolean exportAsXML(String filename) {
        return exportAsXML(new File(filename));
    }
    
    /**
     * Exports the contents of this factory as an XML file.
     * @param file the file to save
     * @return true if no error occured
     */
    public boolean exportAsXML(File file) {
        try {
            Element root = new Element(getName());
            DateFormat dateFormat = DateFormat.getDateTimeInstance();
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            root.setAttribute("date", dateFormat.format(new Date())); //.toGMTString());
            Document doc = new Document(root);
            for (Iterator iter = iterator(); iter.hasNext(); ) {
                String name = (String)iter.next();
                Class c = (Class)getEntry(name);
                BeanInfo info = Introspector.getBeanInfo(c);
                BeanDescriptor desc = info.getBeanDescriptor();
                Element entry = new Element("entry");
                entry.setAttribute("name", name);
                entry.setAttribute("className", desc.getName());
                PropertyDescriptor[] props = info.getPropertyDescriptors();
                for (int i = 0; i < props.length; i++) {
                    PropertyDescriptor p = props[i];
                    if (p.getReadMethod() != null 
                            && p.getWriteMethod() != null) {
                        Element property = new Element("property");
                        property.setAttribute("name", p.getName());
                        property.setAttribute("type", p.getPropertyType().getName());
                        entry.addContent(property);
                    }
                }
                root.addContent(entry);
            }
            
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            FileOutputStream fout = new FileOutputStream(file);
            BufferedOutputStream out = new BufferedOutputStream(fout);
            outputter.output(doc, out);
            out.close();
            fout.close();
            return true;
        }
        catch(Exception e) {
            log.log(Level.SEVERE, "Cannot create a DOM ", e);
            return false;
        }
    }
}