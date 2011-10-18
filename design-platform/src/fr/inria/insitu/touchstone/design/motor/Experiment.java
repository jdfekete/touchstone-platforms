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
package fr.inria.insitu.touchstone.design.motor;
import fr.inria.insitu.touchstone.design.XMLParser.PluginHandler;
import fr.inria.insitu.touchstone.design.graphic.DesignPlatform;
import fr.inria.insitu.touchstone.design.graphic.PanelTiming;
import fr.inria.insitu.touchstone.design.graphic.widgets.AddYourOwn;
import fr.inria.insitu.touchstone.design.graphic.widgets.Function;

import java.io.File;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;

import org.w3c.dom.Element;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;


import com.sun.org.apache.xerces.internal.dom.DocumentImpl;
public class Experiment extends Step implements Serializable {

	private static final long serialVersionUID = 42L;

	private String description;
	private Vector<Plugin> plugins;
	private String author;
	private String title;
	private String shortCode;
	private Intertitle blockClass;

	public FactorSet  factorSet;
	public Blocking   blocking;
	public Timing     timing;
	public Practice   practice;
	public Ordering   ordering;
	public MeasureSet measureSet; 

	private transient DesignPlatform designPlatform;
	private boolean orderingToUpdate = true;

	private Plugin corePlugin;

	private String id;

	public Experiment(DesignPlatform designPlatform, String shortCode, String description, Vector<Plugin> plugins, String author, String title, Intertitle blockClass) {
		super();
		this.id = "Design 1";
		this.description = description;
		this.plugins = plugins;
		this.author = author;
		this.title = title;
		this.shortCode = shortCode;
		this.blockClass = blockClass;
		this.designPlatform = designPlatform;

		File f = new File("core.xml");

		if (f.exists())	
			try {
				XMLReader saxReader = XMLReaderFactory.createXMLReader();
				PluginHandler ph = new PluginHandler(this);
				saxReader.setContentHandler(ph);
				saxReader.parse(f.toURI().toURL().toString());
				corePlugin = ph.getPlugin();
				corePlugin.setJarFile(new File("lib"+File.separator+"touchstone.jar"));
				if(this.plugins == null) this.plugins = new Vector<Plugin>();
				this.plugins.add(corePlugin);
				ph.getPlugin().setXMLFile(f);
			} catch (Exception ex) {
				ex.printStackTrace();
			}	
	}

	public Experiment(DesignPlatform designPlatform){
		this(designPlatform, "", "", new Vector<Plugin>(), "", "", null);
	}

	public Experiment snapshot() throws CloneNotSupportedException {
		Experiment exp = new Experiment(designPlatform, shortCode, description, plugins, author, title, blockClass);
		exp.setFactorSet((FactorSet)getFactorSet().clone());
		exp.setBlocking((Blocking)getBlocking().clone());
		exp.setPractice((Practice)getPractice().clone());
		exp.setTiming((Timing)getTiming().clone());
		exp.setOrdering((Ordering)getOrdering().clone());
		exp.setMeasureSet((MeasureSet)getMeasureSet().clone());
		return exp;
	}

	public void init() {
		Vector<Factor> factors = new Vector<Factor>();
		factors.add(new Factor());
		factors.add(new Factor());
		factors.get(0).setShortName("F1");
		factors.get(1).setShortName("F2");
		for (Iterator<Factor> iterator = factors.iterator(); iterator.hasNext();) {
			Factor next = iterator.next();
			next.setTag("Within Subject");
			next.setRole(fr.inria.insitu.touchstone.design.motor.FactorRole.key);
			next.addValue("1", "");
			next.addValue("2", "");
		}
		setFactorSet(new FactorSet(factors));
		setMeasureSet(new MeasureSet());
		updateOrdering();
	}

	public Intertitle getBlockClass() {
		return blockClass;
	}

	public void setBlockClass(Intertitle blockClass) {
		this.blockClass = blockClass;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}	

