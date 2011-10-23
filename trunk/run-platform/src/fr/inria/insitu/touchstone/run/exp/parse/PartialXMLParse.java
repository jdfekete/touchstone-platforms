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

import fr.inria.insitu.touchstone.run.CharacterFactor;
import fr.inria.insitu.touchstone.run.FactorInitializer;
import fr.inria.insitu.touchstone.run.FactoriesForValues;
import fr.inria.insitu.touchstone.run.NominalFactor;
import fr.inria.insitu.touchstone.run.NumericalFactor;
import fr.inria.insitu.touchstone.run.exp.model.Experiment;

public class PartialXMLParse extends DefaultHandler {

	LinkedList<Object[]> eventsToProcess = new LinkedList<Object[]>();
	Experiment experiment;
	Locator currentLocator;
	File script;
	boolean inParticipant = false;

	private int currentBlock = 1;
	private int currentTrial = 1;
	private int startBlock = 0;
	private int startTrial = 0;
	private String participant;

	/**
	 * Builds a SimpleXMLParse.
	 * @param exp The experiment environment in which the script must be executed.
	 * @param script The name of the file containing the script
	 */
	public PartialXMLParse(Experiment exp, File script, String participant, int startBlock, int startTrial) {
		this.experiment = exp;
		this.script = script;
		this.startBlock = startBlock;
		this.startTrial = startTrial;
		this.participant = participant;
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

	private Object[] lastInterblock = null;
	private Object[] lastIntertrial = null;
	private boolean start = true;
	
	public void startElement (String uri, String name, String qName, Attributes atts)
	{
		if(name.compareTo("run") == 0) {
			inParticipant = atts.getValue("id").compareTo(participant) == 0;
			if(!inParticipant) return;
		}

		if(name.compareTo("block") == 0 || name.compareTo("practice") == 0) {
			if((currentBlock >= startBlock) && inParticipant) {
				if(start) {
					start = false;
					if(lastInterblock != null) eventsToProcess.add(lastInterblock);
					if(lastIntertrial != null) eventsToProcess.add(lastIntertrial);
				}
				registerStartElement(name, atts);
			}
			return;
		} else {
			if(name.compareTo("trial") == 0) {
				if(((currentBlock > startBlock) 
						|| (currentBlock == startBlock  && currentTrial >= startTrial)) && inParticipant) {
					registerStartElement(name, atts);
				}
				return;
			} else {
				if(name.compareTo("interblock") == 0) {
					if(start) {
						lastInterblock = getElement(name, atts);
						return;
					}
				} else {
					if(name.compareTo("intertrial") == 0) {
						if(start) {
							lastIntertrial = getElement(name, atts);
							return;
						}
					} else {
						if(name.compareTo("setup") == 0) {
							if(!inParticipant) return;
						} else {
							if(name.compareTo("factor") == 0) {
								FactorInitializer.getInstance();
								FactoriesForValues.getInstance();
								// TODO
//								String type = atts.getValue("type");
//								String id = atts.getValue("id");
//								if(type.equalsIgnoreCase("string")) {
//									new NominalFactor(id);
//								} else if(type.equalsIgnoreCase("integer")) {
//									new NumericalFactor(id);
//								} else if(type.equalsIgnoreCase("float")) {
//									new NumericalFactor(id);
//								} else {
//									System.err.println("unexpected type: "+type);
//								}
							}
						}
					}
				}
			}
		}
		registerStartElement(name, atts);
	}

	private Object[] getElement(String name, Attributes atts) {
		Object[] element = new Object[3];
		element[0] = name;
		Hashtable<String, String> hashAtts = new Hashtable<String, String>();
		element[1] = hashAtts;
		element[2] = getLocator();
		int nb = atts.getLength();
		for(int i = 0; i < nb; i++) {
			hashAtts.put(atts.getQName(i), atts.getValue(i));
		}
		return element;
	}
	
	private void registerStartElement(String name, Attributes atts) {
		Object[] element = new Object[3];
		element[0] = name;
		Hashtable<String, String> hashAtts = new Hashtable<String, String>();
		element[1] = hashAtts;
		element[2] = getLocator();
		int nb = atts.getLength();
		for(int i = 0; i < nb; i++) {
			hashAtts.put(atts.getQName(i), atts.getValue(i));
		}
		eventsToProcess.add(element);
	}
	
	private void registerEndElement(String name) {
		Object[] element = new Object[2];        
		element[0] = name;
		element[1] = getLocator();
		eventsToProcess.add(element);
	}

	/**
	 * {@inheritDoc}
	 */
	public void endElement (String uri, String name, String qName)
	{
		if(name.compareTo("interblock") == 0) {
			lastInterblock = null;
			if(((currentBlock > startBlock) 
					|| (currentBlock == startBlock  && currentTrial >= startTrial)) && inParticipant) {
				registerEndElement(name);
			}
			return;
		} else {
			if(name.compareTo("intertrial") == 0) {
				lastIntertrial = null;
				if(((currentBlock > startBlock) 
						|| (currentBlock == startBlock  && currentTrial >= startTrial)) && inParticipant) {
					registerEndElement(name);
				}
				return;
			} else {
				if(name.compareTo("block") == 0 || name.compareTo("practice") == 0) {
					if(currentBlock >= startBlock && inParticipant) {
						registerEndElement(name);
					}
					currentTrial = 1;
					currentBlock ++;
					return;
				} else {
					if(name.compareTo("trial") == 0) {
						if(((currentBlock > startBlock) 
								|| (currentBlock == startBlock  && currentTrial >= startTrial)) && inParticipant) {
							registerEndElement(name);
						}
						currentTrial ++;
						return;
					} else {
						if(name.compareTo("run") == 0) {
							if(inParticipant) {
								registerEndElement(name);
							}
							inParticipant = false;
							currentBlock = 1;
							return;
						} else {
							if(name.compareTo("setup") == 0) {
								if(!inParticipant) {
									return;
								}
							}
							// any other XML tags: factor, measure, form, etc.
							registerEndElement(name);
						}
					}
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void endDocument() {
//		System.out.println("***");
//		for(Iterator<Object[]> iterator = eventsToProcess.iterator(); iterator.hasNext(); ) {
//			Object[] next = iterator.next();
//			System.out.println(next[0] + " - " + next.length);
//		}
//		System.out.println("***");
		experiment.setSystemEvents(eventsToProcess);
	}

}
