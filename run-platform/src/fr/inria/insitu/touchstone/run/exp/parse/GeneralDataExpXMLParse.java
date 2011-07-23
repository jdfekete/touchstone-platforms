package fr.inria.insitu.touchstone.run.exp.parse;

import java.io.File;
import java.io.FileReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.helpers.XMLReaderFactory;

import fr.inria.insitu.touchstone.run.Platform;

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
public class GeneralDataExpXMLParse extends DefaultHandler {
	
	// Participant x Block x Trial
	private Hashtable<String, LinkedList<Integer>> expeGeneralData = new Hashtable<String, LinkedList<Integer>>();
	private String experimentName;
    private Locator currentLocator;
    private LinkedList<Integer> currentParticipant;
	private int nbTrialsInCurrentBlock;
	
	private boolean oscEnabled = false;
	private int oscPortPlatform = Platform.DEFAULT_OSC_PORT;
	private Vector<String> oscHostsClients = new Vector<String>();
	private Vector<Integer> oscPortsClients = new Vector<Integer>();
	
    /**
	 * Builds a ParticipantsXMLParse.
	 * @param script The name of the file containing the script
	 */
	public GeneralDataExpXMLParse(File script) {
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
	public GeneralDataExpXMLParse(String script) {
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
		
		if(name.compareTo("experiment") == 0) {
			experimentName = atts.getValue("id");
		} else if(name.compareTo("run") == 0) {
			currentParticipant = new LinkedList<Integer>();
			expeGeneralData.put(atts.getValue("id"), currentParticipant);
		} else if(name.compareTo("block") == 0 || name.compareTo("practice") == 0) {
			nbTrialsInCurrentBlock = 0;
		} else if(name.compareTo("trial") == 0) {
			nbTrialsInCurrentBlock++;
		} else if(name.compareTo("osc") == 0) {
			oscEnabled = true;
			String portPlatform = atts.getValue("port");
			oscPortPlatform = portPlatform != null ? 
					Integer.parseInt(portPlatform) : oscPortPlatform;
		} else if(name.compareTo("client") == 0) {
			String hostClient = atts.getValue("host");
			oscHostsClients.add(hostClient);
			String portClient = atts.getValue("port");
			oscPortsClients.add(portClient != null ? 
					Integer.parseInt(portClient) : -1);
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
	
	public Enumeration<String> getParticipantsID() {
		return expeGeneralData.keys();
	}
	
	public int getNbBlocks(String participant) {
		return expeGeneralData.get(participant).size();
	}
	
	public int getNbTrials(String participant, int block) {
		return expeGeneralData.get(participant).get(block-1);
	}

	public String getExperimentName() {
		return experimentName;
	}

	public boolean isOSCEnabled() {
		return oscEnabled;
	}

	public void setOSCEnabled(boolean enabled) {
		oscEnabled = enabled;
	}

	public int getOSCPortPlatform() {
		return oscPortPlatform;
	}

	public void setOSCPortPlatform(int port) {
		oscPortPlatform = port;
	}

	public Vector<String> getOSCHostsClients() {
		return oscHostsClients;
	}

	public void setOSCHostsClients(Vector<String> oscHostsClients) {
		this.oscHostsClients = oscHostsClients;
	}

	public Vector<Integer> getOSCPortsClients() {
		return oscPortsClients;
	}

	public void setOSCPortsClients(Vector<Integer> oscPortsClients) {
		this.oscPortsClients = oscPortsClients;
	}

}
