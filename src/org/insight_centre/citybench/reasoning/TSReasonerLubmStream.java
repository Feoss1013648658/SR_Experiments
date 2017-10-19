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

public class TSReasonerLubmStream extends TSReasonerSensorStream implements Runnable {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	int rate;
	
	int countRate;

	public static void main(String[] args) {
		try {
			
			TSReasonerLubmStream aps = new TSReasonerLubmStream("testuri", "streams/Universities.stream", 10000000);
			Thread th = new Thread(aps);
			th.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public TSReasonerLubmStream(String uri, String txtFile, int rate) throws IOException {
		super(uri);
		this.rate = rate;
	}


	private List<String> getStreamData(String txtFile) {
		List<String> results = new ArrayList<String>();
		BufferedReader br = null;
		FileReader fr = null;

		try {

			//br = new BufferedReader(new FileReader(FILENAME));
			fr = new FileReader(txtFile);
			br = new BufferedReader(fr);

			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
				
					results.add(sCurrentLine);
				
				
			}

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				if (br != null)
					br.close();

				if (fr != null)
					fr.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}

		}
		return results;

	}
	
	public void streamFile(String streamfile){
		BufferedReader br = null;
		FileReader fr = null;
		int count=countRate;
		int dem =0;
		try {
			
			fr = new FileReader(streamfile);
			br = new BufferedReader(fr);

			String sCurrentLine;
			
			
			long t = System.currentTimeMillis();
			while ((sCurrentLine = br.readLine()) != null && !stop) {
				
//				if (System.currentTimeMillis()-t >1000) break;
				
				
				if(sCurrentLine.contains("<http://www.w3.org/2002/07/owl#Ontology>") ||
						sCurrentLine.contains("<http://www.w3.org/2002/07/owl#imports>"))
						continue;
				
				
					
					
					String[] spo = sCurrentLine.split(" ");
					String s = spo[0].replace("<", "").replace(">", "").replaceAll("\"", "");
					String p = spo[1].replace("<", "").replace(">", "").replaceAll("\"", "");;
					String o = spo[2].replace("<", "").replace(">", "").replaceAll("\"", "");;
					
					
					
					count ++;
					Triple triple = new Triple(s,p,o);
					long timestamp = System.currentTimeMillis();
					TimeStampedTriple q = new TimeStampedTriple(triple, timestamp);
					this.put(q);
					dem++;
					
					
					if(CityBench.obMap.keySet().contains(s)){
//						logger.info("Old timestamp " + s  + CityBench.obMap.get(s).getSysTimestamp().getTime());
						CityBench.obMap.get(s).setSysTimestamp(new Date(timestamp));
						CityBench.obMap.get(s).setObTimeStamp(new Date(timestamp));
//						logger.info("New timestamp " + s  + CityBench.obMap.get(s).getSysTimestamp().getTime());
					}	
					else{
//						logger.info("New subject");
						SensorObservation so = new SensorObservation();
						so.setObId(UUID.randomUUID().toString());
						so.setObTimeStamp(new Date(timestamp));
						so.setSysTimestamp(new Date(timestamp));
						CityBench.obMap.put(s, so);
					}
						
					
					if(count == rate && (System.currentTimeMillis() -t) <1000){
						
						try {
							Thread.sleep(1000-(System.currentTimeMillis() - t) );
							logger.info("File" + streamfile + "...Streamed ...." + count + " = rate in 1 second");
							count = 0;
							t = System.currentTimeMillis();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
					}
					if((System.currentTimeMillis() -t) >= 1000){
						System.out.println("DEM " + dem);
						dem = 0;
						t= System.currentTimeMillis();
					}
				
				
			}
			
		

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				if (br != null)
					br.close();

				if (fr != null)
					fr.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}

		}
	}

	@Override
	public void run() {
		logger.info("Starting sensor stream: " + this.getId());
		countRate = 0;
		try {
		
			while (!stop) {
				
				for(int i = 1; i <= 8; i ++){
					String streamFile = String.format("streams/Universities-%d.stream",i);
					streamFile(streamFile);
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Unexpected thread termination");

		} finally {
			logger.info("Stream Terminated: " + this.getId());
			this.stop();
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
