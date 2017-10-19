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

public class TSReasonerOboStream extends TSReasonerSensorStream implements Runnable {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
//	CsvReader streamData;
//	EventDeclaration ed;
//	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
//	private Date startDate = null;
//	private Date endDate = null;
	List<String> streamData;
//	List<String> stream62955;
	int rate;

	public static void main(String[] args) {
		try {
//			List<String> payloads = new ArrayList<String>();
//			payloads.add(RDFFileManager.defaultPrefix + "Property-1|" + RDFFileManager.defaultPrefix + "FoI-1|"
//					+ RDFFileManager.ctPrefix + "ParkingVacancy");
//			EventDeclaration ed = new EventDeclaration("testEd", "testsrc", "air_pollution", null, payloads, 5.0);
			
			TSReasonerOboStream aps = new TSReasonerOboStream("testuri", "streams/obo_instances.stream", 800);
			Thread th = new Thread(aps);
			th.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public TSReasonerOboStream(String uri, String txtFile, int rate) throws IOException {
		super(uri);
		streamData = getStreamData(txtFile);
//		stream62955 = getStreamData("streams/fma_classes_fma62955.stream");
		this.rate = rate;
		this.sleep = 1000/(rate+100);
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

	@Override
	public void run() {
		logger.info("Starting sensor stream: " + this.getId());
		
		try {
			
			SensorObservation so = new SensorObservation();
			so.setObId("ObObo");
			so.setObTimeStamp(new Date());
			so.setSysTimestamp(new Date());
			
			Random r = new Random();
			int strsize = this.streamData.size();
			int size = strsize*2; 
			
			//242514 ~
			while (true) {
				int count =0;
				long t = System.currentTimeMillis();
				
				while(count < this.rate && System.currentTimeMillis() - t < 1000){
					String s = UUID.randomUUID().toString();
					String p = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
					int index = r.nextInt(size);
					String o = "";
					
					if(index < strsize){
//						logger.info("yesssssssssss......" + count);
						 o = this.streamData.get(index);
					}else{
						o = "obo_PO_00" + r.nextInt(1000000);
					}
					
					CityBench.obMap.put(s, so);
			
					count ++;
					Triple triple = new Triple(s,p,o);
					TimeStampedTriple q = new TimeStampedTriple(triple, System.currentTimeMillis());
					this.put(q);
					
					try {
							Thread.sleep(sleep);
					} catch (Exception e) {

						e.printStackTrace();
						this.stop();
					}

				}
				
//				logger.info("Streamed..." + count + " from " + t + " to " + System.currentTimeMillis());
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Unexpected thread termination");

		} finally {
			logger.info("Stream Terminated: " + this.getId());
			this.stop();
		}
		
//		logger.info("COUNT = " + count);
		

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
