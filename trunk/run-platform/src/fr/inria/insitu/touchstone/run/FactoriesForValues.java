package fr.inria.insitu.touchstone.run;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipFile;

import fr.inria.insitu.touchstone.run.utils.BasicFactory;

class ValueFactory extends BasicFactory {

	protected ValueFactory(String idFactor) {
		super(idFactor);
	}

	protected void addDefaultCreators(String factoryName) { }

}

public class FactoriesForValues {

	private static final Logger LOG = Logger.getLogger(FactoriesForValues.class.getName());
	/**
	 * The mapping between a factor id and the factory 
	 * that can build the values of this factor.
	 */
	private static HashMap<String, BasicFactory> factoriesForFactors = new HashMap<String, BasicFactory>();

	private static FactoriesForValues instance = null;
	
	public static FactoriesForValues getInstance() {
		if(instance == null) { 
			instance = new FactoriesForValues();
			instance.loadFactoryClasses();
		}
		return instance;
	}
	
	private void loadFactoryClasses() {
		Properties p = loadValues();
		addCreators(p);
		loadAllProperties();
	}
	
	private void addCreators(Properties p) {
		for (int i = 0; i < 1000; i++) {
            String suffix = "." + i;
            String nameFactor = p.getProperty("factor" + suffix);
            
            BasicFactory factorFactory = BasicFactory.getFactory(nameFactor);
            if(factorFactory == null) {
            	factorFactory = new ValueFactory(nameFactor);
            }
            
            String nameProperty = p.getProperty("name" + suffix);
            String classProperty = p.getProperty("class" + suffix);
            if(nameProperty == null || classProperty == null) {
            	break;
            }
            try {
//            	System.out.println("add ("+nameProperty+", "+classProperty+") to "+nameFactor);
            	factorFactory.addClass(nameProperty, Class.forName(classProperty));
            } catch (ClassNotFoundException e) {
                LOG.log(Level.SEVERE, "Cannot create class for name "+classProperty, e.getMessage());
            }
        }
	}

	public Properties loadValues() {
		String resourceName = "resources/"+"values.properties";
		InputStream in = 
			Platform.classloader.getResourceAsStream(resourceName);
		Properties props = new Properties();
		if (in != null)
			try {
				props.load(in);
				return props;
			}
		catch(Exception e) {
			LOG.log(Level.WARNING, "Cannot load properties from "+resourceName, e);
		} 
		return props;
	}

	public void loadAllProperties() {
		String resourceName = "resources/"+"values.properties";
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
					addCreators(props);
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
						addCreators(props);
					}
				}
			}
			catch(IOException e) {
				LOG.log(Level.SEVERE, "While trying to open jar file at "+path, e);
			}
		}
	}

	public static ValueFactory getFactor(String idFactor) {
		BasicFactory factorFactory = BasicFactory.getFactory(idFactor);
        if(factorFactory instanceof ValueFactory) {
        	return (ValueFactory)factorFactory;
        }
        return null;
	}

}
