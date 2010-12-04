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
 * Parses the experiment XML script to get:
 * <ul>
 * <li> the participants list </li>
 * <li> the number of blocks per participant </li>
 * <li> the number of trials per block </li>
 * </ul>
 * 
 * @author Caroline Appert
 *
 */
public class ParticipantsXMLParse extends DefaultHandler {
	
	// Participant x Block x Trial
	private Hashtable<String, LinkedList<Integer>> expeGeneralData = new Hashtable<String, LinkedList<Integer>>();
    private Locator currentLocator;
    private LinkedList<Integer> currentParticipant;
	private int nbTrialsInCurrentBlock;
    /**
	 * Builds a ParticipantsXMLParse.
	 * @param script The name of the file containing the script
	 */
	public ParticipantsXMLParse(File script) {
        this.nbTrialsInCurrentBlock = 0;
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
	 * Builds a ParticipantsXMLParse.
	 * @param script The name of the file containing the script
	 */
	public ParticipantsXMLParse(String script) {
		this(new File(script));
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
		if(name.compareTo("run") == 0) {
			currentParticipant = new LinkedList<Integer>();
			expeGeneralData.put(qName, currentParticipant);
		} else {
			if(name.compareTo("block") == 0 || name.compareTo("practice") == 0) {
				nbTrialsInCurrentBlock = 0;
			} else {
				if(name.compareTo("trial") == 0) {
					nbTrialsInCurrentBlock++;
				}
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void endElement (String uri, String name, String qName)
	{
		if(name.compareTo("block") == 0 || name.compareTo("practice") == 0) {
			currentParticipant.add(new Integer(nbTrialsInCurrentBlock));
		}
	}
	
	public int getNbBlocks(String participant) {
		return expeGeneralData.get(participant).size();
	}
	
	public int getNbTrials(String participant, int block) {
		return expeGeneralData.get(participant).get(block-1);
	}
	
}
