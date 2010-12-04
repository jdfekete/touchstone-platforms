package fr.inria.insitu.touchstone.run.exp.parse;

import java.io.File;
import java.io.FileReader;
import java.util.Hashtable;
import java.util.LinkedList;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.helpers.XMLReaderFactory;

import fr.inria.insitu.touchstone.run.exp.model.Experiment;

/**
 * Parser for an experiment script.
 * 
 * @author Caroline Appert
 *
 */
public class SimpleXMLParse extends DefaultHandler {
	
	LinkedList<Object[]> eventsToProcess = new LinkedList<Object[]>();
	Experiment experiment;
    Locator currentLocator;
    File script;
	
    /**
	 * Builds a SimpleXMLParse.
	 * @param exp The experiment environment in which the script must be executed.
	 * @param script The name of the file containing the script
	 */
	public SimpleXMLParse(Experiment exp, File script) {
		this.experiment = exp;
        this.script = script;
		XMLReader xr;
		try {
			xr = XMLReaderFactory.createXMLReader();
			xr.setContentHandler(this);
			xr.setErrorHandler(this);
			FileReader r = new FileReader(script);
            InputSource is = new InputSource(r);
            is.setSystemId(script.getName());
			xr.parse(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Builds a SimpleXMLParse.
	 * @param exp The experiment environment in which the script must be executed.
	 * @param script The name of the file containing the script
	 */
	public SimpleXMLParse(Experiment exp, String script) {
		this(exp, new File(script));
	}
    
    /**
     * {@inheritDoc}
     */
    
    public void setDocumentLocator(Locator locator) {
        currentLocator = locator;
    }
    
    /**
     * @return a readable representation of the current file location
     */
    public Locator getLocator() {
        return new LocatorImpl(currentLocator);
    }
	
	/**
	 * {@inheritDoc}
	 */
	public void startElement (String uri, String name, String qName, Attributes atts)
	{
		Object[] element = new Object[3];
		element[0] = name;
		element[1] = new Hashtable();
        element[2] = getLocator();
		int nb = atts.getLength();
		Hashtable hTable;
		for(int i = 0; i < nb; i++) {
			hTable = (Hashtable)element[1];
			hTable.put(atts.getQName(i), atts.getValue(i));
		}
		eventsToProcess.add(element);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void endElement (String uri, String name, String qName)
	{
		Object[] element = new Object[2];        
		element[0] = name;
        element[1] = getLocator();
		eventsToProcess.add(element);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void endDocument() {
		experiment.setSystemEvents(eventsToProcess);
	}
	
}