	public String getShortCode() {
		return shortCode;
	}

	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}

	public Vector<Plugin> getPlugins() {
		return plugins;
	}

	public void setPlugins(Vector<Plugin> plugins) {
		this.plugins = plugins;
		boolean coreToAdd = true;
		for (Iterator<Plugin> iterator = plugins.iterator(); iterator.hasNext();) {
			Plugin plugin = iterator.next();
			if(plugin.getId().compareTo("Core") == 0) coreToAdd = false;
		}
		if(coreToAdd) this.plugins.add(corePlugin);
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return all the PredefinedIntertitles contained by 
	 * the plugins associated to this Step as a vector of {@link Function}.
	 * Only one instance of each Block.
	 */
	public Vector<Function> getPredefinedBlockClasses(){
		Vector<Function> result = new Vector<Function>();
		for (Plugin plugin : plugins) {
			for (Function function : plugin.getPredefinedBlockClass()) {
				if (!result.contains(function))
					result.add(function);
			}
		}
		return result;
	}

	/**
	 * @return all the PredefinedIntertitles contained by 
	 * the plugins associated to this Step as a vector of {@link Function}.
	 * Only one instance of each Intertitle.
	 */
	public Vector<Function> getPredefinedIntertitles(){
		Vector<Function> result = new Vector<Function>();
		for (Plugin plugin : plugins)
			for (Function function : plugin.getPredefinedIntertitles())
				if (!result.contains(function))
					result.add(function);		
		return result;
	}

	/**
	 * @return all the PredefinedCriteria contained by 
	 * the plugins associated to this Step as a vector of {@link Function}.
	 * Only one instance of each criterion.
	 */
	public Vector<Function> getPredefinedCriteria(){
		Vector<Function> result = new Vector<Function>();
		for (Plugin plugin : plugins)
			for (Function function : plugin.getPredefinedCriteria())
				if (!result.contains(function))
					result.add(function);		
		return result;
	}

	/**
	 * @return all the PredefinedMeasures contained by 
	 * the plugins associated to this Step as a vector of {@link Measure}.
	 * Only one instance of each measure.
	 */
	public Vector<Measure> getPredefinedMeasures(){
		Vector<Measure> result = new Vector<Measure>();
		for (Plugin plugin : plugins) {
			for (Measure measure : plugin.getPredefinedMeasures()) {
				if (!result.contains(measure))
					result.add(measure);
			}
		}
		return result;
	}

	/**
	 * @return all the PredefinedFactors contained by 
	 * the plugins associated to this Step as a vector of {@link Factor}.
	 * Only one instance of each factor.
	 */
	public Vector<Factor> getPredefinedFactors(){
		Vector<Factor> result = new Vector<Factor>();
		for (Plugin plugin : plugins)
			for (Factor factor : plugin.getPredefinedFactors()) {

				boolean added = false;
				for (Iterator<Factor> iterator = result.iterator(); iterator.hasNext();) {
					Factor next = iterator.next();
					if(next.getShortName().compareTo(factor.getShortName()) == 0) {
						Vector<Value> valuesToAdd = factor.getValues();
						for (Iterator<Value> iterator2 = valuesToAdd.iterator(); iterator2.hasNext();) {
							Value nextValue = iterator2.next();
							next.addValue(nextValue);
						}
						added = true;
					}
				}
				if(!added)
					result.add(factor);	
			}
		return result;
	}

	public FactorSet getFactorSet() {
		return factorSet;
	}

	public void setFactorSet(FactorSet factorSet) {
		
		Block oldBlock = null;
		BlockType oldBlockType = null;

		if(getBlocking() != null) {
			oldBlock = getBlocking().getSelectedBlockStructure();
			oldBlockType = getBlocking().getSelectedBlockType();
		}

		Vector<Factor> newSubjFactors = factorSet.getBetweenSubjectFactors();

		Vector<Factor> addedFactors = new Vector<Factor>();

		Vector<Factor> newWithinSubjectFactors = factorSet.getWithinSubjectFactors();
		Vector<Factor> oldFreeFactors = 
			oldBlockType != null ? oldBlockType.getFreeFactors() : new Vector<Factor>();
			Vector<Factor> newFreeFactors = new Vector<Factor>(); 
			for (Iterator<Factor> iterator = oldFreeFactors.iterator(); iterator.hasNext();) {
				Factor factor = iterator.next();
				if(newWithinSubjectFactors.contains(factor)) {
					newFreeFactors.add(factor);
					addedFactors.add(factor);
				}
			}
			Vector<Vector<Factor>> oldBlockedFactors = 
				oldBlockType != null ? oldBlockType.getBlockedFactors() : new Vector<Vector<Factor>>();
				Vector<Vector<Factor>> newBlockedFactors_tmp = new Vector<Vector<Factor>>();
				for (Iterator<Vector<Factor>> iterator = oldBlockedFactors.iterator(); iterator.hasNext();) {
					Vector<Factor> vector = iterator.next();
					newBlockedFactors_tmp.add(new Vector<Factor>());
					for (Iterator<Factor> iterator2 = vector.iterator(); iterator2.hasNext();) {
						Factor factor = iterator2.next();
						if(!newFreeFactors.contains(factor) && newWithinSubjectFactors.contains(factor)) {
							newBlockedFactors_tmp.lastElement().add(factor);
							addedFactors.add(factor);
						}
					}
				}
				Vector<Vector<Factor>> newBlockedFactors = new Vector<Vector<Factor>>();
				for (Iterator<Vector<Factor>> iterator = newBlockedFactors_tmp.iterator(); iterator.hasNext();) {
					Vector<Factor> factors = iterator.next();
					if(factors.size() > 0)
						newBlockedFactors.add(factors);	
				}

				for (Iterator<Factor> iterator = newWithinSubjectFactors.iterator(); iterator.hasNext();) {
					Factor f = iterator.next();
					if(!addedFactors.contains(f))
						newFreeFactors.add(f);
				}

				if(newFreeFactors.size() == 0) {
					Factor factice = new Factor();
					factice.addValue(new Value("", "", factice));
					newFreeFactors.add(factice);
				}

				BlockType selectedBlockType = new BlockType(newSubjFactors, newFreeFactors, newBlockedFactors);
				Block selectedBlock = selectedBlockType.generateBlock();
				setBlocking(new Blocking(selectedBlock, selectedBlockType));
				this.factorSet = factorSet;

				// restore what makes sense from last Blocking
				if(oldBlock != null) {
					// 0 participants
					// for (int i = 1; i<= (selectedBlockType.getNumberOfBlockLevel()+1);i++) blocks
					// selectedBlockType.getNumberOfBlockLevel() + 2 trials
					selectedBlock.setBlockReplication(0,oldBlock.getBlockReplications(0));
					for (int i = 1; i<= (selectedBlockType.getNumberOfBlockLevel()+1);i++) {
						if(i <= (oldBlockType.getNumberOfBlockLevel()+1)) {
							selectedBlock.setBlockReplication(i,oldBlock.getBlockReplications(i));
						} 
					}
					selectedBlock.setBlockReplication(selectedBlockType.getNumberOfBlockLevel() + 2,
							oldBlock.getBlockReplications(selectedBlockType.getNumberOfBlockLevel() + 2));
				}

				setBlocking(new Blocking(selectedBlock, selectedBlockType));
	}

	public Blocking getBlocking() {
		return blocking;
	}

	public void setBlocking(Blocking blocking) {
		this.blocking = blocking;
		orderingToUpdate = true;
		updateTiming();
		updatePractice();
	}

	public void updateOrdering() {
		if(!orderingToUpdate) return;

		Block oldOrderedBlock = getOrdering() != null ? getOrdering().getOrderedBlock() : null;

		Block blockToOrder = getBlocking().getSelectedBlockStructure();
		Block orderedBlock = (Block)blockToOrder.clone();
		orderedBlock.replicateBlockAtDepth(0);
		try {
			for (int i = 0; i < blockToOrder.getDepth(); i++){
				OrderingMode mode = oldOrderedBlock == null || oldOrderedBlock.getBlocksAtDepth(i).size() == 0  
				? new Random() 
				: oldOrderedBlock.getBlocksAtDepth(i).get(0).getOrderingMode();
				boolean serial = oldOrderedBlock == null || oldOrderedBlock.getBlocksAtDepth(i).size() == 0
				? false
						: oldOrderedBlock.getBlocksAtDepth(i).get(0).isSerial();
				if (serial){

					Vector<Block> toOrder = orderedBlock.getBlocksAtDepth(i);
					mode.order(toOrder, serial);
					for (Block block : orderedBlock.getBlocksAtDepth(i+1))
						block.replicate();
				}					
				else {
					for (Block block : orderedBlock.getBlocksAtDepth(i+1))
						block.replicate();
					mode.order(orderedBlock.getBlocksAtDepth(i), serial);
				}

			}
			setOrdering(new Ordering(orderedBlock));
			orderingToUpdate = false;
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	private Intertitle getIntertitle(Function f) {
		return new Intertitle(f);
	}

	private void updateTiming() {
		Vector<String> criteriaString = new Vector<String>();
		Vector<Intertitle> intertitlesObjects = new Vector<Intertitle>();
		Vector<Integer> estimatedTimeValues = new Vector<Integer>();

		Vector<String> practiceCriteriaString = new Vector<String>();
		Vector<Intertitle> practiceIntertitlesObjects = new Vector<Intertitle>();
		Vector<Integer> practiceEstimatedTimeValues = new Vector<Integer>();

		Block selectedBlock = getBlocking().getSelectedBlockStructure();
		Function addYourOwn = new AddYourOwn();

		if(timing == null) {
			// 1 - Experiment setup / 2 - Intro Practice / 3 - Pratice / 4 - Standard Trial / 5 - Inter-Trial Interval
			for(int i = 0 ; i < Timing.INDEX_BEGIN_BLOCKS; i++) {
				criteriaString.add("");
				intertitlesObjects.add(getIntertitle(addYourOwn));
				estimatedTimeValues.add(PanelTiming.DEFAULT_ESTIMATED_TIME);
			}
			// blocks
			for (int i = 0 ; i < (selectedBlock.getDepth() - 2); i++){
				criteriaString.add("");
				intertitlesObjects.add(getIntertitle(addYourOwn));
				estimatedTimeValues.add(PanelTiming.DEFAULT_ESTIMATED_TIME);
			}
			for(int i = 0; i <= (selectedBlock.getDepth() - 2); i++){
				practiceCriteriaString.add("");
				practiceIntertitlesObjects.add(getIntertitle(addYourOwn));
				practiceEstimatedTimeValues.add(PanelTiming.DEFAULT_ESTIMATED_TIME);				
			}
		} else {
			for(int i = 0; i < Timing.INDEX_BEGIN_BLOCKS; i++) {
				criteriaString.add(timing.getCriteria().get(i));
				intertitlesObjects.add(timing.getIntertitles().get(i));
				estimatedTimeValues.add(timing.getEstimatedTime().get(i));
			}
			// blocks
			for (int i = 0 ; i < (selectedBlock.getDepth() - 2); i++) {
				int index = i+Timing.INDEX_BEGIN_BLOCKS;
				if(index < timing.getCriteria().size()) {
					criteriaString.add(timing.getCriteria().get(index));
					intertitlesObjects.add(timing.getIntertitles().get(index));
					estimatedTimeValues.add(timing.getEstimatedTime().get(index));
				} else {
					criteriaString.add("");
					intertitlesObjects.add(getIntertitle(addYourOwn));
					estimatedTimeValues.add(PanelTiming.DEFAULT_ESTIMATED_TIME);
				}
			}

			// practice interblocks
			for (int i = 0 ; i <= (selectedBlock.getDepth() - 2); i++) {
				if(i < timing.getPracticeCriteria().size()) {
					practiceCriteriaString.add(timing.getPracticeCriteria().get(i));
					practiceIntertitlesObjects.add(timing.getPracticeIntertitles().get(i));
					practiceEstimatedTimeValues.add(timing.getPracticeEstimatedTime().get(i));
				} else {
					criteriaString.add("");
					intertitlesObjects.add(getIntertitle(addYourOwn));
					estimatedTimeValues.add(PanelTiming.DEFAULT_ESTIMATED_TIME);
				}
			}
		}
		setTiming(new Timing(criteriaString,intertitlesObjects,estimatedTimeValues, practiceCriteriaString, practiceIntertitlesObjects, practiceEstimatedTimeValues));
	}

	private void updatePractice() {
		Practice newPractice = new Practice(getBlocking());
		if(practice != null) {
			Vector<Vector<PracticeBlock>> oldPractices = practice.getPractices();
			Vector<Vector<PracticeBlock>> newPractices = newPractice.getPractices();
			Vector<Boolean> oldPracticesEnabled = practice.getPracticesEnabled();
			Vector<Boolean> newPracticesEnabled = newPractice.getPracticesEnabled();
			Vector<Boolean> oldRepetitionsEnabled = practice.getReplicationsEnabled();
			Vector<Boolean> newRepetitionsEnabled = newPractice.getReplicationsEnabled();
			Vector<Intertitle> oldBlockClasses = practice.getBlockClasses();
			Vector<Intertitle> newBlockClasses = newPractice.getBlockClasses();
			for (int i = 0; i < newPractices.size(); i++) {
				if(i < oldPractices.size()) {
					newPractices.set(i, oldPractices.get(i));
					newPracticesEnabled.set(i, oldPracticesEnabled.get(i));
					newRepetitionsEnabled.set(i, oldRepetitionsEnabled.get(i));
					newBlockClasses.set(i, oldBlockClasses.get(i));
				}
			}
		}
		setPractice(newPractice);
	}

	public Timing getTiming() {
		return timing;
	}

	public void setTiming(Timing timing) {
		this.timing = timing;
	}

	public Practice getPractice() {
		return practice;
	}

	public void setPractice(Practice practice) {
		this.practice = practice;
	}

	public Ordering getOrdering() {
		return ordering;
	}

	public void setOrdering(Ordering ordering) {
		if(this.ordering != null && ordering.equals(this.ordering)) return;
		this.ordering = ordering;
	}

	public MeasureSet getMeasureSet() {
		return measureSet;
	}

	public void setMeasureSet(MeasureSet measureSet) {
		if(this.measureSet != null && measureSet.equals(this.measureSet)) return;
		this.measureSet = measureSet;
	}

	public String toString() {
		return id;
	}
	
	public String oldToString() {
		return super.toString();
	}

	public void setID(String id) {
		this.id = id;
	}

	public DocumentImpl toXML(){
		DocumentImpl xmlDoc = new DocumentImpl();
		Element root = xmlDoc.createElement("experiment");
		root.setAttributeNS(null, "id", shortCode);
		root.setAttributeNS(null, "name", title);
		root.setAttributeNS(null, "author", author);
		root.setAttributeNS(null, "description", description);
		xmlDoc.appendChild(root);
		factorSet.toXML(xmlDoc, root);
		measureSet.toXML(xmlDoc, root);

		for (int subjectID = 0; subjectID < ordering.getOrderedBlock().size() ; subjectID++){
			Element run = xmlDoc.createElementNS(null, "run");
			run.setAttributeNS(null, "id", "S"+subjectID);
			root.appendChild(run);

			Element parent = run;

			boolean add = false;
			Element setup = xmlDoc.createElementNS(null, "setup");
			if(timing.getSetupClass().toString().length() != 0) {
				setup.setAttributeNS(null, "class", ""+timing.getSetupClass());
				add = true;
			} else {
				// we always generate a default setup in order to put the code for defining measures
				setup.setAttributeNS(null, "class", "SetUp"+getShortCode());
				add = true;
			}
			if(timing.getSetupCriterion().length() != 0) {
				setup.setAttributeNS(null, "criterion", timing.getSetupCriterion());
				add = true;
			}
			if(add) run.appendChild(setup);

			add = false;
			Element intertrial = xmlDoc.createElementNS(null, "intertrial");
			if(timing.getIntertrialClass().toString().length() != 0) {
				intertrial.setAttributeNS(null, "class", ""+timing.getIntertrialClass());
				add = true;
			}
			if(timing.getIntertrialCriterion().length() != 0) {
				intertrial.setAttributeNS(null, "criterion", timing.getIntertrialCriterion());
				add = true;
			}
			if(add) {
				run.appendChild(intertrial);
				parent = intertrial;
			}

			// begin experiment practice
			if(practice != null && practice.getPracticesEnabled().get(0)) {
				Vector<PracticeBlock> practiceBlocks = practice.getPracticeBlocksFor(ordering.getOrderedBlock().get(subjectID), factorSet.getFactors());
				for(int i = 0; i < practiceBlocks.size(); i++) {

					add = false;
					Element interpractice = xmlDoc.createElementNS(null, "interblock");
					Element subParent = parent;
					if(timing.getPracticeIntertitles().get(0).toString().length() != 0) {
						interpractice.setAttributeNS(null, "class", ""+timing.getPracticeIntertitles().get(0));
						add = true;
					}
					if(timing.getPracticeCriteria().get(0).length() != 0) {
						interpractice.setAttributeNS(null, "criterion", timing.getPracticeCriteria().get(0));
						add = true;
					}
					if(add) {
						parent.appendChild(interpractice);
						subParent = interpractice;
					}

					Element practice = xmlDoc.createElementNS(null, "practice");
//					if(blockClass.toString().length() != 0)
//						practice.setAttributeNS(null, "class", ""+blockClass);
//					else
//						practice.setAttributeNS(null, "class", "DefaultBlockClass");
					if(this.practice.getBlockClassAtBlockLevel(0) != null &&
							this.practice.getBlockClassAtBlockLevel(0).toString().length() != 0)
						practice.setAttributeNS(null, "class", ""+this.practice.getBlockClassAtBlockLevel(0));
					else
						practice.setAttributeNS(null, "class", "DefaultBlockClass");
					
					String valuesAtt = "";
					for (Value value : practiceBlocks.get(i).getValues())
						if(value.getFactor().getShortName().length() > 0) valuesAtt+=value.getFactor().getShortName()+"="+value.getShortValue()+",";
					if(valuesAtt.length() > 0) valuesAtt = valuesAtt.substring(0, valuesAtt.length()-1);
					if(valuesAtt.length() > 0) practice.setAttributeNS(null, "values", valuesAtt);
					if(timing.getCriterionTrial().length() > 0) practice.setAttributeNS(null, "criterionTrial", timing.getCriterionTrial());
					subParent.appendChild(practice);
					for(int j = 0; j < practiceBlocks.get(i).size(); j++) {
						Element trial = xmlDoc.createElementNS(null, "trial");
						valuesAtt = "";
						for (Value value : practiceBlocks.get(i).get(j).getValues())
							if(value.getFactor().getShortName().length() > 0) valuesAtt+=value.getFactor().getShortName()+"="+value.getShortValue()+",";
						if(valuesAtt.length() > 0) valuesAtt = valuesAtt.substring(0, valuesAtt.length()-1);
						if(valuesAtt.length() > 0) trial.setAttributeNS(null, "values", valuesAtt);
						practice.appendChild(trial);
					}
				}
			}
			// end experiment practice

			Block block = ordering.getOrderedBlock().get(subjectID);
			blockToXml(block, subjectID, "", xmlDoc, parent);
		}
		return xmlDoc;
	}

	// returns an array of length 2
	// the first element is the number of the block
	// the second element is the total number of blocks
	private void blockCounter(Block experimentPart, Block block, int[] result) {
		if(experimentPart.getDepth() == 1) {
			result[1]++;
			if(experimentPart == block)
				result[0] = result[1];
			return;
		}
		for(int i = 0; i < experimentPart.size(); i++) 
			blockCounter(experimentPart.get(i), block, result);
	}

	private void blockToXml(Block block, int subjectID, String values, DocumentImpl xmlDoc, Element parent) {
		int index;

		int totalDepth = timing.getIntertitles().size() - Timing.INDEX_BEGIN_BLOCKS;
		if (block.size()==0) { // it's a trial
			Element trial = xmlDoc.createElementNS(null, "trial");
			String valuesAtt = "";
			for (Value value : block.getValues())
				if(value.getFactor().getShortName().length() > 0) valuesAtt+=value.getFactor().getShortName()+"="+value.getShortValue()+",";
			if(valuesAtt.length() > 0) valuesAtt = valuesAtt.substring(0, valuesAtt.length()-1);
			if(valuesAtt.length() > 0) trial.setAttributeNS(null, "values", valuesAtt);

			//
			trial.setAttributeNS(null, "number", ""+block.getParent().indexOf(block));
			trial.setAttributeNS(null, "total", ""+block.getParent().size());
			//

			parent.appendChild(trial);
		} else {

			// begin practice at block level
			index = practice.getPractices().size() - block.getDepth();
			// index = 0: experiment level, already done above
			if(index != 0 && practice != null && practice.getPracticesEnabled().get(index)
					&& !(block.isReplication() && !practice.getReplicationsEnabled().get(index))) {
				Vector<PracticeBlock> practiceBlocks = practice.getPracticeBlocksFor(block, factorSet.getFactors());
				
				for(int i = 0; i < practiceBlocks.size(); i++) {

					boolean add = false;
					Element subParent = parent;
					Element interpractice = xmlDoc.createElementNS(null, "interblock");
					if(timing.getPracticeIntertitles().get(index).toString().length() != 0) {
						interpractice.setAttributeNS(null, "class", ""+timing.getPracticeIntertitles().get(index));
						add = true;
					}
					if(timing.getPracticeCriteria().get(index).length() != 0) {
						interpractice.setAttributeNS(null, "criterion", timing.getPracticeCriteria().get(index));
						add = true;
					}
					if(add) {
						parent.appendChild(interpractice);
						subParent = interpractice;
					}


					Element practice = xmlDoc.createElementNS(null, "practice");
//					if(blockClass.toString().length() != 0)
//						practice.setAttributeNS(null, "class", ""+blockClass);
//					else
//						practice.setAttributeNS(null, "class", "DefaultBlockClass");
					
//					if(this.practice.getBlockClassAtBlockLevel(i) != null &&
//							this.practice.getBlockClassAtBlockLevel(i).toString().length() != 0)
//						practice.setAttributeNS(null, "class", ""+this.practice.getBlockClassAtBlockLevel(i));
//					else
//						practice.setAttributeNS(null, "class", "DefaultBlockClass");
					
					if(this.practice.getBlockClassAtBlockLevel(index) != null &&
							this.practice.getBlockClassAtBlockLevel(index).toString().length() != 0)
						practice.setAttributeNS(null, "class", ""+this.practice.getBlockClassAtBlockLevel(index));
					else
						practice.setAttributeNS(null, "class", "DefaultBlockClass");
					
					String valuesAtt = "";
					for (Value value : practiceBlocks.get(i).getValues())
						if(value.getFactor().getShortName().length() > 0) valuesAtt+=value.getFactor().getShortName()+"="+value.getShortValue()+",";
					if(valuesAtt.length() > 0) valuesAtt = valuesAtt.substring(0, valuesAtt.length()-1);
					if(valuesAtt.length() > 0) practice.setAttributeNS(null, "values", valuesAtt);
					if(timing.getCriterionTrial().length() > 0) practice.setAttributeNS(null, "criterionTrial", timing.getCriterionTrial());
					subParent.appendChild(practice);

					for(int j = 0; j < practiceBlocks.get(i).size(); j++) {
						Element trial = xmlDoc.createElementNS(null, "trial");
						valuesAtt = "";
						for (Value value : practiceBlocks.get(i).get(j).getValues())
							if(value.getFactor().getShortName().length() > 0) valuesAtt+=value.getFactor().getShortName()+"="+value.getShortValue()+",";
						if(valuesAtt.length() > 0) valuesAtt = valuesAtt.substring(0, valuesAtt.length()-1);
						if(valuesAtt.length() > 0) trial.setAttributeNS(null, "values", valuesAtt);
						practice.appendChild(trial);
					}
				}
			}
			// end practice at block level


			String valuesString = values;
			for (Value value : block.getValues())
				valuesString += value.getFactor().getShortName()+"="+value.getShortValue()+",";
			Element interblock = null;
			if(block.getDepth() == 1) {
				Block b = block;
				while(b.getParent() != null && b.getParent().getDepth() <= totalDepth && b.getParent().indexOf(b) == 0)
					b = b.getParent();
				
				index = timing.getIntertitles().size() - b.getDepth();
				
				// The test below is necessary in case there is no block but only a series of free trials
				if(index >= Timing.INDEX_BEGIN_BLOCKS) {
					
					boolean add = false;
					interblock = xmlDoc.createElementNS(null, "interblock");
					if(timing.getIntertitles().get(index).toString().length() != 0) {
						interblock.setAttributeNS(null, "class", ""+timing.getIntertitles().get(index));
						add = true;
					}
					if(timing.getCriteria().get(index).length() != 0) {
						interblock.setAttributeNS(null, "criterion", timing.getCriteria().get(index));
						add = true;
					}
					if(add)
						parent.appendChild(interblock);
					else
						interblock = null;
				
				
				}
				
			}
			Element parentBlock = interblock != null ? interblock : parent;
			Element blockElement = null;
			if(block.getDepth() == 1) {
				if(valuesString.length() > 0)
					valuesString = valuesString.substring(0, valuesString.length()-1);
				blockElement = xmlDoc.createElementNS(null, "block");
				if(blockClass.toString().length() != 0) {
					blockElement.setAttributeNS(null, "class", ""+blockClass);
				} else
					blockElement.setAttributeNS(null, "class", "DefaultBlockClass");
				if(valuesString.length() != 0) blockElement.setAttributeNS(null, "values", valuesString);
				if(timing.getCriterionTrial().length() != 0) blockElement.setAttributeNS(null, "criterionTrial", timing.getCriterionTrial());

				//
				int[] countBlocks = new int[2];
				countBlocks[0] = 0; countBlocks[1] = 0;
				Block subjectBlock = ordering.getOrderedBlock().get(subjectID);
				blockCounter(subjectBlock, block, countBlocks);
				blockElement.setAttributeNS(null, "number", ""+countBlocks[0]);
				blockElement.setAttributeNS(null, "total", ""+countBlocks[1]);
				//

				parentBlock.appendChild(blockElement);
			}

			Element p = blockElement != null ? blockElement : parentBlock;
			for(int i = 0; i < block.size(); i++) 
				blockToXml(block.get(i), subjectID, valuesString, xmlDoc, p);
		}
	}

	public void toCSV(PrintWriter pw){
		String[] factorNames = new String[factorSet.getFactors().size()];
		for (int i = 0; i < factorNames.length; i++) {
			factorNames[i] = factorSet.getFactors().get(i).getShortName();
		}
		String line = "Participant,Practice,Block,Trial";
		for (int i = 0; i < factorNames.length; i++) {
			line+=(","+factorNames[i]);
		}
		pw.write(line+"\n");
		for (int subjectID = 0; subjectID < ordering.getOrderedBlock().size() ; subjectID++){
			Block block = ordering.getOrderedBlock().get(subjectID);
			blockToCSV(block, subjectID, factorNames, pw);
		}
	}

	private void blockToCSV(Block block, int subjectID, String[] factorNames, PrintWriter pw) {
		int index;
		if (block.size()==0) { // it's a trial
			int trialNumber = block.getParent().indexOf(block);
			int blockNumber;
			int[] countBlocks = new int[2];
			countBlocks[0] = 0; countBlocks[1] = 0;
			Block subjectBlock = ordering.getOrderedBlock().get(subjectID);
			blockCounter(subjectBlock, block.getParent(), countBlocks);
			blockNumber = countBlocks[0];

			String valuesAtt = "";
			String[] factorValues = new String[factorNames.length];
			Block b = block;
			while(b.getParent().getParent() != null) {
				for (Value value : b.getValues())
					if(value.getFactor().getShortName().length() > 0) {
						for (int i = 0; i < factorNames.length; i++) {
							if(factorNames[i].compareTo(value.getFactor().getShortName()) == 0) {
								factorValues[i] = value.getShortValue();
							}
						}
					}
				b = b.getParent();
			}
			for (int i = 0; i < factorValues.length; i++) {
				valuesAtt+=","+factorValues[i];				
			}
			String line = subjectID+",false,"+blockNumber+","+trialNumber+valuesAtt;
			pw.write(line+"\n");
		} else {
			// begin practice at block level
			index = practice.getPractices().size() - block.getDepth();
			if(practice != null && practice.getPracticesEnabled().get(index)
					&& !(block.isReplication() && !practice.getReplicationsEnabled().get(index))) {
				Vector<PracticeBlock> practiceBlocks = practice.getPracticeBlocksFor(block, factorSet.getFactors());
				for(int i = 0; i < practiceBlocks.size(); i++) {
					for(int j = 0; j < practiceBlocks.get(i).size(); j++) {
						int trialNumber = j;
						int blockNumber = i;
						String valuesAtt = "";
						String[] factorValues = new String[factorNames.length];
						Block b = practiceBlocks.get(i).get(j);
						while(b != null) {
							for (Value value : b.getValues())
								if(value.getFactor().getShortName().length() > 0) {
									for (int k = 0; k < factorNames.length; k++) {
										if(factorNames[k].compareTo(value.getFactor().getShortName()) == 0) {
											factorValues[k] = value.getShortValue();
										}
									}
								}
							b = b.getParent();
						}
						for (int k = 0; k < factorValues.length; k++) {
							valuesAtt+=","+factorValues[k];				
						}
						String line = subjectID+",true,"+blockNumber+","+trialNumber+valuesAtt;
						pw.write(line+"\n");
					}
				}
			}
			// end practice at block level
			for(int i = 0; i < block.size(); i++) 
				blockToCSV(block.get(i), subjectID, factorNames, pw);
		}
	}

	// name / participant / block / trial / other measures
	public void toFacticeLogs(PrintWriter pw){
		
		// TODO write header
		// headerLogComment+="# HIT: success\n";
		
		String line = "experiment\tparticipant\tblock\ttrial\t";
		Vector<Measure> measures = getMeasureSet().getMeasures();
		for (Iterator<Measure> iterator = measures.iterator(); iterator.hasNext();) {
			Measure measure = iterator.next();
			line += measure.getId() + "\t";
		}
		pw.write(line+"\n");
		for (int subjectID = 0; subjectID < ordering.getOrderedBlock().size() ; subjectID++){
			Block block = ordering.getOrderedBlock().get(subjectID);
			blockToFacticeLogs(block, subjectID, pw);
		}
	}
	
	private void blockToFacticeLogs(Block block, int subjectID, PrintWriter pw) {
		int index;
		if (block.size()==0) { // it's a trial
			int trialNumber = block.getParent().indexOf(block);
			int blockNumber;
			int[] countBlocks = new int[2];
			countBlocks[0] = 0; countBlocks[1] = 0;
			Block subjectBlock = ordering.getOrderedBlock().get(subjectID);
			blockCounter(subjectBlock, block.getParent(), countBlocks);
			blockNumber = countBlocks[0];
			String line = getShortCode() + "\t" + subjectID + "\t" + blockNumber + "\t" + trialNumber + "\t";
			Vector<Measure> measures = getMeasureSet().getMeasures();
			for (Iterator<Measure> iterator = measures.iterator(); iterator.hasNext();) {
				Measure measure = iterator.next();
				boolean found = false;
				Block b = block;
				while(b.getParent().getParent() != null) {
					for (Value value : b.getValues())
						if(value.getFactor().getShortName().length() > 0) {
							if(measure.getId().compareTo(value.getFactor().getShortName()) == 0) {
								line += (value.getShortValue()+"\t");
								found = true;
							}
						}
					b = b.getParent();
				}
				
				if(!found) {
					if(measure.getId().compareTo("inPractice") == 0)
						line += "false\t";
					else if(measure.getId().compareTo("nbBlocks") == 0)
						line += countBlocks[1]+"\t";
					else if(measure.getId().compareTo("nbTrials") == 0)
						line += block.getParent().size()+"\t";
					else
						line += measure.getPossibleValue().getRandomValue() + "\t";
				}
			}
			pw.write(line+"\n");
		} else {
			// begin practice at block level
			index = practice.getPractices().size() - block.getDepth();
			if(practice != null && practice.getPracticesEnabled().get(index)
					&& !(block.isReplication() && !practice.getReplicationsEnabled().get(index))) {
				Vector<PracticeBlock> practiceBlocks = practice.getPracticeBlocksFor(block, factorSet.getFactors());
				for(int i = 0; i < practiceBlocks.size(); i++) {
					for(int j = 0; j < practiceBlocks.get(i).size(); j++) {
						int trialNumber = j;
						int blockNumber = i;
						String line = getShortCode() + "\t" + subjectID + "\t" + blockNumber + "\t" + trialNumber + "\t";
						Vector<Measure> measures = getMeasureSet().getMeasures();
						for (Iterator<Measure> iterator = measures.iterator(); iterator.hasNext();) {
							Measure measure = iterator.next();
							boolean found = false;
							Block b = practiceBlocks.get(i).get(j);
							while(b != null) {
								for (Value value : b.getValues())
									if(value.getFactor().getShortName().length() > 0) {
										if(measure.getId().compareTo(value.getFactor().getShortName()) == 0) {
											line += (value.getShortValue()+"\t");
											found = true;
										}
									}
								b = b.getParent();
							}
							
							if(!found) {
								if(measure.getId().compareTo("inPractice") == 0)
									line += "true\t";
								else if(measure.getId().compareTo("nbBlocks") == 0)
									line += "Undefined\t";
								else if(measure.getId().compareTo("nbTrials") == 0)
									line += block.getParent().size()+"\t";
								else {
									line += measure.getPossibleValue().getRandomValue() + "\t";
								}
							}
						}
						pw.write(line+"\n");
					}
				}
			}
			// end practice at block level
			for(int i = 0; i < block.size(); i++) 
				blockToFacticeLogs(block.get(i), subjectID, pw);
		}
	}
	
	public void setDesignPlatform(DesignPlatform designPlatform) {
		this.designPlatform = designPlatform;
	}

}