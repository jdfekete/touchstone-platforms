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
package fr.inria.insitu.touchstone.run;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipFile;

import fr.inria.insitu.touchstone.run.utils.BasicFactory;

/**
 * <code>Factor</code> is the base class to extend in order
 * to define new factors. Usually, it is not recommended to use
 * this class but rather its two subclasses (<code>CharacterFactor</code>
 * and <code>NumericalFactor</code>). 
 * 
 * @see CharacterFactor
 * @see NumericalFactor
 * @author Caroline Appert
 *
 */
public abstract class Factor extends BasicFactory {
	
	private static final Logger LOG = Logger.getLogger(Factor.class.getName());
	protected Object value;
	protected String keyValue = "";
	
	/**
	 * The mapping between a factor id and the factory 
	 * that can build the values of this factor.
	 */
	private static HashMap<String, Factor> factoriesForFactors = null;
	
	static {
		factoriesForFactors = new HashMap<String, Factor>();
		loadFactoryClasses();
	}
	
	/**
	 * Builds a factor given its id.
	 * @param id the factor id (that must be used in the experiment script).
	 */
	protected Factor(String id) {
		super(id, true);
	}
	
	/**
	 * Returns a factor given its id.
	 * @param idFactor the factor id.
	 * @return the factor whose id is <code>idFactor</code>
	 */
	public static Factor getFactor(String idFactor) {
		return factoriesForFactors.get(idFactor);
	}
	
	private static void loadFactoryClasses() {
		String resourceName = "resources/factoriesForFactors.properties";
		InputStream in = 
			Factor.class.getClassLoader().getResourceAsStream(resourceName);
		BufferedReader br = null;
		if(in != null) {
			br = new BufferedReader(new InputStreamReader(in));
			registerFactorsFromStream(br);
		}
		
        String classpath = System.getProperty("java.class.path");
        String sep = System.getProperty("path.separator");
        String[] cp = classpath.split(sep);
        for (int i = 0; i < cp.length; i++) {
            String path = cp[i];
            if (path.endsWith(".jar")) try { 
                JarFile jar = new JarFile(new File(path), false, ZipFile.OPEN_READ);
                JarEntry entry = jar.getJarEntry(resourceName);
                if (entry != null) {
                    in = jar.getInputStream(entry);
                    if (in != null) {
                    	br = new BufferedReader(new InputStreamReader(in));
                    	registerFactorsFromStream(br);
                    }
                }
            }
            catch(IOException e) {
                LOG.log(Level.SEVERE, "While trying to open jar file at "+path, e);
            }
        }
	}
	
	@SuppressWarnings("unchecked")
	private static void registerFactorsFromStream(BufferedReader br) {
		String line = null;
		try {
			line = br.readLine();
			line = br.readLine();
			while(line != null) {
				String[] parts = line.split(":");
				String factorID = parts[1];
				if(factoriesForFactors.get(factorID) == null) {
					try {
						Class factoryClass = Class.forName(parts[0]);
						Class[] classesArgs = { String.class };
						Object[] args = { factorID };
						Factor factorFactory;
						try {
							Constructor<Factor> constructor = factoryClass.getConstructor(classesArgs);
							factorFactory = constructor.newInstance(args);
						} catch (NoSuchMethodException e) {
							factorFactory = (Factor)(factoryClass.newInstance());
						}
						factoriesForFactors.put(factorID, factorFactory);
					} catch (ClassNotFoundException e) {
						LOG.log(Level.SEVERE, "Cannot find factory for factor "+factorID+" in classpath", e);
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
				line = br.readLine();
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
     * Return the value of a factor given the factor id.
     * @param idFactor the factor id
     * @param keyValue the key of the value
     * @return a the value object or null
     */
    public static Object getValue(String idFactor, String keyValue) {
    	Factor bf = getFactor(idFactor);
		if(bf == null) return null;
		return bf.getValue(keyValue);
    }
    
	/**
	 * Returns the value of this <code>Factor</code> given the id value.
	 * @param idValue The id value
	 * @return the value having key <code>idValue</code> of this factor.
	 */
	protected Object getValue(String idValue) {
		return idValue;
	}
	
	/**
	 * @return the current value of this <code>Factor</code>.
	 */
	public Object getValue() {
		return value;
	}
	
    /**
     * Called when the value of this factor is set from an experiment script.
     * Overrides it to specify specific treatments when setting the value a factor.
     * @param value the value to set.
     */
    public void setValue(Object value) {
    	this.value = value;
    }

	public Double getDoubleValue() {
		if(getValue() instanceof Number)
			return ((Number)getValue()).doubleValue();
		else {
			return Double.parseDouble(getValue().toString());
		}
	}

	public Long getLongValue() {
		if(getValue() instanceof Number)
			return ((Number)getValue()).longValue();
		else {
			return (long)Double.parseDouble(getValue().toString());
		}
	}
	
	public Integer getIntValue() {
		return getLongValue().intValue();
	}
	
	public String getStringValue() {
		if(getValue() == null) return "null";
		return getValue().toString();
	}
    
    /**
     * Called when the value of this factor is set from an experiment script.
     * @param value the key of the value to set.
     */
	public final void setKeyValue(String value) {
		keyValue = value;
		setValue(getValue(value));
	}
	
	public String getKeyValue() {
		return keyValue;
	}
}
