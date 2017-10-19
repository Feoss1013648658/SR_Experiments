package org.insight_centre.citybench.reasoning;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.insight_centre.aceis.observations.SensorObservation;
import org.insight_centre.citybench.main.CityBench;
import org.insight_centre.citybench.main.SRJCityBench;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.csparql.common.RDFTable;
import eu.larkc.csparql.common.RDFTuple;
import sr.core.atom_based_reasoner.AtomOutputStream;
import sr.core.triple_based_reasoner.TimeStampedTriple;

/**
 * 
 * @author thule.pham
 *
 */
public class TripleOutputStream extends AtomOutputStream{
	
	final Logger logger = LoggerFactory.getLogger(TripleOutputStream.class);
	public static Set<String> capturedObIds = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
	public static Set<TimeStampedTriple> capturedResults = Collections.newSetFromMap(new ConcurrentHashMap<TimeStampedTriple, Boolean>());
	
	public TripleOutputStream(){
		super();
	}
	
	public TripleOutputStream(String id){
		super(id);
	}
	
	public TripleOutputStream(String id, String fileName){
		super(id, fileName);
	}
	
	@Override
	public void update(Observable o, Object arg) {
		
		if(arg instanceof long[]){
			
			long latency = ((long[]) arg)[0];
			long cnt = ((long[])arg)[1];
			logger.info("cnt = " + cnt + " --- latency " + latency);
			SRJCityBench.pm.addResults(this.getId(),cnt, latency);
			
		}else{
			
			
				int cnt = 0;
				Map<String, Long> latencies = new HashMap<String, Long>();
				for(Object ob : (Set)arg){
					logger.info("Answer set has " + ((Set)ob).size() + " triples");
					
					for(Object temp : (Set)ob){
						TimeStampedTriple result  = ((TimeStampedTriple)temp);
//						logger.info(this.getId() + " Results: " + result.toString());
						if (capturedResults.contains(result)) {
							continue;
						}
						capturedResults.add(result);
						
						cnt += 1;
						String obid = result.getTriple().getSubject();
						
						if (obid == null)
							logger.error("NULL ob Id detected.");
						
						if (!capturedObIds.contains(obid)) {
							capturedObIds.add(obid);
							try {
								SensorObservation so = CityBench.obMap.get(obid);
								if (so == null)
									logger.error("Cannot find observation for: " + obid);
								else{
									
									long creationTime = so.getSysTimestamp().getTime();
									latencies.put(obid, (System.currentTimeMillis() - creationTime));
								}
								
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
						
					}
				}
				
				if (cnt > 0){
					logger.info("cnt = " + cnt + " --- latencies size = " + latencies.keySet().size() + " --- latencies = " + latencies.toString());
					SRJCityBench.pm.addResults(this.getId(), latencies, cnt);
				}
			}
			
			
		
		
		
	}
	
	
//	/**
//	 * 
//	 * @param filename
//	 * @return
//	 */
//	@Override
//	protected FileWriter writeCsvFile(String fileName){
//		if (fileName == null) return null;
//		FileWriter fileWriter = null;
//		try {
//			fileWriter  = new FileWriter(fileName, true);
//			//write the CSV file header
//			fileWriter.append("WindowSize,Time(Tdfin),Time(Tr),Time(Tdfout),Ttt,Memory\n");
//		
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		return fileWriter;
//	}
}
