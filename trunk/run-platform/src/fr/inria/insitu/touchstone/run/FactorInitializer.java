package fr.inria.insitu.touchstone.run;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipFile;

import fr.inria.insitu.touchstone.run.utils.BasicFactory;

public class FactorInitializer {

	private static final Logger log = Logger.getLogger(BasicFactory.class.getName());

	private static FactorInitializer factorFactory = null;

	public static FactorInitializer getInstance() {
		if(factorFactory == null)
			factorFactory = new FactorInitializer();
		return factorFactory;
	}

	protected FactorInitializer() {
		super();
		load();
		loadAll();
	}

	/**
	 * Loads a properties from a resource name.
	 * @param factoryName the factory name
	 * @return the properties
	 */
	protected void load() {
		String resourceName = "resources/"+"factoriesForFactors.properties";
		InputStream in = 
			Platform.classloader.getResourceAsStream(resourceName);
		loadFromInputStream(in);
	}

	private void loadFromInputStream(InputStream in) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;
			line = br.readLine();
			while(line != null) {
				if(!line.startsWith("#")) {
					String[] parts = line.split(":");
					if(parts.length == 2) {
						Class<?> c = Class.forName(parts[0]);
						if (c == null) return;
						Constructor cons = c.getConstructor((Class[])null);
						cons.newInstance((Object[])null);
					}
				}
				line = br.readLine();
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads all the properties from the class path for the specified factory.
	 * @param factoryName the factory name
	 */
	public void loadAll() {
		String resourceName = "resources/"+"factoriesForFactors.properties";
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
					loadFromInputStream(in);
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
						loadFromInputStream(in);
					}
				}
			}
			catch(IOException e) {
				log.log(Level.SEVERE, "While trying to open jar file at "+path, e);
			}
		}
	}

}
