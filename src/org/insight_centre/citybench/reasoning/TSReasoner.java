package org.insight_centre.citybench.reasoning;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observer;

import org.insight_centre.aceis.io.rdf.RDFFileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sr.core.atom_based_reasoner.WindowInfo;
import sr.core.triple_based_reasoner.TripleClingoReasoner;
import sr.core.triple_based_reasoner.TripleStream;


/**
 * 
 * @author thulepham
 *
 */

public class TSReasoner {
	
	static Logger logger = LoggerFactory.getLogger(TSReasoner.class);
	
	/*
	 * 
	 */
	TripleClingoReasoner reasoner;
	

	
	/**
	 * 
	 */
	public TSReasoner() {
		super();
		this.reasoner = new TripleClingoReasoner(false);
	}

	public TSReasoner(boolean parallel) {
		super();
		this.reasoner = new TripleClingoReasoner(parallel);
	}


	/**
	 * Register a set of rules from a file
	 * @param ruleFile
	 */
	public void registerRuleSet(String ruleFile){

		this.reasoner.registerRuleSet(ruleFile);
	}
	
	
	
	
	/**
	 * Register input stream
	 * @param stream: a TripleStream
	 */
	public void registerInputStream(TripleStream stream){
		logger.info(reasoner.getRuleSet().getStreamWindowMap().toString());
		logger.info(stream.getId());
		WindowInfo winInfo = reasoner.getRuleSet().getStreamWindowMap().get(stream.getId());
		if(winInfo == null){
			logger.warn("Can not find window information of stream " +  stream.getId() + "! Window will be created as default window!");
			winInfo = new WindowInfo();
		}
		this.reasoner.registerStream(stream, winInfo);
	}
	
	
	public void registerOutputStream( TripleOutputStream stream){
		this.reasoner.addObserver(stream);
	}


	public TripleClingoReasoner getReasoner() {
		return reasoner;
	}



	public void setReasoner(TripleClingoReasoner reasoner) {
		this.reasoner = reasoner;
	}
	
	
}
