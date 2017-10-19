package org.insight_centre.citybench.reasoning;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.insight_centre.aceis.eventmodel.EventDeclaration;
import org.insight_centre.aceis.io.rdf.RDFFileManager;
import org.insight_centre.aceis.io.streams.DataWrapper;
import org.insight_centre.aceis.observations.AarhusParkingObservation;
import org.insight_centre.aceis.observations.PollutionObservation;
import org.insight_centre.aceis.observations.SensorObservation;
import org.insight_centre.citybench.main.CityBench;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csvreader.CsvReader;
//import com.google.gson.Gson;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;

import eu.larkc.csparql.cep.api.RdfQuadruple;
import sr.core.triple_based_reasoner.TimeStampedTriple;
import sr.core.triple_based_reasoner.Triple;

public class StaticTSReasonerLubmStream extends TSReasonerSensorStream implements Runnable {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	int rate;
	
	
	String streamFile;

	public static void main(String[] args) {
		try {
			
			StaticTSReasonerLubmStream aps = new StaticTSReasonerLubmStream("testuri", "streams/Universities-1.stream", 100);
			Thread th = new Thread(aps);
			th.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public StaticTSReasonerLubmStream(String uri, String txtFile, int rate) throws IOException {
		super(uri);
		this.rate = rate;
		this.streamFile = txtFile;
	}


	
	
	
	@Override
	public void run() {
		logger.info("Starting sensor stream: " + this.getId());
		int countRate = 0;
		
		BufferedReader br = null;
		FileReader fr = null;
	
		try {
			
			fr = new FileReader(this.streamFile);
			br = new BufferedReader(fr);

			String sCurrentLine;
			
			while ((sCurrentLine = br.readLine()) != null) {
				
				
				if(sCurrentLine.contains("<http://www.w3.org/2002/07/owl#Ontology>") ||
						sCurrentLine.contains("<http://www.w3.org/2002/07/owl#imports>"))
						continue;
				
				
					
					String[] spo = sCurrentLine.split(" ");
					String s = spo[0].replace("<", "").replace(">", "").replaceAll("\"", "");
					String p = spo[1].replace("<", "").replace(">", "").replaceAll("\"", "");
					String o = spo[2].replace("<", "").replace(">", "").replaceAll("\"", "");
					
					
					
					countRate ++;
					logger.info("countRate = " + countRate);
					if(countRate == this.rate){
						logger.info("Add lastOb");
						SensorObservation so = new SensorObservation();
						so.setObId("lastOb");
						so.setObTimeStamp(new Date());
						so.setSysTimestamp(new Date());
						CityBench.obMap.put("lastOb", so);
					}else if(countRate > this.rate)
						break;
					
					Triple triple = new Triple(s,p,o);
					TimeStampedTriple q = new TimeStampedTriple(triple, System.currentTimeMillis());
					this.put(q);
					
					
			}

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				if (br != null)
					br.close();

				if (fr != null)
					fr.close();
				
				logger.info("Stream Terminated: " + this.getId());
				this.stop();

			} catch (IOException ex) {

				ex.printStackTrace();

			}
			
				

		}
		
		
	}

	@Override
	protected List<Statement> getStatements(SensorObservation so) throws NumberFormatException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected SensorObservation createObservation(Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	

	

}
